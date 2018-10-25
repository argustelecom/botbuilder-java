package Microsoft.Bot.Builder.Dialogs.Choices;

import Microsoft.Bot.Builder.Dialogs.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class ModelResult<T>
{
	private String Text;
	public final String getText()
	{
		return Text;
	}
	public final void setText(String value)
	{
		Text = value;
	}

	private int Start;
	public final int getStart()
	{
		return Start;
	}
	public final void setStart(int value)
	{
		Start = value;
	}

	private int End;
	public final int getEnd()
	{
		return End;
	}
	public final void setEnd(int value)
	{
		End = value;
	}

	private String TypeName;
	public final String getTypeName()
	{
		return TypeName;
	}
	public final void setTypeName(String value)
	{
		TypeName = value;
	}

	private T Resolution;
	public final T getResolution()
	{
		return Resolution;
	}
	public final void setResolution(T value)
	{
		Resolution = value;
	}
}