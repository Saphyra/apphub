package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.DefaultColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_COMMODITY_NAME;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_COMMODITY;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class CommodityNameCache {
    private final Set<String> cache = new CopyOnWriteArraySet<>();
    private final CountDownLatch latch = new CountDownLatch(1);

    private final JdbcTemplate jdbcTemplate;
    private final EliteBaseProperties eliteBaseProperties;
    private final SleepService sleepService;
    private final ErrorReporterService errorReporterService;
    private final ExecutorServiceBean executorServiceBean;

    @SneakyThrows
    public Collection<String> getCommodityNames() {
        if (!latch.await(eliteBaseProperties.getCache().getCacheReadTimeout().toSeconds(), TimeUnit.SECONDS)) {
            throw ExceptionFactory.loggedException(HttpStatus.REQUEST_TIMEOUT, ErrorCode.TEMPORARILY_NOT_AVAILABLE, "CommodityNameCache is not loaded yet.");
        }

        return cache;
    }

    @SneakyThrows
    public void add(String commodityName) {
        if (latch.await(eliteBaseProperties.getCache().getCacheReadTimeout().toSeconds(), TimeUnit.SECONDS)) {
            cache.add(commodityName);
        } else {
            log.warn("CommodityName {} is not added to commodityNameCache, because cache is not loaded yet.", commodityName);
        }
    }

    @SneakyThrows
    public void addAll(List<String> commodityNames) {
        if (latch.await(eliteBaseProperties.getCache().getCacheReadTimeout().toSeconds(), TimeUnit.SECONDS)) {
            cache.addAll(commodityNames);
        } else {
            log.warn("CommodityNames {} are not added to commodityNameCache, because cache is not loaded yet.", commodityNames);
        }
    }

    @EventListener
    public void init(ApplicationReadyEvent event) {
        executorServiceBean.execute(this::load);
    }

    private void load() {
        log.info("CommodityNameCache loading started...");
        StopWatch stopWatch = StopWatch.createStarted();
        int tryCount = 0;

        try {
            String sql = SqlBuilder.select()
                .column(new DefaultColumn(COLUMN_COMMODITY_NAME))
                .from(new QualifiedTable(SCHEMA, TABLE_COMMODITY))
                .groupBy(new DefaultColumn(COLUMN_COMMODITY_NAME))
                .build();
            log.debug(sql);

            List<String> result = jdbcTemplate.query(sql, rs -> {
                List<String> r = new ArrayList<>();

                while (rs.next()) {
                    r.add(rs.getString(COLUMN_COMMODITY_NAME));
                }

                return r;
            });

            cache.addAll(result);
            latch.countDown();
        } catch (Exception e) {
            tryCount++;
            errorReporterService.report("Failed loading CommodityNameCache. TryCount: " + tryCount, e);
            sleepService.sleep(eliteBaseProperties.getCache().getLoadRetryDelay().toMillis());
        }

        stopWatch.stop();
        log.info("{} items were loaded to CommodityNameCache in {} ms.", cache.size(), stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}
