// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;


public enum DialogTurnStatus
{
	/** 
	 Indicates that there is currently nothing on the dialog stack.
	*/
	Empty,

	/** 
	 Indicates that the dialog on top is waiting for a response from the user.
	*/
	Waiting,

	/** 
	 Indicates that the dialog completed successfully, the result is available, and the stack is empty.
	*/
	Complete,

	/** 
	 Indicates that the dialog was cancelled and the stack is empty.
	*/
	Cancelled;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static DialogTurnStatus forValue(int value)
	{
		return values()[value];
	}
}