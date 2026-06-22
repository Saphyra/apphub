package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationFactory {
    private final IdGenerator idGenerator;

    public Organization create(String name, String description){
        return Organization.builder()
            .id(idGenerator.randomUuid())
            .name(name)
            .description(description)
            .build();
    }
}
