package com.microsoft.bot.builder.dialogs;

import java.util.*;
import java.math.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


//C# TO JAVA CONVERTER TODO TASK: The C# 'struct' constraint has no equivalent in Java:
//ORIGINAL LINE: public class NumberPrompt<T> : Prompt<T> where T : struct
public class NumberPrompt<T> extends Prompt<T>
{

	public NumberPrompt(String dialogId, PromptValidator<T> validator)
	{
		this(dialogId, validator, null);
	}

	public NumberPrompt(String dialogId)
	{
		this(dialogId, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public NumberPrompt(string dialogId, PromptValidator<T> validator = null, string defaultLocale = null)
	public NumberPrompt(String dialogId, PromptValidator<T> validator, String defaultLocale)
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await turnContext.SendActivityAsync(options.getRetryPrompt()).get();
		}
		else if (options.getPrompt() != null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await turnContext.SendActivityAsync(options.getPrompt()).get();
		}
	}


	@Override
	protected CompletableFuture<PromptRecognizerResult<T>> OnRecognizeAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected override CompletableFuture<PromptRecognizerResult<T>> OnRecognizeAsync(TurnContext turnContext, IDictionary<string, object> state, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken))
	@Override
	protected CompletableFuture<PromptRecognizerResult<T>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		PromptRecognizerResult<T> result = new PromptRecognizerResult<T>();
		if (turnContext.Activity.Type == ActivityTypes.Message)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var message = turnContext.Activity.AsMessageActivity();
			String tempVar = getDefaultLocale();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var culture = (turnContext.Activity.Locale != null) ? turnContext.Activity.Locale : (tempVar != null) ? tempVar : English;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var results = NumberRecognizer.RecognizeNumber(message.Text, culture);
			if (results.size() > 0)
			{
				// Try to parse value based on type
				String text = results[0].Resolution["value"].toString();
				if (T.class == Float.class)
				{
					float value;
					tangible.OutObject<Float> tempOut_value = new tangible.OutObject<Float>();
					if (tangible.TryParseHelper.tryParseFloat(text, tempOut_value))
					{
					value = tempOut_value.argValue;
						result.setSucceeded(true);
						result.setValue((T)(Object)value);
					}
				else
				{
					value = tempOut_value.argValue;
				}
				}
				else if (T.class == Integer.class)
				{
					int value;
					tangible.OutObject<Integer> tempOut_value2 = new tangible.OutObject<Integer>();
					if (tangible.TryParseHelper.tryParseInt(text, tempOut_value2))
					{
					value = tempOut_value2.argValue;
						result.setSucceeded(true);
						result.setValue((T)(Object)value);
					}
				else
				{
					value = tempOut_value2.argValue;
				}
				}
				else if (T.class == Long.class)
				{
					long value;
					tangible.OutObject<Long> tempOut_value3 = new tangible.OutObject<Long>();
					if (tangible.TryParseHelper.tryParseLong(text, tempOut_value3))
					{
					value = tempOut_value3.argValue;
						result.setSucceeded(true);
						result.setValue((T)(Object)value);
					}
				else
				{
					value = tempOut_value3.argValue;
				}
				}
				else if (T.class == Double.class)
				{
					double value;
					tangible.OutObject<Double> tempOut_value4 = new tangible.OutObject<Double>();
					if (tangible.TryParseHelper.tryParseDouble(text, tempOut_value4))
					{
					value = tempOut_value4.argValue;
						result.setSucceeded(true);
						result.setValue((T)(Object)value);
					}
				else
				{
					value = tempOut_value4.argValue;
				}
				}
				else if (T.class == BigDecimal.class)
				{
					java.math.BigDecimal value;
					tangible.OutObject<BigDecimal> tempOut_value5 = new tangible.OutObject<BigDecimal>();
					if (BigDecimal.TryParse(text, tempOut_value5))
					{
					value = tempOut_value5.argValue;
						result.setSucceeded(true);
						result.setValue((T)(Object)value);
					}
				else
				{
					value = tempOut_value5.argValue;
				}
				}
				else
				{
					throw new UnsupportedOperationException(String.format("NumberPrompt: type argument T of type 'typeof(T)' is not supported"));
				}
			}
		}

		return CompletableFuture.completedFuture(result);
	}
}