package Microsoft.Bot.Builder;

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.


/** 
 Interface which defines methods for how you can get data from a property source such as BotState.
 
 <typeparam name="T">type of the property.</typeparam>
*/
public interface IStatePropertyAccessor<T> extends IStatePropertyInfo
{
	/** 
	 Get the property value from the source.
	 If the property is not set, and no default value was defined, a <see cref="MissingMemberException"/> is thrown.
	 
	 @param turnContext Turn Context.
	 @param defaultValueFactory Function which defines the property value to be returned if no value has been set.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the result of the asynchronous operation.
	*/

	Task<T> GetAsync(ITurnContext turnContext, Func<T> defaultValueFactory);
	Task<T> GetAsync(ITurnContext turnContext);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task<T> GetAsync(ITurnContext turnContext, Func<T> defaultValueFactory = null, CancellationToken cancellationToken = default(CancellationToken));
	Task<T> GetAsync(ITurnContext turnContext, Func<T> defaultValueFactory, CancellationToken cancellationToken);

	/** 
	 Delete the property from the source.
	 
	 @param turnContext Turn Context.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	Task DeleteAsync(ITurnContext turnContext);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task DeleteAsync(ITurnContext turnContext, CancellationToken cancellationToken = default(CancellationToken));
	Task DeleteAsync(ITurnContext turnContext, CancellationToken cancellationToken);

	/** 
	 Set the property value on the source.
	 
	 @param turnContext Turn Context.
	 @param value The value to set.
	 @param cancellationToken The cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	Task SetAsync(ITurnContext turnContext, T value);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task SetAsync(ITurnContext turnContext, T value, CancellationToken cancellationToken = default(CancellationToken));
	Task SetAsync(ITurnContext turnContext, T value, CancellationToken cancellationToken);
}