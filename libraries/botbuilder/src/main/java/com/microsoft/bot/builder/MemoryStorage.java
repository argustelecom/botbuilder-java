package Microsoft.Bot.Builder;

import Newtonsoft.Json.*;
import Newtonsoft.Json.Linq.*;
import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 A storage layer that uses an in-memory dictionary.
*/
public class MemoryStorage implements IStorage
{
	private static final JsonSerializer StateJsonSerializer = new JsonSerializer() {TypeNameHandling = TypeNameHandling.All};

	private HashMap<String, JObject> _memory;
	private final Object _syncroot = new Object();
	private int _eTag = 0;

	/** 
	 Initializes a new instance of the <see cref="MemoryStorage"/> class.
	 
	 @param dictionary A pre-existing dictionary to use; or null to use a new one.
	*/

	public MemoryStorage()
	{
		this(null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public MemoryStorage(Dictionary<string, JObject> dictionary = null)
	public MemoryStorage(HashMap<String, JObject> dictionary)
	{
		_memory = (dictionary != null) ? dictionary : new HashMap<String, JObject>();
	}

	/** 
	 Deletes storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to delete.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 {@link ReadAsync(string[], CancellationToken)}
	 {@link WriteAsync(IDictionary{string, object}, CancellationToken)}
	*/
	public final Task DeleteAsync(String[] keys, CancellationToken cancellationToken)
	{
		synchronized (_syncroot)
		{
			for (String key : keys)
			{
				_memory.remove(key);
			}
		}

		return Task.CompletedTask;
	}

	/** 
	 Reads storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to read.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 the items read, indexed by key.
	 {@link DeleteAsync(string[], CancellationToken)}
	 {@link WriteAsync(IDictionary{string, object}, CancellationToken)}
	*/
	public final Task<Map<String, Object>> ReadAsync(String[] keys, CancellationToken cancellationToken)
	{
		HashMap<String, Object> storeItems = new HashMap<String, Object>(keys.length);
		synchronized (_syncroot)
		{
			for (String key : keys)
			{
				TValue state;
				if (_memory.containsKey(key) ? (state = _memory.get(key)) == state : false)
				{
					if (state != null)
					{
						storeItems.put(key, state.<Object>ToObject(StateJsonSerializer));
					}
				}
			}
		}

		return Task.<Map<String, Object>>FromResult(storeItems);
	}

	/** 
	 Writes storage items to storage.
	 
	 @param changes The items to write, indexed by key.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 {@link DeleteAsync(string[], CancellationToken)}
	 {@link ReadAsync(string[], CancellationToken)}
	*/
	public final Task WriteAsync(Map<String, Object> changes, CancellationToken cancellationToken)
	{
		synchronized (_syncroot)
		{
			for (Map.Entry<String, Object> change : changes.entrySet())
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var newValue = change.getValue();

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var oldStateETag = null;

				TValue oldState;
				if (_memory.containsKey(change.getKey()) ? (oldState = _memory.get(change.getKey())) == oldState : false)
				{
					Object etag;
//C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
					if (oldState.TryGetValue("eTag", out etag))
					{
						oldStateETag = etag.<String>Value();
					}
				}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var newState = JObject.FromObject(newValue, StateJsonSerializer);

				// Set ETag if applicable
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (newValue is IStoreItem newStoreItem)
				if (newValue instanceof IStoreItem newStoreItem)
				{
					if (oldStateETag != null && !newStoreItem.ETag.equals("*") && newStoreItem.ETag != oldStateETag)
					{
						throw new RuntimeException(String.format("Etag conflict.\r\n\r\nOriginal: %1$s\r\nCurrent: %2$s", newStoreItem.ETag, oldStateETag));
					}

					newState["eTag"] = (_eTag++).toString();
				}

				_memory.put(change.getKey(), newState);
			}
		}

		return Task.CompletedTask;
	}
}