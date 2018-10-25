package Microsoft.Bot.Builder.Dialogs;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Base class for all dialogs.
*/
public abstract class Dialog
{
	public static final DialogTurnResult EndOfTurn = new DialogTurnResult(DialogTurnStatus.Waiting);

	public Dialog(String dialogId)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(dialogId))
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
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final abstract Task<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options);
	public final abstract Task<DialogTurnResult> BeginDialogAsync(DialogContext dc);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public abstract Task<DialogTurnResult> BeginDialogAsync(DialogContext dc, object options = null, CancellationToken cancellationToken = default(CancellationToken));
	public abstract Task<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options, CancellationToken cancellationToken);

	/** 
	 Method called when an instance of the dialog is the "current" dialog and the
	 user replies with a new activity. The dialog will generally continue to receive the users
	 replies until it calls either `DialogSet.end()` or `DialogSet.begin()`.
	 If this method is NOT implemented then the dialog will automatically be ended when the user replies.
	 
	 @param dc The dialog context for the current turn of conversation.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public Task<DialogTurnResult> ContinueDialogAsync(DialogContext dc)
	{
		return ContinueDialogAsync(dc, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public virtual async Task<DialogTurnResult> ContinueDialogAsync(DialogContext dc, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public Task<DialogTurnResult> ContinueDialogAsync(DialogContext dc, CancellationToken cancellationToken)
	{
		// By default just end the current dialog.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await dc.EndDialogAsync(cancellationToken).ConfigureAwait(false);
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
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result)
	{
		return ResumeDialogAsync(dc, reason, result, null);
	}

	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason)
	{
		return ResumeDialogAsync(dc, reason, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public virtual async Task<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, object result = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public Task<DialogTurnResult> ResumeDialogAsync(DialogContext dc, DialogReason reason, Object result, CancellationToken cancellationToken)
	{
		// By default just end the current dialog and return result to parent.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await dc.EndDialogAsync(result, cancellationToken).ConfigureAwait(false);
	}


	public Task RepromptDialogAsync(ITurnContext turnContext, DialogInstance instance)
	{
		return RepromptDialogAsync(turnContext, instance, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public virtual Task RepromptDialogAsync(ITurnContext turnContext, DialogInstance instance, CancellationToken cancellationToken = default(CancellationToken))
	public Task RepromptDialogAsync(ITurnContext turnContext, DialogInstance instance, CancellationToken cancellationToken)
	{
		// No-op by default
		return Task.CompletedTask;
	}


	public Task EndDialogAsync(ITurnContext turnContext, DialogInstance instance, DialogReason reason)
	{
		return EndDialogAsync(turnContext, instance, reason, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public virtual Task EndDialogAsync(ITurnContext turnContext, DialogInstance instance, DialogReason reason, CancellationToken cancellationToken = default(CancellationToken))
	public Task EndDialogAsync(ITurnContext turnContext, DialogInstance instance, DialogReason reason, CancellationToken cancellationToken)
	{
		// No-op by default
		return Task.CompletedTask;
	}
}