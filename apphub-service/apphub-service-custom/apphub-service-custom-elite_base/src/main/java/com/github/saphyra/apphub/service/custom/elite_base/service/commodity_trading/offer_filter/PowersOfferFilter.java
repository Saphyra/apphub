package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.Relation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import com.github.saphyra.apphub.service.custom.elite_base.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class PowersOfferFilter implements OfferFilter {
    private final StarSystemPowerMappingDao starSystemPowerMappingDao;

    @Override
    public List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request) {
        List<UUID> starSystemIds = offers.stream()
            .map(offerDetail -> offerDetail.getStarSystem().getId())
            .toList();

        Map<UUID, List<Power>> powerMapping = Utils.measuredOperation(
                () -> starSystemPowerMappingDao.getByStarSystemIds(starSystemIds),
                "Queried StarSystemPowerMappings for %s star systems in {}ms".formatted(offers.size())
            )
            .stream()
            .collect(Collectors.groupingBy(StarSystemPowerMapping::getStarSystemId))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(StarSystemPowerMapping::getPower).toList()));


        return offers.stream()
            .filter(offerDetail -> matches(offerDetail, request.getPowersRelation(), request.getPowers(), powerMapping))
            .toList();
    }

    private boolean matches(OfferDetail offerDetail, Relation powerRelation, List<String> requestPowers, Map<UUID, List<Power>> powerMapping) {
        boolean result = powerRelation
            .apply(
                requestPowers,
                () -> Optional.ofNullable(powerMapping.get(offerDetail.getStarSystem().getId()))
                    .map(powers -> powers.stream().map(Enum::name).toList())
                    .orElse(Collections.emptyList())
            );
        if (!result && log.isDebugEnabled()) {
            log.debug("Filtered offer with incorrect controllingFaction: {}", offerDetail);
        }
        return result;
    }

    @Override
    public int getOrder() {
        return OfferFilterOrder.POWER_FILTER_ORDER.getOrder();
    }
}
