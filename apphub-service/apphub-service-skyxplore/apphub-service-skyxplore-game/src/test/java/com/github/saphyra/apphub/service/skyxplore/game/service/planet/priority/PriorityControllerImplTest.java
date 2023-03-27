package com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PriorityControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 324;

    @Mock
    private PriorityUpdateService priorityUpdateService;

    @Mock
    private PriorityQueryService priorityQueryService;

    @InjectMocks
    private PriorityControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getPriorities() {
        given(priorityQueryService.getPriorities(USER_ID, PLANET_ID)).willReturn(CollectionUtils.toMap(PriorityType.CONSTRUCTION.name(), PRIORITY));

        Map<String, Integer> result = underTest.getPriorities(PLANET_ID, accessTokenHeader);

        assertThat(result).containsEntry(PriorityType.CONSTRUCTION.name(), PRIORITY);
    }

    @Test
    public void updatePriority() {
        underTest.updatePriority(new OneParamRequest<>(PRIORITY), PLANET_ID, PriorityType.CONSTRUCTION.name(), accessTokenHeader);

        verify(priorityUpdateService).updatePriority(USER_ID, PLANET_ID, PriorityType.CONSTRUCTION.name(), PRIORITY);
    }
}