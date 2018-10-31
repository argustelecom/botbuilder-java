package Microsoft.Bot.Builder.AI.Luis;

import java.util.*;

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.

/** 
 Optional parameters for a LUIS prediction request.
*/
public class LuisPredictionOptions
{
	/** 
	 Gets or sets the Bing Spell Check subscription key.
	 
	 <value>
	 The Bing Spell Check subscription key.
	 </value>
	*/
	private String BingSpellCheckSubscriptionKey;
	public final String getBingSpellCheckSubscriptionKey()
	{
		return BingSpellCheckSubscriptionKey;
	}
	public final void setBingSpellCheckSubscriptionKey(String value)
	{
		BingSpellCheckSubscriptionKey = value;
	}

	/** 
	 Gets or sets whether all intents come back or only the top one.
	 
	 <value>
	 True for returning all intents.
	 </value>
	*/
	private Optional<Boolean> IncludeAllIntents;
	public final Optional<Boolean> getIncludeAllIntents()
	{
		return IncludeAllIntents;
	}
	public final void setIncludeAllIntents(Optional<Boolean> value)
	{
		IncludeAllIntents = value;
	}

	/** 
	 Gets or sets a value indicating whether or not instance data should be included in response.
	 
	 <value>
	 A value indicating whether or not instance data should be included in response.
	 </value>
	*/
	private Optional<Boolean> IncludeInstanceData;
	public final Optional<Boolean> getIncludeInstanceData()
	{
		return IncludeInstanceData;
	}
	public final void setIncludeInstanceData(Optional<Boolean> value)
	{
		IncludeInstanceData = value;
	}

	/** 
	 Gets or sets if queries should be logged in LUIS.
	 
	 <value>
	 If queries should be logged in LUIS.
	 </value>
	*/
	private Optional<Boolean> Log;
	public final Optional<Boolean> getLog()
	{
		return Log;
	}
	public final void setLog(Optional<Boolean> value)
	{
		Log = value;
	}

	/** 
	 Gets or sets whether to spell check queries.
	 
	 <value>
	 Whether to spell check queries.
	 </value>
	*/
	private Optional<Boolean> SpellCheck;
	public final Optional<Boolean> getSpellCheck()
	{
		return SpellCheck;
	}
	public final void setSpellCheck(Optional<Boolean> value)
	{
		SpellCheck = value;
	}

	/** 
	 Gets or sets whether to use the staging endpoint.
	 
	 <value>
	 Whether to use the staging endpoint.
	 </value>
	*/
	private Optional<Boolean> Staging;
	public final Optional<Boolean> getStaging()
	{
		return Staging;
	}
	public final void setStaging(Optional<Boolean> value)
	{
		Staging = value;
	}

	/** 
	 Gets or sets the time zone offset.
	 
	 <value>
	 The time zone offset.
	 </value>
	*/
	private Optional<Double> TimezoneOffset;
	public final Optional<Double> getTimezoneOffset()
	{
		return TimezoneOffset;
	}
	public final void setTimezoneOffset(Optional<Double> value)
	{
		TimezoneOffset = value;
	}
}