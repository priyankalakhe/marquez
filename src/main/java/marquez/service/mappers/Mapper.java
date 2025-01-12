/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package marquez.service.mappers;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.NonNull;
import marquez.common.models.DatasetName;
import marquez.common.models.DatasetType;
import marquez.common.models.JobName;
import marquez.common.models.JobType;
import marquez.common.models.NamespaceName;
import marquez.common.models.OwnerName;
import marquez.common.models.SourceName;
import marquez.common.models.SourceType;
import marquez.common.models.Utils;
import marquez.db.models.DatasetRow;
import marquez.db.models.DatasetVersionRow;
import marquez.db.models.ExtendedDatasetRow;
import marquez.db.models.ExtendedRunRow;
import marquez.db.models.JobRow;
import marquez.db.models.JobVersionRow;
import marquez.db.models.NamespaceOwnershipRow;
import marquez.db.models.NamespaceRow;
import marquez.db.models.OwnerRow;
import marquez.db.models.RunArgsRow;
import marquez.db.models.RunRow;
import marquez.db.models.RunStateRow;
import marquez.db.models.SourceRow;
import marquez.db.models.StreamVersionRow;
import marquez.service.models.Dataset;
import marquez.service.models.DatasetMeta;
import marquez.service.models.DbTable;
import marquez.service.models.DbTableMeta;
import marquez.service.models.Job;
import marquez.service.models.JobMeta;
import marquez.service.models.Namespace;
import marquez.service.models.NamespaceMeta;
import marquez.service.models.Run;
import marquez.service.models.RunMeta;
import marquez.service.models.Source;
import marquez.service.models.SourceMeta;
import marquez.service.models.Stream;
import marquez.service.models.StreamMeta;

public final class Mapper {
  private Mapper() {}

  public static Namespace toNamespace(@NonNull final NamespaceRow row) {
    return new Namespace(
        NamespaceName.of(row.getName()),
        row.getCreatedAt(),
        row.getUpdatedAt(),
        OwnerName.of(row.getCurrentOwnerName()),
        row.getDescription().orElse(null));
  }

  public static List<Namespace> toNamespace(@NonNull final List<NamespaceRow> rows) {
    return rows.stream().map(row -> toNamespace(row)).collect(toImmutableList());
  }

  public static NamespaceRow toNamespaceRow(
      @NonNull final NamespaceName name, @NonNull final NamespaceMeta meta) {
    final Instant now = Instant.now();
    return new NamespaceRow(
        UUID.randomUUID(),
        now,
        now,
        name.getValue(),
        meta.getDescription().orElse(null),
        meta.getOwnerName().getValue());
  }

  public static OwnerRow toOwnerRow(@NonNull final OwnerName name) {
    return new OwnerRow(UUID.randomUUID(), Instant.now(), name.getValue());
  }

  public static NamespaceOwnershipRow toNamespaceOwnershipRow(
      @NonNull final UUID namespaceRowUuid, @NonNull final UUID ownerRowUuid) {
    return new NamespaceOwnershipRow(
        UUID.randomUUID(), Instant.now(), null, namespaceRowUuid, ownerRowUuid);
  }

  public static Source toSource(@NonNull final SourceRow row) {
    return new Source(
        SourceType.valueOf(row.getType()),
        SourceName.of(row.getName()),
        row.getCreatedAt(),
        row.getUpdatedAt(),
        URI.create(row.getConnectionUrl()),
        row.getDescription().orElse(null));
  }

  public static List<Source> toSource(@NonNull final List<SourceRow> rows) {
    return rows.stream().map(row -> toSource(row)).collect(toImmutableList());
  }

  public static SourceRow toSourceRow(
      @NonNull final SourceName name, @NonNull final SourceMeta meta) {
    final Instant now = Instant.now();
    return new SourceRow(
        UUID.randomUUID(),
        meta.getType().toString(),
        now,
        now,
        name.getValue(),
        meta.getConnectionUrl().toASCIIString(),
        meta.getDescription().orElse(null));
  }

  public static Dataset toDataset(
      @NonNull final ExtendedDatasetRow extendedRow, @Nullable final DatasetVersionRow versionRow) {
    final DatasetName name = DatasetName.of(extendedRow.getName());
    final DatasetName physicalName = DatasetName.of(extendedRow.getPhysicalName());
    final Instant createdAt = extendedRow.getCreatedAt();
    final Instant updatedAt = extendedRow.getUpdatedAt();
    final SourceName sourceName = SourceName.of(extendedRow.getSourceName());
    final String description = extendedRow.getDescription().orElse(null);

    final DatasetType type = DatasetType.valueOf(extendedRow.getType());
    switch (type) {
      case STREAM:
        final URL schemaLocation = Utils.toUrl(((StreamVersionRow) versionRow).getSchemaLocation());
        return new Stream(
            name, physicalName, createdAt, updatedAt, sourceName, schemaLocation, description);
      default:
        return new DbTable(name, physicalName, createdAt, updatedAt, sourceName, description);
    }
  }

  public static DatasetRow toDatasetRow(
      @NonNull final UUID namespaceRowUuid,
      @NonNull final UUID sourceRowUuid,
      @NonNull final DatasetName name,
      @NonNull final DatasetMeta meta) {
    final Instant now = Instant.now();
    return new DatasetRow(
        UUID.randomUUID(),
        toDatasetType(meta).toString(),
        now,
        now,
        namespaceRowUuid,
        sourceRowUuid,
        name.getValue(),
        meta.getPhysicalName().getValue(),
        meta.getDescription().orElse(null),
        null);
  }

  private static DatasetType toDatasetType(final DatasetMeta meta) {
    if (meta instanceof DbTableMeta) {
      return DatasetType.DB_TABLE;
    } else if (meta instanceof StreamMeta) {
      return DatasetType.STREAM;
    }

    throw new IllegalArgumentException();
  }

  public static DatasetVersionRow toDatasetVersionRow(
      @NonNull final UUID datasetUuid,
      @NonNull final UUID version,
      @NonNull final DatasetMeta meta) {
    final UUID rowUuid = UUID.randomUUID();
    final Instant now = Instant.now();
    final UUID runUuid = meta.getRunId().orElse(null);

    if (meta instanceof DbTableMeta) {
      return null;
    } else if (meta instanceof StreamMeta) {
      final String schemaLocationString = ((StreamMeta) meta).getSchemaLocation().toString();
      return new StreamVersionRow(
          rowUuid, now, datasetUuid, version, runUuid, schemaLocationString);
    }

    throw new IllegalArgumentException();
  }

  public static Job toJob(
      @NonNull final JobRow row,
      @NonNull final List<DatasetName> inputs,
      @NonNull final List<DatasetName> outputs,
      @NonNull final String locationString) {
    return new Job(
        JobType.valueOf(row.getType()),
        JobName.of(row.getName()),
        row.getCreatedAt(),
        row.getUpdatedAt(),
        inputs,
        outputs,
        Utils.toUrl(locationString),
        row.getDescription().orElse(null));
  }

  public static JobRow toJobRow(
      @NonNull final UUID namespaceUuid, @NonNull final JobName name, @NonNull final JobMeta meta) {
    final Instant now = Instant.now();
    return new JobRow(
        UUID.randomUUID(),
        meta.getType().toString(),
        now,
        now,
        namespaceUuid,
        name.getValue(),
        meta.getDescription().orElse(null),
        null);
  }

  public static JobVersionRow toJobVersionRow(
      @NonNull final UUID jobRowUuid,
      @NonNull final List<UUID> inputs,
      @NonNull final List<UUID> outputs,
      @NonNull final URL location,
      @NonNull final UUID version) {
    final Instant now = Instant.now();
    return new JobVersionRow(
        UUID.randomUUID(),
        now,
        now,
        jobRowUuid,
        inputs,
        outputs,
        location.toString(),
        version,
        null);
  }

  public static Run toRun(@NonNull final ExtendedRunRow extendedRow) {
    return new Run(
        extendedRow.getUuid(),
        extendedRow.getCreatedAt(),
        extendedRow.getUpdatedAt(),
        extendedRow.getNominalStartTime().orElse(null),
        extendedRow.getNominalEndTime().orElse(null),
        Run.State.valueOf(extendedRow.getCurrentRunState().get()),
        Utils.fromJson(extendedRow.getArgs(), new TypeReference<Map<String, String>>() {}));
  }

  public static List<Run> toRun(@NonNull final List<ExtendedRunRow> extendedRows) {
    return extendedRows.stream().map(extendedRow -> toRun(extendedRow)).collect(toImmutableList());
  }

  public static RunRow toRunRow(
      @NonNull final UUID jobVersionUuid,
      @NonNull final UUID runArgsUuid,
      @NonNull final RunMeta runMeta) {
    final Instant now = Instant.now();
    return new RunRow(
        UUID.randomUUID(),
        now,
        now,
        jobVersionUuid,
        runArgsUuid,
        runMeta.getNominalStartTime().orElse(null),
        runMeta.getNominalEndTime().orElse(null),
        null);
  }

  public static RunArgsRow toRunArgsRow(@NonNull final Map<String, String> args, String checksum) {
    return new RunArgsRow(UUID.randomUUID(), Instant.now(), Utils.toJson(args), checksum);
  }

  public static RunStateRow toRunStateRow(
      @NonNull final UUID runId, @NonNull final Run.State runState) {
    return new RunStateRow(UUID.randomUUID(), Instant.now(), runId, runState.toString());
  }
}
