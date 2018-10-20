package com.microsoft.bot.builder.integration;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


import com.microsoft.bot.builder.BotState;
import com.microsoft.bot.builder.IStorage;
import com.microsoft.bot.builder.TurnContext;

/**
 Handles persistence of a conversation state object using the conversation ID as part of the key.
*/
public class ConversationState extends BotState
{
	/** 
	 Initializes a new instance of the <see cref="ConversationState"/> class.
	 
	 @param storage The storage provider to use.
	*/
	public ConversationState(IStorage storage)
	{
		super(storage, "ConversationState");
	}

	/** 
	 Gets the key to use when reading and writing state to and from storage.
	 
	 @param turnContext The context object for this turn.
	 @return The storage key.
	*/
	@Override
	protected String GetStorageKey(TurnContext turnContext)
	{
		if ((turnContext.activity().channelId()) == null) {
			throw new NullPointerException("invalid activity-missing channelId");
		}

		String conversationId = null;
		if (turnContext.activity().conversation() != null) {
			conversationId = turnContext.activity().conversation().id();
			if (conversationId == null) {
				throw new NullPointerException("invalid activity-missing Conversation.Id");
			}
		}

		String channelId = turnContext.activity().channelId();
		return String.format("%1$s/conversations/%2$s", channelId, conversationId);
	}
}