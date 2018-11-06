package com.microsoft.bot.schema;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.bot.schema.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 Entity schema validation tests to ensure that serilization and deserialization work as expected.
 */
public class EntitySchemaTest
{
    protected boolean _setUpIsDone = false;
    protected ObjectMapper _mapper;
    protected ExecutorService _executorService;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @BeforeEach
    public void setUp() {
        if (_setUpIsDone) {
            return;
        }
        _mapper = new ObjectMapper();
        _mapper.registerModule(new JavaTimeModule());
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
            @Override
            public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                jsonGenerator.writeString(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(offsetDateTime));
            }
        });
        _mapper.registerModule(simpleModule);
        _executorService = Executors.newFixedThreadPool(10);
        _setUpIsDone = true;
    }

    /**
     Ensures that <see cref="GeoCoordinates"/> class can be serialized and deserialized properly.
     */
    @Test
    public final void EntityTests_GeoCoordinatesSerializationDeserializationTest() throws IOException {
        GeoCoordinates geoCoordinates = new GeoCoordinates();
        geoCoordinates.withLatitude(22.0);
        geoCoordinates.withElevation(23.0);

        assertEquals("GeoCoordinates", geoCoordinates.type());
        String serialized = _mapper.writeValueAsString(geoCoordinates);

        EntityImpl deserializedEntity = _mapper.readValue(serialized, EntityImpl.class);
        assertEquals(deserializedEntity.type(), geoCoordinates.type());
        GeoCoordinates geo = deserializedEntity.GetAs(GeoCoordinates.class);
        assertEquals(geo.type(),  geoCoordinates.type());
    }

    /**
     Ensures that <see cref="Mention"/> class can be serialized and deserialized properly.
     */
    @Test
    public final void EntityTests_MentionSerializationDeserializationTest() throws IOException {
        Mention mentionEntity = new Mention();
        mentionEntity.withText("TESTTEST");

        assert "mention" == mentionEntity.type();
        String serialized = _mapper.writeValueAsString(mentionEntity);

        EntityImpl deserializedEntity = _mapper.readValue(serialized, EntityImpl.class);
        assertEquals(deserializedEntity.type(), mentionEntity.type());
        Mention mentionDeserialized = deserializedEntity.<Mention>GetAs(Mention.class);
        assertEquals(mentionDeserialized.type(), mentionEntity.type());
    }

    /**
     Ensures that <see cref="Place"/> class can be serialized and deserialized properly.
     */
    @Test
    public final void EntityTests_PlaceSerializationDeserializationTest() throws IOException {
        Place placeEntity = new Place();
        placeEntity.withName("TESTTEST");

        assertEquals("Place", placeEntity.type());
        String serialized = _mapper.writeValueAsString(placeEntity);

        EntityImpl deserializedEntity = _mapper.readValue(serialized, EntityImpl.class);
        assertEquals(deserializedEntity.type(), placeEntity.type());
        Place placeDeserialized = deserializedEntity.<Place>GetAs(Place.class);
        assertEquals (placeDeserialized.type(),placeEntity.type());
    }

    /**
     Ensures that activity can be deserialized with schema object.
     https://github.com/Microsoft/botbuilder-java/issues/57
     */
    @Test
    public final void EntityTests_ActivityReadValue() throws IOException {
        String activityString = "{\n" +
                "  \"type\": \"message\",\n" +
                "  \"id\": null,\n" +
                "  \"timestamp\": \"2018-11-05T23:43:55.337Z\",\n" +
                "  \"localTimestamp\": null,\n" +
                "  \"serviceUrl\": null,\n" +
                "  \"channelId\": null,\n" +
                "  \"from\": {\n" +
                "    \"id\": \"Java-BotBuilder-Function@KLSKBveC4Gw\",\n" +
                "    \"name\": \"Java-BotBuilder-Function\",\n" +
                "    \"role\": null\n" +
                "  },\n" +
                "  \"conversation\": {\n" +
                "    \"group\": null,\n" +
                "    \"isGroup\": null,\n" +
                "    \"conversationType\": null,\n" +
                "    \"id\": \"eecdbc948df1409eb3d85fd52a2e0b0e\",\n" +
                "    \"name\": null,\n" +
                "    \"role\": null\n" +
                "  },\n" +
                "  \"recipient\": {\n" +
                "    \"id\": \"GqmZkaUErhj\",\n" +
                "    \"name\": \"You\",\n" +
                "    \"role\": null\n" +
                "  },\n" +
                "  \"textFormat\": null,\n" +
                "  \"attachmentLayout\": null,\n" +
                "  \"membersAdded\": null,\n" +
                "  \"membersRemoved\": null,\n" +
                "  \"reactionsAdded\": null,\n" +
                "  \"reactionsRemoved\": null,\n" +
                "  \"topicName\": null,\n" +
                "  \"historyDisclosed\": null,\n" +
                "  \"locale\": null,\n" +
                "  \"text\": \"Echo test\",\n" +
                "  \"speak\": null,\n" +
                "  \"inputHint\": null,\n" +
                "  \"summary\": null,\n" +
                "  \"suggestedActions\": null,\n" +
                "  \"attachments\": null,\n" +
                "  \"entities\": null,\n" +
                "  \"channelData\": null,\n" +
                "  \"action\": null,\n" +
                "  \"replyToId\": \"eecdbc948df1409eb3d85fd52a2e0b0e|0000000\",\n" +
                "  \"label\": null,\n" +
                "  \"valueType\": null,\n" +
                "  \"value\": null,\n" +
                "  \"name\": null,\n" +
                "  \"relatesTo\": null,\n" +
                "  \"code\": null,\n" +
                "  \"expiration\": null,\n" +
                "  \"importance\": null,\n" +
                "  \"deliveryMode\": null,\n" +
                "  \"textHighlights\": null\n" +
                "}";

        Activity deserializedEntity = _mapper.readValue(activityString, Activity.class);
        assertEquals(deserializedEntity.type(), "message");
    }

}
