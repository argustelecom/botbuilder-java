package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 The callback delegate for application code.
 
 @param turnContext The turn context.
  @return A <see cref="Task"/> representing the asynchronous operation.
*/
@FunctionalInterface
public interface BotCallbackHandler
{
	CompletableFuture invoke(TurnContext turnContext) throws Exception;
}