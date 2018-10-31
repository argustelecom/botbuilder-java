// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.schema.models.ActionTypes;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.CardAction;
import com.microsoft.bot.schema.models.InputHints;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang3.StringUtils;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;




public class ChoiceFactory
{

	public static Activity ForChannel(String channelId, java.util.List<Choice> list, String text, String speak)
	{
		return ForChannel(channelId, list, text, speak, null);
	}

	public static Activity ForChannel(String channelId, java.util.List<Choice> list, String text)
	{
		return ForChannel(channelId, list, text, null, null);
	}

	public static Activity ForChannel(String channelId, java.util.List<Choice> list)
	{
		return ForChannel(channelId, list, null, null, null);
	}

	public static Activity ForChannel(String channelId, List<Choice> list, String text, String speak, ChoiceFactoryOptions options)
	{
		channelId = (channelId != null) ? channelId : "";

		list = (list != null) ? list : new ArrayList<Choice>();

		// Find maximum title length
		int maxTitleLength = 0;
		for (Choice choice : list)
		{
			boolean l = choice.action() != null && StringUtils.isBlank(choice.action().title()) ? choice.action().title().length() : choice.value().length();
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

	public static Activity Inline(List<Choice> choices, String text)
	{
		return Inline(choices, text, null, null);
	}

	public static Activity Inline(List<Choice> choices)
	{
		return Inline(choices, null, null, null);
	}

	public static Activity Inline(List<Choice> choices, String text, String speak, ChoiceFactoryOptions options)
	{
		choices = (choices != null) ? choices : new ArrayList<Choice>();
		options = (options != null) ? options : new ChoiceFactoryOptions();

		ChoiceFactoryOptions opt = new ChoiceFactoryOptions();
		String tempVar = options.inlineSeparator();
		opt.withInlineSeparator((tempVar != null) ? tempVar : ", ");
		String tempVar2 = options.inlineOr();
		opt.withInlineOr((tempVar2 != null) ? tempVar2 : " or ");
		String tempVar3 = options.inlineOrMore();
		opt.withInlineOrMore((tempVar3 != null) ? tempVar3 : ", or ");
		Optional<Boolean> tempVar4 = options.includeNumbers();
		opt.withIncludeNumbers((tempVar4 != null) ? tempVar4 : Optional.of(true));

		// Format list of choices
		String connector = "";
		String txt = (text != null) ? text : "";
		txt += " ";

		for (int index = 0; index < choices.size(); index++)
		{
			Choice choice = choices.get(index);

			boolean title = choice.action() != null && choice.action().title() != null ? choice.action().title() : choice.value();

			txt += String.format("%1$s", connector);
			if (opt.includeNumbers().get())
			{
				txt += "(" + (String.valueOf(index)) + ") ";
			}

			txt += String.format("%1$s", title);
			if (index == (choices.size() - 2))
			{
				connector = ((index == 0 ? opt.inlineOr() : opt.inlineOrMore()) != null) ? (index == 0 ? opt.inlineOr() : opt.inlineOrMore()) : "";
			}
			else
			{
				String tempVar5 = opt.inlineSeparator();
				connector = (tempVar5 != null) ? tempVar5 : "";
			}
		}

		txt += "";

		// Return activity with choices as an inline list.
		return MessageFactory.Text(txt, speak, InputHints.EXPECTING_INPUT.toString());
	}


	public static Activity List(List<String> choices, String text, String speak)
	{
		return List(choices, text, speak, null);
	}

	public static Activity List(List<String> choices, String text)
	{
		return List(choices, text, null, null);
	}

	public static Activity List(List<String> choices)
	{
		return List(choices, null, null, null);
	}

	public static Activity List(List<String> choices, String text, String speak, ChoiceFactoryOptions options)
	{
		return ListChoice(ToChoices(choices), text, speak, options);
	}


	public static Activity ListChoice(List<Choice> choices, String text, String speak)
	{
		return ListChoice(choices, text, speak, null);
	}

	public static Activity ListChoice(List<Choice> choices, String text)
	{
		return ListChoice(choices, text, null, null);
	}

	public static Activity ListChoice(List<Choice> choices)
	{
		return ListChoice(choices, null, null, null);
	}

	public static Activity ListChoice(List<Choice> choices, String text, String speak, ChoiceFactoryOptions options)
	{
		choices = (choices != null) ? choices : new ArrayList<Choice>();
		options = (options != null) ? options : new ChoiceFactoryOptions();

		Optional<Boolean> tempVar = options.includeNumbers();
        Optional<Boolean>  includeNumbers = (tempVar != null) ? tempVar : Optional.of(true);

		// Format list of choices
		String connector = "";
		String txt = (text != null) ? text : "";
		txt += "\n\n   ";

		for (int index = 0; index < choices.size(); index++)
		{
			Choice choice = choices.get(index);

			boolean title = choice.action() != null && choice.action().title() != null ? choice.action().title() : choice.value();

			txt += connector;
			if (includeNumbers)
			{
				txt += (String.valueOf(index)) + ". ";
			}
			else
			{
				txt += "- ";
			}

			txt += title;
			connector = "\n   ";
		}

		// Return activity with choices as a numbered list.
		return MessageFactory.Text(txt, speak, InputHints.EXPECTING_INPUT.toString());
	}


	public static Activity SuggestedAction(List<String> choices, String text)
	{
		return SuggestedAction(choices, text, null);
	}

	public static Activity SuggestedAction(java.util.List<String> choices)
	{
		return SuggestedAction(choices, null, null);
	}

	public static Activity SuggestedAction(List<String> choices, String text, String speak)
	{
		return SuggestedAction(ToChoices(choices), text, speak);
	}


	public static Activity SuggestedActionChoice(List<Choice> choices, String text)
	{
		return SuggestedActionChoice(choices, text, null);
	}

	public static Activity SuggestedActionChoice(List<Choice> choices)
	{
		return SuggestedActionChoice(choices, null, null);
	}

	public static Activity SuggestedActionChoice(List<Choice> choices, String text, String speak)
	{
		choices = (choices != null) ? choices : new ArrayList<Choice>();

		// Map choices to actions
		ArrayList<CardAction> actions = choices.stream().map((choice) ->
		{
				if (choice.action() != null)
				{
					return choice.action();
				}
				else
				{
					CardAction tempVar = new CardAction()
						.withType(ActionTypes.IM_BACK)
						.withValue(choice.value())
						.withTitle(choice.value());
					return tempVar;
				}
		}).collect(Collectors.toCollection(ArrayList::new));

		// Return activity with choices as suggested actions
		return MessageFactory.SuggestedActionsCard(actions, text, speak, InputHints.EXPECTING_INPUT.toString());
	}

	public static List<Choice> ToChoices(List<String> choices)
	{
	    if (choices == null)
        {
            return (List<Choice>) new ArrayList<Choice>();
        }
		return choices.stream().map(choice -> new Choice().withValue(choice)).collect(Collectors.toList());
	}
}