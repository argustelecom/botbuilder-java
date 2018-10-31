// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;


public class PromptRecognizerResult<T>
{
	public PromptRecognizerResult()
	{
		withSucceeded(false);
	}

	private boolean Succeeded;
	public final boolean succeeded()
	{
		return Succeeded;
	}
	public final PromptRecognizerResult<T> withSucceeded(boolean value)
	{
		Succeeded = value;
		return this;
	}

	private T Value;
	public final T value()
	{
		return Value;
	}
	public final PromptRecognizerResult<T>  withValue(T value)
	{
		Value = value;
		return this;
	}
}