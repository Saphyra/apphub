package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
//TODO unit test
class EquipmentWriteBuffer extends WriteBuffer<ItemDomainId, Equipment> {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentConverter equipmentConverter;
    private final ErrorReporterService errorReporterService;

    protected EquipmentWriteBuffer(DateTimeUtil dateTimeUtil, EquipmentRepository equipmentRepository, EquipmentConverter equipmentConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.equipmentRepository = equipmentRepository;
        this.equipmentConverter = equipmentConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected ItemDomainId getDomainId(Equipment equipment) {
        return ItemDomainId.builder()
            .itemName(equipment.getItemName())
            .externalReference(equipment.getExternalReference())
            .build();
    }

    @Override
    protected void doSynchronize(Collection<Equipment> bufferCopy) {
        bufferCopy.forEach(commodity -> {
            try {
                equipmentRepository.save(equipmentConverter.convertDomain(commodity));
            } catch (Exception e) {
                errorReporterService.report("Failed saving Equipment", e);
            }
        });
    }
}
