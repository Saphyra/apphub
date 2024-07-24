package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class MenuBase {
    protected final List<MenuOption> options;
    private final Menu menu;
    private final MenuOption exitOption;
    private final LocalizationService localizationService;

    public MenuBase(List<MenuOption> options, LocalizationService localizationService, Menu menu) {
        this.localizationService = localizationService;
        this.exitOption = new ExitOption(menu);
        this.options = options;
        this.menu = menu;
    }

    public MenuBase(LocalizationService localizationService, Menu menu) {
        this(Collections.emptyList(), localizationService, menu);
    }

    public void enter() {
        while (true) {
            try {
                MenuOption option = getOption();

                if (option == exitOption) {
                    return;
                }

                if (option.process()) {
                    return;
                }
            } catch (Exception e) {
                log.error("Exception occurred in the menu.", e);
            }
        }
    }

    private MenuOption getOption() {
        while (true) {
            printOptions();

            String input = getInput();

            Optional<MenuOption> maybeResult = getOptionsWithExitOption()
                .filter(option -> option.getCommand().equals(input))
                .findFirst();

            if (maybeResult.isPresent()) {
                return maybeResult.get();
            } else {
                localizationService.writeMessage(LocalizedText.INVALID_COMMAND);
            }
        }
    }

    private Stream<MenuOption> getOptionsWithExitOption() {
        return Stream.concat(getOptions().stream(), Stream.of(exitOption));
    }

    protected List<MenuOption> getOptions() {
        return options.stream()
            .filter(menuOption -> menuOption.getMenus().contains(menu))
            .collect(Collectors.toList());
    }

    protected abstract LocalizationProvider getName();

    private String getInput() {
        localizationService.writeMessage(LocalizedText.WHAT_WOULD_YOU_LIKE_TO_DO);

        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void printOptions() {
        log.info("");
        log.info("");
        localizationService.writeMessage(getName());

        getOptionsWithExitOption()
            .map(option -> option.getLabel(getOptionsWithExitOption()))
            .map(localizationService::getMessage)
            .sorted(String::compareTo)
            .forEach(log::info);
    }

    @RequiredArgsConstructor
    private static class ExitOption implements MenuOption {
        private final Menu menu;

        @Override
        public Menu getMenu() {
            return menu;
        }

        @Override
        public String getCommand() {
            return "0";
        }

        @Override
        public LocalizedText getName() {
            return LocalizedText.EXIT;
        }

        @Override
        public boolean process() {
            throw new IllegalStateException("This method should never be called.");
        }
    }
}
