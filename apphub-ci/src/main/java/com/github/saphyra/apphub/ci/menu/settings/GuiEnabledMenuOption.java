package com.github.saphyra.apphub.ci.menu.settings;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.utils.BooleanParser;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class GuiEnabledMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final BooleanParser booleanParser;

    @Override
    public Menu getMenu() {
        return Menu.SETTINGS_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.SETTINGS_GUI_ENABLED;
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.GUI_ENABLED_MENU_OPTION_LABEL.getLocalizedText(language).formatted(propertyDao.isGuiEnabled());
    }

    @Override
    public boolean process() {
        boolean result = validatingInputReader.getInput(
            LocalizedText.ENABLE_GUI,
            booleanParser::parse,
            aBoolean -> isNull(aBoolean) ? Optional.of(LocalizedText.PROVIDE_YES_OR_NO) : Optional.empty()
        );

        propertyDao.save(PropertyName.GUI_ENABLED, result);

        return true;
    }
}
