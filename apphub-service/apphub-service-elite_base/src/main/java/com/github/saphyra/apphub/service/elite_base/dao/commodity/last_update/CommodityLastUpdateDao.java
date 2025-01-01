package com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update;

import com.github.saphyra.apphub.lib.common_util.StaticCachedDao;
import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommodityLastUpdateDao extends StaticCachedDao<CommodityLastUpdateEntity, CommodityLastUpdate, String, CommodityLastUpdateRepository> {
    private final UuidConverter uuidConverter;

    CommodityLastUpdateDao(Converter<CommodityLastUpdateEntity, CommodityLastUpdate> converter, CommodityLastUpdateRepository repository, UuidConverter uuidConverter) {
        super(converter, repository, false);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected String idExtractor(CommodityLastUpdate commodityLastUpdate) {
        return uuidConverter.convertDomain(commodityLastUpdate.getExternalReference());
    }

    @Override
    protected boolean shouldSave(CommodityLastUpdate commodityLastUpdate) {
        Optional<CommodityLastUpdate> maybeLastUpdate = findById(idExtractor(commodityLastUpdate));

        return maybeLastUpdate.isEmpty() || !maybeLastUpdate.get().getLastUpdate().equals(commodityLastUpdate.getLastUpdate());
    }
}
