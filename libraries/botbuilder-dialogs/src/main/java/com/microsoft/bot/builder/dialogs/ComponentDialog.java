package Microsoft.Bot.Builder.Dialogs;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class ComponentDialog extends Dialog
{
	private static final String PersistedDialogState = "dialogs";

	private DialogSet _dialogs;

	public ComponentDialog(String dialogId)
	{
		super(dialogId);
		if (tangible.StringHelper.isNullOrEmpty(dialogId))
		{
			throw new NullPointerException("dialogId");
		}

		_dialogs = new DialogSet();
	}

	private String InitialDialogId;
	protected final String getInitialDialogId()
	{
		return InitialDialogId;
	}
	protected final void setInitialDialogId(String value)
	{
		InitialDialogId = value;
	}


	@Override
	public Task<DialogTurnResult> BeginDialogAsync(DialogContext outerDc, Object options)
	{
		return BeginDialogAsync(outerDc, options, null);
	}

	@Override
	public Task<DialogTurnResult> BeginDialogAsync(DialogContext outerDc)
	{
		return BeginDialogAsync(outerDc, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task<DialogTurnResult> BeginDialogAsync(DialogContext outerDc, object options = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public Task<DialogTurnResult> BeginDialogAsync(DialogContext outerDc, Object options, CancellationToken cancellationToken)
	{
		if (outerDc == null)
		{
			throw new NullPointerException("outerDc");
		}

		// Start the inner dialog.
		DialogState dialogState = new DialogState();
		outerDc.getActiveDialog().getState().put(PersistedDialogState, dialogState);
		DialogContext innerDc = new DialogContext(_dialogs, outerDc.getContext(), dialogState);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var turnResult = await OnBeginDialogAsync(innerDc, options, cancellationToken).ConfigureAwait(false);

		// Check for end of inner dialog
		if (turnResult.Status != DialogTurnStatus.Waiting)
		{
			// Return result to calling dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await EndComponentAsync(outerDc, turnResult.Result, cancellationToken).ConfigureAwait(false);
		}
		else
		{
			// Just signal waiting
			return Dialog.EndOfTurn;
		}
	}


	@Override
	public Task<DialogTurnResult> ContinueDialogAsync(DialogContext outerDc)
	{
		return ContinueDialogAsync(outerDc, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task<DialogTurnResult> ContinueDialogAsync(DialogContext outerDc, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public Task<DialogTurnResult> ContinueDialogAsync(DialogContext outerDc, CancellationToken cancellationToken)
	{
		if (outerDc == null)
		{
			throw new NullPointerException("outerDc");
		}

		// Continue execution of inner dialog.
		DialogState dialogState = (DialogState)outerDc.getActiveDialog().getState().get(PersistedDialogState);
		DialogContext innerDc = new DialogContext(_dialogs, outerDc.getContext(), dialogState);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var turnResult = await OnContinueDialogAsync(innerDc, cancellationToken).ConfigureAwait(false);

		if (turnResult.Status != DialogTurnStatus.Waiting)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await EndComponentAsync(outerDc, turnResult.Result, cancellationToken).ConfigureAwait(false);
		}
		else
		{
			return Dialog.EndOfTurn;
		}
	}


	@Override
	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason, Object result)
	{
		return ResumeDialogAsync(outerDc, reason, result, null);
	}

	@Override
	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason)
	{
		return ResumeDialogAsync(outerDc, reason, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason, object result = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason, Object result, CancellationToken cancellationToken)
	{
		// Containers are typically leaf nodes on the stack but the dev is free to push other dialogs
		// on top of the stack which will result in the container receiving an unexpected call to
		// dialogResume() when the pushed on dialog ends.
		// To avoid the container prematurely ending we need to implement this method and simply
		// ask our inner dialog stack to re-prompt.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await RepromptDialogAsync(outerDc.getContext(), outerDc.getActiveDialog(), cancellationToken).ConfigureAwait(false);
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
		// Delegate to inner dialog.
		DialogState dialogState = (DialogState)instance.getState().get(PersistedDialogState);
		DialogContext innerDc = new DialogContext(_dialogs, turnContext, dialogState);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await innerDc.RepromptDialogAsync(cancellationToken).ConfigureAwait(false);

		// Notify component
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await OnRepromptDialogAsync(turnContext, instance, cancellationToken).ConfigureAwait(false);
	}


	@Override
	public Task EndDialogAsync(ITurnContext turnContext, DialogInstance instance, DialogReason reason)
	{
		return EndDialogAsync(turnContext, instance, reason, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async Task EndDialogAsync(ITurnContext turnContext, DialogInstance instance, DialogReason reason, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public Task EndDialogAsync(ITurnContext turnContext, DialogInstance instance, DialogReason reason, CancellationToken cancellationToken)
	{
		// Forward cancel to inner dialogs
		if (reason == DialogReason.CancelCalled)
		{
			DialogState dialogState = (DialogState)instance.getState().get(PersistedDialogState);
			DialogContext innerDc = new DialogContext(_dialogs, turnContext, dialogState);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await innerDc.CancelAllDialogsAsync(cancellationToken).ConfigureAwait(false);
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await OnEndDialogAsync(turnContext, instance, reason, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Adds a dialog to the component dialog.
	 
	 @param dialog The dialog to add.
	 @return The updated <see cref="ComponentDialog"/>.
	*/
	public final ComponentDialog AddDialog(Dialog dialog)
	{
		_dialogs.Add(dialog);
		if (tangible.StringHelper.isNullOrEmpty(getInitialDialogId()))
		{
			setInitialDialogId(dialog.getId());
		}

		return this;
	}

	/** 
	 Finds a dialog by ID.
	 
	 @param dialogId The ID of the dialog to find.
	 @return The dialog; or <c>null</c> if there is not a match for the ID.
	*/
	public final Dialog FindDialog(String dialogId)
	{
		return _dialogs.Find(dialogId);
	}


	protected Task<DialogTurnResult> OnBeginDialogAsync(DialogContext innerDc, Object options)
	{
		return OnBeginDialogAsync(innerDc, options, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual Task<DialogTurnResult> OnBeginDialogAsync(DialogContext innerDc, object options, CancellationToken cancellationToken = default(CancellationToken))
	protected Task<DialogTurnResult> OnBeginDialogAsync(DialogContext innerDc, Object options, CancellationToken cancellationToken)
	{
		return innerDc.BeginDialogAsync(getInitialDialogId(), options, cancellationToken);
	}


	protected Task<DialogTurnResult> OnContinueDialogAsync(DialogContext innerDc)
	{
		return OnContinueDialogAsync(innerDc, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual Task<DialogTurnResult> OnContinueDialogAsync(DialogContext innerDc, CancellationToken cancellationToken = default(CancellationToken))
	protected Task<DialogTurnResult> OnContinueDialogAsync(DialogContext innerDc, CancellationToken cancellationToken)
	{
		return innerDc.ContinueDialogAsync(cancellationToken);
	}


	protected Task OnEndDialogAsync(ITurnContext context, DialogInstance instance, DialogReason reason)
	{
		return OnEndDialogAsync(context, instance, reason, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual Task OnEndDialogAsync(ITurnContext context, DialogInstance instance, DialogReason reason, CancellationToken cancellationToken = default(CancellationToken))
	protected Task OnEndDialogAsync(ITurnContext context, DialogInstance instance, DialogReason reason, CancellationToken cancellationToken)
	{
		return Task.CompletedTask;
	}


	protected Task OnRepromptDialogAsync(ITurnContext turnContext, DialogInstance instance)
	{
		return OnRepromptDialogAsync(turnContext, instance, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual Task OnRepromptDialogAsync(ITurnContext turnContext, DialogInstance instance, CancellationToken cancellationToken = default(CancellationToken))
	protected Task OnRepromptDialogAsync(ITurnContext turnContext, DialogInstance instance, CancellationToken cancellationToken)
	{
		return Task.CompletedTask;
	}

	protected Task<DialogTurnResult> EndComponentAsync(DialogContext outerDc, Object result, CancellationToken cancellationToken)
	{
		return outerDc.EndDialogAsync(result);
	}
}