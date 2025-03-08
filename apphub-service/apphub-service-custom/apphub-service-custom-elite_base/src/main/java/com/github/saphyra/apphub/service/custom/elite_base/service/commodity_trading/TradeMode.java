package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public enum TradeMode {
    BUY(
        (commodityDao, request) -> commodityDao.findSuppliers(request.getCommodity(), request.getMinTradeAmount(), request.getMinPrice(), request.getMaxPrice()),
        Commodity::getStock,
        Commodity::getSellPrice
    ),
    SELL(
        (commodityDao, request) -> commodityDao.findConsumers(request.getCommodity(), request.getMinTradeAmount(), request.getMinPrice(), request.getMaxPrice()),
        Commodity::getDemand,
        Commodity::getBuyPrice
    ),
    ;

    @Getter
    private final BiFunction<CommodityDao, CommodityTradingRequest, List<Commodity>> offerProvider;
    @Getter
    private final Function<Commodity, Integer> tradeAmountExtractor;
    @Getter
    private final Function<Commodity, Integer> priceExtractor;
}
