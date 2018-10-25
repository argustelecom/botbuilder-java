package com.microsoft.bot.builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


import com.microsoft.bot.schema.models.Activity;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

/**
 Represents a store for recording conversations.
*/
public interface ITranscriptStore extends ITranscriptLogger
{
	/** 
	 Gets from the store activities that match a set of criteria.
	 
	 @param channelId The ID of the channel the conversation is in.
	 @param conversationId The ID of the conversation.
	 @param continuationToken The continuation token (if available).
	 @param startDate A cutoff date. Activities older than this date are not included.
	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the matching activities.
	*/

	CompletableFuture<PagedResult<Activity>> GetTranscriptActivitiesAsync(String channelId, String conversationId);
	CompletableFuture<PagedResult<Activity>> GetTranscriptActivitiesAsync(String channelId, String conversationId, String continuationToken);
	CompletableFuture<PagedResult<Activity>> GetTranscriptActivitiesAsync(String channelId, String conversationId, String continuationToken, OffsetDateTime startDate);

	/** 
	 Gets the conversations on a channel from the store.
	 
	 @param channelId The ID of the channel.
	 @param continuationToken Continuation token (if available).
	 @return A task that represents the work queued to execute.
	 List all transcripts for given ChannelID.
	*/

	CompletableFuture<PagedResult<TranscriptInfo>> ListTranscriptsAsync(String channelId);
	CompletableFuture<PagedResult<TranscriptInfo>> ListTranscriptsAsync(String channelId, String continuationToken);

	/** 
	 Deletes conversation data from the store.
	 
	 @param channelId The ID of the channel the conversation is in.
	 @param conversationId The ID of the conversation to delete.
	 @return A task that represents the work queued to execute.
	*/
	CompletableFuture DeleteTranscriptAsync(String channelId, String conversationId);
}