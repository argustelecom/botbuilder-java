package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Handles persistence of a conversation state object using the conversation ID as part of the key.
*/
public class ConversationState extends BotState
{
	/** 
	 Initializes a new instance of the <see cref="ConversationState"/> class.
	 
	 @param storage The storage provider to use.
	*/
	public ConversationState(IStorage storage)
	{
		super(storage, "ConversationState");
	}

	/** 
	 Gets the key to use when reading and writing state to and from storage.
	 
	 @param turnContext The context object for this turn.
	 @return The storage key.
	*/
	@Override
	protected String GetStorageKey(ITurnContext turnContext)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: var channelId = turnContext.Activity.ChannelId ?? throw new ArgumentNullException("invalid activity-missing channelId");
		var channelId = ((turnContext.getActivity().ChannelId) != null) ? turnContext.getActivity().ChannelId : throw new NullPointerException("invalid activity-missing channelId");
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: var conversationId = turnContext.Activity.Conversation == null ? null : turnContext.Activity.Conversation.Id ?? throw new ArgumentNullException("invalid activity-missing Conversation.Id");
		boolean conversationId = turnContext.getActivity().Conversation == null ? null : ((turnContext.getActivity().Conversation.Id) != null) ? turnContext.getActivity().Conversation.Id : throw new NullPointerException("invalid activity-missing Conversation.Id");
		return String.format("%1$s/conversations/%2$s", channelId, conversationId);
	}
}