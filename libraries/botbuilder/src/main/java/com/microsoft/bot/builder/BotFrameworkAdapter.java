// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.authentication.*;
import com.microsoft.bot.connector.implementation.ConnectorClientImpl;
import com.microsoft.bot.schema.ActivityImpl;
import com.microsoft.bot.schema.models.*;
import com.microsoft.bot.schema.TokenStatus;
import com.microsoft.rest.retry.RetryStrategy;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;



/** 
 A bot adapter that can connect a bot to a service endpoint.
 
 The bot adapter encapsulates authentication processes and sends
 activities to and receives activities from the Bot Connector Service. When your
 bot receives an activity, the adapter creates a context object, passes it to your
 bot's application logic, and sends responses back to the user's channel.
 <p>Use <see cref="Use(Middleware)"/> to add <see cref="Middleware"/> objects
 to your adapter’s middleware collection. The adapter processes and directs
 incoming activities in through the bot middleware pipeline to your bot’s logic
 and then back out again. As each activity flows in and out of the bot, each piece
 of middleware can inspect or act upon the activity, both before and after the bot
 logic runs.</p>
 
 {@link ITurnContext}
 {@link IActivity}
 {@link Bot}
 {@link Middleware}
*/
public class BotFrameworkAdapter extends BotAdapter
{
	private static final String InvokeReponseKey = "BotFrameworkAdapter.InvokeResponse";
	private static final String BotIdentityKey = "BotIdentity";


	private CredentialProvider _credentialProvider;
	private ChannelProvider  _channelProvider;
	private OkHttpClient _httpClient;
	private final RetryStrategy _connectorClientRetryStrategy;
	private java.util.concurrent.ConcurrentHashMap<String, MicrosoftAppCredentials> _appCredentialMap = new ConcurrentHashMap<String, MicrosoftAppCredentials>();

	// There is a significant boost in throughput if we reuse a connectorClient
	// _connectorClients is a cache using [serviceUrl + appId].
	private java.util.concurrent.ConcurrentHashMap<String, ConnectorClient> _connectorClients = new ConcurrentHashMap<String, ConnectorClient>();

	/** 
	 Initializes a new instance of the <see cref="BotFrameworkAdapter"/> class,
	 using a credential provider.
	 
	 @param credentialProvider The credential provider.
	 @param ChannelProvider  The channel provider.
	 @param connectorClientRetryStrategy  Retry policy for retrying HTTP operations.
	 @param customHttpClient The HTTP client.
	 @param middleware The middleware to initially add to the adapter.
	 @exception ArgumentNullException
	 <paramref name="credentialProvider"/> is <c>null</c>.
	 Use a <see cref="MiddlewareSet"/> object to add multiple middleware
	 components in the conustructor. Use the <see cref="Use(Middleware)"/> method to
	 add additional middleware to the adapter after construction.
	 
	*/

	public BotFrameworkAdapter(CredentialProvider credentialProvider, ChannelProvider channelProvider, RetryStrategy  connectorClientRetryPolicy, OkHttpClient customHttpClient)
	{
		this(credentialProvider, channelProvider, connectorClientRetryPolicy, customHttpClient, null);
	}

	public BotFrameworkAdapter(CredentialProvider credentialProvider, ChannelProvider channelProvider, RetryStrategy  connectorClientRetryPolicy)
	{
		this(credentialProvider, channelProvider, connectorClientRetryPolicy, null, null);
	}

	public BotFrameworkAdapter(CredentialProvider credentialProvider, ChannelProvider channelProvider)
	{
		this(credentialProvider, channelProvider, null, null, null);
	}

	public BotFrameworkAdapter(CredentialProvider credentialProvider)
	{
		this(credentialProvider, null, null, null, null);
	}

	public BotFrameworkAdapter(
			CredentialProvider credentialProvider,
			ChannelProvider channelProvider,
			RetryStrategy connectorClientRetryPolicy,
			OkHttpClient customHttpClient,
			Middleware middleware)
	{
        if (credentialProvider == null)
            throw new NullPointerException("credentialProvider");
		_credentialProvider =  credentialProvider;
		_channelProvider  = channelProvider;
		_httpClient = (customHttpClient != null) ? customHttpClient : null;
		_connectorClientRetryStrategy  = connectorClientRetryPolicy;

		if (middleware != null)
		{
			Use(middleware);
		}
	}

	/** 
	 Sends a proactive message from the bot to a conversation.
	 
	 @param botAppId The application ID of the bot. This is the appId returned by Portal registration, and is
	 generally found in the "MicrosoftAppId" parameter in appSettings.json.
	 @param reference A reference to the conversation to continue.
	 @param callback The method to call for the resulting bot turn.
	 @return A task that represents the work queued to execute.
	 @exception NullPointerException
	 <paramref name="botAppId"/>, <paramref name="reference"/>, or
	 <paramref name="callback"/> is <c>null</c>.
	 Call this method to proactively send a message to a conversation.
	 Most _channels require a user to initaiate a conversation with a bot
	 before the bot can send activities to the user.
	 <p>This method registers the following services for the turn.<list type="bullet">
	 <item><see cref="IIdentity"/> (key = "BotIdentity"), a claims identity for the bot.</item>
	 <item><see cref="IConnectorClient"/>, the channel connector client to use this turn.</item>
	 </list></p>
	 <p>
	 This overload differers from the Node implementation by requiring the BotId to be
	 passed in. The .Net code allows multiple bots to be hosted in a single adapter which
	 isn't something supported by Node.
	 </p>
	 
	 {@link ProcessActivityAsync(string, Activity, BotCallbackHandler )}
	 {@link BotAdapter.RunPipelineAsync(ITurnContext, BotCallbackHandler )}
	*/
	@Override
	public CompletableFuture ContinueConversationAsync(
			String botAppId,
			ConversationReference reference,
			BotCallbackHandler callback) throws Exception {
	    return CompletableFuture.runAsync(() -> {
            if (StringUtils.isBlank(botAppId))
            {
                throw new NullPointerException("botAppId");
            }

            if (reference == null)
            {
                throw new NullPointerException("reference");
            }

            if (callback == null)
            {
                throw new NullPointerException("callback");
            }

            try {
                try (TurnContextImpl context = new TurnContextImpl(this, new ConversationReferenceHelper(reference).GetPostToBotMessage()))
                {
                    // Hand craft Claims Identity.
                    HashMap<String, String> claims = new HashMap<String, String>();
                    claims.put(AuthenticationConstants.AudienceClaim, botAppId);
                    claims.put(AuthenticationConstants.AppIdClaim, botAppId);
                    ClaimsIdentityImpl claimsIdentity = new ClaimsIdentityImpl("ExternalBearer", claims);

                    context.turnState().Add("BotIdentity", claimsIdentity);

                    ConnectorClient connectorClient = this.CreateConnectorClientAsync(reference.serviceUrl(), claimsIdentity).join();
                    context.turnState().Add("ConnectorClient", connectorClient);
                    RunPipelineAsync(context, callback);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
	}

	/** 
	 Adds middleware to the adapter's pipeline.
	 
	 @param middleware The middleware to add.
	 @return The updated adapter object.
	 Middleware is added to the adapter at initialization time.
	 For each turn, the adapter calls middleware in the order in which you added it.
	 
	*/
	public final BotFrameworkAdapter Use(Middleware middleware)
	{
		_middlewareSet.Use(middleware);
		return this;
	}

	/** 
	 Creates a turn context and runs the middleware pipeline for an incoming activity.
	 
	 @param authHeader The HTTP authentication header of the request.
	 @param activity The incoming activity.
	 @param callback The code to run at the end of the adapter's middleware pipeline.

	 @return A task that represents the work queued to execute. If the activity type
	 was 'Invoke' and the corresponding key (channelId + activityId) was found
	 then an InvokeResponse is returned, otherwise null is returned.
	 @exception NullPointerException <paramref name="activity"/> is <c>null</c>.
	 @exception UnauthorizedAccessException authentication failed.
	 Call this method to reactively send a message to a conversation.
	 If the task completes successfully, then if the activity's <see cref="Activity.Type"/>
	 is <see cref="ActivityTypes.Invoke"/> and the corresponding key
	 (<see cref="Activity.ChannelId"/> + <see cref="Activity.Id"/>) is found
	 then an <see cref="InvokeResponse"/> is returned, otherwise null is returned.
	 <p>This method registers the following services for the turn.<list type="bullet">
	 <item><see cref="IIdentity"/> (key = "BotIdentity"), a claims identity for the bot.</item>
	 <item><see cref="IConnectorClient"/>, the channel connector client to use this turn.</item>
	 </list></p>
	 
	 {@link ContinueConversationAsync(string, ConversationReference, BotCallbackHandler )}
	 {@link BotAdapter.RunPipelineAsync(ITurnContext, BotCallbackHandler )}
	*/
	public final CompletableFuture<InvokeResponse> ProcessActivityAsync(String authHeader, Activity activity, BotCallbackHandler callback)
	{
	    return CompletableFuture.supplyAsync(() -> {
            BotAssert.ActivityNotNull(activity);

            ClaimsIdentity claimsIdentity = null;
            try {
                claimsIdentity = JwtTokenValidation.authenticateRequest(activity, authHeader, _credentialProvider);
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
            return ProcessActivityAsync(claimsIdentity, (ActivityImpl)activity, callback).join();
        });
	}

	/** 
	 Creates a turn context and runs the middleware pipeline for an incoming activity.
	 
	 @param identity A <see cref="ClaimsIdentity"/> for the request.
	 @param activity The incoming activity.
	 @param callback The code to run at the end of the adapter's middleware pipeline.

	 @return A task that represents the work queued to execute.
	*/
	public final CompletableFuture<InvokeResponse> ProcessActivityAsync(ClaimsIdentity identity, ActivityImpl activity, BotCallbackHandler callback)
	{
	    return CompletableFuture.supplyAsync(() -> {
            BotAssert.ActivityNotNull(activity);

            try (TurnContextImpl context = new TurnContextImpl(this, activity))
            {
                context.turnState().Add(BotIdentityKey, identity);

                ConnectorClient connectorClient = CreateConnectorClientAsync(activity.serviceUrl(), identity).join();
                context.turnState().Add("ConnectorClient", connectorClient);

                RunPipelineAsync(context, callback).join();

                // Handle Invoke scenarios, which deviate from the request/response model in that
                // the Bot will return a specific body and return code.
                if (activity.type() == ActivityTypes.INVOKE.toString())
                {
                    Activity invokeResponse = context.turnState().<Activity>Get(InvokeReponseKey);
                    if (invokeResponse == null)
                    {
                        // ToDo: Trace Here
                        throw new IllegalStateException("Bot failed to return a valid 'invokeResponse' activity.");
                    }
                    else
                    {
                        return (InvokeResponse)invokeResponse.value();
                    }
                }

                // For all non-invoke scenarios, the HTTP layers above don't have to mess
                // withthe Body and return codes.
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }

        });
	}

	/** 
	 Sends activities to the conversation.
	 
	 @param turnContext The context object for the turn.
	 @param activities The activities to send.
	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 an array of <see cref="ResourceResponse"/> objects containing the IDs that
	 the receiving channel assigned to the activities.
	 {@link ITurnContext.OnSendActivities(SendActivitiesHandler)}
	*/
	@Override
	public CompletableFuture<ResourceResponse[]> SendActivitiesAsync(TurnContext turnContext, Activity[] activities) throws InterruptedException {
	    return CompletableFuture.supplyAsync(() -> {
            if (turnContext == null)
            {
                throw new NullPointerException("turnContext");
            }

            if (activities == null)
            {
                throw new NullPointerException("activities");
            }

            if (activities.length == 0)
            {
                throw new IllegalArgumentException("Expecting one or more activities, but the array was empty.");
            }

            ResourceResponse[] responses = new ResourceResponse[activities.length];

            /*
             * NOTE: we're using for here (vs. foreach) because we want to simultaneously index into the
             * activities array to get the activity to process as well as use that index to assign
             * the response to the responses array and this is the most cost effective way to do that.
             */
            for (int index = 0; index < activities.length; index++)
            {
                Activity activity = activities[index];

                ResourceResponse response = null;
                final AtomicReference<ResourceResponse> finalResponse = new AtomicReference<ResourceResponse>(response);

                if (activity.type().toString().equals("delay"))
                {
                    // The Activity Schema doesn't have a delay type build in, so it's simulated
                    // here in the Bot. This matches the behavior in the Node connector.
                    int delayMs = (int)activity.value();
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        // Swallow the sleep interruption.
                    }

                    // No need to create a response. One will be created below.
                }
                else if (activity.type().toString().equals("invokeResponse")) // Aligning name with Node
                {
                    turnContext.turnState().Add(InvokeReponseKey, activity);

                    // No need to create a response. One will be created below.
                }
                else if (activity.type() == ActivityTypes.TRACE.toString() && !activity.channelId().equals("emulator"))
                {
                    // if it is a Trace activity we only send to the channel if it's the emulator.
                }
                else if (!StringUtils.isBlank(activity.replyToId()))
                {
                    ConnectorClientImpl connectorClient = (ConnectorClientImpl) turnContext.turnState().Get(ConnectorClientImpl.class.getName());

                    connectorClient.conversations().replyToActivityAsync(activity.conversation().id(), activity.id(), activity).toBlocking().subscribe(s -> finalResponse.set(s));
                }
                else
                {
                    ConnectorClientImpl connectorClient = turnContext.turnState().Get(ConnectorClientImpl.class.getName());

                    connectorClient.conversations().sendToConversationAsync(activity.conversation().id(), activity).toBlocking().subscribe(s -> finalResponse.set(s));
                }

                // If No response is set, then defult to a "simple" response. This can't really be done
                // above, as there are cases where the ReplyTo/SendTo methods will also return null
                // (See below) so the check has to happen here.

                // Note: In addition to the Invoke / Delay / Activity cases, this code also applies
                // with Skype and Teams with regards to typing events.  When sending a typing event in
                // these _channels they do not return a RequestResponse which causes the bot to blow up.
                // https://github.com/Microsoft/botbuilder-dotnet/issues/460
                // bug report : https://github.com/Microsoft/botbuilder-dotnet/issues/465
                if (response == null)
                {
                    response = new ResourceResponse()
                            .withId((activity.id() != null) ? activity.id() : "");
                }

                responses[index] = response;
            }

            return responses;
        });
	}

	/** 
	 Replaces an existing activity in the conversation.
	 
	 @param turnContext The context object for the turn.
	 @param activity New replacement activity.
	 @return A task that represents the work queued to execute.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 <p>Before calling this, set the ID of the replacement activity to the ID
	 of the activity to replace.</p>
	 {@link ITurnContext.OnUpdateActivity(UpdateActivityHandler)}
	*/
	public CompletableFuture<ResourceResponse> UpdateActivityAsync(TurnContext turnContext, Activity activity)
	{
	    return CompletableFuture.supplyAsync(() -> {
            ConnectorClient connectorClient = turnContext.turnState().Get(ConnectorClientImpl.class.getName());
            return connectorClient.conversations().updateActivity(activity.conversation().id(), activity.id(), activity);
        });
	}

	/** 
	 Deletes an existing activity in the conversation.
	 
	 @param turnContext The context object for the turn.
	 @param reference Conversation reference for the activity to delete.
	 @return A task that represents the work queued to execute.
	 The <see cref="ConversationReference.ActivityId"/> of the conversation
	 reference identifies the activity to delete.
	 {@link TurnContext.onDeleteActivity(DeleteActivityHandler)}
	*/
	@Override
	public CompletableFuture DeleteActivityAsync(TurnContext turnContext, ConversationReference reference)
	{
	    return CompletableFuture.runAsync(() -> {
            ConnectorClient connectorClient = turnContext.turnState().Get(ConnectorClientImpl.class.getName());

            connectorClient.conversations().deleteActivity(reference.conversation().id(), reference.activityId());
        });
	}

	/** 
	 Removes a member from the current conversation.
	 
	 @param turnContext The context object for the turn.
	 @param memberId The ID of the member to remove from the conversation.

	 @return A task that represents the work queued to execute.
	*/
	public final CompletableFuture DeleteConversationMemberAsync(TurnContext turnContext, String memberId)
	{
	    return CompletableFuture.runAsync(() -> {
            if (turnContext.activity().conversation() == null)
            {
                throw new NullPointerException("BotFrameworkAdapter.deleteConversationMember(): missing conversation");
            }

            if (StringUtils.isBlank(turnContext.activity().conversation().id()))
            {
                throw new NullPointerException("BotFrameworkAdapter.deleteConversationMember(): missing conversation.id");
            }

            ConnectorClient connectorClient = turnContext.turnState().Get(ConnectorClientImpl.class.getName());

            String conversationId = turnContext.activity().conversation().id();

            connectorClient.conversations().deleteConversationMember(conversationId, memberId);

        });
	}

	/** 
	 Lists the members of a given activity.
	 
	 @param turnContext The context object for the turn.
	 @param activityId (Optional) Activity ID to enumerate. If not specified the current activities ID will be used.
	 @return List of Members of the activity.
	*/
	public final CompletableFuture<List<ChannelAccount>> GetActivityMembersAsync(TurnContext turnContext, final String activityId)
	{
	    return CompletableFuture.supplyAsync(() -> {
	        String tempActivityId = activityId;
            // If no activity was passed in, use the current activity.
            if (activityId == null)
            {
                tempActivityId = turnContext.activity().id();
            }

            if (turnContext.activity().conversation() == null)
            {
                throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation");
            }

            if (StringUtils.isBlank(turnContext.activity().conversation().id()))
            {
                throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation.id");
            }

            ConnectorClient connectorClient = turnContext.turnState().Get(ConnectorClientImpl.class.getName());
            String conversationId = turnContext.activity().conversation().id();

            List<ChannelAccount> accounts = connectorClient.conversations().getActivityMembers(conversationId, tempActivityId);

            return accounts;
        });
	}

	/** 
	 Lists the members of the current conversation.
	 
	 @param turnContext The context object for the turn.
	 @return List of Members of the current conversation.
	*/
	public final CompletableFuture<List<ChannelAccount>> GetConversationMembersAsync(TurnContext turnContext)
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (turnContext.activity().conversation() == null)
            {
                throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation");
            }

            if (StringUtils.isBlank(turnContext.activity().conversation().id()))
            {
                throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation.id");
            }

            ConnectorClient connectorClient = turnContext.turnState().Get(ConnectorClientImpl.class.getName());
            String conversationId = turnContext.activity().conversation().id();

            List<ChannelAccount> accounts = connectorClient.conversations().getConversationMembers(conversationId);
            return accounts;
        });
	}

	/** 
	 Lists the Conversations in which this bot has participated for a given channel server. The
	 channel server returns results in pages and each page will include a `continuationToken`
	 that can be used to fetch the next page of results from the server.
	 
	 @param serviceUrl The URL of the channel server to query.  This can be retrieved
	 from `context.activity.serviceUrl`. 
	 @param credentials The credentials needed for the Bot to connect to the services.
	 @param continuationToken

	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the members of the current conversation.
	 This overload may be called from outside the context of a conversation, as only the
	 bot's service URL and credentials are required.
	 
	*/
	public final CompletableFuture<ConversationsResult> GetConversationsAsync(String serviceUrl, MicrosoftAppCredentials credentials, String continuationToken)
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (StringUtils.isBlank(serviceUrl))
            {
                throw new NullPointerException("serviceUrl");
            }

            if (credentials == null)
            {
                throw new NullPointerException("credentials");
            }

            ConnectorClient connectorClient = CreateConnectorClient(serviceUrl, credentials);
            ConversationsResult results = connectorClient.conversations().getConversations(continuationToken);
            return results;
        });
	}

	/** 
	 Lists the Conversations in which this bot has participated for a given channel server. The
	 channel server returns results in pages and each page will include a `continuationToken`
	 that can be used to fetch the next page of results from the server.
	 
	 @param turnContext The context object for the turn.
	 @param continuationToken

	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the members of the current conversation.
	 This overload may be called during standard activity processing, at which point the Bot's
	 service URL and credentials that are part of the current activity processing pipeline
	 will be used.
	 
	*/
	public final CompletableFuture<ConversationsResult> GetConversations(TurnContext turnContext, String continuationToken)
	{
	    return CompletableFuture.supplyAsync(() -> {
            ConnectorClient connectorClient = turnContext.turnState().Get(ConnectorClientImpl.class.getName());
            ConversationsResult results = connectorClient.conversations().getConversations(continuationToken);
            return results;
        });
	}

	/** Attempts to retrieve the token for a user that's in a login flow.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.
	 @param magicCode (Optional) Optional user entered code to validate.
	 @return Token Response.
	*/
	public final CompletableFuture<TokenResponse> GetUserTokenAsync(TurnContext turnContext, String connectionName, String magicCode)
	{
	    return CompletableFuture.supplyAsync(() -> {
            BotAssert.ContextNotNull(turnContext);
            if (turnContext.activity().from() == null || StringUtils.isBlank(turnContext.activity().from().id()))
            {
                throw new NullPointerException("BotFrameworkAdapter.GetuserToken(): missing from or from.id");
            }

            if (StringUtils.isBlank(connectionName))
            {
                throw new NullPointerException("connectionName");
            }

            OAuthClient client = null;
            try {
                client = CreateOAuthApiClientAsync(turnContext).join();
            } catch (ExecutionException|InterruptedException|IOException|URISyntaxException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
            return client.GetUserTokenAsync(turnContext.activity().from().id(), connectionName, magicCode, null).join();
        });
	}

	/** 
	 Get the raw signin link to be sent to the user for signin for a connection name.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.

	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the raw signin link.
	*/
	public final CompletableFuture<String> GetOauthSignInLinkAsync(TurnContext turnContext, String connectionName) throws URISyntaxException, JsonProcessingException {
	    return CompletableFuture.supplyAsync(() ->
        {
            BotAssert.ContextNotNull(turnContext);
            if (StringUtils.isBlank(connectionName))
            {
                throw new NullPointerException("connectionName");
            }

            Activity activity = turnContext.activity();
            OAuthClient client = null;
            try {
                client = CreateOAuthApiClientAsync(turnContext).join();
            } catch (InterruptedException|IOException|URISyntaxException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }

            try {
                return client.GetSignInLinkAsync(activity, connectionName).join();
            } catch (URISyntaxException|JsonProcessingException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
	}

	/** 
	 Get the raw signin link to be sent to the user for signin for a connection name.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.
	 @param userId The user id that will be associated with the token.
	 @param finalRedirect The final URL that the OAuth flow will redirect to.

	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the raw signin link.
	*/

	public final CompletableFuture<String> GetOauthSignInLinkAsync(TurnContext turnContext, String connectionName, String userId, String finalRedirect)
	{
		return GetOauthSignInLinkAsync(turnContext, connectionName, userId, finalRedirect);
	}

	public final CompletableFuture<String> GetOauthSignInLinkAsync(TurnContext turnContext, String connectionName, String userId)
	{
		return GetOauthSignInLinkAsync(turnContext, connectionName, userId, null);
	}



    /**
     Signs the user out with the token server.

     @param turnContext Context for the current turn of conversation with the user.
     @param connectionName Name of the auth connection to use.

     @return A task that represents the work queued to execute.
     */
    public final CompletableFuture SignOutUserAsync(TurnContext turnContext, String connectionName)
	{
		return SignOutUserAsync(turnContext, connectionName, null);
	}


    /**
     Signs the user out with the token server.

     @param turnContext Context for the current turn of conversation with the user.

     @return A task that represents the work queued to execute.
     */
	public final CompletableFuture SignOutUserAsync(TurnContext turnContext)
	{
		return SignOutUserAsync(turnContext, null, null);
	}

    /**
     Signs the user out with the token server.

     @param turnContext Context for the current turn of conversation with the user.
     @param connectionName Name of the auth connection to use.
     @param userId User id of user to sign out.

     @return A task that represents the work queued to execute.
     */
	public final CompletableFuture<Boolean> SignOutUserAsync(TurnContext turnContext, String connectionName, String userId) {
	    final String finalUserId = userId;
	    return CompletableFuture.supplyAsync(() -> {
            BotAssert.ContextNotNull(turnContext);

            String supplyUserId = finalUserId;
            if (StringUtils.isBlank(finalUserId))
            {
                supplyUserId = turnContext.activity() == null ? null : (turnContext.activity().from() == null ? null : turnContext.activity().from().id());
            }

            OAuthClient client = null;
            try {
                client = CreateOAuthApiClientAsync(turnContext).join();
            } catch (ExecutionException|IOException|URISyntaxException|InterruptedException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
            try {
                return client.SignOutUserAsync(supplyUserId, connectionName).join();
            } catch (URISyntaxException|IOException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }

        });
	}

	/** 
	 Retrieves the token status for each configured connection for the given user.
	 
	 @param context Context for the current turn of conversation with the user.
	 @param userId The user Id for which token status is retrieved.
	 @return Array of TokenStatus.
	*/
	public final CompletableFuture<TokenStatus[]> GetTokenStatusAsync(TurnContext context, String userId) throws URISyntaxException {
		return GetTokenStatusAsync(context, userId, null);
	}

    /**
     Retrieves the token status for each configured connection for the given user.

     @param context Context for the current turn of conversation with the user.
     @param userId The user Id for which token status is retrieved.
     @param includeFilter Optional comma seperated list of connection's to include. Blank will return token status for all configured connections.
     @return Array of TokenStatus.
     */
	public final CompletableFuture<TokenStatus[]> GetTokenStatusAsync(TurnContext context, String userId, String includeFilter) throws URISyntaxException {
	    return CompletableFuture.supplyAsync(() -> {
            BotAssert.ContextNotNull(context);

            if (StringUtils.isBlank(userId))
            {
                throw new NullPointerException("userId");
            }

            OAuthClient client = null;
            try {
                client = CreateOAuthApiClientAsync(context).join();
            } catch (ExecutionException|InterruptedException|IOException|URISyntaxException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
            try {
                return client.GetTokenStatusAsync(userId, includeFilter).join();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
	}

	/** 
	 Retrieves Azure Active Directory tokens for particular resources on a configured connection.
	 
	 @param context Context for the current turn of conversation with the user.
	 @param connectionName The name of the Azure Active Direcotry connection configured with this bot.
	 @param resourceUrls The list of resource URLs to retrieve tokens for.
	 @return Dictionary of resourceUrl to the corresponding TokenResponse.
	*/
	public final CompletableFuture<java.util.HashMap<String, TokenResponse>> GetAadTokensAsync(TurnContext context, String connectionName, String[] resourceUrls)
	{
		return GetAadTokensAsync(context, connectionName, resourceUrls, null);
	}

    /**
     Retrieves Azure Active Directory tokens for particular resources on a configured connection.

     @param context Context for the current turn of conversation with the user.
     @param connectionName The name of the Azure Active Direcotry connection configured with this bot.
     @param resourceUrls The list of resource URLs to retrieve tokens for.
     @param userId The user Id for which tokens are retrieved. If passing in null the userId is taken from the Activity in the ITurnContext.
     @return Dictionary of resourceUrl to the corresponding TokenResponse.
     */
	public final CompletableFuture<HashMap<String, TokenResponse>> GetAadTokensAsync(TurnContext context, String connectionName, String[] resourceUrls, final String userId)  {
	    return CompletableFuture.supplyAsync(() -> {
            BotAssert.ContextNotNull(context);

            String tempUserId = userId;

            if (StringUtils.isBlank(connectionName))
            {
                throw new NullPointerException("connectionName");
            }

            if (resourceUrls == null)
            {
                throw new NullPointerException("userId");
            }

            if (StringUtils.isBlank(tempUserId))
            {
                tempUserId = context.activity() == null ? null : (context.activity().from() == null ? null : context.activity().from().id());
            }

            OAuthClient client = null;
            try {
                client = this.CreateOAuthApiClientAsync(context).join();
            } catch (ExecutionException|InterruptedException|IOException|URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                return client.GetAadTokensAsync(tempUserId, connectionName, resourceUrls).join();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
	}

	/** 
	 Creates a conversation on the specified channel.
	 
	 @param channelId The ID for the channel.
	 @param serviceUrl The channel's service URL endpoint.
	 @param credentials The application credentials for the bot.
	 @param conversationParameters The conversation information to use to
	 create the conversation.
	 @param callback The method to call for the resulting bot turn.

	 @return A task that represents the work queued to execute.
	 To start a conversation, your bot must know its account information
	 and the user's account information on that channel.
	 Most _channels only support initiating a direct message (non-group) conversation.
	 <p>The adapter attempts to create a new conversation on the channel, and
	 then sends a <c>conversationUpdate</c> activity through its middleware pipeline
	 to the <paramref name="callback"/> method.</p>
	 <p>If the conversation is established with the
	 specified users, the ID of the activity's <see cref="IActivity.Conversation"/>
	 will contain the ID of the new conversation.</p>
	 
	*/
	public CompletableFuture CreateConversationAsync(String channelId, String serviceUrl, MicrosoftAppCredentials credentials, ConversationParameters conversationParameters, BotCallbackHandler callback)
	{
	    return CompletableFuture.runAsync(() -> {
            ConnectorClient connectorClient = CreateConnectorClient(serviceUrl, credentials);

            ConversationResourceResponse result = connectorClient.conversations().createConversation(conversationParameters);

            // Create a conversation update activity to represent the result.
            ActivityImpl eventActivity = new ActivityImpl().withType(ActivityTypes.EVENT.toString())
                    .withName("CreateConversation")
                    .withChannelId(channelId)
                    .withServiceUrl((serviceUrl))
                    .withId((result.activityId() != null) ? result.activityId() : UUID.randomUUID().toString())
                    .withConversation(new ConversationAccount().withId(result.id()))
                    .withRecipient(conversationParameters.bot());

            try (TurnContextImpl context = new TurnContextImpl(this, eventActivity))
            {
                ClaimsIdentity claimsIdentity = new ClaimsIdentityImpl();
                claimsIdentity.claims().put(AuthenticationConstants.AudienceClaim, credentials.microsoftAppId());
                claimsIdentity.claims().put(AuthenticationConstants.AppIdClaim, credentials.microsoftAppId());
                claimsIdentity.claims().put(AuthenticationConstants.ServiceUrlClaim, serviceUrl);

                context.turnState().Add(BotIdentityKey, claimsIdentity);
                context.turnState().Add("ConnectorClient", connectorClient);

                RunPipelineAsync(context, callback).join();
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
	}

	/** 
	 Creates an OAuth client for the bot.
	 
	 @param turnContext The context object for the current turn.
	 @return An OAuth client for the bot.
	*/
	protected final CompletableFuture<OAuthClient> CreateOAuthApiClientAsync(TurnContext turnContext) throws ExecutionException, InterruptedException, IOException, URISyntaxException {
	    return CompletableFuture.supplyAsync(() -> {
            ConnectorClientImpl tempVar = turnContext.turnState().Get(ConnectorClientImpl.class.getName());
            ConnectorClientImpl client = tempVar instanceof ConnectorClientImpl ? (ConnectorClientImpl)tempVar : null;
            if (client == null)
            {
                throw new NullPointerException("CreateOAuthApiClient: OAuth requires a valid ConnectorClient instance");
            }

            if (!OAuthClient.getEmulateOAuthCards() && turnContext.activity().channelId().equalsIgnoreCase("emulator")
                    && (_credentialProvider.isAuthenticationDisabledAsync().join() == false))
            {
                OAuthClient.setEmulateOAuthCards(true);
            }

            if (OAuthClient.getEmulateOAuthCards())
            {
                OAuthClient oauthClient = null;
                try {
                    oauthClient = new OAuthClient(client, turnContext.activity().serviceUrl());
                } catch (URISyntaxException|MalformedURLException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }

                try {
                    oauthClient.SendEmulateOAuthCardsAsync(OAuthClient.getEmulateOAuthCards()).get();
                } catch (InterruptedException|IOException|ExecutionException|URISyntaxException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
                return oauthClient;
            }

            try {
                return new OAuthClient(client, OAuthClient.getOAuthEndpoint());
            } catch (URISyntaxException|MalformedURLException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
	}

	/** 
	 Creates the connector client asynchronous.
	 
	 @param serviceUrl The service URL.
	 @param claimsIdentity The claims identity.
	 @return ConnectorClient instance.
	 @exception NotSupportedException ClaimsIdemtity cannot be null. Pass Anonymous ClaimsIdentity if authentication is turned off.
	*/
	private CompletableFuture<ConnectorClient> CreateConnectorClientAsync(String serviceUrl, ClaimsIdentity claimsIdentity)
	{
	    return CompletableFuture.supplyAsync(() ->{
            if (claimsIdentity == null)
            {
                throw new UnsupportedOperationException("ClaimsIdemtity cannot be null. Pass Anonymous ClaimsIdentity if authentication is turned off.");
            }

            // For requests from channel App Id is in Audience claim of JWT token. For emulator it is in AppId claim. For
            // unauthenticated requests we have anonymouse identity provided auth is disabled.
            // For Activities coming from Emulator AppId claim contains the Bot's AAD AppId.
            String botAppIdClaim = null;
            Map<String, String> claims = claimsIdentity.claims();
            if (claims != null){
                if (claims.containsKey(AuthenticationConstants.AudienceClaim))
                    botAppIdClaim = claims.get(AuthenticationConstants.AudienceClaim);
                else if (claims.containsKey(AuthenticationConstants.AppIdClaim))
                    botAppIdClaim = claims.get(AuthenticationConstants.AppIdClaim);
            }

            // For anonymous requests (requests with no header) appId is not set in claims.
            if (botAppIdClaim != null)
            {
                String botId = botAppIdClaim;
                MicrosoftAppCredentials appCredentials = null;
                try {
                    appCredentials = GetAppCredentialsAsync(botId).join();
                } catch (ExecutionException|InterruptedException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
                return CreateConnectorClient(serviceUrl, appCredentials);
            }
            else
            {
                return CreateConnectorClient(serviceUrl);
            }

        });
	}

	/** 
	 Creates the connector client.
	 
	 @param serviceUrl The service URL.
	 @return Connector client instance.
	*/
	private ConnectorClient CreateConnectorClient(String serviceUrl)
	{
		return CreateConnectorClient(serviceUrl, null);
	}
    /**
     Creates the connector client.

     @param serviceUrl The service URL.
     @param appCredentials The application credentials for the bot.
     @return Connector client instance.
     */
	private ConnectorClient CreateConnectorClient(String serviceUrl, MicrosoftAppCredentials appCredentials)
	{
		String appId = "";
		if (appCredentials != null)
			if (!StringUtils.isBlank(appCredentials.microsoftAppId()))
				appId = appCredentials.microsoftAppId();

		String clientKey = String.format("%1$s%2$s", serviceUrl, appId);

        ConnectorClientImpl connectorClient;

        if (appCredentials != null)
        {
            connectorClient = new ConnectorClientImpl(serviceUrl, appCredentials);
        }
        else
        {
            MicrosoftAppCredentials emptyCredentials;
            if (_channelProvider != null &&  _channelProvider.IsGovernment())
                emptyCredentials = MicrosoftGovernmentAppCredentials.Empty;
            else
                emptyCredentials = MicrosoftAppCredentials.Empty;

            connectorClient = new ConnectorClientImpl(serviceUrl, emptyCredentials);
        }

        if (_connectorClientRetryStrategy  != null)
        {
            connectorClient.withRestRetryStrategy(this._connectorClientRetryStrategy);
        }

        return _connectorClients.putIfAbsent(clientKey, connectorClient);
	}

	/** 
	 Gets the application credentials. App Credentials are cached so as to ensure we are not refreshing
	 token everytime.
	 
	 @param appId The application identifier (AAD Id for the bot).
	 @return App credentials.
	*/
	private CompletableFuture<MicrosoftAppCredentials> GetAppCredentialsAsync(String appId) throws ExecutionException, InterruptedException {
	    return CompletableFuture.supplyAsync(() -> {
            if (appId == null)
            {
                return MicrosoftAppCredentials.Empty;
            }

            if (_appCredentialMap.containsKey(appId))
                return _appCredentialMap.get(appId);

            String appPassword =  _credentialProvider.getAppPasswordAsync(appId).join();
            MicrosoftAppCredentials appCredentials = new MicrosoftAppCredentials(appId, appPassword);
            if (_channelProvider  != null && _channelProvider.IsGovernment())
                appCredentials =  new MicrosoftGovernmentAppCredentials(appId, appPassword);
            _appCredentialMap.put(appId, appCredentials);
            return appCredentials;
        });
	}
}