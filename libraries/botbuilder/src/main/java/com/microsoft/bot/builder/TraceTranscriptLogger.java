package com.microsoft.bot.builder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.microsoft.bot.schema.models.Activity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Representas a transcript logger that writes activites to a <see cref="Trace"/> object.
*/
public class TraceTranscriptLogger implements ITranscriptLogger
{
	ObjectMapper objectMapper = new ObjectMapper();
	protected static final Logger logger = LogManager.getLogger();

	/** 
	 Log an activity to the transcript.
	 
	 @param activity The activity to transcribe.
	 @return A task that represents the work queued to execute.
	*/
	public final CompletableFuture LogActivityAsync(Activity activity) throws JsonProcessingException {
		return CompletableFuture.runAsync(() -> {
			BotAssert.ActivityNotNull(activity);
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			try {
				logger.info(objectMapper.writeValueAsString(activity));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}
}