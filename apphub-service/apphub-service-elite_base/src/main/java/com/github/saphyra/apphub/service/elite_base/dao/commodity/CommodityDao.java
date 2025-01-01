package com.github.saphyra.apphub.service.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class CommodityDao extends AbstractDao<CommodityEntity, Commodity, String, CommodityRepository> {
    private final UuidConverter uuidConverter;

    CommodityDao(CommodityConverter converter, CommodityRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<Commodity> findByCommodityNameAndExternalReference(String name, UUID externalReference) {
        return converter.convertEntity(repository.findByCommodityNameAndExternalReference(name, uuidConverter.convertDomain(externalReference)));
    }

    public Optional<Commodity> findByCommodityNameAndMarketId(String name, Long marketId) {
        return converter.convertEntity(repository.findByCommodityNameAndMarketId(name, marketId));
    }

    public List<Commodity> getByExternalReferenceOrMarketId(UUID externalReference, Long marketId) {
        return converter.convertEntity(repository.getByExternalReferenceOrMarketId(uuidConverter.convertDomain(externalReference), marketId));
    }
}
