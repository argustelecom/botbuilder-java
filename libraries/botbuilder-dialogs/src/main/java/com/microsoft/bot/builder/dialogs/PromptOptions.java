package Microsoft.Bot.Builder.Dialogs;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


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