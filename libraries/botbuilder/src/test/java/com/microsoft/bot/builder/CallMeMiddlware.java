package com.microsoft.bot.builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class CallMeMiddlware implements Middleware {
    private ActionDel callMe;

    public CallMeMiddlware(ActionDel callme) {
        this.callMe = callme;
    }

    @Override
    public CompletableFuture OnTurnAsync(TurnContext context, NextDelegate next) {
        return CompletableFuture.runAsync(() -> {
            this.callMe.CallMe();
            try {
                next.invoke().get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
    }
}
