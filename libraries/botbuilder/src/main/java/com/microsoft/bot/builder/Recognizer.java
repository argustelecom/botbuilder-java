// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import java.util.concurrent.CompletableFuture;

/**
 Interface for Recognizers.
*/
public interface Recognizer
{
	/** 
	 Runs an utterance through a recognizer and returns a generic recognizer result.
	 
	 @param turnContext Turn context.
	 @return Analysis of utterance.
	*/
	CompletableFuture<RecognizerResult> RecognizeAsync(TurnContext turnContext);

	/** 
	 Runs an utterance through a recognizer and returns a strongly-typed recognizer result.
	 
	 <typeparam name="T">The recognition result type.</typeparam>
	 @param turnContext Turn context.
	 @return Analysis of utterance.
	*/
	//ORIGINAL LINE: CompletableFuture<T> RecognizeAsync<T>(TurnContext turnContext) where T : RecognizerConvert, new();
	<T extends RecognizerConvert> CompletableFuture<T> RecognizeWithTypeAsync(TurnContext turnContext);
}