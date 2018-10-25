// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import java.util.*;
import com.microsoft.bot.builder.TurnContext;

public class PromptValidatorContext<T>
{
	public PromptValidatorContext(TurnContext turnContext, PromptRecognizerResult<T> recognized, Map<String, Object> state, PromptOptions options)
	{
		Context = turnContext;
		Options = options;
		Recognized = recognized;
		State = state;
	}

	private TurnContext Context;
	public final TurnContext getContext()
	{
		return Context;
	}

	private PromptRecognizerResult<T> Recognized;
	public final PromptRecognizerResult<T> getRecognized()
	{
		return Recognized;
	}

	private PromptOptions Options;
	public final PromptOptions getOptions()
	{
		return Options;
	}

	private Map<String, Object> State;
	public final Map<String, Object> getState()
	{
		return State;
	}
}