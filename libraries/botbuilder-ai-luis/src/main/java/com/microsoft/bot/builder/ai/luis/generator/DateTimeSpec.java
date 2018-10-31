package Microsoft.Bot.Builder.AI.Luis;

import Newtonsoft.Json.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/** 
 Represents the built-in LUIS date-time type.
 
 
 LUIS recognizes time expressions like "next monday" and converts those to a type and set of timex expressions.
 More information on timex can be found here: http: //www.timeml.org/publications/timeMLdocs/timeml_1.2.1.html#timex3.
 More information on the library which does the recognition can be found here: https: //github.com/Microsoft/Recognizers-Text.
 
*/
public class DateTimeSpec
{
	/** 
	 Initializes a new instance of the <see cref="DateTimeSpec"/> class.
	 
	 @param type The type of TIMEX expression.
	 @param expressions The TIMEX expression.
	 @exception ArgumentNullException <paramref name="type"/> is null or contains only white space,
	 or <paramref name="expressions"/> is null.
	*/
	public DateTimeSpec(String type, java.lang.Iterable<String> expressions)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(type))
		{
			throw new NullPointerException("type");
		}

		if (expressions == null)
		{
			throw new NullPointerException("expressions");
		}

		Type = type;
		Expressions = expressions.ToList();
	}

	/** 
	 Gets type of expression.
	 
	 Example types include:
	 <list type="*">
	 <item>time -- simple time expression like "3pm".</item>
	 <item>date -- simple date like "july 3rd".</item>
	 <item>datetime -- combination of date and time like "march 23 2pm".</item>
	 <item>timerange -- a range of time like "2pm to 4pm".</item>
	 <item>daterange -- a range of dates like "march 23rd to 24th".</item>
	 <item>datetimerang -- a range of dates and times like "july 3rd 2pm to 5th 4pm".</item>
	 <item>set -- a recurrence like "every monday".</item>
	 </list>
	 
	 <value>
	 The type of expression.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("type")] public string Type {get;}
	private String Type;
	public final String getType()
	{
		return Type;
	}

	/** 
	 Gets Timex expressions.
	 
	 <value>
	 Timex expressions.
	 </value>
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonProperty("timex")] public IReadOnlyList<string> Expressions {get;}
	private IReadOnlyList<String> Expressions;
	public final IReadOnlyList<String> getExpressions()
	{
		return Expressions;
	}

	/** <inheritdoc/>
	*/
	@Override
	public String toString()
	{
		return String.format("DateTimeSpec(%1$s, [%2$s]", getType(), tangible.StringHelper.join(", ", getExpressions()));
	}
}