// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


/**
 Reads and writes state for your bot to storage.
 */
public abstract class BotState implements PropertyManager
{
    private String _contextServiceKey;
    private Storage _storage;

    /**
     Initializes a new instance of the <see cref="BotState"/> class.

     @param storage The storage provider to use.
     @param contextServiceKey the key for caching on the context services dictionary.
     */
    public BotState(Storage storage, String contextServiceKey)
    {
        if (storage == null)
        {
            throw new NullPointerException("storage");
        }
        _storage = storage;

        if (StringUtils.isBlank(contextServiceKey))
        {
            throw new NullPointerException("contextServiceKey");

        }
        _contextServiceKey = contextServiceKey;
    }

    /**
     Create a property definition and register it with this BotState.

     <typeparam name="T">type of property.</typeparam>
     @param name name of the property.
     @return The created state property accessor.
     */
    public final <T> StatePropertyAccessor<T> CreateProperty(String name)
    {
        if (StringUtils.isBlank(name))
        {
            throw new NullPointerException("name");
        }

        return new BotStatePropertyAccessor<T>(this, name);
    }

    /**
     Reads in  the current state object and caches it in the context object for this turm.

     @param turnContext The context object for this turn.
     @param force Optional. True to bypass the cache.
     @return A task that represents the work queued to execute.
     */

    public final CompletableFuture LoadAsync(TurnContext turnContext, boolean force)
    {
        return CompletableFuture.runAsync(() -> {
            if (turnContext == null) {
                throw new NullPointerException("turnContext");
            }

            CachedBotState cachedState = turnContext.turnState().<CachedBotState>Get(_contextServiceKey);
            String storageKey = GetStorageKey(turnContext);
            if (force || cachedState == null || cachedState.getState() == null) {
                Map<String, ? extends Object> items = null;
                try {
                    items = _storage.ReadAsync(new String[]{storageKey}).join();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }

                Object val = null;
                if (items.containsKey(storageKey))
                {
                    val = items.get(storageKey);
                }
                turnContext.turnState().Add(_contextServiceKey, new CachedBotState((Map<String, Object>) val));
            }
        });
    }


    /**
     If it has changed, writes to storage the state object that is cached in the current context object for this turn.

     @param turnContext The context object for this turn.
     @return A task that represents the work queued to execute.
     */
    public final CompletableFuture SaveChangesAsync(TurnContext turnContext)
    {
        return SaveChangesAsync(turnContext, false);
    }

    /**
     If it has changed, writes to storage the state object that is cached in the current context object for this turn.

     @param turnContext The context object for this turn.
     @param force  True to save state to storage whether or not there are changes.
     @return A task that represents the work queued to execute.
     */
    public final CompletableFuture SaveChangesAsync(TurnContext turnContext, boolean force)
    {
        return CompletableFuture.runAsync(() -> {
            if (turnContext == null) {
                throw new NullPointerException("turnContext");
            }

            CachedBotState cachedState = turnContext.turnState().<CachedBotState>Get(_contextServiceKey);
            if (force || (cachedState != null && cachedState.IsChanged())) {
                String key = GetStorageKey(turnContext);
                HashMap<String, Object> changes = new HashMap<String, Object>() {
                    {
                        put(key, cachedState.getState());
                    }
                };

                try {
                    _storage.WriteAsync(changes).join();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
                try {
                    cachedState.setHash(cachedState.ComputeHash(cachedState.getState()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
                return;
            }
        });
    }

    /**
     Reset the state cache in the turn context to it's default form.

     @param turnContext The context object for this turn.
     @return A <see cref="Task"/> representing the asynchronous operation.
     */
    public final CompletableFuture ClearStateAsync(TurnContext turnContext)
    {
        return CompletableFuture.runAsync(() -> {
            if (turnContext == null)
            {
                throw new NullPointerException("turnContext");
            }

            CachedBotState cachedState = turnContext.turnState().<CachedBotState>Get(_contextServiceKey);
            if (cachedState != null)
            {
                turnContext.turnState().Add(_contextServiceKey, new CachedBotState());
            }

            });
    }

    /**
     When overridden in a derived class, gets the key to use when reading and writing state to and from storage.

     @param turnContext The context object for this turn.
     @return The storage key.
     */
    protected abstract String GetStorageKey(TurnContext turnContext);

    /**
     Gets a property from the state cache in the turn context.

     <typeparam name="T">The property type.</typeparam>
     @param turnContext The context object for this turn.
     @param propertyName The name of the property to get.
     @return A task that represents the work queued to execute.
     If the task is successful, the result contains the property value.
     */
    protected final CompletableFuture<Object> GetPropertyValueAsync(TurnContext turnContext, String propertyName)
    {
        if (turnContext == null)
        {
            throw new NullPointerException("turnContext");
        }

        if (StringUtils.isBlank(propertyName))
        {
            throw new NullPointerException("propertyName");
        }

        CachedBotState cachedState = turnContext.turnState().<CachedBotState>Get(_contextServiceKey);

        // if there is no value, this will throw, to signal to IPropertyAccesor that a default value should be computed
        // This allows this to work with value types
        return CompletableFuture.completedFuture(cachedState.getState().get(propertyName));
    }

    /**
     Deletes a property from the state cache in the turn context.

     @param turnContext The context object for this turn.
     @param propertyName The name of the property to delete.
     @return A task that represents the work queued to execute.
     */
    protected final CompletableFuture DeletePropertyValueAsync(TurnContext turnContext, String propertyName)
    {
        if (turnContext == null)
        {
            throw new NullPointerException("turnContext");
        }

        if (propertyName == null)
        {
            throw new NullPointerException("propertyName");
        }

        CachedBotState cachedState = turnContext.turnState().<CachedBotState>Get(_contextServiceKey);
        cachedState.getState().remove(propertyName);
        return CompletableFuture.completedFuture(null);
    }

    /**
     Set the value of a property in the state cache in the turn context.

     @param turnContext The context object for this turn.
     @param propertyName The name of the property to set.
     @param value The value to set on the property.
     @return A task that represents the work queued to execute.
     */
    protected final CompletableFuture SetPropertyValueAsync(TurnContext turnContext, String propertyName, Object value)
    {
        if (turnContext == null)
        {
            throw new NullPointerException("turnContext");
        }

        if (propertyName == null)
        {
            throw new NullPointerException("propertyName");
        }

        CachedBotState cachedState = turnContext.turnState().<CachedBotState>Get(_contextServiceKey);
        cachedState.getState().put(propertyName, value);
        return CompletableFuture.completedFuture(null);
    }
}
