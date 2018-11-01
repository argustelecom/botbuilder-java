package com.microsoft.bot.builder.ai.qna;



// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.bot.schema.models.Activity;

/**
 This class represents all the trace info that we collect from the QnAMaker Middleware.
*/
public class QnAMakerTraceInfo
{
	/** 
	 Gets or sets message which instigated the query to QnAMaker.
	 
	 <value>
	 Message which instigated the query to QnAMaker.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("message")] public Activity Message {get;set;}
	private Activity Message;
	public final Activity getMessage()
	{
		return Message;
	}
	public final void setMessage(Activity value)
	{
		Message = value;
	}

	/** 
	 Gets or sets results that QnAMaker returned.
	 
	 <value>
	 Results that QnAMaker returned.
	 </value>
	*/
    @JsonProperty(value = "queryResults")
	private QueryResult[] QueryResults;
	public final QueryResult[] getQueryResults()
	{
		return QueryResults;
	}
	public final void setQueryResults(QueryResult[] value)
	{
		QueryResults = value;
	}

	/** 
	 Gets or sets iD of the Knowledgebase that is being used.
	 
	 <value>
	 ID of the Knowledgebase that is being used.
	 </value>
	*/
    @JsonProperty(value = "knowledgeBaseId")
	private String KnowledgeBaseId;
	public final String getKnowledgeBaseId()
	{
		return KnowledgeBaseId;
	}
	public final void setKnowledgeBaseId(String value)
	{
		KnowledgeBaseId = value;
	}

	/** 
	 Gets or sets the minimum score threshold, used to filter returned results.
	 
	 Scores are normalized to the range of 0.0 to 1.0
	 before filtering.
	 <value>
	 The minimum score threshold, used to filter returned results.
	 </value>
	*/
    @JsonProperty(value = "scoreThreshold")
	private float ScoreThreshold;
	public final float getScoreThreshold()
	{
		return ScoreThreshold;
	}
	public final void setScoreThreshold(float value)
	{
		ScoreThreshold = value;
	}

	/** 
	 Gets or sets number of ranked results that are asked to be returned.
	 
	 <value>
	 Number of ranked results that are asked to be returned.
	 </value>
	*/
    @JsonProperty(value = "top")
	private int Top;
	public final int getTop()
	{
		return Top;
	}
	public final void setTop(int value)
	{
		Top = value;
	}

	/** 
	 Gets or sets the filters used to return answers that have the specified metadata.
	 
	 <value>
	 The filters used to return answers that have the specified metadata.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("strictFilters")] public Metadata[] StrictFilters {get;set;}
	private Metadata[] StrictFilters;
	public final Metadata[] getStrictFilters()
	{
		return StrictFilters;
	}
	public final void setStrictFilters(Metadata[] value)
	{
		StrictFilters = value;
	}

	/** 
	 Gets or sets miscellaneous data to boost answers.
	 
	 <value>
	 Miscellaneous data to boost answers.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("metadataBoost")] public Metadata[] MetadataBoost {get;set;}
	private Metadata[] MetadataBoost;
	public final Metadata[] getMetadataBoost()
	{
		return MetadataBoost;
	}
	public final void setMetadataBoost(Metadata[] value)
	{
		MetadataBoost = value;
	}
}