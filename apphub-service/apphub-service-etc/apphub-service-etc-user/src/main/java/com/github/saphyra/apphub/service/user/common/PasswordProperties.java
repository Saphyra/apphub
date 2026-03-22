package com.github.saphyra.apphub.service.user.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class PasswordProperties {
    @Value("${password.lockAccountFailures}")
    private Integer lockAccountFailures;

    @Value("${password.lockedMinutes}")
    private Integer lockedMinutes;
}
