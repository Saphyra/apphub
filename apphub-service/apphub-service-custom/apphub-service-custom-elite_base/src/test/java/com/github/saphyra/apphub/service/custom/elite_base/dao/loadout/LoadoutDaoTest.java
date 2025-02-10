package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.Loadout;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutRepository;
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
class LoadoutDaoTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 34L;
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";

    @Mock
    private LoadoutConverter converter;

    @Mock
    private LoadoutRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private LoadoutDao underTest;

    @Mock
    private Loadout domain;

    @Mock
    private LoadoutEntity entity;

    @Test
    void getByExternalReferenceOrMarketId() {
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(repository.getByExternalReferenceOrMarketId(EXTERNAL_REFERENCE_STRING, MARKET_ID)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByExternalReferenceOrMarketId(EXTERNAL_REFERENCE, MARKET_ID)).containsExactly(domain);
    }
}