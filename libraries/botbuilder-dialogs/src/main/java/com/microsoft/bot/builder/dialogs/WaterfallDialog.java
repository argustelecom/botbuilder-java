// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import com.microsoft.bot.schema.models.ActivityTypes;

/**
 Dialog optimized for prompting a user with a series of questions. Waterfalls accept a stack of
 functions which will be executed in sequence.Each waterfall step can ask a question of the user
 and the users response will be passed as an argument to the next waterfall step.
*/
public class WaterfallDialog extends Dialog
{
	private static final String PersistedOptions = "options";
	private static final String StepIndex = "stepIndex";
	private static final String PersistedValues = "values";

	private ArrayList<WaterfallStep> _steps;

	/** 
	 Initializes a new instance of the <see cref="WaterfallDialog"/> class.
	 
	 @param dialogId The dialog id.
	 @param steps Optional steps to be defined by caller.
	*/

	public WaterfallDialog(String dialogId)
	{
		this(dialogId, null);
	}

	public WaterfallDialog(String dialogId, Iterable<WaterfallStep> steps)
	{
		super(dialogId);
		if (steps != null)
		{
			_steps = new ArrayList<WaterfallStep>();
            steps.forEach(_steps::add);
		}
		else
		{
			_steps = new ArrayList<WaterfallStep>();
		}
	}

	/** 
	 Add a new step to the waterfall.
	 
	 @param step Step to add.
	 @return Waterfall dialog for fluent calls to .AddStep().
	*/
	public final WaterfallDialog AddStep(WaterfallStep step)
	{
		if (step == null)
        {
            throw new NullPointerException("step");
        }

		_steps.add(step);

		return this;
	}


	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc)
	{
		return BeginDialogAsync(dc, null);
	}

	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options)
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (dc == null)
            {
                throw new NullPointerException("dc");
            }

            // Initialize waterfall state
            Map<String, Object> state = dc.getActiveDialog().getState();
            state.put(PersistedOptions, options);
            state.put(PersistedValues, new HashMap<String, Object>());

            // Run first step

            try {
                return RunStepAsync(dc, 0, DialogReason.BeginCalled, null).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, dc.getContext().executorService());
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc)
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (dc == null)
            {
                throw new NullPointerException("dc");
            }

            // Don't do anything for non-message activities.
            if (dc.getContext().activity().type() != ActivityTypes.MESSAGE.toString())
            {
                return Dialog.EndOfTurn;
            }

            // Run next step with the message text as the result.
            try {
                return ResumeDialogAsync(dc, DialogReason.ContinueCalled, dc.getContext().activity().text()).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, dc.getContext().executorService());
	}



	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result )
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (dc == null)
            {
                throw new NullPointerException("dc");
            }

            // Increment step index and run step
            Map<String, Object> state = dc.getActiveDialog().getState();

            // For issue https://github.com/Microsoft/botbuilder-dotnet/issues/871
            // See the linked issue for details. This issue was happening when using the CosmosDB
            // data store for state. The stepIndex which was an object being cast to an Int64
            // after deserialization was throwing an exception for not being Int32 datatype.
            // This change ensures the correct datatype conversion has been done.
            int index = (int)state.get(StepIndex);

            try {
                return RunStepAsync(dc, index + 1, reason, result).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, dc.getContext().executorService());
	}


	protected CompletableFuture<DialogTurnResult> OnStepAsync(WaterfallStepContext stepContext )
	{
        return CompletableFuture.supplyAsync(() -> {
            try {
                return _steps.get(stepContext.getIndex()).invoke(stepContext).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, stepContext.getContext().executorService());

	}


	private CompletableFuture<DialogTurnResult> RunStepAsync(DialogContext dc, int index, DialogReason reason, Object result )
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (dc == null)
            {
                throw new NullPointerException("dc");
            }

            if (index < _steps.size())
            {
                // Update persisted step index
                Map<String, Object> state = dc.getActiveDialog().getState();
                state.put(StepIndex, index);

                // Create step context
                Object options = state.get(PersistedOptions);
                Map<String, Object> values = (Map<String, Object>)state.get(PersistedValues);
                WaterfallStepContext stepContext = new WaterfallStepContext(this, dc, options, values, index, reason, result);

                // Execute step

                try {
                    return OnStepAsync(stepContext).get();
                } catch (InterruptedException|ExecutionException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
            }
            else
            {
                // End of waterfall so just return any result to parent

                try {
                    return dc.EndDialogAsync(result).get();
                } catch (InterruptedException|ExecutionException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
            }

        });
	}
}