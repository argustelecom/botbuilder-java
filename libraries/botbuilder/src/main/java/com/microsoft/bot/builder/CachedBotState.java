package com.microsoft.bot.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        setState((state != null) ? state : new ConcurrentHashMap<String, Object>());
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

    public final String ComputeHash(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(obj);

    }
}
