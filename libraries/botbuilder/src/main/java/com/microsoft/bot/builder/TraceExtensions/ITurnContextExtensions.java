package Microsoft.Bot.Builder.TraceExtensions;

import Microsoft.Bot.Builder.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.



/** 
 Contains methods for woring with <see cref="ITurnContext"/> objects.
*/
public final class ITurnContextExtensions
{
	/** 
	 Sends a trace activity to the <see cref="BotAdapter"/> for logging purposes.
	 
	 @param turnContext The context for the current turn.
	 @param name The value to assign to the activity's <see cref="Activity.Name"/> property.
	 @param value The value to assign to the activity's <see cref="Activity.Value"/> property.
	 @param valueType The value to assign to the activity's <see cref="Activity.ValueType"/> property.
	 @param label The value to assign to the activity's <see cref="Activity.Label"/> property.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the adapter is being hosted in the Emulator, the task result contains
	 a <see cref="ResourceResponse"/> object with the original trace activity's ID; otherwise,
	 it containsa <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	*/

	public static Task<ResourceResponse> TraceActivityAsync(ITurnContext turnContext, String name, Object value, String valueType, String label)
	{
		return TraceActivityAsync(turnContext, name, value, valueType, label, null);
	}

	public static Task<ResourceResponse> TraceActivityAsync(ITurnContext turnContext, String name, Object value, String valueType)
	{
		return TraceActivityAsync(turnContext, name, value, valueType, null, null);
	}

	public static Task<ResourceResponse> TraceActivityAsync(ITurnContext turnContext, String name, Object value)
	{
		return TraceActivityAsync(turnContext, name, value, null, null, null);
	}

	public static Task<ResourceResponse> TraceActivityAsync(ITurnContext turnContext, String name)
	{
		return TraceActivityAsync(turnContext, name, null, null, null, null);
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: public static Task<ResourceResponse> TraceActivityAsync(this ITurnContext turnContext, string name, object value = null, string valueType = null, [CallerMemberName] string label = null, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public static Task<ResourceResponse> TraceActivityAsync(ITurnContext turnContext, String name, Object value, String valueType, String label, CancellationToken cancellationToken)
	{
		return turnContext.SendActivityAsync(turnContext.getActivity().CreateTrace(name, value, valueType, label), cancellationToken);
	}
}