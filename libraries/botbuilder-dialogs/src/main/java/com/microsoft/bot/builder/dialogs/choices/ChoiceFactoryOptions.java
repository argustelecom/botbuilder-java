// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.dialogs.*;
import java.util.*;



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
	public final String inlineSeparator()
	{
		return InlineSeparator;
	}
	public final ChoiceFactoryOptions withInlineSeparator(String value)
	{
		InlineSeparator = value;
		return this;
	}

	/** 
	 Gets or sets the separator inserted between the choices when their are only 2 choices. The default
	 value is `" or "`. This is optional.
	 
	 <value>
	 The separator inserted between the choices when their are only 2 choices.
	 </value>
	*/
	private String InlineOr;
	public final String inlineOr()
	{
		return InlineOr;
	}
	public final ChoiceFactoryOptions withInlineOr(String value)
	{
		InlineOr = value;
		return this;
	}

	/** 
	 Gets or sets the separator inserted between the last 2 choices when their are more than 2 choices.
	 The default value is `", or "`. This is optional.
	 
	 <value>
	 The separator inserted between the last 2 choices when their are more than 2 choices.
	 </value>
	*/
	private String InlineOrMore;
	public final String inlineOrMore()
	{
		return InlineOrMore;
	}
	public final ChoiceFactoryOptions withInlineOrMore(String value)
	{
		InlineOrMore = value;
		return this;
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
	public final Optional<Boolean> includeNumbers()
	{
		return IncludeNumbers;
	}
	public final ChoiceFactoryOptions withIncludeNumbers(Optional<Boolean> value)
	{
		IncludeNumbers = value;
		return this;
	}
}