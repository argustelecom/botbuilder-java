// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;

import java.util.*;
import java.math.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


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

    @Override
    public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc) {
        return BeginDialogPromptAsync(dc);
    }

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
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry )
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

        }, turnContext.executorService());
	}

	@Override
	protected CompletableFuture<PromptRecognizerResult<T>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (turnContext == null)
            {
                throw new NullPointerException("turnContext");
            }

            PromptRecognizerResult<T> result = new PromptRecognizerResult<T>();
            if (turnContext.activity().type() == ActivityTypes.MESSAGE.toString())
            {
                Activity message = turnContext.activity();
                String tempVar = getDefaultLocale();
                String culture = (turnContext.activity().locale() != null) ? turnContext.activity().locale() : (tempVar != null) ? tempVar : English;
                var results = NumberRecognizer.RecognizeNumber(message.text(), culture);
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
                            result.withSucceeded(true);
                            result.withValue((T)(Object)value);
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
                            result.withSucceeded(true);
                            result.withValue((T)(Object)value);
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
                            result.withSucceeded(true);
                            result.withValue((T)(Object)value);
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
                            result.withSucceeded(true);
                            result.withValue((T)(Object)value);
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
                            result.withSucceeded(true);
                            result.withValue((T)(Object)value);
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

        });
	}
}