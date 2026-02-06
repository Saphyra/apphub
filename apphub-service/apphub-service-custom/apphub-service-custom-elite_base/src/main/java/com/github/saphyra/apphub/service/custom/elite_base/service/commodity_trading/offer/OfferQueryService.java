package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao.OfferDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter.OfferFilter;
import com.github.saphyra.apphub.service.custom.elite_base.util.Utils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
//TODO unit test
public class OfferQueryService {
    private final Map<OrderCommoditiesBy, OfferDao> offerDaos;
    private final List<OfferFilter> offerFilters;
    private final StarSystemDao starSystemDao;
    private final OfferDetailFactory offerDetailFactory;
    private final EliteBaseProperties eliteBaseProperties;

    OfferQueryService(List<OfferDao> offerDaos, List<OfferFilter> offerFilters, StarSystemDao starSystemDao, OfferDetailFactory offerDetailFactory, EliteBaseProperties eliteBaseProperties) {
        this.offerDaos = offerDaos.stream()
            .collect(Collectors.toMap(OfferDao::getOrderBy, offerDao -> offerDao));
        this.offerFilters = offerFilters;
        this.starSystemDao = starSystemDao;
        this.offerDetailFactory = offerDetailFactory;
        this.eliteBaseProperties = eliteBaseProperties;
    }

    public BiWrapper<Integer, List<OfferDetail>> getOffers(CommodityTradingRequest request) {
        StarSystem referenceSystem = Utils.measuredOperation(
            () -> starSystemDao.findByIdValidated(request.getReferenceStarId()),
            "Reference StarSystem query took {} ms"
        );

        List<OfferDetail> result = new ArrayList<>();

        List<OfferDetail> lastQuery;
        int offset = request.getOffset();
        do {
            lastQuery = query(offset, request, referenceSystem);
            offset += eliteBaseProperties.getSearchPageSize();
            List<OfferDetail> filtered = filter(lastQuery, request);
            result.addAll(filtered);
        } while (result.size() < eliteBaseProperties.getSearchPageSize() && !lastQuery.isEmpty());

        return new BiWrapper<>(offset, result);
    }

    private List<OfferDetail> filter(List<OfferDetail> lastQuery, CommodityTradingRequest request) {
        for (OfferFilter filter : offerFilters) {
            lastQuery = filter.filter(lastQuery, request);
        }

        return lastQuery;
    }

    private List<OfferDetail> query(int offset, CommodityTradingRequest request, StarSystem referenceSystem) {
        List<Offer> offers = Utils.measuredOperation(
            () -> offerDaos.get(request.getOrderBy()).getOffers(offset, request, referenceSystem),
            "Querying offers took {} ms"
        );

        return Utils.measuredOperation(
            () -> offerDetailFactory.create(offers),
            "OfferDetail assembly took {} ms for %s offers".formatted(offers.size())
        );
    }

    @PostConstruct
    void validate() {
        ValidationUtil.equals(offerDaos.size(), OrderCommoditiesBy.values().length, "Not all OrderCommoditiesBy have an OfferDao implementation.");
    }
}
