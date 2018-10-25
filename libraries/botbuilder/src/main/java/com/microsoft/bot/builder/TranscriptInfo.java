// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import java.time.OffsetDateTime;

/**
 Represents a copy of a conversation.
*/
public class TranscriptInfo
{
	/** 
	 Gets or sets the ID of the channel in which the conversation occurred.
	 
	 <value>The ID of the channel in which the conversation occurred.</value>
	*/
	private String ChannelId;
	public final String channelId()
	{
		return ChannelId;
	}
	public final TranscriptInfo withChannelId(String value)
	{
		ChannelId = value;
		return this;
	}

	/** 
	 Gets or sets the ID of the conversation.
	 
	 <value>The ID of the conversation.</value>
	*/
	private String Id;
	public final String id()
	{
		return Id;
	}
	public final TranscriptInfo withId(String value)
	{
		Id = value;
		return this;
	}

	/** 
	 Gets or sets the date the conversation began.
	 
	 <value>The date then conversation began.</value>
	*/
	private OffsetDateTime Created = OffsetDateTime.now();
	public final OffsetDateTime created()
	{
		return Created;
	}
	public final TranscriptInfo withCreated(OffsetDateTime value)
	{
		Created = value;
		return this;
	}
}