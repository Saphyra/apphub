package com.github.saphyra.apphub.api.platform.event_gateway.model.request;


import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SendEventRequestTest {
    private SendEventRequest<Object> underTest;

    @Test
    public void isBlockingRequest_flagNotSet() {
        underTest = SendEventRequest.builder()
            .metadata(new HashMap<>())
            .build();

        Boolean result = underTest.isBlockingRequest();

        assertThat(result).isFalse();
    }

    @Test
    public void isBlockingRequest_metadataNull() {
        underTest = SendEventRequest.builder()
            .metadata(null)
            .build();

        Boolean result = underTest.isBlockingRequest();

        assertThat(result).isFalse();
    }

    @Test
    public void isBlockingRequest_flagNull() {
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put(Constants.SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST, null);
        underTest = SendEventRequest.builder()
            .metadata(metadata)
            .build();

        Boolean result = underTest.isBlockingRequest();

        assertThat(result).isFalse();
    }

    @Test
    public void isBlockingRequest_invalidFlag() {
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put(Constants.SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST, "asd");
        underTest = SendEventRequest.builder()
            .metadata(metadata)
            .build();

        Boolean result = underTest.isBlockingRequest();

        assertThat(result).isFalse();
    }

    @Test
    public void isBlockingRequest_flagFalse() {
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put(Constants.SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST, String.valueOf(false));
        underTest = SendEventRequest.builder()
            .metadata(metadata)
            .build();

        Boolean result = underTest.isBlockingRequest();

        assertThat(result).isFalse();
    }

    @Test
    public void isBlockingRequest_flagTrue() {
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put(Constants.SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST, String.valueOf(true));
        underTest = SendEventRequest.builder()
            .metadata(metadata)
            .build();

        Boolean result = underTest.isBlockingRequest();

        assertThat(result).isTrue();
    }

    @Test
    public void blockingRequest_false() {
        underTest = SendEventRequest.builder().build();

        underTest.blockingRequest(false);

        assertThat(underTest.isBlockingRequest()).isFalse();
    }

    @Test
    public void blockingRequest_true() {
        underTest = SendEventRequest.builder().build();

        underTest.blockingRequest(true);

        assertThat(underTest.isBlockingRequest()).isTrue();
    }
}