package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
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
        List<FcMaterialEntity> entities = fcMaterialConverter.convertDomain(bufferCopy);

        try {
            fcMaterialRepository.saveAll(entities);
        } catch (Exception e) {
            errorReporterService.report("Failed saving FcMaterial batch. Trying one by one", e);
            entities.forEach(entity -> {
                try {
                    fcMaterialRepository.save(entity);
                } catch (Exception e2) {
                    errorReporterService.report("Failed saving FcMaterial", e2);
                }
            });
        }
    }
}
