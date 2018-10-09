package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


import java.util.concurrent.CompletableFuture;

/**
 Interface for Recognizers.
*/
public interface Recognizer
{
	/** 
	 Runs an utterance through a recognizer and returns a generic recognizer result.
	 
	 @param turnContext Turn context.
	 @param cancellationToken Cancellation token.
	 @return Analysis of utterance.
	*/
	CompletableFuture<RecognizerResult> RecognizeAsync(TurnContext turnContext);

	/** 
	 Runs an utterance through a recognizer and returns a strongly-typed recognizer result.
	 
	 <typeparam name="T">The recognition result type.</typeparam>
	 @param turnContext Turn context.
	 @param cancellationToken Cancellation token.
	 @return Analysis of utterance.
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'new()' constraint has no equivalent in Java:
//ORIGINAL LINE: CompletableFuture<T> RecognizeAsync<T>(TurnContext turnContext) where T : RecognizerConvert, new();
	<T extends RecognizerConvert> CompletableFuture<T> RecognizeAsync(TurnContext turnContext);
}