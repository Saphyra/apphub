package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
class CommodityWriteBuffer extends WriteBuffer<ItemDomainId, Commodity> {
    private final CommodityRepository commodityRepository;
    private final CommodityConverter commodityConverter;
    private final ErrorReporterService errorReporterService;

    protected CommodityWriteBuffer(DateTimeUtil dateTimeUtil, CommodityRepository commodityRepository, CommodityConverter commodityConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.commodityRepository = commodityRepository;
        this.commodityConverter = commodityConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected ItemDomainId getDomainId(Commodity commodity) {
        return ItemDomainId.builder()
            .itemName(commodity.getItemName())
            .externalReference(commodity.getExternalReference())
            .build();
    }

    @Override
    protected void doSynchronize(Collection<Commodity> bufferCopy) {
        List<CommodityEntity> entities = commodityConverter.convertDomain(bufferCopy);

        try {
            commodityRepository.saveAll(entities);
        } catch (Exception e) {
            errorReporterService.report("Failed saving Commodity batch. Trying one by one", e);
            entities.forEach(entity -> {
                try {
                    commodityRepository.save(entity);
                } catch (Exception e2) {
                    errorReporterService.report("Failed saving Commodity", e2);
                }
            });
        }
    }
}
