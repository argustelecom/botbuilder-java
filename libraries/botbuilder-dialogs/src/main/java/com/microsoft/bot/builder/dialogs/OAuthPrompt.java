package com.microsoft.bot.builder.dialogs;

import Newtonsoft.Json.Linq.*;
import java.util.*;
import java.time.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Creates a new prompt that asks the user to sign in using the Bot Frameworks Single Sign On (SSO)
 service.

 @remarks
 The prompt will attempt to retrieve the users current token and if the user isn't signed in, it
 will send them an `OAuthCard` containing a button they can press to signin. Depending on the
 channel, the user will be sent through one of two possible signin flows:

 - The automatic signin flow where once the user signs in and the SSO service will forward the bot
 the users access token using either an `event` or `invoke` activity.
 - The "magic code" flow where where once the user signs in they will be prompted by the SSO
 service to send the bot a six digit code confirming their identity. This code will be sent as a
 standard `message` activity.

 Both flows are automatically supported by the `OAuthPrompt` and the only thing you need to be
 careful of is that you don't block the `event` and `invoke` activities that the prompt might
 be waiting on.

 > [!NOTE]
 > You should avoid persisting the access token with your bots other state. The Bot Frameworks
 > SSO service will securely store the token on your behalf. If you store it in your bots state
 > it could expire or be revoked in between turns.
 >
 > When calling the prompt from within a waterfall step you should use the token within the step
 > following the prompt and then let the token go out of scope at the end of your function.

 #### Prompt Usage

 When used with your bots `DialogSet` you can simply add a new instance of the prompt as a named
 dialog using `DialogSet.add()`. You can then start the prompt from a waterfall step using either
 `DialogContext.begin()` or `DialogContext.prompt()`. The user will be prompted to signin as
 needed and their access token will be passed as an argument to the callers next waterfall step.
*/
public class OAuthPrompt extends Dialog
{
	private static final String PersistedOptions = "options";
	private static final String PersistedState = "state";
	private static final String PersistedExpires = "expires";

		// Default prompt timeout of 15 minutes (in ms)
	private static final int DefaultPromptTimeout = 54000000;

	// regex to check if code supplied is a 6 digit numerical code (hence, a magic code).
	private final Regex _magicCodeRegex = new Regex("(\\d{6})");

	private OAuthPromptSettings _settings;
	private PromptValidator<TokenResponse> _validator;


	public OAuthPrompt(String dialogId, OAuthPromptSettings settings)
	{
		this(dialogId, settings, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public OAuthPrompt(string dialogId, OAuthPromptSettings settings, PromptValidator<TokenResponse> validator = null)
	public OAuthPrompt(String dialogId, OAuthPromptSettings settings, PromptValidator<TokenResponse> validator)
	{
		super(dialogId);
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: _settings = settings ?? throw new ArgumentNullException(nameof(settings));
		_settings = (settings != null) ? settings : throw new NullPointerException("settings");
		_validator = (PromptValidatorContext promptContext, CancellationToken cancellationToken) -> validator.invoke(promptContext, cancellationToken);
	}


	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options)
	{
		return BeginDialogAsync(dc, options, null);
	}

	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc)
	{
		return BeginDialogAsync(dc, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, object options = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options, CancellationToken cancellationToken)
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		PromptOptions opt = null;
		if (options != null)
		{
			if (options instanceof PromptOptions)
			{
				// Ensure prompts have input hint set
				opt = options instanceof PromptOptions ? (PromptOptions)options : null;
				if (opt.getPrompt() != null && tangible.StringHelper.isNullOrEmpty(opt.getPrompt().InputHint))
				{
					opt.getPrompt().InputHint = InputHints.ExpectingInput;
				}

				if (opt.getRetryPrompt() != null && tangible.StringHelper.isNullOrEmpty(opt.getRetryPrompt().InputHint))
				{
					opt.getRetryPrompt().InputHint = InputHints.ExpectingInput;
				}
			}
			else
			{
				throw new IllegalArgumentException("options");
			}
		}

		// Initialize state
		Nullable<Integer> tempVar = _settings.getTimeout();
		int timeout = (tempVar != null) ? tempVar : DefaultPromptTimeout;
		Map<String, Object> state = dc.getActiveDialog().getState();
		state.put(PersistedOptions, opt);
		state.put(PersistedState, new HashMap<String, Object>());
		state.put(PersistedExpires, LocalDateTime.now().AddMilliseconds(timeout));

		// Attempt to get the users token
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var output = await GetUserTokenAsync(dc.getContext(), cancellationToken).ConfigureAwait(false);
		if (output != null)
		{
			// Return token
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await dc.EndDialogAsync(output, cancellationToken).ConfigureAwait(false);
		}
		else
		{
			// Prompt user to login
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await SendOAuthCardAsync(dc.getContext(), opt == null ? null : opt.getPrompt(), cancellationToken).ConfigureAwait(false);
			return Dialog.EndOfTurn;
		}
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc)
	{
		return ContinueDialogAsync(dc, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public override async CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc, CancellationToken cancellationToken)
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		// Recognize token
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var recognized = await RecognizeTokenAsync(dc.getContext(), cancellationToken).ConfigureAwait(false);

		// Check for timeout
		Map<String, Object> state = dc.getActiveDialog().getState();
		LocalDateTime expires = (LocalDateTime)state.get(PersistedExpires);
		boolean isMessage = dc.getContext().Activity.Type == ActivityTypes.Message;
		boolean hasTimedOut = isMessage && (LocalDateTime.Compare(LocalDateTime.now(), expires) > 0);

		if (hasTimedOut)
		{
			// if the token fetch request timesout, complete the prompt with no result.
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			return await dc.EndDialogAsync(cancellationToken).ConfigureAwait(false);
		}
		else
		{
			Map<String, Object> promptState = (Map<String, Object>)state.get(PersistedState);
			PromptOptions promptOptions = (PromptOptions)state.get(PersistedOptions);

			// Validate the return value
			boolean isValid = false;
			if (_validator != null)
			{
				PromptValidatorContext<TokenResponse> promptContext = new PromptValidatorContext<TokenResponse>(dc.getContext(), recognized, promptState, promptOptions);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				isValid = await _validator.invoke(promptContext, cancellationToken).ConfigureAwait(false);
			}
			else if (recognized.Succeeded)
			{
				isValid = true;
			}

			// Return recognized value or re-prompt
			if (isValid)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				return await dc.EndDialogAsync(recognized.Value, cancellationToken).ConfigureAwait(false);
			}
			else
			{
				if (!dc.getContext().Responded && isMessage && promptOptions != null && promptOptions.getRetryPrompt() != null)
				{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
					await dc.getContext().SendActivityAsync(promptOptions.getRetryPrompt(), cancellationToken).ConfigureAwait(false);
				}

				return Dialog.EndOfTurn;
			}
		}
	}

	/** 
	 Get a token for a user signed in.
	 
	 @param turnContext Context for the current turn of the conversation with the user.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final CompletableFuture<TokenResponse> GetUserTokenAsync(ITurnContext turnContext)
	{
		return GetUserTokenAsync(turnContext, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async CompletableFuture<TokenResponse> GetUserTokenAsync(ITurnContext turnContext, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final CompletableFuture<TokenResponse> GetUserTokenAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	{
		String magicCode = null;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (!(turnContext.Adapter is BotFrameworkAdapter adapter))
		if (!(turnContext.Adapter instanceof BotFrameworkAdapter adapter))
		{
			throw new IllegalStateException("OAuthPrompt.GetUserToken(): not supported by the current adapter");
		}

		if (IsTeamsVerificationInvoke(turnContext))
		{
			JObject value = turnContext.Activity.Value instanceof JObject ? (JObject)turnContext.Activity.Value : null;
			magicCode = value.GetValue("state") == null ? null : value.GetValue("state").toString();
		}

		if (turnContext.Activity.Type == ActivityTypes.Message && _magicCodeRegex.IsMatch(turnContext.Activity.Text))
		{
			magicCode = turnContext.Activity.Text;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		return await adapter.GetUserTokenAsync(turnContext, _settings.getConnectionName(), magicCode, cancellationToken).ConfigureAwait(false);
	}

	/** 
	 Sign Out the User.
	 
	 @param turnContext Context for the current turn of the conversation with the user.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task SignOutUserAsync(ITurnContext turnContext)
	{
		return SignOutUserAsync(turnContext, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task SignOutUserAsync(ITurnContext turnContext, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task SignOutUserAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (!(turnContext.Adapter is BotFrameworkAdapter adapter))
		if (!(turnContext.Adapter instanceof BotFrameworkAdapter adapter))
		{
			throw new IllegalStateException("OAuthPrompt.SignOutUser(): not supported by the current adapter");
		}

		// Sign out user
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await adapter.SignOutUserAsync(turnContext, _settings.getConnectionName(), turnContext.Activity == null ? null : (turnContext.Activity.From == null ? null : turnContext.Activity.From.Id), cancellationToken).ConfigureAwait(false);
	}


	private Task SendOAuthCardAsync(ITurnContext turnContext, IMessageActivity prompt)
	{
		return SendOAuthCardAsync(turnContext, prompt, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async Task SendOAuthCardAsync(ITurnContext turnContext, IMessageActivity prompt, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	private Task SendOAuthCardAsync(ITurnContext turnContext, IMessageActivity prompt, CancellationToken cancellationToken)
	{
		BotAssert.ContextNotNull(turnContext);

//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (!(turnContext.Adapter is BotFrameworkAdapter adapter))
		if (!(turnContext.Adapter instanceof BotFrameworkAdapter adapter))
		{
			throw new IllegalStateException("OAuthPrompt.Prompt(): not supported by the current adapter");
		}

		// Ensure prompt initialized
		if (prompt == null)
		{
			prompt = Activity.CreateMessageActivity();
		}

		if (prompt.Attachments == null)
		{
			prompt.Attachments = new ArrayList<Attachment>();
		}

		// Append appropriate card if missing
		if (!ChannelSupportsOAuthCard(turnContext.Activity.ChannelId))
		{
			if (!prompt.Attachments.Any(a -> a.Content instanceof SigninCard))
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				var link = await adapter.GetOauthSignInLinkAsync(turnContext, _settings.getConnectionName(), cancellationToken).ConfigureAwait(false);
				Attachment tempVar = new Attachment();
				tempVar.setContentType(SigninCard.ContentType);
				tempVar.Content = new SigninCard();
				tempVar.Content.Text = _settings.getText();
				CardAction tempVar2 = new CardAction();
				tempVar2.Title = _settings.getTitle();
				tempVar2.Value = link;
				tempVar2.Type = ActionTypes.Signin;
				tempVar.Content.Buttons = new CardAction[] {tempVar2};
				prompt.Attachments.Add(tempVar);
			}
		}
		else if (!prompt.Attachments.Any(a -> a.Content instanceof OAuthCard))
		{
			Attachment tempVar3 = new Attachment();
			tempVar3.setContentType(OAuthCard.ContentType);
			tempVar3.Content = new OAuthCard();
			tempVar3.Content.Text = _settings.getText();
			tempVar3.Content.ConnectionName = _settings.getConnectionName();
			CardAction tempVar4 = new CardAction();
			tempVar4.Title = _settings.getTitle();
			tempVar4.Text = _settings.getText();
			tempVar4.Type = ActionTypes.Signin;
			tempVar3.Content.Buttons = new CardAction[] {tempVar4};
			prompt.Attachments.Add(tempVar3);
		}

		// Set input hint
		if (tangible.StringHelper.isNullOrEmpty(prompt.InputHint))
		{
			prompt.InputHint = InputHints.ExpectingInput;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await turnContext.SendActivityAsync(prompt, cancellationToken).ConfigureAwait(false);
	}


	private CompletableFuture<PromptRecognizerResult<TokenResponse>> RecognizeTokenAsync(ITurnContext turnContext)
	{
		return RecognizeTokenAsync(turnContext, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async CompletableFuture<PromptRecognizerResult<TokenResponse>> RecognizeTokenAsync(ITurnContext turnContext, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	private CompletableFuture<PromptRecognizerResult<TokenResponse>> RecognizeTokenAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	{
		PromptRecognizerResult<TokenResponse> result = new PromptRecognizerResult<TokenResponse>();
		if (IsTokenResponseEvent(turnContext))
		{
			JObject tokenResponseObject = turnContext.Activity.Value instanceof JObject ? (JObject)turnContext.Activity.Value : null;
			boolean token = tokenResponseObject == null ? null : tokenResponseObject.<TokenResponse>ToObject();
			result.setSucceeded(true);
			result.setValue(token);
		}
		else if (IsTeamsVerificationInvoke(turnContext))
		{
			JObject magicCodeObject = turnContext.Activity.Value instanceof JObject ? (JObject)turnContext.Activity.Value : null;
			String magicCode = magicCodeObject.GetValue("state") == null ? null : magicCodeObject.GetValue("state").toString();

//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (!(turnContext.Adapter is BotFrameworkAdapter adapter))
			if (!(turnContext.Adapter instanceof BotFrameworkAdapter adapter))
			{
				throw new IllegalStateException("OAuthPrompt.Recognize(): not supported by the current adapter");
			}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			var token = await adapter.GetUserTokenAsync(turnContext, _settings.getConnectionName(), magicCode, cancellationToken).ConfigureAwait(false);
			if (token != null)
			{
				result.setSucceeded(true);
				result.setValue(token);
			}
		}
		else if (turnContext.Activity.Type == ActivityTypes.Message)
		{
			System.Text.RegularExpressions.Match matched = _magicCodeRegex.Match(turnContext.Activity.Text);
			if (matched.Success)
			{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (!(turnContext.Adapter is BotFrameworkAdapter adapter))
				if (!(turnContext.Adapter instanceof BotFrameworkAdapter adapter))
				{
					throw new IllegalStateException("OAuthPrompt.Recognize(): not supported by the current adapter");
				}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				var token = await adapter.GetUserTokenAsync(turnContext, _settings.getConnectionName(), matched.Value, cancellationToken).ConfigureAwait(false);
				if (token != null)
				{
					result.setSucceeded(true);
					result.setValue(token);
				}
			}
		}

		return result;
	}

	private boolean IsTokenResponseEvent(ITurnContext turnContext)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var activity = turnContext.Activity;
		return activity.Type == ActivityTypes.Event && activity.Name.equals("tokens/response");
	}

	private boolean IsTeamsVerificationInvoke(ITurnContext turnContext)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var activity = turnContext.Activity;
		return activity.Type == ActivityTypes.Invoke && activity.Name.equals("signin/verifyState");
	}

	private boolean ChannelSupportsOAuthCard(String channelId)
	{
		switch (channelId)
		{
			case "msteams":
			case "cortana":
			case "skype":
			case "skypeforbusiness":
				return false;
		}

		return true;
	}
}