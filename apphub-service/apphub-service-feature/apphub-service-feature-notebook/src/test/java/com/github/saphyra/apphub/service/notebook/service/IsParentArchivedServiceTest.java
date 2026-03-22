package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IsParentArchivedServiceTest {
    private static final UUID PARENT_ID_1 = UUID.randomUUID();
    private static final UUID PARENT_ID_2 = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private IsParentArchivedService underTest;

    @Mock
    private ListItem listItem1;

    @Mock
    private ListItem listItem2;

    @Test
    void nullParentId() {
        assertThat(underTest.isAnyOfParentsArchived(null)).isFalse();
    }

    @Test
    void parentIsArchived() {
        given(listItemDao.findByIdValidated(PARENT_ID_1)).willReturn(listItem1);
        given(listItem1.isArchived()).willReturn(false);
        given(listItem1.getParent()).willReturn(PARENT_ID_2);
        given(listItemDao.findByIdValidated(PARENT_ID_2)).willReturn(listItem2);
        given(listItem2.isArchived()).willReturn(true);

        assertThat(underTest.isAnyOfParentsArchived(PARENT_ID_1)).isTrue();
    }

    @Test
    void parentIsNotArchived() {
        given(listItemDao.findByIdValidated(PARENT_ID_1)).willReturn(listItem1);
        given(listItem1.isArchived()).willReturn(false);
        given(listItem1.getParent()).willReturn(PARENT_ID_2);
        given(listItemDao.findByIdValidated(PARENT_ID_2)).willReturn(listItem2);
        given(listItem2.isArchived()).willReturn(false);

        assertThat(underTest.isAnyOfParentsArchived(PARENT_ID_1)).isFalse();
    }
}