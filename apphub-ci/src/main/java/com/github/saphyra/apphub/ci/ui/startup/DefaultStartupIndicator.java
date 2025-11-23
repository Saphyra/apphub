package com.github.saphyra.apphub.ci.ui.startup;

import com.github.saphyra.apphub.ci.ui.utils.ScreenSizeUtils;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DefaultStartupIndicator implements StartupIndicator {
    private static final int PANEL_WIDTH = 500;

    private final Map<String, StartupService> services;
    private final StartupIndicatorContext startupIndicatorContext;

    private JFrame frame;
    private JProgressBar progressBar;

    static void main() {
        SwingUtilities.invokeLater(() -> {
            List<String> services = Stream.iterate(0, n -> n + 1)
                .limit(20)
                .map(n -> "Service-" + n)
                .collect(Collectors.toList());
            new DefaultStartupIndicator(services, null).run();
        });
    }

    DefaultStartupIndicator(List<String> serviceNames, StartupIndicatorContext startupIndicatorContext) {
        this.startupIndicatorContext = startupIndicatorContext;
        int maxServiceNameLength = serviceNames.stream()
            .mapToInt(String::length)
            .max()
            .orElse(0);

        services = serviceNames.stream()
            .collect(Collectors.toMap(serviceName -> serviceName, serviceName -> new StartupService(startupIndicatorContext, maxServiceNameLength, serviceName)));
    }

    @Override
    public void startupInitiated(String name) {
        services.get(name)
            .startupInitiated();
    }

    @Override
    public void startupCompleted(String name) {
        services.get(name)
            .startupCompleted();

        int finished = (int) services.values()
            .stream()
            .filter(startupService -> startupService.getStatus() == StartupStatus.STARTED)
            .count();

        SwingUtilities.invokeLater(() -> progressBar.setValue(finished));
    }

    @Override
    public void scheduleShutdown() {
        startupIndicatorContext.getExecutorServiceBean()
            .execute(() -> {
                startupIndicatorContext.getSleepService().sleep(10000);
                SwingUtilities.invokeLater(() -> frame.dispose());
            });
    }

    public StartupIndicator run() {
        SwingUtilities.invokeLater(() -> {
            //Frame
            frame = createFrame(services.size());

            //Progress Bar
            progressBar = createProgressBar(services.size());
            frame.add(progressBar, BorderLayout.NORTH);

            //Service list
            JScrollPane serviceList = createServiceList(services.values());
            frame.add(serviceList, BorderLayout.CENTER);
        });

        return this;
    }

    private static JScrollPane createServiceList(Collection<StartupService> services) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        services.forEach(startupService -> panel.add(startupService.getLabel()));
        return new JScrollPane(panel);
    }

    private static JProgressBar createProgressBar(int size) {
        JProgressBar progressBar = new JProgressBar(0, size);

        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(PANEL_WIDTH, 30));

        return progressBar;
    }

    private static JFrame createFrame(int serviceSize) {
        JFrame frame = new JFrame("AppHub CI - Startup up services");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(PANEL_WIDTH, getScreenHeight(serviceSize)));
        frame.setVisible(true);

        return frame;
    }

    private static int getScreenHeight(int serviceSize) {
        int maxHeight = (int) (ScreenSizeUtils.getScreenHeight() * 0.7);
        int suggestedHeight = 40 + serviceSize * 25;

        return Math.min(maxHeight, suggestedHeight);
    }
}
