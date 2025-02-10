package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CommodityDao extends AbstractDao<CommodityEntity, Commodity, CommodityEntityId, CommodityRepository> {
    private final UuidConverter uuidConverter;

    CommodityDao(CommodityConverter converter, CommodityRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Commodity> getByExternalReferenceOrMarketId(UUID externalReference, Long marketId) {
        return converter.convertEntity(repository.getByIdExternalReferenceOrMarketId(uuidConverter.convertDomain(externalReference), marketId));
    }
}
