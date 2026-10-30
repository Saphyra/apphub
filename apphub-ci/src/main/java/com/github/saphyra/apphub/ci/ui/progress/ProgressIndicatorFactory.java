package com.github.saphyra.apphub.ci.ui.progress;

import com.github.saphyra.apphub.ci.dao.property.PropertyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProgressIndicatorFactory {
    private static final ProgressIndicator NO_OP_INDICATOR = new NoOpProgressIndicator();

    private final PropertyDao propertyDao;
    private final ProgressIndicatorContext progressIndicatorContext;

    public ProgressIndicator createFromLabels(String panelTitle, List<String> labels) {
        return propertyDao.isGuiEnabled() ? new DefaultProgressIndicator(panelTitle, labels, progressIndicatorContext).run() : NO_OP_INDICATOR;
    }

    public ProgressIndicator noOpIndicator() {
        return NO_OP_INDICATOR;
    }
}
