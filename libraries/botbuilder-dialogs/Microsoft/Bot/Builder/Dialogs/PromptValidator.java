package Microsoft.Bot.Builder.Dialogs;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 The delegate definition for custom prompt validators. Implement this function to add custom validation to a prompt.
 
 <typeparam name="T"></typeparam>
 @param promptContext The prompt validation context.
 @param cancellationToken The cancellation token.
 @return A <see cref="Task"/> of bool representing the asynchronous operation indicating validation success or failure.
*/
@FunctionalInterface
public interface PromptValidator<T>
{
	Task<bool> invoke(PromptValidatorContext promptContext, CancellationToken cancellationToken);
}