// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.TurnContext;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 Base class for all dialogs.
*/
public abstract class Dialog
{
	public static final DialogTurnResult EndOfTurn = new DialogTurnResult(DialogTurnStatus.Waiting);

	public Dialog(String dialogId)
	{
		if (StringUtils.isBlank(dialogId))
		{
			throw new NullPointerException("dialogId");
		}

		Id = dialogId;
	}

	private String Id;
	public final String getId()
	{
		return Id;
	}

	/** 
	 Method called when a new dialog has been pushed onto the stack and is being activated.
	 
	 @param dc The dialog context for the current turn of conversation.
	 @param options (Optional) arguments that were passed to the dialog during `begin()` call that started the instance.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/
	public abstract CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options);

    /**
     Method called when a new dialog has been pushed onto the stack and is being activated.

     @param dc The dialog context for the current turn of conversation.
     @return A <see cref="Task"/> representing the asynchronous operation.
     */
	public abstract CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc);


	/** 
	 Method called when an instance of the dialog is the "current" dialog and the
	 user replies with a new activity. The dialog will generally continue to receive the users
	 replies until it calls either `DialogSet.end()` or `DialogSet.begin()`.
	 If this method is NOT implemented then the dialog will automatically be ended when the user replies.
	 
	 @param dc The dialog context for the current turn of conversation.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc )
	{
	    return CompletableFuture.supplyAsync(() -> {
            // By default just end the current dialog.

            try {
                return dc.EndDialogAsync().get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
	}


    /**
     Method called when an instance of the dialog is being returned to from another
     dialog that was started by the current instance using `DialogSet.begin()`.
     If this method is NOT implemented then the dialog will be automatically ended with a call
     to `DialogSet.endDialogWithResult()`. Any result passed from the called dialog will be passed
     to the current dialogs parent.

     @param dc The dialog context for the current turn of conversation.
     @param reason Reason why the dialog resumed.
     @return A <see cref="Task"/> representing the asynchronous operation.
     */
    public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason)
	{
		return ResumeDialogAsync(dc, reason, null);
	}


    /**
     Method called when an instance of the dialog is being returned to from another
     dialog that was started by the current instance using `DialogSet.begin()`.
     If this method is NOT implemented then the dialog will be automatically ended with a call
     to `DialogSet.endDialogWithResult()`. Any result passed from the called dialog will be passed
     to the current dialogs parent.

     @param dc The dialog context for the current turn of conversation.
     @param reason Reason why the dialog resumed.
     @param result (Optional) value returned from the dialog that was called. The type of the value returned is dependant on the dialog that was called.
     @return A <see cref="Task"/> representing the asynchronous operation.
     */
    public CompletableFuture<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result )
	{
	    return CompletableFuture.supplyAsync(() -> {
            // By default just end the current dialog and return result to parent.

            try {
                return dc.EndDialogAsync(result).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
    }

	public CompletableFuture RepromptDialogAsync(TurnContext turnContext, DialogInstance instance )
	{
		// No-op by default
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture EndDialogAsync(TurnContext turnContext, DialogInstance instance, DialogReason reason )
	{
		// No-op by default
		return CompletableFuture.completedFuture(null);
	}
}