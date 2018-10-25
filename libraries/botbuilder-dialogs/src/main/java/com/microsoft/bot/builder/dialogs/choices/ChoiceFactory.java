package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.dialogs.*;
import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.



public class ChoiceFactory
{

	public static IMessageActivity ForChannel(String channelId, java.util.List<Choice> list, String text, String speak)
	{
		return ForChannel(channelId, list, text, speak, null);
	}

	public static IMessageActivity ForChannel(String channelId, java.util.List<Choice> list, String text)
	{
		return ForChannel(channelId, list, text, null, null);
	}

	public static IMessageActivity ForChannel(String channelId, java.util.List<Choice> list)
	{
		return ForChannel(channelId, list, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity ForChannel(string channelId, IList<Choice> list, string text = null, string speak = null, ChoiceFactoryOptions options = null)
	public static IMessageActivity ForChannel(String channelId, List<Choice> list, String text, String speak, ChoiceFactoryOptions options)
	{
		channelId = (channelId != null) ? channelId : "";

		list = (list != null) ? list : new ArrayList<Choice>();

		// Find maximum title length
		int maxTitleLength = 0;
		for (Choice choice : list)
		{
			boolean l = choice.getAction() != null && StringUtils.isBlank(choice.getAction().Title) ? choice.getAction().Title.getLength() : choice.getValue().length();
			if (l > maxTitleLength)
			{
				maxTitleLength = l;
			}
		}

		// Determine list style
		boolean supportsSuggestedActions = Channel.SupportsSuggestedActions(channelId, list.size());
		boolean supportsCardActions = Channel.SupportsCardActions(channelId, list.size());
		int maxActionTitleLength = Channel.MaxActionTitleLength(channelId);
		boolean hasMessageFeed = Channel.HasMessageFeed(channelId);
		boolean longTitles = maxTitleLength > maxActionTitleLength;

		if (!longTitles && (supportsSuggestedActions || (!hasMessageFeed && supportsCardActions)))
		{
			// We always prefer showing choices using suggested actions. If the titles are too long, however,
			// we'll have to show them as a text list.
			return SuggestedAction(list, text, speak);
		}
		else if (!longTitles && list.size() <= 3)
		{
			// If the titles are short and there are 3 or less choices we'll use an inline list.
			return Inline(list, text, speak, options);
		}
		else
		{
			// Show a numbered list.
			return List(list, text, speak, options);
		}
	}


	public static Activity Inline(java.util.List<Choice> choices, String text, String speak)
	{
		return Inline(choices, text, speak, null);
	}

	public static Activity Inline(java.util.List<Choice> choices, String text)
	{
		return Inline(choices, text, null, null);
	}

	public static Activity Inline(java.util.List<Choice> choices)
	{
		return Inline(choices, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static Activity Inline(IList<Choice> choices, string text = null, string speak = null, ChoiceFactoryOptions options = null)
	public static Activity Inline(List<Choice> choices, String text, String speak, ChoiceFactoryOptions options)
	{
		choices = (choices != null) ? choices : new ArrayList<Choice>();
		options = (options != null) ? options : new ChoiceFactoryOptions();

		ChoiceFactoryOptions opt = new ChoiceFactoryOptions();
		String tempVar = options.getInlineSeparator();
		opt.setInlineSeparator((tempVar != null) ? tempVar : ", ");
		String tempVar2 = options.getInlineOr();
		opt.setInlineOr((tempVar2 != null) ? tempVar2 : " or ");
		String tempVar3 = options.getInlineOrMore();
		opt.setInlineOrMore((tempVar3 != null) ? tempVar3 : ", or ");
		Nullable<Boolean> tempVar4 = options.getIncludeNumbers();
		opt.setIncludeNumbers((tempVar4 != null) ? tempVar4 : true);

		// Format list of choices
		String connector = "";
		String txt = (text != null) ? text : "";
		txt += " ";

		for (int index = 0; index < choices.size(); index++)
		{
			Choice choice = choices.get(index);

			boolean title = choice.getAction() != null && choice.getAction().Title != null ? choice.getAction().Title : choice.getValue();

			txt += String.format("%1$s", connector);
			if (opt.getIncludeNumbers().get())
			{
				txt += "(" + (String.valueOf(index) + ") ";
			}

			txt += String.format("%1$s", title);
			if (index == (choices.size() - 2))
			{
				connector = ((index == 0 ? opt.getInlineOr() : opt.getInlineOrMore()) != null) ? (index == 0 ? opt.getInlineOr() : opt.getInlineOrMore()) : "";
			}
			else
			{
				String tempVar5 = opt.getInlineSeparator();
				connector = (tempVar5 != null) ? tempVar5 : "";
			}
		}

		txt += "";

		// Return activity with choices as an inline list.
		return MessageFactory.Text(txt, speak, InputHints.ExpectingInput);
	}


	public static Activity List(java.util.List<String> choices, String text, String speak)
	{
		return List(choices, text, speak, null);
	}

	public static Activity List(java.util.List<String> choices, String text)
	{
		return List(choices, text, null, null);
	}

	public static Activity List(java.util.List<String> choices)
	{
		return List(choices, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static Activity List(IList<string> choices, string text = null, string speak = null, ChoiceFactoryOptions options = null)
	public static Activity List(List<String> choices, String text, String speak, ChoiceFactoryOptions options)
	{
		return List(ToChoices(choices), text, speak, options);
	}


	public static Activity List(java.util.List<Choice> choices, String text, String speak)
	{
		return List(choices, text, speak, null);
	}

	public static Activity List(java.util.List<Choice> choices, String text)
	{
		return List(choices, text, null, null);
	}

	public static Activity List(java.util.List<Choice> choices)
	{
		return List(choices, null, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static Activity List(IList<Choice> choices, string text = null, string speak = null, ChoiceFactoryOptions options = null)
	public static Activity List(List<Choice> choices, String text, String speak, ChoiceFactoryOptions options)
	{
		choices = (choices != null) ? choices : new ArrayList<Choice>();
		options = (options != null) ? options : new ChoiceFactoryOptions();

		Nullable<Boolean> tempVar = options.getIncludeNumbers();
		boolean includeNumbers = (tempVar != null) ? tempVar : true;

		// Format list of choices
		String connector = "";
		String txt = (text != null) ? text : "";
		txt += "\n\n   ";

		for (int index = 0; index < choices.size(); index++)
		{
			Choice choice = choices.get(index);

			boolean title = choice.getAction() != null && choice.getAction().Title != null ? choice.getAction().Title : choice.getValue();

			txt += connector;
			if (includeNumbers)
			{
				txt += (String.valueOf(index) + ". ";
			}
			else
			{
				txt += "- ";
			}

			txt += title;
			connector = "\n   ";
		}

		// Return activity with choices as a numbered list.
		return MessageFactory.Text(txt, speak, InputHints.ExpectingInput);
	}


	public static IMessageActivity SuggestedAction(java.util.List<String> choices, String text)
	{
		return SuggestedAction(choices, text, null);
	}

	public static IMessageActivity SuggestedAction(java.util.List<String> choices)
	{
		return SuggestedAction(choices, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity SuggestedAction(IList<string> choices, string text = null, string speak = null)
	public static IMessageActivity SuggestedAction(List<String> choices, String text, String speak)
	{
		return SuggestedAction(ToChoices(choices), text, speak);
	}


	public static IMessageActivity SuggestedAction(java.util.List<Choice> choices, String text)
	{
		return SuggestedAction(choices, text, null);
	}

	public static IMessageActivity SuggestedAction(java.util.List<Choice> choices)
	{
		return SuggestedAction(choices, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static IMessageActivity SuggestedAction(IList<Choice> choices, string text = null, string speak = null)
	public static IMessageActivity SuggestedAction(List<Choice> choices, String text, String speak)
	{
		choices = (choices != null) ? choices : new ArrayList<Choice>();

		// Map choices to actions
		ArrayList<Object> actions = choices.Select((choice) ->
		{
				if (choice.Action != null)
				{
					return choice.Action;
				}
				else
				{
					CardAction tempVar = new CardAction();
					tempVar.Type = ActionTypes.ImBack;
					tempVar.Value = choice.Value;
					tempVar.Title = choice.Value;
					return tempVar;
				}
		}).ToList();

		// Return activity with choices as suggested actions
		return MessageFactory.SuggestedActions(actions, text, speak, InputHints.ExpectingInput);
	}

	public static List<Choice> ToChoices(List<String> choices)
	{
		ArrayList<Choice>() : choices.Select(choice -> new Choice tempVar = new ArrayList<Choice>() : choices.Select(choice -> new Choice();
		tempVar.Value = choice;
		return (choices == null) ? tempVar).ToList();
	}
}