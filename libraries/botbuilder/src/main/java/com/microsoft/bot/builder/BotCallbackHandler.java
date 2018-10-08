package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 The callback delegate for application code.
 
 @param turnContext The turn context.
 @param cancellationToken The task cancellation token.
 @return A <see cref="Task"/> representing the asynchronous operation.
*/
@FunctionalInterface
public interface BotCallbackHandler
{
	Task invoke(ITurnContext turnContext, CancellationToken cancellationToken);
}