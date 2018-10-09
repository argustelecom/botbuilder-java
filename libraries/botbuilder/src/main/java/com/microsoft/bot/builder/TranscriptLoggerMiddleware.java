package com.microsoft.bot.builder;

import Newtonsoft.Json.*;
import java.util.*;
import java.time.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Middleware for logging incoming and outgoing activitites to an <see cref="ITranscriptStore"/>.
*/
public class TranscriptLoggerMiddleware implements Middleware
{
	private static JsonSerializerSettings _jsonSettings = new JsonSerializerSettings() {NullValueHandling = NullValueHandling.Ignore};
	private ITranscriptLogger logger;

	private LinkedList<Activity> transcript = new LinkedList<Activity>();

	/** 
	 Initializes a new instance of the <see cref="TranscriptLoggerMiddleware"/> class.
	 
	 @param transcriptLogger The conversation store to use.
	*/
	public TranscriptLoggerMiddleware(ITranscriptLogger transcriptLogger)
	{
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: logger = transcriptLogger ?? throw new ArgumentNullException("TranscriptLoggerMiddleware requires a ITranscriptLogger implementation.  ");
		logger = (transcriptLogger != null) ? transcriptLogger : throw new NullPointerException("TranscriptLoggerMiddleware requires a ITranscriptLogger implementation.  ");
	}

	/** 
	 Records incoming and outgoing activities to the conversation store.
	 
	 @param turnContext The context object for this turn.
	 @param nextTurn The delegate to call to continue the bot middleware pipeline.

	 @return A task that represents the work queued to execute.
	 {@link ITurnContext}
	 {@link Bot.Schema.IActivity}
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async void OnTurnAsync(TurnContext turnContext, NextDelegate nextTurn)
	public final void OnTurnAsync(TurnContext turnContext, NextDelegate nextTurn)
	{
		// log incoming activity at beginning of turn
		if (turnContext.getActivity() != null)
		{
			if (StringUtils.isBlank((String)turnContext.getActivity().From.Properties["role"]))
			{
				turnContext.getActivity().From.Properties["role"] = "user";
			}

			LogActivity(CloneActivity(turnContext.getActivity()));
		}

		// hook up onSend pipeline
		turnContext.OnSendActivities(async(ctx, activities, nextSend) ->
		{
				// run full pipeline
				var responses = await nextSend();

				for (var activity : activities)
				{
					LogActivity(CloneActivity(activity));
				}

				return responses;
		});

		// hook up update activity pipeline
		turnContext.OnUpdateActivity(async(ctx, activity, nextUpdate) ->
		{
				// run full pipeline
				var response = await nextUpdate();

				// add Message Update activity
				IActivity updateActivity = CloneActivity(activity);
				updateActivity.Type = ActivityTypes.MessageUpdate;
				LogActivity(updateActivity);
				return response;
		});

		// hook up delete activity pipeline
		turnContext.OnDeleteActivity(async(ctx, reference, nextDelete) ->
		{
				// run full pipeline
				await nextDelete();

				// add MessageDelete activity
				// log as MessageDelete activity
				Activity deleteActivity = new Activity();
				deleteActivity.Type = ActivityTypes.MessageDelete;
				deleteActivity.Id = reference.ActivityId;

				LogActivity(deleteActivity);
		});

		// process bot logic
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		await nextTurn.invoke(cancellationToken);

		// flush transcript at end of turn
		while (!transcript.isEmpty())
		{
			try
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var activity = transcript.poll();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await logger.LogActivityAsync(activity);
			}
			catch (RuntimeException err)
			{
				System.Diagnostics.Trace.TraceError(String.format("Transcript logActivity failed with %1$s", err));
			}
		}
	}

	private static IActivity CloneActivity(IActivity activity)
	{
		activity = JsonConvert.<Activity>DeserializeObject(JsonConvert.SerializeObject(activity, _jsonSettings));
		return activity;
	}

	private void LogActivity(IActivity activity)
	{
		synchronized (transcript)
		{
			if (activity.Timestamp == null)
			{
				activity.Timestamp = LocalDateTime.UtcNow;
			}

			transcript.offer(activity);
		}
	}
}