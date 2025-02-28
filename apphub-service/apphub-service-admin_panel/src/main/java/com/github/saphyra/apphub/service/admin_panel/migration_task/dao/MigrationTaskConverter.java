package com.github.saphyra.apphub.service.admin_panel.migration_task.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MigrationTaskConverter extends ConverterBase<MigrationTaskEntity, MigrationTask> {
    @Override
    protected MigrationTaskEntity processDomainConversion(MigrationTask domain) {
        return MigrationTaskEntity.builder()
            .event(domain.getEvent())
            .name(domain.getName())
            .completed(domain.getCompleted())
            .repeatable(domain.getRepeatable())
            .build();
    }

    @Override
    protected MigrationTask processEntityConversion(MigrationTaskEntity entity) {
        return MigrationTask.builder()
            .event(entity.getEvent())
            .name(entity.getName())
            .completed(entity.getCompleted())
            .repeatable(entity.getRepeatable())
            .build();
    }
}
