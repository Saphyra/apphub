package com.github.saphyra.apphub.ci.menu.thread_count_editer;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ThreadCountEditerMenu extends MenuBase<ThreadCountEditerMenuOption> {
    ThreadCountEditerMenu(List<ThreadCountEditerMenuOption> threadCountEditerMenuOptions, LocalizationService localizationService) {
        super(threadCountEditerMenuOptions, localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.THREAD_COUNT;
    }
}
