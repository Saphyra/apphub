package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupDao;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMappingDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PinGroupDeletionServiceTest {
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();

    @Mock
    private PinGroupDao pinGroupDao;

    @Mock
    private PinMappingDao pinMappingDao;

    @InjectMocks
    private PinGroupDeletionService underTest;

    @Test
    void delete() {
        underTest.delete(PIN_GROUP_ID);

        then(pinGroupDao).should().findByIdValidated(PIN_GROUP_ID);
        then(pinGroupDao).should().deleteById(PIN_GROUP_ID);
        then(pinMappingDao).should().deleteByPinGroupId(PIN_GROUP_ID);
    }
}