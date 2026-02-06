package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading;

import java.util.List;

public interface TradeableDao {
    List<? extends  Tradeable> getByMarketId(Long marketId);

    void deleteAllTradeables(List<Tradeable> tradeables);

    void saveAll(List<Tradeable> tradeables);
}
