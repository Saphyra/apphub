package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MinorFactionDaoTest {
    private static final String FACTION_NAME = "faction-name";

    @Mock
    private MinorFactionConverter converter;

    @Mock
    private MinorFactionRepository repository;

    @InjectMocks
    private MinorFactionDao underTest;

    @Mock
    private MinorFaction domain;

    @Mock
    private MinorFactionEntity entity;

    @Test
    void findByFactionName() {
        given(repository.findByFactionName(FACTION_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByFactionName(FACTION_NAME)).contains(domain);
    }
}