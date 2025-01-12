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

package marquez.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class Columns {
  private Columns() {}

  /* COMMON ROW COLUMNS */
  public static final String ROW_UUID = "uuid";
  public static final String TYPE = "type";
  public static final String CREATED_AT = "created_at";
  public static final String UPDATED_AT = "updated_at";
  public static final String NAME = "name";
  public static final String VERSION = "version";
  public static final String DESCRIPTION = "description";
  public static final String NAMESPACE_UUID = "namespace_uuid";
  public static final String DATASET_UUID = "dataset_uuid";
  public static final String DATASET_VERSION_UUID = "dataset_version_uuid";
  public static final String JOB_VERSION_UUID = "job_version_uuid";
  public static final String CURRENT_VERSION_UUID = "current_version_uuid";

  /* NAMESPACE ROW COLUMNS */
  public static final String CURRENT_OWNER_NAME = "current_owner_name";

  /* NAMESPACE OWNERSHIP ROW COLUMNS */
  public static final String STARTED_AT = "started_at";
  public static final String ENDED_AT = "ended_at";
  public static final String OWNER_UUID = "owner_uuid";

  /* SOURCE ROW COLUMNS */
  public static final String CONNECTION_URL = "connection_url";

  /* DATASET ROW COLUMNS */
  public static final String SOURCE_UUID = "source_uuid";
  public static final String SOURCE_NAME = "source_name";
  public static final String PHYSICAL_NAME = "physical_name";

  /* STREAM VERSION ROW COLUMNS */
  public static final String INPUTS = "inputs";
  public static final String OUTPUTS = "outputs";
  public static final String SCHEMA_LOCATION = "schema_location";
  public static final String IO_TYPE = "io_type";

  /* JOB VERSION ROW COLUMNS */
  public static final String JOB_UUID = "job_uuid";
  public static final String LOCATION = "location";
  public static final String LATEST_RUN_UUID = "latest_run_uuid";

  /* RUN ROW COLUMNS */
  public static final String RUN_ARGS_UUID = "run_args_uuid";
  public static final String NOMINAL_START_TIME = "nominal_start_time";
  public static final String NOMINAL_END_TIME = "nominal_end_time";
  public static final String CURRENT_RUN_STATE = "current_run_state";

  /* RUN ARGS ROW COLUMNS */
  public static final String ARGS = "args";
  public static final String CHECKSUM = "checksum";

  /* RUN STATE ROW COLUMNS */
  public static final String TRANSITIONED_AT = "transitioned_at";
  public static final String RUN_UUID = "run_uuid";
  public static final String STATE = "state";

  public static UUID uuidOrNull(final ResultSet results, final String column) throws SQLException {
    if (results.getObject(column) == null) {
      return null;
    }
    return results.getObject(column, UUID.class);
  }

  public static UUID uuidOrThrow(final ResultSet results, final String column) throws SQLException {
    if (results.getObject(column) == null) {
      throw new IllegalArgumentException();
    }
    return results.getObject(column, UUID.class);
  }

  public static Instant timestampOrNull(final ResultSet results, final String column)
      throws SQLException {
    if (results.getObject(column) == null) {
      return null;
    }
    return results.getTimestamp(column).toInstant();
  }

  public static Instant timestampOrThrow(final ResultSet results, final String column)
      throws SQLException {
    if (results.getObject(column) == null) {
      throw new IllegalArgumentException();
    }
    return results.getTimestamp(column).toInstant();
  }

  public static String stringOrNull(final ResultSet results, final String column)
      throws SQLException {
    if (results.getObject(column) == null) {
      return null;
    }
    return results.getString(column);
  }

  public static String stringOrThrow(final ResultSet results, final String column)
      throws SQLException {
    if (results.getObject(column) == null) {
      throw new IllegalArgumentException();
    }
    return results.getString(column);
  }

  public static List<UUID> uuidArrayOrThrow(final ResultSet results, final String column)
      throws SQLException {
    if (results.getObject(column) == null) {
      throw new IllegalArgumentException();
    }
    return Arrays.asList((UUID[]) results.getArray(column).getArray());
  }

  public static List<String> stringArrayOrThrow(final ResultSet results, final String column)
      throws SQLException {
    if (results.getObject(column) == null) {
      throw new IllegalArgumentException();
    }
    return Arrays.asList((String[]) results.getArray(column).getArray());
  }
}
