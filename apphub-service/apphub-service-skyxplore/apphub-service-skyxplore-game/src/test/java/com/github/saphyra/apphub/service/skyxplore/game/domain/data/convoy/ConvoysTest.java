package com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConvoysTest {
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID_1 = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID_2 = UUID.randomUUID();
    private static final UUID CONVOY_ID_1 = UUID.randomUUID();
    private static final UUID CONVOY_ID_2 = UUID.randomUUID();

    private final Convoys underTest = new Convoys();

    @Mock
    private Convoy convoy1;

    @Mock
    private Convoy convoy2;

    @Test
    void getByResourceDeliveryRequestId() {
        underTest.addAll(List.of(convoy1, convoy2));
        given(convoy1.getResourceDeliveryRequestId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID_1);
        given(convoy2.getResourceDeliveryRequestId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID_2);

        assertThat(underTest.getByResourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID_1)).containsExactly(convoy1);
    }

    @Test
    void findByIdValidated() {
        underTest.add(convoy1);
        given(convoy1.getConvoyId()).willReturn(CONVOY_ID_1);

        assertThat(underTest.findByIdValidated(CONVOY_ID_1)).isEqualTo(convoy1);
    }

    @Test
    void findByIdValidated_notFound() {
        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(CONVOY_ID_1), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void remove() {
        underTest.addAll(List.of(convoy1, convoy2));
        given(convoy1.getConvoyId()).willReturn(CONVOY_ID_1);
        given(convoy2.getConvoyId()).willReturn(CONVOY_ID_2);

        underTest.remove(CONVOY_ID_1);

        assertThat(underTest).containsExactly(convoy2);
    }
}