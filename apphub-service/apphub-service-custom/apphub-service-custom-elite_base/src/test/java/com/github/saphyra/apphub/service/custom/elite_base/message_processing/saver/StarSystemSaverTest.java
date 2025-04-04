package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StarSystemSaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long STAR_ID = 32432L;
    private static final String STAR_NAME = "star-name";
    private static final Double[] STAR_POSITION = new Double[]{34.2, 3.1, 45.5};

    @Mock
    private StarSystemDao starSystemDao;

    @Mock
    private StarSystemFactory starSystemFactory;

    @InjectMocks
    private StarSystemSaver underTest;

    @Mock
    private StarSystem starSystem;

    @Test
    void nullStarIdAndStarName() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, null, null, null))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void save_new() {
        given(starSystemDao.findByStarId(STAR_ID)).willReturn(Optional.empty());
        given(starSystemDao.findByStarName(STAR_NAME)).willReturn(Optional.empty());
        given(starSystemFactory.create(LAST_UPDATE, STAR_ID, STAR_NAME, STAR_POSITION, StarType.A)).willReturn(starSystem);
        given(starSystem.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, STAR_ID, STAR_NAME, STAR_POSITION, StarType.A)).isEqualTo(starSystem);

        then(starSystem).should(times(0)).setLastUpdate(any());
        then(starSystem).should(times(0)).setStarId(any());
        then(starSystem).should(times(0)).setStarName(any());
        then(starSystem).should(times(0)).setPosition(any());
        then(starSystem).should(times(0)).setStarType(any());
        then(starSystemDao).should().save(starSystem);
    }

    @Test
    void save_existing() {
        given(starSystemDao.findByStarId(STAR_ID)).willReturn(Optional.empty());
        given(starSystemDao.findByStarName(STAR_NAME)).willReturn(Optional.of(starSystem));
        given(starSystem.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));
        given(starSystem.getStarId()).willReturn(null);

        assertThat(underTest.save(LAST_UPDATE, STAR_ID, STAR_NAME, STAR_POSITION, StarType.A)).isEqualTo(starSystem);

        then(starSystem).should().setLastUpdate(LAST_UPDATE);
        then(starSystem).should().setStarId(STAR_ID);
        then(starSystem).should().setStarName(STAR_NAME);
        then(starSystem).should().setPosition(StarSystemPosition.parse(STAR_POSITION));
        then(starSystem).should().setStarType(StarType.A);
        then(starSystemDao).should().save(starSystem);
    }
}