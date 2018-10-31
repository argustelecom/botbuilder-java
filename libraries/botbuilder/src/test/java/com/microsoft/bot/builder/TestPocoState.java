package com.microsoft.bot.builder;


import com.fasterxml.jackson.annotation.JsonProperty;

public class TestPocoState
{
    @JsonProperty(value="val")
    private String stringValue;
    public String getValue() {
        return this.stringValue;
    }
    public void setValue(String value) {
        this.stringValue = value;
    }
}

