package Microsoft.Bot.Builder.Dialogs;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 A related set of dialogs that can all call each other.
*/
public class DialogSet
{
	private IStatePropertyAccessor<DialogState> _dialogState;
	private final Map<String, Dialog> _dialogs = new HashMap<String, Dialog>();

	public DialogSet(IStatePropertyAccessor<DialogState> dialogState)
	{
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: _dialogState = dialogState ?? throw new ArgumentNullException(string.Format("missing {0}", nameof(dialogState)));
		_dialogState = (dialogState != null) ? dialogState : throw new NullPointerException(String.format("missing %1$s", "dialogState"));
	}

	public DialogSet()
	{
		// TODO: This is only used by ComponentDialog and future release
		// will refactor to use IStatePropertyAccessor from context
		_dialogState = null;
	}

	/** 
	 Adds a new dialog to the set and returns the added dialog.
	 
	 @param dialog The dialog to add.
	 @return The DialogSet for fluent calls to Add().
	*/
	public final DialogSet Add(Dialog dialog)
	{
		if (dialog == null)
		{
			throw new NullPointerException("dialog");
		}

		if (_dialogs.containsKey(dialog.getId()))
		{
			throw new IllegalArgumentException(String.format("DialogSet.Add(): A dialog with an id of '%1$s' already added.", dialog.getId()));
		}

		_dialogs.put(dialog.getId(), dialog);
		return this;
	}


	public final Task<DialogContext> CreateContextAsync(ITurnContext turnContext)
	{
		return CreateContextAsync(turnContext, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<DialogContext> CreateContextAsync(ITurnContext turnContext, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task<DialogContext> CreateContextAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	{
		BotAssert.ContextNotNull(turnContext);

		// ToDo: Component Dialog doesn't call this code path. This needs to be cleaned up in 4.1.
		if (_dialogState == null)
		{
			// Note: This shouldn't ever trigger, as the _dialogState is set in the constructor and validated there.
			throw new IllegalStateException(String.format("DialogSet.CreateContextAsync(): DialogSet created with a null IStatePropertyAccessor."));
		}

		// Load/initialize dialog state
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var state = await _dialogState.GetAsync(turnContext, () ->
		{
				return new DialogState();
		}, cancellationToken).ConfigureAwait(false);

		// Create and return context
		return new DialogContext(this, turnContext, state);
	}

	/** 
	 Finds a dialog that was previously added to the set using [add()](#add).
	 
	 @param dialogId ID of the dialog/prompt to lookup.
	 @return dialog if found otherwise null.
	*/
	public final Dialog Find(String dialogId)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(dialogId))
		{
			throw new NullPointerException("dialogId");
		}

		TValue result;
		if (_dialogs.containsKey(dialogId) ? (result = _dialogs.get(dialogId)) == result : false)
		{
			return result;
		}

		return null;
	}
}