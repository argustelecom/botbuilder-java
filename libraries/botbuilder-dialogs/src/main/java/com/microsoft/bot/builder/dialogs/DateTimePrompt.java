package com.microsoft.bot.builder.dialogs;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


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

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public DateTimePrompt(string dialogId, PromptValidator<IList<DateTimeResolution>> validator = null, string defaultLocale = null)
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
	protected Task OnPromptAsync(ITurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options, boolean isRetry)
	{
		return OnPromptAsync(turnContext, state, options, isRetry, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: protected override async Task OnPromptAsync(ITurnContext turnContext, IDictionary<string, object> state, PromptOptions options, bool isRetry, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	protected Task OnPromptAsync(ITurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry, CancellationToken cancellationToken)
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await turnContext.SendActivityAsync(options.getRetryPrompt(), cancellationToken).ConfigureAwait(false);
		}
		else if (options.getPrompt() != null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await turnContext.SendActivityAsync(options.getPrompt(), cancellationToken).ConfigureAwait(false);
		}
	}


	@Override
	protected CompletableFuture<PromptRecognizerResult<java.util.List<DateTimeResolution>>> OnRecognizeAsync(ITurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected override CompletableFuture<PromptRecognizerResult<IList<DateTimeResolution>>> OnRecognizeAsync(ITurnContext turnContext, IDictionary<string, object> state, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken))
	@Override
	protected CompletableFuture<PromptRecognizerResult<List<DateTimeResolution>>> OnRecognizeAsync(ITurnContext turnContext, Map<String, Object> state, PromptOptions options, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		PromptRecognizerResult<List<DateTimeResolution>> result = new PromptRecognizerResult<List<DateTimeResolution>>();
		if (turnContext.Activity.Type == ActivityTypes.Message)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var message = turnContext.Activity.AsMessageActivity();
			String tempVar = getDefaultLocale();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var culture = (turnContext.Activity.Locale != null) ? turnContext.Activity.Locale : (tempVar != null) ? tempVar : English;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var results = DateTimeRecognizer.RecognizeDateTime(message.Text, culture);
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

		return Task.FromResult(result);
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