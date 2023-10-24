package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderChecklistItemsServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID_2 = UUID.randomUUID();

    @Mock
    private ChecklistQueryService checklistQueryService;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private OrderChecklistItemsService underTest;

    @Mock
    private Dimension dimension1;

    @Mock
    private Dimension dimension2;

    @Mock
    private Content content1;

    @Mock
    private Content content2;

    @Mock
    private ChecklistResponse checklistResponse;

    @Test
    void orderItems() {
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(dimension1, dimension2));
        given(dimension1.getDimensionId()).willReturn(CHECKLIST_ITEM_ID_1);
        given(dimension2.getDimensionId()).willReturn(CHECKLIST_ITEM_ID_2);
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID_1)).willReturn(content1);
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID_2)).willReturn(content2);
        given(content1.getContent()).willReturn("2");
        given(content2.getContent()).willReturn("1");
        given(checklistQueryService.getChecklistResponse(LIST_ITEM_ID)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.orderItems(LIST_ITEM_ID);

        then(dimension1).should().setIndex(1);
        then(dimension2).should().setIndex(0);
        then(dimensionDao).should().saveAll(List.of(dimension2, dimension1));

        assertThat(result).isEqualTo(checklistResponse);
    }
}