package com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update;

import com.github.saphyra.apphub.lib.common_util.StaticCachedDao;
import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommodityLastUpdateDao extends StaticCachedDao<CommodityLastUpdateEntity, CommodityLastUpdate, CommodityLastUpdateId, CommodityLastUpdateRepository> {
    private final UuidConverter uuidConverter;

    CommodityLastUpdateDao(Converter<CommodityLastUpdateEntity, CommodityLastUpdate> converter, CommodityLastUpdateRepository repository, UuidConverter uuidConverter) {
        super(converter, repository, false);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected CommodityLastUpdateId idExtractor(CommodityLastUpdate commodityLastUpdate) {
        return CommodityLastUpdateId.builder()
            .externalReference(uuidConverter.convertDomain(commodityLastUpdate.getExternalReference()))
            .commodityType(commodityLastUpdate.getCommodityType())
            .build();
    }

    @Override
    protected boolean shouldSave(CommodityLastUpdate commodityLastUpdate) {
        Optional<CommodityLastUpdate> maybeLastUpdate = findById(idExtractor(commodityLastUpdate));

        return maybeLastUpdate.isEmpty() || !maybeLastUpdate.get().getLastUpdate().equals(commodityLastUpdate.getLastUpdate());
    }
}
