package Microsoft.Bot.Builder;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Contains an ordered set of <see cref="IMiddleware"/>.
*/
public class MiddlewareSet implements IMiddleware
{
	private final List<IMiddleware> _middleware = new ArrayList<IMiddleware>();

	/** 
	 Adds a middleware object to the end of the set.
	 
	 @param middleware The middleware to add.
	 @return The updated middleware set.
	 <see cref="BotAdapter.Use(IMiddleware)"/>
	*/
	public final MiddlewareSet Use(IMiddleware middleware)
	{
		BotAssert.MiddlewareNotNull(middleware);
		_middleware.add(middleware);
		return this;
	}

	/** 
	 Processes an incoming activity.
	 
	 @param turnContext The context object for this turn.
	 @param next The delegate to call to continue the bot middleware pipeline.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task OnTurnAsync(ITurnContext turnContext, NextDelegate next, CancellationToken cancellationToken)
	public final Task OnTurnAsync(ITurnContext turnContext, NextDelegate next, CancellationToken cancellationToken)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await ReceiveActivityInternalAsync(turnContext, null, 0, cancellationToken).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await next.invoke(cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Processes an activity.
	 
	 @param turnContext The context object for the turn.
	 @param callback The delegate to call when the set finishes processing the activity.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task ReceiveActivityWithStatusAsync(ITurnContext turnContext, BotCallbackHandler callback, CancellationToken cancellationToken)
	public final Task ReceiveActivityWithStatusAsync(ITurnContext turnContext, BotCallbackHandler callback, CancellationToken cancellationToken)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await ReceiveActivityInternalAsync(turnContext, callback, 0, cancellationToken).ConfigureAwait(false);
	}

	private Task ReceiveActivityInternalAsync(ITurnContext turnContext, BotCallbackHandler callback, int nextMiddlewareIndex, CancellationToken cancellationToken)
	{
		// Check if we're at the end of the middleware list yet
		if (nextMiddlewareIndex == _middleware.size())
		{
			// If all the Middlware ran, the "leading edge" of the tree is now complete.
			// This means it's time to run any developer specified callback.
			// Once this callback is done, the "trailing edge" calls are then completed. This
			// allows code that looks like:
			//      Trace.TraceInformation("before");
			//      await next();
			//      Trace.TraceInformation("after");
			// to run as expected.

			// If a callback was provided invoke it now and return its task, otherwise just return the completed task
			return callback == null ? null : (callback.invoke(turnContext, cancellationToken) != null) ? callback.invoke(turnContext, cancellationToken) : Task.CompletedTask;
		}

		// Get the next piece of middleware
		IMiddleware nextMiddleware = _middleware.get(nextMiddlewareIndex);

		// Execute the next middleware passing a closure that will recurse back into this method at the next piece of middlware as the NextDelegate
		return nextMiddleware.OnTurnAsync(turnContext, (ct) -> ReceiveActivityInternalAsync(turnContext, callback, nextMiddlewareIndex + 1, ct), cancellationToken);
	}
}