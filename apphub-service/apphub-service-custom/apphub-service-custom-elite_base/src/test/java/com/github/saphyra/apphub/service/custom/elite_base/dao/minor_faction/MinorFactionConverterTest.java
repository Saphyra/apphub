package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionStateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionStateSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.StateStatus;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MinorFactionConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String FACTION_NAME = "faction-name";
    private static final Double INFLUENCE = 234.34;
    private static final String ID_STRING = "id";
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @Mock
    private MinorFactionStateDao minorFactionStateDao;

    @Mock
    private MinorFactionStateSyncService minorFactionStateSyncService;

    @InjectMocks
    private MinorFactionConverter underTest;

    @Mock
    private MinorFactionState activeState;

    @Mock
    private MinorFactionState pendingState;

    @Mock
    private MinorFactionState recoveringState;

    @Test
    void convertDomain() {
        MinorFaction domain = MinorFaction.builder()
            .id(ID)
            .lastUpdate(LAST_UPDATE)
            .factionName(FACTION_NAME)
            .state(FactionStateEnum.BLIGHT)
            .influence(INFLUENCE)
            .allegiance(Allegiance.ALLIANCE)
            .activeStates(LazyLoadedField.loaded(List.of(activeState)))
            .pendingStates(LazyLoadedField.loaded(List.of(pendingState)))
            .recoveringStates(LazyLoadedField.loaded(List.of(recoveringState)))
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, MinorFactionEntity::getId)
            .returns(LAST_UPDATE_STRING, MinorFactionEntity::getLastUpdate)
            .returns(FACTION_NAME, MinorFactionEntity::getFactionName)
            .returns(FactionStateEnum.BLIGHT, MinorFactionEntity::getState)
            .returns(INFLUENCE, MinorFactionEntity::getInfluence)
            .returns(Allegiance.ALLIANCE, MinorFactionEntity::getAllegiance);

        then(minorFactionStateSyncService).should().sync(ID, List.of(activeState), List.of(pendingState), List.of(recoveringState));
    }

    @Test
    void convertEntity() {
        MinorFactionEntity domain = MinorFactionEntity.builder()
            .id(ID_STRING)
            .lastUpdate(LAST_UPDATE_STRING)
            .factionName(FACTION_NAME)
            .state(FactionStateEnum.BLIGHT)
            .influence(INFLUENCE)
            .allegiance(Allegiance.ALLIANCE)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);
        given(minorFactionStateDao.getByMinorFactionIdAndStatus(ID, StateStatus.ACTIVE)).willReturn(List.of(activeState));
        given(minorFactionStateDao.getByMinorFactionIdAndStatus(ID, StateStatus.PENDING)).willReturn(List.of(pendingState));
        given(minorFactionStateDao.getByMinorFactionIdAndStatus(ID, StateStatus.RECOVERING)).willReturn(List.of(recoveringState));

        assertThat(underTest.convertEntity(domain))
            .returns(ID, MinorFaction::getId)
            .returns(LAST_UPDATE, MinorFaction::getLastUpdate)
            .returns(FACTION_NAME, MinorFaction::getFactionName)
            .returns(FactionStateEnum.BLIGHT, MinorFaction::getState)
            .returns(INFLUENCE, MinorFaction::getInfluence)
            .returns(Allegiance.ALLIANCE, MinorFaction::getAllegiance)
            .returns(List.of(activeState), MinorFaction::getActiveStates)
            .returns(List.of(pendingState), MinorFaction::getPendingStates)
            .returns(List.of(recoveringState), MinorFaction::getRecoveringStates);
    }
}