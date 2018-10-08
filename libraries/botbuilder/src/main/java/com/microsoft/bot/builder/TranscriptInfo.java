package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


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
	public final String getChannelId()
	{
		return ChannelId;
	}
	public final void setChannelId(String value)
	{
		ChannelId = value;
	}

	/** 
	 Gets or sets the ID of the conversation.
	 
	 <value>The ID of the conversation.</value>
	*/
	private String Id;
	public final String getId()
	{
		return Id;
	}
	public final void setId(String value)
	{
		Id = value;
	}

	/** 
	 Gets or sets the date the conversation began.
	 
	 <value>The date then conversation began.</value>
	*/
	private DateTimeOffset Created = new DateTimeOffset();
	public final DateTimeOffset getCreated()
	{
		return Created;
	}
	public final void setCreated(DateTimeOffset value)
	{
		Created = value;
	}
}