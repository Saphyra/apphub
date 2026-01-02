package com.github.saphyra.apphub.service.platform.storage.ftp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FtpConnectionCheckerTest {
    @Mock
    private FtpClientFactory ftpClientFactory;

    @InjectMocks
    private FtpConnectionChecker underTest;

    @Mock
    private FtpClientWrapper ftpClientWrapper;

    @Test
    void checkFtpConnection() {
        given(ftpClientFactory.create()).willReturn(ftpClientWrapper);

        underTest.checkFtpConnection();

        then(ftpClientWrapper).should().listFiles("/");
        then(ftpClientWrapper).should().close();
    }
}