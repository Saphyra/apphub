package com.github.saphyra.apphub.service.custom.elite_base.service.star_system;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemSuggestionListCache;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StarSystemControllerImplTest {
    private static final String QUERY = "qUery";
    private static final UUID STAR_ID = UUID.randomUUID();
    private static final String STAR_NAME = "star-name";

    @Mock
    private StarSystemSuggestionListCache starSystemSuggestionListCache;

    @InjectMocks
    private StarSystemControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private StarSystem starSystem;

    @Test
    void search_queryTooShort() {
        ExceptionValidator.validateInvalidParam(() -> underTest.search(new OneParamRequest<>("as"), accessTokenHeader), "query", "too short");
    }

    @Test
    void search() {
        given(starSystemSuggestionListCache.get(QUERY.toLowerCase())).willReturn(Optional.of(List.of(starSystem)));
        given(starSystem.getId()).willReturn(STAR_ID);
        given(starSystem.getStarName()).willReturn(STAR_NAME);

        assertThat(underTest.search(new OneParamRequest<>(QUERY), accessTokenHeader)).containsEntry(STAR_ID, STAR_NAME);
    }
}