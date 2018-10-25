// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class TextPrompt extends Prompt<String>
{

	public TextPrompt(String dialogId)
	{
		this(dialogId, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public TextPrompt(string dialogId, PromptValidator<string> validator = null)
	public TextPrompt(String dialogId, PromptValidator<String> validator)
	{
		super(dialogId, validator);
	}


	@Override
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options, boolean isRetry)
	{
		return OnPromptAsync(turnContext, state, options, isRetry, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: protected override async CompletableFuture OnPromptAsync(TurnContext turnContext, IDictionary<string, object> state, PromptOptions options, bool isRetry, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry )
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
	protected CompletableFuture<PromptRecognizerResult<String>> OnRecognizeAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

	@Override
	protected CompletableFuture<PromptRecognizerResult<String>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		PromptRecognizerResult<String> result = new PromptRecognizerResult<String>();
		if (turnContext.activity().type() == ActivityTypes.MESSAGE.toString())
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			Activity message = turnContext.activity().AsMessageActivity();
			if (message.text() != null)
			{
				result.setSucceeded(true);
				result.setValue(message.text());
			}
		}

		return CompletableFuture.completedFuture(result);
	}
}