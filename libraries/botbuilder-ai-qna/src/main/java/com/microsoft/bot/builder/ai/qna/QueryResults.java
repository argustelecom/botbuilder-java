package Microsoft.Bot.Builder.AI.QnA;

import Newtonsoft.Json.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Contains answers for a user query.
*/
public class QueryResults
{
	/** 
	 Gets or sets the answers for a user query,
	 sorted in decreasing order of ranking score.
	 
	 <value>
	 The answers for a user query,
	 sorted in decreasing order of ranking score.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("answers")] public QueryResult[] Answers {get;set;}
	private QueryResult[] Answers;
	public final QueryResult[] getAnswers()
	{
		return Answers;
	}
	public final void setAnswers(QueryResult[] value)
	{
		Answers = value;
	}
}