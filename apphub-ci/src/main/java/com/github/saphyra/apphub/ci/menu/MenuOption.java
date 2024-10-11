package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.utils.Utils;

import java.util.Collection;
import java.util.List;

public interface MenuOption {
    Menu getMenu();

    default List<Menu> getMenus() {
        return List.of(getMenu());
    }

    MenuOrder getOrder();

    LocalizationProvider getName();

    default LocalizationProvider getLabel(int command, Collection<MenuOption> options) {
        int maxLength = getMaxLength(options);

        return language -> "[%s] - %s".formatted(Utils.withLeading(command, maxLength, "0"), getName().getLocalizedText(language));
    }

    /**
     * @return true, if Menu should exit as well
     */
    boolean process();

    private static int getMaxLength(Collection<MenuOption> options) {
        return String.valueOf(options.size())
            .length();
    }
}
