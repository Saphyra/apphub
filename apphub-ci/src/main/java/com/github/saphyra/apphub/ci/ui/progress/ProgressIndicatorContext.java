package com.github.saphyra.apphub.ci.ui.progress;

import com.github.saphyra.apphub.ci.util.SleepService;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class ProgressIndicatorContext {
    private final ExecutorServiceBean executorServiceBean;
    private final SleepService sleepService;
}
