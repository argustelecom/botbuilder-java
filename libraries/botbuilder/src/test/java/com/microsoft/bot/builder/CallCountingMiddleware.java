package com.microsoft.bot.builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class CallCountingMiddleware implements Middleware
{
    private int _calls;
    public final int calls()
    {
        return _calls;
    }
    public final CallCountingMiddleware withCalls(int value)
    {
        _calls = value;
        return this;
    }
    public final CompletableFuture OnTurnAsync(TurnContext turnContext, NextDelegate next)
    {
        return CompletableFuture.runAsync(() -> {
            withCalls(calls() + 1);
            try {
                next.invoke().get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
    }
}
