package Microsoft.Bot.Builder.Dialogs;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 A delegate definition of a Waterfall step. This is implemented by application code.
 
 @param stepContext The WaterfallStepContext for this waterfall dialog.
 @param cancellationToken The cancellation token.
 @return A <see cref="Task"/> of <see cref="DialogTurnResult"/> representing the asynchronous operation.
*/
@FunctionalInterface
public interface WaterfallStep
{
	Task<DialogTurnResult> invoke(WaterfallStepContext stepContext, CancellationToken cancellationToken);
}