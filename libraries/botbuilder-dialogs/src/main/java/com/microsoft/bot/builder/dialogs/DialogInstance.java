// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;



/** 
 Tracking information for a dialog on the stack.
*/
public class DialogInstance
{
	/** 
	 Gets or sets the ID of the dialog this instance is for.
	 
	 <value>
	 ID of the dialog this instance is for.
	 </value>
	*/
	private String Id;
	public final String getId()
	{
		return Id;
	}
	public final void setId(String value)
	{
		Id = value;
	}

	/** 
	 Gets or sets the instances persisted state.
	 
	 <value>
	 The instances persisted state.
	 </value>
	*/
	private Map<String, Object> State;
	public final Map<String, Object> getState()
	{
		return State;
	}
	public final void setState(Map<String, Object> value)
	{
		State = value;
	}
}