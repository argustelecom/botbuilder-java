// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import java.util.concurrent.CompletableFuture;

/**
 Represents a bot that can operate on incoming activities.
 
 A <see cref="BotAdapter"/> passes incoming activities from the user's
 channel to the bot's <see cref="OnTurnAsync(ITurnContext, CancellationToken)"/> method.
 {@link Middleware}
*/
public interface Bot
{
	/** 
	 When implemented in a bot, handles an incoming activity.
	 
	 @param turnContext The context object for this turn.
	 @return A task that represents the work queued to execute.
	 The <paramref name="turnContext"/> provides information about the
	 incoming activity, and other data needed to process the activity.
	 {@link TurnContext}
	 {@link com.microsoft.bot.Activity}
	*/

	CompletableFuture OnTurnAsync(TurnContext turnContext);
}