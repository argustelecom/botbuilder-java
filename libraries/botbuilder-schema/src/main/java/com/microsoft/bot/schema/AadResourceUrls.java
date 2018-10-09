// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 A collection of Azure Active Directory resource URLs
 */
public class AadResourceUrls
{
    /**
     An array of resource URLs to use with Azure Active Directory
     */
    @JsonProperty(value = "resourceUrls")
    private String[] ResourceUrls;
    public final String[] resourceUrls()
    {
        return ResourceUrls;
    }
    public final void withResourceUrls(String[] value)
    {
        ResourceUrls = value;
    }
}
