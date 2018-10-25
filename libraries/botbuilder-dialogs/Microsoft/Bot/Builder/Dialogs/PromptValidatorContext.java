package Microsoft.Bot.Builder.Dialogs;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class PromptValidatorContext<T>
{
	public PromptValidatorContext(ITurnContext turnContext, PromptRecognizerResult<T> recognized, Map<String, Object> state, PromptOptions options)
	{
		Context = turnContext;
		Options = options;
		Recognized = recognized;
		State = state;
	}

	private ITurnContext Context;
	public final ITurnContext getContext()
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