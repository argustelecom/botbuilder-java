package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Interface for Recognizers.
*/
public interface IRecognizer
{
	/** 
	 Runs an utterance through a recognizer and returns a generic recognizer result.
	 
	 @param turnContext Turn context.
	 @param cancellationToken Cancellation token.
	 @return Analysis of utterance.
	*/
	Task<RecognizerResult> RecognizeAsync(ITurnContext turnContext, CancellationToken cancellationToken);

	/** 
	 Runs an utterance through a recognizer and returns a strongly-typed recognizer result.
	 
	 <typeparam name="T">The recognition result type.</typeparam>
	 @param turnContext Turn context.
	 @param cancellationToken Cancellation token.
	 @return Analysis of utterance.
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'new()' constraint has no equivalent in Java:
//ORIGINAL LINE: Task<T> RecognizeAsync<T>(ITurnContext turnContext, CancellationToken cancellationToken) where T : IRecognizerConvert, new();
	<T extends IRecognizerConvert> Task<T> RecognizeAsync(ITurnContext turnContext, CancellationToken cancellationToken);
}