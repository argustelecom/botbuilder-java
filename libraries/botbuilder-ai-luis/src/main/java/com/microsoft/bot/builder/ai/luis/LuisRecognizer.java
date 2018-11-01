// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.ai.luis;

import Newtonsoft.Json.Linq.*;
import java.util.*;

/** <inheritdoc />
 
 A LUIS based implementation of <see cref="IRecognizer"/>.
*/
public class LuisRecognizer implements IRecognizer
{
	/** 
	 The value type for a LUIS trace activity.
	*/
	public static final String LuisTraceType = "https://www.luis.ai/schemas/trace";

	/** 
	 The context label for a LUIS trace activity.
	*/
	public static final String LuisTraceLabel = "Luis Trace";
	private static final String _metadataKey = "$instance";
	private ILUISRuntimeClient _runtime;
	private LuisApplication _application;
	private LuisPredictionOptions _options;
	private boolean _includeApiResults;

	/** 
	 Initializes a new instance of the <see cref="LuisRecognizer"/> class.
	 
	 @param application The LUIS application to use to recognize text.
	 @param predictionOptions (Optional) The LUIS prediction options to use.
	 @param includeApiResults (Optional) TRUE to include raw LUIS API response.
	 @param clientHandler (Optional) Custom handler for LUIS API calls to allow mocking.
	*/

	public LuisRecognizer(LuisApplication application, LuisPredictionOptions predictionOptions, boolean includeApiResults)
	{
		this(application, predictionOptions, includeApiResults, null);
	}

	public LuisRecognizer(LuisApplication application, LuisPredictionOptions predictionOptions)
	{
		this(application, predictionOptions, false, null);
	}

	public LuisRecognizer(LuisApplication application)
	{
		this(application, null, false, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public LuisRecognizer(LuisApplication application, LuisPredictionOptions predictionOptions = null, bool includeApiResults = false, HttpClientHandler clientHandler = null)
	public LuisRecognizer(LuisApplication application, LuisPredictionOptions predictionOptions, boolean includeApiResults, HttpClientHandler clientHandler)
	{
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: _application = application ?? throw new ArgumentNullException(nameof(application));
		_application = (application != null) ? application : throw new NullPointerException("application");
		_options = (predictionOptions != null) ? predictionOptions : new LuisPredictionOptions();
		_includeApiResults = includeApiResults;
		_runtime = new LUISRuntimeClient(new ApiKeyServiceClientCredentials(application.getEndpointKey()), clientHandler)
		{
			Endpoint = application.getEndpoint(),
		};
	}

	/** 
	 Initializes a new instance of the <see cref="LuisRecognizer"/> class.
	 
	 @param service The LUIS service from configuration.
	 @param predictionOptions (Optional) The LUIS prediction options to use.
	 @param includeApiResults (Optional) TRUE to include raw LUIS API response.
	 @param clientHandler (Optional) Custom handler for LUIS API calls to allow mocking.
	*/

	public LuisRecognizer(LuisService service, LuisPredictionOptions predictionOptions, boolean includeApiResults)
	{
		this(service, predictionOptions, includeApiResults, null);
	}

	public LuisRecognizer(LuisService service, LuisPredictionOptions predictionOptions)
	{
		this(service, predictionOptions, false, null);
	}

	public LuisRecognizer(LuisService service)
	{
		this(service, null, false, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public LuisRecognizer(LuisService service, LuisPredictionOptions predictionOptions = null, bool includeApiResults = false, HttpClientHandler clientHandler = null)
	public LuisRecognizer(LuisService service, LuisPredictionOptions predictionOptions, boolean includeApiResults, HttpClientHandler clientHandler)
	{
		this(new LuisApplication(service), predictionOptions, includeApiResults, clientHandler);
	}

	/** 
	 Returns the name of the top scoring intent from a set of LUIS results.
	 
	 @param results Result set to be searched.
	 @param defaultIntent (Optional) Intent name to return should a top intent be found. Defaults to a value of "None".
	 @param minScore (Optional) Minimum score needed for an intent to be considered as a top intent. If all intents in the set are below this threshold then the `defaultIntent` will be returned.  Defaults to a value of `0.0`.
	 @return The top scoring intent name.
	*/

	public static String TopIntent(RecognizerResult results, String defaultIntent)
	{
		return TopIntent(results, defaultIntent, 0.0);
	}

	public static String TopIntent(RecognizerResult results)
	{
		return TopIntent(results, "None", 0.0);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string TopIntent(RecognizerResult results, string defaultIntent = "None", double minScore = 0.0)
	public static String TopIntent(RecognizerResult results, String defaultIntent, double minScore)
	{
		if (results == null)
		{
			throw new NullPointerException("results");
		}

		String topIntent = null;
		double topScore = -1.0;
		if (results.Intents.size() > 0)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			for (var intent : results.Intents)
			{
				double score = (double)intent.Value.Score;
				if (score > topScore && score >= minScore)
				{
					topIntent = intent.Key;
					topScore = score;
				}
			}
		}

		return !StringUtils.isBlank(topIntent) ? topIntent : defaultIntent;
	}

	/** <inheritdoc />
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async CompletableFuture<RecognizerResult> RecognizeAsync(TurnContext turnContext, CancellationToken cancellationToken)
	public final CompletableFuture<RecognizerResult> RecognizeAsync(TurnContext turnContext, CancellationToken cancellationToken)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to ' ' in Java:
		return   RecognizeInternalAsync(turnContext, cancellationToken).get();
	}

	/** <inheritdoc />
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async CompletableFuture<T> RecognizeAsync<T>(TurnContext turnContext, CancellationToken cancellationToken) where T : IRecognizerConvert, new()
//C# TO JAVA CONVERTER TODO TASK: The C# 'new()' constraint has no equivalent in Java:
	public final <T extends IRecognizerConvert> CompletableFuture<T> RecognizeAsync(TurnContext turnContext, CancellationToken cancellationToken)
	{
		T result = new T();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to ' ' in Java:
		result.Convert(  RecognizeInternalAsync(turnContext, cancellationToken).Configure (false));
		return result;
	}

	private static String NormalizedIntent(String intent)
	{
		return intent.replace('.', '_').replace(' ', '_');
	}

	private static Map<String, IntentScore> GetIntents(LuisResult luisResult)
	{
		if (luisResult.Intents != null)
		{
			IntentScore tempVar = new IntentScore();
			tempVar.Score = (i.Score != null) ? i.Score : 0;
			return luisResult.Intents.ToDictionary(i -> NormalizedIntent(i.Intent), i -> tempVar);
		}
		else
		{
			IntentScore tempVar2 = new IntentScore();
			tempVar2.Score = (luisResult.TopScoringIntent.Score != null) ? luisResult.TopScoringIntent.Score : 0;
			return new HashMap<String, IntentScore>(Map.ofEntries(Map.entry(NormalizedIntent(luisResult.TopScoringIntent.Intent), tempVar2)));
		}
	}

	private static JObject ExtractEntitiesAndMetadata(List<EntityModel> entities, List<CompositeEntityModel> compositeEntities, boolean verbose)
	{
		JObject entitiesAndMetadata = new JObject();
		if (verbose)
		{
			entitiesAndMetadata[_metadataKey] = new JObject();
		}

		HashSet<String> compositeEntityTypes = new HashSet<String>();

		// We start by populating composite entities so that entities covered by them are removed from the entities list
		if (compositeEntities != null && compositeEntities.Any())
		{
			compositeEntityTypes = new HashSet<String>(compositeEntities.Select(ce -> ce.ParentType));
			entities = compositeEntities.Aggregate(entities, (current, compositeEntity) -> PopulateCompositeEntityModel(compositeEntity, current, entitiesAndMetadata, verbose));
		}

		for (EntityModel entity : entities)
		{
			// we'll address composite entities separately
			if (compositeEntityTypes.contains(entity.Type))
			{
				continue;
			}

			AddProperty(entitiesAndMetadata, ExtractNormalizedEntityName(entity), ExtractEntityValue(entity));

			if (verbose)
			{
				AddProperty((JObject)entitiesAndMetadata[_metadataKey], ExtractNormalizedEntityName(entity), ExtractEntityMetadata(entity));
			}
		}

		return entitiesAndMetadata;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to the C# 'dynamic' keyword:
	private static JToken Number(dynamic value)
	{
		if (value == null)
		{
			return null;
		}

		long longVal;
		tangible.OutObject<Long> tempOut_longVal = new tangible.OutObject<Long>();
		JToken tempVar = tangible.TryParseHelper.tryParseLong((String)value, tempOut_longVal) ? new JValue(longVal) : new JValue(Double.parseDouble((String)value));
	longVal = tempOut_longVal.argValue;
	return tempVar;
	}

	private static JToken ExtractEntityValue(EntityModel entity)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning disable IDE0007 // Use implicit type
		dynamic resolution;
//C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
		if (entity.AdditionalProperties == null || !entity.AdditionalProperties.TryGetValue("resolution", out resolution))
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning restore IDE0007 // Use implicit type
		{
			return entity.Entity;
		}

		if (entity.Type.startsWith("builtin.datetime."))
		{
			return JObject.FromObject(resolution);
		}
		else if (entity.Type.startsWith("builtin.datetimeV2."))
		{
			if (resolution.values == null || resolution.values.size() == 0)
			{
				return JArray.FromObject(resolution);
			}

			java.lang.Iterable<dynamic> resolutionValues = (java.lang.Iterable<dynamic>)resolution.values;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var type = resolution.values[0].type;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
			var timexes = resolutionValues.Select(val -> val.timex);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
			var distinctTimexes = timexes.Distinct();
			return new JObject(new JProperty("type", type), new JProperty("timex", JArray.FromObject(distinctTimexes)));
		}
		else
		{
			switch (entity.Type)
			{
				case "builtin.number":
				case "builtin.ordinal":
					return Number(resolution.value);
				case "builtin.percentage":
				{
						String svalue = (String)resolution.value;
						if (svalue.endsWith("%"))
						{
							svalue = svalue.substring(0, svalue.length() - 1);
						}

						return Number(svalue);
				}

				case "builtin.age":
				case "builtin.dimension":
				case "builtin.currency":
				case "builtin.temperature":
				{
						String units = (String)resolution.unit;
						JToken val = Number(resolution.value);
						JObject obj = new JObject();
						if (val != null)
						{
							obj.Add("number", val);
						}

						obj.Add("units", units);
						return obj;
				}

				default:
					return (resolution.value != null) ? resolution.value : JArray.FromObject(resolution.values);
			}
		}
	}

	private static JObject ExtractEntityMetadata(EntityModel entity)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to the C# 'dynamic' keyword:
		dynamic obj = JObject.FromObject(new {startIndex = (int)entity.StartIndex, endIndex = (int)entity.EndIndex + 1, text = entity.Entity, type = entity.Type});
		if (entity.AdditionalProperties != null)
		{
			Object score;
//C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
			if (entity.AdditionalProperties.TryGetValue("score", out score))
			{
				obj.score = (double)score;
			}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning disable IDE0007 // Use implicit type
			dynamic resolution;
//C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
			if (entity.AdditionalProperties.TryGetValue("resolution", out resolution) && resolution.subtype != null)
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning restore IDE0007 // Use implicit type
			{
				obj.subtype = resolution.subtype;
			}
		}

		return obj;
	}

	private static String ExtractNormalizedEntityName(EntityModel entity)
	{
		// Type::Role -> Role
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var type = entity.Type.split("[:]", -1).Last();
		if (type.StartsWith("builtin.datetimeV2."))
		{
			type = "datetime";
		}

		if (type.StartsWith("builtin.currency"))
		{
			type = "money";
		}

		if (type.StartsWith("builtin."))
		{
			type = type.Substring(8);
		}

		boolean role = entity.AdditionalProperties != null && entity.AdditionalProperties.ContainsKey("role") ? (String)entity.AdditionalProperties["role"] : "";
		if (!tangible.StringHelper.isNullOrWhiteSpace(role))
		{
			type = role;
		}

		return type.Replace('.', '_').Replace(' ', '_');
	}

	private static List<EntityModel> PopulateCompositeEntityModel(CompositeEntityModel compositeEntity, List<EntityModel> entities, JObject entitiesAndMetadata, boolean verbose)
	{
		JObject childrenEntites = new JObject();
		JObject childrenEntitiesMetadata = new JObject();
		if (verbose)
		{
			childrenEntites[_metadataKey] = new JObject();
		}

		// This is now implemented as O(n^2) search and can be reduced to O(2n) using a map as an optimization if n grows
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var compositeEntityMetadata = entities.FirstOrDefault(e -> e.Type == compositeEntity.ParentType && e.Entity == compositeEntity.Value);

		// This is an error case and should not happen in theory
		if (compositeEntityMetadata == null)
		{
			return entities;
		}

		if (verbose)
		{
			childrenEntitiesMetadata = ExtractEntityMetadata(compositeEntityMetadata);
			childrenEntites[_metadataKey] = new JObject();
		}

		HashSet<EntityModel> coveredSet = new HashSet<EntityModel>();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		for (var child : compositeEntity.Children)
		{
			for (EntityModel entity : entities)
			{
				// We already covered this entity
				if (coveredSet.contains(entity))
				{
					continue;
				}

				// This entity doesn't belong to this composite entity
				if (child.Type != entity.Type || !CompositeContainsEntity(compositeEntityMetadata, entity))
				{
					continue;
				}

				// Add to the set to ensure that we don't consider the same child entity more than once per composite
				coveredSet.add(entity);
				AddProperty(childrenEntites, ExtractNormalizedEntityName(entity), ExtractEntityValue(entity));

				if (verbose)
				{
					AddProperty((JObject)childrenEntites[_metadataKey], ExtractNormalizedEntityName(entity), ExtractEntityMetadata(entity));
				}
			}
		}

		AddProperty(entitiesAndMetadata, compositeEntity.ParentType, childrenEntites);
		if (verbose)
		{
			AddProperty((JObject)entitiesAndMetadata[_metadataKey], compositeEntity.ParentType, childrenEntitiesMetadata);
		}

		// filter entities that were covered by this composite entity
		return entities.Except(coveredSet).ToList();
	}

	private static boolean CompositeContainsEntity(EntityModel compositeEntityMetadata, EntityModel entity)
	{
		return entity.StartIndex >= compositeEntityMetadata.StartIndex && entity.EndIndex <= compositeEntityMetadata.EndIndex;
	}

	/** 
	 If a property doesn't exist add it to a new array, otherwise append it to the existing array.
	*/
	private static void AddProperty(JObject obj, String key, JToken value)
	{
		if (((Map<String, JToken>)obj).containsKey(key))
		{
			((JArray)obj[key]).Add(value);
		}
		else
		{
			obj[key] = new JArray(value);
		}
	}

	private static void AddProperties(LuisResult luis, RecognizerResult result)
	{
		if (luis.SentimentAnalysis != null)
		{
			result.Properties.Add("sentiment", new JObject(new JProperty("label", luis.SentimentAnalysis.Label), new JProperty("score", luis.SentimentAnalysis.Score)));
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: private async CompletableFuture<RecognizerResult> RecognizeInternalAsync(TurnContext turnContext, CancellationToken cancellationToken)
	private CompletableFuture<RecognizerResult> RecognizeInternalAsync(TurnContext turnContext, CancellationToken cancellationToken)
	{
		BotAssert.ContextNotNull(turnContext);

		if (turnContext.Activity.Type != ActivityTypes.Message)
		{
			return null;
		}

		boolean utterance = turnContext.Activity == null ? null : (turnContext.Activity.AsMessageActivity() == null ? null : turnContext.Activity.AsMessageActivity().Text);

		if (tangible.StringHelper.isNullOrWhiteSpace(utterance))
		{
			throw new NullPointerException("utterance");
		}

		Nullable<Boolean> tempVar = _options.getLog();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to ' ' in Java:
//C# TO JAVA CONVERTER TODO TASK: C# to Java Converter could not resolve the named parameters in the following line:
//ORIGINAL LINE: var luisResult =   _runtime.Prediction.ResolveAsync(_application.ApplicationId, utterance, timezoneOffset: _options.TimezoneOffset, verbose: _options.IncludeAllIntents, staging: _options.Staging, spellCheck: _options.SpellCheck, bingSpellCheckSubscriptionKey: _options.BingSpellCheckSubscriptionKey, log: _options.Log ?? true, cancellationToken: cancellationToken).get();
		var luisResult =   _runtime.Prediction.ResolveAsync(_application.getApplicationId(), utterance, timezoneOffset: _options.getTimezoneOffset(), verbose: _options.getIncludeAllIntents(), staging: _options.getStaging(), spellCheck: _options.getSpellCheck(), bingSpellCheckSubscriptionKey: _options.getBingSpellCheckSubscriptionKey(), log: (tempVar != null) ? tempVar : true, cancellationToken: cancellationToken).get();

		RecognizerResult recognizerResult = new RecognizerResult();
		recognizerResult.Text = utterance;
		recognizerResult.AlteredText = luisResult.AlteredQuery;
		recognizerResult.Intents = GetIntents(luisResult);
		Nullable<Boolean> tempVar2 = _options.getIncludeInstanceData();
		recognizerResult.Entities = ExtractEntitiesAndMetadata(luisResult.Entities, luisResult.CompositeEntities, (tempVar2 != null) ? tempVar2 : true);
		AddProperties(luisResult, recognizerResult);
		if (_includeApiResults)
		{
			recognizerResult.Properties.Add("luisResult", luisResult);
		}

		class AnonymousType
		{
			public String ModelID;

			public AnonymousType(String _ModelID)
			{
				ModelID = _ModelID;
			}
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		var traceInfo = JObject.FromObject(new {recognizerResult, luisModel = AnonymousType(_application.getApplicationId()), luisOptions = _options, luisResult});

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to ' ' in Java:
		  turnContext.TraceActivityAsync("LuisRecognizer", traceInfo, LuisTraceType, LuisTraceLabel, cancellationToken).get();
		return recognizerResult;
	}
}