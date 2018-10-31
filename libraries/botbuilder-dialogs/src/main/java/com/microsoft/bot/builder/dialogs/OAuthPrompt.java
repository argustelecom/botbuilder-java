// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.microsoft.bot.builder.BotAssert;
import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.models.*;
import com.microsoft.bot.schema.ActivityImpl;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.lang3.StringUtils;

import java.net.URISyntaxException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.time.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    Pattern _magicCodeRegex = Pattern.compile("(\\d{6})");


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
	public CompletableFuture<DialogTurnResult> BeginDialogAsync(DialogContext dc)
	{
		return BeginDialogAsync(dc, null);
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
				if (opt.getPrompt() != null && StringUtils.isBlank(opt.getPrompt().inputHint().toString()))
				{
					opt.getPrompt().withInputHint(InputHints.EXPECTING_INPUT);
				}

				if (opt.getRetryPrompt() != null && StringUtils.isBlank(opt.getRetryPrompt().inputHint()))
				{
					opt.getRetryPrompt().withInputHint(InputHints.EXPECTING_INPUT);
				}
			}
			else
			{
				throw new IllegalArgumentException("options");
			}
		}

		// Initialize state
		Optional<Integer> tempVar = _settings.getTimeout();
		int timeout = tempVar.orElse(DefaultPromptTimeout);
		Map<String, Object> state = dc.getActiveDialog().getState();
		state.put(PersistedOptions, opt);
		state.put(PersistedState, new HashMap<String, Object>());
		state.put(PersistedExpires, OffsetDateTime.now().plus(Duration.ofMillis(timeout)));

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
	public CompletableFuture<DialogTurnResult> ContinueDialogAsync(DialogContext dc )
	{
	    return CompletableFuture.supplyAsync(() -> {
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

                    try {
                        isValid = _validator.invoke(promptContext).get();
                    } catch (InterruptedException|ExecutionException e) {
                        e.printStackTrace();
                        throw new CompletionException(e);
                    }
                }
                else if (recognized.succeeded())
                {
                    isValid = true;
                }

                // Return recognized value or re-prompt
                if (isValid)
                {

                    try {
                        return dc.EndDialogAsync(recognized.value()).get();
                    } catch (InterruptedException|ExecutionException e) {
                        e.printStackTrace();
                        throw new CompletionException(e);
                    }
                }
                else
                {
                    if (!dc.getContext().responded() && isMessage && promptOptions != null && promptOptions.getRetryPrompt() != null)
                    {

                        try {
                            dc.getContext().SendActivityAsync(promptOptions.getRetryPrompt()).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new CompletionException(e);
                        }
                    }
                    return Dialog.EndOfTurn;
                }
            }
        });
	}

	/** 
	 Get a token for a user signed in.
	 
	 @param turnContext Context for the current turn of the conversation with the user.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/
	public final CompletableFuture<TokenResponse> GetUserTokenAsync(TurnContext turnContext )
	{
		String magicCode = null;

		if (!(turnContext.adapter() instanceof BotFrameworkAdapter))
		{
			throw new IllegalStateException("OAuthPrompt.GetUserToken(): not supported by the current adapter");
		}
        BotFrameworkAdapter adapter = (BotFrameworkAdapter) turnContext.adapter();

		if (IsTeamsVerificationInvoke(turnContext))
		{
            JsonNode value = turnContext.activity().value() instanceof JsonNode ? (JsonNode)turnContext.activity().value() : null;
			magicCode = ((ObjectNode)value).get("state") == null ? null : ((ObjectNode)value).get("state").toString();
		}

        Matcher match =  _magicCodeRegex.matcher(turnContext.activity().text());

		if (turnContext.activity().type() == ActivityTypes.MESSAGE && match.find())
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
	public final CompletableFuture SignOutUserAsync(TurnContext turnContext )
	{
	    return CompletableFuture.runAsync(() -> {
            if (!(turnContext.adapter() instanceof BotFrameworkAdapter))
            {
                throw new IllegalStateException("OAuthPrompt.SignOutUser(): not supported by the current adapter");
            }
            BotFrameworkAdapter adapter = (BotFrameworkAdapter) turnContext.adapter();

            // Sign out user
            try {
                adapter.SignOutUserAsync(turnContext,
                        _settings.getConnectionName(),
                        turnContext.activity() == null ? null : (turnContext.activity().from() == null ? null : turnContext.activity().from().id())).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
	}


	private CompletableFuture SendOAuthCardAsync(TurnContext turnContext, Activity prompt )
	{
		BotAssert.ContextNotNull(turnContext);

		if (!(turnContext.adapter() instanceof BotFrameworkAdapter))
		{
			throw new IllegalStateException("OAuthPrompt.Prompt(): not supported by the current adapter");
		}
        BotFrameworkAdapter adapter = (BotFrameworkAdapter) turnContext.adapter();

		// Ensure prompt initialized
		if (prompt == null)
		{
			prompt = ActivityImpl.CreateMessageActivity();
		}

		if (prompt.attachments() == null)
		{
			prompt.withAttachments(new ArrayList<Attachment>());
		}

		// Append appropriate card if missing
		if (!ChannelSupportsOAuthCard(turnContext.activity().channelId()))
		{
			if (!prompt.attachments().stream().anyMatch(a -> a.content() instanceof SigninCard))
			{
                String link = null;
                try {
                    link = adapter.GetOauthSignInLinkAsync(turnContext, _settings.getConnectionName()).get();
                } catch (InterruptedException|ExecutionException|URISyntaxException|JsonProcessingException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
                Attachment tempVar = new Attachment();
				tempVar.withContentType(SigninCardExt.ContentType);
                SigninCard signin = new SigninCard().withText(_settings.getText());
				tempVar.withContent(signin);
				CardAction tempVar2 = new CardAction();
				tempVar2.withTitle(_settings.getTitle());
				tempVar2.withValue(link);
				tempVar2.withType(ActionTypes.SIGNIN);
                signin.withButtons(new ArrayList<CardAction>() {{ add(tempVar2); }});
				prompt.attachments().add(tempVar);
			}
		}
		else if (!prompt.attachments().stream().anyMatch(a -> a.content() instanceof OAuthCard))
		{
			Attachment tempVar3 = new Attachment();
			tempVar3.withContentType(OAuthCardExt.ContentType);
			OAuthCard var3Content = new OAuthCard().withText(_settings.getText()).withConnectionName(_settings.getConnectionName());
			tempVar3.withContent(var3Content);
			CardAction tempVar4 = new CardAction();
			tempVar4.withTitle(_settings.getTitle());
			tempVar4.withText(_settings.getText());
			tempVar4.withType(ActionTypes.SIGNIN);
			var3Content.withButtons(new ArrayList<CardAction>()   {{ add(tempVar4); }});
			prompt.attachments().add(tempVar3);
		}

		// Set input hint
		if (StringUtils.isBlank(prompt.inputHint().toString()))
		{
			prompt.withInputHint(InputHints.EXPECTING_INPUT);
		}


		turnContext.SendActivityAsync(prompt).get();
	}


	private CompletableFuture<PromptRecognizerResult<TokenResponse>> RecognizeTokenAsync(TurnContext turnContext )
	{
	    return CompletableFuture.supplyAsync(() -> {
            PromptRecognizerResult<TokenResponse> result = new PromptRecognizerResult<TokenResponse>();
            if (IsTokenResponseEvent(turnContext))
            {
                JsonNode tokenResponseObject = turnContext.activity().value() instanceof JsonNode ? (JsonNode)turnContext.activity().value() : null;
                TokenResponse token = tokenResponseObject == null ? null : tokenResponseObject.<TokenResponse>ToObject();
                result.withSucceeded(true);
                result.withValue(token);
            }
            else if (IsTeamsVerificationInvoke(turnContext))
            {
                JsonNode magicCodeObject = turnContext.activity().value() instanceof JsonNode ? (JsonNode)turnContext.activity().value() : null;
                String magicCode = magicCodeObject.GetValue("state") == null ? null : magicCodeObject.GetValue("state").toString();

                if (!(turnContext.adapter() instanceof BotFrameworkAdapter))
                {
                    throw new IllegalStateException("OAuthPrompt.Recognize(): not supported by the current adapter");
                }
                BotFrameworkAdapter adapter = (BotFrameworkAdapter) turnContext.adapter();

                TokenResponse token = adapter.GetUserTokenAsync(turnContext, _settings.getConnectionName(), magicCode).get();
                if (token != null)
                {
                    result.withSucceeded(true);
                    result.withValue(token);
                }
            }
            else if (turnContext.activity().type() == ActivityTypes.MESSAGE)
            {
                if (_magicCodeRegex.matcher(turnContext.activity().text()).find())
                {
                    if (!(turnContext.adapter() instanceof BotFrameworkAdapter))
                    {
                        throw new IllegalStateException("OAuthPrompt.Recognize(): not supported by the current adapter");
                    }
                    BotFrameworkAdapter adapter = (BotFrameworkAdapter) turnContext.adapter();

                    TokenResponse token = adapter.GetUserTokenAsync(turnContext, _settings.getConnectionName(), matched.Value).get();
                    if (token != null)
                    {
                        result.withSucceeded(true);
                        result.withValue(token);
                    }
                }
            }
            return result;
        });
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