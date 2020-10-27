package org.kestra.task.gcp.bigquery.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class MaterializedViewDefinition {
    @Schema(title = "Date when this materialized view was last modified")
    private final Instant lastRefreshDate;

    @Schema(title = "the query whose result is persisted")
    public final String query;

    @Schema(title = "is enable automatic refresh of the materialized view when the base table is updated")
    private final Boolean enableRefresh;

    @Schema(title = "the maximum frequency at which this materialized view will be refreshed")
    private final Long refreshIntervalMs;

    public static MaterializedViewDefinition of(com.google.cloud.bigquery.MaterializedViewDefinition materializedViewDefinition) {
        return MaterializedViewDefinition.builder()
            .lastRefreshDate(materializedViewDefinition.getLastRefreshTime() == null ? null : Instant.ofEpochMilli(materializedViewDefinition.getLastRefreshTime()))
            .query(materializedViewDefinition.getQuery())
            .enableRefresh(materializedViewDefinition.getEnableRefresh())
            .refreshIntervalMs(materializedViewDefinition.getRefreshIntervalMs())
            .build();
    }
}