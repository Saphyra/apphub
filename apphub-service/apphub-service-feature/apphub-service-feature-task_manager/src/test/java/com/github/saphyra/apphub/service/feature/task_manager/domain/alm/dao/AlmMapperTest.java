package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_OPERATIONS;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_SK;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AlmMapperTest {
    private static final UUID PRINCIPAL = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final List<Operation> OPERATIONS = List.of(Operation.OWNER, Operation.READ);

    private final AlmMapper underTest = new AlmMapper();

    @Test
    void convertDomain() {
        Alm domain = Alm.builder()
            .principal(PRINCIPAL)
            .principalType(PrincipalType.USER)
            .objectId(OBJECT_ID)
            .objectType(ObjectType.ORGANIZATION)
            .operations(OPERATIONS)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PrincipalType.USER + "#" + PRINCIPAL);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(ObjectType.ORGANIZATION + "#" + OBJECT_ID);
        assertThat(result.get(COLUMN_OPERATIONS).ss()).containsExactly(Operation.OWNER.name(), Operation.READ.name());
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> entity = Map.of(
            COLUMN_PK, AttributeValue.builder().s(PrincipalType.USER + "#" + PRINCIPAL).build(),
            COLUMN_SK, AttributeValue.builder().s(ObjectType.ORGANIZATION + "#" + OBJECT_ID).build(),
            COLUMN_OPERATIONS, AttributeValue.builder().ss(Operation.OWNER.name(), Operation.READ.name()).build()
        );

        assertThat(underTest.convertEntity(entity))
            .returns(PRINCIPAL, Alm::getPrincipal)
            .returns(PrincipalType.USER, Alm::getPrincipalType)
            .returns(OBJECT_ID, Alm::getObjectId)
            .returns(ObjectType.ORGANIZATION, Alm::getObjectType)
            .returns(OPERATIONS, Alm::getOperations);
    }
}