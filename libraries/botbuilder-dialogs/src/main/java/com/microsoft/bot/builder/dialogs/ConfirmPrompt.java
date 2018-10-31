// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.dialogs.choices.*;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import org.apache.commons.lang3.StringUtils;


import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 Prompts a user to confirm something with a yes/no response.

 By default the prompt will return to the calling dialog a `boolean` representing the users
 selection.
 When used with your bots 'DialogSet' you can simply add a new instance of the prompt as a named
 dialog using <code>DialogSet.Add()</code>. You can then start the prompt from a waterfall step using either
 <code>DialogContext.Begin()</code> or <code>DialogContext.Prompt()</code>. The user will be prompted to answer a
 'yes/no' or 'true/false' question and the users response will be passed as an argument to the
 callers next waterfall step
 
*/
public class ConfirmPrompt extends Prompt<Boolean>
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


    private static final HashMap<String, ChoiceTuple> DefaultConfirmChoices = new HashMap<String, ChoiceTuple>() {{
        put(Spanish, new ChoiceTuple("Sí", "No"));
        put(Dutch, new ChoiceTuple("Ja", "Nee"));
        put(English, new ChoiceTuple("Yes", "No"));
        put(French, new ChoiceTuple("Oui", "Non"));
        put(German, new ChoiceTuple("Ja", "Nein"));
        put(Japanese, new ChoiceTuple("はい", "いいえ"));
        put(Portuguese, new ChoiceTuple("Sim", "Não"));
        put(Chinese, new ChoiceTuple("是的", "不"));
    }};

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

	/** 
	 Initializes a new instance of the <see cref="ConfirmPrompt"/> class.
	 
	 @param dialogId Dialog identifier.
	 @param validator Validator that will be called each time the user responds to the prompt.
	 If the validator replies with a message no additional retry prompt will be sent.
	 @param defaultLocale The default culture or locale to use if the <see cref="Activity.Locale"/>
	 of the <see cref="DialogContext"/>.<see cref="DialogContext.Context"/>.<see cref="ITurnContext.Activity"/>
	 is not specified.
	*/

	public ConfirmPrompt(String dialogId, PromptValidator<Boolean> validator)
	{
		this(dialogId, validator, null);
	}

	public ConfirmPrompt(String dialogId)
	{
		this(dialogId, null, null);
	}

	public ConfirmPrompt(String dialogId, PromptValidator<Boolean> validator, String defaultLocale)
	{
		super(dialogId, validator);
		setStyle(ListStyle.Auto);
		setDefaultLocale(defaultLocale);
	}

	/** 
	 Gets or sets the style of the yes/no choices rendered to the user when prompting.
	 {@link Choices.ListStyle}
	 
	 <value>
	 The style of the yes/no choices rendered to the user when prompting.
	 </value>
	*/
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

	/** 
	 Gets or sets additional options passed to the {@link ChoiceFactory}
	 and used to tweak the style of choices rendered to the user.
	 
	 <value>
	 Additional options passed to the {@link ChoiceFactory}
	 and used to tweak the style of choices rendered to the user.
	 </value>
	*/
	private ChoiceFactoryOptions ChoiceOptions;
	public final ChoiceFactoryOptions getChoiceOptions()
	{
		return ChoiceOptions;
	}
	public final void setChoiceOptions(ChoiceFactoryOptions value)
	{
		ChoiceOptions = value;
	}

	private ChoiceTuple ConfirmChoices;
	public final ChoiceTuple getConfirmChoices()
	{
		return ConfirmChoices;
	}
	public final void setConfirmChoices(ChoiceTuple value)
	{
		ConfirmChoices = value;
	}


	@Override
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry )
	{
		return CompletableFuture.supplyAsync(() -> {
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
            Activity prompt;
            String channelId = turnContext.activity().channelId();
            ChoiceFactoryOptions tempVar = getChoiceOptions();
            ChoiceFactoryOptions choiceOptions = (tempVar != null) ? tempVar : DefaultChoiceOptions.get(culture);
            ChoiceTuple tempVar2 = getConfirmChoices();
            ChoiceTuple confirmChoices = (tempVar2 != null) ? tempVar2 : DefaultConfirmChoices.get(culture);
            ArrayList<Choice> choices = new ArrayList<Choice>(Arrays.asList(confirmChoices.Item1, confirmChoices.Item2));
            if (isRetry && options.getRetryPrompt() != null)
            {
                prompt = AppendChoices(options.getRetryPrompt(), channelId, choices, getStyle(), choiceOptions);
            }
            else
            {
                prompt = AppendChoices(options.getPrompt(), channelId, choices, getStyle(), choiceOptions);
            }

            // Send prompt

            try {
                turnContext.SendActivityAsync(prompt).get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
            return;
        }, turnContext.executorService());
	}

	@Override
	protected CompletableFuture<PromptRecognizerResult<Boolean>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (turnContext == null)
            {
                throw new NullPointerException("turnContext");
            }

            PromptRecognizerResult<Boolean> result = new PromptRecognizerResult<Boolean>();
            if (turnContext.activity().type() == ActivityTypes.MESSAGE)
            {
                // Recognize utterance
                Activity message = turnContext.activity();
                String tempVar = getDefaultLocale();
                String culture = (turnContext.activity().locale() != null) ? turnContext.activity().locale() : (tempVar != null) ? tempVar : English;
                List<ModelResult> results = ChoiceRecognizer.RecognizeBoolean(message.text(), culture);
                if (results.size() > 0)
                {
                    ModelResult first = results.get(0);
                    boolean value = Boolean.getBoolean(first.getResolution().toString());

                    // TODO: What is the resolution object? getResolution().["value"]
                    if (Boolean.getBoolean(first.getResolution().toString()))
                    {
                        result.withSucceeded(true);
                        result.withValue(value);
                    }
                }
                else
                {
                    // First check whether the prompt was sent to the user with numbers - if it was we should recognize numbers
                    ChoiceFactoryOptions tempVar2 = getChoiceOptions();
                    ChoiceFactoryOptions choiceOptions = (tempVar2 != null) ? tempVar2 : DefaultChoiceOptions.get(culture);

                    // This logic reflects the fact that IncludeNumbers is nullable and True is the default set in Inline style
                    if (!choiceOptions.includeNumbers().isPresent() || choiceOptions.includeNumbers().get())
                    {
                        // The text may be a number in which case we will interpret that as a choice.
                        ChoiceTuple tempVar3 = getConfirmChoices();
                        ChoiceTuple confirmChoices = (tempVar3 != null) ? tempVar3 : DefaultConfirmChoices.get(culture);
                        ArrayList<Choice> choices = new ArrayList<Choice>(Arrays.asList(confirmChoices.item1(), confirmChoices.item2()));
                        ArrayList<ModelResult<FoundChoice>> secondAttemptResults = ChoiceRecognizers.RecognizeChoices(message.text(), choices);
                        if (!secondAttemptResults.isEmpty())
                        {
                            result.withSucceeded(true);
                            result.withValue(secondAttemptResults.get(0).getResolution().getIndex() == 0);
                        }
                    }
                }
            }

            return CompletableFuture.completedFuture(result);

        }, turnContext.executorService());
	}
}