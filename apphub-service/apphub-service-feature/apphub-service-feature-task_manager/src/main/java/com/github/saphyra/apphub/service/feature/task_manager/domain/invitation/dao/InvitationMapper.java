package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_INVITED_BY;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_ORGANIZATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_USER;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_ORGANIZATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_USER;

@Component
@RequiredArgsConstructor
class InvitationMapper extends ConverterBase<Map<String, AttributeValue>, Invitation> {
    private final UuidConverter uuidConverter;

    @Override
    protected Map<String, AttributeValue> processDomainConversion(Invitation domain) {
        return Map.of(
            COLUMN_USER, AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(domain.getInvitedUserId())).build(),
            COLUMN_ORGANIZATION, AttributeValue.builder().s(PREFIX_ORGANIZATION + uuidConverter.convertDomain(domain.getOrganizationId())).build(),
            COLUMN_INVITED_BY, AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(domain.getInvitedBy())).build()
        );
    }

    @Override
    protected Invitation processEntityConversion(Map<String, AttributeValue> entity) {
        return Invitation.builder()
            .invitedUserId(uuidConverter.convertEntity(entity.get(COLUMN_USER).s().substring(PREFIX_USER.length())))
            .organizationId(uuidConverter.convertEntity(entity.get(COLUMN_ORGANIZATION).s().substring(PREFIX_ORGANIZATION.length())))
            .invitedBy(uuidConverter.convertEntity(entity.get(COLUMN_INVITED_BY).s().substring(PREFIX_USER.length())))
            .build();
    }
}
