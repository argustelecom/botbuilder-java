// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.ai.qna;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.bot.schema.ActivityImpl;
import com.microsoft.bot.schema.models.Activity;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 Provides access to a QnA Maker knowledge base.
*/
public class QnAMaker
{
	public static final String QnAMakerMiddlewareName = "QnAMakerMiddleware";
	public static final String QnAMakerTraceType = "https://www.qnamaker.ai/schemas/trace";
	public static final String QnAMakerTraceLabel = "QnAMaker Trace";

	private static final OkHttpClient DefaultHttpClient = new OkHttpClient();
	private OkHttpClient _httpClient;

	private QnAMakerEndpoint _endpoint;
	private QnAMakerOptions _options;

	/** 
	 Initializes a new instance of the <see cref="QnAMaker"/> class.
	 
	 @param endpoint The endpoint of the knowledge base to query.
	 @param options The options for the QnA Maker knowledge base.
	 @param httpClient An alternate client with which to talk to QnAMaker.
	 If null, a default client is used for this instance.
	*/

	public QnAMaker(QnAMakerEndpoint endpoint, QnAMakerOptions options)
	{
		this(endpoint, options, null);
	}

	public QnAMaker(QnAMakerEndpoint endpoint)
	{
		this(endpoint, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public QnAMaker(QnAMakerEndpoint endpoint, QnAMakerOptions options = null, HttpClient httpClient = null)
	public QnAMaker(QnAMakerEndpoint endpoint, QnAMakerOptions options, OkHttpClient httpClient)
	{
		_httpClient = (httpClient != null) ? httpClient : DefaultHttpClient;

//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: _endpoint = endpoint ?? throw new ArgumentNullException(nameof(endpoint));
		_endpoint = (endpoint != null) ? endpoint : throw new NullPointerException("endpoint");

		if (StringUtils.isBlank(endpoint.getKnowledgeBaseId()))
		{
			throw new IllegalArgumentException("KnowledgeBaseId");
		}

		if (StringUtils.isBlank(endpoint.getHost()))
		{
			throw new IllegalArgumentException("Host");
		}

		if (StringUtils.isBlank(endpoint.getEndpointKey()))
		{
			throw new IllegalArgumentException("EndpointKey");
		}

		_options = (options != null) ? options : new QnAMakerOptions();

		if (_options.getScoreThreshold() == 0)
		{
			_options.setScoreThreshold(0.3F);
		}

		if (_options.getTop() == 0)
		{
			_options.setTop(1);
		}

		if (_options.getScoreThreshold() < 0 || _options.getScoreThreshold() > 1)
		{
			throw new IndexOutOfBoundsException("ScoreThreshold", "Score threshold should be a value between 0 and 1");
		}

		if (_options.getTop() < 1)
		{
			throw new IndexOutOfBoundsException("Top", "Top should be an integer greater than 0");
		}

		if (_options.getStrictFilters() == null)
		{
			_options.setStrictFilters(new Metadata[] { });
		}

		if (_options.getMetadataBoost() == null)
		{
			_options.setMetadataBoost(new Metadata[] { });
		}
	}

	/** 
	 Initializes a new instance of the <see cref="QnAMaker"/> class.
	 
	 @param service QnA service details from configuration.
	 @param options The options for the QnA Maker knowledge base.
	 @param httpClient An alternate client with which to talk to QnAMaker.
	 If null, a default client is used for this instance.
	*/

	public QnAMaker(QnAMakerService service, QnAMakerOptions options)
	{
		this(service, options, null);
	}

	public QnAMaker(QnAMakerService service)
	{
		this(service, null, null);
	}

	public QnAMaker(QnAMakerService service, QnAMakerOptions options, OkHttpClient httpClient)
	{
		this(new QnAMakerEndpoint(service), options, httpClient);
	}

	/** 
	 Generates an answer from the knowledge base.
	 
	 @param turnContext The Turn Context that contains the user question to be queried against your knowledge base.
	 @return A list of answers for the user query, sorted in decreasing order of ranking score.
	*/
	public final CompletableFuture<QueryResult[]> GetAnswersAsync(TurnContext turnContext)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		if (turnContext.Activity == null)
		{
			throw new NullPointerException("Activity");
		}

		Activity messageActivity = turnContext.activity();
		if (messageActivity == null)
		{
			throw new IllegalArgumentException("Activity type is not a message");
		}

		if (StringUtils.isBlank(turnContext.activity().text()))
		{
			throw new IllegalArgumentException("Null or empty text");
		}

		String requestUrl = String.format("%1$s/knowledgebases/%2$s/generateanswer", _endpoint.getHost(), _endpoint.getKnowledgeBaseId());

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Post, requestUrl);

		class AnonymousType
		{
			public String question;
			public int top;
			public Microsoft.Bot.Builder.AI.QnA.Metadata[] strictFilters;
			public Microsoft.Bot.Builder.AI.QnA.Metadata[] metadataBoost;

			public AnonymousType(String _question, int _top, Microsoft.Bot.Builder.AI.QnA.Metadata[] _strictFilters, Microsoft.Bot.Builder.AI.QnA.Metadata[] _metadataBoost)
			{
				question = _question;
				top = _top;
				strictFilters = _strictFilters;
				metadataBoost = _metadataBoost;
			}
		}
		var jsonRequest = JsonConvert.SerializeObject(AnonymousType(messageActivity.Text, _options.getTop(), _options.getStrictFilters(), _options.getMetadataBoost()), Formatting.None);

		request.Content = new StringContent(jsonRequest, System.Text.Encoding.UTF8, "application/json");

		boolean isLegacyProtocol = _endpoint.getHost().endsWith("v2.0") || _endpoint.getHost().endsWith("v3.0");

		if (isLegacyProtocol)
		{
			request.Headers.Add("Ocp-Apim-Subscription-Key", _endpoint.getEndpointKey());
		}
		else
		{
			request.Headers.Add("Authorization", String.format("EndpointKey %1$s", _endpoint.getEndpointKey()));
		}

		var response = _httpClient.SendAsync(request).get();
		if (!response.IsSuccessStatusCode)
		{
			return null;
		}

		var jsonResponse = response.Content.ReadAsStringAsync().get();

		var results = isLegacyProtocol ? ConvertLegacyResults(JsonConvert.<InternalQueryResults>DeserializeObject(jsonResponse)) : JsonConvert.<QueryResults>DeserializeObject(jsonResponse);

		for (var answer : results.Answers)
		{
			answer.Score = answer.Score / 100;
		}

		Object[] result = results.Answers.Where(answer -> answer.Score > _options.getScoreThreshold()).ToArray();

		QnAMakerTraceInfo traceInfo = new QnAMakerTraceInfo();
		traceInfo.setMessage((Activity)messageActivity);
		traceInfo.setQueryResults(result);
		traceInfo.setKnowledgeBaseId(_endpoint.getKnowledgeBaseId());
		traceInfo.setScoreThreshold(_options.getScoreThreshold());
		traceInfo.setTop(_options.getTop());
		traceInfo.setStrictFilters(_options.getStrictFilters());
		traceInfo.setMetadataBoost(_options.getMetadataBoost());
		Activity traceActivity = ActivityImpl.CreateTraceActivity(QnAMakerMiddlewareName, QnAMakerTraceType, traceInfo, QnAMakerTraceLabel);
		turnContext.SendActivityAsync(traceActivity).get();

		return result;
	}

	// The old version of the protocol returns the id in a field called qnaId the
	// following classes and helper function translate this old structure
	private QueryResults ConvertLegacyResults(InternalQueryResults legacyResults)
	{
		QueryResult tempVar = new QueryResult();
		tempVar.setId(answer.QnaId);
		tempVar.setAnswer(answer.Answer);
		tempVar.setMetadata(answer.Metadata);
		tempVar.setScore(answer.Score);
		tempVar.setSource(answer.Source);
		tempVar.setQuestions(answer.Questions);
		return new QueryResults {Answers = legacyResults.getAnswers().Select(answer -> tempVar).ToArray()};
	}

	private static class InternalQueryResult extends QueryResult
	{
@JsonProperty(value = "qnaId")
		private int QnaId;
		public final int getQnaId()
		{
			return QnaId;
		}
		public final void setQnaId(int value)
		{
			QnaId = value;
		}
	}

	private static class InternalQueryResults
	{
		/** 
		 Gets or sets the answers for a user query,
		 sorted in decreasing order of ranking score.
		*/
        @JsonProperty(value = "answers")

        private InternalQueryResult[] Answers;
		public final InternalQueryResult[] getAnswers()
		{
			return Answers;
		}
		public final void setAnswers(InternalQueryResult[] value)
		{
			Answers = value;
		}
	}
}