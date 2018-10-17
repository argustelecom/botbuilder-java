package com.microsoft.bot.builder;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.authentication.*;
import com.microsoft.bot.connector.implementation.ConnectorClientImpl;
import com.microsoft.bot.schema.TokenExchangeState;
import com.microsoft.bot.schema.models.*;
import com.microsoft.bot.schema.TokenStatus;
import com.microsoft.rest.retry.RetryStrategy;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


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
	//private HttpClient _httpClient;
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

	public BotFrameworkAdapter(CredentialProvider credentialProvider, ChannelProvider channelProvider, RetryStrategy  connectorClientRetryPolicy, HttpClient customHttpClient)
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
			HttpClient customHttpClient,
			Middleware middleware)
	{
        if (credentialProvider == null)
            throw new NullPointerException("credentialProvider");
		_credentialProvider =  credentialProvider;
		_channelProvider  = channelProvider;
		_httpClient = (customHttpClient != null) ? customHttpClient : DefaultHttpClient;
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
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 @exception ArgumentNullException
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async void ContinueConversationAsync(string botAppId, ConversationReference reference, BotCallbackHandler callback)
	@Override
	public void ContinueConversationAsync(
			String botAppId,
			ConversationReference reference,
			Consumer<TurnContext> callback) throws Exception {
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

		try (TurnContextImpl context = new TurnContextImpl(this, new ConversationReferenceHelper(reference).GetPostToBotMessage()))
		{
			// Hand craft Claims Identity.
			HashMap<String, String> claims = new HashMap<String, String>();
			claims.put(AuthenticationConstants.AudienceClaim, botAppId);
			claims.put(AuthenticationConstants.AppIdClaim, botAppId);
			ClaimsIdentityImpl claimsIdentity = new ClaimsIdentityImpl("ExternalBearer", claims);

			context.getTurnState().Add("BotIdentity", claimsIdentity);

			ConnectorClient connectorClient = this.CreateConnectorClientAsync(reference.serviceUrl(), claimsIdentity).join();
			context.getTurnState().Add("ConnectorClient", connectorClient);
			RunPipeline(context, callback);
		}
	}

	/** 
	 Adds middleware to the adapter's pipeline.
	 
	 @param middleware The middleware to add.
	 @return The updated adapter object.
	 Middleware is added to the adapter at initialization time.
	 For each turn, the adapter calls middleware in the order in which you added it.
	 
	*/
//C# TO JAVA CONVERTER WARNING: There is no Java equivalent to C#'s shadowing via the 'new' keyword:
//ORIGINAL LINE: public new BotFrameworkAdapter Use(Middleware middleware)
	public final BotFrameworkAdapter Use(Middleware middleware)
	{
		getMiddlewareSet().Use(middleware);
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
	 @exception ArgumentNullException <paramref name="activity"/> is <c>null</c>.
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async CompletableFuture<InvokeResponse> ProcessActivityAsync(string authHeader, Activity activity, BotCallbackHandler callback)
	public final CompletableFuture<InvokeResponse> ProcessActivityAsync(String authHeader, Activity activity, BotCallbackHandler callback)
	{
		BotAssert.ActivityNotNull(activity);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var claimsIdentity = JwtTokenValidation.authenticateRequest(activity, authHeader, _credentialProvider, _channelProvider, _httpClient);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return ProcessActivityAsync(claimsIdentity, activity, callback);
	}

	/** 
	 Creates a turn context and runs the middleware pipeline for an incoming activity.
	 
	 @param identity A <see cref="ClaimsIdentity"/> for the request.
	 @param activity The incoming activity.
	 @param callback The code to run at the end of the adapter's middleware pipeline.

	 @return A task that represents the work queued to execute.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async CompletableFuture<InvokeResponse> ProcessActivityAsync(ClaimsIdentity identity, Activity activity, BotCallbackHandler callback)
	public final InvokeResponse ProcessActivity(ClaimsIdentity identity, Activity activity, BotCallbackHandler callback)
	{
		BotAssert.ActivityNotNull(activity);

		try (TurnContext context = new TurnContext(this, activity))
		{
			context.getTurnState().put(BotIdentityKey, identity);

			ConnectorClient connectorClient = CreateConnectorClientAsync(activity.serviceUrl(), identity);
			context.getTurnState().put("ConnectorClient", connectorClient);

			RunPipeline(context, callback);

			// Handle Invoke scenarios, which deviate from the request/response model in that
			// the Bot will return a specific body and return code.
			if (activity.type() == ActivityTypes.INVOKE)
			{
				Activity invokeResponse = context.getTurnState().<Activity>Get(InvokeReponseKey);
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
		}
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<ResourceResponse[]> SendActivitiesAsync(TurnContext turnContext, Activity[] activities)
	@Override
	public ResourceResponse[] SendActivitiesAsync(TurnContext turnContext, Activity[] activities) throws InterruptedException {
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
			throw new IllegalArgumentException("Expecting one or more activities, but the array was empty.", "activities");
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
				Thread.sleep(delayMs);

				// No need to create a response. One will be created below.
			}
			else if (activity.type().toString().equals("invokeResponse")) // Aligning name with Node
			{
				turnContext.getTurnState().put(InvokeReponseKey, activity);

				// No need to create a response. One will be created below.
			}
			else if (activity.type() == ActivityTypes.TRACE && !activity.channelId().equals("emulator"))
			{
				// if it is a Trace activity we only send to the channel if it's the emulator.
			}
			else if (!StringUtils.isBlank(activity.replyToId()))
			{
				ConnectorClient connectorClient = turnContext.getTurnState().<ConnectorClient>Get();

				connectorClient.conversations().replyToActivityAsync(activity.conversation().id(), activity.id(), activity).toBlocking().subscribe(s -> finalResponse.set(s));
			}
			else
			{
				ConnectorClient connectorClient = turnContext.getTurnState().<ConnectorClient>Get();

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
	public ResourceResponse UpdateActivity(TurnContext turnContext, Activity activity)
	{
		ConnectorClient connectorClient = turnContext.getTurnState().<ConnectorClient>Get();
		return connectorClient.conversations().updateActivity(activity.conversation().id(), activity.id(), activity);
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
	public void DeleteActivity(TurnContext turnContext, ConversationReference reference)
	{
		ConnectorClient connectorClient = turnContext.getTurnState().<ConnectorClient>Get();

		connectorClient.conversations().deleteActivity(reference.conversation().id(), reference.activityId());
	}

	/** 
	 Removes a member from the current conversation.
	 
	 @param turnContext The context object for the turn.
	 @param memberId The ID of the member to remove from the conversation.

	 @return A task that represents the work queued to execute.
	*/
	public final void DeleteConversationMember(TurnContext turnContext, String memberId)
	{
		if (turnContext.getActivity().conversation() == null)
		{
			throw new NullPointerException("BotFrameworkAdapter.deleteConversationMember(): missing conversation");
		}

		if (StringUtils.isBlank(turnContext.getActivity().conversation().id()))
		{
			throw new NullPointerException("BotFrameworkAdapter.deleteConversationMember(): missing conversation.id");
		}

		ConnectorClient connectorClient = turnContext.getTurnState().<ConnectorClient>Get();

		String conversationId = turnContext.getActivity().conversation().id();

		connectorClient.conversations().deleteConversationMember(conversationId, memberId);
	}

	/** 
	 Lists the members of a given activity.
	 
	 @param turnContext The context object for the turn.
	 @param activityId (Optional) Activity ID to enumerate. If not specified the current activities ID will be used.
	 @return List of Members of the activity.
	*/
	public final List<ChannelAccount> GetActivityMembers(TurnContext turnContext, String activityId)
	{
		// If no activity was passed in, use the current activity.
		if (activityId == null)
		{
			activityId = turnContext.getActivity().id();
		}

		if (turnContext.getActivity().conversation() == null)
		{
			throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation");
		}

		if (StringUtils.isBlank(turnContext.getActivity().conversation().id()))
		{
			throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation.id");
		}

		ConnectorClient connectorClient = turnContext.getTurnState().<ConnectorClient>Get();
		String conversationId = turnContext.getActivity().conversation().id();

		List<ChannelAccount> accounts = connectorClient.conversations().getActivityMembers(conversationId, activityId);

		return accounts;
	}

	/** 
	 Lists the members of the current conversation.
	 
	 @param turnContext The context object for the turn.
	 @return List of Members of the current conversation.
	*/
	public final CompletableFuture<List<ChannelAccount>> GetConversationMembersAsync(TurnContext turnContext)
	{
		if (turnContext.getActivity().conversation() == null)
		{
			throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation");
		}

		if (StringUtils.isBlank(turnContext.getActivity().conversation().id()))
		{
			throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation.id");
		}

		ConnectorClient connectorClient = turnContext.getTurnState().<ConnectorClient>Get();
		String conversationId = turnContext.getActivity().conversation().id();

		List<ChannelAccount> accounts = connectorClient.conversations().getConversationMembers(conversationId);
		return accounts;
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
	public final ConversationsResult GetConversations(String serviceUrl, MicrosoftAppCredentials credentials, String continuationToken)
	{
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
	public final ConversationsResult GetConversations(TurnContext turnContext, String continuationToken)
	{
		ConnectorClient connectorClient = turnContext.getTurnState().<ConnectorClient>Get();
		ConversationsResult results = connectorClient.conversations().getConversations(continuationToken);
		return results;
	}

	/** Attempts to retrieve the token for a user that's in a login flow.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.
	 @param magicCode (Optional) Optional user entered code to validate.
	 @return Token Response.
	*/
	public final CompletableFuture<TokenResponse> GetUserTokenAsync(TurnContext turnContext, String connectionName, String magicCode)
	{
		BotAssert.ContextNotNull(turnContext);
		if (turnContext.getActivity().from() == null || StringUtils.isBlank(turnContext.getActivity().from().id()))
		{
			throw new NullPointerException("BotFrameworkAdapter.GetuserToken(): missing from or from.id");
		}

		if (StringUtils.isBlank(connectionName))
		{
			throw new NullPointerException("connectionName");
		}

		OAuthClient client = CreateOAuthApiClientAsync(turnContext);
		return client.GetUserTokenAsync(turnContext.getActivity().from().id(), connectionName, magicCode, null);
	}

	/** 
	 Get the raw signin link to be sent to the user for signin for a connection name.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.

	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the raw signin link.
	*/
	public final CompletableFuture<String> GetOauthSignInLinkAsync(TurnContext turnContext, String connectionName) throws URISyntaxException, JsonProcessingException {
		BotAssert.ContextNotNull(turnContext);
		if (StringUtils.isBlank(connectionName))
		{
			throw new NullPointerException("connectionName");
		}

		Activity activity = turnContext.getActivity();
		OAuthClient client = CreateOAuthApiClientAsync(turnContext);
		return client.GetSignInLinkAsync(activity, connectionName);
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
		return GetOauthSignInLinkAsync(turnContext, connectionName, userId, null, null);
	}


	/** 
	 Signs the user out with the token server.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.
	 @param userId User id of user to sign out.

	 @return A task that represents the work queued to execute.
	*/
	public final void SignOutUserAsync(TurnContext turnContext, String connectionName, String userId)
	{
		return SignOutUserAsync(turnContext, connectionName, userId, null);
	}

    /**
     Signs the user out with the token server.

     @param turnContext Context for the current turn of conversation with the user.
     @param connectionName Name of the auth connection to use.

     @return A task that represents the work queued to execute.
     */
    public final void SignOutUserAsync(TurnContext turnContext, String connectionName)
	{
		return SignOutUserAsync(turnContext, connectionName, null, null);
	}


    /**
     Signs the user out with the token server.

     @param turnContext Context for the current turn of conversation with the user.

     @return A task that represents the work queued to execute.
     */
	public final void SignOutUserAsync(TurnContext turnContext)
	{
		return SignOutUserAsync(turnContext, null, null, null);
	}

	public final CompletableFuture<Boolean> SignOutUserAsync(TurnContext turnContext, String connectionName, String userId) throws IOException, URISyntaxException {
		BotAssert.ContextNotNull(turnContext);

		if (StringUtils.isBlank(userId))
		{
			userId = turnContext.getActivity() == null ? null : (turnContext.getActivity().from() == null ? null : turnContext.getActivity().from().id());
		}

		OAuthClient client = CreateOAuthApiClientAsync(turnContext);
		return client.SignOutUserAsync(userId, connectionName);
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
		BotAssert.ContextNotNull(context);

		if (StringUtils.isBlank(userId))
		{
			throw new NullPointerException("userId");
		}

		OAuthClient client = CreateOAuthApiClientAsync(context);
		return client.GetTokenStatusAsync(userId, includeFilter);
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
	public final CompletableFuture<HashMap<String, TokenResponse>> GetAadTokensAsync(TurnContext context, String connectionName, String[] resourceUrls, String userId) throws URISyntaxException, InterruptedException, ExecutionException, IOException {
		BotAssert.ContextNotNull(context);

		if (StringUtils.isBlank(connectionName))
		{
			throw new NullPointerException("connectionName");
		}

		if (resourceUrls == null)
		{
			throw new NullPointerException("userId");
		}

		if (StringUtils.isBlank(userId))
		{
			userId = context.getActivity() == null ? null : (context.getActivity().From == null ? null : context.getActivity().From.Id);
		}

		OAuthClient client = this.CreateOAuthApiClientAsync(context);
		return client.GetAadTokensAsync(userId, connectionName, resourceUrls);
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
	public void CreateConversationAsync(String channelId, String serviceUrl, MicrosoftAppCredentials credentials, ConversationParameters conversationParameters, BotCallbackHandler callback)
	{
		ConnectorClient connectorClient = CreateConnectorClient(serviceUrl, credentials);

		ConversationResourceResponse result = connectorClient.conversations().createConversation(conversationParameters);

		// Create a conversation update activity to represent the result.
		Activity eventActivity = new Activity().withType(ActivityTypes.EVENT)
				.withName("CreateConversation")
				.withChannelId(channelId)
				.withServiceUrl((serviceUrl))
				.withId((result.activityId() != null) ? result.activityId() : UUID.randomUUID().toString())
				.withConversation(new ConversationAccount().withId(result.id()))
				.withRecipient(conversationParameters.bot());

		try (TurnContext context = new TurnContext(this, (Activity)eventActivity))
		{
			ClaimsIdentity claimsIdentity = new ClaimsIdentityImpl();
			claimsIdentity.claims().put(AuthenticationConstants.AudienceClaim, credentials.microsoftAppId());
			claimsIdentity.claims().put(AuthenticationConstants.AppIdClaim, credentials.microsoftAppId());
			claimsIdentity.claims().put(AuthenticationConstants.ServiceUrlClaim, serviceUrl);

			context.getTurnState().put(BotIdentityKey, claimsIdentity);
			context.getTurnState().put("ConnectorClient", connectorClient);

			RunPipeline(context, callback);
		}
	}

	/** 
	 Creates an OAuth client for the bot.
	 
	 @param turnContext The context object for the current turn.
	 @return An OAuth client for the bot.
	*/
	protected final OAuthClient CreateOAuthApiClientAsync(TurnContext turnContext) throws ExecutionException, InterruptedException, IOException, URISyntaxException {
		ConnectorClient tempVar = turnContext.getTurnState().<ConnectorClient>Get();
		ConnectorClient client = tempVar instanceof ConnectorClient ? (ConnectorClient)tempVar : null;
		if (client == null)
		{
			throw new NullPointerException("CreateOAuthApiClient: OAuth requires a valid ConnectorClient instance");
		}

		if (!OAuthClient.getEmulateOAuthCards() && turnContext.getActivity().channelId().equalsIgnoreCase("emulator")
				&& (_credentialProvider.isAuthenticationDisabledAsync().get() == false))
		{
			OAuthClient.setEmulateOAuthCards(true);
		}

		if (OAuthClient.getEmulateOAuthCards())
		{
			OAuthClient oauthClient = new OAuthClient(client, turnContext.getActivity().serviceUrl());

			oauthClient.SendEmulateOAuthCardsAsync(OAuthClient.getEmulateOAuthCards()).get();
			return oauthClient;
		}

		return new OAuthClient(client, OAuthClient.getOAuthEndpoint());
	}

	/** 
	 Creates the connector client asynchronous.
	 
	 @param serviceUrl The service URL.
	 @param claimsIdentity The claims identity.
	 @return ConnectorClient instance.
	 @exception NotSupportedException ClaimsIdemtity cannot be null. Pass Anonymous ClaimsIdentity if authentication is turned off.
	*/
	private ConnectorClient CreateConnectorClientAsync(String serviceUrl, ClaimsIdentity claimsIdentity)
	{
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
			MicrosoftAppCredentials appCredentials = GetAppCredentialsAsync(botId);
			return CreateConnectorClient(serviceUrl, appCredentials);
		}
		else
		{
			return CreateConnectorClient(serviceUrl);
		}
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
	private MicrosoftAppCredentials GetAppCredentialsAsync(String appId) throws ExecutionException, InterruptedException {
		if (appId == null)
		{
			return MicrosoftAppCredentials.Empty;
		}

		if (_appCredentialMap.containsKey(appId))
		    return _appCredentialMap.get(appId);

		String appPassword =  _credentialProvider.getAppPasswordAsync(appId).get();
        MicrosoftAppCredentials appCredentials = new MicrosoftAppCredentials(appId, appPassword);
        if (_channelProvider  != null && _channelProvider.IsGovernment())
            appCredentials =  new MicrosoftGovernmentAppCredentials(appId, appPassword);
		_appCredentialMap.put(appId, appCredentials);
		return appCredentials;
	}
}