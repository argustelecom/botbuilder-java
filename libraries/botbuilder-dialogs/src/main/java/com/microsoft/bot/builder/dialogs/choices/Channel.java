// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs.choices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.dialogs.*;
import com.microsoft.bot.connector.Channels;
import org.apache.commons.lang3.StringUtils;

public class Channel
{

	public static boolean SupportsSuggestedActions(String channelId)
	{
		return SupportsSuggestedActions(channelId, 100);
	}

	public static boolean SupportsSuggestedActions(String channelId, int buttonCnt)
	{
		switch (channelId)
		{
			case Channels.Facebook:
			case Channels.Skype:
				return buttonCnt <= 10;

			case Channels.Kik:
				return buttonCnt <= 20;

			case Channels.Slack:
			case Channels.Telegram:
			case Channels.Emulator:
			case Channels.Directline:
			case Channels.Webchat:
				return buttonCnt <= 100;

			default:
				return false;
		}
	}


	public static boolean SupportsCardActions(String channelId)
	{
		return SupportsCardActions(channelId, 100);
	}

	public static boolean SupportsCardActions(String channelId, int buttonCnt)
	{
		switch (channelId)
		{
			case Channels.Facebook:
			case Channels.Skype:
			case Channels.Msteams:
				return buttonCnt <= 3;

			case Channels.Slack:
			case Channels.Emulator:
			case Channels.Directline:
			case Channels.Webchat:
			case Channels.Cortana:
				return buttonCnt <= 100;

			default:
				return false;
		}
	}

	public static boolean HasMessageFeed(String channelId)
	{
		switch (channelId)
		{
			case Channels.Cortana:
				return false;

			default:
				return true;
		}
	}

	public static int MaxActionTitleLength(String channelId)
	{
		return 20;
	}

	public static String GetChannelId(TurnContext turnContext)
	{
		return StringUtils.isBlank(turnContext.activity().channelId()) ? "" : turnContext.activity().channelId();
	}

}