package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
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
class BodySaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long BODY_ID_LONG = 32432L;
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String BODY_NAME = "body-name";
    private static final Double DISTANCE_FROM_STAR = 3423.324;

    @Mock
    private BodyDao bodyDao;

    @Mock
    private BodyFactory bodyFactory;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

    @InjectMocks
    private BodySaver underTest;

    @Mock
    private Body body;

    @Mock
    private LastUpdate lastUpdate;

    @Test
    void saveOptional_nullBodyName() {
        assertThat(underTest.saveOptional(LAST_UPDATE, null, null, BODY_ID_LONG, null, null)).isEmpty();
    }

    @Test
    void save_nullBodyName() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID_LONG, null, null))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void save_new() {
        given(bodyDao.findByBodyName(BODY_NAME)).willReturn(Optional.empty());
        given(bodyFactory.create(STAR_SYSTEM_ID, BodyType.STAR, BODY_ID_LONG, BODY_NAME, DISTANCE_FROM_STAR)).willReturn(body);
        given(body.getId()).willReturn(BODY_ID);
        given(lastUpdateFactory.create(BODY_ID, ObjectType.BODY, LAST_UPDATE)).willReturn(lastUpdate);

        assertThat(underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID_LONG, BODY_NAME, DISTANCE_FROM_STAR)).isEqualTo(body);

        then(body).should(never()).setStarSystemId(any());
        then(body).should(never()).setType(any());
        then(body).should(never()).setBodyId(any());
        then(body).should(never()).setBodyName(any());
        then(body).should(never()).setDistanceFromStar(any());
        then(bodyDao).should().save(body);
        then(lastUpdateDao).should().save(lastUpdate);
    }

    @Test
    void save_existing_unmodified() {
        given(bodyDao.findByBodyName(BODY_NAME)).willReturn(Optional.of(body));
        given(body.getId()).willReturn(BODY_ID);
        given(body.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(body.getType()).willReturn(BodyType.STAR);
        given(body.getBodyId()).willReturn(BODY_ID_LONG);
        given(body.getBodyName()).willReturn(BODY_NAME);
        given(body.getDistanceFromStar()).willReturn(DISTANCE_FROM_STAR);
        given(lastUpdateDao.findById(BODY_ID, ObjectType.BODY)).willReturn(Optional.empty());
        given(lastUpdateFactory.create(BODY_ID, ObjectType.BODY, LAST_UPDATE)).willReturn(lastUpdate);

        assertThat(underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID_LONG, BODY_NAME, DISTANCE_FROM_STAR)).isEqualTo(body);

        then(body).should(never()).setStarSystemId(any());
        then(body).should(never()).setType(any());
        then(body).should(never()).setBodyId(any());
        then(body).should(never()).setBodyName(any());
        then(body).should(never()).setDistanceFromStar(any());
        then(bodyDao).should(never()).save(body);
        then(lastUpdateDao).should().save(lastUpdate);
    }

    @Test
    void save_existing_modified() {
        given(bodyDao.findByBodyName(BODY_NAME)).willReturn(Optional.of(body));
        given(body.getId()).willReturn(BODY_ID);
        given(body.getStarSystemId()).willReturn(null);
        given(body.getType()).willReturn(null);
        given(body.getBodyId()).willReturn(null);
        given(body.getBodyName()).willReturn(null);
        given(body.getDistanceFromStar()).willReturn(null);
        given(lastUpdateDao.findById(BODY_ID, ObjectType.BODY)).willReturn(Optional.empty());
        given(lastUpdateFactory.create(BODY_ID, ObjectType.BODY, LAST_UPDATE)).willReturn(lastUpdate);

        assertThat(underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID_LONG, BODY_NAME, DISTANCE_FROM_STAR)).isEqualTo(body);

        then(body).should().setStarSystemId(STAR_SYSTEM_ID);
        then(body).should().setType(BodyType.STAR);
        then(body).should().setBodyId(BODY_ID_LONG);
        then(body).should().setBodyName(BODY_NAME);
        then(body).should().setDistanceFromStar(DISTANCE_FROM_STAR);
        then(bodyDao).should().save(body);
        then(lastUpdateDao).should().save(lastUpdate);
    }
}
