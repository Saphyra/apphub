package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.utils.Utils;

import java.util.List;

public interface MenuOption {
    String getCommand();

    LocalizationProvider getName();

    default LocalizationProvider getLabel(List<MenuOption> options) {
        int maxLength = getMaxLength(options);

        return language -> "[%s] - %s".formatted(Utils.withLeading(getCommand(), maxLength, "0"), getName().getLocalizedText(language));
    }

    /**
     *
     * @return true, if Menu shoud exit as well
     */
    boolean process();

    private static int getMaxLength(List<MenuOption> options) {
        return options.stream()
            .mapToInt(option -> option.getCommand().length())
            .max()
            .orElse(0);
    }
}
