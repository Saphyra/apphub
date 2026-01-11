package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.CommodityFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material.FcMaterialDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material.FcMaterialFactory;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.TradeMode;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class TradingDaoSupport {
    private final Map<ItemType, TradeableDao> daos;
    private final CommodityFactory commodityFactory;
    private final FcMaterialFactory fcMaterialFactory;
    private final ExecutorServiceBean executorServiceBean;

    public TradingDaoSupport(CommodityDao commodityDao, FcMaterialDao fcMaterialDao, CommodityFactory commodityFactory, FcMaterialFactory fcMaterialFactory, ExecutorServiceBean executorServiceBean) {
        this.commodityFactory = commodityFactory;
        this.fcMaterialFactory = fcMaterialFactory;
        this.executorServiceBean = executorServiceBean;
        this.daos = Map.of(
            ItemType.COMMODITY, commodityDao,
            ItemType.FC_MATERIAL, fcMaterialDao
        );
    }

    public List<Tradeable> getByMarketId(ItemType type, Long marketId) {
        return cast(getDao(type).getByMarketId(marketId));
    }

    private TradeableDao getDao(ItemType type) {
        return Optional.ofNullable(daos.get(type))
            .orElseThrow(() -> createTypeNotSupportedException(type));
    }

    private static IllegalArgumentException createTypeNotSupportedException(ItemType type) {
        return new IllegalArgumentException(type + " is not a Tradeable item type.");
    }

    @SuppressWarnings("unchecked")
    private List<Tradeable> cast(List<? extends Tradeable> list) {
        return (List<Tradeable>) list;
    }

    public void deleteAll(ItemType type, List<Tradeable> tradeables) {
        getDao(type).deleteAllTradeables(tradeables);
    }

    public Tradeable create(ItemType type, ItemLocationType locationType, UUID externalReference, Long marketId, String name, Integer buyPrice, Integer sellPrice, Integer demand, Integer stock) {
        return switch (type) {
            case COMMODITY -> commodityFactory.create(locationType, externalReference, marketId, name, buyPrice, sellPrice, demand, stock);
            case FC_MATERIAL -> fcMaterialFactory.create(locationType, externalReference, marketId, name, buyPrice, sellPrice, demand, stock);
            default -> throw createTypeNotSupportedException(type);
        };
    }

    public void saveAll(ItemType type, List<Tradeable> modifiedCommodities) {
        getDao(type)
            .saveAll(modifiedCommodities);
    }

    public List<Tradeable> getOffers(TradeMode tradeMode, String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice) {
        return executorServiceBean.processCollectionWithWait(
                daos.values(),
                dao -> switch (tradeMode) {
                    case BUY -> cast(dao.getSuppliers(itemName, minTradeAmount, minPrice, maxPrice));
                    case SELL -> cast(dao.getConsumers(itemName, minTradeAmount, minPrice, maxPrice));
                }
            )
            .stream()
            .flatMap(Collection::stream)
            .toList();
    }
}
