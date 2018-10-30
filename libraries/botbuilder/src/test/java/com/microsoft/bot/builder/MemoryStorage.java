package com.microsoft.bot.builder;

import java.util.concurrent.ExecutorService;

/**
 * RamStorage stores data in volative dictionary
 */
public class MemoryStorage extends DictionaryStorage {

    public MemoryStorage(ExecutorService executorService) {
            super(null, executorService);
    }
}
