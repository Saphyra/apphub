package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructionAreasTest {
    private static final UUID SURFACE_ID_1 = UUID.randomUUID();
    private static final UUID SURFACE_ID_2 = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID_1 = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID_2 = UUID.randomUUID();

    @Mock
    private ConstructionArea constructionArea1;

    @Test
    void findBySurfaceId() {
        given(constructionArea1.getSurfaceId()).willReturn(SURFACE_ID_1);

        assertThat(new ConstructionAreas(constructionArea1).findBySurfaceId(SURFACE_ID_1)).contains(constructionArea1);
    }

    @Test
    void findBySurfaceId_notFound() {
        given(constructionArea1.getSurfaceId()).willReturn(SURFACE_ID_2);

        assertThat(new ConstructionAreas(constructionArea1).findBySurfaceId(SURFACE_ID_1)).isEmpty();
    }

    @Test
    void findByConstructionAreaIdValidated() {
        given(constructionArea1.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID_1);

        assertThat(new ConstructionAreas(constructionArea1).findByIdValidated(CONSTRUCTION_AREA_ID_1)).isEqualTo(constructionArea1);
    }

    @Test
    void findByConstructionAreaIdValidated_notFound() {
        given(constructionArea1.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID_2);

        ExceptionValidator.validateNotLoggedException(() -> new ConstructionAreas(constructionArea1).findByIdValidated(CONSTRUCTION_AREA_ID_1), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}