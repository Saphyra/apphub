package com.github.saphyra.apphub.service.platform.storage.client.s3;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(value = "aws.s3")
@S3ClientEnabled
@ToString(exclude = "secretKey")
@Slf4j
public class S3Properties {
    private String bucketName;
    private String accessKeyId;
    private String secretKey;

    @PostConstruct
    void logSelf() {
        log.info("{}", this);
    }
}
