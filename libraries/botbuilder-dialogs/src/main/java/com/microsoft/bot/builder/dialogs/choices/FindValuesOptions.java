// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.dialogs.*;
import java.util.*;


public class FindValuesOptions
{
	/** 
	 Gets or sets a value indicating whether only some of the tokens in a value need to exist to be considered
	 a match. The default value is "false". This is optional.
	 
	 <value>
	 A <c>true</c> if only some of the tokens in a value need to exist to be considered; otherwise <c>false</c>.
	 </value>
	*/
	private boolean AllowPartialMatches;
	public final boolean getAllowPartialMatches()
	{
		return AllowPartialMatches;
	}
	public final void setAllowPartialMatches(boolean value)
	{
		AllowPartialMatches = value;
	}

	/** 
	 Gets or sets the locale/culture code of the utterance. The default is `en-US`. This is optional.
	 
	 <value>
	 The locale/culture code of the utterance.
	 </value>
	*/
	private String Locale;
	public final String getLocale()
	{
		return Locale;
	}
	public final void setLocale(String value)
	{
		Locale = value;
	}

	/** 
	 Gets or sets the maximum tokens allowed between two matched tokens in the utterance. So with
	 a max distance of 2 the value "second last" would match the utterance "second from the last"
	 but it wouldn't match "Wait a second. That's not the last one is it?".
	 The default value is "2".
	 
	 <value>
	 The maximum tokens allowed between two matched tokens in the utterance.
	 </value>
	*/
	private Optional<Integer> MaxTokenDistance;
	public final Optional<Integer> getMaxTokenDistance()
	{
		return MaxTokenDistance;
	}
	public final void setMaxTokenDistance(Optional<Integer> value)
	{
		MaxTokenDistance = value;
	}

	/** 
	 Gets or sets the tokenizer to use when parsing the utterance and values being recognized.
	 
	 <value>
	 The tokenizer to use when parsing the utterance and values being recognized.
	 </value>
	*/
	private TokenizerFunction Tokenizer;
	public final TokenizerFunction getTokenizer()
	{
		return Tokenizer;
	}
	public final void setTokenizer(TokenizerFunction value)
	{
		Tokenizer = value;
	}
}