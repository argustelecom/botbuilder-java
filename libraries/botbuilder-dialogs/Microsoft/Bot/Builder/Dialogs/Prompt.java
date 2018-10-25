package Microsoft.Bot.Builder.Dialogs;

import Newtonsoft.Json.*;
import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


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

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public Prompt(string dialogId, PromptValidator<T> validator = null)
	public Prompt(String dialogId, PromptValidator<T> validator)
	{
		super(dialogId);
		_validator = (PromptValidatorContext promptContext, CancellationToken cancellationToken) -> validator.invoke(promptContext, cancellationToken);
	}


	@Override
	public Task<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options)
	{
		return BeginDialogAsync(dc, options, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task<DialogTurnResult> BeginDialogAsync(DialogContext dc, object options, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public Task<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options, CancellationToken cancellationToken)
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		if (!(options instanceof PromptOptions))
		{
			throw new IndexOutOfBoundsException("options", "Prompt options are required for Prompt dialogs");
		}

		// Ensure prompts have input hint set
		PromptOptions opt = (PromptOptions)options;
		if (opt.getPrompt() != null && tangible.StringHelper.isNullOrEmpty(opt.getPrompt().InputHint))
		{
			opt.getPrompt().InputHint = InputHints.ExpectingInput;
		}

		if (opt.getRetryPrompt() != null && tangible.StringHelper.isNullOrEmpty(opt.getRetryPrompt().InputHint))
		{
			opt.getRetryPrompt().InputHint = InputHints.ExpectingInput;
		}

		// Initialize prompt state
		Map<String, Object> state = dc.getActiveDialog().getState();
		state.put(PersistedOptions, opt);
		state.put(PersistedState, new HashMap<String, Object>());

		// Send initial prompt
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await OnPromptAsync(dc.getContext(), (Map<String, Object>)state.get(PersistedState), (PromptOptions)state.get(PersistedOptions), false, cancellationToken).ConfigureAwait(false);
		return Dialog.EndOfTurn;
	}


	@Override
	public Task<DialogTurnResult> ContinueDialogAsync(DialogContext dc)
	{
		return ContinueDialogAsync(dc, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task<DialogTurnResult> ContinueDialogAsync(DialogContext dc, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public Task<DialogTurnResult> ContinueDialogAsync(DialogContext dc, CancellationToken cancellationToken)
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		// Don't do anything for non-message activities
		if (dc.getContext().Activity.Type != ActivityTypes.Message)
		{
			return Dialog.EndOfTurn;
		}

		// Perform base recognition
		Microsoft.Bot.Builder.Dialogs.DialogInstance instance = dc.getActiveDialog();
		Map<String, Object> state = (Map<String, Object>)instance.getState().get(PersistedState);
		PromptOptions options = (PromptOptions)instance.getState().get(PersistedOptions);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var recognized = await OnRecognizeAsync(dc.getContext(), state, options, cancellationToken).ConfigureAwait(false);

		// Validate the return value
		boolean isValid = false;
		if (_validator != null)
		{
			PromptValidatorContext<T> promptContext = new PromptValidatorContext<T>(dc.getContext(), recognized, state, options);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			isValid = await _validator.invoke(promptContext, cancellationToken).ConfigureAwait(false);
		}
		else if (recognized.Succeeded)
		{
			isValid = true;
		}

		// Return recognized value or re-prompt
		if (isValid)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await dc.EndDialogAsync(recognized.Value).ConfigureAwait(false);
		}
		else
		{
			if (!dc.getContext().Responded)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await OnPromptAsync(dc.getContext(), state, options, true).ConfigureAwait(false);
			}

			return Dialog.EndOfTurn;
		}
	}


	@Override
	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result)
	{
		return ResumeDialogAsync(dc, reason, result, null);
	}

	@Override
	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason)
	{
		return ResumeDialogAsync(dc, reason, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, object result = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result, CancellationToken cancellationToken)
	{
		// Prompts are typically leaf nodes on the stack but the dev is free to push other dialogs
		// on top of the stack which will result in the prompt receiving an unexpected call to
		// dialogResume() when the pushed on dialog ends.
		// To avoid the prompt prematurely ending we need to implement this method and
		// simply re-prompt the user.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await RepromptDialogAsync(dc.getContext(), dc.getActiveDialog()).ConfigureAwait(false);
		return Dialog.EndOfTurn;
	}


	@Override
	public Task RepromptDialogAsync(ITurnContext turnContext, DialogInstance instance)
	{
		return RepromptDialogAsync(turnContext, instance, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task RepromptDialogAsync(ITurnContext turnContext, DialogInstance instance, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public Task RepromptDialogAsync(ITurnContext turnContext, DialogInstance instance, CancellationToken cancellationToken)
	{
		Map<String, Object> state = (Map<String, Object>)instance.getState().get(PersistedState);
		PromptOptions options = (PromptOptions)instance.getState().get(PersistedOptions);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await OnPromptAsync(turnContext, state, options, false).ConfigureAwait(false);
	}


	protected final abstract Task OnPromptAsync(ITurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options, boolean isRetry);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected abstract Task OnPromptAsync(ITurnContext turnContext, IDictionary<string, object> state, PromptOptions options, bool isRetry, CancellationToken cancellationToken = default(CancellationToken));
	protected abstract Task OnPromptAsync(ITurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry, CancellationToken cancellationToken);


	protected final abstract Task<PromptRecognizerResult<T>> OnRecognizeAsync(ITurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected abstract Task<PromptRecognizerResult<T>> OnRecognizeAsync(ITurnContext turnContext, IDictionary<string, object> state, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken));
	protected abstract Task<PromptRecognizerResult<T>> OnRecognizeAsync(ITurnContext turnContext, Map<String, Object> state, PromptOptions options, CancellationToken cancellationToken);


	protected final IMessageActivity AppendChoices(IMessageActivity prompt, String channelId, java.util.List<Choice> choices, ListStyle style, ChoiceFactoryOptions options)
	{
		return AppendChoices(prompt, channelId, choices, style, options, null);
	}

	protected final IMessageActivity AppendChoices(IMessageActivity prompt, String channelId, java.util.List<Choice> choices, ListStyle style)
	{
		return AppendChoices(prompt, channelId, choices, style, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected IMessageActivity AppendChoices(IMessageActivity prompt, string channelId, IList<Choice> choices, ListStyle style, ChoiceFactoryOptions options = null, CancellationToken cancellationToken = default(CancellationToken))
	protected final IMessageActivity AppendChoices(IMessageActivity prompt, String channelId, List<Choice> choices, ListStyle style, ChoiceFactoryOptions options, CancellationToken cancellationToken)
	{
		// Get base prompt text (if any)
		boolean text = prompt != null && !tangible.StringHelper.isNullOrEmpty(prompt.Text) ? prompt.Text : "";

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