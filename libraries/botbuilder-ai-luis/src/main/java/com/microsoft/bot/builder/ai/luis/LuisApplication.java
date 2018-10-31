package Microsoft.Bot.Builder.AI.Luis;

import java.util.*;

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.


/** 
 Data describing a LUIS application.
*/
public class LuisApplication
{
	public LuisApplication()
	{
	}

	/** 
	 Initializes a new instance of the <see cref="LuisApplication"/> class.
	 
	 @param applicationId LUIS application ID.
	 @param endpointKey LUIS subscription or endpoint key.
	 @param endpoint LUIS endpoint to use like https://westus.api.cognitive.microsoft.com.
	*/
	public LuisApplication(String applicationId, String endpointKey, String endpoint)
	{
		java.util.UUID appGuid;
		tangible.OutObject<UUID> tempOut_appGuid = new tangible.OutObject<UUID>();
		if (!UUID.TryParse(applicationId, tempOut_appGuid))
		{
		appGuid = tempOut_appGuid.argValue;
			throw new IllegalArgumentException(String.format("\"%1$s\" is not a valid LUIS application id.", applicationId));
		}
	else
	{
		appGuid = tempOut_appGuid.argValue;
	}

		java.util.UUID subscriptionGuid;
		tangible.OutObject<UUID> tempOut_subscriptionGuid = new tangible.OutObject<UUID>();
		if (!UUID.TryParse(endpointKey, tempOut_subscriptionGuid))
		{
		subscriptionGuid = tempOut_subscriptionGuid.argValue;
			throw new IllegalArgumentException(String.format("\"%1$s\" is not a valid LUIS subscription key.", subscriptionGuid));
		}
	else
	{
		subscriptionGuid = tempOut_subscriptionGuid.argValue;
	}

		if (tangible.StringHelper.isNullOrWhiteSpace(endpoint))
		{
			throw new IllegalArgumentException(String.format("\"%1$s\" is not a valid LUIS endpoint.", endpoint));
		}

		if (!Uri.IsWellFormedUriString(endpoint, UriKind.Absolute))
		{
			throw new IllegalArgumentException(String.format("\"%1$s\" is not a valid LUIS endpoint.", endpoint));
		}

		setApplicationId(applicationId);
		setEndpointKey(endpointKey);
		setEndpoint(endpoint);
	}

	/** 
	 Initializes a new instance of the <see cref="LuisApplication"/> class.
	 
	 @param service LUIS coonfiguration.
	*/
	public LuisApplication(LuisService service)
	{
		this(service.AppId, service.SubscriptionKey, service.GetEndpoint());
	}

	/** 
	 Gets or sets lUIS application ID.
	 
	 <value>
	 LUIS application ID.
	 </value>
	*/
	private String ApplicationId;
	public final String getApplicationId()
	{
		return ApplicationId;
	}
	public final void setApplicationId(String value)
	{
		ApplicationId = value;
	}

	/** 
	 Gets or sets lUIS subscription or endpoint key.
	 
	 <value>
	 LUIS subscription or endpoint key.
	 </value>
	*/
	private String EndpointKey;
	public final String getEndpointKey()
	{
		return EndpointKey;
	}
	public final void setEndpointKey(String value)
	{
		EndpointKey = value;
	}

	/** 
	 Gets or sets lUIS endpoint like https://westus.api.cognitive.microsoft.com.
	 
	 <value>
	 LUIS endpoint where application is hosted.
	 </value>
	*/
	private String Endpoint;
	public final String getEndpoint()
	{
		return Endpoint;
	}
	public final void setEndpoint(String value)
	{
		Endpoint = value;
	}
}