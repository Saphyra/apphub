package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_DESCRIPTION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_NAME;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_ORGANIZATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_ORGANIZATION;

@Component
@RequiredArgsConstructor
class OrganizationMapper extends ConverterBase<Map<String, AttributeValue>, Organization> {
    private final UuidConverter uuidConverter;

    @Override
    protected Map<String, AttributeValue> processDomainConversion(Organization domain) {
        return Map.of(
            COLUMN_ORGANIZATION, AttributeValue.builder().s(PREFIX_ORGANIZATION + uuidConverter.convertDomain(domain.getId())).build(),
            COLUMN_NAME, AttributeValue.builder().s(domain.getName()).build(),
            COLUMN_DESCRIPTION, AttributeValue.builder().s(domain.getDescription()).build()
        );
    }

    @Override
    protected Organization processEntityConversion(Map<String, AttributeValue> entity) {
        return Organization.builder()
            .id(uuidConverter.convertEntity(entity.get(COLUMN_ORGANIZATION).s().substring(PREFIX_ORGANIZATION.length())))
            .name(entity.get(COLUMN_NAME).s())
            .description(entity.get(COLUMN_DESCRIPTION).s())
            .build();
    }
}
