// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import java.util.*;


/** 
 Score plus any extra information about an intent.
*/
public class IntentScore
{
	/** 
	 Gets or sets confidence in an intent.
	 
	 <value>
	 Confidence in an intent.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("score")] public Nullable<double> Score {get;set;}
	private Optional<Double> Score = Optional.empty();
	public final Optional<Double> getScore()
	{
		return Score;
	}
	public final void setScore(Optional<Double> value)
	{
		Score = value;
	}

	/** 
	 Gets or sets any extra properties to include in the results.
	 
	 <value>
	 Any extra properties to include in the results.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonExtensionData(ReadData = true, WriteData = true)] public IDictionary<string, object> Properties {get;set;} = new Dictionary<string, object>();
	private Map<String, Object> Properties = new HashMap<String, Object> ();
	public final Map<String, Object> getProperties()
	{
		return Properties;
	}
	public final void setProperties(Map<String, Object> value)
	{
		Properties = value;
	}
}