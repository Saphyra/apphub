package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CitizenAllocationServiceTest {
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private CitizenAllocationDao dao;

    @InjectMocks
    private CitizenAllocationService underTest;

    @Mock
    private CitizenAllocationModel model;

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.CITIZEN_ALLOCATION);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(dao).saveAll(Arrays.asList(model));
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(dao).deleteById(ID);
    }
}