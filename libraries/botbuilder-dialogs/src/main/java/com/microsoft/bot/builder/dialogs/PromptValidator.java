// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface PromptValidator<T>
{
    /**
     The delegate definition for custom prompt validators. Implement this function to add custom validation to a prompt.

     <typeparam name="T"></typeparam>
     @param promptContext The prompt validation context.
     @return A <see cref="Task"/> of bool representing the asynchronous operation indicating validation success or failure.
     */
	CompletableFuture<Boolean> invoke(PromptValidatorContext promptContext);
}