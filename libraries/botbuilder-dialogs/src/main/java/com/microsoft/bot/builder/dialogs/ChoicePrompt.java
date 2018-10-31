// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.dialogs.choices.*;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import org.apache.commons.lang3.StringUtils;



public class ChoicePrompt extends Prompt<FoundChoice>
{
    public static String English = "en-us";
    public static String EnglishOthers = "en-*";
    public static String Chinese = "zh-cn";
    public static String Spanish = "es-es";
    public static String Portuguese = "pt-br";
    public static String French = "fr-fr";
    public static String German = "de-de";
    public static String Italian = "it-it";
    public static String Japanese = "ja-jp";
    public static String Dutch = "nl-nl";
    public static String Korean = "ko-kr";

    private static final HashMap<String, ChoiceFactoryOptions> DefaultChoiceOptions = new HashMap<String, ChoiceFactoryOptions>() {{
        put(Spanish, new ChoiceFactoryOptions().withInlineSeparator(", ").withInlineOr(" o ").withInlineOrMore(", o ").withIncludeNumbers(Optional.of(true)));
        put(Dutch, new ChoiceFactoryOptions().withInlineSeparator(", ").withInlineOr(" of ").withInlineOrMore(", of ").withIncludeNumbers(Optional.of(true)));
        put(English, new ChoiceFactoryOptions().withInlineSeparator(", ").withInlineOr(" or ").withInlineOrMore(", or ").withIncludeNumbers(Optional.of(true)));
        put(French, new ChoiceFactoryOptions().withInlineSeparator(", ").withInlineOr(" ou ").withInlineOrMore(", ou ").withIncludeNumbers(Optional.of(true)));
        put(German, new ChoiceFactoryOptions().withInlineSeparator(", ").withInlineOr(" oder ").withInlineOrMore(", oder ").withIncludeNumbers(Optional.of(true)));
        put(Japanese, new ChoiceFactoryOptions().withInlineSeparator(", ").withInlineOr(" または ").withInlineOrMore("、 または ").withIncludeNumbers(Optional.of(true)));
        put(Portuguese, new ChoiceFactoryOptions().withInlineSeparator(", ").withInlineOr(" ou ").withInlineOrMore(", ou ").withIncludeNumbers(Optional.of(true)));
        put(Chinese, new ChoiceFactoryOptions().withInlineSeparator("， ").withInlineOr(" 要么 ").withInlineOrMore("， 要么 ").withIncludeNumbers(Optional.of(true)));
    }};


	public ChoicePrompt(String dialogId, PromptValidator<FoundChoice> validator)
	{
		this(dialogId, validator, null);
	}

	public ChoicePrompt(String dialogId)
	{
		this(dialogId, null, null);
	}

	public ChoicePrompt(String dialogId, PromptValidator<FoundChoice> validator, String defaultLocale)
	{
		super(dialogId, validator);
		setStyle(ListStyle.Auto);
		setDefaultLocale(defaultLocale);
	}

	private ListStyle Style = ListStyle.values()[0];
	public final ListStyle getStyle()
	{
		return Style;
	}
	public final void setStyle(ListStyle value)
	{
		Style = value;
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

	private ChoiceFactoryOptions ChoiceOptions;
	public final ChoiceFactoryOptions getChoiceOptions()
	{
		return ChoiceOptions;
	}
	public final void setChoiceOptions(ChoiceFactoryOptions value)
	{
		ChoiceOptions = value;
	}

	private FindChoicesOptions RecognizerOptions;
	public final FindChoicesOptions getRecognizerOptions()
	{
		return RecognizerOptions;
	}
	public final void setRecognizerOptions(FindChoicesOptions value)
	{
		RecognizerOptions = value;
	}


	@Override
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options, boolean isRetry)
	{
		return OnPromptAsync(turnContext, state, options, isRetry, null);
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

		// Determine culture
		String culture = (turnContext.activity().locale() != null) ? turnContext.activity().locale() : getDefaultLocale();
		if (StringUtils.isBlank(culture) || !DefaultChoiceOptions.containsKey(culture))
		{
			culture = English;
		}

		// Format prompt to send
		IMessageActivity prompt;
		java.util.List<Choice> tempVar = options.getChoices();
		ArrayList<Choice> choices = (tempVar != null) ? tempVar : new ArrayList<Choice>();
		String channelId = turnContext.activity().channelId();
		ChoiceFactoryOptions tempVar2 = getChoiceOptions();
		ChoiceFactoryOptions choiceOptions = (tempVar2 != null) ? tempVar2 : DefaultChoiceOptions.get(culture);
		if (isRetry && options.getRetryPrompt() != null)
		{
			prompt = AppendChoices(options.getRetryPrompt(), channelId, choices, getStyle(), choiceOptions);
		}
		else
		{
			prompt = AppendChoices(options.getPrompt(), channelId, choices, getStyle(), choiceOptions);
		}

		// Send prompt

		turnContext.SendActivityAsync(prompt).get();
	}


	@Override
	protected CompletableFuture<PromptRecognizerResult<FoundChoice>> OnRecognizeAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

	@Override
	protected CompletableFuture<PromptRecognizerResult<FoundChoice>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		java.util.List<Choice> tempVar = options.getChoices();
		ArrayList<Choice> choices = (tempVar != null) ? tempVar : new ArrayList<Choice>();

		PromptRecognizerResult<FoundChoice> result = new PromptRecognizerResult<FoundChoice>();
		if (turnContext.activity().type() == ActivityTypes.MESSAGE.toString())
		{
			Activity activity = turnContext.activity();
			String utterance = ((Activity) activity).text();
			FindChoicesOptions tempVar2 = getRecognizerOptions();
			FindChoicesOptions opt = (tempVar2 != null) ? tempVar2 : new FindChoicesOptions();
			String tempVar3 = opt.getLocale();
			String tempVar4 = getDefaultLocale();
			opt.setLocale((activity.locale() != null) ? activity.locale() : (tempVar3 != null) ? tempVar3 : (tempVar4 != null) ? tempVar4 : English);
			ArrayList<ModelResult<FoundChoice>> results = ChoiceRecognizers.RecognizeChoices(utterance, choices, opt);
			if (results != null && !results.isEmpty())
			{
				result.setSucceeded(true);
				result.setValue(results.get(0).getResolution());
			}
		}

		return CompletableFuture.completedFuture(result);
	}
}