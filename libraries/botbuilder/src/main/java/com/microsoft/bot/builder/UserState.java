package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Handles persistence of a user state object using the user ID as part of the key.
*/
public class UserState extends BotState
{
	/** 
	 Initializes a new instance of the <see cref="UserState"/> class.
	 
	 @param storage The storage provider to use.
	*/
	public UserState(IStorage storage)
	{
		super(storage, "UserState");
	}

	/** 
	 Gets the key to use when reading and writing state to and from storage.
	 
	 @param turnContext The context object for this turn.
	 @return The storage key.
	*/
	@Override
	protected String GetStorageKey(TurnContext turnContext)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: var channelId = turnContext.Activity.ChannelId ?? throw new ArgumentNullException("invalid activity-missing channelId");
		String channelId = ((turnContext.getActivity().channelId()) != null) ? turnContext.getActivity().channelId() : throw new NullPointerException("invalid activity-missing channelId");
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: var userId = turnContext.Activity.From == null ? null : turnContext.Activity.From.Id ?? throw new ArgumentNullException("invalid activity-missing From.Id");
		boolean userId = turnContext.getActivity().getFrom() == null ? null : ((turnContext.getActivity().From.Id) != null) ? turnContext.getActivity().From.Id : throw new NullPointerException("invalid activity-missing From.Id");
		return String.format("%1$s/users/%2$s", channelId, userId);
	}
}