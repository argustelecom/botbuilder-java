package com.microsoft.bot.builder.adapters;

import com.microsoft.bot.builder.Middleware;
import com.microsoft.bot.builder.BotState;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.integration.BotFrameworkPaths;
import com.microsoft.bot.connector.authentication.CredentialProvider;
import com.microsoft.bot.connector.authentication.ChannelProvider;
import com.microsoft.bot.connector.authentication.SimpleCredentialProvider;
import com.microsoft.rest.retry.RetryStrategy;
import okhttp3.OkHttpClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.



/** 
 Contains settings that your ASP.NET application uses to initialize the <see cref="BotAdapter"/>
 that it adds to the HTTP request pipeline.
*/
public class BotFrameworkOptions
{
	/** 
	 Initializes a new instance of the <see cref="BotFrameworkOptions"/> class.
	*/
	public BotFrameworkOptions()
	{
	}

	/** 
	 Gets or sets an <see cref="ICredentialProvider"/> that should be used to store and retrieve the
	 credentials used during authentication with the Bot Framework Service.
	 
	 <value>The credential provider.</value>
	*/
	private CredentialProvider CredentialProvider = new SimpleCredentialProvider();
	public final CredentialProvider getCredentialProvider()
	{
		return CredentialProvider;
	}
	public final void setCredentialProvider(CredentialProvider value)
	{
		CredentialProvider = value;
	}

	/** 
	 Gets or sets an <see cref="IChannelProvider"/> that should be used to provide configuration for
	 how to validate authentication tokens received from the Bot Framework Service.
	 
	 <value>The credential provider.</value>
	*/
	private ChannelProvider ChannelProvider;
	public final ChannelProvider getChannelProvider()
	{
		return ChannelProvider;
	}
	public final void setChannelProvider(ChannelProvider value)
	{
		ChannelProvider = value;
	}

	/** 
	 Gets or sets an error handler to use to catche exceptions in the middleware or application.
	 
	 <value>The error handler.</value>
	*/
	private BiFunction<TurnContext, RuntimeException, CompletableFuture> OnTurnError;
	public final BiFunction<TurnContext, RuntimeException, CompletableFuture> getOnTurnError()
	{
		return OnTurnError;
	}
	public final CompletableFuture setOnTurnError(BiFunction<TurnContext, RuntimeException, CompletableFuture> value)
	{
		OnTurnError = (TurnContext arg1, RuntimeException arg2) -> value.apply(arg1, arg2);
	}

	/** 
	 Gets a list of the <see cref="Middleware"/> to use on each incoming activity.
	 
	 <value>The middleware list.</value>
	 {@link BotAdapter.Use(IMiddleware)}
	*/
	private List<Middleware> Middleware = new ArrayList<Middleware> ();
	public final List<Middleware> getMiddleware()
	{
		return Middleware;
	}

	/** 
	 Gets a list of the <see cref="BotState"/> providers to use on each incoming activity.
	 Objects in the State list enable other components to get access to the state providers
	 during the start up process.  For example, creating state property accessors within a ASP.net Core Singleton
	 that could be passed to your Bot-derived class.
	 The providers in this list are not associated with the BotStateSet Middleware component. To clarify, state providers
	 in this list are not automatically loaded or saved during the turn process.
	 
	 <value>The list of property state providers.</value>
	*/
	private List<BotState> State = new ArrayList<BotState> ();
	public final List<BotState> getState()
	{
		return State;
	}

	/** 
	 Gets or sets the retry policy to use in case of errors from Bot Framework Service.
	 
	 <value>The retry policy.</value>
	*/
	private RetryStrategy ConnectorClientRetryPolicy;
	public final RetryStrategy getConnectorClientRetryPolicy()
	{
		return ConnectorClientRetryPolicy;
	}
	public final void setConnectorClientRetryPolicy(RetryStrategy value)
	{
		ConnectorClientRetryPolicy = value;
	}

	/** 
	 Gets or sets the <see cref="HttpClient"/> instance that should be used to make requests to the Bot Framework Service.
	 
	 <value>The HTTP client.</value>
	*/
	private OkHttpClient HttpClient;
	public final OkHttpClient getHttpClient()
	{
		return HttpClient;
	}
	public final void setHttpClient(OkHttpClient value)
	{
		HttpClient = value;
	}

	/** 
	 Gets or sets what paths should be used when exposing the various bot endpoints.
	 
	 <value>The path strings.</value>
	 {@link BotFrameworkPaths}
	*/
	private BotFrameworkPaths Paths = new BotFrameworkPaths();
	public final BotFrameworkPaths getPaths()
	{
		return Paths;
	}
	public final void setPaths(BotFrameworkPaths value)
	{
		Paths = value;
	}
}