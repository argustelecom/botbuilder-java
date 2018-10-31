// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;

import java.util.*;
import java.io.*;
import java.util.Map;



/** 
 Values persisted for the lifetime of the turn as part of the <see cref="ITurnContext"/>.
 
 
 TODO: add more details on what kind of values can/should be stored here, by whom and what the lifetime semantics are, etc.
 
*/



/**
 * Represents a set of collection of services associated with the {@link TurnContext}.
 *
 * TODO: add more details on what kind of services can/should be stored here, by whom and what the lifetime semantics are, etc.
 *
 */
public interface TurnContextStateCollection extends Iterable<Map.Entry<String, Object>>, AutoCloseable {

    /**
     * Add a service with a specified key.
     * @param TService The type of service to be added.
     * @param key The key to store the service under.
     * @param service The service to add.
     * @throws IllegalArgumentException Thrown when a service is already registered with the specified {@code key}
     */
    <TService extends Object> void Add(String key, TService service) throws IllegalArgumentException;
	/**
	 * Add a service with a specified key.
	 * @param TService The type of service to be added.
	 * @param key The key to store the service under.
	 * @param service The service to add.
	 * @throws IllegalArgumentException Thrown when a service is already registered with the specified {@code key}
	 */
	<TService extends Object> void Add(String key, TService service, boolean force) throws IllegalArgumentException;

	/**
	 * Get a service by its key.
	 * @param TService The type of service to be retrieved.
	 * @param key The key of the service to get.
	 * @return The service stored under the specified key.
	 */
	<TService extends Object> TService Get(String key) throws IllegalArgumentException;

}

