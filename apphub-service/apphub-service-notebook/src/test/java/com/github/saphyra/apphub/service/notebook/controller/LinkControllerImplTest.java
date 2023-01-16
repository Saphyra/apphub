package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.link.LinkCreationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LinkControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LINK_ID = UUID.randomUUID();

    @Mock
    private LinkCreationService linkCreationService;

    @InjectMocks
    private LinkControllerImpl underTest;

    @Mock
    private LinkRequest linkRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void createLink() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(linkCreationService.create(linkRequest, USER_ID)).willReturn(LINK_ID);

        OneParamResponse<UUID> result = underTest.createLink(linkRequest, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(LINK_ID);
    }

}