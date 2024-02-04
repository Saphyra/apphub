package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupDao;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMapping;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMappingDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PinGroupItemServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private PinGroupDao pinGroupDao;

    @Mock
    private PinMappingDao pinMappingDao;

    @Mock
    private PinMappingFactory pinMappingFactory;

    @InjectMocks
    private PinGroupItemService underTest;

    @Mock
    private PinMapping pinMapping;

    @Test
    void addItem_alreadyInPinGroup() {
        given(pinMappingDao.findByPinGroupIdAndListItemId(PIN_GROUP_ID, LIST_ITEM_ID)).willReturn(Optional.of(pinMapping));

        underTest.addItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);

        then(listItemDao).should().findByIdValidated(LIST_ITEM_ID);
        then(pinGroupDao).should().findByIdValidated(PIN_GROUP_ID);


        then(pinMappingFactory).shouldHaveNoInteractions();
    }

    @Test
    void addItem() {
        given(pinMappingDao.findByPinGroupIdAndListItemId(PIN_GROUP_ID, LIST_ITEM_ID)).willReturn(Optional.empty());
        given(pinMappingFactory.create(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID)).willReturn(pinMapping);

        underTest.addItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);

        then(listItemDao).should().findByIdValidated(LIST_ITEM_ID);
        then(pinGroupDao).should().findByIdValidated(PIN_GROUP_ID);
        then(pinMappingDao).should().save(pinMapping);
    }

    @Test
    void removeItem_forbiddenOperation() {
        given(pinMappingDao.findByPinGroupIdAndListItemIdValidated(PIN_GROUP_ID, LIST_ITEM_ID)).willReturn(pinMapping);
        given(pinMapping.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.removeItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    void removeItem() {
        given(pinMappingDao.findByPinGroupIdAndListItemIdValidated(PIN_GROUP_ID, LIST_ITEM_ID)).willReturn(pinMapping);
        given(pinMapping.getUserId()).willReturn(USER_ID);

        underTest.removeItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);

        then(pinMappingDao).should().delete(pinMapping);
    }
}