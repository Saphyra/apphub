package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UrlAssemblerTest {
    private static final String SERVICE_NAME = "service-name";
    private static final String URL = "url";

    @InjectMocks
    private UrlAssembler underTest;

    @Mock
    private EventProcessor eventProcessor;

    @Test
    public void assemble() {
        given(eventProcessor.getHost()).willReturn(SERVICE_NAME);
        given(eventProcessor.getUrl()).willReturn(URL);

        String result = underTest.assemble(eventProcessor);

        assertThat(result)
            .startsWith("http://")
            .contains(SERVICE_NAME)
            .endsWith(URL);
    }
}