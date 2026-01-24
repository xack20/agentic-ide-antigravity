package com.ecommerce.shared.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document for tracking processed events per projection (idempotency).
 */
@Document(collection = "processed_events")
@CompoundIndex(name = "event_lookup", def = "{'eventId': 1, 'projectionName': 1}", unique = true)
public class ProcessedEventDocument {

    @Id
    private String id;

    private String eventId;
    private String projectionName;
    private String eventType;
    private Instant processedAt;

    public ProcessedEventDocument() {
    }

    public ProcessedEventDocument(String eventId, String projectionName, String eventType) {
        this.id = projectionName + ":" + eventId;
        this.eventId = eventId;
        this.projectionName = projectionName;
        this.eventType = eventType;
        this.processedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getProjectionName() {
        return projectionName;
    }

    public void setProjectionName(String projectionName) {
        this.projectionName = projectionName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
