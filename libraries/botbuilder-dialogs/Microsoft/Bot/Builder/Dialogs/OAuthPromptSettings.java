package Microsoft.Bot.Builder.Dialogs;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

public class OAuthPromptSettings
{
	private String ConnectionName;
	public final String getConnectionName()
	{
		return ConnectionName;
	}
	public final void setConnectionName(String value)
	{
		ConnectionName = value;
	}

	private String Title;
	public final String getTitle()
	{
		return Title;
	}
	public final void setTitle(String value)
	{
		Title = value;
	}

	private String Text;
	public final String getText()
	{
		return Text;
	}
	public final void setText(String value)
	{
		Text = value;
	}

	private Optional<Integer> Timeout;
	public final Optional<Integer> getTimeout()
	{
		return Timeout;
	}
	public final void setTimeout(Optional<Integer> value)
	{
		Timeout = value;
	}
}