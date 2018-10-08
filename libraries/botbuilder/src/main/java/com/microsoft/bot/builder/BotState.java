package Microsoft.Bot.Builder;

import Newtonsoft.Json.*;
import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Reads and writes state for your bot to storage.
*/
public abstract class BotState implements IPropertyManager
{
	private String _contextServiceKey;
	private IStorage _storage;

	/** 
	 Initializes a new instance of the <see cref="BotState"/> class.
	 
	 @param storage The storage provider to use.
	 @param contextServiceKey the key for caching on the context services dictionary.
	*/
	public BotState(IStorage storage, String contextServiceKey)
	{
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: _storage = storage ?? throw new ArgumentNullException(nameof(storage));
		_storage = (storage != null) ? storage : throw new NullPointerException("storage");
//C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
//ORIGINAL LINE: _contextServiceKey = contextServiceKey ?? throw new ArgumentNullException(nameof(contextServiceKey));
		_contextServiceKey = (contextServiceKey != null) ? contextServiceKey : throw new NullPointerException("contextServiceKey");
	}

	/** 
	 Create a property definition and register it with this BotState.
	 
	 <typeparam name="T">type of property.</typeparam>
	 @param name name of the property.
	 @return The created state property accessor.
	*/
	public final <T> IStatePropertyAccessor<T> CreateProperty(String name)
	{
		if (tangible.StringHelper.isNullOrWhiteSpace(name))
		{
			throw new NullPointerException("name");
		}

		return new BotStatePropertyAccessor<T>(this, name);
	}

	/** 
	 Reads in  the current state object and caches it in the context object for this turm.
	 
	 @param turnContext The context object for this turn.
	 @param force Optional. True to bypass the cache.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/

	public final Task LoadAsync(ITurnContext turnContext, boolean force)
	{
		return LoadAsync(turnContext, force, null);
	}

	public final Task LoadAsync(ITurnContext turnContext)
	{
		return LoadAsync(turnContext, false, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task LoadAsync(ITurnContext turnContext, bool force = false, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task LoadAsync(ITurnContext turnContext, boolean force, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		CachedBotState cachedState = turnContext.getTurnState().<CachedBotState>Get(_contextServiceKey);
		String storageKey = GetStorageKey(turnContext);
		if (force || cachedState == null || cachedState.getState() == null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			var items = await Microsoft.Bot.Builder.StorageExtensions.ReadAsync(_storage, new String[] {storageKey}, cancellationToken).ConfigureAwait(false);
			Object val;
//C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
			items.TryGetValue(storageKey, out val);
			turnContext.getTurnState().put(_contextServiceKey, new CachedBotState((Map<String, Object>)val));
		}
	}

	/** 
	 If it has changed, writes to storage the state object that is cached in the current context object for this turn.
	 
	 @param turnContext The context object for this turn.
	 @param force Optional. True to save state to storage whether or not there are changes.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/

	public final Task SaveChangesAsync(ITurnContext turnContext, boolean force)
	{
		return SaveChangesAsync(turnContext, force, null);
	}

	public final Task SaveChangesAsync(ITurnContext turnContext)
	{
		return SaveChangesAsync(turnContext, false, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task SaveChangesAsync(ITurnContext turnContext, bool force = false, CancellationToken cancellationToken = default(CancellationToken))
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public final Task SaveChangesAsync(ITurnContext turnContext, boolean force, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		CachedBotState cachedState = turnContext.getTurnState().<CachedBotState>Get(_contextServiceKey);
		if (force || (cachedState != null && cachedState.IsChanged()))
		{
			String key = GetStorageKey(turnContext);
			HashMap<String, Object> changes = new HashMap<String, Object>(Map.ofEntries(Map.entry(key, cachedState.getState())));
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await _storage.WriteAsync(changes).ConfigureAwait(false);
			cachedState.setHash(cachedState.ComputeHash(cachedState.getState()));
			return;
		}
	}

	/** 
	 Reset the state cache in the turn context to it's default form.
	 
	 @param turnContext The context object for this turn.
	 @param cancellationToken cancellation token.
	 @return A <see cref="Task"/> representing the asynchronous operation.
	*/

	public final Task ClearStateAsync(ITurnContext turnContext)
	{
		return ClearStateAsync(turnContext, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public Task ClearStateAsync(ITurnContext turnContext, CancellationToken cancellationToken = default(CancellationToken))
	public final Task ClearStateAsync(ITurnContext turnContext, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		CachedBotState cachedState = turnContext.getTurnState().<CachedBotState>Get(_contextServiceKey);
		if (cachedState != null)
		{
			turnContext.getTurnState().put(_contextServiceKey, new CachedBotState());
		}

		return Task.CompletedTask;
	}

	/** 
	 When overridden in a derived class, gets the key to use when reading and writing state to and from storage.
	 
	 @param turnContext The context object for this turn.
	 @return The storage key.
	*/
	protected abstract String GetStorageKey(ITurnContext turnContext);

	/** 
	 Gets a property from the state cache in the turn context.
	 
	 <typeparam name="T">The property type.</typeparam>
	 @param turnContext The context object for this turn.
	 @param propertyName The name of the property to get.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the task is successful, the result contains the property value.
	*/

	protected final <T> Task<T> GetPropertyValueAsync(ITurnContext turnContext, String propertyName)
	{
		return GetPropertyValueAsync(turnContext, propertyName, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected Task<T> GetPropertyValueAsync<T>(ITurnContext turnContext, string propertyName, CancellationToken cancellationToken = default(CancellationToken))
	protected final <T> Task<T> GetPropertyValueAsync(ITurnContext turnContext, String propertyName, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		if (propertyName == null)
		{
			throw new NullPointerException("propertyName");
		}

		CachedBotState cachedState = turnContext.getTurnState().<CachedBotState>Get(_contextServiceKey);

		// if there is no value, this will throw, to signal to IPropertyAccesor that a default value should be computed
		// This allows this to work with value types
		return Task.FromResult((T)cachedState.getState().get(propertyName));
	}

	/** 
	 Deletes a property from the state cache in the turn context.
	 
	 @param turnContext The context object for this turn.
	 @param propertyName The name of the property to delete.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/

	protected final Task DeletePropertyValueAsync(ITurnContext turnContext, String propertyName)
	{
		return DeletePropertyValueAsync(turnContext, propertyName, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected Task DeletePropertyValueAsync(ITurnContext turnContext, string propertyName, CancellationToken cancellationToken = default(CancellationToken))
	protected final Task DeletePropertyValueAsync(ITurnContext turnContext, String propertyName, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		if (propertyName == null)
		{
			throw new NullPointerException("propertyName");
		}

		CachedBotState cachedState = turnContext.getTurnState().<CachedBotState>Get(_contextServiceKey);
		cachedState.getState().remove(propertyName);
		return Task.CompletedTask;
	}

	/** 
	 Set the value of a property in the state cache in the turn context.
	 
	 @param turnContext The context object for this turn.
	 @param propertyName The name of the property to set.
	 @param value The value to set on the property.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	*/

	protected final Task SetPropertyValueAsync(ITurnContext turnContext, String propertyName, Object value)
	{
		return SetPropertyValueAsync(turnContext, propertyName, value, null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: protected Task SetPropertyValueAsync(ITurnContext turnContext, string propertyName, object value, CancellationToken cancellationToken = default(CancellationToken))
	protected final Task SetPropertyValueAsync(ITurnContext turnContext, String propertyName, Object value, CancellationToken cancellationToken)
	{
		if (turnContext == null)
		{
			throw new NullPointerException("turnContext");
		}

		if (propertyName == null)
		{
			throw new NullPointerException("propertyName");
		}

		CachedBotState cachedState = turnContext.getTurnState().<CachedBotState>Get(_contextServiceKey);
		cachedState.getState().put(propertyName, value);
		return Task.CompletedTask;
	}

	/** 
	 Internal cached bot state.
	*/
	private static class CachedBotState
	{

		public CachedBotState()
		{
			this(null);
		}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public CachedBotState(IDictionary<string, object> state = null)
		public CachedBotState(Map<String, Object> state)
		{
			setState((state != null) ? state : new java.util.concurrent.ConcurrentHashMap<String, Object>());
			setHash(ComputeHash(getState()));
		}

		private Map<String, Object> State;
		public final Map<String, Object> getState()
		{
			return State;
		}
		public final void setState(Map<String, Object> value)
		{
			State = value;
		}

		private String Hash;
		public final String getHash()
		{
			return Hash;
		}
		public final void setHash(String value)
		{
			Hash = value;
		}

		public final boolean IsChanged()
		{
			return !getHash().equals(ComputeHash(getState()));
		}

		public final String ComputeHash(Object obj)
		{
			return JsonConvert.SerializeObject(obj);
		}
	}

	/** 
	 Implements IPropertyAccessor for an IPropertyContainer.
	 Note the semantic of this accessor are intended to be lazy, this means teh Get, Set and Delete
	 methods will first call LoadAsync. This will be a no-op if the data is already loaded.
	 The implication is you can just use this accessor in the application code directly without first calling LoadAsync
	 this approach works with the AutoSaveStateMiddleware which will save as needed at the end of a turn.
	 
	 <typeparam name="T">type of value the propertyAccessor accesses.</typeparam>
	*/
	private static class BotStatePropertyAccessor<T> implements IStatePropertyAccessor<T>
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
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task DeleteAsync(ITurnContext turnContext, CancellationToken cancellationToken)
		public final Task DeleteAsync(ITurnContext turnContext, CancellationToken cancellationToken)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await _botState.LoadAsync(turnContext, false, cancellationToken).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await _botState.DeletePropertyValueAsync(turnContext, getName(), cancellationToken).ConfigureAwait(false);
		}

		/** 
		 Get the property value. The semantics are intended to be lazy, note the use of LoadAsync at the start.
		 /// 
		 @param turnContext The context object for this turn.
		 @param defaultValueFactory Defines the default value. Invoked when no value been set for the requested state property.  If defaultValueFactory is defined as null, the MissingMemberException will be thrown if the underlying property is not set.
		 @param cancellationToken The cancellation token.
		 @return A <see cref="Task"/> representing the asynchronous operation.
		*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task<T> GetAsync(ITurnContext turnContext, Func<T> defaultValueFactory, CancellationToken cancellationToken)
		public final Task<T> GetAsync(ITurnContext turnContext, tangible.Func0Param<T> defaultValueFactory, CancellationToken cancellationToken)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await _botState.LoadAsync(turnContext, false, cancellationToken).ConfigureAwait(false);
			try
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				return await _botState.<T>GetPropertyValueAsync(turnContext, getName(), cancellationToken).ConfigureAwait(false);
			}
			catch (KeyNotFoundException e)
			{
				// ask for default value from factory
				if (defaultValueFactory == null)
				{
					throw new MissingMemberException("Property not set and no default provided.");
				}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var result = defaultValueFactory.invoke();

				// save default value for any further calls
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
				await SetAsync(turnContext, result, cancellationToken).ConfigureAwait(false);
				return result;
			}
		}

		/** 
		 Set the property value. The semantics are intended to be lazy, note the use of LoadAsync at the start.
		 
		 @param turnContext turn context.
		 @param value value.
		 @param cancellationToken The cancellation token.
		 @return A <see cref="Task"/> representing the asynchronous operation.
		*/
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public async Task SetAsync(ITurnContext turnContext, T value, CancellationToken cancellationToken)
		public final Task SetAsync(ITurnContext turnContext, T value, CancellationToken cancellationToken)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await _botState.LoadAsync(turnContext, false, cancellationToken).ConfigureAwait(false);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
			await _botState.SetPropertyValueAsync(turnContext, getName(), value, cancellationToken).ConfigureAwait(false);
		}
	}
}