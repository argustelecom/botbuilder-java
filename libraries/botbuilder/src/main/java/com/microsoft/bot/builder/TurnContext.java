package Microsoft.Bot.Builder;

import java.util.*;
import java.io.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Provides context for a turn of a bot.
 
 Context provides information needed to process an incoming activity.
 The context object is created by a <see cref="BotAdapter"/> and persists for the
 length of the turn.
 {@link IBot}
 {@link IMiddleware}
*/
public class TurnContext implements ITurnContext, Closeable
{
	private final List<SendActivitiesHandler> _onSendActivities = new ArrayList<SendActivitiesHandler>();
	private final List<UpdateActivityHandler> _onUpdateActivity = new ArrayList<UpdateActivityHandler>();
	private final List<DeleteActivityHandler> _onDeleteActivity = new ArrayList<DeleteActivityHandler>();

	/** 
	 Initializes a new instance of the <see cref="TurnContext"/> class.
	 
	 @param adapter The adapter creating the context.
	 @param activity The incoming activity for the turn;
	 or <c>null</c> for a turn for a proactive message.
	 @exception ArgumentNullException <paramref name="activity"/> or
	 <paramref name="adapter"/> is <c>null</c>.
	 For use by bot adapter implementations only.
	*/
	public TurnContext(BotAdapter adapter, Activity activity)
	{
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: Adapter = adapter ?? throw new ArgumentNullException(nameof(adapter));
		Adapter = (adapter != null) ? adapter : throw new NullPointerException("adapter");
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: Activity = activity ?? throw new ArgumentNullException(nameof(activity));
		Activity = (activity != null) ? activity : throw new NullPointerException("activity");
	}

	/** 
	 Gets the bot adapter that created this context object.
	 
	 <value>The bot adapter that created this context object.</value>
	*/
	private BotAdapter Adapter;
	public final BotAdapter getAdapter()
	{
		return Adapter;
	}

	/** 
	 Gets the services registered on this context object.
	 
	 <value>The services registered on this context object.</value>
	*/
	private TurnContextStateCollection TurnState = new TurnContextStateCollection();
	public final TurnContextStateCollection getTurnState()
	{
		return TurnState;
	}

	/** 
	 Gets the activity associated with this turn; or <c>null</c> when processing
	 a proactive message.
	 
	 <value>The activity associated with this turn.</value>
	*/
	private Activity Activity;
	public final Activity getActivity()
	{
		return Activity;
	}

	/** 
	 Gets a value indicating whether at least one response was sent for the current turn.
	 
	 <value><c>true</c> if at least one response was sent for the current turn.</value>
	 <see cref="ITraceActivity"/> activities on their own do not set this flag.
	*/
	private boolean Responded;
	public final boolean getResponded()
	{
		return Responded;
	}
	private void setResponded(boolean value)
	{
		Responded = value;
	}

	/** 
	 Adds a response handler for send activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 @exception ArgumentNullException <paramref name="handler"/> is <c>null</c>.
	 When the context's <see cref="SendActivityAsync(IActivity, CancellationToken)"/>
	 or <see cref="SendActivitiesAsync(IActivity[], CancellationToken)"/> methods are called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	*/
	public final ITurnContext OnSendActivities(SendActivitiesHandler handler)
	{
		if (handler == null)
		{
			throw new NullPointerException("handler");
		}

		_onSendActivities.add(handler);
		return this;
	}

	/** 
	 Adds a response handler for update activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 @exception ArgumentNullException <paramref name="handler"/> is <c>null</c>.
	 When the context's <see cref="UpdateActivityAsync(IActivity, CancellationToken)"/> is called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	*/
	public final ITurnContext OnUpdateActivity(UpdateActivityHandler handler)
	{
		if (handler == null)
		{
			throw new NullPointerException("handler");
		}

		_onUpdateActivity.add(handler);
		return this;
	}

	/** 
	 Adds a response handler for delete activity operations.
	 
	 @param handler The handler to add to the context object.
	 @return The updated context object.
	 @exception ArgumentNullException <paramref name="handler"/> is <c>null</c>.
	 When the context's <see cref="DeleteActivityAsync(ConversationReference, CancellationToken)"/>
	 or <see cref="DeleteActivityAsync(string, CancellationToken)"/> is called,
	 the adapter calls the registered handlers in the order in which they were
	 added to the context object.
	 
	*/
	public final ITurnContext OnDeleteActivity(DeleteActivityHandler handler)
	{
		if (handler == null)
		{
			throw new NullPointerException("handler");
		}

		_onDeleteActivity.add(handler);
		return this;
	}

	/** 
	 Sends a message activity to the sender of the incoming activity.
	 
	 @param textReplyToSend The text of the message to send.
	 @param speak Optional, text to be spoken by your bot on a speech-enabled
	 channel.
	 @param inputHint Optional, indicates whether your bot is accepting,
	 expecting, or ignoring user input after the message is delivered to the client.
	 One of: "acceptingInput", "ignoringInput", or "expectingInput".
	 Default is null.
	 @param cancellationToken The cancellation token.
	 @return A task that represents the work queued to execute.
	 @exception ArgumentNullException
	 <paramref name="textReplyToSend"/> is <c>null</c> or whitespace.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 <p>See the channel's documentation for limits imposed upon the contents of
	 <paramref name="textReplyToSend"/>.</p>
	 <p>To control various characteristics of your bot's speech such as voice,
	 rate, volume, pronunciation, and pitch, specify <paramref name="speak"/> in
	 Speech Synthesis Markup Language (SSML) format.</p>
	 
	*/

	public final Task<ResourceResponse> SendActivityAsync(String textReplyToSend, String speak, String inputHint)
	{
		return SendActivityAsync(textReplyToSend, speak, inputHint, null);
	}

	public final Task<ResourceResponse> SendActivityAsync(String textReplyToSend, String speak)
	{
		return SendActivityAsync(textReplyToSend, speak, null, null);
	}

	public final Task<ResourceResponse> SendActivityAsync(String textReplyToSend)
	{
		return SendActivityAsync(textReplyToSend, null, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<ResourceResponse> SendActivityAsync(string textReplyToSend, string speak = null, string inputHint = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<ResourceResponse> SendActivityAsync(String textReplyToSend, String speak, String inputHint, CancellationToken cancellationToken)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(textReplyToSend))
		{
			throw new NullPointerException("textReplyToSend");
		}

		Activity activityToSend = new Activity(ActivityTypes.Message);
		activityToSend.Text = textReplyToSend;

		if (!tangible.StringHelper.isNullOrEmpty(speak))
		{
			activityToSend.Speak = speak;
		}

		if (!tangible.StringHelper.isNullOrEmpty(inputHint))
		{
			activityToSend.InputHint = inputHint;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await SendActivityAsync(activityToSend, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Sends an activity to the sender of the incoming activity.
	 
	 @param activity The activity to send.
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 @exception ArgumentNullException <paramref name="activity"/> is <c>null</c>.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	*/

	public final Task<ResourceResponse> SendActivityAsync(IActivity activity)
	{
		return SendActivityAsync(activity, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<ResourceResponse> SendActivityAsync(IActivity activity, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<ResourceResponse> SendActivityAsync(IActivity activity, CancellationToken cancellationToken)
	{
		BotAssert.ActivityNotNull(activity);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		ResourceResponse[] responses = await SendActivitiesAsync(new IActivity[] {activity}, cancellationToken).ConfigureAwait(false);
		if (responses == null || responses.length == 0)
		{
			// It's possible an interceptor prevented the activity from having been sent.
			// Just return an empty response in that case.
			return new ResourceResponse();
		}
		else
		{
			return responses[0];
		}
	}

	/** 
	 Sends a set of activities to the sender of the incoming activity.
	 
	 @param activities The activities to send.
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 an array of <see cref="ResourceResponse"/> objects containing the IDs that
	 the receiving channel assigned to the activities.
	*/

	public final Task<ResourceResponse[]> SendActivitiesAsync(IActivity[] activities)
	{
		return SendActivitiesAsync(activities, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public Task<ResourceResponse[]> SendActivitiesAsync(IActivity[] activities, CancellationToken cancellationToken = default(CancellationToken))
	public final Task<ResourceResponse[]> SendActivitiesAsync(IActivity[] activities, CancellationToken cancellationToken)
	{
		if (activities == null)
		{
			throw new NullPointerException("activities");
		}

		if (activities.length == 0)
		{
			throw new IllegalArgumentException("Expecting one or more activities, but the array was empty.", "activities");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var conversationReference = this.getActivity().GetConversationReference();

		ArrayList<Activity> bufferedActivities = new ArrayList<Activity>(activities.length);

		for (int index = 0; index < activities.length; index++)
		{
			// Buffer the incoming activities into a List<T> since we allow the set to be manipulated by the callbacks
			// Bind the relevant Conversation Reference properties, such as URLs and
			// ChannelId's, to the activity we're about to send
			bufferedActivities.add(activities[index].ApplyConversationReference(conversationReference));
		}

		// If there are no callbacks registered, bypass the overhead of invoking them and send directly to the adapter
		if (_onSendActivities.isEmpty())
		{
			return SendActivitiesThroughAdapter();
		}

		// Send through the full callback pipeline
		return SendActivitiesThroughCallbackPipeline();

//C# TO JAVA CONVERTER TODO TASK: Local functions are not converted by C# to Java Converter:
//		Task<ResourceResponse[]> SendActivitiesThroughCallbackPipeline(int nextCallbackIndex = 0)
//			{
//				// If we've executed the last callback, we now send straight to the adapter
//				if (nextCallbackIndex == _onSendActivities.Count)
//				{
//					return SendActivitiesThroughAdapter();
//				}
//
//				return _onSendActivities[nextCallbackIndex].Invoke(this, bufferedActivities, () => SendActivitiesThroughCallbackPipeline(nextCallbackIndex + 1));
//			}

//C# TO JAVA CONVERTER TODO TASK: Local functions are not converted by C# to Java Converter:
//		async Task<ResourceResponse[]> SendActivitiesThroughAdapter()
//			{
//				// Send from the list which may have been manipulated via the event handlers.
//				// Note that 'responses' was captured from the root of the call, and will be
//				// returned to the original caller.
//				var responses = await Adapter.SendActivitiesAsync(this, bufferedActivities.ToArray(), cancellationToken).ConfigureAwait(false);
//				var sentNonTraceActivity = false;
//
//				for (var index = 0; index < responses.Length; index++)
//				{
//					var activity = bufferedActivities[index];
//
//					activity.Id = responses[index].Id;
//
//					sentNonTraceActivity |= activity.Type != ActivityTypes.Trace;
//				}
//
//				if (sentNonTraceActivity)
//				{
//					Responded = true;
//				}
//
//				return responses;
//			}
	}

	/** 
	 Replaces an existing activity.
	 
	 @param activity New replacement activity.
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 @exception Microsoft.Bot.Schema.ErrorResponseException
	 The HTTP operation failed and the response contained additional information.
	 @exception System.AggregateException
	 One or more exceptions occurred during the operation.
	 If the activity is successfully sent, the task result contains
	 a <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 <p>Before calling this, set the ID of the replacement activity to the ID
	 of the activity to replace.</p>
	*/

	public final Task<ResourceResponse> UpdateActivityAsync(IActivity activity)
	{
		return UpdateActivityAsync(activity, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<ResourceResponse> UpdateActivityAsync(IActivity activity, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<ResourceResponse> UpdateActivityAsync(IActivity activity, CancellationToken cancellationToken)
	{
		Activity a = (Activity)activity;

//C# TO JAVA CONVERTER TODO TASK: Local functions are not converted by C# to Java Converter:
//		async Task<ResourceResponse> ActuallyUpdateStuff()
//			{
//				return await Adapter.UpdateActivityAsync(this, a, cancellationToken).ConfigureAwait(false);
//			}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await UpdateActivityInternalAsync(a, _onUpdateActivity, ActuallyUpdateStuff, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Deletes an existing activity.
	 
	 @param activityId The ID of the activity to delete.
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 @exception Microsoft.Bot.Schema.ErrorResponseException
	 The HTTP operation failed and the response contained additional information.
	*/

	public final Task DeleteActivityAsync(String activityId)
	{
		return DeleteActivityAsync(activityId, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task DeleteActivityAsync(string activityId, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task DeleteActivityAsync(String activityId, CancellationToken cancellationToken)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(activityId))
		{
			throw new NullPointerException("activityId");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var cr = getActivity().GetConversationReference();
		cr.ActivityId = activityId;

//C# TO JAVA CONVERTER TODO TASK: Local functions are not converted by C# to Java Converter:
//		async Task ActuallyDeleteStuff()
//			{
//				await Adapter.DeleteActivityAsync(this, cr, cancellationToken).ConfigureAwait(false);
//			}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await DeleteActivityInternalAsync(cr, _onDeleteActivity, ActuallyDeleteStuff, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Deletes an existing activity.
	 
	 @param conversationReference The conversation containing the activity to delete.
	 @param cancellationToken Cancellation token.
	 @return A task that represents the work queued to execute.
	 @exception Microsoft.Bot.Schema.ErrorResponseException
	 The HTTP operation failed and the response contained additional information.
	 The conversation reference's <see cref="ConversationReference.ActivityId"/>
	 indicates the activity in the conversation to delete.
	*/

	public final Task DeleteActivityAsync(ConversationReference conversationReference)
	{
		return DeleteActivityAsync(conversationReference, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task DeleteActivityAsync(ConversationReference conversationReference, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task DeleteActivityAsync(ConversationReference conversationReference, CancellationToken cancellationToken)
	{
		if (conversationReference == null)
		{
			throw new NullPointerException("conversationReference");
		}

//C# TO JAVA CONVERTER TODO TASK: Local functions are not converted by C# to Java Converter:
//		async Task ActuallyDeleteStuff()
//			{
//				await Adapter.DeleteActivityAsync(this, conversationReference, cancellationToken).ConfigureAwait(false);
//			}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await DeleteActivityInternalAsync(conversationReference, _onDeleteActivity, ActuallyDeleteStuff, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Frees resources.
	*/
	public final void close() throws IOException
	{
		getTurnState().close();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async Task<ResourceResponse> UpdateActivityInternalAsync(Activity activity, IEnumerable<UpdateActivityHandler> updateHandlers, Func<Task<ResourceResponse>> callAtBottom, CancellationToken cancellationToken)
	private Task<ResourceResponse> UpdateActivityInternalAsync(Activity activity, java.lang.Iterable<UpdateActivityHandler> updateHandlers, tangible.Func0Param<Task<ResourceResponse>> callAtBottom, CancellationToken cancellationToken)
	{
		BotAssert.ActivityNotNull(activity);
		if (updateHandlers == null)
		{
			throw new IllegalArgumentException("updateHandlers");
		}

		// No middleware to run.
		if (updateHandlers.size() == 0)
		{
			if (callAtBottom != null)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				return await callAtBottom.invoke().ConfigureAwait(false);
			}

			return null;
		}

		// Default to "No more Middleware after this".
//C# TO JAVA CONVERTER TODO TASK: Local functions are not converted by C# to Java Converter:
//		async Task<ResourceResponse> Next()
//			{
//				// Remove the first item from the list of middleware to call,
//				// so that the next call just has the remaining items to worry about.
//				IEnumerable<UpdateActivityHandler> remaining = updateHandlers.Skip(1);
//				var result = await UpdateActivityInternalAsync(activity, remaining, callAtBottom, cancellationToken).ConfigureAwait(false);
//				activity.Id = result.Id;
//				return result;
//			}

		// Grab the current middleware, which is the 1st element in the array, and execute it
		UpdateActivityHandler toCall = updateHandlers.First();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await toCall.invoke(this, activity, Next).ConfigureAwait(false);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async Task DeleteActivityInternalAsync(ConversationReference cr, IEnumerable<DeleteActivityHandler> updateHandlers, Func<Task> callAtBottom, CancellationToken cancellationToken)
	private Task DeleteActivityInternalAsync(ConversationReference cr, java.lang.Iterable<DeleteActivityHandler> updateHandlers, tangible.Func0Param<Task> callAtBottom, CancellationToken cancellationToken)
	{
		BotAssert.ConversationReferenceNotNull(cr);

		if (updateHandlers == null)
		{
			throw new IllegalArgumentException("updateHandlers");
		}

		// No middleware to run.
		if (updateHandlers.size() == 0)
		{
			if (callAtBottom != null)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await callAtBottom.invoke().ConfigureAwait(false);
			}

			return;
		}

		// Default to "No more Middleware after this".
//C# TO JAVA CONVERTER TODO TASK: Local functions are not converted by C# to Java Converter:
//		async Task Next()
//			{
//				// Remove the first item from the list of middleware to call,
//				// so that the next call just has the remaining items to worry about.
//				IEnumerable<DeleteActivityHandler> remaining = updateHandlers.Skip(1);
//				await DeleteActivityInternalAsync(cr, remaining, callAtBottom, cancellationToken).ConfigureAwait(false);
//			}

		// Grab the current middleware, which is the 1st element in the array, and execute it.
		DeleteActivityHandler toCall = updateHandlers.First();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await toCall.invoke(this, cr, Next).ConfigureAwait(false);
	}
}