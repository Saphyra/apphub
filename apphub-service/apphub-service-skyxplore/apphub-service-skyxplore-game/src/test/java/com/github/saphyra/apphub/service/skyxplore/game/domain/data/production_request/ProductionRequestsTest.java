package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductionRequestsTest {
    private static final UUID RESERVED_STORAGE_ID_1 = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID_2 = UUID.randomUUID();
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();

    private final ProductionRequests underTest = new ProductionRequests();

    @Test
    void getByReservedStorageId() {
        ProductionRequest productionRequest1 = ProductionRequest.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_1)
            .build();
        underTest.add(productionRequest1);
        ProductionRequest productionRequest2 = ProductionRequest.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_2)
            .build();
        underTest.add(productionRequest2);

        assertThat(underTest.getByReservedStorageId(RESERVED_STORAGE_ID_1)).containsExactly(productionRequest1);
    }

    @Test
    void findByIdValidated() {
        ProductionRequest productionRequest = ProductionRequest.builder()
            .productionRequestId(PRODUCTION_REQUEST_ID)
            .build();
        underTest.add(productionRequest);

        assertThat(underTest.findByIdValidated(PRODUCTION_REQUEST_ID)).isEqualTo(productionRequest);
    }

    @Test
    void findByIdValidated_notFound() {
        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(PRODUCTION_REQUEST_ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}