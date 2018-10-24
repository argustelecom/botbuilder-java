package com.microsoft.bot.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 A storage layer that uses an in-memory dictionary.
*/
public class MemoryStorage implements IStorage
{
	private static final ObjectMapper _mapper = new ObjectMapper();
    private HashMap<String, Object> _memory;
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

	public MemoryStorage(HashMap<String, Object> dictionary)
	{
	    _mapper.enableDefaultTyping();
		_memory = (dictionary != null) ? dictionary : new HashMap<String, Object>();
	}

	/** 
	 Deletes storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to delete.

	 @return A task that represents the work queued to execute.
	 {@link ReadAsync(string[] )}
	 {@link WriteAsync(IDictionary{string, object} )}
	*/
	public final CompletableFuture DeleteAsync(String[] keys)
	{
	    return CompletableFuture.supplyAsync(() -> {
            synchronized (_syncroot)
            {
                for (String key : keys)
                {
                    _memory.remove(key);
                }
            }
        });
	}

	/** 
	 Reads storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to read.

	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 the items read, indexed by key.
	 {@link DeleteAsync(string[] )}
	 {@link WriteAsync(IDictionary{string, object} )}
	*/

	public final CompletableFuture<Map<String, Object>> ReadAsync(String[] keys)
	{
	    return CompletableFuture.supplyAsync(() -> {
            HashMap<String, Object> storeItems = new HashMap<String, Object>(keys.length);
            synchronized (_syncroot)
            {
                for (String key : keys)
                {
                    Object state;
                    if (_memory.containsKey(key))
                    {
                        state = _memory.get(key);
                        if (state != null)
                        {
                            storeItems.put(key, state);
                        }
                    }
                }
            }
            return storeItems;
        });
	}

	/** 
	 Writes storage items to storage.
	 
	 @param changes The items to write, indexed by key.

	 @return A task that represents the work queued to execute.
	 {@link DeleteAsync(string[] )}
	 {@link ReadAsync(string[] )}
	*/
	public final CompletableFuture WriteAsync(Map<String, Object> changes)
	{
	    return CompletableFuture.runAsync(() -> {
            synchronized (_syncroot)
            {
                for (Map.Entry<String, Object> change : changes.entrySet())
                {
                    Object newValue = change.getValue();

                    Object oldStateETag = null;
                    Object oldState = null;
                    if (_memory.containsKey(change.getKey()))
                    {
                        oldState = _memory.get(change.getKey());
                        if (oldState instanceof Map)
                        {
                            Map<String, Object> oldStateDict = (Map<String, Object>)oldState;
                            if (oldStateDict.containsKey("eTag"))
                            {
                                oldStateETag = (String)oldStateDict.get("eTag");
                            }

                        }
                    }

                    // Set ETag if applicable
                    IStoreItem newStoreItem = null;
                    if (newValue instanceof IStoreItem )
                    {
                        newStoreItem = (IStoreItem)newValue;
                        if (oldStateETag != null && !newStoreItem.getETag().equals("*") && newStoreItem.getETag() != oldStateETag)
                        {
                            throw new RuntimeException(String.format("Etag conflict.\r\n\r\nOriginal: %1$s\r\nCurrent: %2$s", newStoreItem.getETag(), oldStateETag));
                        }
                        newStoreItem.setETag(Integer.toString(_eTag++));
                    }
                    _memory.put(change.getKey(), newStoreItem);
                }
            }
        });
	}
}