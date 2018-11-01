package com.microsoft.bot.builder.ai.luis;


import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Strongly typed class for LUIS number and units entity recognition.
 
 
 Specific subtypes of this class are generated to match the builtin age, currency, dimension and temperature entities.
 
*/
public class NumberWithUnits
{
	/** 
	 Initializes a new instance of the <see cref="NumberWithUnits"/> class.
	 
	 @param number Number.
	 @param units Units for number.
	*/
	public NumberWithUnits(Optional<Double> number, String units)
	{
		setNumber(number);
		setUnits(units);
	}

	/** 
	 Gets or sets recognized number, or null if unit only.
	 
	 <value>
	 Recognized number, or null if unit only.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("number")] public Nullable<double> Number {get;set;}
	private Optional<Double> Number;
	public final Optional<Double> getNumber()
	{
		return Number;
	}
	public final void setNumber(Optional<Double> value)
	{
		Number = value;
	}

	/** 
	 Gets or sets normalized recognized unit.
	 
	 <value>
	 Normalized recognized unit.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("units")] public string Units {get;set;}
	private String Units;
	public final String getUnits()
	{
		return Units;
	}
	public final void setUnits(String value)
	{
		Units = value;
	}
}