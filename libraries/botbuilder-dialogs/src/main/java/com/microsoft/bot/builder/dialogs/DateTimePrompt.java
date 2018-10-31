// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class DateTimePrompt extends Prompt<List<DateTimeResolution>>
{

	public DateTimePrompt(String dialogId, PromptValidator<java.util.List<DateTimeResolution>> validator)
	{
		this(dialogId, validator, null);
	}

	public DateTimePrompt(String dialogId)
	{
		this(dialogId, null, null);
	}

	public DateTimePrompt(String dialogId, PromptValidator<List<DateTimeResolution>> validator, String defaultLocale)
	{
		super(dialogId, validator);
		setDefaultLocale(defaultLocale);
	}

	private String DefaultLocale;
	public final String getDefaultLocale()
	{
		return DefaultLocale;
	}
	public final void setDefaultLocale(String value)
	{
		DefaultLocale = value;
	}


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
	protected CompletableFuture<PromptRecognizerResult<List<DateTimeResolution>>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		PromptRecognizerResult<List<DateTimeResolution>> result = new PromptRecognizerResult<List<DateTimeResolution>>();
		if (turnContext.activity().type() == ActivityTypes.MESSAGE)
		{
			Activity message = turnContext.activity();
			String tempVar = getDefaultLocale();
			String culture = (turnContext.activity().locale() != null) ? turnContext.activity().locale() : (tempVar != null) ? tempVar : English;
			var results = DateTimeRecognizer.RecognizeDateTime(message.text(), culture);
			if (results.size() > 0)
			{
				// Return list of resolutions from first match
				result.setSucceeded(true);
				result.setValue(new ArrayList<DateTimeResolution>());
				ArrayList<HashMap<String, String>> values = (ArrayList<HashMap<String, String>>)results[0].Resolution["values"];
				for (HashMap<String, String> value : values)
				{
					result.getValue().add(ReadResolution(value));
				}
			}
		}

		return CompletableFuture.completedFuture(result);
	}

	private DateTimeResolution ReadResolution(Map<String, String> resolution)
	{
		DateTimeResolution result = new DateTimeResolution();

		TValue timex;
		if (resolution.containsKey("timex") ? (timex = resolution.get("timex")) == timex : false)
		{
			result.setTimex(timex);
		}

		TValue value;
		if (resolution.containsKey("value") ? (value = resolution.get("value")) == value : false)
		{
			result.setValue(value);
		}

		TValue start;
		if (resolution.containsKey("start") ? (start = resolution.get("start")) == start : false)
		{
			result.setStart(start);
		}

		TValue end;
		if (resolution.containsKey("end") ? (end = resolution.get("end")) == end : false)
		{
			result.setEnd(end);
		}

		return result;
	}
}