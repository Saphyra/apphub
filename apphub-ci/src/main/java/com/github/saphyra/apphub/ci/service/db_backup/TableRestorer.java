package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicator;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Component
@Slf4j
class TableRestorer {
    /**
     * Truncates all tables in a single statement, before any import starts.
     * Truncating during imports would block on the locks held by concurrent imports of related (e.g. partitioned) tables.
     */
    void truncate(String dbUrl, String username, String password, List<String> tables) {
        if (tables.isEmpty()) {
            return;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();

        try (
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            Statement statement = conn.createStatement()
        ) {
            log.info("Truncating {} tables", tables.size());
            statement.execute("TRUNCATE TABLE " + String.join(", ", tables) + " CASCADE");
            log.info("Truncated {} tables in {} seconds", tables.size(), stopwatch.elapsed(TimeUnit.SECONDS));
        } catch (Exception ex) {
            throw new RuntimeException("Failed truncating tables " + tables, ex);
        }
    }

    /**
     * @param stream the raw (gzipped) backup stream. Closing it is the caller's responsibility.
     */
    void restore(ProgressIndicator progressIndicator, String dbUrl, String username, String password, String table, InputStream stream) {
        progressIndicator.started(table);
        Stopwatch stopwatch = Stopwatch.createStarted();

        try (
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            GZIPInputStream gzip = new GZIPInputStream(stream)
        ) {
            conn.setAutoCommit(false);

            PGConnection pg = conn.unwrap(PGConnection.class);
            CopyManager copy = pg.getCopyAPI();

            copy.copyIn("COPY " + table + " FROM STDIN WITH (FORMAT BINARY)", gzip);

            conn.commit();
            progressIndicator.completed(table);
        } catch (Exception ex) {
            progressIndicator.failed(table);
            throw new RuntimeException("Failed restoring " + table, ex);
        }

        stopwatch.stop();
        log.info("{} restored in {} seconds.", table, stopwatch.elapsed(TimeUnit.SECONDS));
    }
}
