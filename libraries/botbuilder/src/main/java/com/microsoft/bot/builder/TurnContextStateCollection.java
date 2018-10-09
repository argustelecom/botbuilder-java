package com.microsoft.bot.builder;

import java.util.*;
import java.io.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Values persisted for the lifetime of the turn as part of the <see cref="ITurnContext"/>.
 
 
 TODO: add more details on what kind of values can/should be stored here, by whom and what the lifetime semantics are, etc.
 
*/
public class TurnContextStateCollection extends HashMap<String, Object> implements Closeable
{
	/** 
	 Initializes a new instance of the <see cref="TurnContextStateCollection"/> class.
	*/
	public TurnContextStateCollection()
	{
	}

	/** 
	 Gets a cached value by name from the turn's context.
	 
	 <typeparam name="T">The type of the service.</typeparam>
	 @param key The name of the service.
	 @exception ArgumentNullException <paramref name="key"/> is null.
	 @return The service object; or null if no service is registered by the key, or
	 the retrieved object does not match the service type.
	*/
	public final <T> T Get(String key)
	{
		if (key == null)
		{
			throw new NullPointerException("key");
		}

		Object service;
		tangible.OutObject<Object> tempOut_service = new tangible.OutObject<Object>();
		if (this.TryGetValue(key, tempOut_service))
		{
		service = tempOut_service.argValue;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (service is T result)
			if (service instanceof T result)
			{
				return result;
			}
		}
	else
	{
		service = tempOut_service.argValue;
	}

		// return null if either the key or type don't match
		return null;
	}

	/** 
	 Gets the default value by type from the turn's context.
	 
	 <typeparam name="T">The type of the service.</typeparam>
	 @return The service object; or null if no default service of the type is registered.
	 The default service key is the <see cref="Type.FullName"/> of the service type.
	*/
	public final <T> T Get()
	{
		return this.<T>Get(T.class.FullName);
	}

	/** 
	 Adds a value to the turn's context.
	 
	 <typeparam name="T">The type of the service.</typeparam>
	 @param key The name of the service.
	 @param value The value to add.
	 @exception ArgumentNullException <paramref name="key"/> or <paramref name="value"/>
	 is null.
	*/
	public final <T> void Add(String key, T value)
	{
		if (key == null)
		{
			throw new NullPointerException("key");
		}

		if (value == null)
		{
			throw new NullPointerException("value");
		}

		// note this can throw if teh key is already present
		super.put(key, value);
	}

	/** 
	 Adds a value to the turn's context.
	 
	 <typeparam name="T">The type of the service.</typeparam>
	 @param value The service object to add.
	 @exception ArgumentNullException <paramref name="value"/> is null.
	 The default service key is the <see cref="Type.FullName"/> of the service type.
	*/
	public final <T> void Add(T value)
	{
		this.put(T.class.FullName, value);
	}

	/** <inheritdoc/>
	*/
	public final void close() throws IOException
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		for (var entry : this.values())
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (entry is IDisposable disposableService)
			if (entry instanceof Closeable disposableService)
			{
				// Don't dispose the ConnectorClient, since this is cached in the adapter (singleton).
				// Disposing will release the HttpClient causing Response Sends to fail.
				if (entry instanceof IConnectorClient)
				{
					continue;
				}

				disposableService.Dispose();
			}
		}
	}
}