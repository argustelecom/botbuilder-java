// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.microsoft.bot.builder.adapters.TestAdapter;
import com.microsoft.bot.builder.adapters.TestFlow;
import com.microsoft.bot.schema.ActivityImpl;
import com.microsoft.bot.schema.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class TranscriptMiddlewareTest {

    @Test
    public final void Transcript_SimpleReceive() throws Exception {
        MemoryTranscriptStore transcriptStore = new MemoryTranscriptStore();
        TestAdapter adapter = (new TestAdapter()).Use(new TranscriptLoggerMiddleware(transcriptStore));
        final String[] conversationId = {null};


        new TestFlow(adapter, (ctxt) ->
        {

                TurnContextImpl context = (TurnContextImpl) ctxt;
                conversationId[0] = context.activity().conversation().id();
                ActivityImpl typingActivity = new ActivityImpl()
                        .withType(ActivityTypes.TYPING.toString())
                        .withRelatesTo(context.activity().relatesTo());
                try {
                    ResourceResponse response = context.SendActivityAsync(typingActivity).get();
                    System.out.printf("Here's the response:");
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                try {
                    context.SendActivityAsync("echo:" + context.activity().text());
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                return CompletableFuture.completedFuture(null);

        }).Send("foo")
                .AssertReply((activity) -> {
                    Assert.assertEquals(activity.type(), ActivityTypes.TYPING);
                    return null;
                }).StartTest();
                //.AssertReply("echo:foo").StartTest();


    }

    @Test
    public final void Transcript_MiddlewareTest() throws Exception {
        MemoryTranscriptStore transcriptStore = new MemoryTranscriptStore();
        TranscriptLoggerMiddleware logger = new TranscriptLoggerMiddleware(transcriptStore);
        TestAdapter adapter = new TestAdapter();
        ActivityImpl activity = ActivityImpl.CreateMessageActivity()
                .withFrom(new ChannelAccount().withName("MyAccount").withId("acctid").withRole(RoleTypes.USER));
        TurnContextImpl context = new TurnContextImpl(adapter, activity);
        NextDelegate nd = new NextDelegate() {
            @Override
            public CompletableFuture invoke() {
                System.out.printf("Delegate called!");
                System.out.flush();
                return CompletableFuture.completedFuture(null);
            }
        };
        ActivityImpl typingActivity = new ActivityImpl()
                .withType(ActivityTypes.TYPING.toString())
                .withRelatesTo(context.activity().relatesTo());
        try {
            context.SendActivityAsync(typingActivity);
            System.out.printf("HI");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }


        //logger.OnTurn(context, nd).get();
    }

    @Test
    public final void Transcript_LogActivities() throws ExecutionException, InterruptedException {
        Logger logger = LogManager.getLogger(Dialog.class);
        MemoryTranscriptStore transcriptStore = new MemoryTranscriptStore();
        TestAdapter adapter = (new TestAdapter()).Use(new TranscriptLoggerMiddleware(transcriptStore));
        final String[] conversationId = {null};


        String result = new TestFlow(adapter, (context) ->
        {

                //TurnContextImpl context = (TurnContextImpl) ctxt;
                conversationId[0] = context.activity().conversation().id();
                ActivityImpl typingActivity = new ActivityImpl()
                        .withType(ActivityTypes.TYPING.toString())
                        .withRelatesTo(context.activity().relatesTo());
                try {
                    context.SendActivityAsync((Activity)typingActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                try {
                    context.SendActivityAsync("echo:" + context.activity().text()).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                return CompletableFuture.completedFuture(null);
        }).Send("foo")
                .AssertReply((activity) -> {
                    Assert.assertEquals(activity.type(), ActivityTypes.TYPING.toString());
                    return null;
                })
                .AssertReply("echo:foo")
                .Send("bar")
                .AssertReply((activity) -> {
                    Assert.assertEquals(activity.type(), ActivityTypes.TYPING.toString());
                    return null;
                })
                .AssertReply("echo:bar")
                .StartTest();


        PagedResult pagedResult = transcriptStore.GetTranscriptActivitiesAsync("test", conversationId[0]).join();
        Assert.assertEquals(6, pagedResult.getItems().length);
        Assert.assertEquals( "foo", ((Activity)pagedResult.getItems()[0]).text());
        Assert.assertNotEquals(((Activity)pagedResult.getItems()[1]), null);
        Assert.assertEquals("echo:foo", ((Activity) pagedResult.getItems()[2]).text());
        Assert.assertEquals("bar", ((Activity)pagedResult.getItems()[3]).text());

        Assert.assertTrue(pagedResult.getItems()[4] != null);
        Assert.assertEquals("echo:bar", ((Activity)pagedResult.getItems()[5]).text());
        for (Object activity : pagedResult.getItems())
        {
            Assert.assertFalse(StringUtils.isBlank(((Activity) activity).id()));
            Assert.assertTrue(((Activity)activity).timestamp().isAfter(OffsetDateTime.MIN));
        }
        System.out.printf("Complete");
    }

    @Test
    public void Transcript_LogUpdateActivities() throws InterruptedException, ExecutionException {
        MemoryTranscriptStore transcriptStore = new MemoryTranscriptStore();
        TestAdapter adapter = (new TestAdapter()).Use(new TranscriptLoggerMiddleware(transcriptStore));
        final String[] conversationId = {null};
        final Activity[] activityToUpdate = {null};
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        new TestFlow(adapter, (context) ->
        {

                conversationId[0] = context.activity().conversation().id();
                if (context.activity().text().equals("update")) {
                    activityToUpdate[0].withText("new response");
                    try {
                        context.UpdateActivity(activityToUpdate[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityImpl activity = ((ActivityImpl) context.activity()).CreateReply("response");
                    ResourceResponse response = null;
                    try {
                        response = context.SendActivityAsync(activity).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail();
                    }
                    activity.withId(response.id());

                    // clone the activity, so we can use it to do an update
                    activityToUpdate[0] = ActivityImpl.CloneActity(activity);
                    //JsonConvert.<Activity>DeserializeObject(JsonConvert.SerializeObject(activity));

                }
                return CompletableFuture.completedFuture(null);
        }).Send("foo")
                .Send("update")
                .AssertReply("new response")
                .StartTest();
        Thread.sleep(500);
        PagedResult pagedResult = transcriptStore.GetTranscriptActivitiesAsync("test", conversationId[0]).join();
        Assert.assertEquals(4, pagedResult.getItems().length);
        Assert.assertEquals("foo", ((Activity)pagedResult.getItems()[0]).text());
        Assert.assertEquals( "response", ((Activity)pagedResult.getItems()[1]).text());
        Assert.assertEquals( "new response", ((Activity)pagedResult.getItems()[2]).text());
        Assert.assertEquals("update", ((Activity)pagedResult.getItems()[3]).text());
        Assert.assertEquals( ((Activity)pagedResult.getItems()[1]).id(),  ((Activity) pagedResult.getItems()[2]).id());

    }

    @Test
    public final void Transcript_LogDeleteActivities() throws InterruptedException, ExecutionException {
        MemoryTranscriptStore transcriptStore = new MemoryTranscriptStore();
        TestAdapter adapter = (new TestAdapter()).Use(new TranscriptLoggerMiddleware(transcriptStore));
        final String[] conversationId = {null};
        final String[] activityId = {null};
        new TestFlow(adapter, (context) ->
        {

                conversationId[0] = context.activity().conversation().id();
                if (context.activity().text().equals("deleteIt")) {
                    try {
                        context.DeleteActivity(activityId[0]).join();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail();
                    }
                } else {
                    ActivityImpl activity = ((ActivityImpl) context.activity()).CreateReply("response");
                    ResourceResponse response = null;
                    try {
                        response = context.SendActivityAsync(activity).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail();
                    }
                    activityId[0] = response.id();
                }
                return CompletableFuture.completedFuture(null);


        }).Send("foo")
                .AssertReply("response")
                .Send("deleteIt")
                .StartTest();
        Thread.sleep(1500);
        PagedResult pagedResult = transcriptStore.GetTranscriptActivitiesAsync("test", conversationId[0]).join();
        for (Object act : pagedResult.getItems()) {
            System.out.printf("Here is the object: %s : Type: %s\n", act.getClass().getTypeName(), ((Activity)act).type());
        }

        for (Object activity : pagedResult.getItems() ) {
            System.out.printf("Recipient: %s\nText: %s\n", ((Activity) activity).recipient().name(), ((Activity)activity).text());
        }
        Assert.assertEquals(4, pagedResult.getItems().length);
        Assert.assertEquals("foo", ((Activity)pagedResult.getItems()[0]).text());
        Assert.assertEquals("response", ((Activity)pagedResult.getItems()[1]).text());
        Assert.assertEquals("deleteIt", ((Activity)pagedResult.getItems()[2]).text());
        Assert.assertEquals(ActivityTypes.MESSAGE_DELETE, ((Activity)pagedResult.getItems()[3]).type());
        Assert.assertEquals(((Activity)pagedResult.getItems()[1]).id(), ((Activity) pagedResult.getItems()[3]).id());
    }
}

