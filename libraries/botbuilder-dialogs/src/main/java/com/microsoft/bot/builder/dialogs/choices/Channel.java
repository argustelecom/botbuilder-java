package Microsoft.Bot.Builder.Dialogs.Choices;

import Microsoft.Bot.Builder.Dialogs.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.



public class Channel
{

	public static boolean SupportsSuggestedActions(String channelId)
	{
		return SupportsSuggestedActions(channelId, 100);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static bool SupportsSuggestedActions(string channelId, int buttonCnt = 100)
	public static boolean SupportsSuggestedActions(String channelId, int buttonCnt)
	{
		switch (channelId)
		{
			case Connector.Channels.Facebook:
			case Connector.Channels.Skype:
				return buttonCnt <= 10;

			case Connector.Channels.Kik:
				return buttonCnt <= 20;

			case Connector.Channels.Slack:
			case Connector.Channels.Telegram:
			case Connector.Channels.Emulator:
			case Connector.Channels.Directline:
			case Connector.Channels.Webchat:
				return buttonCnt <= 100;

			default:
				return false;
		}
	}


	public static boolean SupportsCardActions(String channelId)
	{
		return SupportsCardActions(channelId, 100);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static bool SupportsCardActions(string channelId, int buttonCnt = 100)
	public static boolean SupportsCardActions(String channelId, int buttonCnt)
	{
		switch (channelId)
		{
			case Connector.Channels.Facebook:
			case Connector.Channels.Skype:
			case Connector.Channels.Msteams:
				return buttonCnt <= 3;

			case Connector.Channels.Slack:
			case Connector.Channels.Emulator:
			case Connector.Channels.Directline:
			case Connector.Channels.Webchat:
			case Connector.Channels.Cortana:
				return buttonCnt <= 100;

			default:
				return false;
		}
	}

	public static boolean HasMessageFeed(String channelId)
	{
		switch (channelId)
		{
			case Connector.Channels.Cortana:
				return false;

			default:
				return true;
		}
	}

	public static int MaxActionTitleLength(String channelId)
	{
		return 20;
	}

	public static String GetChannelId(ITurnContext turnContext)
	{
		return tangible.StringHelper.isNullOrEmpty(turnContext.Activity.ChannelId) ? "" : turnContext.Activity.ChannelId;
	}

	// This class has been deprecated in favor of the class in Microsoft.Bot.Connector.Channels located
	// at https://github.com/Microsoft/botbuilder-dotnet/libraries/Microsoft.Bot.Connector/Channels.cs.
	// This change is non-breaking and this class now inherits from the class in the connector library.
	@Deprecated
	public static class Channels extends Connector.Channels
	{
	}
}