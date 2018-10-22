package com.microsoft.bot.builder;

import java.util.Map;

/**
 Internal cached bot state.
 */
class CachedBotState
{

    public CachedBotState()
    {
        this(null);
    }

    public CachedBotState(Map<String, Object> state)
    {
        setState((state != null) ? state : new java.util.concurrent.ConcurrentHashMap<String, Object>());
        setHash(ComputeHash(getState()));
    }

    private Map<String, Object> State;
    public final Map<String, Object> getState()
    {
        return State;
    }
    public final void setState(Map<String, Object> value)
    {
        State = value;
    }

    private String Hash;
    public final String getHash()
    {
        return Hash;
    }
    public final void setHash(String value)
    {
        Hash = value;
    }

    public final boolean IsChanged()
    {
        return !getHash().equals(ComputeHash(getState()));
    }

    public final String ComputeHash(Object obj)
    {
        return JsonConvert.SerializeObject(obj);
    }
}
