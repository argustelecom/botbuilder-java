package com.microsoft.bot.builder;

import Newtonsoft.Json.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Representas a transcript logger that writes activites to a <see cref="Trace"/> object.
*/
public class TraceTranscriptLogger implements ITranscriptLogger
{
	private static JsonSerializerSettings serializationSettings = new JsonSerializerSettings() {NullValueHandling = NullValueHandling.Ignore, Formatting = Formatting.Indented};

	/** 
	 Log an activity to the transcript.
	 
	 @param activity The activity to transcribe.
	 @return A task that represents the work queued to execute.
	*/
	public final void LogActivityAsync(IActivity activity)
	{
		BotAssert.ActivityNotNull(activity);
		Trace.TraceInformation(JsonConvert.SerializeObject(activity, serializationSettings));
		return Task.CompletedTask;
	}
}