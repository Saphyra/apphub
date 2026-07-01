package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class StarSystemSaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long STAR_ID = 32432L;
    private static final String STAR_NAME = "star-name";
    private static final Double[] STAR_POSITION = new Double[]{34.2, 3.1, 45.5};
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private StarSystemDao starSystemDao;

    @Mock
    private StarSystemFactory starSystemFactory;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

    @InjectMocks
    private StarSystemSaver underTest;

    @Mock
    private StarSystem starSystem;

    @Mock
    private LastUpdate lastUpdate;

    @Test
    void nullStarIdAndStarName() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, null, null, null))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void save_new() {
        given(starSystemDao.findByStarName(STAR_NAME)).willReturn(Optional.empty());
        given(starSystemFactory.create(STAR_ID, STAR_NAME, STAR_POSITION, StarType.A)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(lastUpdateFactory.create(STAR_SYSTEM_ID, ObjectType.STAR_SYSTEM, LAST_UPDATE)).willReturn(lastUpdate);

        assertThat(underTest.save(LAST_UPDATE, STAR_ID, STAR_NAME, STAR_POSITION, StarType.A)).isEqualTo(starSystem);

        then(starSystem).should(never()).setStarId(any());
        then(starSystem).should(never()).setStarName(any());
        then(starSystem).should(never()).setPosition(any());
        then(starSystem).should(never()).setStarType(any());
        then(starSystemDao).should().save(starSystem);
        then(lastUpdateDao).should().save(lastUpdate);
    }

    @Test
    void save_existing() {
        given(starSystemDao.findByStarName(STAR_NAME)).willReturn(Optional.of(starSystem));
        given(starSystem.getStarId()).willReturn(null);

        assertThat(underTest.save(LAST_UPDATE, STAR_ID, STAR_NAME, STAR_POSITION, StarType.A)).isEqualTo(starSystem);

        then(starSystem).should().setStarId(STAR_ID);
        then(starSystem).should().setStarName(STAR_NAME);
        then(starSystem).should().setPosition(StarSystemPosition.parse(STAR_POSITION));
        then(starSystem).should().setStarType(StarType.A);
        then(starSystemDao).should().save(starSystem);
    }

    @Test
    void outdatedMessage(){
        given(starSystemDao.findByStarName(STAR_NAME)).willReturn(Optional.of(starSystem));
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(lastUpdateDao.findById(STAR_SYSTEM_ID, ObjectType.STAR_SYSTEM)).willReturn(Optional.of(lastUpdate));
        given(lastUpdate.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, STAR_ID, STAR_NAME, STAR_POSITION, StarType.A)).isEqualTo(starSystem);

        then(lastUpdateDao).should(never()).save(any());
        then(starSystemDao).should(never()).save(any());
    }
}