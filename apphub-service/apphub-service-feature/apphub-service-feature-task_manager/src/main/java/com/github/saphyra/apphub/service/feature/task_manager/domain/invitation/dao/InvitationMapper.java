package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_ORGANIZATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_USER;

@Component
@RequiredArgsConstructor
class InvitationMapper extends ConverterBase<Map<String, AttributeValue>, Invitation> {
    private final UuidConverter uuidConverter;

    @Override
    protected Map<String, AttributeValue> processDomainConversion(Invitation domain) {
        return Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(domain.getInvitedUserId())).build(),
            COLUMN_SK, AttributeValue.builder().s(
                PREFIX_ORGANIZATION + uuidConverter.convertDomain(domain.getOrganizationId())
                    + "|"
                    + PREFIX_USER + uuidConverter.convertDomain(domain.getInvitedBy())
            ).build()
        );
    }

    @Override
    protected Invitation processEntityConversion(Map<String, AttributeValue> entity) {
        String invitedUserId = entity.get(COLUMN_PK).s().substring(PREFIX_USER.length());
        String[] skParts = entity.get(COLUMN_SK).s().split("\\|", 2);

        return Invitation.builder()
            .invitedUserId(uuidConverter.convertEntity(invitedUserId))
            .organizationId(uuidConverter.convertEntity(skParts[0].substring(PREFIX_ORGANIZATION.length())))
            .invitedBy(uuidConverter.convertEntity(skParts[1].substring(PREFIX_USER.length())))
            .build();
    }
}
