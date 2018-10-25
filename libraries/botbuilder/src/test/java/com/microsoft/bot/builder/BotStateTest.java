
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.bot.builder.adapters.TestAdapter;
import com.microsoft.bot.builder.adapters.TestFlow;
import com.microsoft.bot.connector.implementation.ConnectorClientImpl;
import com.microsoft.bot.schema.models.ChannelAccount;
import com.microsoft.bot.schema.models.ResourceResponse;
import com.microsoft.rest.RestClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static java.util.concurrent.CompletableFuture.completedFuture;

//    [TestClass]
//            [TestCategory("State Management")]
public class BotStateTest {
    protected ConnectorClientImpl connector;
    protected ChannelAccount bot;
    protected ChannelAccount user;


    protected void initializeClients(RestClient restClient, String botId, String userId) {

        connector = new ConnectorClientImpl(restClient);
        bot = new ChannelAccount().withId(botId);
        user = new ChannelAccount().withId(userId);

    }


    protected void cleanUpResources() {
    }

    @Test
    public void State_DoNOTRememberContextState() throws ExecutionException, InterruptedException {

        TestAdapter adapter = new TestAdapter();

        new TestFlow(adapter, (context) -> {
            UserState obj = context.turnState().Get(UserState.class.getName());
            Assert.assertNull("context.state should not exist", obj);
            return completedFuture(null);
        })
                .Send("set value")
                .StartTest();

    }

    //@Test
    public void State_RememberIStoreItemUserState() throws ExecutionException, InterruptedException {
        UserState userState = new UserState(new MemoryStorage());
        StatePropertyAccessor<TestPocoState> testProperty = userState.<TestPocoState>CreateProperty("test");
        TestAdapter adapter = new TestAdapter()
                .Use(new AutoSaveStateMiddleware(userState));

        BotCallbackHandler callback = (context) -> {
            System.out.print(String.format("State_RememberIStoreItemUserState CALLBACK called.."));
            System.out.flush();
            TestPocoState state = null;
            try {
                state = testProperty.<TestPocoState>GetAsync(context, () -> new TestPocoState()).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
            Assert.assertNotNull("user state should exist", userState);
            switch (context.activity().text()) {
                case "set value":
                    state.setValue("test");
                    try {
                        ((TurnContextImpl)context).SendActivityAsync("value saved");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail(String.format("Error sending activity! - set value"));
                    }
                    break;
                case "get value":
                    try {
                        Assert.assertFalse(StringUtils.isBlank(state.getValue()));
                        ((TurnContextImpl)context).SendActivityAsync(state.getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail(String.format("Error sending activity! - get value"));
                    }
                    break;
            }
            return completedFuture(null);

        };

        new TestFlow(adapter, callback)
                .Test("set value", "value saved")
                .Test("get value", "test")
        .StartTest();

    }

    @Test
    public void State_RememberPocoUserState() throws ExecutionException, InterruptedException {
        UserState userState = new UserState(new MemoryStorage());
        StatePropertyAccessor<TestPocoState> testPocoProperty = userState.CreateProperty("testPoco");
        TestAdapter adapter = new TestAdapter()
                .Use(new AutoSaveStateMiddleware());
        new TestFlow(adapter,
                (context) -> {
                    TestPocoState testPocoState = testPocoProperty.GetAsync(context, () -> new TestPocoState()).get();
                    Assert.assertNotNull("user state should exist", userState);
                    switch (context.activity().text()) {
                        case "set value":
                            testPocoState.setValue("test");
                            context.SendActivityAsync("value saved").get();
                            break;
                        case "get value":
                            Assert.assertFalse(StringUtils.isBlank(testPocoState.getValue()));
                            context.SendActivityAsync(testPocoState.getValue()).get();
                            break;
                    }
                    return completedFuture(null);
                })
                .Test("set value", "value saved")
                .Test("get value", "test")
                .StartTest();
    }

    @Test
    public void State_RememberIStoreItemConversationState() throws ExecutionException, InterruptedException {
        UserState userState = new UserState(new MemoryStorage());
        StatePropertyAccessor<TestState> testProperty = userState.CreateProperty("test");

        TestAdapter adapter = new TestAdapter()
                .Use(new AutoSaveStateMiddleware(userState));

        new TestFlow(adapter,
                (context) ->
                {
                    TestState conversationState = testProperty.GetAsync(context, () -> new TestState()).get();
                    Assert.assertNotNull("state.conversation should exist", conversationState);
                    switch (context.activity().text()) {
                        case "set value":
                            conversationState.withValue("test");
                            context.SendActivityAsync("value saved").get();
                            break;
                        case "get value":
                            Assert.assertFalse(StringUtils.isBlank(conversationState.value()));
                            context.SendActivityAsync(conversationState.value()).get();
                            break;
                    }
                    return completedFuture(null);
                })
                .Test("set value", "value saved")
                .Test("get value", "test")
                .StartTest();
    }

    @Test
    public void State_RememberPocoConversationState() throws ExecutionException, InterruptedException {
        UserState userState = new UserState(new MemoryStorage());
        StatePropertyAccessor<TestPocoState> testPocoProperty = userState.CreateProperty("testPoco");
        TestAdapter adapter = new TestAdapter()
                .Use(new AutoSaveStateMiddleware(userState));

        new TestFlow(adapter,
                (context) ->
                {
                    TestPocoState conversationState = null;
                    try {
                        conversationState = testPocoProperty.GetAsync(context, () -> new TestPocoState()).get();
                    } catch (InterruptedException|ExecutionException e) {
                        e.printStackTrace();
                        Assert.fail(String.format("Error getting conversation state!"));
                    }

                    Assert.assertNotNull("state.conversation should exist", conversationState);
                    switch (context.activity().text()) {
                        case "set value":
                            conversationState.setValue("test");
                            try {
                                context.SendActivityAsync("value saved").get();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(String.format("Error sending activity! - set value"));
                            }
                            break;
                        case "get value":
                            try {
                                Assert.assertFalse(StringUtils.isBlank(conversationState.getValue()));
                                context.SendActivityAsync(conversationState.getValue()).get();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(String.format("Error sending activity! - get value"));
                            }
                            break;
                    }
                    return completedFuture(null);
                })

                .Test("set value", "value saved")
                .Test("get value", "test")
                .StartTest();
    }

    @Test
    public void State_CustomStateManagerTest() throws ExecutionException, InterruptedException {

        String testGuid = UUID.randomUUID().toString();
        CustomKeyState customState = new CustomKeyState(new MemoryStorage());
        TestAdapter adapter = new TestAdapter()
                .Use(new AutoSaveStateMiddleware(customState));
        StatePropertyAccessor<TestPocoState> testProperty = customState.CreateProperty("test");

        new TestFlow(adapter,
                (context) ->
                {
                    TestPocoState test = null;
                    try {
                        test = testProperty.GetAsync(context, () -> new TestPocoState()).get();
                    } catch (InterruptedException|ExecutionException e) {
                        e.printStackTrace();
                        Assert.fail(String.format("Error getting state!"));
                    }

                    switch (context.activity().text()) {
                            case "set value":
                                test.setValue(testGuid);
                                try {
                                    context.SendActivityAsync("value saved");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Assert.fail(String.format("Error sending activity! - set value"));
                                }
                                break;
                            case "get value":
                                try {
                                    Assert.assertFalse(StringUtils.isBlank(test.getValue()));
                                    context.SendActivityAsync(test.getValue());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Assert.fail(String.format("Error sending activity! - get value"));
                                }
                                break;
                        }
                        return completedFuture(null);
                })
                .Test("set value", "value saved")
                .Test("get value", testGuid.toString())
                .StartTest();
    }
    @Test
    public void State_RoundTripTypedObjectwTrace() throws ExecutionException, InterruptedException {
        ConversationState convoState = new ConversationState(new MemoryStorage());
        StatePropertyAccessor<TypedObject> testProperty = convoState.CreateProperty("typed");
        TestAdapter adapter = new TestAdapter()
                .Use(new AutoSaveStateMiddleware(convoState));

        new TestFlow(adapter,
                (context) ->
                {
                        System.out.println(String.format(">>Test Callback(tid:%s): STARTING : %s", Thread.currentThread().getId(), context.activity().text()));
                        System.out.flush();
                        TypedObject conversation = testProperty.GetAsync(context, () -> new TypedObject()).get();
                        Assert.assertNotNull("conversationstate should exist", conversation);
                        System.out.println(String.format(">>Test Callback(tid:%s): Text is : %s", Thread.currentThread().getId(), context.activity().text()));
                        System.out.flush();
                        switch (context.activity().text()) {
                            case "set value":
                                conversation.withName("test");
                                try {
                                    System.out.println(String.format(">>Test Callback(tid:%s): Send activity : %s", Thread.currentThread().getId(),
                                            "value saved"));
                                    System.out.flush();
                                    ResourceResponse response = context.SendActivityAsync("value saved").get();
                                    System.out.println(String.format(">>Test Callback(tid:%s): Response Id: %s", Thread.currentThread().getId(),
                                            response.id()));
                                    System.out.flush();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Assert.fail(String.format("Error sending activity! - set value"));
                                }
                                break;
                            case "get value":
                                try {
                                    System.out.println(String.format(">>Test Callback(tid:%s): Send activity : %s", Thread.currentThread().getId(),
                                            "TypedObject"));
                                    System.out.flush();
                                    context.SendActivityAsync("TypedObject");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Assert.fail(String.format("Error sending activity! - get value"));
                                }
                                break;
                        }
                        return completedFuture(null);
                })
                .Turn("set value", "value saved", "Description", 50000)
                .Turn("get value", "TypedObject", "Description", 50000)
                .StartTest();

    }


    @Test
    public void State_RoundTripTypedObject() throws ExecutionException, InterruptedException {
        ConversationState convoState = new ConversationState(new MemoryStorage());
        StatePropertyAccessor<TypedObject> testProperty = convoState.CreateProperty("typed");
        TestAdapter adapter = new TestAdapter()
                .Use(new AutoSaveStateMiddleware());

        new TestFlow(adapter,
                (context) ->
                {
                        TypedObject conversation = testProperty.GetAsync(context, () -> new TypedObject()).get();
                        Assert.assertNotNull("conversationstate should exist", conversation);
                        switch (context.activity().text()) {
                            case "set value":
                                conversation.withName("test");
                                try {
                                    context.SendActivityAsync("value saved");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Assert.fail(String.format("Error sending activity! - set value"));
                                }
                                break;
                            case "get value":
                                try {
                                    context.SendActivityAsync("TypedObject");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Assert.fail(String.format("Error sending activity! - get value"));
                                }
                                break;
                        }
                        return completedFuture(null);
                })
                .Test("set value", "value saved")
                .Test("get value", "TypedObject")
                .StartTest();

    }

    @Test
    public void State_UseBotStateDirectly() throws ExecutionException, InterruptedException {
        TestAdapter adapter = new TestAdapter();

        new TestFlow(adapter,
                (context) ->
                {
                    TestBotState botStateManager = new TestBotState(new MemoryStorage());
                    StatePropertyAccessor<CustomState> testProperty = botStateManager.CreateProperty("test");

                    botStateManager.LoadAsync(context, false).get();

                    CustomState customState = testProperty.GetAsync(context, () -> new CustomState()).get();

                    // this should be a 'new CustomState' as nothing is currently stored in storage
                    Assert.assertEquals(customState, new CustomState());

                    // amend property and write to storage
                    customState.setCustomString("test");
                    botStateManager.SaveChangesAsync(context).get();

                    // set customState to null before reading from storage
                    customState.setCustomString("asdfsadf");
                    // read into context again
                    botStateManager.LoadAsync(context, false).get();

                    customState = testProperty.GetAsync(context, () -> new CustomState()).get();

                    // check object read from value has the correct value for CustomString
                    Assert.assertEquals(customState.getCustomString(), "test");
                    return completedFuture(null);
                }
                )
                .StartTest();
    }


}

