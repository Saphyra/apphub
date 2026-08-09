package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_LABEL;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LabelMapperTest {
    private static final String USER_ID = "user-id";
    private static final String LABEL_ID = "label-id";
    private static final String LABEL = "label-value";

    @InjectMocks
    private LabelMapper underTest;

    @Test
    void convertDomain() {
        LabelEntity domain = LabelEntity.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_USER + USER_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_LABEL + LABEL_ID);
        assertThat(result.get(COLUMN_LABEL).s()).isEqualTo(LABEL);
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> item = Map.ofEntries(
            Map.entry(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + USER_ID).build()),
            Map.entry(COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL + LABEL_ID).build()),
            Map.entry(COLUMN_LABEL, AttributeValue.builder().s(LABEL).build())
        );

        LabelEntity result = underTest.convertEntity(item);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getLabelId()).isEqualTo(LABEL_ID);
        assertThat(result.getLabel()).isEqualTo(LABEL);
    }
}