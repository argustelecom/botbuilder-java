package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Contains helper methods for working with <see cref="RecognizerResult"/> objects.
*/
public final class RecognizerResultExtensions
{
	/** 
	 Return the top scoring intent and its score.
	 
	 @param result Recognizer result.
	 @return Intent and score.
	*/
//C# TO JAVA CONVERTER TODO TASK: Methods returning tuples are not converted by C# to Java Converter:
//	public static(string intent, double score) GetTopScoringIntent(this RecognizerResult result)
//		{
//			if (result == null)
//			{
//				throw new ArgumentNullException(nameof(result));
//			}
//
//			if (result.Intents == null)
//			{
//				throw new ArgumentNullException(nameof(result.Intents));
//			}
//
//			var topIntent = (string.Empty, 0.0d);
//			foreach (var intent in result.Intents)
//			{
//				var score = intent.Value.Score;
//				if (score > topIntent.Item2)
//				{
//					topIntent = (intent.Key, score.Value);
//				}
//			}
//
//			return topIntent;
//		}
}