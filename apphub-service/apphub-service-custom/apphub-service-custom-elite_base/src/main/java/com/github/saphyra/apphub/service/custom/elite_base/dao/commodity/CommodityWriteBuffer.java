package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class CommodityWriteBuffer extends WriteBuffer<CommodityDomainId, Commodity> {
    private final CommodityRepository commodityRepository;
    private final CommodityConverter commodityConverter;
    private final ErrorReporterService errorReporterService;

    CommodityWriteBuffer(DateTimeUtil dateTimeUtil, CommodityRepository commodityRepository, CommodityConverter commodityConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.commodityRepository = commodityRepository;
        this.commodityConverter = commodityConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected void doSynchronize(Collection<Commodity> bufferCopy) {
        bufferCopy.forEach(commodity -> {
            try {
                commodityRepository.save(commodityConverter.convertDomain(commodity));
            } catch (Exception e) {
                errorReporterService.report("Failed saving commodity", e);
            }
        });
    }

    @Override
    protected CommodityDomainId getDomainId(Commodity commodity) {
        return CommodityDomainId.builder()
            .commodityName(commodity.getCommodityName())
            .externalReference(commodity.getExternalReference())
            .build();
    }
}
