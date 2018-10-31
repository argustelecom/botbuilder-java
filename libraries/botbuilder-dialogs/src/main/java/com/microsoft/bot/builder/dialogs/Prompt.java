// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import Newtonsoft.Json.*;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.ActivityTypes;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 Basic configuration options supported by all prompts.
 
 <typeparam name="T">The type of the <see cref="Prompt{T}"/>.</typeparam>
*/
public abstract class Prompt<T> extends Dialog
{
	private static final String PersistedOptions = "options";
	private static final String PersistedState = "state";

	private PromptValidator<T> _validator;


	public Prompt(String dialogId)
	{
		this(dialogId, null);
	}

	public Prompt(String dialogId, PromptValidator<T> validator)
	{
		super(dialogId);
		_validator = (PromptValidatorContext promptContext ) -> validator.invoke(promptContext);
	}


	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options)
	{
		return BeginDialogAsync(dc, options);
	}


	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options )
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		if (!(options instanceof PromptOptions))
		{
			throw new IndexOutOfBoundsException("Prompt options are required for Prompt dialogs");
		}

		// Ensure prompts have input hint set
		PromptOptions opt = (PromptOptions)options;
		if (opt.getPrompt() != null && StringUtils.isBlank(opt.getPrompt().InputHint))
		{
			opt.getPrompt().InputHint = InputHints.ExpectingInput;
		}

		if (opt.getRetryPrompt() != null && StringUtils.isBlank(opt.getRetryPrompt().InputHint))
		{
			opt.getRetryPrompt().InputHint = InputHints.ExpectingInput;
		}

		// Initialize prompt state
		Map<String, Object> state = dc.getActiveDialog().getState();
		state.put(PersistedOptions, opt);
		state.put(PersistedState, new HashMap<String, Object>());

		// Send initial prompt

		OnPromptAsync(dc.getContext(), (Map<String, Object>)state.get(PersistedState), (PromptOptions)state.get(PersistedOptions), false).get();
		return Dialog.EndOfTurn;
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc)
	{
		return ContinueDialogAsync(dc, null);
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc )
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		// Don't do anything for non-message activities
		if (dc.getContext().activity().type() != ActivityTypes.MESSAGE)
		{
			return Dialog.EndOfTurn;
		}

		// Perform base recognition
		DialogInstance instance = dc.getActiveDialog();
		Map<String, Object> state = (Map<String, Object>)instance.getState().get(PersistedState);
		PromptOptions options = (PromptOptions)instance.getState().get(PersistedOptions);

        PromptRecognizerResult recognized = OnRecognizeAsync(dc.getContext(), state, options).get();

		// Validate the return value
		boolean isValid = false;
		if (_validator != null)
		{
			PromptValidatorContext<T> promptContext = new PromptValidatorContext<T>(dc.getContext(), recognized, state, options);

			isValid = _validator.invoke(promptContext).get();
		}
		else if (recognized.Succeeded)
		{
			isValid = true;
		}

		// Return recognized value or re-prompt
		if (isValid)
		{

			return dc.EndDialogAsync(recognized).getValue()).get();
		}
		else
		{
			if (!dc.getContext().Responded)
			{

				OnPromptAsync(dc.getContext(), state, options, true).get();
			}

			return Dialog.EndOfTurn;
		}
	}


	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result)
	{
		return ResumeDialogAsync(dc, reason, result, null);
	}

	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason)
	{
		return ResumeDialogAsync(dc, reason, null, null);
	}


	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result )
	{
		// Prompts are typically leaf nodes on the stack but the dev is free to push other dialogs
		// on top of the stack which will result in the prompt receiving an unexpected call to
		// dialogResume() when the pushed on dialog ends.
		// To avoid the prompt prematurely ending we need to implement this method and
		// simply re-prompt the user.

		await RepromptDialogAsync(dc.getContext(), dc.getActiveDialog()).get();
		return Dialog.EndOfTurn;
	}


	@Override
	public CompletableFuture RepromptDialogAsync(TurnContext turnContext, DialogInstance instance)
	{
		return RepromptDialogAsync(turnContext, instance, null);
	}


	@Override
	public CompletableFuture RepromptDialogAsync(TurnContext turnContext, DialogInstance instance )
	{
		Map<String, Object> state = (Map<String, Object>)instance.getState().get(PersistedState);
		PromptOptions options = (PromptOptions)instance.getState().get(PersistedOptions);

		OnPromptAsync(turnContext, state, options, false).get();
	}


	protected final abstract CompletableFuture OnPromptAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry);
	protected abstract CompletableFuture OnPromptAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry );


	protected final abstract CompletableFuture<PromptRecognizerResult<T>> OnRecognizeAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options);
	protected abstract CompletableFuture<PromptRecognizerResult<T>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options );


	protected final IMessageActivity AppendChoices(IMessageActivity prompt, String channelId, java.util.List<Choice> choices, ListStyle style, ChoiceFactoryOptions options)
	{
		return AppendChoices(prompt, channelId, choices, style, options, null);
	}

	protected final IMessageActivity AppendChoices(IMessageActivity prompt, String channelId, java.util.List<Choice> choices, ListStyle style)
	{
		return AppendChoices(prompt, channelId, choices, style, null, null);
	}

	protected final IMessageActivity AppendChoices(IMessageActivity prompt, String channelId, List<Choice> choices, ListStyle style, ChoiceFactoryOptions options )
	{
		// Get base prompt text (if any)
		boolean text = prompt != null && !StringUtils.isBlank(prompt.Text) ? prompt.Text : "";

		// Create temporary msg
		IMessageActivity msg;
		switch (style)
		{
			case Inline:
				msg = ChoiceFactory.Inline(choices, text, null, options);
				break;

			case List:
				msg = ChoiceFactory.List(choices, text, null, options);
				break;

			case SuggestedAction:
				msg = ChoiceFactory.SuggestedAction(choices, text);
				break;

			case None:
				msg = Activity.CreateMessageActivity();
				msg.Text = text;
				break;

			default:
				msg = ChoiceFactory.ForChannel(channelId, choices, text, null, options);
				break;
		}

		// Update prompt with text and actions
		if (prompt != null)
		{
			// clone the prompt the set in the options (note ActivityEx has Properties so this is the safest mechanism)
			prompt = JsonConvert.<Activity>DeserializeObject(JsonConvert.SerializeObject(prompt));

			prompt.Text = msg.Text;
			if (msg.SuggestedActions != null && msg.SuggestedActions.Actions != null && msg.SuggestedActions.Actions.size() > 0)
			{
				prompt.SuggestedActions = msg.SuggestedActions;
			}

			return prompt;
		}
		else
		{
			msg.InputHint = InputHints.ExpectingInput;
			return msg;
		}
	}
}