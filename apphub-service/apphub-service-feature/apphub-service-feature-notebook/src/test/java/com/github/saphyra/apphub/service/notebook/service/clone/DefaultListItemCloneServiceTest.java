package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultListItemCloneServiceTest {
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private DefaultListItemCloneService underTest;

    @Mock
    private ListItem toClone;

    @Mock
    private ListItem clone;

    @Test
    void cloneListItem() {
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clone);

        underTest.clone(PARENT, toClone);

        then(listItemDao).should().save(clone);
    }
}