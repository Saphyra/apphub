package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class PowerplayStateOfferFilter implements OfferFilter {
    @Override
    public List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request) {
        return offers.stream()
            .filter(offerDetail -> matches(offerDetail, request))
            .toList();
    }

    private boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        if (isNull(request.getPowerplayState())) {
            //User does not filter for powerplayState
            return true;
        }

        boolean result = offerDetail.getPowerplayState()
            .filter(powerplayState -> powerplayState.equals(PowerplayState.valueOf(request.getPowerplayState())))
            .isPresent();
        if (!result && log.isDebugEnabled()) {
            log.debug("Filtering offer from system with incorrect powerplayState {}", offerDetail);
        }
        return result;
    }
}
