package com.microsoft.bot.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.bot.builder.StoreItem;

public class TestState implements StoreItem {
    @JsonProperty(value="etag")
    private String etag;

    @Override
    public String getETag() {
        return this.etag;
    }

    @Override
    public void setETag(String etag) {
        this.etag = etag;
    }

    @JsonProperty(value="stringVal")
    private String stringVal;

    public String stringValue() {
        return this.stringVal;
    }

    public void withStringValue(String value) {
        this.stringVal = value;
    }
}

