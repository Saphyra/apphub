package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class DefaultProperties {
    private final int buildThreadCount = 6;
    private final int testsThreadCount = 20;
    private final int serviceStartupCountLimit = 5;
    private final int preCreateDriverCount = 0;
    private final String bashFileLocation = "bash";
    private final int browserStartupLimit = 5;
    private boolean guiEnabled = true;
    private final int testRetryCount = 2;
}
