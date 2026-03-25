package com.github.saphyra.apphub.service.platform.monitoring.service.migration;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//TODO unit test
interface MetricMigrationDataProvider {
    MetricDataType getType();

    LocalDateTime getExpirationTime();

    LocalDateTime step(LocalDateTime reference);

    MetricDataType getResultType();


    @Component
    @RequiredArgsConstructor
    class SecondMetricMigrationDataProvider implements MetricMigrationDataProvider {
        private final DateTimeUtil dateTimeUtil;
        private final MonitoringProperties monitoringProperties;

        @Override
        public MetricDataType getType() {
            return MetricDataType.SECOND;
        }

        @Override
        public LocalDateTime getExpirationTime() {
            return dateTimeUtil.getCurrentDateTime()
                .withNano(0)
                .minus(monitoringProperties.getMigration().get(getType()).getExpirationDuration());
        }

        @Override
        public LocalDateTime step(LocalDateTime reference) {
            return reference.minus(monitoringProperties.getMigration().get(getType()).getStepDuration());
        }

        @Override
        public MetricDataType getResultType() {
            return MetricDataType.MINUTE;
        }
    }

    @Component
    @RequiredArgsConstructor
    class MinuteMetricMigrationDataProvider implements MetricMigrationDataProvider {
        private final DateTimeUtil dateTimeUtil;
        private final MonitoringProperties monitoringProperties;

        @Override
        public MetricDataType getType() {
            return MetricDataType.MINUTE;
        }

        @Override
        public LocalDateTime getExpirationTime() {
            return dateTimeUtil.getCurrentDateTime()
                .withNano(0)
                .withSecond(0)
                .minus(monitoringProperties.getMigration().get(getType()).getExpirationDuration());
        }

        @Override
        public LocalDateTime step(LocalDateTime reference) {
            return reference.minus(monitoringProperties.getMigration().get(getType()).getStepDuration());
        }

        @Override
        public MetricDataType getResultType() {
            return MetricDataType.HOUR;
        }
    }
}
