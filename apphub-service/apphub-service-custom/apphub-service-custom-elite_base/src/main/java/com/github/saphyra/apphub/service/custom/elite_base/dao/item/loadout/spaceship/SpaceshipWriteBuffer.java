package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
//TODO unit test
class SpaceshipWriteBuffer extends WriteBuffer<ItemDomainId, Spaceship> {
    private final SpaceshipRepository spaceshipRepository;
    private final SpaceshipConverter spaceshipConverter;
    private final ErrorReporterService errorReporterService;

    protected SpaceshipWriteBuffer(DateTimeUtil dateTimeUtil, SpaceshipRepository spaceshipRepository, SpaceshipConverter spaceshipConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.spaceshipRepository = spaceshipRepository;
        this.spaceshipConverter = spaceshipConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected ItemDomainId getDomainId(Spaceship spaceship) {
        return ItemDomainId.builder()
            .itemName(spaceship.getItemName())
            .externalReference(spaceship.getExternalReference())
            .build();
    }

    @Override
    protected void doSynchronize(Collection<Spaceship> bufferCopy) {
        bufferCopy.forEach(commodity -> {
            try {
                spaceshipRepository.save(spaceshipConverter.convertDomain(commodity));
            } catch (Exception e) {
                errorReporterService.report("Failed saving Spaceship", e);
            }
        });
    }
}
