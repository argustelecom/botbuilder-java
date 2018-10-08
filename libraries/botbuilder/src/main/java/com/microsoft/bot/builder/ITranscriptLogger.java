package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Transcript logger stores activities for conversations for recall.
*/
public interface ITranscriptLogger
{
	/** 
	 Log an activity to the transcript.
	 
	 @param activity The activity to transcribe.
	 @return A task that represents the work queued to execute.
	*/
	Task LogActivityAsync(IActivity activity);
}