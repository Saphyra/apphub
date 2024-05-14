package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.utils.Utils;

import java.util.List;

public interface MenuOption {
    String getCommand();

    String getName();

    default String getLabel(List<MenuOption> options) {
        int maxLength = getMaxLength(options);

        return "[%s] - %s".formatted(Utils.withLeading(getCommand(), maxLength, " "), getName());
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
