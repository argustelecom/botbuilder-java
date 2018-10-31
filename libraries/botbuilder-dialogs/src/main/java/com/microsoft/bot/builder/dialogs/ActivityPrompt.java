// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.microsoft.bot.builder.dialogs;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.InputHints;
import org.apache.commons.lang3.StringUtils;




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
                throw new IndexOutOfBoundsException("Prompt options are required for Prompt dialogs");
            }

            // Ensure prompts have input hint set
            PromptOptions opt = (PromptOptions)options;
            if (opt.getPrompt() != null && StringUtils.isBlank(opt.getPrompt().inputHint()))
            {
                opt.getPrompt().withInputHint(InputHints.EXPECTING_INPUT);
            }

            if (opt.getRetryPrompt() != null && StringUtils.isBlank(opt.getRetryPrompt().inputHint().toString()))
            {
                opt.getRetryPrompt().withInputHint(InputHints.EXPECTING_INPUT);
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
            Boolean isValid = null;
            try {
                isValid = _validator.invoke(promptContext).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }

            // Return recognized value or re-prompt
            if (isValid)
            {
                try {
                    return dc.EndDialogAsync(recognized.value()).get();
                } catch (InterruptedException|ExecutionException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
            }
            else
            {
                return Dialog.EndOfTurn;
            }

        });
	}



	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason)
	{
		return ResumeDialogAsync(dc, reason, null);
	}


	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result )
	{
	    return CompletableFuture.supplyAsync(() -> {
            // Prompts are typically leaf nodes on the stack but the dev is free to push other dialogs
            // on top of the stack which will result in the prompt receiving an unexpected call to
            // dialogResume() when the pushed on dialog ends.
            // To avoid the prompt prematurely ending we need to implement this method and
            // simply re-prompt the user.

            RepromptDialogAsync(dc.getContext(), dc.getActiveDialog()).get();
            return Dialog.EndOfTurn;

        }, dc.getContext().executorService());
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

		OnPromptAsync(turnContext, state, options).get();
	}


	protected CompletableFuture OnPromptAsync(TurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnPromptAsync(turnContext, state, options, null);
	}


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

			turnContext.SendActivityAsync(options.getPrompt()).get();
		}
	}

	protected CompletableFuture<PromptRecognizerResult<Activity>> OnRecognizeAsync(TurnContext turnContext, Map<String, Object> state, PromptOptions options )
	{
		PromptRecognizerResult<Activity> tempVar = new PromptRecognizerResult<Activity>();
		tempVar.setSucceeded(true);
		tempVar.setValue(turnContext.activity());
		return CompletableFuture.completedFuture(tempVar);
	}
}