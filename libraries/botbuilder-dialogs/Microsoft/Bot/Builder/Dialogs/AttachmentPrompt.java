package Microsoft.Bot.Builder.Dialogs;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


public class AttachmentPrompt extends Prompt<List<Attachment>>
{

	public AttachmentPrompt(String dialogId)
	{
		this(dialogId, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public AttachmentPrompt(string dialogId, PromptValidator<IList<Attachment>> validator = null)
	public AttachmentPrompt(String dialogId, PromptValidator<List<Attachment>> validator)
	{
		super(dialogId, validator);
	}


	@Override
	protected Task OnPromptAsync(ITurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options, boolean isRetry)
	{
		return OnPromptAsync(turnContext, state, options, isRetry, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: protected override async Task OnPromptAsync(ITurnContext turnContext, IDictionary<string, object> state, PromptOptions options, bool isRetry, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	protected Task OnPromptAsync(ITurnContext turnContext, Map<String, Object> state, PromptOptions options, boolean isRetry, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		if (options == null)
		{
			throw new NullPointerException("options");
		}

		if (isRetry && options.getRetryPrompt() != null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await turnContext.SendActivityAsync(options.getRetryPrompt(), cancellationToken).ConfigureAwait(false);
		}
		else if (options.getPrompt() != null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await turnContext.SendActivityAsync(options.getPrompt(), cancellationToken).ConfigureAwait(false);
		}
	}


	@Override
	protected Task<PromptRecognizerResult<java.util.List<Attachment>>> OnRecognizeAsync(ITurnContext turnContext, java.util.Map<String, Object> state, PromptOptions options)
	{
		return OnRecognizeAsync(turnContext, state, options, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected override Task<PromptRecognizerResult<IList<Attachment>>> OnRecognizeAsync(ITurnContext turnContext, IDictionary<string, object> state, PromptOptions options, CancellationToken cancellationToken = default(CancellationToken))
	@Override
	protected Task<PromptRecognizerResult<List<Attachment>>> OnRecognizeAsync(ITurnContext turnContext, Map<String, Object> state, PromptOptions options, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		PromptRecognizerResult<List<Attachment>> result = new PromptRecognizerResult<List<Attachment>>();
		if (turnContext.Activity.Type == ActivityTypes.Message)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var message = turnContext.Activity.AsMessageActivity();
			if (message.Attachments != null && message.Attachments.size() > 0)
			{
				result.setSucceeded(true);
				result.setValue(message.Attachments);
			}
		}

		return Task.FromResult(result);
	}
}