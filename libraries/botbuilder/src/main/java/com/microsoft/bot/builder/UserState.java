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
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}
		if (turnContext.activity() == null)
		{
			throw new NullPointerException("activity");
		}
		if (turnContext.activity().channelId() == null)
		{
			throw new NullPointerException("invalid activity-missing channelId");
		}

		String channelId = turnContext.activity().channelId();
		String userId = null;
		if (turnContext.activity().from() != null)
		{
			if (turnContext.activity().from().id() == null) {
				throw new NullPointerException("invalid activity-missing from id");
			}
			userId = turnContext.activity().from().id();
		}

		return String.format("%1$s/users/%2$s", channelId, userId);
	}
}