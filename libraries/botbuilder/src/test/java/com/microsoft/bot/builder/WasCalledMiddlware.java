package com.microsoft.bot.builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class WasCalledMiddlware implements Middleware {
    boolean called = false;
    public boolean getCalled() {
        return this.called;
    }
    public void setCalled(boolean called) {
        this.called = called;
    }

    public CompletableFuture OnTurnAsync(TurnContext context, NextDelegate next) {
        return CompletableFuture.runAsync(() -> {
            setCalled(true);
            try {
                next.invoke().get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
    }
}
