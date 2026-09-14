package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_OBJECT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_OPERATIONS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PARENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PRINCIPAL;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
@RequiredArgsConstructor
class AlmMapper extends ConverterBase<Map<String, AttributeValue>, Alm> {
    private final UuidConverter uuidConverter;

    @Override
    protected Map<String, AttributeValue> processDomainConversion(Alm domain) {
        Map<String, AttributeValue> result = new HashMap<>();

        result.put(COLUMN_PRINCIPAL, AttributeValue.builder().s(domain.getPrincipalType() + "#" + uuidConverter.convertDomain(domain.getPrincipal())).build());
        result.put(COLUMN_OBJECT, AttributeValue.builder().s(domain.getObjectType() + "#" + uuidConverter.convertDomain(domain.getObjectId())).build());
        result.put(COLUMN_OPERATIONS, AttributeValue.builder().ss(domain.getGrants().stream().map(Enum::name).toList()).build());
        result.put(COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(domain.getOwner())).build());
        result.put(COLUMN_PARENT, AttributeValue.builder().s(uuidConverter.convertDomain(domain.getParent())).build());

        return result;
    }

    @Override
    protected Alm processEntityConversion(Map<String, AttributeValue> entity) {
        String[] pkParts = entity.get(COLUMN_PRINCIPAL).s().split("#", 2);
        String[] skParts = entity.get(COLUMN_OBJECT).s().split("#", 2);
        String[] userIdParts = entity.get(COLUMN_USER_ID).s().split("#", 2);

        return Alm.builder()
            .principalType(PrincipalType.valueOf(pkParts[0]))
            .principal(uuidConverter.convertEntity(pkParts[1]))
            .objectType(SharedObjectType.valueOf(skParts[0]))
            .objectId(uuidConverter.convertEntity(skParts[1]))
            .owner(uuidConverter.convertEntity(userIdParts[1]))
            .parent(uuidConverter.convertEntity(entity.get(COLUMN_PARENT).s()))
            .grants(entity.get(COLUMN_OPERATIONS).ss().stream().map(Grant::valueOf).collect(Collectors.toSet()))
            .build();
    }
}
