package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.image.ImageCreationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ImageControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private ImageCreationService imageCreationService;

    @InjectMocks
    private ImageControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CreateFileRequest createImageRequest;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void createImage() {
        given(imageCreationService.createImage(USER_ID, createImageRequest)).willReturn(STORED_FILE_ID);

        OneParamResponse<UUID> result = underTest.createImage(createImageRequest, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(STORED_FILE_ID);
    }
}