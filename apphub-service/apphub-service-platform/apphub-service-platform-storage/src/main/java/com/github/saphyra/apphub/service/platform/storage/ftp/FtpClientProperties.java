package com.github.saphyra.apphub.service.platform.storage.ftp;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@ToString(exclude = "password")
@Slf4j
@Data
class FtpClientProperties {
    @Value("${ftp.client.server}")
    private String server;

    @Value("${ftp.client.port}")
    private int port;

    @Value("${ftp.client.username}")
    private String username;

    @Value("${ftp.client.password}")
    private String password;

    @PostConstruct
    public void postConstruct() {
        log.info("{}", this);
    }
}
