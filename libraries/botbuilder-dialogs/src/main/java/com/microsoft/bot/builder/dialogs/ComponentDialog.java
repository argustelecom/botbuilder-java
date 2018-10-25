package com.microsoft.bot.builder.dialogs;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class ComponentDialog extends Dialog
{
	private static final String PersistedDialogState = "dialogs";

	private DialogSet _dialogs;

	public ComponentDialog(String dialogId)
	{
		super(dialogId);
		if (StringUtils.isBlank(dialogId))
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
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext outerDc, Object options)
	{
		return BeginDialogAsync(outerDc, options, null);
	}

	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext outerDc)
	{
		return BeginDialogAsync(outerDc, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext outerDc, object options = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext outerDc, Object options )
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
		var turnResult = await OnBeginDialogAsync(innerDc, options).get();

		// Check for end of inner dialog
		if (turnResult.Status != DialogTurnStatus.Waiting)
		{
			// Return result to calling dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await EndComponentAsync(outerDc, turnResult.Result).get();
		}
		else
		{
			// Just signal waiting
			return Dialog.EndOfTurn;
		}
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext outerDc)
	{
		return ContinueDialogAsync(outerDc, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext outerDc, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext outerDc )
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
		var turnResult = await OnContinueDialogAsync(innerDc).get();

		if (turnResult.Status != DialogTurnStatus.Waiting)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await EndComponentAsync(outerDc, turnResult.Result).get();
		}
		else
		{
			return Dialog.EndOfTurn;
		}
	}


	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason, Object result)
	{
		return ResumeDialogAsync(outerDc, reason, result, null);
	}

	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason)
	{
		return ResumeDialogAsync(outerDc, reason, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason, object result = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason, Object result )
	{
		// Containers are typically leaf nodes on the stack but the dev is free to push other dialogs
		// on top of the stack which will result in the container receiving an unexpected call to
		// dialogResume() when the pushed on dialog ends.
		// To avoid the container prematurely ending we need to implement this method and simply
		// ask our inner dialog stack to re-prompt.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await RepromptDialogAsync(outerDc.getContext(), outerDc.getActiveDialog()).get();
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
		// Delegate to inner dialog.
		DialogState dialogState = (DialogState)instance.getState().get(PersistedDialogState);
		DialogContext innerDc = new DialogContext(_dialogs, turnContext, dialogState);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await innerDc.RepromptDialogAsync(cancellationToken).get();

		// Notify component
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await OnRepromptDialogAsync(turnContext, instance).get();
	}


	@Override
	public CompletableFuture EndDialogAsync(TurnContext turnContext, DialogInstance instance, DialogReason reason)
	{
		return EndDialogAsync(turnContext, instance, reason, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture EndDialogAsync(TurnContext turnContext, DialogInstance instance, DialogReason reason, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture EndDialogAsync(TurnContext turnContext, DialogInstance instance, DialogReason reason )
	{
		// Forward cancel to inner dialogs
		if (reason == DialogReason.CancelCalled)
		{
			DialogState dialogState = (DialogState)instance.getState().get(PersistedDialogState);
			DialogContext innerDc = new DialogContext(_dialogs, turnContext, dialogState);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await innerDc.CancelAllDialogsAsync(cancellationToken).get();
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await OnEndDialogAsync(turnContext, instance, reason).get();
	}

	/** 
	 Adds a dialog to the component dialog.
	 
	 @param dialog The dialog to add.
	 @return The updated <see cref="ComponentDialog"/>.
	*/
	public final ComponentDialog AddDialog(Dialog dialog)
	{
		_dialogs.Add(dialog);
		if (StringUtils.isBlank(getInitialDialogId()))
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


	protected CompletableFuture<DialogTurnResult> OnBeginDialogAsync(DialogContext innerDc, Object options)
	{
		return OnBeginDialogAsync(innerDc, options, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual CompletableFuture<DialogTurnResult> OnBeginDialogAsync(DialogContext innerDc, object options, CancellationToken cancellationToken = default(CancellationToken))
	protected CompletableFuture<DialogTurnResult> OnBeginDialogAsync(DialogContext innerDc, Object options )
	{
		return innerDc.BeginDialogAsync(getInitialDialogId(), options);
	}


	protected CompletableFuture<DialogTurnResult> OnContinueDialogAsync(DialogContext innerDc)
	{
		return OnContinueDialogAsync(innerDc, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual CompletableFuture<DialogTurnResult> OnContinueDialogAsync(DialogContext innerDc, CancellationToken cancellationToken = default(CancellationToken))
	protected CompletableFuture<DialogTurnResult> OnContinueDialogAsync(DialogContext innerDc )
	{
		return innerDc.ContinueDialogAsync(cancellationToken);
	}


	protected CompletableFuture OnEndDialogAsync(TurnContext context, DialogInstance instance, DialogReason reason)
	{
		return OnEndDialogAsync(context, instance, reason, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual CompletableFuture OnEndDialogAsync(TurnContext context, DialogInstance instance, DialogReason reason, CancellationToken cancellationToken = default(CancellationToken))
	protected CompletableFuture OnEndDialogAsync(TurnContext context, DialogInstance instance, DialogReason reason )
	{
		return Task.CompletedTask;
	}


	protected CompletableFuture OnRepromptDialogAsync(TurnContext turnContext, DialogInstance instance)
	{
		return OnRepromptDialogAsync(turnContext, instance, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected virtual CompletableFuture OnRepromptDialogAsync(TurnContext turnContext, DialogInstance instance, CancellationToken cancellationToken = default(CancellationToken))
	protected CompletableFuture OnRepromptDialogAsync(TurnContext turnContext, DialogInstance instance )
	{
		return Task.CompletedTask;
	}

	protected CompletableFuture<DialogTurnResult> EndComponentAsync(DialogContext outerDc, Object result )
	{
		return outerDc.EndDialogAsync(result);
	}
}