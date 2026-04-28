package com.github.saphyra.apphub.ci.menu.settings.s3;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class S3EnvironmentOption implements MenuOption {
    private final int order;
    private final S3MenuContext context;
    private final Environment environment;

    @Override
    public Menu getMenu() {
        return Menu.S3_SETTINGS_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return () -> order;
    }

    @Override
    public LocalizationProvider getName() {
        return _ -> environment.name();
    }

    @Override
    public boolean process() {
        context.setEnvironment(environment);

        context.getApplicationContextProxy().getBean(S3PropertiesMenu.class).enter();
        return false;
    }
}
