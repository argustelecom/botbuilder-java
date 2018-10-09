package com.microsoft.bot.builder;

import java.util.*;
import java.util.concurrent.CompletableFuture;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Defines the interface for a storage layer.
*/
public interface IStorage
{
	/** 
	 Reads storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to read.

	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 the items read, indexed by key.
	 {@link DeleteAsync(string[] )}
	 {@link WriteAsync(IDictionary{string, object} )}
	*/

	CompletableFuture<Map<String, Object>> ReadAsync(String[] keys);

	/** 
	 Writes storage items to storage.
	 
	 @param changes The items to write, indexed by key.

	 @return A task that represents the work queued to execute.
	 {@link DeleteAsync(string[] )}
	 {@link ReadAsync(string[] )}
	*/

	CompletableFuture WriteAsync(java.util.Map<String, Object> changes);

	/** 
	 Deletes storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to delete.

	 @return A task that represents the work queued to execute.
	 {@link ReadAsync(string[] )}
	 {@link WriteAsync(IDictionary{string, object} )}
	*/

	CompletableFuture DeleteAsync(String[] keys);
}