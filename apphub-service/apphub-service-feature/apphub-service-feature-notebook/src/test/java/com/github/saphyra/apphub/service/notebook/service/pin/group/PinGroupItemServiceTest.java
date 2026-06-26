package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PinGroupItemServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private PinGroupDao pinGroupDao;

    @InjectMocks
    private PinGroupItemService underTest;

    @Mock
    private PinGroup pinGroup;

    @Test
    void addItem_alreadyInPinGroup() {
        given(pinGroupDao.findByIdValidated(USER_ID, PIN_GROUP_ID)).willReturn(pinGroup);
        given(pinGroup.getListItemIds()).willReturn(Set.of(LIST_ITEM_ID));

        underTest.addItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);

        then(listItemDao).should().findByIdValidated(USER_ID, LIST_ITEM_ID);
        then(pinGroup).should(never()).addListItem(any());
        then(pinGroupDao).should(never()).save(any());
    }

    @Test
    void addItem() {
        given(pinGroupDao.findByIdValidated(USER_ID, PIN_GROUP_ID)).willReturn(pinGroup);
        given(pinGroup.getListItemIds()).willReturn(Set.of());

        underTest.addItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);

        then(listItemDao).should().findByIdValidated(USER_ID, LIST_ITEM_ID);
        then(pinGroup).should().addListItem(LIST_ITEM_ID);
        then(pinGroupDao).should().save(pinGroup);
    }

    @Test
    void removeItem_notInPinGroup() {
        given(pinGroupDao.findByIdValidated(USER_ID, PIN_GROUP_ID)).willReturn(pinGroup);
        given(pinGroup.getListItemIds()).willReturn(Set.of());

        underTest.removeItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);

        then(pinGroup).should(never()).removeListItem(any());
        then(pinGroupDao).should(never()).save(any());
    }

    @Test
    void removeItem() {
        given(pinGroupDao.findByIdValidated(USER_ID, PIN_GROUP_ID)).willReturn(pinGroup);
        given(pinGroup.getListItemIds()).willReturn(Set.of(LIST_ITEM_ID));

        underTest.removeItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);

        then(pinGroup).should().removeListItem(LIST_ITEM_ID);
        then(pinGroupDao).should().save(pinGroup);
    }
}