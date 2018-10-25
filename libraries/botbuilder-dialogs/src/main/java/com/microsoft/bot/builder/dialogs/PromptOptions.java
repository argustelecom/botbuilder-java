// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.dialogs.choices.Choice;
import com.microsoft.bot.schema.models.Activity;

import java.util.*;

public class PromptOptions
{
	/** 
	 Gets or sets the initial prompt to send the user as {@link Activity} Activity.
	 
	 <value>
	 The initial prompt to send the user as {@link Activity} Activity.
	 </value>
	*/
	private Activity Prompt;
	public final Activity getPrompt()
	{
		return Prompt;
	}
	public final void setPrompt(Activity value)
	{
		Prompt = value;
	}

	/** 
	 Gets or sets the retry prompt to send the user as {@link Activity} Activity.
	 
	 <value>
	 The retry prompt to send the user as {@link Activity} Activity.
	 </value>
	*/
	private Activity RetryPrompt;
	public final Activity getRetryPrompt()
	{
		return RetryPrompt;
	}
	public final void setRetryPrompt(Activity value)
	{
		RetryPrompt = value;
	}

	private List<Choice> Choices;
	public final List<Choice> getChoices()
	{
		return Choices;
	}
	public final void setChoices(List<Choice> value)
	{
		Choices = value;
	}

	private Object Validations;
	public final Object getValidations()
	{
		return Validations;
	}
	public final void setValidations(Object value)
	{
		Validations = value;
	}
}