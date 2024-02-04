package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.api.notebook.model.pin.PinGroupResponse;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PinGroupQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final String PIN_GROUP_NAME = "pin-group-name";

    @Mock
    private PinGroupDao pinGroupDao;

    @InjectMocks
    private PinGroupQueryService underTest;

    @Mock
    private PinGroup pinGroup;

    @Test
    void getPinGroups() {
        given(pinGroupDao.getByUserId(USER_ID)).willReturn(List.of(pinGroup));
        given(pinGroup.getPinGroupId()).willReturn(PIN_GROUP_ID);
        given(pinGroup.getPinGroupName()).willReturn(PIN_GROUP_NAME);

        List<PinGroupResponse> result = underTest.getPinGroups(USER_ID);

        assertThat(result).containsExactly(PinGroupResponse.builder().pinGroupId(PIN_GROUP_ID).pinGroupName(PIN_GROUP_NAME).build());
    }
}