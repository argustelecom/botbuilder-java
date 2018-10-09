package com.microsoft.bot.builder;

import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ConversationReference;
import com.microsoft.bot.schema.models.ResourceResponse;

import java.util.concurrent.CompletableFuture;

/**
 Provides context for a turn of a bot.
 
 Context provides information needed to process an incoming activity.
 The context object is created by a <see cref="BotAdapter"/> and persists for the
 length of the turn.
 {@link Bot}
 {@link Middleware}
*/
public interface TurnContext
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
	 {@link SendActivityAsync(IActivity )}
	 {@link SendActivitiesAsync(IActivity[] )}
	 {@link UpdateActivityAsync(IActivity )}
	 {@link DeleteActivityAsync(ConversationReference )}
	*/

	CompletableFuture<ResourceResponse> SendActivityAsync(String textReplyToSend, String speak, String inputHint);
	CompletableFuture<ResourceResponse> SendActivityAsync(String textReplyToSend, String speak);
	CompletableFuture<ResourceResponse> SendActivityAsync(String textReplyToSend);

	/** 
	 Sends an activity to the sender of the incoming activity.
	 
	 @param activity The activity to send.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link SendActivityAsync(string, string, string )}
	 {@link SendActivitiesAsync(IActivity[] )}
	 {@link UpdateActivityAsync(IActivity )}
	 {@link DeleteActivityAsync(ConversationReference )}
	*/

	CompletableFuture<ResourceResponse> SendActivityAsync(Activity activity);

	/** 
	 Sends a set of activities to the sender of the incoming activity.
	 
	 @param activities The activities to send.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 an array of <see cref="ResourceResponse"/> objects containing the IDs that
	 the receiving channel assigned to the activities.
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link SendActivityAsync(string, string, string )}
	 {@link SendActivityAsync(IActivity )}
	 {@link UpdateActivityAsync(IActivity )}
	 {@link DeleteActivityAsync(ConversationReference )}
	*/

	CompletableFuture<ResourceResponse[]> SendActivitiesAsync(Activity[] activities);

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
	 {@link SendActivitiesAsync(IActivity[] )}
	 {@link DeleteActivityAsync(ConversationReference )}
	*/

	CompletableFuture<ResourceResponse> UpdateActivityAsync(Activity activity);

	/** 
	 Deletes an existing activity.
	 
	 @param activityId The ID of the activity to delete.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 Not all channels support this operation. Channels that don't, may throw an exception.
	 {@link OnDeleteActivity(DeleteActivityHandler)}
	 {@link DeleteActivityAsync(ConversationReference )}
	 {@link SendActivitiesAsync(IActivity[] )}
	 {@link UpdateActivityAsync(IActivity )}
	*/

	CompletableFuture DeleteActivityAsync(String activityId);

	/** 
	 Deletes an existing activity.
	 
	 @param conversationReference The conversation containing the activity to delete.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 The conversation reference's <see cref="ConversationReference.ActivityId"/>
	 indicates the activity in the conversation to delete.
	 <p>Not all channels support this operation. Channels that don't, may throw an exception.</p>
	 {@link OnDeleteActivity(DeleteActivityHandler)}
	 {@link DeleteActivityAsync(string )}
	 {@link SendActivitiesAsync(IActivity[] )}
	 {@link UpdateActivityAsync(IActivity )}
	*/

	CompletableFuture DeleteActivityAsync(ConversationReference conversationReference);

	/** 
	 Adds a response handler for send activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 When the context's <see cref="SendActivityAsync(IActivity, CancellationToken)"/>
	 or <see cref="SendActivitiesAsync(IActivity[], CancellationToken)"/> methods are called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	 {@link SendActivityAsync(string, string, string )}
	 {@link SendActivityAsync(IActivity )}
	 {@link SendActivitiesAsync(IActivity[] )}
	 {@link SendActivitiesHandler}
	 {@link OnUpdateActivity(UpdateActivityHandler)}
	 {@link OnDeleteActivity(DeleteActivityHandler)}
	*/
	TurnContext OnSendActivities(SendActivitiesHandler handler);

	/** 
	 Adds a response handler for update activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 When the context's <see cref="UpdateActivity(IActivity, CancellationToken)"/> is called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	 {@link UpdateActivityAsync(IActivity )}
	 {@link UpdateActivityHandler}
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link OnDeleteActivity(DeleteActivityHandler)}
	*/
	TurnContext OnUpdateActivity(UpdateActivityHandler handler);

	/** 
	 Adds a response handler for delete activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 @exception ArgumentNullException <paramref name="handler"/> is <c>null</c>.
	 When the context's <see cref="DeleteActivity(string, CancellationToken)"/> is called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	 {@link DeleteActivityAsync(ConversationReference )}
	 {@link DeleteActivityAsync(string )}
	 {@link DeleteActivityHandler}
	 {@link OnSendActivities(SendActivitiesHandler)}
	 {@link OnUpdateActivity(UpdateActivityHandler)}
	*/
	TurnContext OnDeleteActivity(DeleteActivityHandler handler);
}