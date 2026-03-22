package com.syncride.userservice.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

/**
 * Custom serializer for GeoJsonPoint to produce the exact same JSON as Node.js/Mongoose:
 * { "type": "Point", "coordinates": [longitude, latitude] }
 *
 * Without this, Spring's default Jackson serialization adds extra "x" and "y" fields
 * which breaks the UI that expects the Node.js format.
 */
public class GeoJsonPointSerializer extends JsonSerializer<GeoJsonPoint> {

    @Override
    public void serialize(GeoJsonPoint value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        gen.writeStartObject();
        gen.writeStringField("type", "Point");
        gen.writeArrayFieldStart("coordinates");
        gen.writeNumber(value.getX()); // longitude
        gen.writeNumber(value.getY()); // latitude
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
