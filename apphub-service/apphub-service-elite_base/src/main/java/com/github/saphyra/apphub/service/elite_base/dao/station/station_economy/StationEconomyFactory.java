package com.github.saphyra.apphub.service.elite_base.dao.station.station_economy;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StationEconomyFactory {
    private final IdGenerator idGenerator;

    public StationEconomy create(UUID stationId, Economy economy) {
        return StationEconomy.builder()
            .id(idGenerator.randomUuid())
            .stationId(stationId)
            .economy(EconomyEnum.parse(economy.getName()))
            .proportion(economy.getProportion())
            .build();
    }
}
