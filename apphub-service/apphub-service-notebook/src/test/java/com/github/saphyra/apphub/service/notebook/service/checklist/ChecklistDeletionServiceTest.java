package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ChecklistItemDeletionService checklistItemDeletionService;

    @InjectMocks
    private ChecklistDeletionService underTest;

    @Mock
    private Dimension dimension;

    @Test
    void delete() {
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(dimension));

        underTest.delete(LIST_ITEM_ID);

        then(checklistItemDeletionService).should().deleteChecklistItem(dimension);
    }
}