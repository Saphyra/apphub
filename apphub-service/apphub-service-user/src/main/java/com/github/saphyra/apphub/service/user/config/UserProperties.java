package com.github.saphyra.apphub.service.user.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class UserProperties {
    @Value("${deleteAccountBatchCount}")
    private Integer deleteAccountBatchCount;
}
