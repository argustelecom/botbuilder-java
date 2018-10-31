// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.BotAssert;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.microsoft.bot.schema.models.Attachment;
import com.microsoft.bot.schema.models.TokenResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.time.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;


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

	public OAuthPrompt(String dialogId, OAuthPromptSettings settings, PromptValidator<TokenResponse> validator)
	{
		super(dialogId);
		if (settings == null)
        {
            throw new NullPointerException("settings");
        }
		_settings = settings;
		_validator = (PromptValidatorContext promptContext ) -> validator.invoke(promptContext);
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


	@Override
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc, Object options )
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
				if (opt.getPrompt() != null && StringUtils.isBlank(opt.getPrompt().InputHint))
				{
					opt.getPrompt().InputHint = InputHints.ExpectingInput;
				}

				if (opt.getRetryPrompt() != null && StringUtils.isBlank(opt.getRetryPrompt().InputHint))
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


        TokenResponse output = GetUserTokenAsync(dc.getContext()).get();
		if (output != null)
		{
			// Return token

			return dc.EndDialogAsync(output).get();
		}
		else
		{
			// Prompt user to login

			SendOAuthCardAsync(dc.getContext(), opt == null ? null : opt.getPrompt()).get();
			return Dialog.EndOfTurn;
		}
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc)
	{
		return ContinueDialogAsync(dc, null);
	}


	@Override
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc )
	{
		if (dc == null)
		{
			throw new NullPointerException("dc");
		}

		// Recognize token
        PromptRecognizerResult recognized = RecognizeTokenAsync(dc.getContext()).get();

		// Check for timeout
		Map<String, Object> state = dc.getActiveDialog().getState();
		LocalDateTime expires = (LocalDateTime)state.get(PersistedExpires);
		boolean isMessage = dc.getContext().activity().type() == ActivityTypes.MESSAGE;
		boolean hasTimedOut = isMessage && (LocalDateTime.Compare(LocalDateTime.now(), expires) > 0);

		if (hasTimedOut)
		{
			// if the token fetch request timesout, complete the prompt with no result.

            try {
                return dc.EndDialogAsync().get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
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

				isValid = _validator.invoke(promptContext).get();
			}
			else if (recognized.getSucceeded())
			{
				isValid = true;
			}

			// Return recognized value or re-prompt
			if (isValid)
			{

				return dc.EndDialogAsync(recognized.getValue()).get();
			}
			else
			{
				if (!dc.getContext().responded() && isMessage && promptOptions != null && promptOptions.getRetryPrompt() != null)
				{

					dc.getContext().SendActivityAsync(promptOptions.getRetryPrompt()).get();
				}

				return Dialog.EndOfTurn;
			}
		}
	}

	/** 
	 Get a token for a user signed in.
	 
	 @param turnContext Context for the current turn of the conversation with the user.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final CompletableFuture<TokenResponse> GetUserTokenAsync(TurnContext turnContext)
	{
		return GetUserTokenAsync(turnContext, null);
	}


	public final CompletableFuture<TokenResponse> GetUserTokenAsync(TurnContext turnContext )
	{
		String magicCode = null;
		if (!(turnContext.adapter() instanceof BotFrameworkAdapter adapter))
		{
			throw new IllegalStateException("OAuthPrompt.GetUserToken(): not supported by the current adapter");
		}

		if (IsTeamsVerificationInvoke(turnContext))
		{
			JObject value = turnContext.activity().Value instanceof JObject ? (JObject)turnContext.Activity.Value : null;
			magicCode = value.GetValue("state") == null ? null : value.GetValue("state").toString();
		}

		if (turnContext.activity().type() == ActivityTypes.MESSAGE && _magicCodeRegex.IsMatch(turnContext.Activity.Text))
		{
			magicCode = turnContext.activity().text();
		}


		return adapter.GetUserTokenAsync(turnContext, _settings.getConnectionName(), magicCode).get();
	}

	/** 
	 Sign Out the User.
	 
	 @param turnContext Context for the current turn of the conversation with the user.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final CompletableFuture SignOutUserAsync(TurnContext turnContext)
	{
		return SignOutUserAsync(turnContext, null);
	}


	public final CompletableFuture SignOutUserAsync(TurnContext turnContext )
	{
		if (!(turnContext.adapter() instanceof BotFrameworkAdapter adapter))
		{
			throw new IllegalStateException("OAuthPrompt.SignOutUser(): not supported by the current adapter");
		}

		// Sign out user

		adapter.SignOutUserAsync(turnContext, _settings.getConnectionName(), turnContext.activity() == null ? null : (turnContext.Activity.From == null ? null : turnContext.Activity.From.Id)).get();
	}


	private CompletableFuture SendOAuthCardAsync(TurnContext turnContext, IMessageActivity prompt)
	{
		return SendOAuthCardAsync(turnContext, prompt, null);
	}


	private CompletableFuture SendOAuthCardAsync(TurnContext turnContext, IMessageActivity prompt )
	{
		BotAssert.ContextNotNull(turnContext);

		if (!(turnContext.adapter() instanceof BotFrameworkAdapter adapter))
		{
			throw new IllegalStateException("OAuthPrompt.Prompt(): not supported by the current adapter");
		}

		// Ensure prompt initialized
		if (prompt == null)
		{
			prompt = ActivityImpl.CreateMessageActivity();
		}

		if (prompt.Attachments == null)
		{
			prompt.Attachments = new ArrayList<Attachment>();
		}

		// Append appropriate card if missing
		if (!ChannelSupportsOAuthCard(turnContext.activity().channelId()))
		{
			if (!prompt.Attachments.Any(a -> a.Content instanceof SigninCard))
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:

				var link = await adapter.GetOauthSignInLinkAsync(turnContext, _settings.getConnectionName()).get();
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
		if (StringUtils.isBlank(prompt.InputHint))
		{
			prompt.InputHint = InputHints.ExpectingInput;
		}


		turnContext.SendActivityAsync(prompt).get();
	}


	private CompletableFuture<PromptRecognizerResult<TokenResponse>> RecognizeTokenAsync(TurnContext turnContext)
	{
		return RecognizeTokenAsync(turnContext, null);
	}


	private CompletableFuture<PromptRecognizerResult<TokenResponse>> RecognizeTokenAsync(TurnContext turnContext )
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

			if (!(turnContext.adapter() instanceof BotFrameworkAdapter adapter))
			{
				throw new IllegalStateException("OAuthPrompt.Recognize(): not supported by the current adapter");
			}


			var token = adapter.GetUserTokenAsync(turnContext, _settings.getConnectionName(), magicCode).get();
			if (token != null)
			{
				result.setSucceeded(true);
				result.setValue(token);
			}
		}
		else if (turnContext.activity().type() == ActivityTypes.MESSAGE)
		{
			System.Text.RegularExpressions.Match matched = _magicCodeRegex.Match(turnContext.Activity.Text);
			if (matched.Success)
			{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (!(turnContext.Adapter is BotFrameworkAdapter adapter))
				if (!(turnContext.adapter() instanceof BotFrameworkAdapter adapter))
				{
					throw new IllegalStateException("OAuthPrompt.Recognize(): not supported by the current adapter");
				}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:

				var token = adapter.GetUserTokenAsync(turnContext, _settings.getConnectionName(), matched.Value).get();
				if (token != null)
				{
					result.setSucceeded(true);
					result.setValue(token);
				}
			}
		}

		return result;
	}

	private boolean IsTokenResponseEvent(TurnContext turnContext)
	{
		Activity activity = turnContext.activity();
		return activity.type() == ActivityTypes.EVENT && activity.name().equals("tokens/response");
	}

	private boolean IsTeamsVerificationInvoke(TurnContext turnContext)
	{
		Activity activity = turnContext.activity();
		return activity.type() == ActivityTypes.INVOKE && activity.name().equals("signin/verifyState");
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