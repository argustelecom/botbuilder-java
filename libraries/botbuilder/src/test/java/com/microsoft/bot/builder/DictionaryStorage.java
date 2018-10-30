package com.microsoft.bot.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.bot.schema.models.Entity;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Models IStorage around a dictionary
 */
public class DictionaryStorage implements Storage {
    private static ObjectMapper objectMapper;

    // TODO: Object needs to be defined
    private Map<String, Object> memory;
    private final Object syncroot = new Object();
    private int _eTag = 0;
    private final String typeNameForNonEntity = "__type_name_";
    private ExecutorService _executorService;

    public DictionaryStorage(ExecutorService executorService) {
        this(null, executorService);
    }
    public DictionaryStorage(Map<String, Object> dictionary, ExecutorService executorService) {
        _executorService = executorService;
        DictionaryStorage.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enableDefaultTyping()
                .findAndRegisterModules();
        this.memory = (dictionary != null) ? dictionary : new ConcurrentHashMap<String, Object>();
    }

    public ExecutorService executorService() { return _executorService; }

    public CompletableFuture DeleteAsync(String[] keys) {
        synchronized (this.syncroot) {
                for (String key : keys)  {
                        Object o = this.memory.get(key);
                        this.memory.remove(o);
                }
        }
        return completedFuture(null);
    }

    @Override
    public CompletableFuture<Map<String, ?>> ReadAsync(String[] keys) throws JsonProcessingException {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> storeItems = new HashMap<String, Object>(keys.length);
            synchronized (this.syncroot) {
                for (String key : keys) {
                    if (this.memory.containsKey(key)) {
                        Object state = this.memory.get(key);
                        if (state != null) {
                            try {
                                if (!(state instanceof JsonNode))
                                    throw new RuntimeException("DictionaryRead failed: entry not JsonNode");
                                JsonNode stateNode = (JsonNode) state;
                                // Check if type info is set for the class
                                if (!(stateNode.hasNonNull(this.typeNameForNonEntity))) {
                                    throw new RuntimeException(String.format("DictionaryRead failed: Type info not present"));
                                }
                                String clsName = stateNode.get(this.typeNameForNonEntity).textValue();

                                // Load the class info
                                Class<?> cls;
                                try {
                                    cls = Class.forName(clsName);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(String.format("DictionaryRead failed: Could not load class %s", clsName));
                                }

                                // Populate dictionary
                                storeItems.put(key,DictionaryStorage.objectMapper.treeToValue(stateNode, cls ));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                                throw new RuntimeException(String.format("DictionaryRead failed: %s", e.toString()));
                            }
                        }
                    }

                }
            }

            return storeItems;
        }, executorService());
    }

    @Override
    public CompletableFuture WriteAsync(Map<String, ?> changes) throws Exception {
        synchronized (this.syncroot) {
            for (Map.Entry change : changes.entrySet()) {
                Object newValue = change.getValue();

                String oldStateETag = null; // default(string);
                if (this.memory.containsValue(change.getKey())) {
                    Map oldState = (Map) this.memory.get(change.getKey());
                    if (oldState.containsValue("eTag")) {
                        Map.Entry eTagToken = (Map.Entry) oldState.get("eTag");
                        oldStateETag = (String) eTagToken.getValue();
                    }

                }
                // Dictionary stores Key:JsonNode (with type information held within the JsonNode)
                JsonNode newState = DictionaryStorage.objectMapper.valueToTree(newValue);
                ((ObjectNode)newState).put(this.typeNameForNonEntity, newValue.getClass().getTypeName());

                // Set ETag if applicable
                if (newValue instanceof StoreItem) {
                    StoreItem newStoreItem = (StoreItem) newValue;
                    if(oldStateETag != null && newStoreItem.getETag() != "*" &&
                        newStoreItem.getETag() != oldStateETag) {
                        throw new Exception(String.format("Etag conflict.\r\n\r\nOriginal: %s\r\nCurrent: %s",
                                newStoreItem.getETag(), oldStateETag));
                    }
                    Integer newTag = _eTag++;
                    ((ObjectNode)newState).put("eTag", newTag.toString());
                }

                this.memory.put((String)change.getKey(), newState);
            }
        }
        return completedFuture(null);
    }

}

