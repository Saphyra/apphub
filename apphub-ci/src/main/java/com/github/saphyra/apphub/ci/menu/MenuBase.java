package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
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
        Map<Integer, MenuOption> options = mapOptions();
        while (true) {
            printOptions();

            Optional<MenuOption> maybeResult = getInput()
                .map(options::get);

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

    private Optional<Integer> getInput() {
        try {
            localizationService.writeMessage(LocalizedText.WHAT_WOULD_YOU_LIKE_TO_DO);

            Scanner scanner = new Scanner(System.in);
            return Optional.of(Integer.parseInt(scanner.nextLine()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void printOptions() {
        log.info("");
        log.info("");
        localizationService.writeMessage(getName());

        Map<Integer, MenuOption> options = mapOptions();

        options.entrySet()
            .stream()
            .map(entry -> entry.getValue().getLabel(entry.getKey(), options.values()))
            .map(localizationService::getMessage)
            .sorted(String::compareTo)
            .forEach(log::info);
    }

    private Map<Integer, MenuOption> mapOptions() {
        getDuplicatedOrder()
            .ifPresent(order -> {
                throw new IllegalStateException("Duplicated order: " + order);
            });

        List<MenuOption> options = getOptionsWithExitOption()
            .sorted(Comparator.comparingInt(o -> o.getOrder().getOrder()))
            .toList();

        Map<Integer, MenuOption> result = new HashMap<>();
        for (int i = 0; i < options.size(); i++) {
            result.put(i, options.get(i));
        }

        return result;
    }

    private Optional<MenuOrder> getDuplicatedOrder() {
        Set<Integer> orders = new HashSet<>();

        List<MenuOption> options = getOptionsWithExitOption()
            .toList();
        for (MenuOption option : options) {
            MenuOrder order = option
                .getOrder();
            if (orders.contains(order.getOrder())) {
                return Optional.of(order);
            }

            orders.add(order.getOrder());
        }

        return Optional.empty();
    }

    @RequiredArgsConstructor
    private static class ExitOption implements MenuOption {
        private final Menu menu;

        @Override
        public Menu getMenu() {
            return menu;
        }

        @Override
        public MenuOrderEnum getOrder() {
            return MenuOrderEnum.EXIT_OPTION;
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
