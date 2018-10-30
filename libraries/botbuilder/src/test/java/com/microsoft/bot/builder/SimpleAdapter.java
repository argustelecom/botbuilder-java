package com.microsoft.bot.builder;

import com.microsoft.bot.schema.ActivityImpl;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ConversationReference;
import com.microsoft.bot.schema.models.ResourceResponse;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SimpleAdapter extends BotAdapter {
    private Consumer<Activity[]> callOnSend = null;
    private Consumer<Activity> callOnUpdate = null;
    private Consumer<ConversationReference> callOnDelete = null;

    // Callback Function but doesn't need to be.  Avoid java legacy type erasure
    public SimpleAdapter(Consumer<Activity[]> callOnSend) {
        this(callOnSend, null, null);
    }

    public SimpleAdapter(Consumer<Activity[]> callOnSend, Consumer<Activity> callOnUpdate) {
        this(callOnSend, callOnUpdate, null);
    }

    public SimpleAdapter(Consumer<Activity[]> callOnSend, Consumer<Activity> callOnUpdate, Consumer<ConversationReference> callOnDelete) {
        super(Executors.newFixedThreadPool(10));
        this.callOnSend = callOnSend;
        this.callOnUpdate = callOnUpdate;
        this.callOnDelete = callOnDelete;
    }

    public SimpleAdapter() {
        super(Executors.newFixedThreadPool(10));
    }


    @Override
    public CompletableFuture<ResourceResponse[]> SendActivitiesAsync(TurnContext context, Activity[] activities) {
        return CompletableFuture.supplyAsync(() -> {
            Assert.assertNotNull("SimpleAdapter.deleteActivity: missing reference", activities);
            Assert.assertTrue("SimpleAdapter.sendActivities: empty activities array.", activities.length > 0);

            if (this.callOnSend != null)
                this.callOnSend.accept(activities);

            List<ResourceResponse> responses = new ArrayList<ResourceResponse>();
            for (Activity activity : activities) {
                responses.add(new ResourceResponse().withId(activity.id()));
            }
            ResourceResponse[] result = new ResourceResponse[responses.size()];
            return responses.toArray(result);
        });
    }

    @Override
    public CompletableFuture<ResourceResponse> UpdateActivityAsync(TurnContext context, Activity activity) {
        return CompletableFuture.supplyAsync(() -> {
            Assert.assertNotNull("SimpleAdapter.updateActivity: missing activity", activity);
            if (this.callOnUpdate != null)
                this.callOnUpdate.accept(activity);
            return new ResourceResponse()
                    .withId(activity.id());
        });
    }

    @Override
    public CompletableFuture DeleteActivityAsync(TurnContext context, ConversationReference reference) {
        return CompletableFuture.runAsync(() -> {
            Assert.assertNotNull("SimpleAdapter.deleteActivity: missing reference", reference);
            if (callOnDelete != null)
                this.callOnDelete.accept(reference);
        });
    }


    public CompletableFuture ProcessRequest(ActivityImpl activty, BotCallbackHandler callback)  {
        return CompletableFuture.runAsync(() -> {
            try (TurnContextImpl ctx = new TurnContextImpl(this, activty, executorService())) {
                this.RunPipelineAsync(ctx, callback).get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(String.format("Error running pipeline: %s", e.toString()));
            }
        });
    }
}

