package Microsoft.Bot.Builder.Dialogs.Choices;

import Microsoft.Bot.Builder.Dialogs.*;
import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class ChoiceFactoryOptions
{
	/** 
	 Gets or sets the character used to separate individual choices when there are more than 2 choices.
	 The default value is `", "`. This is optional.
	 
	 <value>
	 The character used to separate individual choices when there are more than 2 choices.
	 </value>
	*/
	private String InlineSeparator;
	public final String getInlineSeparator()
	{
		return InlineSeparator;
	}
	public final void setInlineSeparator(String value)
	{
		InlineSeparator = value;
	}

	/** 
	 Gets or sets the separator inserted between the choices when their are only 2 choices. The default
	 value is `" or "`. This is optional.
	 
	 <value>
	 The separator inserted between the choices when their are only 2 choices.
	 </value>
	*/
	private String InlineOr;
	public final String getInlineOr()
	{
		return InlineOr;
	}
	public final void setInlineOr(String value)
	{
		InlineOr = value;
	}

	/** 
	 Gets or sets the separator inserted between the last 2 choices when their are more than 2 choices.
	 The default value is `", or "`. This is optional.
	 
	 <value>
	 The separator inserted between the last 2 choices when their are more than 2 choices.
	 </value>
	*/
	private String InlineOrMore;
	public final String getInlineOrMore()
	{
		return InlineOrMore;
	}
	public final void setInlineOrMore(String value)
	{
		InlineOrMore = value;
	}

	/** 
	 Gets or sets a value indicating whether an inline and list style choices will be prefixed with the index of the
	 choice as in "1. choice". If <see langword="false"/>, the list style will use a bulleted list instead.The default value is <see langword="true"/>.
	 
	 <value>
	 A <c>true</c>if an inline and list style choices will be prefixed with the index of the
	 choice as in "1. choice"; otherwise a <c>false</c> and the list style will use a bulleted list instead.
	 </value>
	*/
	private Optional<Boolean> IncludeNumbers;
	public final Optional<Boolean> getIncludeNumbers()
	{
		return IncludeNumbers;
	}
	public final void setIncludeNumbers(Optional<Boolean> value)
	{
		IncludeNumbers = value;
	}
}