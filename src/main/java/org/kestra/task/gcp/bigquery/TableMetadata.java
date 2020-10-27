package org.kestra.task.gcp.bigquery;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.kestra.core.models.tasks.RunnableTask;
import org.kestra.core.runners.RunContext;
import org.slf4j.Logger;

import java.util.Objects;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(
    title = "Get table metadata"
)
public class TableMetadata extends AbstractTable implements RunnableTask<AbstractTable.Output> {
    @Builder.Default
    @Schema(
        title = "Policy to apply if a table don't exists.",
        description = "If the policy is `SKIP`, the output will contain only null value, otherwize an error is raised."
    )
    private final IfNotExists ifNotExists = IfNotExists.ERROR;

    @Override
    public Output run(RunContext runContext) throws Exception {
        BigQuery connection = this.connection(runContext);
        Logger logger = runContext.logger();

        TableId tableId = this.projectId != null  ?
            TableId.of(runContext.render(this.projectId), runContext.render(this.dataset), runContext.render(this.table)) :
            TableId.of(runContext.render(this.dataset), runContext.render(this.table));

        logger.debug("Getting table metadata '{}'", tableId);

        Table table = connection.getTable(tableId);

        if (table == null) {
            if (ifNotExists == IfNotExists.ERROR) {
                throw new IllegalArgumentException("Unable to find table '" + tableId.getProject() + ":" + tableId.getDataset() + "." + tableId.getTable() + "'");
            } else if (ifNotExists == IfNotExists.SKIP) {
                return Output.builder()
                    .build();
            }
        }

        return Output.of(Objects.requireNonNull(table));
    }

    public enum IfNotExists {
        ERROR,
        SKIP
    }
}