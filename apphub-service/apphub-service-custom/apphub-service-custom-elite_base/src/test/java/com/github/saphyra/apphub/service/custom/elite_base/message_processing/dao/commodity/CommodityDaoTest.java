package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommodityDaoTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 34L;
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CommodityConverter converter;

    @Mock
    private CommodityRepository repository;

    @InjectMocks
    private CommodityDao underTest;

    @Mock
    private Commodity domain;

    @Mock
    private CommodityEntity entity;

    @Test
    void getByExternalReferenceOrMarketId() {
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(repository.getByIdExternalReferenceOrMarketId(EXTERNAL_REFERENCE_STRING, MARKET_ID)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByExternalReferenceOrMarketId(EXTERNAL_REFERENCE, MARKET_ID)).containsExactly(domain);
    }
}