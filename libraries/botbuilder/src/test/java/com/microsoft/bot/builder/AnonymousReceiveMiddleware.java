// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;


import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 Helper class for defining middleware by using a delegate or anonymous method.
 */
public class AnonymousReceiveMiddleware implements Middleware
{
    private BiFunction<TurnContext, NextDelegate, CompletableFuture> _toCall;

    /**
     Initializes a new instance of the <see cref="AnonymousReceiveMiddleware"/> class.

     @param anonymousMethod The method to use as the middleware's process
     request handler.
     */
    public AnonymousReceiveMiddleware(BiFunction<TurnContext, NextDelegate, CompletableFuture> anonymousMethod)
    {
        if (anonymousMethod == null)
        {
            throw new NullPointerException("anonymousMethod");
        }
        _toCall = anonymousMethod;
    }

    /**
     Uses the method provided in the <see cref="AnonymousReceiveMiddleware"/> to
     process an incoming activity.

     @param turnContext The context object for this turn.
     @param next The delegate to call to continue the bot middleware pipeline.
     @return A task that represents the work queued to execute.
     */
    public final CompletableFuture OnTurnAsync(TurnContext turnContext, NextDelegate next)
    {
        return _toCall.apply(turnContext, next);
    }
}

