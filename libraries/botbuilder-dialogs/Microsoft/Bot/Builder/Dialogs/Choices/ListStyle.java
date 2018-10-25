package Microsoft.Bot.Builder.Dialogs.Choices;

import Microsoft.Bot.Builder.Dialogs.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Controls the way that choices for a `ChoicePrompt` or yes/no options for a `ConfirmPrompt` are
 presented to a user.
*/
public enum ListStyle
{
	/** 
	 Don't include any choices for prompt.
	*/
	None,

	/** 
	 Automatically select the appropriate style for the current channel.
	*/
	Auto,

	/** 
	 Add choices to prompt as an inline list.
	*/
	Inline,

	/** 
	 Add choices to prompt as a numbered list.
	*/
	List,

	/** 
	 Add choices to prompt as suggested actions.
	*/
	SuggestedAction;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ListStyle forValue(int value)
	{
		return values()[value];
	}
}