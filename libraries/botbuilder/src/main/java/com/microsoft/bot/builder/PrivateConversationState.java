package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Handles persistence of a conversation state object using the conversation.Id and from.Id part of an activity.
*/
public class PrivateConversationState extends BotState
{
	/** 
	 Initializes a new instance of the <see cref="PrivateConversationState"/> class.
	 
	 @param storage The storage provider to use.
	*/
	public PrivateConversationState(Storage storage)
	{
		super(storage, "PrivateConversationState");
	}

	/** 
	 Gets the key to use when reading and writing state to and from storage.
	 
	 @param turnContext The context object for this turn.
	 @return The storage key.
	*/
	@Override
	protected String GetStorageKey(TurnContext turnContext)
	{
		if (turnContext.activity().channelId() == null) {
			throw new NullPointerException("invalid activity-missing channelId");
		}

		String channelId = turnContext.activity().channelId();
		String conversationId = null;
		if (turnContext.activity().conversation() != null) {
			conversationId = turnContext.activity().conversation().id();
			if (conversationId == null) {
				throw new NullPointerException("invalid activity-missing Conversation.Id");
			}
		}


		String userId = null;
		if (turnContext.activity().from() != null) {
			userId = turnContext.activity().from().id();
			if (userId == null) {
				throw new NullPointerException("invalid activity-missing From.Id");
			}
		}

		return String.format("%1$s/conversations/%2$s/users/%3$s", channelId, conversationId, userId);
	}
}