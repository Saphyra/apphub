package com.github.saphyra.apphub.service.platform.storage.ftp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FtpClientFactory {
    private final FtpClientProperties properties;

    public FtpClientWrapper create() {
        return new FtpClientWrapper(properties);
    }
}
