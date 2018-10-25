package Microsoft.Bot.Builder.Dialogs.Choices;

import Microsoft.Bot.Builder.Dialogs.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 A value that can be sorted and still refer to its original position with a source array.
*/
public class SortedValue
{
	/** 
	 Gets or sets the value that will be sorted.
	 
	 <value>
	 The value that will be sorted.
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
	 Gets or sets the values original position within its unsorted array.
	 
	 <value>
	 The values original position within its unsorted array.
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
}