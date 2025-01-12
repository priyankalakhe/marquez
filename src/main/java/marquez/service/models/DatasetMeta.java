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

package marquez.service.models;

import com.google.common.base.Joiner;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import marquez.common.models.DatasetName;
import marquez.common.models.NamespaceName;
import marquez.common.models.SourceName;

@EqualsAndHashCode
@ToString
public abstract class DatasetMeta {
  static final Joiner VERSION_JOINER = Joiner.on(":");

  @Getter private final DatasetName physicalName;
  @Getter private final SourceName sourceName;
  @Nullable private final String description;
  @Nullable private final UUID runId;

  public DatasetMeta(
      @NonNull final DatasetName physicalName,
      @NonNull final SourceName sourceName,
      @Nullable final String description,
      @Nullable final UUID runId) {
    this.physicalName = physicalName;
    this.sourceName = sourceName;
    this.description = description;
    this.runId = runId;
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public Optional<UUID> getRunId() {
    return Optional.ofNullable(runId);
  }

  public abstract Optional<UUID> version(NamespaceName namespaceName, DatasetName name);
}
