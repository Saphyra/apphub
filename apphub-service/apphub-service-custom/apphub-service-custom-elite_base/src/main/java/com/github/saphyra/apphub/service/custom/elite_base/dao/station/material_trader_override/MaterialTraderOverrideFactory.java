package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.material_trader.CreateMaterialTraderOverrideRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialTraderOverrideFactory {
    public MaterialTraderOverride create(CreateMaterialTraderOverrideRequest request, boolean verified) {
        return MaterialTraderOverride.builder()
            .stationId(request.getStationId())
            .materialType(request.getMaterialType())
            .verified(verified)
            .build();
    }
}
