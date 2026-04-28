package com.github.saphyra.apphub.service.platform.storage.client.ftp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@FtpClientEnabled
class FtpClientFactory {
    private final FtpClientProperties properties;

    FtpClientWrapper create() {
        log.info("Creating FTP client with username {}", properties.getUsername());
        return new FtpClientWrapper(properties);
    }
}
