// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.connector.authentication;


/**
 Values and Constants used for Authentication and Authrization by the Bot Framework Protocol to US Government DataCenters
 */
public final class GovernmentAuthenticationConstants
{
    /**
     Government Channel Service property value
     */
    public static final String ChannelService = "https://botframework.us";

    /**
     TO GOVERNMENT CHANNEL FROM BOT: Login URL
     */
    public static final String ToChannelFromBotLoginUrl = "https://login.microsoftonline.us/botframework.com/oauth2/v2.0/token";

    /**
     TO GOVERNMENT CHANNEL FROM BOT: OAuth scope to request
     */
    public static final String ToChannelFromBotOAuthScope = "https://api.botframework.us/.default";

    /**
     TO BOT FROM GOVERNMENT CHANNEL: Token issuer
     */
    public static final String ToBotFromChannelTokenIssuer = "https://api.botframework.us";

    /**
     OAuth Url used to get a token from OAuthApiClient
     */
    public static final String OAuthUrlGov = "https://api.botframework.us";

    /**
     TO BOT FROM GOVERNMANT CHANNEL: OpenID metadata document for tokens coming from MSA
     */
    public static final String ToBotFromChannelOpenIdMetadataUrl = "https://login.botframework.us/v1/.well-known/openidconfiguration";
}
