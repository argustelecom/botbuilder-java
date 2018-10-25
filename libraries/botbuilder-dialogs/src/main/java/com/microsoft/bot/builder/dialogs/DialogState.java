// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;



public class DialogState
{
	public DialogState()
	{
		this(null);
	}

	public DialogState(ArrayList<DialogInstance> stack)
	{
		DialogStack = (stack != null) ? stack : new ArrayList<DialogInstance>();
	}

	private ArrayList<DialogInstance> DialogStack;
	public final ArrayList<DialogInstance> getDialogStack()
	{
		return DialogStack;
	}
}