package com.github.saphyra.apphub.service.custom.elite_base.common.executor;

import com.github.saphyra.apphub.lib.concurrency.FixedExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessorExecutor extends FixedExecutorServiceBean {
    public MessageProcessorExecutor(
        @Value("${elite-base.executor.messageProcessor.threadCount}") int threadCount,
        ErrorReporterService errorReporterService
    ) {
        super(threadCount, errorReporterService);
    }
}
