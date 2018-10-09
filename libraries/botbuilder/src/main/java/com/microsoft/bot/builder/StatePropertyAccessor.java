package com.microsoft.bot.builder;

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.


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
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the result of the asynchronous operation.
	*/

	CompletableFuture<T> GetAsync(TurnContext turnContext, Func<T> defaultValueFactory);
	CompletableFuture<T> GetAsync(TurnContext turnContext);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: CompletableFuture<T> GetAsync(TurnContext turnContext, Func<T> defaultValueFactory = null, CancellationToken cancellationToken = default(CancellationToken));
	CompletableFuture<T> GetAsync(TurnContext turnContext, Func<T> defaultValueFactory);

	/** 
	 Delete the property from the source.
	 
	 @param turnContext Turn Context.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	Task DeleteAsync(TurnContext turnContext);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: void DeleteAsync(TurnContext turnContext, CancellationToken cancellationToken = default(CancellationToken));
	Task DeleteAsync(TurnContext turnContext);

	/** 
	 Set the property value on the source.
	 
	 @param turnContext Turn Context.
	 @param value The value to set.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	Task SetAsync(TurnContext turnContext, T value);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: void SetAsync(TurnContext turnContext, T value, CancellationToken cancellationToken = default(CancellationToken));
	Task SetAsync(TurnContext turnContext, T value);
}