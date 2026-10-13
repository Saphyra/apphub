package com.github.saphyra.apphub.ci.ui.startup;

import com.github.saphyra.apphub.ci.util.SleepService;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class StartupIndicatorContext {
    private final ExecutorServiceBean executorServiceBean;
    private final SleepService sleepService;
}
