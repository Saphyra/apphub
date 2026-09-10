package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_LABEL;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
class LabelMapper extends ConverterBase<Map<String, AttributeValue>, LabelEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(LabelEntity domain) {
        return Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + domain.getUserId()).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL + domain.getLabelId()).build(),
            COLUMN_LABEL, AttributeValue.builder().s(domain.getLabel()).build()
        );
    }

    @Override
    protected LabelEntity processEntityConversion(Map<String, AttributeValue> item) {
        return LabelEntity.builder()
            .userId(item.get(COLUMN_PK).s().substring(PREFIX_USER.length()))
            .labelId(item.get(COLUMN_SK).s().substring(PREFIX_LABEL.length()))
            .label(item.get(COLUMN_LABEL).s())
            .build();
    }
}
