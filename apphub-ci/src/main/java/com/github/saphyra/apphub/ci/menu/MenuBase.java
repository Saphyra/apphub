package com.github.saphyra.apphub.ci.menu;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class MenuBase<OPTION extends MenuOption> {
    protected final List<MenuOption> options;
    private final MenuOption exitOption;

    public MenuBase(List<OPTION> options) {
        this.exitOption = new ExitOption();
        this.options = Stream.concat(options.stream(), Stream.of(exitOption))
            .collect(Collectors.toList());
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

            Optional<MenuOption> maybeResult = options.stream()
                .filter(option -> option.getCommand().equals(input))
                .findFirst();

            if (maybeResult.isPresent()) {
                return maybeResult.get();
            } else {
                log.info("Invalid command."); //TODO translate
            }
        }
    }

    protected abstract String getName();

    private String getInput() {
        log.info("What would you like to do?"); //TODO translate

        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void printOptions() {
        log.info("");
        log.info("");
        log.info(getName());

        options.stream()
            .map(option -> option.getLabel(options))
            .sorted(String::compareTo)
            .forEach(log::info);
    }

    private static class ExitOption implements MenuOption {
        @Override
        public String getCommand() {
            return "0";
        }

        @Override
        public String getName() {
            return "Exit";//TODO translate
        }

        @Override
        public boolean process() {
            throw new IllegalStateException("This method should never be called.");
        }
    }
}
