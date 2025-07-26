package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class CommodityWriteBuffer extends WriteBuffer<CommodityDomainId, Commodity> {
    private final CommodityRepository commodityRepository;
    private final CommodityConverter commodityConverter;

    CommodityWriteBuffer(DateTimeUtil dateTimeUtil, CommodityRepository commodityRepository, CommodityConverter commodityConverter) {
        super(dateTimeUtil);
        this.commodityRepository = commodityRepository;
        this.commodityConverter = commodityConverter;
    }

    @Override
    protected void doSynchronize(Collection<Commodity> bufferCopy) {
        commodityRepository.saveAll(commodityConverter.convertDomain(bufferCopy));
    }

    @Override
    protected CommodityDomainId getDomainId(Commodity commodity) {
        return CommodityDomainId.builder()
            .commodityName(commodity.getCommodityName())
            .externalReference(commodity.getExternalReference())
            .build();
    }
}
