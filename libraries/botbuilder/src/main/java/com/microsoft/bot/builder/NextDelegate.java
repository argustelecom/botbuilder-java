package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Encapsulates an asynchronous method that calls the next
 <see cref="Middleware"/>.<see cref="Middleware.OnTurnAsync"/>
 or <see cref="Bot"/>.<see cref="Bot.OnTurnAsync"/> method in the middleware pipeline.
 
 @param cancellationToken A cancellation token that can be used by other objects
 or threads to receive notice of cancellation.
 @return A task that represents the work queued to execute.
*/
@FunctionalInterface
public interface NextDelegate
{
	Task invoke(CancellationToken cancellationToken);
}