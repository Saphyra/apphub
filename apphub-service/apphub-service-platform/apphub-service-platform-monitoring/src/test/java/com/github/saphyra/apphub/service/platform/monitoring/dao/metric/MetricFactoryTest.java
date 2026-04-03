package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MetricFactoryTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String FUNCTIONALITY = "functionality";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private MetricFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(METRIC_ID);

        assertThat(underTest.create(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY))
            .returns(METRIC_ID, Metric::getMetricId)
            .returns(Feature.ELITE_BASE_MESSAGE_PROCESSING, Metric::getFeature)
            .returns(FUNCTIONALITY, Metric::getFunctionality);
    }
}