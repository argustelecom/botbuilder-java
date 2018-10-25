// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.TraceExtensions;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.TurnContextImpl;
import com.microsoft.bot.schema.ActivityImpl;
import com.microsoft.bot.schema.models.ResourceResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


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

	 @return A task that represents the work queued to execute.
	 If the adapter is being hosted in the Emulator, the task result contains
	 a <see cref="ResourceResponse"/> object with the original trace activity's ID; otherwise,
	 it containsa <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 */

	public static CompletableFuture<ResourceResponse> TraceActivityAsync(TurnContext turnContext, String name, Object value, String valueType)
	{
		return TraceActivityAsync(turnContext, name, value, valueType, null);
	}
	/**
	 Sends a trace activity to the <see cref="BotAdapter"/> for logging purposes.

	 @param turnContext The context for the current turn.
	 @param name The value to assign to the activity's <see cref="Activity.Name"/> property.
	 @param value The value to assign to the activity's <see cref="Activity.Value"/> property.

	 @return A task that represents the work queued to execute.
	 If the adapter is being hosted in the Emulator, the task result contains
	 a <see cref="ResourceResponse"/> object with the original trace activity's ID; otherwise,
	 it containsa <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 */
	public static CompletableFuture<ResourceResponse> TraceActivityAsync(TurnContext turnContext, String name, Object value)
	{
		return TraceActivityAsync(turnContext, name, value, null, null);
	}


	/**
	 Sends a trace activity to the <see cref="BotAdapter"/> for logging purposes.

	 @param turnContext The context for the current turn.
	 @param name The value to assign to the activity's <see cref="Activity.Name"/> property.

	 @return A task that represents the work queued to execute.
	 If the adapter is being hosted in the Emulator, the task result contains
	 a <see cref="ResourceResponse"/> object with the original trace activity's ID; otherwise,
	 it containsa <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 */
	public static CompletableFuture<ResourceResponse> TraceActivityAsync(TurnContext turnContext, String name)
	{
		return TraceActivityAsync(turnContext, name, null, null, null);
	}

	/**
	 Sends a trace activity to the <see cref="BotAdapter"/> for logging purposes.

	 @param turnContext The context for the current turn.
	 @param name The value to assign to the activity's <see cref="Activity.Name"/> property.
	 @param value The value to assign to the activity's <see cref="Activity.Value"/> property.
	 @param valueType The value to assign to the activity's <see cref="Activity.ValueType"/> property.
	 @param label The value to assign to the activity's <see cref="Activity.Label"/> property.

	 @return A task that represents the work queued to execute.
	 If the adapter is being hosted in the Emulator, the task result contains
	 a <see cref="ResourceResponse"/> object with the original trace activity's ID; otherwise,
	 it containsa <see cref="ResourceResponse"/> object containing the ID that the receiving
	 channel assigned to the activity.
	 */
	public static CompletableFuture<ResourceResponse> TraceActivityAsync(TurnContext turnContext, String name, Object value, String valueType, String label) {
	    return CompletableFuture.supplyAsync(() -> {
            try {
                return ((TurnContextImpl)turnContext).SendActivityAsync(((ActivityImpl)turnContext.activity()).CreateTrace(name, value, valueType, label)).join();
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });

	}
}