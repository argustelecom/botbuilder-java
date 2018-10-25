package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.schema.models.Activity;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Basic configuration options supported by all prompts.
*/
public abstract class ActivityPrompt extends Dialog
{
	private static final String PersistedOptions = "options";
	private static final String PersistedState = "state";

	private PromptValidator<Activity> _validator;

	public ActivityPrompt(String dialogId, PromptValidator<Activity> validator)
	{
		super(dialogId);
		if (validator == null)
        {
            throw new NullPointerException("validator");
        }
		_validator = validator;
	}


	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options)
	{
	    return CompletableFuture.supplyAsync(() -> {
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
            OnPromptAsync(dc.getContext(), (Map<String, Object>)state.get(PersistedState), (PromptOptions)state.get(PersistedOptions)).get();
            return Dialog.EndOfTurn;

        });
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc)
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (dc == null)
            {
                throw new NullPointerException("dc");
            }

            // Perform base recognition
            DialogInstance instance = dc.getActiveDialog();
            Map<String, Object> state = (Map<String, Object>)instance.getState().get(PersistedState);
            PromptOptions options = (PromptOptions)instance.getState().get(PersistedOptions);
            PromptRecognizerResult<Activity> recognized = null;
            try {
                recognized = OnRecognizeAsync(dc.getContext(), state, options).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }

            // Validate the return value
            PromptValidatorContext<Activity> promptContext = new PromptValidatorContext<Activity>(dc.getContext(), recognized, state, options);
            Boolean isValid = _validator.invoke(promptContext).get();

            // Return recognized value or re-prompt
            if (isValid)
            {
                return dc.EndDialogAsync(recognized.getValue()).get();
            }
            else
            {
                return Dialog.EndOfTurn;
            }

        });
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

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, object result = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result )
	{
		// Prompts are typically leaf nodes on the stack but the dev is free to push other dialogs
		// on top of the stack which will result in the prompt receiving an unexpected call to
		// dialogResume() when the pushed on dialog ends.
		// To avoid the prompt prematurely ending we need to implement this method and
		// simply re-prompt the user.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await RepromptDialogAsync(dc.getContext(), dc.getActiveDialog()).get();
		return Dialog.EndOfTurn;
	}


	@Override
	public CompletableFuture RepromptDialogAsync(TurnContext turnContext, DialogInstance instance)
	{
		return RepromptDialogAsync(turnContext, instance, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture RepromptDialogAsync(TurnContext turnContext, DialogInstance instance, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture RepromptDialogAsync(TurnContext turnContext, DialogInstance instance )
	{
		Map<String, Object> state = (Map<String, Object>)instance.getState().get(PersistedState);
		PromptOptions options = (PromptOptions)instance.getState().get(PersistedOptions);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await OnPromptAsync(turnContext, state, options).get();
	}


	protected CompletableFuture OnPromptAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnPromptAsync(turnContext, state, options, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: protected virtual async CompletableFuture OnPromptAsync(TurnContext turnContext, IDictionary<string, object> state, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	protected CompletableFuture OnPromptAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		if (options == null)
		{
			throw new NullPointerException("options");
		}

		if (options.getPrompt() != null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await turnContext.SendActivityAsync(options.getPrompt()).get();
		}
	}


	protected CompletableFuture<PromptRecognizerResult<Activity>> OnRecognizeAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual CompletableFuture<PromptRecognizerResult<Activity>> OnRecognizeAsync(TurnContext turnContext, IDictionary<string, object> state, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken))
	protected CompletableFuture<PromptRecognizerResult<Activity>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		PromptRecognizerResult<Activity> tempVar = new PromptRecognizerResult<Activity>();
		tempVar.setSucceeded(true);
		tempVar.setValue(turnContext.Activity);
		return Task.FromResult(tempVar);
	}
}