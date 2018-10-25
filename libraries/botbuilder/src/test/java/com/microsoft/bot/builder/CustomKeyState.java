package com.microsoft.bot.builder;


public class CustomKeyState extends BotState
{
    public CustomKeyState(Storage storage)
    {
        super(storage, PropertyName);
    }

    public static final String PropertyName = "Microsoft.Bot.Builder.Tests.CustomKeyState";

    @Override
    protected String GetStorageKey(TurnContext turnContext)
    {
        return "CustomKey";
    }
}

