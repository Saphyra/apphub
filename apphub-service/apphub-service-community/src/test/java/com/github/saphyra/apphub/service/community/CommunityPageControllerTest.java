package com.github.saphyra.apphub.service.community;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CommunityPageControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private CommunityPageController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void getIndex() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        ModelAndView result = underTest.getIndex(accessTokenHeader);

        assertThat(result.getViewName()).isEqualTo("community");
        assertThat(result.getModel()).containsEntry("userId", USER_ID);
    }
}