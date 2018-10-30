// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder;



import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Storage
{
    /**
     * Read StoreItems from storage
     * @param keys keys of the storeItems to read
     * @return StoreItem dictionary
     */
    CompletableFuture<Map<String, ? extends Object>> ReadAsync(String... keys) throws JsonProcessingException;

    /**
     * Write StoreItems to storage
     * @param changes
     */
    CompletableFuture WriteAsync(Map<String, ? extends Object> changes) throws Exception;

    /**
     * Delete StoreItems from storage
     * @param keys keys of the storeItems to delete
     */
    CompletableFuture DeleteAsync(String... keys);
}


