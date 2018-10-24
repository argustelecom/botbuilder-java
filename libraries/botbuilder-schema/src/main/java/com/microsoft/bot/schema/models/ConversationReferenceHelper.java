package com.microsoft.bot.schema.models;

import com.microsoft.bot.schema.ActivityImpl;

import java.util.UUID;

public class ConversationReferenceHelper {
    private ConversationReference reference;
    public ConversationReferenceHelper(ConversationReference reference) {
        this.reference = reference;
    }
    /**
     * Creates {@link Activity} from conversation reference as it is posted to bot.
     */
    public ActivityImpl GetPostToBotMessage()
    {
        return (ActivityImpl) new ActivityImpl()
                .withType(ActivityTypes.MESSAGE.toString())
                .withId(UUID.randomUUID().toString())
                .withRecipient(new ChannelAccount()
                        .withId((reference.bot().id()))
                        .withName(reference.bot().name()))
                .withChannelId(reference.channelId())
                .withServiceUrl(reference.serviceUrl())
                .withConversation(new ConversationAccount()
                        .withId(reference.conversation().id())
                        .withIsGroup(reference.conversation().isGroup())
                        .withName(reference.conversation().name()))
                .withFrom(new ChannelAccount()
                    .withId(reference.user().id())
                    .withName(reference.user().name()));
    }

    /**
     * Creates {@link Activity} from conversation reference that can be posted to user as reply.
     */
    public ActivityImpl GetPostToUserMessage()
    {
        Activity msg = this.GetPostToBotMessage();

        // swap from and recipient
        ChannelAccount bot = msg.recipient();
        ChannelAccount user = msg.from();
        msg.withFrom(bot);
        msg.withRecipient(user);
        return (ActivityImpl) msg;
    }

    public static Activity GetContinuationActivity(ConversationReference reference)
    {
        Activity activity = new ActivityImpl();
        activity.withType(ActivityTypes.EVENT.toString());
        activity.withName("ContinueConversation");
        activity.withId(UUID.randomUUID().toString());
        activity.withChannelId(reference.channelId());
        activity.withServiceUrl(reference.serviceUrl());
        activity.withConversation(reference.conversation());
        activity.withRecipient(reference.bot());
        activity.withFrom(reference.user());
        activity.withRelatesTo(reference);
        return (Activity)activity;

    }
}


