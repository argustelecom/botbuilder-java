// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.connector.authentication;

/**
 MicrosoftGovernmentAppCredentials auth implementation
 */
public class MicrosoftGovernmentAppCredentials extends MicrosoftAppCredentials
{
    /**
     An empty set of credentials.
     */
//C# TO JAVA CONVERTER WARNING: There is no Java equivalent to C#'s shadowing via the 'new' keyword:
//ORIGINAL LINE: public static new readonly MicrosoftGovernmentAppCredentials Empty = new MicrosoftGovernmentAppCredentials(null, null);
    public static final MicrosoftGovernmentAppCredentials Empty = new MicrosoftGovernmentAppCredentials(null, null);

    /**
     Creates a new instance of the <see cref="MicrosoftGovernmentAppCredentials"/> class.

     @param appId The Microsoft app ID.
     @param password The Microsoft app password.
     */
    public MicrosoftGovernmentAppCredentials(String appId, String password)
    {
        super(appId, password);
    }

    /**
     Gets the OAuth endpoint to use.
     */
    @Override
    public String oAuthEndpoint()
    {
        return GovernmentAuthenticationConstants.ToChannelFromBotLoginUrl;
    }

    /**
     Gets the OAuth scope to use.
     */
    @Override
    public String oAuthScope()
    {
        return GovernmentAuthenticationConstants.ToChannelFromBotOAuthScope;
    }
}
