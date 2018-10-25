// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.dialogs.*;
import java.util.*;


public class ChoiceRecognizers
{

	public static ArrayList<ModelResult<FoundChoice>> RecognizeChoices(String utterance, java.util.List<String> choices)
	{
		return RecognizeChoices(utterance, choices, null);
	}

	public static ArrayList<ModelResult<FoundChoice>> RecognizeChoices(String utterance, List<String> choices, FindChoicesOptions options)
	{
		Choice tempVar = new Choice();
		tempVar.setValue(s);
		return RecognizeChoices(utterance, choices.Select(s -> tempVar).ToList(), options);
	}


	public static java.util.ArrayList<ModelResult<FoundChoice>> RecognizeChoices(String utterance, java.util.List<Choice> list)
	{
		return RecognizeChoices(utterance, list, null);
	}

	public static ArrayList<ModelResult<FoundChoice>> RecognizeChoices(String utterance, List<Choice> list, FindChoicesOptions options)
	{
		// Try finding choices by text search first
		// - We only want to use a single strategy for returning results to avoid issues where utterances
		//   like the "the third one" or "the red one" or "the first division book" would miss-recognize as
		//   a numerical index or ordinal as well.
		String tempVar = options.getLocale();
		boolean locale = options == null ? null : (tempVar != null) ? tempVar : Recognizers.Text.Culture.English;
		ArrayList<ModelResult<FoundChoice>> matched = Find.FindChoices(utterance, list, options);
		if (matched.isEmpty())
		{
			// Next try finding by ordinal
			ArrayList<ModelResult<FoundChoice>> matches = RecognizeOrdinal(utterance, locale);
			if (matches.Any())
			{
				for (ModelResult<FoundChoice> match : matches)
				{
					MatchChoiceByIndex(list, matched, match);
				}
			}
			else
			{
				// Finally try by numerical index
				matches = RecognizeNumber(utterance, locale);

				for (ModelResult<FoundChoice> match : matches)
				{
					MatchChoiceByIndex(list, matched, match);
				}
			}

			// Sort any found matches by their position within the utterance.
			// - The results from findChoices() are already properly sorted so we just need this
			//   for ordinal & numerical lookups.
			Collections.sort(matched, (a, b) -> a.Start - b.Start);
		}

		return matched;
	}

	private static void MatchChoiceByIndex(List<Choice> list, ArrayList<ModelResult<FoundChoice>> matched, ModelResult<FoundChoice> match)
	{
		try
		{
			int index = Integer.parseInt(match.getResolution().getValue()) - 1;
			if (index >= 0 && index < list.size())
			{
				Choice choice = list.get(index);
				ModelResult<FoundChoice> tempVar = new ModelResult<FoundChoice>();
				tempVar.setStart(match.getStart());
				tempVar.setEnd(match.getEnd());
				tempVar.setTypeName("choice");
				tempVar.setText(match.getText());
				FoundChoice tempVar2 = new FoundChoice();
				tempVar2.setValue(choice.getValue());
				tempVar2.setIndex(index);
				tempVar2.setScore(1.0f);
				tempVar.setResolution(tempVar2);
				matched.add(tempVar);
			}
		}
		catch (RuntimeException e)
		{
		}
	}

	private static ArrayList<ModelResult<FoundChoice>> RecognizeOrdinal(String utterance, String culture)
	{
		NumberRecognizer model = (new NumberRecognizer(culture)).GetOrdinalModel(culture);
		var result = model.Parse(utterance);
		ModelResult<FoundChoice> tempVar = new ModelResult<FoundChoice>();
		tempVar.setStart(r.Start);
		tempVar.setEnd(r.End);
		tempVar.setText(r.Text);
		FoundChoice tempVar2 = new FoundChoice();
		tempVar2.setValue(r.Resolution["value"].toString());
		tempVar.setResolution(tempVar2);
		return result.Select(r -> tempVar).ToList();
	}

	private static ArrayList<ModelResult<FoundChoice>> RecognizeNumber(String utterance, String culture)
	{
		NumberRecognizer model = (new NumberRecognizer(culture)).GetNumberModel(culture);
		var result = model.Parse(utterance);
		ModelResult<FoundChoice> tempVar = new ModelResult<FoundChoice>();
		tempVar.setStart(r.Start);
		tempVar.setEnd(r.End);
		tempVar.setText(r.Text);
		FoundChoice tempVar2 = new FoundChoice();
		tempVar2.setValue(r.Resolution["value"].toString());
		tempVar.setResolution(tempVar2);
		return result.Select(r -> tempVar).ToList();
	}
}