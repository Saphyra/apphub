package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_DESCRIPTION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_NAME;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_ORGANIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrganizationMapperTest {
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String ORGANIZATION_ID_STRING = "organization-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private OrganizationMapper underTest;

    @Test
    void convertDomain() {
        Organization domain = Organization.builder()
            .id(ORGANIZATION_ID)
            .name(NAME)
            .description(DESCRIPTION)
            .build();

        given(uuidConverter.convertDomain(ORGANIZATION_ID)).willReturn(ORGANIZATION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .containsEntry(COLUMN_PK, AttributeValue.builder().s(PREFIX_ORGANIZATION + ORGANIZATION_ID_STRING).build())
            .containsEntry(COLUMN_NAME, AttributeValue.builder().s(NAME).build())
            .containsEntry(COLUMN_DESCRIPTION, AttributeValue.builder().s(DESCRIPTION).build());
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> entity = Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_ORGANIZATION + ORGANIZATION_ID_STRING).build(),
            COLUMN_NAME, AttributeValue.builder().s(NAME).build(),
            COLUMN_DESCRIPTION, AttributeValue.builder().s(DESCRIPTION).build()
        );

        given(uuidConverter.convertEntity(ORGANIZATION_ID_STRING)).willReturn(ORGANIZATION_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(ORGANIZATION_ID, Organization::getId)
            .returns(NAME, Organization::getName)
            .returns(DESCRIPTION, Organization::getDescription);
    }
}