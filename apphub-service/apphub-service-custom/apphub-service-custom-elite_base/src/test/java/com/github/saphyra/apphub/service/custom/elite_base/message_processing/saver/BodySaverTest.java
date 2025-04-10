package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyType;
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
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BodySaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long BODY_ID = 32432L;
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String BODY_NAME = "body-name";
    private static final Double DISTANCE_FROM_STAR = 3423.324;

    @Mock
    private BodyDao bodyDao;

    @Mock
    private BodyFactory bodyFactory;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private BodySaver underTest;

    @Mock
    private Body body;

    @Test
    void allNull() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, null, null, null, null, null))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void starSystemIdNull() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, null, null, BODY_ID, null, null))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bodyIdNull() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, null, null, null, null))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void foundByStarSystemIdAndBodyId_deprecatedMessage() {
        given(bodyDao.findByBodyName(BODY_NAME)).willReturn(Optional.of(body));
        given(body.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID, BODY_NAME, DISTANCE_FROM_STAR)).isEqualTo(body);

        then(body).should(times(0)).setLastUpdate(any());
        then(body).should(times(0)).setStarSystemId(any());
        then(body).should(times(0)).setType(any());
        then(body).should(times(0)).setBodyId(any());
        then(body).should(times(0)).setBodyName(any());
        then(body).should(times(0)).setDistanceFromStar(any());
        then(bodyDao).should(times(0)).save(body);
    }

    @Test
    void foundByMarketId() {
        given(bodyDao.findByBodyName(BODY_NAME)).willReturn(Optional.of(body));
        given(body.getLastUpdate()).willReturn(LAST_UPDATE);

        assertThat(underTest.save(LAST_UPDATE, null, BodyType.STAR, BODY_ID, BODY_NAME, DISTANCE_FROM_STAR)).isEqualTo(body);

        then(bodyDao).should(times(0)).findByStarSystemIdAndBodyId(any(), any());
        then(body).should(times(0)).setLastUpdate(any());
        then(body).should(times(0)).setStarSystemId(any());
        then(body).should().setType(BodyType.STAR);
        then(body).should().setBodyId(BODY_ID);
        then(body).should().setBodyName(BODY_NAME);
        then(body).should().setDistanceFromStar(DISTANCE_FROM_STAR);
        then(bodyDao).should().save(body);
    }

    @Test
    void notFound() {
        given(bodyDao.findByBodyName(BODY_NAME)).willReturn(Optional.empty());
        given(bodyFactory.create(LAST_UPDATE, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID, BODY_NAME, DISTANCE_FROM_STAR)).willReturn(body);
        given(body.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID, BODY_NAME, DISTANCE_FROM_STAR)).isEqualTo(body);

        then(body).should().setLastUpdate(LAST_UPDATE);
        then(body).should().setStarSystemId(STAR_SYSTEM_ID);
        then(body).should().setType(BodyType.STAR);
        then(body).should().setBodyId(BODY_ID);
        then(body).should().setBodyName(BODY_NAME);
        then(body).should().setDistanceFromStar(DISTANCE_FROM_STAR);
        then(bodyDao).should(times(2)).save(body);
    }
}