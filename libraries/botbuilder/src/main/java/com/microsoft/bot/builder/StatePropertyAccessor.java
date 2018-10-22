package com.microsoft.bot.builder;

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 Interface which defines methods for how you can get data from a property source such as BotState.
 
 <typeparam name="T">type of the property.</typeparam>
*/
public interface StatePropertyAccessor<T> extends StatePropertyInfo
{
	/** 
	 Get the property value from the source.
	 If the property is not set, and no default value was defined, a <see cref="MissingMemberException"/> is thrown.
	 
	 @param turnContext Turn Context.
	 @param defaultValueFactory Function which defines the property value to be returned if no value has been set.
	 @return A <see cref="Task"/> representing the result of the asynchronous operation.
	*/

	CompletableFuture<T> GetAsync(TurnContext turnContext, Supplier<T> defaultValueFactory);

	/** 
	 Delete the property from the source.
	 
	 @param turnContext Turn Context.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/
	CompletableFuture DeleteAsync(TurnContext turnContext);

	/** 
	 Set the property value on the source.
	 
	 @param turnContext Turn Context.
	 @param value The value to set.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	CompletableFuture SetAsync(TurnContext turnContext, T value);
}