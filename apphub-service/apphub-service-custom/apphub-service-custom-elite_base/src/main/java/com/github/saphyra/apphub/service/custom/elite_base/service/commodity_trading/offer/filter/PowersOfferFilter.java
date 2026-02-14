package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.Relation;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class PowersOfferFilter implements OfferFilter {
    @Override
    public List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request) {
        if (request.getPowersRelation() == Relation.ANY) {
            return offers;
        }

        return offers.stream()
            .filter(offerDetail -> matches(offerDetail, request.getPowersRelation(), request.getPowers()))
            .toList();
    }

    private boolean matches(OfferDetail offerDetail, Relation powerRelation, List<String> requestPowers) {
        boolean result = powerRelation
            .apply(
                requestPowers,
                () -> offerDetail.getPowers()
                    .map(powers -> powers.stream().map(Enum::name).toList())
                    .orElse(Collections.emptyList())
            );
        if (!result && log.isDebugEnabled()) {
            log.debug("Filtered offer with incorrect controllingFaction: {}", offerDetail);
        }
        return result;
    }
}
