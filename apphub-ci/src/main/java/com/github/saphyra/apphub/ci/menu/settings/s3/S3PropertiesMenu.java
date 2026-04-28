package com.github.saphyra.apphub.ci.menu.settings.s3;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class S3PropertiesMenu extends MenuBase {
    private final S3MenuContext context;

    public S3PropertiesMenu(LocalizationService localizationService, S3MenuContext context) {
        super(getMenuOptions(context), localizationService, Menu.S3_PROPERTIES_MENU);
        this.context = context;
    }

    @Override
    protected LocalizationProvider getName() {
        return language -> LocalizedText.S3_PROPERTIES_MENU.getLocalizedText(language).formatted(context.getEnvironment().name());
    }

    private static List<MenuOption> getMenuOptions(S3MenuContext context) {
        List<MenuOption> result = new ArrayList<>();
        for(int order = 0; order < S3MenuContext.PROPERTIES.size(); order++) {
            MenuOption menuOption = S3PropertyMenuOption.builder()
                .order(order + 1)
                .propertyName(S3MenuContext.PROPERTIES.get(order))
                .context(context)
                .build();
            result.add(menuOption);
        }

        result.add(new S3DeleteEnvironmentMenuOption(result.size() + 1, context));

        return result;
    }
}
