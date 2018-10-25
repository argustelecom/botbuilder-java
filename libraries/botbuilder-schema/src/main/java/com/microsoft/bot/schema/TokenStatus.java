// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 The status of a particular token
 */
public class TokenStatus
{
    /**
     The name of the connection the token status pertains to
     */
    @JsonProperty(value = "connectionName")
    private String ConnectionName;
    public final String connectionName()
    {
        return ConnectionName;
    }
    public final void withConnectionName(String value)
    {
        ConnectionName = value;
    }

    /**
     Whether there is a token or not
     */
    @JsonProperty(value = "hasToken")
    private boolean HasToken;
    public final boolean hasToken()
    {
        return HasToken;
    }
    public final void withHasToken(boolean value)
    {
        HasToken = value;
    }

    /**
     The display name of the service provider for which this Token belongs to
     */
    @JsonProperty(value = "serviceProviderDisplayName")
    private String ServiceProviderDisplayName;
    public final String serviceProviderDisplayName()
    {
        return ServiceProviderDisplayName;
    }
    public final void withServiceProviderDisplayName(String value)
    {
        ServiceProviderDisplayName = value;
    }
}
