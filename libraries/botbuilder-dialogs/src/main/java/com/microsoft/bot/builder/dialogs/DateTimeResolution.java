// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;


public class DateTimeResolution
{
	private String Value;
	public final String getValue()
	{
		return Value;
	}
	public final void setValue(String value)
	{
		Value = value;
	}

	private String Start;
	public final String getStart()
	{
		return Start;
	}
	public final void setStart(String value)
	{
		Start = value;
	}

	private String End;
	public final String getEnd()
	{
		return End;
	}
	public final void setEnd(String value)
	{
		End = value;
	}

	private String Timex;
	public final String getTimex()
	{
		return Timex;
	}
	public final void setTimex(String value)
	{
		Timex = value;
	}
}