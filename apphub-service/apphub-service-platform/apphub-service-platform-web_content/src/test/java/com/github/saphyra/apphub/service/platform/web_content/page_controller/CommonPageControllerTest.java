package com.github.saphyra.apphub.service.platform.web_content.page_controller;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CommonPageControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private CommonPageController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void getCommunityPage() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        ModelAndView result = underTest.community(accessTokenHeader);

        assertThat(result.getViewName()).isEqualTo("community/community");
        assertThat(result.getModel()).containsEntry("userId", USER_ID);
    }
}