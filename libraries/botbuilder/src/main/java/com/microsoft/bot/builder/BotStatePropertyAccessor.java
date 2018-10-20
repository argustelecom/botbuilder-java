package com.microsoft.bot.builder;

import java.util.concurrent.CompletableFuture;

/**
 Implements IPropertyAccessor for an IPropertyContainer.
 Note the semantic of this accessor are intended to be lazy, this means teh Get, Set and Delete
 methods will first call LoadAsync. This will be a no-op if the data is already loaded.
 The implication is you can just use this accessor in the application code directly without first calling LoadAsync
 this approach works with the AutoSaveStateMiddleware which will save as needed at the end of a turn.

 <typeparam name="T">type of value the propertyAccessor accesses.</typeparam>
 */
private class BotStatePropertyAccessor<T> implements StatePropertyAccessor<T>
{
    private BotState _botState;

    public BotStatePropertyAccessor(BotState botState, String name)
    {
        _botState = botState;
        setName(name);
    }

    /**
     Gets name of the property.

     <value>
     name of the property.
     </value>
     */
    private String Name;
    public final String getName()
    {
        return Name;
    }
    private void setName(String value)
    {
        Name = value;
    }

    /**
     Delete the property. The semantics are intended to be lazy, note the use of LoadAsync at the start.

     @param turnContext The turn context.
     @param cancellationToken The cancellation token.
     @return A <see cref="Task"/> representing the asynchronous operation.
     */
    public final CompletableFuture DeleteAsync(TurnContext turnContext)
    {
        _botState.LoadAsync(turnContext, false, cancellationToken).join();
        _botState.DeletePropertyValueAsync(turnContext, getName(), cancellationToken).join();
    }

    /**
     Get the property value. The semantics are intended to be lazy, note the use of LoadAsync at the start.
     ///
     @param turnContext The context object for this turn.
     @param defaultValueFactory Defines the default value. Invoked when no value been set for the requested state property.  If defaultValueFactory is defined as null, the MissingMemberException will be thrown if the underlying property is not set.
     @param cancellationToken The cancellation token.
     @return A <see cref="Task"/> representing the asynchronous operation.
     */
    public final CompletableFuture<T> GetAsync(TurnContext turnContext, tangible.Func0Param<T> defaultValueFactory)
    {
        _botState.LoadAsync(turnContext, false).join();
        try
        {
            return _botState.<T>GetPropertyValueAsync(turnContext, getName()).join();
        }
        catch (KeyNotFoundException e)
        {
            // ask for default value from factory
            if (defaultValueFactory == null)
            {
                throw new MissingMemberException("Property not set and no default provided.");
            }

            var result = defaultValueFactory.invoke();

            // save default value for any further calls
            SetAsync(turnContext, result).join();
            return result;
        }
    }

    /**
     Set the property value. The semantics are intended to be lazy, note the use of LoadAsync at the start.

     @param turnContext turn context.
     @param value value.
     @return A <see cref="Task"/> representing the asynchronous operation.
     */
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task SetAsync(ITurnContext turnContext, T value, CancellationToken cancellationToken)
    public final CompletableFuture SetAsync(TurnContext turnContext, T value)
    {
        _botState.LoadAsync(turnContext, false).join();
        _botState.SetPropertyValueAsync(turnContext, getName(), value).join();
    }
}
