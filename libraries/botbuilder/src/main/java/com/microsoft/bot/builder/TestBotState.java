package com.microsoft.bot.builder;

public class TestBotState extends BotState
{
    public TestBotState(Storage storage)
    {
        super(storage, String.format("BotState:%1$s", BotState.class.getTypeName()));
    }

    @Override
    protected String GetStorageKey(TurnContext turnContext)
    {
        return String.format("botstate/%1$s/%2$s/%3$s", turnContext.activity().channelId(), turnContext.activity().conversation().id(), BotState.class.getTypeName());
    }
}
