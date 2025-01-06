package com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price;

import com.github.saphyra.apphub.lib.common_util.StaticCachedDao;
import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
//TODO unit test
public class CommodityAveragePriceDao extends StaticCachedDao<CommodityAveragePriceEntity, CommodityAveragePrice, String, CommodityAveragePriceRepository> {
    CommodityAveragePriceDao(Converter<CommodityAveragePriceEntity, CommodityAveragePrice> converter, CommodityAveragePriceRepository repository) {
        super(converter, repository, true);
    }

    @Override
    protected String idExtractor(CommodityAveragePrice commodityAveragePrice) {
        return commodityAveragePrice.getCommodityName();
    }

    @Override
    protected boolean shouldSave(CommodityAveragePrice commodityAveragePrice) {
        Optional<CommodityAveragePrice> maybeCached = findById(idExtractor(commodityAveragePrice));

        return maybeCached.isEmpty() || !Objects.equals(maybeCached.get().getAveragePrice(), commodityAveragePrice.getAveragePrice());
    }
}
