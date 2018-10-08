package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


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

	Task<PagedResult<IActivity>> GetTranscriptActivitiesAsync(String channelId, String conversationId, String continuationToken);
	Task<PagedResult<IActivity>> GetTranscriptActivitiesAsync(String channelId, String conversationId);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task<PagedResult<IActivity>> GetTranscriptActivitiesAsync(string channelId, string conversationId, string continuationToken = null, DateTimeOffset startDate = default(DateTimeOffset));
	Task<PagedResult<IActivity>> GetTranscriptActivitiesAsync(String channelId, String conversationId, String continuationToken, DateTimeOffset startDate);

	/** 
	 Gets the conversations on a channel from the store.
	 
	 @param channelId The ID of the channel.
	 @param continuationToken Continuation token (if available).
	 @return A task that represents the work queued to execute.
	 List all transcripts for given ChannelID.
	*/

	Task<PagedResult<TranscriptInfo>> ListTranscriptsAsync(String channelId);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task<PagedResult<TranscriptInfo>> ListTranscriptsAsync(string channelId, string continuationToken = null);
	Task<PagedResult<TranscriptInfo>> ListTranscriptsAsync(String channelId, String continuationToken);

	/** 
	 Deletes conversation data from the store.
	 
	 @param channelId The ID of the channel the conversation is in.
	 @param conversationId The ID of the conversation to delete.
	 @return A task that represents the work queued to execute.
	*/
	Task DeleteTranscriptAsync(String channelId, String conversationId);
}