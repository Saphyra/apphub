package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
//TODO unit test
class FcMaterialWriteBuffer extends WriteBuffer<ItemDomainId, FcMaterial> {
    private final FcMaterialRepository fcMaterialRepository;
    private final FcMaterialConverter fcMaterialConverter;
    private final ErrorReporterService errorReporterService;

    protected FcMaterialWriteBuffer(DateTimeUtil dateTimeUtil, FcMaterialRepository fcMaterialRepository, FcMaterialConverter fcMaterialConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.fcMaterialRepository = fcMaterialRepository;
        this.fcMaterialConverter = fcMaterialConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected ItemDomainId getDomainId(FcMaterial commodity) {
        return ItemDomainId.builder()
            .itemName(commodity.getItemName())
            .externalReference(commodity.getExternalReference())
            .build();
    }

    @Override
    protected void doSynchronize(Collection<FcMaterial> bufferCopy) {
        bufferCopy.forEach(commodity -> {
            try {
                fcMaterialRepository.save(fcMaterialConverter.convertDomain(commodity));
            } catch (Exception e) {
                errorReporterService.report("Failed saving FcMaterial", e);
            }
        });
    }
}
