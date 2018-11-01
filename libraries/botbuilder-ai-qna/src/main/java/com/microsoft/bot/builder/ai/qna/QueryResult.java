package com.microsoft.bot.builder.ai.qna;



// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Represents an individual result from a knowledge base query.
*/
public class QueryResult
{
	/** 
	 Gets or sets the list of questions indexed in the QnA Service for the given answer.
	 
	 <value>
	 The list of questions indexed in the QnA Service for the given answer.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("questions")] public string[] Questions {get;set;}
	private String[] Questions;
	public final String[] getQuestions()
	{
		return Questions;
	}
	public final void setQuestions(String[] value)
	{
		Questions = value;
	}

	/** 
	 Gets or sets the answer text.
	 
	 <value>
	 The answer text.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("answer")] public string Answer {get;set;}
	private String Answer;
	public final String getAnswer()
	{
		return Answer;
	}
	public final void setAnswer(String value)
	{
		Answer = value;
	}

	/** 
	 Gets or sets the answer's score, from 0.0 (least confidence) to
	 1.0 (greatest confidence).
	 
	 <value>
	 The answer's score, from 0.0 (least confidence) to
	 1.0 (greatest confidence).
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("score")] public float Score {get;set;}
	private float Score;
	public final float getScore()
	{
		return Score;
	}
	public final void setScore(float value)
	{
		Score = value;
	}

	/** 
	 Gets or sets metadata that is associated with the answer.
	 
	 <value>
	 Metadata that is associated with the answer.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty(PropertyName = "metadata")] public Metadata[] Metadata {get;set;}
	private Metadata[] Metadata;
	public final Metadata[] getMetadata()
	{
		return Metadata;
	}
	public final void setMetadata(Metadata[] value)
	{
		Metadata = value;
	}

	/** 
	 Gets or sets the source from which the QnA was extracted.
	 
	 <value>
	 The source from which the QnA was extracted.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty(PropertyName = "source")] public string Source {get;set;}
	private String Source;
	public final String getSource()
	{
		return Source;
	}
	public final void setSource(String value)
	{
		Source = value;
	}

	/** 
	 Gets or sets the index of the answer in the knowledge base. V3 uses
	 'qnaId', V4 uses 'id'.
	 
	 <value>
	 The index of the answer in the knowledge base. V3 uses
	 'qnaId', V4 uses 'id'.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty(PropertyName = "id")] public int Id {get;set;}
	private int Id;
	public final int getId()
	{
		return Id;
	}
	public final void setId(int value)
	{
		Id = value;
	}
}