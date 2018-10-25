package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.dialogs.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class FoundChoice
{
	/** 
	 Gets or sets the value of the choice that was matched.
	 
	 <value>
	 The value of the choice that was matched.
	 </value>
	*/
	private String Value;
	public final String getValue()
	{
		return Value;
	}
	public final void setValue(String value)
	{
		Value = value;
	}

	/** 
	 Gets or sets the choices index within the list of choices that was searched over.
	 
	 <value>
	 The choices index within the list of choices that was searched over.
	 </value>
	*/
	private int Index;
	public final int getIndex()
	{
		return Index;
	}
	public final void setIndex(int value)
	{
		Index = value;
	}

	/** 
	 Gets or sets the accuracy with which the synonym matched the specified portion of the utterance. A
	 value of 1.0 would indicate a perfect match.
	 
	 <value>
	 The accuracy with which the synonym matched the specified portion of the utterance. A
	 value of 1.0 would indicate a perfect match.
	 </value>
	*/
	private float Score;
	public final float getScore()
	{
		return Score;
	}
	public final void setScore(float value)
	{
		Score = value;
	}

	/** 
	 Gets or sets the synonym that was matched. This is optional.
	 
	 <value>
	 The synonym that was matched.
	 </value>
	*/
	private String Synonym;
	public final String getSynonym()
	{
		return Synonym;
	}
	public final void setSynonym(String value)
	{
		Synonym = value;
	}
}