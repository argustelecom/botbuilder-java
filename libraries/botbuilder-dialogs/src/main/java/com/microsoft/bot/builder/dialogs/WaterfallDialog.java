package com.microsoft.bot.builder.dialogs;

import java.util.*;
import java.util.concurrent.CompletableFuture;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


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

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public WaterfallDialog(string dialogId, IEnumerable<WaterfallStep> steps = null)
	public WaterfallDialog(String dialogId, Iterable<WaterfallStep> steps)
	{
		super(dialogId);
		if (steps != null)
		{
			_steps = new ArrayList<WaterfallStep>(steps);
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
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: _steps.Add(step ?? throw new ArgumentNullException(nameof(step)));
		_steps.add((step != null) ? step : throw new NullPointerException("step"));
		return this;
	}


	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options)
	{
		return BeginDialogAsync(dc, options, null);
	}

	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc)
	{
		return BeginDialogAsync(dc, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, object options = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options )
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		// Initialize waterfall state
		Map<String, Object> state = dc.getActiveDialog().getState();
		state.put(PersistedOptions, options);
		state.put(PersistedValues, new HashMap<String, Object>());

		// Run first step
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await RunStepAsync(dc, 0, DialogReason.BeginCalled, null).get();
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc)
	{
		return ContinueDialogAsync(dc, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc )
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		// Don't do anything for non-message activities.
		if (dc.getContext().Activity.Type != ActivityTypes.Message)
		{
			return Dialog.EndOfTurn;
		}

		// Run next step with the message text as the result.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await ResumeDialogAsync(dc, DialogReason.ContinueCalled, dc.getContext().Activity.Text).get();
	}


	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result)
	{
		return ResumeDialogAsync(dc, reason, result, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, object result, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result )
	{
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await RunStepAsync(dc, index + 1, reason, result).get();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: protected virtual async CompletableFuture<DialogTurnResult> OnStepAsync(WaterfallStepContext stepContext )
	protected CompletableFuture<DialogTurnResult> OnStepAsync(WaterfallStepContext stepContext )
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await _steps.get(stepContext.getIndex())(stepContext).get();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async CompletableFuture<DialogTurnResult> RunStepAsync(DialogContext dc, int index, DialogReason reason, object result )
	private CompletableFuture<DialogTurnResult> RunStepAsync(DialogContext dc, int index, DialogReason reason, Object result )
	{
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await OnStepAsync(stepContext).get();
		}
		else
		{
			// End of waterfall so just return any result to parent
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await dc.EndDialogAsync(result).get();
		}
	}
}