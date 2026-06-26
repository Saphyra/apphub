package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_OBJECT;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_OPERATIONS;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PRINCIPAL;

@Component
@RequiredArgsConstructor
class AlmMapper extends ConverterBase<Map<String, AttributeValue>, Alm> {

    @Override
    protected Map<String, AttributeValue> processDomainConversion(Alm domain) {
        Map<String, AttributeValue> result = new HashMap<>();

        result.put(COLUMN_PRINCIPAL, AttributeValue.builder().s(domain.getPrincipalType() + "#" + domain.getPrincipal()).build());
        result.put(COLUMN_OBJECT, AttributeValue.builder().s(domain.getObjectType() + "#" + domain.getObjectId()).build());
        result.put(COLUMN_OPERATIONS, AttributeValue.builder().ss(domain.getOperations().stream().map(Enum::name).toList()).build());

        return result;
    }

    @Override
    protected Alm processEntityConversion(Map<String, AttributeValue> entity) {
        String[] pkParts = entity.get(COLUMN_PRINCIPAL).s().split("#", 2);
        String[] skParts = entity.get(COLUMN_OBJECT).s().split("#", 2);

        return Alm.builder()
            .principalType(PrincipalType.valueOf(pkParts[0]))
            .principal(UUID.fromString(pkParts[1]))
            .objectType(ObjectType.valueOf(skParts[0]))
            .objectId(UUID.fromString(skParts[1]))
            .operations(entity.get(COLUMN_OPERATIONS).ss().stream().map(Operation::valueOf).toList())
            .build();
    }
}
