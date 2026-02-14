package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
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
        List<SpaceshipEntity> entities = spaceshipConverter.convertDomain(bufferCopy);

        try {
            spaceshipRepository.saveAll(entities);
        } catch (Exception e) {
            errorReporterService.report("Failed saving Spaceship batch. Trying one by one", e);
            entities.forEach(entity -> {
                try {
                    spaceshipRepository.save(entity);
                } catch (Exception e2) {
                    errorReporterService.report("Failed saving Spaceship", e2);
                }
            });
        }
    }
}
