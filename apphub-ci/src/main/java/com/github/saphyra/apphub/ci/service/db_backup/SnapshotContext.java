package com.github.saphyra.apphub.ci.service.db_backup;

import java.sql.Connection;

public record SnapshotContext(Connection connection, String snapshotId) {
}
