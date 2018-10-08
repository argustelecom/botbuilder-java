package Microsoft.Bot.Builder;

import Newtonsoft.Json.*;
import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 A bot adapter that can connect a bot to a service endpoint.
 
 The bot adapter encapsulates authentication processes and sends
 activities to and receives activities from the Bot Connector Service. When your
 bot receives an activity, the adapter creates a context object, passes it to your
 bot's application logic, and sends responses back to the user's channel.
 <p>Use <see cref="Use(IMiddleware)"/> to add <see cref="IMiddleware"/> objects
 to your adapter’s middleware collection. The adapter processes and directs
 incoming activities in through the bot middleware pipeline to your bot’s logic
 and then back out again. As each activity flows in and out of the bot, each piece
 of middleware can inspect or act upon the activity, both before and after the bot
 logic runs.</p>
 
 {@link ITurnContext}
 {@link IActivity}
 {@link IBot}
 {@link IMiddleware}
*/
public class BotFrameworkAdapter extends BotAdapter
{
	private static final String InvokeReponseKey = "BotFrameworkAdapter.InvokeResponse";
	private static final String BotIdentityKey = "BotIdentity";

	private static final HttpClient DefaultHttpClient = new HttpClient();
	private ICredentialProvider _credentialProvider;
	private IChannelProvider _channelProvider;
	private HttpClient _httpClient;
	private RetryPolicy _connectorClientRetryPolicy;
	private java.util.concurrent.ConcurrentHashMap<String, MicrosoftAppCredentials> _appCredentialMap = new java.util.concurrent.ConcurrentHashMap<String, MicrosoftAppCredentials>();

	// There is a significant boost in throughput if we reuse a connectorClient
	// _connectorClients is a cache using [serviceUrl + appId].
	private java.util.concurrent.ConcurrentHashMap<String, ConnectorClient> _connectorClients = new java.util.concurrent.ConcurrentHashMap<String, ConnectorClient>();

	/** 
	 Initializes a new instance of the <see cref="BotFrameworkAdapter"/> class,
	 using a credential provider.
	 
	 @param credentialProvider The credential provider.
	 @param channelProvider The channel provider.
	 @param connectorClientRetryPolicy Retry policy for retrying HTTP operations.
	 @param customHttpClient The HTTP client.
	 @param middleware The middleware to initially add to the adapter.
	 @exception ArgumentNullException
	 <paramref name="credentialProvider"/> is <c>null</c>.
	 Use a <see cref="MiddlewareSet"/> object to add multiple middleware
	 components in the conustructor. Use the <see cref="Use(IMiddleware)"/> method to
	 add additional middleware to the adapter after construction.
	 
	*/

	public BotFrameworkAdapter(ICredentialProvider credentialProvider, IChannelProvider channelProvider, RetryPolicy connectorClientRetryPolicy, HttpClient customHttpClient)
	{
		this(credentialProvider, channelProvider, connectorClientRetryPolicy, customHttpClient, null);
	}

	public BotFrameworkAdapter(ICredentialProvider credentialProvider, IChannelProvider channelProvider, RetryPolicy connectorClientRetryPolicy)
	{
		this(credentialProvider, channelProvider, connectorClientRetryPolicy, null, null);
	}

	public BotFrameworkAdapter(ICredentialProvider credentialProvider, IChannelProvider channelProvider)
	{
		this(credentialProvider, channelProvider, null, null, null);
	}

	public BotFrameworkAdapter(ICredentialProvider credentialProvider)
	{
		this(credentialProvider, null, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public BotFrameworkAdapter(ICredentialProvider credentialProvider, IChannelProvider channelProvider = null, RetryPolicy connectorClientRetryPolicy = null, HttpClient customHttpClient = null, IMiddleware middleware = null)
	public BotFrameworkAdapter(ICredentialProvider credentialProvider, IChannelProvider channelProvider, RetryPolicy connectorClientRetryPolicy, HttpClient customHttpClient, IMiddleware middleware)
	{
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: _credentialProvider = credentialProvider ?? throw new ArgumentNullException(nameof(credentialProvider));
		_credentialProvider = (credentialProvider != null) ? credentialProvider : throw new NullPointerException("credentialProvider");
		_channelProvider = channelProvider;
		_httpClient = (customHttpClient != null) ? customHttpClient : DefaultHttpClient;
		_connectorClientRetryPolicy = connectorClientRetryPolicy;

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
	 
	 {@link ProcessActivityAsync(string, Activity, BotCallbackHandler, CancellationToken)}
	 {@link BotAdapter.RunPipelineAsync(ITurnContext, BotCallbackHandler, CancellationToken)}
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task ContinueConversationAsync(string botAppId, ConversationReference reference, BotCallbackHandler callback, CancellationToken cancellationToken)
	@Override
	public Task ContinueConversationAsync(String botAppId, ConversationReference reference, BotCallbackHandler callback, CancellationToken cancellationToken)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(botAppId))
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

		try (TurnContext context = new TurnContext(this, reference.GetContinuationActivity()))
		{
			// Hand craft Claims Identity.
			ClaimsIdentity claimsIdentity = new ClaimsIdentity(new ArrayList<Claim>(Arrays.asList(new Claim(AuthenticationConstants.AudienceClaim, botAppId), new Claim(AuthenticationConstants.AppIdClaim, botAppId))));
				// Adding claims for both Emulator and Channel.

			context.getTurnState().<IIdentity>add(BotIdentityKey, claimsIdentity);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			var connectorClient = await CreateConnectorClientAsync(reference.ServiceUrl, claimsIdentity, cancellationToken).ConfigureAwait(false);
			context.getTurnState().put(connectorClient);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await RunPipelineAsync(context, callback, cancellationToken).ConfigureAwait(false);
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
//ORIGINAL LINE: public new BotFrameworkAdapter Use(IMiddleware middleware)
	public final BotFrameworkAdapter Use(IMiddleware middleware)
	{
		getMiddlewareSet().Use(middleware);
		return this;
	}

	/** 
	 Creates a turn context and runs the middleware pipeline for an incoming activity.
	 
	 @param authHeader The HTTP authentication header of the request.
	 @param activity The incoming activity.
	 @param callback The code to run at the end of the adapter's middleware pipeline.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
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
	 
	 {@link ContinueConversationAsync(string, ConversationReference, BotCallbackHandler, CancellationToken)}
	 {@link BotAdapter.RunPipelineAsync(ITurnContext, BotCallbackHandler, CancellationToken)}
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<InvokeResponse> ProcessActivityAsync(string authHeader, Activity activity, BotCallbackHandler callback, CancellationToken cancellationToken)
	public final Task<InvokeResponse> ProcessActivityAsync(String authHeader, Activity activity, BotCallbackHandler callback, CancellationToken cancellationToken)
	{
		BotAssert.ActivityNotNull(activity);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var claimsIdentity = await JwtTokenValidation.AuthenticateRequest(activity, authHeader, _credentialProvider, _channelProvider, _httpClient).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await ProcessActivityAsync(claimsIdentity, activity, callback, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Creates a turn context and runs the middleware pipeline for an incoming activity.
	 
	 @param identity A <see cref="ClaimsIdentity"/> for the request.
	 @param activity The incoming activity.
	 @param callback The code to run at the end of the adapter's middleware pipeline.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<InvokeResponse> ProcessActivityAsync(ClaimsIdentity identity, Activity activity, BotCallbackHandler callback, CancellationToken cancellationToken)
	public final Task<InvokeResponse> ProcessActivityAsync(ClaimsIdentity identity, Activity activity, BotCallbackHandler callback, CancellationToken cancellationToken)
	{
		BotAssert.ActivityNotNull(activity);

		try (TurnContext context = new TurnContext(this, activity))
		{
			context.getTurnState().<IIdentity>add(BotIdentityKey, identity);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			var connectorClient = await CreateConnectorClientAsync(activity.ServiceUrl, identity, cancellationToken).ConfigureAwait(false);
			context.getTurnState().put(connectorClient);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await RunPipelineAsync(context, callback, cancellationToken).ConfigureAwait(false);

			// Handle Invoke scenarios, which deviate from the request/response model in that
			// the Bot will return a specific body and return code.
			if (activity.Type == ActivityTypes.Invoke)
			{
				Activity invokeResponse = context.getTurnState().<Activity>Get(InvokeReponseKey);
				if (invokeResponse == null)
				{
					// ToDo: Trace Here
					throw new IllegalStateException("Bot failed to return a valid 'invokeResponse' activity.");
				}
				else
				{
					return (InvokeResponse)invokeResponse.Value;
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
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 an array of <see cref="ResourceResponse"/> objects containing the IDs that
	 the receiving channel assigned to the activities.
	 {@link ITurnContext.OnSendActivities(SendActivitiesHandler)}
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task<ResourceResponse[]> SendActivitiesAsync(ITurnContext turnContext, Activity[] activities, CancellationToken cancellationToken)
	@Override
	public Task<ResourceResponse[]> SendActivitiesAsync(ITurnContext turnContext, Activity[] activities, CancellationToken cancellationToken)
	{
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var response = null;

			if (activity.Type == ActivityTypesEx.Delay)
			{
				// The Activity Schema doesn't have a delay type build in, so it's simulated
				// here in the Bot. This matches the behavior in the Node connector.
				int delayMs = (int)activity.Value;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await Task.Delay(delayMs, cancellationToken).ConfigureAwait(false);

				// No need to create a response. One will be created below.
			}
			else if (activity.Type == ActivityTypesEx.InvokeResponse)
			{
				turnContext.getTurnState().put(InvokeReponseKey, activity);

				// No need to create a response. One will be created below.
			}
			else if (activity.Type == ActivityTypes.Trace && !activity.ChannelId.equals("emulator"))
			{
				// if it is a Trace activity we only send to the channel if it's the emulator.
			}
			else if (!tangible.StringHelper.isNullOrWhiteSpace(activity.ReplyToId))
			{
				IConnectorClient connectorClient = turnContext.getTurnState().<IConnectorClient>Get();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				response = await connectorClient.Conversations.ReplyToActivityAsync(activity, cancellationToken).ConfigureAwait(false);
			}
			else
			{
				IConnectorClient connectorClient = turnContext.getTurnState().<IConnectorClient>Get();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				response = await connectorClient.Conversations.SendToConversationAsync(activity, cancellationToken).ConfigureAwait(false);
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
				response = new ResourceResponse((activity.Id != null) ? activity.Id : "");
			}

			responses[index] = response;
		}

		return responses;
	}

	/** 
	 Replaces an existing activity in the conversation.
	 
	 @param turnContext The context object for the turn.
	 @param activity New replacement activity.
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 <p>Before calling this, set the ID of the replacement activity to the ID
	 of the activity to replace.</p>
	 {@link ITurnContext.OnUpdateActivity(UpdateActivityHandler)}
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task<ResourceResponse> UpdateActivityAsync(ITurnContext turnContext, Activity activity, CancellationToken cancellationToken)
	@Override
	public Task<ResourceResponse> UpdateActivityAsync(ITurnContext turnContext, Activity activity, CancellationToken cancellationToken)
	{
		IConnectorClient connectorClient = turnContext.getTurnState().<IConnectorClient>Get();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await connectorClient.Conversations.UpdateActivityAsync(activity, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Deletes an existing activity in the conversation.
	 
	 @param turnContext The context object for the turn.
	 @param reference Conversation reference for the activity to delete.
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 The <see cref="ConversationReference.ActivityId"/> of the conversation
	 reference identifies the activity to delete.
	 {@link ITurnContext.OnDeleteActivity(DeleteActivityHandler)}
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task DeleteActivityAsync(ITurnContext turnContext, ConversationReference reference, CancellationToken cancellationToken)
	@Override
	public Task DeleteActivityAsync(ITurnContext turnContext, ConversationReference reference, CancellationToken cancellationToken)
	{
		IConnectorClient connectorClient = turnContext.getTurnState().<IConnectorClient>Get();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await connectorClient.Conversations.DeleteActivityAsync(reference.Conversation.Id, reference.ActivityId, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Removes a member from the current conversation.
	 
	 @param turnContext The context object for the turn.
	 @param memberId The ID of the member to remove from the conversation.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task DeleteConversationMemberAsync(ITurnContext turnContext, string memberId, CancellationToken cancellationToken)
	public final Task DeleteConversationMemberAsync(ITurnContext turnContext, String memberId, CancellationToken cancellationToken)
	{
		if (turnContext.getActivity().Conversation == null)
		{
			throw new NullPointerException("BotFrameworkAdapter.deleteConversationMember(): missing conversation");
		}

		if (tangible.StringHelper.isNullOrWhiteSpace(turnContext.getActivity().Conversation.Id))
		{
			throw new NullPointerException("BotFrameworkAdapter.deleteConversationMember(): missing conversation.id");
		}

		IConnectorClient connectorClient = turnContext.getTurnState().<IConnectorClient>Get();

		String conversationId = turnContext.getActivity().Conversation.Id;

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await connectorClient.Conversations.DeleteConversationMemberAsync(conversationId, memberId, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Lists the members of a given activity.
	 
	 @param turnContext The context object for the turn.
	 @param activityId (Optional) Activity ID to enumerate. If not specified the current activities ID will be used.
	 @param cancellationToken Cancellation token.
	 @return List of Members of the activity.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<IList<ChannelAccount>> GetActivityMembersAsync(ITurnContext turnContext, string activityId, CancellationToken cancellationToken)
	public final Task<List<ChannelAccount>> GetActivityMembersAsync(ITurnContext turnContext, String activityId, CancellationToken cancellationToken)
	{
		// If no activity was passed in, use the current activity.
		if (activityId == null)
		{
			activityId = turnContext.getActivity().Id;
		}

		if (turnContext.getActivity().Conversation == null)
		{
			throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation");
		}

		if (tangible.StringHelper.isNullOrWhiteSpace(turnContext.getActivity().Conversation.Id))
		{
			throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation.id");
		}

		IConnectorClient connectorClient = turnContext.getTurnState().<IConnectorClient>Get();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var conversationId = turnContext.getActivity().Conversation.Id;

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		List<ChannelAccount> accounts = await connectorClient.Conversations.GetActivityMembersAsync(conversationId, activityId, cancellationToken).ConfigureAwait(false);

		return accounts;
	}

	/** 
	 Lists the members of the current conversation.
	 
	 @param turnContext The context object for the turn.
	 @param cancellationToken Cancellation token.
	 @return List of Members of the current conversation.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<IList<ChannelAccount>> GetConversationMembersAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	public final Task<List<ChannelAccount>> GetConversationMembersAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	{
		if (turnContext.getActivity().Conversation == null)
		{
			throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation");
		}

		if (tangible.StringHelper.isNullOrWhiteSpace(turnContext.getActivity().Conversation.Id))
		{
			throw new NullPointerException("BotFrameworkAdapter.GetActivityMembers(): missing conversation.id");
		}

		IConnectorClient connectorClient = turnContext.getTurnState().<IConnectorClient>Get();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var conversationId = turnContext.getActivity().Conversation.Id;

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		List<ChannelAccount> accounts = await connectorClient.Conversations.GetConversationMembersAsync(conversationId, cancellationToken).ConfigureAwait(false);
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
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the members of the current conversation.
	 This overload may be called from outside the context of a conversation, as only the
	 bot's service URL and credentials are required.
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<ConversationsResult> GetConversationsAsync(string serviceUrl, MicrosoftAppCredentials credentials, string continuationToken, CancellationToken cancellationToken)
	public final Task<ConversationsResult> GetConversationsAsync(String serviceUrl, MicrosoftAppCredentials credentials, String continuationToken, CancellationToken cancellationToken)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(serviceUrl))
		{
			throw new NullPointerException("serviceUrl");
		}

		if (credentials == null)
		{
			throw new NullPointerException("credentials");
		}

		IConnectorClient connectorClient = CreateConnectorClient(serviceUrl, credentials);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var results = await connectorClient.Conversations.GetConversationsAsync(continuationToken, cancellationToken).ConfigureAwait(false);
		return results;
	}

	/** 
	 Lists the Conversations in which this bot has participated for a given channel server. The
	 channel server returns results in pages and each page will include a `continuationToken`
	 that can be used to fetch the next page of results from the server.
	 
	 @param turnContext The context object for the turn.
	 @param continuationToken
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the members of the current conversation.
	 This overload may be called during standard activity processing, at which point the Bot's
	 service URL and credentials that are part of the current activity processing pipeline
	 will be used.
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<ConversationsResult> GetConversationsAsync(ITurnContext turnContext, string continuationToken, CancellationToken cancellationToken)
	public final Task<ConversationsResult> GetConversationsAsync(ITurnContext turnContext, String continuationToken, CancellationToken cancellationToken)
	{
		IConnectorClient connectorClient = turnContext.getTurnState().<IConnectorClient>Get();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var results = await connectorClient.Conversations.GetConversationsAsync(continuationToken, cancellationToken).ConfigureAwait(false);
		return results;
	}

	/** Attempts to retrieve the token for a user that's in a login flow.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.
	 @param magicCode (Optional) Optional user entered code to validate.
	 @param cancellationToken Cancellation token.
	 @return Token Response.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<TokenResponse> GetUserTokenAsync(ITurnContext turnContext, string connectionName, string magicCode, CancellationToken cancellationToken)
	public final Task<TokenResponse> GetUserTokenAsync(ITurnContext turnContext, String connectionName, String magicCode, CancellationToken cancellationToken)
	{
		BotAssert.ContextNotNull(turnContext);
		if (turnContext.getActivity().From == null || tangible.StringHelper.isNullOrWhiteSpace(turnContext.getActivity().From.Id))
		{
			throw new NullPointerException("BotFrameworkAdapter.GetuserToken(): missing from or from.id");
		}

		if (tangible.StringHelper.isNullOrWhiteSpace(connectionName))
		{
			throw new NullPointerException("connectionName");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var client = await CreateOAuthApiClientAsync(turnContext).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await client.GetUserTokenAsync(turnContext.getActivity().From.Id, connectionName, magicCode, null, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Get the raw signin link to be sent to the user for signin for a connection name.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the raw signin link.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<string> GetOauthSignInLinkAsync(ITurnContext turnContext, string connectionName, CancellationToken cancellationToken)
	public final Task<String> GetOauthSignInLinkAsync(ITurnContext turnContext, String connectionName, CancellationToken cancellationToken)
	{
		BotAssert.ContextNotNull(turnContext);
		if (tangible.StringHelper.isNullOrWhiteSpace(connectionName))
		{
			throw new NullPointerException("connectionName");
		}

		Activity activity = turnContext.getActivity();

		TokenExchangeState tokenExchangeState = new TokenExchangeState();
		tokenExchangeState.ConnectionName = connectionName;
		tokenExchangeState.Conversation = new ConversationReference();
		tokenExchangeState.Conversation.ActivityId = activity.Id;
		tokenExchangeState.Conversation.Bot = activity.Recipient;
		tokenExchangeState.Conversation.ChannelId = activity.ChannelId;
		tokenExchangeState.Conversation.Conversation = activity.Conversation;
		tokenExchangeState.Conversation.ServiceUrl = activity.ServiceUrl;
		tokenExchangeState.Conversation.User = activity.From;
		tokenExchangeState.MsAppId = (_credentialProvider instanceof MicrosoftAppCredentials ? (MicrosoftAppCredentials)_credentialProvider : null) == null ? null : (_credentialProvider instanceof MicrosoftAppCredentials ? (MicrosoftAppCredentials)_credentialProvider : null).MicrosoftAppId;

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var serializedState = JsonConvert.SerializeObject(tokenExchangeState);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var encodedState = Encoding.UTF8.GetBytes(serializedState);
		String state = Convert.ToBase64String(encodedState);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var client = await CreateOAuthApiClientAsync(turnContext).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await client.GetSignInLinkAsync(state, null, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Get the raw signin link to be sent to the user for signin for a connection name.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.
	 @param userId The user id that will be associated with the token.
	 @param finalRedirect The final URL that the OAuth flow will redirect to.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the raw signin link.
	*/

	public final Task<String> GetOauthSignInLinkAsync(ITurnContext turnContext, String connectionName, String userId, String finalRedirect)
	{
		return GetOauthSignInLinkAsync(turnContext, connectionName, userId, finalRedirect, null);
	}

	public final Task<String> GetOauthSignInLinkAsync(ITurnContext turnContext, String connectionName, String userId)
	{
		return GetOauthSignInLinkAsync(turnContext, connectionName, userId, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<string> GetOauthSignInLinkAsync(ITurnContext turnContext, string connectionName, string userId, string finalRedirect = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<String> GetOauthSignInLinkAsync(ITurnContext turnContext, String connectionName, String userId, String finalRedirect, CancellationToken cancellationToken)
	{
		BotAssert.ContextNotNull(turnContext);

		if (tangible.StringHelper.isNullOrWhiteSpace(connectionName))
		{
			throw new NullPointerException("connectionName");
		}

		if (tangible.StringHelper.isNullOrWhiteSpace(userId))
		{
			throw new NullPointerException("userId");
		}

		TokenExchangeState tokenExchangeState = new TokenExchangeState();
		tokenExchangeState.ConnectionName = connectionName;
		tokenExchangeState.Conversation = new ConversationReference();
		tokenExchangeState.Conversation.ActivityId = null;
		tokenExchangeState.Conversation.Bot = new ChannelAccount();
		tokenExchangeState.Conversation.Bot.Role = "bot";
		tokenExchangeState.Conversation.ChannelId = "directline";
		tokenExchangeState.Conversation.Conversation = new ConversationAccount();
		tokenExchangeState.Conversation.ServiceUrl = null;
		tokenExchangeState.Conversation.User = new ChannelAccount();
		tokenExchangeState.Conversation.User.Role = "user";
		tokenExchangeState.Conversation.User.Id = userId;
		tokenExchangeState.MsAppId = (this._credentialProvider instanceof MicrosoftAppCredentials ? (MicrosoftAppCredentials)this._credentialProvider : null) == null ? null : (this._credentialProvider instanceof MicrosoftAppCredentials ? (MicrosoftAppCredentials)this._credentialProvider : null).MicrosoftAppId;

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var serializedState = JsonConvert.SerializeObject(tokenExchangeState);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var encodedState = Encoding.UTF8.GetBytes(serializedState);
		String state = Convert.ToBase64String(encodedState);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var client = await CreateOAuthApiClientAsync(turnContext).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await client.GetSignInLinkAsync(state, finalRedirect, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Signs the user out with the token server.
	 
	 @param turnContext Context for the current turn of conversation with the user.
	 @param connectionName Name of the auth connection to use.
	 @param userId User id of user to sign out.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/

	public final Task SignOutUserAsync(ITurnContext turnContext, String connectionName, String userId)
	{
		return SignOutUserAsync(turnContext, connectionName, userId, null);
	}

	public final Task SignOutUserAsync(ITurnContext turnContext, String connectionName)
	{
		return SignOutUserAsync(turnContext, connectionName, null, null);
	}

	public final Task SignOutUserAsync(ITurnContext turnContext)
	{
		return SignOutUserAsync(turnContext, null, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task SignOutUserAsync(ITurnContext turnContext, string connectionName = null, string userId = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task SignOutUserAsync(ITurnContext turnContext, String connectionName, String userId, CancellationToken cancellationToken)
	{
		BotAssert.ContextNotNull(turnContext);

		if (tangible.StringHelper.isNullOrEmpty(userId))
		{
			userId = turnContext.getActivity() == null ? null : (turnContext.getActivity().From == null ? null : turnContext.getActivity().From.Id);
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var client = await CreateOAuthApiClientAsync(turnContext).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await client.SignOutUserAsync(userId, connectionName, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Retrieves the token status for each configured connection for the given user.
	 
	 @param context Context for the current turn of conversation with the user.
	 @param userId The user Id for which token status is retrieved.
	 @param includeFilter Optional comma seperated list of connection's to include. Blank will return token status for all configured connections.
	 @return Array of TokenStatus.
	*/

	public final Task<TokenStatus[]> GetTokenStatusAsync(ITurnContext context, String userId)
	{
		return GetTokenStatusAsync(context, userId, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<TokenStatus[]> GetTokenStatusAsync(ITurnContext context, string userId, string includeFilter = null)
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<TokenStatus[]> GetTokenStatusAsync(ITurnContext context, String userId, String includeFilter)
	{
		BotAssert.ContextNotNull(context);

		if (tangible.StringHelper.isNullOrWhiteSpace(userId))
		{
			throw new NullPointerException("userId");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var client = await this.CreateOAuthApiClientAsync(context).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await client.GetTokenStatusAsync(userId, includeFilter).ConfigureAwait(false);
	}

	/** 
	 Retrieves Azure Active Directory tokens for particular resources on a configured connection.
	 
	 @param context Context for the current turn of conversation with the user.
	 @param connectionName The name of the Azure Active Direcotry connection configured with this bot.
	 @param resourceUrls The list of resource URLs to retrieve tokens for.
	 @param userId The user Id for which tokens are retrieved. If passing in null the userId is taken from the Activity in the ITurnContext.
	 @return Dictionary of resourceUrl to the corresponding TokenResponse.
	*/

	public final Task<java.util.HashMap<String, TokenResponse>> GetAadTokensAsync(ITurnContext context, String connectionName, String[] resourceUrls)
	{
		return GetAadTokensAsync(context, connectionName, resourceUrls, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<Dictionary<string, TokenResponse>> GetAadTokensAsync(ITurnContext context, string connectionName, string[] resourceUrls, string userId = null)
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<HashMap<String, TokenResponse>> GetAadTokensAsync(ITurnContext context, String connectionName, String[] resourceUrls, String userId)
	{
		BotAssert.ContextNotNull(context);

		if (tangible.StringHelper.isNullOrWhiteSpace(connectionName))
		{
			throw new NullPointerException("connectionName");
		}

		if (resourceUrls == null)
		{
			throw new NullPointerException("userId");
		}

		if (tangible.StringHelper.isNullOrWhiteSpace(userId))
		{
			userId = context.getActivity() == null ? null : (context.getActivity().From == null ? null : context.getActivity().From.Id);
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var client = await this.CreateOAuthApiClientAsync(context).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await client.GetAadTokensAsync(userId, connectionName, resourceUrls).ConfigureAwait(false);
	}

	/** 
	 Creates a conversation on the specified channel.
	 
	 @param channelId The ID for the channel.
	 @param serviceUrl The channel's service URL endpoint.
	 @param credentials The application credentials for the bot.
	 @param conversationParameters The conversation information to use to
	 create the conversation.
	 @param callback The method to call for the resulting bot turn.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public virtual async Task CreateConversationAsync(string channelId, string serviceUrl, MicrosoftAppCredentials credentials, ConversationParameters conversationParameters, BotCallbackHandler callback, CancellationToken cancellationToken)
	public Task CreateConversationAsync(String channelId, String serviceUrl, MicrosoftAppCredentials credentials, ConversationParameters conversationParameters, BotCallbackHandler callback, CancellationToken cancellationToken)
	{
		IConnectorClient connectorClient = CreateConnectorClient(serviceUrl, credentials);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var result = await connectorClient.Conversations.CreateConversationAsync(conversationParameters, cancellationToken).ConfigureAwait(false);

		// Create a conversation update activity to represent the result.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var eventActivity = Activity.CreateEventActivity();
		eventActivity.Name = "CreateConversation";
		eventActivity.ChannelId = channelId;
		eventActivity.ServiceUrl = serviceUrl;
		eventActivity.Id = (result.ActivityId != null) ? result.ActivityId : UUID.NewGuid().toString("n");
//C# TO JAVA CONVERTER TODO TASK: C# to Java Converter could not resolve the named parameters in the following line:
//ORIGINAL LINE: eventActivity.Conversation = new ConversationAccount(id: result.Id);
		eventActivity.Conversation = new ConversationAccount(id: result.Id);
		eventActivity.Recipient = conversationParameters.Bot;

		try (TurnContext context = new TurnContext(this, (Activity)eventActivity))
		{
			ClaimsIdentity claimsIdentity = new ClaimsIdentity();
			claimsIdentity.AddClaim(new Claim(AuthenticationConstants.AudienceClaim, credentials.MicrosoftAppId));
			claimsIdentity.AddClaim(new Claim(AuthenticationConstants.AppIdClaim, credentials.MicrosoftAppId));
			claimsIdentity.AddClaim(new Claim(AuthenticationConstants.ServiceUrlClaim, serviceUrl));

			context.getTurnState().<IIdentity>add(BotIdentityKey, claimsIdentity);
			context.getTurnState().put(connectorClient);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await RunPipelineAsync(context, callback, cancellationToken).ConfigureAwait(false);
		}
	}

	/** 
	 Creates an OAuth client for the bot.
	 
	 @param turnContext The context object for the current turn.
	 @return An OAuth client for the bot.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: protected async Task<OAuthClient> CreateOAuthApiClientAsync(ITurnContext turnContext)
	protected final Task<OAuthClient> CreateOAuthApiClientAsync(ITurnContext turnContext)
	{
		IConnectorClient tempVar = turnContext.getTurnState().<IConnectorClient>Get();
		ConnectorClient client = tempVar instanceof ConnectorClient ? (ConnectorClient)tempVar : null;
		if (client == null)
		{
			throw new NullPointerException("CreateOAuthApiClient: OAuth requires a valid ConnectorClient instance");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		if (!OAuthClient.EmulateOAuthCards && String.equals(turnContext.getActivity().ChannelId, "emulator", StringComparison.InvariantCultureIgnoreCase) && (await _credentialProvider.IsAuthenticationDisabledAsync().ConfigureAwait(false)))
		{
			OAuthClient.EmulateOAuthCards = true;
		}

		if (OAuthClient.EmulateOAuthCards)
		{
			OAuthClient oauthClient = new OAuthClient(client, turnContext.getActivity().ServiceUrl);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			Task.Run(async() -> await oauthClient.SendEmulateOAuthCardsAsync(OAuthClient.EmulateOAuthCards).ConfigureAwait(false)).Wait();
			return oauthClient;
		}

		return new OAuthClient(client, OAuthClient.OAuthEndpoint);
	}

	/** 
	 Creates the connector client asynchronous.
	 
	 @param serviceUrl The service URL.
	 @param claimsIdentity The claims identity.
	 @param cancellationToken Cancellation token.
	 @return ConnectorClient instance.
	 @exception NotSupportedException ClaimsIdemtity cannot be null. Pass Anonymous ClaimsIdentity if authentication is turned off.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async Task<IConnectorClient> CreateConnectorClientAsync(string serviceUrl, ClaimsIdentity claimsIdentity, CancellationToken cancellationToken)
	private Task<IConnectorClient> CreateConnectorClientAsync(String serviceUrl, ClaimsIdentity claimsIdentity, CancellationToken cancellationToken)
	{
		if (claimsIdentity == null)
		{
			throw new UnsupportedOperationException("ClaimsIdemtity cannot be null. Pass Anonymous ClaimsIdentity if authentication is turned off.");
		}

		// For requests from channel App Id is in Audience claim of JWT token. For emulator it is in AppId claim. For
		// unauthenticated requests we have anonymouse identity provided auth is disabled.
		// For Activities coming from Emulator AppId claim contains the Bot's AAD AppId.
		boolean botAppIdClaim = claimsIdentity.Claims == null ? null : (claimsIdentity.Claims.SingleOrDefault(claim -> claim.Type == AuthenticationConstants.AudienceClaim) != null) ? claimsIdentity.Claims.SingleOrDefault(claim -> claim.Type == AuthenticationConstants.AudienceClaim) : claimsIdentity.Claims == null ? null : claimsIdentity.Claims.SingleOrDefault(claim -> claim.Type == AuthenticationConstants.AppIdClaim);

		// For anonymous requests (requests with no header) appId is not set in claims.
		if (botAppIdClaim != null)
		{
			String botId = botAppIdClaim.Value;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			var appCredentials = await GetAppCredentialsAsync(botId, cancellationToken).ConfigureAwait(false);
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
	 @param appCredentials The application credentials for the bot.
	 @return Connector client instance.
	*/

	private IConnectorClient CreateConnectorClient(String serviceUrl)
	{
		return CreateConnectorClient(serviceUrl, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: private IConnectorClient CreateConnectorClient(string serviceUrl, MicrosoftAppCredentials appCredentials = null)
	private IConnectorClient CreateConnectorClient(String serviceUrl, MicrosoftAppCredentials appCredentials)
	{
		String clientKey = String.format("%1$s%2$s", serviceUrl, appCredentials?(.MicrosoftAppId != null) ? .MicrosoftAppId : "");

		return _connectorClients.putIfAbsent(clientKey, (key) ->
		{
				ConnectorClient connectorClient;
				if (appCredentials != null)
				{
					connectorClient = new ConnectorClient(new Uri(serviceUrl), appCredentials);
				}
				else
				{
					var emptyCredentials = (_channelProvider != null && _channelProvider.IsGovernment()) ? MicrosoftGovernmentAppCredentials.Empty : MicrosoftAppCredentials.Empty;
					connectorClient = new ConnectorClient(new Uri(serviceUrl), emptyCredentials);
				}

				if (_connectorClientRetryPolicy != null)
				{
					connectorClient.SetRetryPolicy(_connectorClientRetryPolicy);
				}

				return connectorClient;
		});
	}

	/** 
	 Gets the application credentials. App Credentials are cached so as to ensure we are not refreshing
	 token everytime.
	 
	 @param appId The application identifier (AAD Id for the bot).
	 @param cancellationToken Cancellation token.
	 @return App credentials.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async Task<MicrosoftAppCredentials> GetAppCredentialsAsync(string appId, CancellationToken cancellationToken)
	private Task<MicrosoftAppCredentials> GetAppCredentialsAsync(String appId, CancellationToken cancellationToken)
	{
		if (appId == null)
		{
			return MicrosoftAppCredentials.Empty;
		}

		TValue appCredentials;
//C# TO JAVA CONVERTER TODO TASK: There is no Java ConcurrentHashMap equivalent to this .NET ConcurrentDictionary method:
//C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
		if (_appCredentialMap.TryGetValue(appId, out appCredentials))
		{
			return appCredentials;
		}

		// NOTE: we can't do async operations inside of a AddOrUpdate, so we split access pattern
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		String appPassword = await _credentialProvider.GetAppPasswordAsync(appId).ConfigureAwait(false);
		appCredentials = (_channelProvider != null && _channelProvider.IsGovernment()) ? new MicrosoftGovernmentAppCredentials(appId, appPassword) : new MicrosoftAppCredentials(appId, appPassword);
		_appCredentialMap.put(appId, appCredentials);
		return appCredentials;
	}
}