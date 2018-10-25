// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.dialogs.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public final class Find
{

	public static ArrayList<ModelResult<FoundChoice>> FindChoices(String utterance, java.util.List<String> choices)
	{
		return FindChoices(utterance, choices, null);
	}

	public static ArrayList<ModelResult<FoundChoice>> FindChoices(String utterance, List<String> choices, FindChoicesOptions options)
	{
		if (choices == null)
		{
			throw new NullPointerException("choices");
		}

		Choice tempVar = new Choice();
		tempVar.setValue(s);
		return FindChoices(utterance, choices.Select(s -> tempVar).ToList(), options);
	}


	public static ArrayList<ModelResult<FoundChoice>> FindChoices(String utterance, java.util.List<Choice> choices)
	{
		return FindChoices(utterance, choices, null);
	}

	public static ArrayList<ModelResult<FoundChoice>> FindChoices(String utterance, List<Choice> choices, FindChoicesOptions options)
	{
		if (StringUtils.isBlank(utterance))
		{
			throw new NullPointerException("utterance");
		}

		if (choices == null)
		{
			throw new NullPointerException("choices");
		}

		FindChoicesOptions opt = (options != null) ? options : new FindChoicesOptions();

		// Build up full list of synonyms to search over.
		// - Each entry in the list contains the index of the choice it belongs to which will later be
		//   used to map the search results back to their choice.
		ArrayList<SortedValue> synonyms = new ArrayList<SortedValue>();

		for (int index = 0; index < choices.size(); index++)
		{
			Choice choice = choices.get(index);

			if (!opt.getNoValue())
			{
				SortedValue tempVar = new SortedValue();
				tempVar.setValue(choice.getValue());
				tempVar.setIndex(index);
				synonyms.add(tempVar);
			}

			if (choice.getAction() != null && choice.getAction().title() != null && !opt.getNoAction())
			{
				SortedValue tempVar2 = new SortedValue();
				tempVar2.setValue(choice.getAction().title());
				tempVar2.setIndex(index);
				synonyms.add(tempVar2);
			}

			if (choice.getSynonyms() != null)
			{
				for (String synonym : choice.getSynonyms())
				{
					SortedValue tempVar3 = new SortedValue();
					tempVar3.setValue(synonym);
					tempVar3.setIndex(index);
					synonyms.add(tempVar3);
				}
			}
		}

		// Find synonyms in utterance and map back to their choices
		return FindValues(utterance, synonyms, options).Select((v) ->
		{
				 Choice choice = choices.get(v.Resolution.Index);
				 ModelResult<FoundChoice> tempVar4 = new ModelResult<FoundChoice>();
				 tempVar4.setStart(v.Start);
				 tempVar4.setEnd(v.End);
				 tempVar4.setTypeName("choice");
				 tempVar4.setText(v.Text);
				 FoundChoice tempVar5 = new FoundChoice();
				 tempVar5.setValue(choice.getValue());
				 tempVar5.setIndex(v.Resolution.Index);
				 tempVar5.setScore(v.Resolution.Score);
				 tempVar5.setSynonym(v.Resolution.Value);
				 tempVar4.setResolution(tempVar5);
				 return tempVar4;
		}).ToList();
	}


	public static java.util.ArrayList<ModelResult<FoundValue>> FindValues(String utterance, java.util.ArrayList<SortedValue> values)
	{
		return FindValues(utterance, values, null);
	}

	public static ArrayList<ModelResult<FoundValue>> FindValues(String utterance, ArrayList<SortedValue> values, FindValuesOptions options)
	{
		// Sort values in descending order by length so that the longest value is searched over first.
		ArrayList<SortedValue> list = values;
		Collections.sort(list, (a, b) -> b.Value.getLength() - a.Value.getLength());

		// Search for each value within the utterance.
		ArrayList<ModelResult<FoundValue>> matches = new ArrayList<ModelResult<FoundValue>>();
		FindValuesOptions opt = (options != null) ? options : new FindValuesOptions();
		TokenizerFunction tempVar = opt.getTokenizer();
		TokenizerFunction tokenizer = (tempVar != null) ? tempVar : Tokenizer.getDefaultTokenizer();
		var tokens = tokenizer(utterance, opt.getLocale());
		Nullable<Integer> tempVar2 = opt.getMaxTokenDistance();
		int maxDistance = (tempVar2 != null) ? tempVar2 : 2;

		for (int index = 0; index < list.size(); index++)
		{
			SortedValue entry = list.get(index);

			// Find all matches for a value
			// - To match "last one" in "the last time I chose the last one" we need
			//   to re-search the string starting from the end of the previous match.
			// - The start & end position returned for the match are token positions.
			int startPos = 0;
			var searchedTokens = tokenizer(entry.getValue().trim(), opt.getLocale());
			while (startPos < tokens.size())
			{
				ModelResult<FoundValue> match = MatchValue(tokens, maxDistance, opt, entry.getIndex(), entry.getValue(), searchedTokens, startPos);
				if (match != null)
				{
					startPos = match.getEnd() + 1;
					matches.add(match);
				}
				else
				{
					break;
				}
			}
		}

		// Sort matches by score descending
		Collections.sort(matches, (a, b) -> (int)(b.Resolution.Score - a.Resolution.Score));

		// Filter out duplicate matching indexes and overlapping characters.
		// - The start & end positions are token positions and need to be translated to
		//   character positions before returning. We also need to populate the "text"
		//   field as well.
		ArrayList<ModelResult<FoundValue>> results = new ArrayList<ModelResult<FoundValue>>();
		HashSet<Integer> foundIndexes = new HashSet<Integer>();
		HashSet<Integer> usedTokens = new HashSet<Integer>();

		for (ModelResult<FoundValue> match : matches)
		{
			// Apply filters
			boolean add = !foundIndexes.contains(match.getResolution().getIndex());
			for (int i = match.getStart(); i <= match.getEnd(); i++)
			{
				if (usedTokens.contains(i))
				{
					add = false;
					break;
				}
			}

			// Add to results
			if (add)
			{
				// Update filter info
				foundIndexes.add(match.getResolution().getIndex());

				for (int i = match.getStart(); i <= match.getEnd(); i++)
				{
					usedTokens.add(i);
				}

				// Translate start & end and populate text field
				match.setStart(tokens[match.getStart()].Start);
				match.setEnd(tokens[match.getEnd()].End);

				// Note: JavaScript Substring is (start, end) whereas .NET is (start, len)
				match.setText(utterance.substring(match.getStart(), (match.getEnd() + 1)));
				results.add(match);
			}
		}

		// Return the results sorted by position in the utterance
		Collections.sort(results, (a, b) -> a.Start - b.Start);
		return results;
	}

	private static int IndexOfToken(List<Token> tokens, Token token, int startPos)
	{
		for (int i = startPos; i < tokens.size(); i++)
		{
			if (tokens.get(i).getNormalized().equals(token.getNormalized()))
			{
				return i;
			}
		}

		return -1;
	}

	private static ModelResult<FoundValue> MatchValue(List<Token> sourceTokens, int maxDistance, FindValuesOptions options, int index, String value, ArrayList<Token> searchedTokens, int startPos)
	{
		// Match value to utterance and calculate total deviation.
		// - The tokens are matched in order so "second last" will match in
		//   "the second from last one" but not in "the last from the second one".
		// - The total deviation is a count of the number of tokens skipped in the
		//   match so for the example above the number of tokens matched would be
		//   2 and the total deviation would be 1.
		int matched = 0;
		int totalDeviation = 0;
		int start = -1;
		int end = -1;
		for (Token token : searchedTokens)
		{
			// Find the position of the token in the utterance.
			int pos = IndexOfToken(sourceTokens, token, startPos);
			if (pos >= 0)
			{
				// Calculate the distance between the current tokens position and the previous tokens distance.
				boolean distance = matched > 0 ? pos - startPos : 0;
				if (distance <= maxDistance)
				{
					// Update count of tokens matched and move start pointer to search for next token after
					// the current token.
					matched++;
					totalDeviation += distance;
					startPos = pos + 1;

					// Update start & end position that will track the span of the utterance that's matched.
					if (start < 0)
					{
						start = pos;
					}

					end = pos;
				}
			}
		}

		// Calculate score and format result
		// - The start & end positions and the results text field will be corrected by the caller.
		ModelResult<FoundValue> result = null;

		if (matched > 0 && (matched == searchedTokens.size() || options.getAllowPartialMatches()))
		{
			// Percentage of tokens matched. If matching "second last" in
			// "the second from last one" the completeness would be 1.0 since
			// all tokens were found.
			int completeness = matched / searchedTokens.size();

			// Accuracy of the match. The accuracy is reduced by additional tokens
			// occurring in the value that weren't in the utterance. So an utterance
			// of "second last" matched against a value of "second from last" would
			// result in an accuracy of 0.5.
			int accuracy = matched / (matched + totalDeviation);

			// The final score is simply the completeness multiplied by the accuracy.
			int score = completeness * accuracy;

			// Format result
			result = new ModelResult<FoundValue>();
			result.setStart(start);
			result.setEnd(end);
			result.setTypeName("value");
			FoundValue tempVar = new FoundValue();
			tempVar.setValue(value);
			tempVar.setIndex(index);
			tempVar.setScore(score);
			result.setResolution(tempVar);
		}

		return result;
	}
}