// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.dialogs.*;
import com.microsoft.bot.schema.models.CardAction;

import java.util.*;

public class Choice
{

	public Choice()
	{
		this(null);
	}
	public Choice(String value)
	{
		withValue(value);
	}

	/** 
	 Gets or sets the value to return when selected.
	 
	 <value>
	 The value to return when selected.
	 </value>
	*/
	private String Value;
	public final String value()
	{
		return Value;
	}
	public final Choice withValue(String value)
	{
		Value = value;
		return this;
	}

	/** 
	 Gets or sets the action to use when rendering the choice as a suggested action. This is optional.
	 
	 <value>
	 The action to use when rendering the choice as a suggested action.
	 </value>
	*/
	private CardAction Action;
	public final CardAction action()
	{
		return Action;
	}
	public final Choice withAction(CardAction value)
	{
		Action = value;
		return this;
	}

	/** 
	 Gets or sets the list of synonyms to recognize in addition to the value. This is optional.
	 
	 <value>
	 The list of synonyms to recognize in addition to the value.
	 </value>
	*/
	private ArrayList<String> Synonyms;
	public final ArrayList<String> synonyms()
	{
		return Synonyms;
	}
	public final Choice withSynonyms(ArrayList<String> value)
	{
		Synonyms = value;
		return this;
	}
}