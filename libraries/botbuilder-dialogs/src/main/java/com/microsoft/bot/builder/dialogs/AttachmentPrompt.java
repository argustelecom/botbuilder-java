// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.microsoft.bot.schema.models.Attachment;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class AttachmentPrompt extends Prompt<List<Attachment>>
{

	public AttachmentPrompt(String dialogId)
	{
		this(dialogId, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public AttachmentPrompt(string dialogId, PromptValidator<IList<Attachment>> validator = null)
	public AttachmentPrompt(String dialogId, PromptValidator<List<Attachment>> validator)
	{
		super(dialogId, validator);
	}


	@Override
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options, boolean isRetry)
	{
		return OnPromptAsync(turnContext, state, options, isRetry, null);
	}

	@Override
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry)
	{
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
			turnContext.SendActivityAsync(options.getRetryPrompt()).get();
		}
		else if (options.getPrompt() != null)
		{
			turnContext.SendActivityAsync(options.getPrompt()).get();
		}
	}


	@Override
	protected CompletableFuture<PromptRecognizerResult<java.util.List<Attachment>>> OnRecognizeAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

	@Override
	protected CompletableFuture<PromptRecognizerResult<List<Attachment>>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		PromptRecognizerResult<List<Attachment>> result = new PromptRecognizerResult<List<Attachment>>();
		if (turnContext.activity().type() == ActivityTypes.MESSAGE.toString())
		{
			Activity message = turnContext.activity().AsMessageActivity();
			if (message.attachments() != null && message.attachments().size() > 0)
			{
				result.setSucceeded(true);
				result.setValue(message.attachments());
			}
		}

		return CompletableFuture.completedFuture(result);
	}
}