// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.dialogs.*;

public class Token
{
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

	private String Text;
	public final String getText()
	{
		return Text;
	}
	public final void setText(String value)
	{
		Text = value;
	}

	private String Normalized;
	public final String getNormalized()
	{
		return Normalized;
	}
	public final void setNormalized(String value)
	{
		Normalized = value;
	}
}