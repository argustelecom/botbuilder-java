package Microsoft.Bot.Builder.Dialogs;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class DialogContext
{
	/** 
	 Initializes a new instance of the <see cref="DialogContext"/> class.
	 
	 @param dialogs Parent dialog set.
	 @param turnContext Context for the current turn of conversation with the user.
	 @param state Current dialog state.
	*/
	public DialogContext(DialogSet dialogs, ITurnContext turnContext, DialogState state)
	{
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: Dialogs = dialogs ?? throw new ArgumentNullException(nameof(dialogs));
		setDialogs((dialogs != null) ? dialogs : throw new NullPointerException("dialogs"));
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: Context = turnContext ?? throw new ArgumentNullException(nameof(turnContext));
		setContext((turnContext != null) ? turnContext : throw new NullPointerException("turnContext"));

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

	private ITurnContext Context;
	public final ITurnContext getContext()
	{
		return Context;
	}
	private void setContext(ITurnContext value)
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
		if (getStack().Any())
		{
			return getStack().get(0);
		}

		return null;
	}

	/** 
	 Pushes a new dialog onto the dialog stack.
	 
	 @param dialogId ID of the dialog to start.
	 @param options (Optional) additional argument(s) to pass to the dialog being started.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task<DialogTurnResult> BeginDialogAsync(String dialogId, Object options)
	{
		return BeginDialogAsync(dialogId, options, null);
	}

	public final Task<DialogTurnResult> BeginDialogAsync(String dialogId)
	{
		return BeginDialogAsync(dialogId, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<DialogTurnResult> BeginDialogAsync(string dialogId, object options = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<DialogTurnResult> BeginDialogAsync(String dialogId, Object options, CancellationToken cancellationToken)
	{
		if (tangible.StringHelper.isNullOrEmpty(dialogId))
		{
			throw new NullPointerException("dialogId");
		}

		// Lookup dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var dialog = getDialogs().Find(dialogId);
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await dialog.BeginDialogAsync(this, options, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Helper function to simplify formatting the options for calling a prompt dialog. This helper will
	 take a `PromptOptions` argument and then call[begin(context, dialogId, options)](#begin).
	 
	 @param dialogId ID of the prompt to start.
	 @param options Contains a Prompt, potentially a RetryPrompt and if using ChoicePrompt, Choices.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task<DialogTurnResult> PromptAsync(String dialogId, PromptOptions options)
	{
		return PromptAsync(dialogId, options, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<DialogTurnResult> PromptAsync(string dialogId, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<DialogTurnResult> PromptAsync(String dialogId, PromptOptions options, CancellationToken cancellationToken)
	{
		if (tangible.StringHelper.isNullOrEmpty(dialogId))
		{
			throw new NullPointerException("dialogId");
		}

		if (options == null)
		{
			throw new NullPointerException("options");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await BeginDialogAsync(dialogId, options, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Continues execution of the active dialog, if there is one, by passing the context object to
	 its `Dialog.ContinueDialogAsync()` method. You can check `context.responded` after the call completes
	 to determine if a dialog was run and a reply was sent to the user.
	 
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task<DialogTurnResult> ContinueDialogAsync()
	{
		return ContinueDialogAsync(null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<DialogTurnResult> ContinueDialogAsync(CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<DialogTurnResult> ContinueDialogAsync(CancellationToken cancellationToken)
	{
		// Check for a dialog on the stack
		if (getActiveDialog() != null)
		{
			// Lookup dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var dialog = getDialogs().Find(getActiveDialog().getId());
			if (dialog == null)
			{
				throw new RuntimeException(String.format("DialogContext.ContinueDialogAsync(): Can't continue dialog. A dialog with an id of '%1$s' wasn't found.", getActiveDialog().getId()));
			}

			// Continue execution of dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await dialog.ContinueDialogAsync(this, cancellationToken).ConfigureAwait(false);
		}
		else
		{
			return new DialogTurnResult(DialogTurnStatus.Empty);
		}
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
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task<DialogTurnResult> EndDialogAsync(Object result)
	{
		return EndDialogAsync(result, null);
	}

	public final Task<DialogTurnResult> EndDialogAsync()
	{
		return EndDialogAsync(null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<DialogTurnResult> EndDialogAsync(object result = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<DialogTurnResult> EndDialogAsync(Object result, CancellationToken cancellationToken)
	{
		// Pop active dialog off the stack
		if (getStack().Any())
		{
			getStack().remove(0);
		}

		// Resume previous dialog
		if (getActiveDialog() != null)
		{
			// Lookup dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var dialog = getDialogs().Find(getActiveDialog().getId());
			if (dialog == null)
			{
				throw new RuntimeException(String.format("DialogContext.EndDialogAsync(): Can't resume previous dialog. A dialog with an id of '%1$s' wasn't found.", getActiveDialog().getId()));
			}

			// Return result to previous dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await dialog.ResumeDialogAsync(this, DialogReason.EndCalled, result, cancellationToken).ConfigureAwait(false);
		}
		else
		{
			return new DialogTurnResult(DialogTurnStatus.Complete, result);
		}
	}

	/** 
	 Deletes any existing dialog stack thus cancelling all dialogs on the stack.
	 
	 @param cancellationToken The cancellation token.
	 @return The dialog context.
	*/

	public final Task<DialogTurnResult> CancelAllDialogsAsync()
	{
		return CancelAllDialogsAsync(null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<DialogTurnResult> CancelAllDialogsAsync(CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<DialogTurnResult> CancelAllDialogsAsync(CancellationToken cancellationToken)
	{
		if (getStack().Any())
		{
			while (getStack().Any())
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await EndActiveDialogAsync(DialogReason.CancelCalled, cancellationToken).ConfigureAwait(false);
			}

			return new DialogTurnResult(DialogTurnStatus.Cancelled);
		}
		else
		{
			return new DialogTurnResult(DialogTurnStatus.Empty);
		}
	}

	/** 
	 Ends the active dialog and starts a new dialog in its place. This is particularly useful
	 for creating loops or redirecting to another dialog.
	 
	 @param dialogId ID of the new dialog to start.
	 @param options (Optional) additional argument(s) to pass to the new dialog.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task<DialogTurnResult> ReplaceDialogAsync(String dialogId, Object options)
	{
		return ReplaceDialogAsync(dialogId, options, null);
	}

	public final Task<DialogTurnResult> ReplaceDialogAsync(String dialogId)
	{
		return ReplaceDialogAsync(dialogId, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<DialogTurnResult> ReplaceDialogAsync(string dialogId, object options = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<DialogTurnResult> ReplaceDialogAsync(String dialogId, Object options, CancellationToken cancellationToken)
	{
		// Pop stack
		if (getStack().Any())
		{
			getStack().remove(0);
		}

		// Start replacement dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await BeginDialogAsync(dialogId, options, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Calls reprompt on the currently active dialog, if there is one. Used with Prompts that have a reprompt behavior.
	 
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task RepromptDialogAsync()
	{
		return RepromptDialogAsync(null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task RepromptDialogAsync(CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task RepromptDialogAsync(CancellationToken cancellationToken)
	{
		// Check for a dialog on the stack
		if (getActiveDialog() != null)
		{
			// Lookup dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var dialog = getDialogs().Find(getActiveDialog().getId());
			if (dialog == null)
			{
				throw new RuntimeException(String.format("DialogSet.RepromptDialogAsync(): Can't find A dialog with an id of '%1$s'.", getActiveDialog().getId()));
			}

			// Ask dialog to re-prompt if supported
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await dialog.RepromptDialogAsync(getContext(), getActiveDialog(), cancellationToken).ConfigureAwait(false);
		}
	}


	private Task EndActiveDialogAsync(DialogReason reason)
	{
		return EndActiveDialogAsync(reason, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async Task EndActiveDialogAsync(DialogReason reason, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	private Task EndActiveDialogAsync(DialogReason reason, CancellationToken cancellationToken)
	{
		Microsoft.Bot.Builder.Dialogs.DialogInstance instance = getActiveDialog();
		if (instance != null)
		{
			// Lookup dialog
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var dialog = getDialogs().Find(instance.getId());
			if (dialog != null)
			{
				// Notify dialog of end
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await dialog.EndDialogAsync(getContext(), instance, reason, cancellationToken).ConfigureAwait(false);
			}

			// Pop dialog off stack
			getStack().remove(0);
		}
	}
}