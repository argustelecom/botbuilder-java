package com.microsoft.bot.builder.ai.qna;



// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class Metadata implements Serializable
{
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty(PropertyName = "name")] public string Name {get;set;}
	private String Name;
	public final String getName()
	{
		return Name;
	}
	public final void setName(String value)
	{
		Name = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty(PropertyName = "value")] public string Value {get;set;}
	private String Value;
	public final String getValue()
	{
		return Value;
	}
	public final void setValue(String value)
	{
		Value = value;
	}
}