// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.microsoft.bot.schema.models.Attachment;



public class AttachmentPrompt extends Prompt<List<Attachment>>
{

	public AttachmentPrompt(String dialogId)
	{
		this(dialogId, null);
	}

    @Override
    public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc) {
        return BeginDialogPromptAsync(dc);
    }

    public AttachmentPrompt(String dialogId, PromptValidator<List<Attachment>> validator)
	{
		super(dialogId, validator);
	}


	@Override
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry)
	{
	    return CompletableFuture.runAsync(() -> {
            if (turnContext == null)
            {
                throw new NullPointerException("turnContext");
            }

            if (options == null)
            {
                throw new NullPointerException("options");
            }

            if (isRetry && options.getRetryPrompt() != null)
            {
                try {
                    turnContext.SendActivityAsync(options.getRetryPrompt()).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
            }
            else if (options.getPrompt() != null)
            {
                try {
                    turnContext.SendActivityAsync(options.getPrompt()).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
            }

        });
	}

	@Override
	protected CompletableFuture<PromptRecognizerResult<List<Attachment>>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		PromptRecognizerResult<List<Attachment>> result = new PromptRecognizerResult<List<Attachment>>();
		if (turnContext.activity().type() == ActivityTypes.MESSAGE)
		{
			Activity message = turnContext.activity();
			if (message.attachments() != null && message.attachments().size() > 0)
			{
				result.withSucceeded(true);
				result.withValue(message.attachments());
			}
		}

		return CompletableFuture.completedFuture(result);
	}
}