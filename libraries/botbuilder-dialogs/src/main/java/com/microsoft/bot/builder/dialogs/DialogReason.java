package com.microsoft.bot.builder.dialogs;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

public enum DialogReason
{
	/** 
	 A dialog is being started through a call to `DialogContext.BeginAsync()`.
	*/
	BeginCalled,

	/** 
	 A dialog is being continued through a call to `DialogContext.ContinueDialogAsync()`.
	*/
	ContinueCalled,

	/** 
	 A dialog ended normally through a call to `DialogContext.EndDialogAsync()`.
	*/
	EndCalled,

	/** 
	 A dialog is ending because its being replaced through a call to `DialogContext.ReplaceDialogAsync()`.
	*/
	ReplaceCalled,

	/** 
	 A dialog was cancelled as part of a call to `DialogContext.CancelAllDialogsAsync()`.
	*/
	CancelCalled,

	/** 
	 A step was advanced through a call to `WaterfallStepContext.NextAsync()`.
	*/
	NextCalled;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static DialogReason forValue(int value)
	{
		return values()[value];
	}
}