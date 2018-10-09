package com.microsoft.bot.builder;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Contains an ordered set of <see cref="Middleware"/>.
*/
public class MiddlewareSet implements Middleware
{
	private final List<Middleware> _middleware = new ArrayList<Middleware>();

	/** 
	 Adds a middleware object to the end of the set.
	 
	 @param middleware The middleware to add.
	 @return The updated middleware set.
	 <see cref="BotAdapter.Use(Middleware)"/>
	*/
	public final MiddlewareSet Use(Middleware middleware)
	{
		BotAssert.MiddlewareNotNull(middleware);
		_middleware.add(middleware);
		return this;
	}

	/** 
	 Processes an incoming activity.
	 
	 @param turnContext The context object for this turn.
	 @param next The delegate to call to continue the bot middleware pipeline.

	 @return A task that represents the work queued to execute.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async void OnTurnAsync(TurnContext turnContext, NextDelegate next)
	public final void OnTurnAsync(TurnContext turnContext, NextDelegate next)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await ReceiveActivityInternalAsync(turnContext, null, 0, cancellationToken);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await next.invoke(cancellationToken);
	}

	/** 
	 Processes an activity.
	 
	 @param turnContext The context object for the turn.
	 @param callback The delegate to call when the set finishes processing the activity.

	 @return A task that represents the work queued to execute.
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async void ReceiveActivityWithStatusAsync(TurnContext turnContext, BotCallbackHandler callback)
	public final void ReceiveActivityWithStatusAsync(TurnContext turnContext, BotCallbackHandler callback)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await ReceiveActivityInternalAsync(turnContext, callback, 0, cancellationToken);
	}

	private void ReceiveActivityInternalAsync(TurnContext turnContext, BotCallbackHandler callback, int nextMiddlewareIndex)
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
		Middleware nextMiddleware = _middleware.get(nextMiddlewareIndex);

		// Execute the next middleware passing a closure that will recurse back into this method at the next piece of middlware as the NextDelegate
		return nextMiddleware.OnTurnAsync(turnContext, (ct) -> ReceiveActivityInternalAsync(turnContext, callback, nextMiddlewareIndex + 1, ct), cancellationToken);
	}
}