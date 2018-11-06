// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.schema;



import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.schema.models.Entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EntityImpl extends Entity {
    private ObjectMapper _objectMapper = new ObjectMapper();
    private Map<String, String> _typeMapString;

    /**
     * Initializes a new instance of the Entity class.
     */
    public EntityImpl() {
        CustomInit();
    }


    /**
     * Initializes a new instance of the Entity class.
     * @param type Entity Type (typically from schema.org
     * types)
     */
    public EntityImpl(String type) {
        withType(type);
        CustomInit();
    }

    /**
     * An initialization method that performs custom operations like setting defaults
     */
    void CustomInit() {
        _typeMapString = Stream.of(new String[][] {
                { "com.microsoft.bot.schema.models.Mention", "mention" },
                { "com.microsoft.bot.schema.models.Place", "Place" },
                { "com.microsoft.bot.schema.models.GeoCoordinates", "GeoCoordinates" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        String className = this.getClass().getTypeName();
        if (_typeMapString.containsKey(className)) {
            withType(_typeMapString.get(className));
        }
    }
    /**
     * Gets or sets entity Type (typically from schema.org types)
     * 11/5/2018: Why is this here?
     */
    //public String type;


    /**
     * @return
     */
    private HashMap<String, JsonNode> properties = new HashMap<String, JsonNode>();

    @JsonAnyGetter
    public Map<String, JsonNode> properties() {

        return this.properties;

    }


    @JsonAnySetter
    public void setProperties(String key, JsonNode value) {
        this.properties.put(key, value);
    }


    /**
     */

    /**
     * Retrieve internal payload.
     */

    /**
     */

    /**
     * @param T 
     */

    /**
     * @return 
     */

    public <T> T GetAs(Class<T> type)  {

        // Serialize
        String tempJson;
        try {
            tempJson = _objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        // Deserialize
        T newObj = null;
        try {
            newObj = (T) _objectMapper.readValue(tempJson, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return newObj;


    }


    /**
     * Set internal payload.
     * @param T 
     * @param obj 
     */

    public <T> boolean SetAs(T obj) {
        // Serialize
        String tempJson;
        try {
            tempJson = _objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }

        EntityImpl tempEntity;
        try {
            tempEntity = _objectMapper.readValue(tempJson, EntityImpl.class);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (Map.Entry<String, JsonNode> entry : tempEntity.properties.entrySet()) {
            this.properties.put(entry.getKey(), entry.getValue());
        }
        withType(obj.getClass().getTypeName());

        return true;

    }

};

