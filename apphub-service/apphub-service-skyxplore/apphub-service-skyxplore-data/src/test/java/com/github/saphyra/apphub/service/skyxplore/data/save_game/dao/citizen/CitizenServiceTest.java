package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CitizenServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private CitizenDao citizenDao;

    @Mock
    private CitizenModelValidator citizenModelValidator;

    @InjectMocks
    private CitizenService underTest;

    @Mock
    private CitizenModel model;


    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(citizenDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.CITIZEN);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(citizenModelValidator).validate(model);
        verify(citizenDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(citizenDao.findById(CITIZEN_ID)).willReturn(Optional.of(model));

        Optional<CitizenModel> result = underTest.findById(CITIZEN_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(citizenDao.getByLocation(LOCATION)).willReturn(Arrays.asList(model));

        List<CitizenModel> result = underTest.getByParent(LOCATION);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(CITIZEN_ID);

        verify(citizenDao).deleteById(CITIZEN_ID);
    }
}