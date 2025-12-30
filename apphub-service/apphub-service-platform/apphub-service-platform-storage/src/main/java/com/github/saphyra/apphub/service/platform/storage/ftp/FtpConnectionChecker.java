package com.github.saphyra.apphub.service.platform.storage.ftp;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
//TODO unit test
public class FtpConnectionChecker {
    private final FtpClientFactory ftpClientFactory;

    @PostConstruct
    void checkFtpConnection() {
        log.info("Checking FTP connection...");
        try (FtpClientWrapper ftpClient = ftpClientFactory.create()) {
            ftpClient.listFiles("/");
            log.info("FTP connection successful.");
        }
    }
}
