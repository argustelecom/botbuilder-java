// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.TurnContext;
import org.apache.commons.lang3.StringUtils;
import java.util.concurrent.CompletableFuture;

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
		DialogTurnResult turnResult = OnBeginDialogAsync(innerDc, options).get();

		// Check for end of inner dialog
		if (turnResult.getStatus() != DialogTurnStatus.Waiting)
		{
			// Return result to calling dialog

			return EndComponentAsync(outerDc, ((DialogTurnResult) turnResult).getResult()).get();
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

		var turnResult = OnContinueDialogAsync(innerDc).get();

		if (turnResult.Status != DialogTurnStatus.Waiting)
		{

			return EndComponentAsync(outerDc, turnResult.Result).get();
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

	@Override
	public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext outerDc, DialogReason reason, Object result )
	{
		// Containers are typically leaf nodes on the stack but the dev is free to push other dialogs
		// on top of the stack which will result in the container receiving an unexpected call to
		// dialogResume() when the pushed on dialog ends.
		// To avoid the container prematurely ending we need to implement this method and simply
		// ask our inner dialog stack to re-prompt.
		RepromptDialogAsync(outerDc.getContext(), outerDc.getActiveDialog()).get();
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
		// Delegate to inner dialog.
		DialogState dialogState = (DialogState)instance.getState().get(PersistedDialogState);
		DialogContext innerDc = new DialogContext(_dialogs, turnContext, dialogState);

		innerDc.RepromptDialogAsync().get();

		// Notify component

		OnRepromptDialogAsync(turnContext, instance).get();
	}


	@Override
	public CompletableFuture EndDialogAsync(TurnContext turnContext, DialogInstance instance, DialogReason reason)
	{
		return EndDialogAsync(turnContext, instance, reason, null);
	}


	@Override
	public CompletableFuture EndDialogAsync(TurnContext turnContext, DialogInstance instance, DialogReason reason )
	{
		// Forward cancel to inner dialogs
		if (reason == DialogReason.CancelCalled)
		{
			DialogState dialogState = (DialogState)instance.getState().get(PersistedDialogState);
			DialogContext innerDc = new DialogContext(_dialogs, turnContext, dialogState);
			innerDc.CancelAllDialogsAsync().get();
		}

		OnEndDialogAsync(turnContext, instance, reason).get();
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

	protected CompletableFuture<DialogTurnResult> OnBeginDialogAsync(DialogContext innerDc, Object options )
	{
		return innerDc.BeginDialogAsync(getInitialDialogId(), options);
	}


	protected CompletableFuture<DialogTurnResult> OnContinueDialogAsync(DialogContext innerDc)
	{
		return OnContinueDialogAsync(innerDc, null);
	}

	protected CompletableFuture<DialogTurnResult> OnContinueDialogAsync(DialogContext innerDc )
	{
		return innerDc.ContinueDialogAsync();
	}


	protected CompletableFuture OnEndDialogAsync(TurnContext context, DialogInstance instance, DialogReason reason )
	{
		return CompletableFuture.completedFuture(null);
	}


	protected CompletableFuture OnRepromptDialogAsync(TurnContext turnContext, DialogInstance instance)
	{
		return OnRepromptDialogAsync(turnContext, instance, null);
	}

	protected CompletableFuture OnRepromptDialogAsync(TurnContext turnContext, DialogInstance instance )
	{
		return CompletableFuture.completedFuture(null);
	}

	protected CompletableFuture<DialogTurnResult> EndComponentAsync(DialogContext outerDc, Object result )
	{
		return outerDc.EndDialogAsync(result);
	}
}