package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.utils.Utils;

import java.util.List;
import java.util.stream.Stream;

public interface MenuOption {
    Menu getMenu();

    default List<Menu> getMenus() {
        return List.of(getMenu());
    }

    String getCommand();

    LocalizationProvider getName();

    default LocalizationProvider getLabel(Stream<MenuOption> options) {
        int maxLength = getMaxLength(options);

        return language -> "[%s] - %s".formatted(Utils.withLeading(getCommand(), maxLength, "0"), getName().getLocalizedText(language));
    }

    /**
     * @return true, if Menu shoud exit as well
     */
    boolean process();

    private static int getMaxLength(Stream<MenuOption> options) {
        return options.mapToInt(option -> option.getCommand().length())
            .max()
            .orElse(0);
    }
}
