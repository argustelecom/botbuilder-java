package com.microsoft.bot.builder;

import com.microsoft.bot.schema.models.Activity;

import java.util.*;
import java.util.concurrent.CompletableFuture;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 The memory transcript store stores transcripts in volatile memory in a Dictionary.
 
 
 Because this uses an unbounded volitile dictionary this should only be used for unit tests or non-production environments.
 
*/
public class MemoryTranscriptStore implements ITranscriptStore
{
	private HashMap<String, HashMap<String, ArrayList<Activity>>> _channels = new HashMap<String, HashMap<String, ArrayList<Activity>>>();

	/** 
	 Logs an activity to the transcript.
	 
	 @param activity The activity to log.
	 @return A task that represents the work queued to execute.
	*/
	public final void LogActivityAsync(Activity activity)
	{
		if (activity == null)
		{
			throw new NullPointerException("activity cannot be null for LogActivity()");
		}

		synchronized (_channels)
		{
			HashMap<String, ArrayList<Activity>> channel;
			if (_channels.containsKey(activity.channelId()))
			{
				channel = _channels.get(activity.channelId());
			}
			else
			{
				channel = new HashMap<String, ArrayList<Activity>>();
				_channels.put(activity.channelId(), channel);
			}

			ArrayList<Activity> transcript;
			String conversationId = activity.conversation().id();
			if (channel.containsKey(conversationId))
			{
				transcript = channel.get(conversationId);
			}
			else
			{
				transcript = new ArrayList<Activity>();
				channel.put(conversationId, transcript);
			}

			((ArrayList) transcript).add(activity);
		}
	}

	/** 
	 Gets from the store activities that match a set of criteria.
	 
	 @param channelId The ID of the channel the conversation is in.
	 @param conversationId The ID of the conversation.
	 @param continuationToken
	 @param startDate A cutoff date. Activities older than this date are not included.
	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains the matching activities.
	*/

	public final CompletableFuture<PagedResult<Activity>> GetTranscriptActivitiesAsync(String channelId, String conversationId, String continuationToken)
	{
		return GetTranscriptActivitiesAsync(channelId, conversationId, continuationToken, null);
	}

	public final CompletableFuture<PagedResult<Activity>> GetTranscriptActivitiesAsync(String channelId, String conversationId)
	{
		return GetTranscriptActivitiesAsync(channelId, conversationId, null, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public CompletableFuture<PagedResult<Activity>> GetTranscriptActivitiesAsync(string channelId, string conversationId, string continuationToken = null, DateTimeOffset startDate = default(DateTimeOffset))
	public final CompletableFuture<PagedResult<Activity>> GetTranscriptActivitiesAsync(String channelId, String conversationId, String continuationToken, DateTimeOffset startDate)
	{
		if (channelId == null)
		{
			throw new NullPointerException(String.format("missing %1$s", "channelId"));
		}

		if (conversationId == null)
		{
			throw new NullPointerException(String.format("missing %1$s", "conversationId"));
		}

		PagedResult<Activity> pagedResult = new PagedResult<Activity>();
		synchronized (_channels)
		{
			HashMap<String, ArrayList<Activity>> channel;
			if (_channels.containsKey(channelId) ? (channel = _channels.get(channelId)) == channel : false)
			{
				ArrayList<Activity> transcript;
				if (channel.containsKey(conversationId) ? (transcript = channel.get(conversationId)) == transcript : false)
				{
					if (continuationToken != null)
					{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
						pagedResult.setItems(transcript.OrderBy(a -> a.Timestamp).Where(a -> a.Timestamp >= startDate).SkipWhile(a = !continuationToken.equals(> a.Id)).Skip(1).Take(20).ToArray());

						if (pagedResult.getItems().Count() == 20)
						{
							pagedResult.setContinuationToken(pagedResult.getItems().Last().Id);
						}
					}
					else
					{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
						pagedResult.setItems(transcript.OrderBy(a -> a.Timestamp).Where(a -> a.Timestamp >= startDate).Take(20).ToArray());

						if (pagedResult.getItems().Count() == 20)
						{
							pagedResult.setContinuationToken(pagedResult.getItems().Last().Id);
						}
					}
				}
			}
		}

		return Task.FromResult(pagedResult);
	}

	/** 
	 Deletes conversation data from the store.
	 
	 @param channelId The ID of the channel the conversation is in.
	 @param conversationId The ID of the conversation to delete.
	 @return A task that represents the work queued to execute.
	*/
	public final void DeleteTranscriptAsync(String channelId, String conversationId)
	{
		if (channelId == null)
		{
			throw new NullPointerException(String.format("%1$s should not be null", "channelId"));
		}

		if (conversationId == null)
		{
			throw new NullPointerException(String.format("%1$s should not be null", "conversationId"));
		}

		synchronized (_channels)
		{
			TValue channel;
			if (_channels.containsKey(channelId) ? (channel = _channels.get(channelId)) == channel : false)
			{
				if (channel.ContainsKey(conversationId))
				{
					channel.Remove(conversationId);
				}
			}
		}

		return Task.CompletedTask;
	}

	/** 
	 Gets the conversations on a channel from the store.
	 
	 @param channelId The ID of the channel.
	 @param continuationToken
	 @return A task that represents the work queued to execute.
	 
	*/

	public final CompletableFuture<PagedResult<TranscriptInfo>> ListTranscriptsAsync(String channelId)
	{
		return ListTranscriptsAsync(channelId, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public CompletableFuture<PagedResult<TranscriptInfo>> ListTranscriptsAsync(string channelId, string continuationToken = null)
	public final CompletableFuture<PagedResult<TranscriptInfo>> ListTranscriptsAsync(String channelId, String continuationToken)
	{
		if (channelId == null)
		{
			throw new NullPointerException(String.format("missing %1$s", "channelId"));
		}

		PagedResult<TranscriptInfo> pagedResult = new PagedResult<TranscriptInfo>();
		synchronized (_channels)
		{
			TValue channel;
			if (_channels.containsKey(channelId) ? (channel = _channels.get(channelId)) == channel : false)
			{
				if (continuationToken != null)
				{
					TranscriptInfo tempVar = new TranscriptInfo();
					tempVar.setChannelId(channelId);
					tempVar.setId(c.Key);
					tempVar.setCreated(c.Value.FirstOrDefault() == null ? null : ((c.Value.FirstOrDefault().Timestamp) != null) ? c.Value.FirstOrDefault().Timestamp : new DateTimeOffset());
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
					pagedResult.setItems(channel.Select(c -> tempVar).OrderBy(c -> c.Created).SkipWhile(c = !continuationToken.equals(> c.Id)).Skip(1).Take(20).ToArray());

					if (pagedResult.getItems().Count() == 20)
					{
						pagedResult.setContinuationToken(pagedResult.getItems().Last().Id);
					}
				}
				else
				{
					TranscriptInfo tempVar2 = new TranscriptInfo();
					tempVar2.setChannelId(channelId);
					tempVar2.setId(c.Key);
					tempVar2.setCreated(c.Value.FirstOrDefault() == null ? null : ((c.Value.FirstOrDefault().Timestamp) != null) ? c.Value.FirstOrDefault().Timestamp : new DateTimeOffset());
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
					pagedResult.setItems(channel.Select(c -> tempVar2).OrderBy(c -> c.Created).Take(20).ToArray());

					if (pagedResult.getItems().Count() == 20)
					{
						pagedResult.setContinuationToken(pagedResult.getItems().Last().Id);
					}
				}
			}
		}

		return Task.FromResult(pagedResult);
	}
}