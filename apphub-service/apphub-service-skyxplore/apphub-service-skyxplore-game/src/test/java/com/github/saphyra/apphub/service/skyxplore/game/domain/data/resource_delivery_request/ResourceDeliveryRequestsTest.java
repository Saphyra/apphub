package com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ResourceDeliveryRequestsTest {
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID_1 = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID_2 = UUID.randomUUID();

    private final ResourceDeliveryRequests underTest = new ResourceDeliveryRequests();

    @Test
    void findByIdValidated() {
        ResourceDeliveryRequest request = ResourceDeliveryRequest.builder()
            .resourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID)
            .build();
        underTest.add(request);

        assertThat(underTest.findByIdValidated(RESOURCE_DELIVERY_REQUEST_ID)).isEqualTo(request);
    }

    @Test
    void findByIdValidated_notFound() {
        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(RESOURCE_DELIVERY_REQUEST_ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByReservedStorageId() {
        ResourceDeliveryRequest request1 = ResourceDeliveryRequest.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_1)
            .build();
        underTest.add(request1);
        ResourceDeliveryRequest request2 = ResourceDeliveryRequest.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_2)
            .build();
        underTest.add(request2);

        assertThat(underTest.getByReservedStorageId(RESERVED_STORAGE_ID_1)).containsExactly(request1);
    }
}