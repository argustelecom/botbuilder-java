// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface WaterfallStep
{
    /**
     A delegate definition of a Waterfall step. This is implemented by application code.

     @param stepContext The WaterfallStepContext for this waterfall dialog.
     @return A <see cref="Task"/> of <see cref="DialogTurnResult"/> representing the asynchronous operation.
     */
	CompletableFuture<DialogTurnResult> invoke(WaterfallStepContext stepContext );
}