package com.microsoft.bot.builder;

import java.util.concurrent.CompletableFuture;

public class DoNotCallNextMiddleware implements Middleware {
    private final ActionDel _callMe;

    public DoNotCallNextMiddleware(ActionDel callMe) {
        _callMe = callMe;
    }

    public CompletableFuture OnTurnAsync(TurnContext context, NextDelegate next) {
        return CompletableFuture.runAsync(() -> {
            _callMe.CallMe();
            // DO NOT call NEXT
        });
    }
}
