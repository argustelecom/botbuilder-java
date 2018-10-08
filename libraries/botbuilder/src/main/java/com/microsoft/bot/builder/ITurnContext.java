package Microsoft.Bot.Builder;

/** 
 Provides context for a turn of a bot.
 
 Context provides information needed to process an incoming activity.
 The context object is created by a <see cref="BotAdapter"/> and persists for the
 length of the turn.
 {@link IBot}
 {@link IMiddleware}
*/
public interface ITurnContext
{
	/** 
	 Gets the bot adapter that created this context object.
	 
	 <value>The bot adapter that created this context object.</value>
	*/
	BotAdapter getAdapter();

	/** 
	 Collection of values cached with the context object for the lifetime of the turn.
	 
	 <value>The collection of services registered on this context object.</value>
	*/
	TurnContextStateCollection getTurnState();

	/** 
	 Gets the incoming request.
	 
	 <value>The incoming request.</value>
	*/
	Activity getActivity();

	/** 
	 Gets a value indicating whether at least one response was sent for the current turn.
	 
	 <value><c>true</c> if at least one response was sent for the current turn; otherwise, <c>false</c>.</value>
	*/
	boolean getResponded();

	/** 
	 Sends a message activity to the sender of the incoming activity.
	 
	 @param textReplyToSend The text of the message to send.
	 @param speak Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is "acceptingInput".
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 <p>See the channel's documentation for limits imposed upon the contents of
	 <paramref name="textReplyToSend"/>.</p>
	 <p>To control various characteristics of your bot's speech such as voice,
	 rate, volume, pronunciation, and pitch, specify <paramref name="speak"/> in
	 Speech Synthesis Markup Language (SSML) format.</p>
	 
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link SendActivityAsync(IActivity, CancellationToken)}
	 {@link SendActivitiesAsync(IActivity[], CancellationToken)}
	 {@link UpdateActivityAsync(IActivity, CancellationToken)}
	 {@link DeleteActivityAsync(ConversationReference, CancellationToken)}
	*/

	Task<ResourceResponse> SendActivityAsync(String textReplyToSend, String speak, String inputHint);
	Task<ResourceResponse> SendActivityAsync(String textReplyToSend, String speak);
	Task<ResourceResponse> SendActivityAsync(String textReplyToSend);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task<ResourceResponse> SendActivityAsync(string textReplyToSend, string speak = null, string inputHint = InputHints.AcceptingInput, CancellationToken cancellationToken = default(CancellationToken));
	Task<ResourceResponse> SendActivityAsync(String textReplyToSend, String speak, String inputHint, CancellationToken cancellationToken);

	/** 
	 Sends an activity to the sender of the incoming activity.
	 
	 @param activity The activity to send.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link SendActivityAsync(string, string, string, CancellationToken)}
	 {@link SendActivitiesAsync(IActivity[], CancellationToken)}
	 {@link UpdateActivityAsync(IActivity, CancellationToken)}
	 {@link DeleteActivityAsync(ConversationReference, CancellationToken)}
	*/

	Task<ResourceResponse> SendActivityAsync(IActivity activity);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task<ResourceResponse> SendActivityAsync(IActivity activity, CancellationToken cancellationToken = default(CancellationToken));
	Task<ResourceResponse> SendActivityAsync(IActivity activity, CancellationToken cancellationToken);

	/** 
	 Sends a set of activities to the sender of the incoming activity.
	 
	 @param activities The activities to send.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 an array of <see cref="ResourceResponse"/> objects containing the IDs that
	 the receiving channel assigned to the activities.
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link SendActivityAsync(string, string, string, CancellationToken)}
	 {@link SendActivityAsync(IActivity, CancellationToken)}
	 {@link UpdateActivityAsync(IActivity, CancellationToken)}
	 {@link DeleteActivityAsync(ConversationReference, CancellationToken)}
	*/

	Task<ResourceResponse[]> SendActivitiesAsync(IActivity[] activities);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task<ResourceResponse[]> SendActivitiesAsync(IActivity[] activities, CancellationToken cancellationToken = default(CancellationToken));
	Task<ResourceResponse[]> SendActivitiesAsync(IActivity[] activities, CancellationToken cancellationToken);

	/** 
	 Replaces an existing activity.
	 
	 @param activity New replacement activity.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 <p>Before calling this, set the ID of the replacement activity to the ID
	 of the activity to replace.</p>
	 <p>Not all channels support this operation. Channels that don't, may throw an exception.</p>
	 {@link OnUpdateActivity(UpdateActivityHandler)}
	 {@link SendActivitiesAsync(IActivity[], CancellationToken)}
	 {@link DeleteActivityAsync(ConversationReference, CancellationToken)}
	*/

	Task<ResourceResponse> UpdateActivityAsync(IActivity activity);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task<ResourceResponse> UpdateActivityAsync(IActivity activity, CancellationToken cancellationToken = default(CancellationToken));
	Task<ResourceResponse> UpdateActivityAsync(IActivity activity, CancellationToken cancellationToken);

	/** 
	 Deletes an existing activity.
	 
	 @param activityId The ID of the activity to delete.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 Not all channels support this operation. Channels that don't, may throw an exception.
	 {@link OnDeleteActivity(DeleteActivityHandler)}
	 {@link DeleteActivityAsync(ConversationReference, CancellationToken)}
	 {@link SendActivitiesAsync(IActivity[], CancellationToken)}
	 {@link UpdateActivityAsync(IActivity, CancellationToken)}
	*/

	Task DeleteActivityAsync(String activityId);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task DeleteActivityAsync(string activityId, CancellationToken cancellationToken = default(CancellationToken));
	Task DeleteActivityAsync(String activityId, CancellationToken cancellationToken);

	/** 
	 Deletes an existing activity.
	 
	 @param conversationReference The conversation containing the activity to delete.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 The conversation reference's <see cref="ConversationReference.ActivityId"/>
	 indicates the activity in the conversation to delete.
	 <p>Not all channels support this operation. Channels that don't, may throw an exception.</p>
	 {@link OnDeleteActivity(DeleteActivityHandler)}
	 {@link DeleteActivityAsync(string, CancellationToken)}
	 {@link SendActivitiesAsync(IActivity[], CancellationToken)}
	 {@link UpdateActivityAsync(IActivity, CancellationToken)}
	*/

	Task DeleteActivityAsync(ConversationReference conversationReference);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task DeleteActivityAsync(ConversationReference conversationReference, CancellationToken cancellationToken = default(CancellationToken));
	Task DeleteActivityAsync(ConversationReference conversationReference, CancellationToken cancellationToken);

	/** 
	 Adds a response handler for send activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 When the context's <see cref="SendActivityAsync(IActivity, CancellationToken)"/>
	 or <see cref="SendActivitiesAsync(IActivity[], CancellationToken)"/> methods are called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	 {@link SendActivityAsync(string, string, string, CancellationToken)}
	 {@link SendActivityAsync(IActivity, CancellationToken)}
	 {@link SendActivitiesAsync(IActivity[], CancellationToken)}
	 {@link SendActivitiesHandler}
	 {@link OnUpdateActivity(UpdateActivityHandler)}
	 {@link OnDeleteActivity(DeleteActivityHandler)}
	*/
	ITurnContext OnSendActivities(SendActivitiesHandler handler);

	/** 
	 Adds a response handler for update activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 When the context's <see cref="UpdateActivityAsync(IActivity, CancellationToken)"/> is called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	 {@link UpdateActivityAsync(IActivity, CancellationToken)}
	 {@link UpdateActivityHandler}
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link OnDeleteActivity(DeleteActivityHandler)}
	*/
	ITurnContext OnUpdateActivity(UpdateActivityHandler handler);

	/** 
	 Adds a response handler for delete activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 @exception ArgumentNullException <paramref name="handler"/> is <c>null</c>.
	 When the context's <see cref="DeleteActivityAsync(string, CancellationToken)"/> is called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	 {@link DeleteActivityAsync(ConversationReference, CancellationToken)}
	 {@link DeleteActivityAsync(string, CancellationToken)}
	 {@link DeleteActivityHandler}
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link OnUpdateActivity(UpdateActivityHandler)}
	*/
	ITurnContext OnDeleteActivity(DeleteActivityHandler handler);
}