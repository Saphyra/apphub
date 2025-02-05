package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_economy;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationEconomyFactory {
    public StationEconomy create(UUID stationId, Economy economy) {
        return StationEconomy.builder()
            .stationId(stationId)
            .economy(EconomyEnum.parse(economy.getName()))
            .proportion(economy.getProportion())
            .build();
    }
}
