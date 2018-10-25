// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;



public class ChoicePrompt extends Prompt<FoundChoice>
{
	private static final HashMap<String, ChoiceFactoryOptions> DefaultChoiceOptions = new HashMap<String, ChoiceFactoryOptions>(Map.ofEntries(Map.entry(Spanish, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " o ", InlineOrMore = ", o ", IncludeNumbers = true}), Map.entry(Dutch, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " of ", InlineOrMore = ", of ", IncludeNumbers = true}), Map.entry(English, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " or ", InlineOrMore = ", or ", IncludeNumbers = true}), Map.entry(French, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " ou ", InlineOrMore = ", ou ", IncludeNumbers = true}), Map.entry(German, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " oder ", InlineOrMore = ", oder ", IncludeNumbers = true}), Map.entry(Japanese, new ChoiceFactoryOptions {InlineSeparator = "、 ", InlineOr = " または ", InlineOrMore = "、 または ", IncludeNumbers = true}), Map.entry(Portuguese, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " ou ", InlineOrMore = ", ou ", IncludeNumbers = true}), Map.entry(Chinese, new ChoiceFactoryOptions {InlineSeparator = "， ", InlineOr = " 要么 ", InlineOrMore = "， 要么 ", IncludeNumbers = true})));


	public ChoicePrompt(String dialogId, PromptValidator<FoundChoice> validator)
	{
		this(dialogId, validator, null);
	}

	public ChoicePrompt(String dialogId)
	{
		this(dialogId, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public ChoicePrompt(string dialogId, PromptValidator<FoundChoice> validator = null, string defaultLocale = null)
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

		// Determine culture
		String culture = (turnContext.Activity.Locale != null) ? turnContext.Activity.Locale : getDefaultLocale();
		if (StringUtils.isBlank(culture) || !DefaultChoiceOptions.containsKey(culture))
		{
			culture = English;
		}

		// Format prompt to send
		IMessageActivity prompt;
		java.util.List<Choice> tempVar = options.getChoices();
		ArrayList<Choice> choices = (tempVar != null) ? tempVar : new ArrayList<Choice>();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var channelId = turnContext.Activity.ChannelId;
		Microsoft.Bot.Builder.Dialogs.Choices.ChoiceFactoryOptions tempVar2 = getChoiceOptions();
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await turnContext.SendActivityAsync(prompt).get();
	}


	@Override
	protected CompletableFuture<PromptRecognizerResult<FoundChoice>> OnRecognizeAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected override CompletableFuture<PromptRecognizerResult<FoundChoice>> OnRecognizeAsync(TurnContext turnContext, IDictionary<string, object> state, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken))
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
		if (turnContext.Activity.Type == ActivityTypes.Message)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var activity = turnContext.Activity;
			String utterance = activity.Text;
			Microsoft.Bot.Builder.Dialogs.Choices.FindChoicesOptions tempVar2 = getRecognizerOptions();
			FindChoicesOptions opt = (tempVar2 != null) ? tempVar2 : new FindChoicesOptions();
			String tempVar3 = opt.getLocale();
			String tempVar4 = getDefaultLocale();
			opt.setLocale((activity.Locale != null) ? activity.Locale : (tempVar3 != null) ? tempVar3 : (tempVar4 != null) ? tempVar4 : English);
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