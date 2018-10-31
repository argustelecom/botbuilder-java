// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.BotAssert;
import com.microsoft.bot.builder.StatePropertyAccessor;
import com.microsoft.bot.builder.TurnContext;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 A related set of dialogs that can all call each other.
*/
public class DialogSet
{
	private StatePropertyAccessor<DialogState> _dialogState;
	private final Map<String, Dialog> _dialogs = new HashMap<String, Dialog>();

	public DialogSet(StatePropertyAccessor<DialogState> dialogState)
	{
	    if (dialogState == null)
        {
            throw new NullPointerException(String.format("missing %1$s", "dialogState"));
        }
		_dialogState = dialogState;
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

	public final CompletableFuture<DialogContext> CreateContextAsync(TurnContext turnContext )
	{
		BotAssert.ContextNotNull(turnContext);

		// ToDo: Component Dialog doesn't call this code path. This needs to be cleaned up in 4.1.
		if (_dialogState == null)
		{
			// Note: This shouldn't ever trigger, as the _dialogState is set in the constructor and validated there.
			throw new IllegalStateException(String.format("DialogSet.CreateContextAsync(): DialogSet created with a null IStatePropertyAccessor."));
		}

		// Load/initialize dialog state
		Object state = _dialogState.GetAsync(turnContext, () ->
		{
				return new DialogState();
		}).get();

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
		if (StringUtils.isBlank(dialogId))
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