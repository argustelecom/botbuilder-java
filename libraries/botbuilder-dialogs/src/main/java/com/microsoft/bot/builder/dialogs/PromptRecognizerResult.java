package Microsoft.Bot.Builder.Dialogs;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

public class PromptRecognizerResult<T>
{
	public PromptRecognizerResult()
	{
		setSucceeded(false);
	}

	private boolean Succeeded;
	public final boolean getSucceeded()
	{
		return Succeeded;
	}
	public final void setSucceeded(boolean value)
	{
		Succeeded = value;
	}

	private T Value;
	public final T getValue()
	{
		return Value;
	}
	public final void setValue(T value)
	{
		Value = value;
	}
}