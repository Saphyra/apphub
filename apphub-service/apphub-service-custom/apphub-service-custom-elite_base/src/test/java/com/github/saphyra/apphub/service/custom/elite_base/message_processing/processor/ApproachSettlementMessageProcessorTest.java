package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.approach_settlement.ApproachSettlementMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ControllingFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.BodySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.MinorFactionSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.SettlementSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StationSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ApproachSettlementMessageProcessorTest {
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final Long STAR_ID = 34L;
    private static final String STAR_NAME = "star-name";
    private static final Double[] STAR_POSITION = new Double[]{12.32};
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long BODY_ID = 3243L;
    private static final String BODY_NAME = "body-name";
    private static final String FACTION_NAME = "faction-name";
    private static final String SERVICE = "service";
    private static final UUID INTERNAL_BODY_ID = UUID.randomUUID();
    private static final String SETTLEMENT_NAME = "settlement-name";
    private static final Long MARKET_ID = 3412L;
    private static final Double LONGITUDE = 34353.3246;
    private static final Double LATITUDE = 786787.87;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private StarSystemSaver starSystemSaver;

    @Mock
    private BodySaver bodySaver;

    @Mock
    private MinorFactionSaver minorFactionSaver;

    @Mock
    private StationSaver stationSaver;

    @Mock
    private SettlementSaver settlementSaver;

    @InjectMocks
    private ApproachSettlementMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Mock
    private StarSystem starSystem;

    @Mock
    private Body body;

    @Mock
    private Economy economy;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.APPROACH_SETTLEMENT);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage() {
        String[] services = new String[]{SERVICE};
        Economy[] economies = {economy};

        ApproachSettlementMessage approachSettlementMessage = ApproachSettlementMessage.builder()
            .timestamp(TIMESTAMP)
            .starId(STAR_ID)
            .starName(STAR_NAME)
            .starPosition(STAR_POSITION)
            .bodyId(BODY_ID)
            .bodyName(BODY_NAME)
            .controllingFaction(ControllingFaction.builder()
                .factionName(FACTION_NAME)
                .state(FactionStateEnum.BOOM.getValue())
                .build())
            .settlementName(SETTLEMENT_NAME)
            .marketId(MARKET_ID)
            .allegiance(Allegiance.ALLIANCE.getValue())
            .economy(EconomyEnum.SERVICE.getValue())
            .services(services)
            .economies(economies)
            .longitude(LONGITUDE)
            .latitude(LATITUDE)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, ApproachSettlementMessage.class)).willReturn(approachSettlementMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(bodySaver.save(TIMESTAMP, STAR_SYSTEM_ID, BodyType.PLANET, BODY_ID, BODY_NAME)).willReturn(body);
        given(body.getId()).willReturn(INTERNAL_BODY_ID);

        underTest.processMessage(edMessage);

        then(minorFactionSaver).should().save(TIMESTAMP, FACTION_NAME, FactionStateEnum.BOOM);
        then(stationSaver).should().save(TIMESTAMP, STAR_SYSTEM_ID, INTERNAL_BODY_ID, SETTLEMENT_NAME, MARKET_ID, Allegiance.ALLIANCE, EconomyEnum.SERVICE, services, economies);
        then(settlementSaver).should().save(TIMESTAMP, STAR_SYSTEM_ID, INTERNAL_BODY_ID, SETTLEMENT_NAME, MARKET_ID, LONGITUDE, LATITUDE);
    }
}