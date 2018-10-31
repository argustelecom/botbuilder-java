// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import com.microsoft.bot.builder.TurnContext;
import org.apache.commons.lang3.StringUtils;

public class DialogContext
{
	/** 
	 Initializes a new instance of the <see cref="DialogContext"/> class.
	 
	 @param dialogs Parent dialog set.
	 @param turnContext Context for the current turn of conversation with the user.
	 @param state Current dialog state.
	*/
	public DialogContext(DialogSet dialogs, TurnContext turnContext, DialogState state)
	{
	    if (dialogs == null)
        {
            throw new NullPointerException("dialogs");
        }
        if (turnContext == null)
        {
            throw new NullPointerException("turnContext");
        }
		setDialogs(dialogs);
		setContext(turnContext);

		setStack(state.getDialogStack());
	}

	private DialogSet Dialogs;
	public final DialogSet getDialogs()
	{
		return Dialogs;
	}
	private void setDialogs(DialogSet value)
	{
		Dialogs = value;
	}

	private TurnContext Context;
	public final TurnContext getContext()
	{
		return Context;
	}
	private void setContext(TurnContext value)
	{
		Context = value;
	}

	private ArrayList<DialogInstance> Stack;
	public final ArrayList<DialogInstance> getStack()
	{
		return Stack;
	}
	private void setStack(ArrayList<DialogInstance> value)
	{
		Stack = value;
	}

	/** 
	 Gets the cached instance of the active dialog on the top of the stack or <c>null</c> if the stack is empty.
	 
	 <value>
	 The cached instance of the active dialog on the top of the stack or <c>null</c> if the stack is empty.
	 </value>
	*/
	public final DialogInstance getActiveDialog()
	{
		if (!getStack().isEmpty())
		{
			return getStack().get(0);
		}

		return null;
	}

    /**
     Pushes a new dialog onto the dialog stack.

     @param dialogId ID of the dialog to start.
     @return A <see cref="Task"/> representing the asynchronous operation.
     */
    public final CompletableFuture<DialogTurnResult> BeginDialogAsync(String dialogId)
    {
        return BeginDialogAsync(dialogId, null);
    }

	/** 
	 Pushes a new dialog onto the dialog stack.
	 
	 @param dialogId ID of the dialog to start.
	 @param options (Optional) additional argument(s) to pass to the dialog being started.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/
	public final CompletableFuture<DialogTurnResult> BeginDialogAsync(String dialogId, Object options)
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (StringUtils.isBlank(dialogId))
            {
                throw new NullPointerException("dialogId");
            }

            // Lookup dialog
            Dialog dialog = getDialogs().Find(dialogId);
            if (dialog == null)
            {
                throw new RuntimeException(String.format("DialogContext.BeginDialogAsync(): A dialog with an id of '%1$s' wasn't found.", dialogId));
            }

            // Push new instance onto stack.
            DialogInstance instance = new DialogInstance();
            instance.setId(dialogId);
            instance.setState(new HashMap<String, Object>());

            getStack().add(0, instance);

            // Call dialogs BeginAsync() method.

            try {
                return dialog.BeginDialogAsync(this, options).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, getContext().executorService());
	}

	/** 
	 Helper function to simplify formatting the options for calling a prompt dialog. This helper will
	 take a `PromptOptions` argument and then call[begin(context, dialogId, options)](#begin).
	 
	 @param dialogId ID of the prompt to start.
	 @param options Contains a Prompt, potentially a RetryPrompt and if using ChoicePrompt, Choices.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/
	public final CompletableFuture<DialogTurnResult> PromptAsync(String dialogId, PromptOptions options )
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (StringUtils.isBlank(dialogId))
            {
                throw new NullPointerException("dialogId");
            }

            if (options == null)
            {
                throw new NullPointerException("options");
            }

            try {
                return BeginDialogAsync(dialogId, options).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, getContext().executorService());
	}

	/** 
	 Continues execution of the active dialog, if there is one, by passing the context object to
	 its `Dialog.ContinueDialogAsync()` method. You can check `context.responded` after the call completes
	 to determine if a dialog was run and a reply was sent to the user.
	 
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/
	public final CompletableFuture<DialogTurnResult> ContinueDialogAsync()
	{
	    return CompletableFuture.supplyAsync(() -> {
            // Check for a dialog on the stack
            if (getActiveDialog() != null)
            {
                // Lookup dialog
                Dialog dialog = getDialogs().Find(getActiveDialog().getId());
                if (dialog == null)
                {
                    throw new RuntimeException(String.format("DialogContext.ContinueDialogAsync(): Can't continue dialog. A dialog with an id of '%1$s' wasn't found.", getActiveDialog().getId()));
                }

                // Continue execution of dialog

                return dialog.ContinueDialogAsync(this).get();
            }
            else
            {
                return new DialogTurnResult(DialogTurnStatus.Empty);
            }

        }, getContext().executorService());
	}


	/**
	 Ends a dialog by popping it off the stack and returns an optional result to the dialogs
	 parent.The parent dialog is the dialog the started the on being ended via a call to
	 either[begin()](#begin) or [prompt()](#prompt).
	 The parent dialog will have its `Dialog.resume()` method invoked with any returned
	 result. If the parent dialog hasn't implemented a `resume()` method then it will be
	 automatically ended as well and the result passed to its parent. If there are no more
	 parent dialogs on the stack then processing of the turn will end.

	 @return A <see cref="Task"/> representing the asynchronous operation.
	 */
	public final CompletableFuture<DialogTurnResult> EndDialogAsync()
	{
		return EndDialogAsync(null);
	}

	/**
	 Ends a dialog by popping it off the stack and returns an optional result to the dialogs
	 parent.The parent dialog is the dialog the started the on being ended via a call to
	 either[begin()](#begin) or [prompt()](#prompt).
	 The parent dialog will have its `Dialog.resume()` method invoked with any returned
	 result. If the parent dialog hasn't implemented a `resume()` method then it will be
	 automatically ended as well and the result passed to its parent. If there are no more
	 parent dialogs on the stack then processing of the turn will end.

	 @param result (Optional) result to pass to the parent dialogs.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	 */
	public final CompletableFuture<DialogTurnResult> EndDialogAsync(Object result )
	{
	    return CompletableFuture.supplyAsync(() -> {
            // Pop active dialog off the stack
            if (!getStack().isEmpty())
            {
                getStack().remove(0);
            }

            // Resume previous dialog
            if (getActiveDialog() != null)
            {
                // Lookup dialog
                Dialog dialog = getDialogs().Find(getActiveDialog().getId());
                if (dialog == null)
                {
                    throw new RuntimeException(String.format("DialogContext.EndDialogAsync(): Can't resume previous dialog. A dialog with an id of '%1$s' wasn't found.", getActiveDialog().getId()));
                }

                // Return result to previous dialog

                return dialog.ResumeDialogAsync(this, DialogReason.EndCalled, result).get();
            }
            else
            {
                return new DialogTurnResult(DialogTurnStatus.Complete, result);
            }

        }, getContext().executorService());
	}

	/** 
	 Deletes any existing dialog stack thus cancelling all dialogs on the stack.
	 
	 @return The dialog context.
	*/
	public final CompletableFuture<DialogTurnResult> CancelAllDialogsAsync()
	{
	    return CompletableFuture.supplyAsync(() -> {
            if (!getStack().isEmpty())
            {
                while (!getStack().isEmpty())
                {
                    try {
                        EndActiveDialogAsync(DialogReason.CancelCalled).get();
                    } catch (InterruptedException|ExecutionException e) {
                        e.printStackTrace();
                        throw new CompletionException(e);
                    }
                }
                return new DialogTurnResult(DialogTurnStatus.Cancelled);
            }
            else
            {
                return new DialogTurnResult(DialogTurnStatus.Empty);
            }
        }, getContext().executorService());
	}


	/**
	 Ends the active dialog and starts a new dialog in its place. This is particularly useful
	 for creating loops or redirecting to another dialog.

	 @param dialogId ID of the new dialog to start.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	 */
	public final CompletableFuture<DialogTurnResult> ReplaceDialogAsync(String dialogId)
	{
		return ReplaceDialogAsync(dialogId, null);
	}

	/**
	 Ends the active dialog and starts a new dialog in its place. This is particularly useful
	 for creating loops or redirecting to another dialog.

	 @param dialogId ID of the new dialog to start.
	 @param options (Optional) additional argument(s) to pass to the new dialog.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	 */
	public final CompletableFuture<DialogTurnResult> ReplaceDialogAsync(String dialogId, Object options )
	{
	    return CompletableFuture.supplyAsync(() -> {
            // Pop stack
            if (!getStack().isEmpty())
            {
                getStack().remove(0);
            }

            // Start replacement dialog
            try {
                return BeginDialogAsync(dialogId, options).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, getContext().executorService());
	}

	/** 
	 Calls reprompt on the currently active dialog, if there is one. Used with Prompts that have a reprompt behavior.
	 
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/
	public final CompletableFuture RepromptDialogAsync()
	{
	    return CompletableFuture.runAsync(() -> {
            // Check for a dialog on the stack
            if (getActiveDialog() != null)
            {
                // Lookup dialog
                Dialog dialog = getDialogs().Find(getActiveDialog().getId());
                if (dialog == null)
                {
                    throw new RuntimeException(String.format("DialogSet.RepromptDialogAsync(): Can't find A dialog with an id of '%1$s'.", getActiveDialog().getId()));
                }

                // Ask dialog to re-prompt if supported
                try {
                    dialog.RepromptDialogAsync(getContext(), getActiveDialog()).get();
                } catch (InterruptedException|ExecutionException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
            }
        }, getContext().executorService());
	}


	private CompletableFuture EndActiveDialogAsync(DialogReason reason)
	{
	    return CompletableFuture.runAsync(() -> {
            DialogInstance instance = getActiveDialog();
            if (instance != null)
            {
                // Lookup dialog
                Dialog dialog = getDialogs().Find(instance.getId());
                if (dialog != null)
                {
                    // Notify dialog of end

                    try {
                        dialog.EndDialogAsync(getContext(), instance, reason).get();
                    } catch (InterruptedException|ExecutionException e) {
                        e.printStackTrace();
                        throw new CompletionException(e);
                    }
                }

                // Pop dialog off stack
                getStack().remove(0);
            }

        }, getContext().executorService());
	}
}