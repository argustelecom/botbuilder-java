// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.dialogs.choices.Choice;
import com.microsoft.bot.builder.dialogs.choices.ChoiceFactoryOptions;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;



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
	private static final HashMap<String, Pair<Choice, Choice>> DefaultConfirmChoices = new HashMap<String, Pair<Choice, Choice>>(Map.ofEntries(Map.Entry(Spanish, new Pair<Choice, Choice>(new Choice {Value = "Sí"}, new Choice {Value = "No"})), Map.entry(Dutch, new Tuple<Choice, Choice>(new Choice {Value = "Ja"}, new Choice {Value = "Nee"})), Map.entry(English, new Tuple<Choice, Choice>(new Choice {Value = "Yes"}, new Choice {Value = "No"})), Map.entry(French, new Tuple<Choice, Choice>(new Choice {Value = "Oui"}, new Choice {Value = "Non"})), Map.entry(German, new Tuple<Choice, Choice>(new Choice {Value = "Ja"}, new Choice {Value = "Nein"})), Map.entry(Japanese, new Tuple<Choice, Choice>(new Choice {Value = "はい"}, new Choice {Value = "いいえ"})), Map.entry(Portuguese, new Tuple<Choice, Choice>(new Choice {Value = "Sim"}, new Choice {Value = "Não"})), Map.entry(Chinese, new Tuple<Choice, Choice>(new Choice {Value = "是的"}, new Choice {Value = "不"}))));

	private static final HashMap<String, ChoiceFactoryOptions> DefaultChoiceOptions = new HashMap<String, ChoiceFactoryOptions>(Map.ofEntries(Map.Entry(Spanish, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " o ", InlineOrMore = ", o ", IncludeNumbers = true}), Map.entry(Dutch, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " of ", InlineOrMore = ", of ", IncludeNumbers = true}), Map.entry(English, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " or ", InlineOrMore = ", or ", IncludeNumbers = true}), Map.entry(French, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " ou ", InlineOrMore = ", ou ", IncludeNumbers = true}), Map.entry(German, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " oder ", InlineOrMore = ", oder ", IncludeNumbers = true}), Map.entry(Japanese, new ChoiceFactoryOptions {InlineSeparator = "、 ", InlineOr = " または ", InlineOrMore = "、 または ", IncludeNumbers = true}), Map.entry(Portuguese, new ChoiceFactoryOptions {InlineSeparator = ", ", InlineOr = " ou ", InlineOrMore = ", ou ", IncludeNumbers = true}), Map.entry(Chinese, new ChoiceFactoryOptions {InlineSeparator = "， ", InlineOr = " 要么 ", InlineOrMore = "， 要么 ", IncludeNumbers = true})));

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

	private Tuple<Choice, Choice> ConfirmChoices;
	public final Tuple<Choice, Choice> getConfirmChoices()
	{
		return ConfirmChoices;
	}
	public final void setConfirmChoices(Tuple<Choice, Choice> value)
	{
		ConfirmChoices = value;
	}


	@Override
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options, boolean isRetry)
	{
		return OnPromptAsync(turnContext, state, options, isRetry, null);
	}


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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var channelId = turnContext.Activity.ChannelId;
		Microsoft.Bot.Builder.Dialogs.Choices.ChoiceFactoryOptions tempVar = getChoiceOptions();
		ChoiceFactoryOptions choiceOptions = (tempVar != null) ? tempVar : DefaultChoiceOptions.get(culture);
		Tuple<Choice, Choice> tempVar2 = getConfirmChoices();
		Tuple<Choice, Choice> confirmChoices = (tempVar2 != null) ? tempVar2 : DefaultConfirmChoices.get(culture);
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

		await turnContext.SendActivityAsync(prompt).get();
	}


	@Override
	protected CompletableFuture<PromptRecognizerResult<Boolean>> OnRecognizeAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected override CompletableFuture<PromptRecognizerResult<bool>> OnRecognizeAsync(TurnContext turnContext, IDictionary<string, object> state, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken))
	@Override
	protected CompletableFuture<PromptRecognizerResult<Boolean>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		PromptRecognizerResult<Boolean> result = new PromptRecognizerResult<Boolean>();
		if (turnContext.Activity.Type == ActivityTypes.Message)
		{
			// Recognize utterance
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var message = turnContext.Activity.AsMessageActivity();
			String tempVar = getDefaultLocale();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var culture = (turnContext.Activity.Locale != null) ? turnContext.Activity.Locale : (tempVar != null) ? tempVar : English;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var results = ChoiceRecognizer.RecognizeBoolean(message.Text, culture);
			if (results.size() > 0)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var first = results[0];
				boolean value;
				tangible.OutObject<Boolean> tempOut_value = new tangible.OutObject<Boolean>();
				if (Boolean.TryParse(first.Resolution["value"].toString(), tempOut_value))
				{
				value = tempOut_value.argValue;
					result.setSucceeded(true);
					result.setValue(value);
				}
			else
			{
				value = tempOut_value.argValue;
			}
			}
			else
			{
				// First check whether the prompt was sent to the user with numbers - if it was we should recognize numbers
				Microsoft.Bot.Builder.Dialogs.Choices.ChoiceFactoryOptions tempVar2 = getChoiceOptions();
				ChoiceFactoryOptions choiceOptions = (tempVar2 != null) ? tempVar2 : DefaultChoiceOptions.get(culture);

				// This logic reflects the fact that IncludeNumbers is nullable and True is the default set in Inline style
				if (!choiceOptions.getIncludeNumbers().isPresent() || choiceOptions.getIncludeNumbers().get())
				{
					// The text may be a number in which case we will interpret that as a choice.
					Tuple<Choice, Choice> tempVar3 = getConfirmChoices();
					Tuple<Choice, Choice> confirmChoices = (tempVar3 != null) ? tempVar3 : DefaultConfirmChoices.get(culture);
					ArrayList<Choice> choices = new ArrayList<Choice>(Arrays.asList(confirmChoices.Item1, confirmChoices.Item2));
					ArrayList<ModelResult<FoundChoice>> secondAttemptResults = ChoiceRecognizers.RecognizeChoices(message.Text, choices);
					if (!secondAttemptResults.isEmpty())
					{
						result.setSucceeded(true);
						result.setValue(secondAttemptResults.get(0).getResolution().getIndex() == 0);
					}
				}
			}
		}

		return CompletableFuture.completedFuture(result);
	}
}