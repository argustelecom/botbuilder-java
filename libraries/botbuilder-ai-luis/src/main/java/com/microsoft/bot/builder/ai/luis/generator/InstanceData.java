package Microsoft.Bot.Builder.AI.Luis;


import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/** 
 Strongly typed information corresponding to LUIS $instance value.
*/
public class InstanceData
{
	/** 
	 Gets or sets 0-based index in the analyzed text for where entity starts.
	 
	 <value>
	 0-based index in the analyzed text for where entity starts.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("startIndex")] public int StartIndex {get;set;}
	private int StartIndex;
	public final int getStartIndex()
	{
		return StartIndex;
	}
	public final void setStartIndex(int value)
	{
		StartIndex = value;
	}

	/** 
	 Gets or sets 0-based index of the first character beyond the recognized entity.
	 
	 <value>
	 0-based index of the first character beyond the recognized entity.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("endIndex")] public int EndIndex {get;set;}
	private int EndIndex;
	public final int getEndIndex()
	{
		return EndIndex;
	}
	public final void setEndIndex(int value)
	{
		EndIndex = value;
	}

	/** 
	 Gets or sets word broken and normalized text for the entity.
	 
	 <value>
	 Word broken and normalized text for the entity.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("text")] public string Text {get;set;}
	private String Text;
	public final String getText()
	{
		return Text;
	}
	public final void setText(String value)
	{
		Text = value;
	}

	/** 
	 Gets or sets optional confidence in the recognition.
	 
	 <value>
	 Optional confidence in the recognition.
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
	 Gets or sets optional type for the entity.
	 
	 <value>
	 Optional entity type.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("type")] public string Type {get;set;}
	private String Type;
	public final String getType()
	{
		return Type;
	}
	public final void setType(String value)
	{
		Type = value;
	}

	/** 
	 Gets or sets optional subtype for the entity.
	 
	 <value>
	 Optional entity subtype.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("subtype")] public string Subtype {get;set;}
	private String Subtype;
	public final String getSubtype()
	{
		return Subtype;
	}
	public final void setSubtype(String value)
	{
		Subtype = value;
	}

	/** 
	 Gets or sets any extra properties.
	 
	 <value>
	 Any extra properties.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonExtensionData(ReadData = true, WriteData = true)] public IDictionary<string, object> Properties {get;set;}
	private Map<String, Object> Properties;
	public final Map<String, Object> getProperties()
	{
		return Properties;
	}
	public final void setProperties(Map<String, Object> value)
	{
		Properties = value;
	}
}