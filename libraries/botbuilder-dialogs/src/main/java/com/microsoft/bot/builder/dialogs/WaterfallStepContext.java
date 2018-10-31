// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class WaterfallStepContext extends DialogContext
{
	private WaterfallDialog _parent;
	private boolean _nextCalled;


	public WaterfallStepContext(WaterfallDialog parent, DialogContext dc, Object options, java.util.Map<String, Object> values, int index, DialogReason reason)
	{
		this(parent, dc, options, values, index, reason, null);
	}

	public WaterfallStepContext(WaterfallDialog parent, DialogContext dc, Object options, Map<String, Object> values, int index, DialogReason reason, Object result)
	{
		super(dc.getDialogs(), dc.getContext(), new DialogState(dc.getStack()));
		_parent = parent;
		_nextCalled = false;
		Index = index;
		Options = options;
		Reason = reason;
		Result = result;
		Values = values;
	}

	/** 
	 Gets the index of the current waterfall step being executed.
	*/
	private int Index;
	public final int getIndex()
	{
		return Index;
	}

	/** 
	 Gets any options the waterfall dialog was called with.
	*/
	private Object Options;
	public final Object getOptions()
	{
		return Options;
	}

	/** 
	 Gets the reason the waterfall step is being executed.
	*/
	private DialogReason Reason = DialogReason.values()[0];
	public final DialogReason getReason()
	{
		return Reason;
	}

	/** 
	 Gets results returned by a dialog called in the previous waterfall step.
	*/
	private Object Result;
	public final Object getResult()
	{
		return Result;
	}

	/** 
	 Gets a dictionary of values which will be persisted across all waterfall steps.
	*/
	private Map<String, Object> Values;
	public final Map<String, Object> getValues()
	{
		return Values;
	}

	/** 
	 Used to skip to the next waterfall step.
	 
	 @return A <see cref="Task"/> of <see cref="DialogTurnResult"/> representing the asynchronous operation.
	*/
	public final CompletableFuture<DialogTurnResult> NextAsync()
	{
		return NextAsync(null);
	}

    /**
     Used to skip to the next waterfall step.

     @param result Optional result to pass to next step.
     @return A <see cref="Task"/> of <see cref="DialogTurnResult"/> representing the asynchronous operation.
     */
	public final CompletableFuture<DialogTurnResult> NextAsync(Object result )
	{
		// Ensure next hasn't been called
		if (_nextCalled)
		{
			throw new RuntimeException(String.format("WaterfallStepContext.NextAsync(): method already called for dialog and step '%1$s[%2$s]'.", _parent.getId(), getIndex()));
		}

		// Trigger next step
		_nextCalled = true;

		return _parent.ResumeDialogAsync(this, DialogReason.NextCalled, result).get();
	}
}