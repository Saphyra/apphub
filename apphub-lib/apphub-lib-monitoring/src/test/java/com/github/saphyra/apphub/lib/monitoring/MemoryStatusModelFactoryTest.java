package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MemoryStatusModelFactoryTest {
    private static final String SERVICE_NAME = "service-name";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private MemoryStatusModelFactory underTest;

    @Test
    public void create() {
        given(dateTimeUtil.getCurrentTime()).willReturn(CURRENT_DATE);

        MemoryStatusModel result = underTest.create(SERVICE_NAME);

        assertThat(result.getService()).isEqualTo(SERVICE_NAME);
        assertThat(result.getEpochSeconds()).isEqualTo(CURRENT_DATE.toEpochSecond(ZoneOffset.UTC));
        assertThat(result.getAvailableMemoryBytes()).isNotNull();
        assertThat(result.getAllocatedMemoryBytes()).isLessThan(result.getAvailableMemoryBytes());
        assertThat(result.getUsedMemoryBytes()).isLessThan(result.getAllocatedMemoryBytes());
    }
}