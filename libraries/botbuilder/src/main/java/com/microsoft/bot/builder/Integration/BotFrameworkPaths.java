package com.microsoft.bot.builder.integration;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class BotFrameworkPaths
{
	public BotFrameworkPaths()
	{
		setBasePath("/api");
		setMessagesPath("/messages");
	}

	/** 
	 Gets or sets the base path at which the bot's endpoints should be exposed.
	 
	 <value>
	 A string that represents the base URL at which the bot should be exposed.
	 </value>
	*/
	private String BasePath;
	public final String getBasePath()
	{
		return BasePath;
	}
	public final void setBasePath(String value)
	{
		BasePath = value;
	}

	/** 
	 Gets or sets the path, relative to the <see cref="BasePath"/>, at which the bot framework messages are expected to be delivered.
	 
	 <value>
	 A string that represents the URL at which the bot framework messages are expected to be delivered.
	 </value>
	*/
	private String MessagesPath;
	public final String getMessagesPath()
	{
		return MessagesPath;
	}
	public final void setMessagesPath(String value)
	{
		MessagesPath = value;
	}
}