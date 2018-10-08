package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 When added, this middleware will send typing activities back to the user when a Message activity
 is receieved to let them know that the bot has receieved the message and is working on the response.
 You can specify a delay in milliseconds before the first typing activity is sent and then a frequency,
 also in milliseconds which determines how often another typing activity is sent. Typing activities
 will continue to be sent until your bot sends another message back to the user.
*/
public class ShowTypingMiddleware implements IMiddleware
{
	private TimeSpan _delay = new TimeSpan();
	private TimeSpan _period = new TimeSpan();

	/** 
	 Initializes a new instance of the <see cref="ShowTypingMiddleware"/> class.
	 
	 @param delay Initial delay before sending first typing indicator. Defaults to 500ms.
	 @param period Rate at which additional typing indicators will be sent. Defaults to every 2000ms.
	*/

	public ShowTypingMiddleware(int delay)
	{
		this(delay, 2000);
	}

	public ShowTypingMiddleware()
	{
		this(500, 2000);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public ShowTypingMiddleware(int delay = 500, int period = 2000)
	public ShowTypingMiddleware(int delay, int period)
	{
		if (delay < 0)
		{
			throw new IndexOutOfBoundsException("delay", "Delay must be greater than or equal to zero");
		}

		if (period <= 0)
		{
			throw new IndexOutOfBoundsException("period", "Repeat period must be greater than zero");
		}

		_delay = TimeSpan.FromMilliseconds(delay);
		_period = TimeSpan.FromMilliseconds(period);
	}

	/** 
	 Processess an incoming activity.
	 
	 @param turnContext The context object for this turn.
	 @param next The delegate to call to continue the bot middleware pipeline.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 Spawns a thread that sends the periodic typing activities until the turn ends.
	 
	 {@link ITurnContext}
	 {@link Bot.Schema.IActivity}
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task OnTurnAsync(ITurnContext turnContext, NextDelegate next, CancellationToken cancellationToken)
	public final Task OnTurnAsync(ITurnContext turnContext, NextDelegate next, CancellationToken cancellationToken)
	{
		CancellationTokenSource cts = null;
		try
		{
			// If the incoming activity is a MessageActivity, start a timer to periodically send the typing activity
			if (turnContext.getActivity().Type == ActivityTypes.Message)
			{
				cts = new CancellationTokenSource();
				cancellationToken.Register(() -> cts.Cancel());

				// do not await task - we want this to run in thw background and we wil cancel it when its done
				System.Threading.Tasks.Task task = Task.Run(() -> SendTypingAsync(turnContext, _delay, _period, cts.Token), cancellationToken);
			}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await next.invoke(cancellationToken).ConfigureAwait(false);
		}
		finally
		{
			if (cts != null)
			{
				cts.Cancel();
			}
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private static async Task SendTypingAsync(ITurnContext turnContext, TimeSpan delay, TimeSpan period, CancellationToken cancellationToken)
	private static Task SendTypingAsync(ITurnContext turnContext, TimeSpan delay, TimeSpan period, CancellationToken cancellationToken)
	{
		try
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await Task.Delay(delay, cancellationToken).ConfigureAwait(false);

			while (!cancellationToken.IsCancellationRequested)
			{
				if (!cancellationToken.IsCancellationRequested)
				{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
					await SendTypingActivityAsync(turnContext, cancellationToken).ConfigureAwait(false);
				}

				// if we happen to cancel when in the delay we will get a TaskCanceledException
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await Task.Delay(period, cancellationToken).ConfigureAwait(false);
			}
		}
		catch (TaskCanceledException e)
		{
			// do nothing
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private static async Task SendTypingActivityAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	private static Task SendTypingActivityAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	{
		// create a TypingActivity, associate it with the conversation and send immediately
		Activity typingActivity = new Activity();
		typingActivity.Type = ActivityTypes.Typing;
		typingActivity.RelatesTo = turnContext.getActivity().RelatesTo;

		// sending the Activity directly on the Adapter avoids other Middleware and avoids setting the Responded
		// flag, however, this also requires that the conversation reference details are explicitly added.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var conversationReference = turnContext.getActivity().GetConversationReference();
		typingActivity.ApplyConversationReference(conversationReference);

		// make sure to send the Activity directly on the Adapter rather than via the TurnContext
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await turnContext.getAdapter().SendActivitiesAsync(turnContext, new Activity[] {typingActivity}, cancellationToken).ConfigureAwait(false);
	}
}