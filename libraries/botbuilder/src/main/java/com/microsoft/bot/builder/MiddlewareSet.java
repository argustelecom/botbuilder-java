// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import java.util.*;
import java.util.concurrent.CompletableFuture;



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
	public final void OnTurnAsync(TurnContext turnContext, NextDelegate next)
	{
		ReceiveActivityInternalAsync(turnContext, null, 0).join();
		next.invoke().join();
	}

	/** 
	 Processes an activity.
	 
	 @param turnContext The context object for the turn.
	 @param callback The delegate to call when the set finishes processing the activity.

	 @return A task that represents the work queued to execute.
	*/
	public final CompletableFuture ReceiveActivityWithStatusAsync(TurnContext turnContext, BotCallbackHandler callback)
	{
		return ReceiveActivityInternalAsync(turnContext, callback, 0);
	}

	private CompletableFuture ReceiveActivityInternalAsync(TurnContext turnContext, BotCallbackHandler callback, int nextMiddlewareIndex)
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
			return callback == null ? null : (callback.invoke(turnContext) != null) ? callback.invoke(turnContext) : CompletableFuture.completedFuture(null);
		}

		// Get the next piece of middleware
		Middleware nextMiddleware = _middleware.get(nextMiddlewareIndex);

		// Execute the next middleware passing a closure that will recurse back into this method at the next piece of middlware as the NextDelegate
		return nextMiddleware.OnTurnAsync(turnContext, () ->
				{
					ReceiveActivityInternalAsync(turnContext, callback, nextMiddlewareIndex + 1).join();
				});

	}
}