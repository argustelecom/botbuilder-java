package Microsoft.Bot.Builder.Dialogs.Choices;

import Microsoft.Bot.Builder.Dialogs.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class FindChoicesOptions extends FindValuesOptions
{
	/** 
	 Gets or sets a value indicating whether the choices value will NOT be search over.
	 The default is <c>false</c>. This is optional.
	 
	 <value>
	 A <c>true</c> if the choices value will NOT be search over; otherwise <c>false</c>.
	 </value>
	*/
	private boolean NoValue;
	public final boolean getNoValue()
	{
		return NoValue;
	}
	public final void setNoValue(boolean value)
	{
		NoValue = value;
	}

	/** 
	 Gets or sets a value indicating whether the title of the choices action will NOT be searched over.
	 The default is <c>false</c>. This is optional.
	 
	 <value>
	 A <c>true</c> if the title of the choices action will NOT be searched over; otherwise <c>false</c>.
	 </value>
	*/
	private boolean NoAction;
	public final boolean getNoAction()
	{
		return NoAction;
	}
	public final void setNoAction(boolean value)
	{
		NoAction = value;
	}
}