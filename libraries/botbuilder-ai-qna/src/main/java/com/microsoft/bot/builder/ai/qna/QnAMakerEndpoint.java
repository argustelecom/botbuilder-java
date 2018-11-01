package com.microsoft.bot.builder.ai.qna;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Defines an endpoint used to connect to a QnA Maker Knowledge base.
*/
public class QnAMakerEndpoint
{
	public QnAMakerEndpoint()
	{
	}

	/** 
	 Initializes a new instance of the <see cref="QnAMakerEndpoint"/> class.
	 
	 @param service QnA service details from configuration.
	*/
	public QnAMakerEndpoint(QnAMakerService service)
	{
		setKnowledgeBaseId(service.KbId);
		setEndpointKey(service.EndpointKey);
		setHost(service.Hostname);
	}

	/** 
	 Gets or sets the knowledge base ID.
	 
	 <value>
	 The knowledge base ID.
	 </value>
	*/
	private String KnowledgeBaseId;
	public final String getKnowledgeBaseId()
	{
		return KnowledgeBaseId;
	}
	public final void setKnowledgeBaseId(String value)
	{
		KnowledgeBaseId = value;
	}

	/** 
	 Gets or sets the endpoint key for the knowledge base.
	 
	 <value>
	 The endpoint key for the knowledge base.
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
	 Gets or sets the host path. For example "https://westus.api.cognitive.microsoft.com/qnamaker/v2.0".
	 
	 <value>
	 The host path.
	 </value>
	*/
	private String Host;
	public final String getHost()
	{
		return Host;
	}
	public final void setHost(String value)
	{
		Host = value;
	}
}