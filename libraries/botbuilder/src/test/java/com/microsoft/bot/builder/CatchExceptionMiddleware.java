package com.microsoft.bot.builder;

import com.microsoft.bot.schema.ActivityImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class CatchExceptionMiddleware implements Middleware
{
    public final CompletableFuture OnTurnAsync(TurnContext turnContext, NextDelegate next)
    {
        return CompletableFuture.runAsync(() -> {
            try {
                turnContext.SendActivityAsync(((ActivityImpl)turnContext.activity()).CreateReply("BEFORE")).get();
            } catch (Exception e) {
                e.printStackTrace();
                // Not a part of the test.  Should fail.
                throw new CompletionException(e);
            }
            try
            {
                next.invoke().get();
            }
            catch (RuntimeException|InterruptedException|ExecutionException ex)
            {
                ex.printStackTrace();
                try {
                    turnContext.SendActivityAsync(((ActivityImpl)turnContext.activity()).CreateReply("CAUGHT:" + ex.getMessage())).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Not a part of the test.  Should fail.
                    throw new CompletionException(e);
                }

            }

            try {
                turnContext.SendActivityAsync(((ActivityImpl)turnContext.activity()).CreateReply("AFTER"));
            } catch (Exception e) {
                e.printStackTrace();
                // Not a part of the test.  Should fail.
                throw new CompletionException(e);
            }
        });
    }

}
