package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


import com.microsoft.bot.schema.models.ConversationReference;
import com.microsoft.bot.schema.models.ConversationReferenceHelper;

/**
 Represents a bot adapter that can connect a bot to a service endpoint.
 This class is abstract.
 
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
public abstract class BotAdapter
{
	/** 
	 Initializes a new instance of the <see cref="BotAdapter"/> class.
	*/
	public BotAdapter()
	{
		super();
	}

	/** 
	 Gets or sets an error handler that can catche exceptions in the middleware or application.
	 
	 <value>An error handler that can catch exceptions in the middleware or application.</value>
	*/
	private tangible.Func2Param<ITurnContext, RuntimeException, Task> OnTurnError;
	public final tangible.Func2Param<ITurnContext, RuntimeException, Task> getOnTurnError()
	{
		return OnTurnError;
	}
	public final void setOnTurnError(tangible.Func2Param<ITurnContext, RuntimeException, Task> value)
	{
		OnTurnError = (TurnContext arg1, RuntimeException arg2) -> value.invoke(arg1, arg2);
	}

	/** 
	 Gets the collection of middleware in the adapter's pipeline.
	 
	 <value>The middleware collection for the pipeline.</value>
	*/
	private MiddlewareSet MiddlewareSet = new MiddlewareSet();
	protected final MiddlewareSet getMiddlewareSet()
	{
		return MiddlewareSet;
	}

	/** 
	 Adds middleware to the adapter's pipeline.
	 
	 @param middleware The middleware to add.
	 @return The updated adapter object.
	 Middleware is added to the adapter at initialization time.
	 For each turn, the adapter calls middleware in the order in which you added it.
	 
	*/
	public final BotAdapter Use(Middleware middleware)
	{
		getMiddlewareSet().Use(middleware);
		return this;
	}

	/** 
	 When overridden in a derived class, sends activities to the conversation.
	 
	 @param turnContext The context object for the turn.
	 @param activities The activities to send.

	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 an array of <see cref="ResourceResponse"/> objects containing the IDs that
	 the receiving channel assigned to the activities.
	 {@link ITurnContext.OnSendActivities(SendActivitiesHandler)}
	*/
	public abstract CompletableFuture<ResourceResponse[]> SendActivitiesAsync(TurnContext turnContext, Activity[] activities);

	/** 
	 When overridden in a derived class, replaces an existing activity in the
	 conversation.
	 
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
	public abstract CompletableFuture<ResourceResponse> UpdateActivityAsync(TurnContext turnContext, Activity activity);

	/** 
	 When overridden in a derived class, deletes an existing activity in the
	 conversation.
	 
	 @param turnContext The context object for the turn.
	 @param reference Conversation reference for the activity to delete.

	 @return A task that represents the work queued to execute.
	 The <see cref="ConversationReference.ActivityId"/> of the conversation
	 reference identifies the activity to delete.
	 {@link ITurnContext.OnDeleteActivity(DeleteActivityHandler)}
	*/
	public abstract void DeleteActivity(TurnContext turnContext, ConversationReference reference);

	/** 
	 Sends a proactive message to a conversation.
	 
	 @param botId The application ID of the bot. This paramter is ignored in
	 single tenant the Adpters (Console, Test, etc) but is critical to the BotFrameworkAdapter
	 which is multi-tenant aware. 
	 @param reference A reference to the conversation to continue.
	 @param callback The method to call for the resulting bot turn.

	 @return A task that represents the work queued to execute.
	 Call this method to proactively send a message to a conversation.
	 Most _channels require a user to initiate a conversation with a bot
	 before the bot can send activities to the user.
	 {@link RunPipelineAsync(ITurnContext, BotCallbackHandler )}
	*/
	public void ContinueConversation(String botId, ConversationReference reference, BotCallbackHandler callback)
	{
		try (TurnContext context = new TurnContext(this, ConversationReferenceHelper.GetContinuationActivity(reference)))
		{
			return RunPipeline(context, callback);
		}
	}

	/** 
	 Starts activity processing for the current bot turn.
	 
	 @param turnContext The turn's context object.
	 @param callback A callback method to run at the end of the pipeline.

	 @return A task that represents the work queued to execute.
	 @exception ArgumentNullException
	 <paramref name="turnContext"/> is null.
	 The adapter calls middleware in the order in which you added it.
	 The adapter passes in the context object for the turn and a next delegate,
	 and the middleware calls the delegate to pass control to the next middleware
	 in the pipeline. Once control reaches the end of the pipeline, the adapter calls
	 the <paramref name="callback"/> method. If a middleware component doesn’t call
	 the next delegate, the adapter does not call  any of the subsequent middleware’s
	 <see cref="Middleware.OnTurnAsync(ITurnContext, NextDelegate, CancellationToken)"/>
	 methods or the callback method, and the pipeline short circuits.
	 <p>When the turn is initiated by a user activity (reactive messaging), the
	 callback method will be a reference to the bot's
	 <see cref="Bot.OnTurnAsync(ITurnContext, CancellationToken)"/> method. When the turn is
	 initiated by a call to <see cref="ContinueConversationAsync(string, ConversationReference, BotCallbackHandler, CancellationToken)"/>
	 (proactive messaging), the callback method is the callback method that was provided in the call.</p>
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: protected async void RunPipelineAsync(TurnContext turnContext, BotCallbackHandler callback)
	protected final void RunPipeline(TurnContext turnContext, BotCallbackHandler callback)
	{
		BotAssert.ContextNotNull(turnContext);

		// Call any registered Middleware Components looking for ReceiveActivityAsync()
		if (turnContext.getActivity() != null)
		{
			try
			{
				getMiddlewareSet().ReceiveActivityWithStatusAsync(turnContext, callback);
			}
			catch (RuntimeException e)
			{
				if (getOnTurnError() != null)
				{
					getOnTurnError().Invoke(turnContext, e);
				}
				else
				{
					throw e;
				}
			}
		}
		else
		{
			// call back to caller on proactive case
			if (callback != null)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await callback.invoke(turnContext, cancellationToken);
			}
		}
	}
}