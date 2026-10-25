package com.github.saphyra.apphub.ci.ui.startup;

import com.github.saphyra.apphub.ci.ui.utils.ScreenSizeUtils;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class DefaultStartupIndicator implements StartupIndicator {
    private static final int PANEL_WIDTH = 500;

    private final Map<String, StartupService> services;
    private final StartupIndicatorContext startupIndicatorContext;

    private JFrame frame;
    private JProgressBar progressBar;

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

    @Override
    public void startupFailed(String name) {
        services.get(name)
            .startupFailed();

        int finished = (int) services.values()
            .stream()
            .filter(startupService -> startupService.getStatus() == StartupStatus.STARTED)
            .count();

        SwingUtilities.invokeLater(() -> progressBar.setValue(finished));
    }

    public StartupIndicator run() {
        SwingUtilities.invokeLater(() -> {
            //Progress Bar
            progressBar = createProgressBar(services.size());

            //Service list
            JScrollPane serviceList = createServiceList(services.values());

            //Frame
            frame = createFrame(progressBar, serviceList);
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

    private static JFrame createFrame(JProgressBar progressBar, JScrollPane serviceList) {
        JFrame frame = new JFrame("Apphub CI - Startup up services");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(progressBar, BorderLayout.NORTH);
        frame.add(serviceList, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(new Dimension(PANEL_WIDTH, getScreenHeight(frame, progressBar, serviceList)));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }

    private static int getScreenHeight(JFrame frame, JProgressBar progressBar, JScrollPane serviceList) {
        int screenCap = (int) (ScreenSizeUtils.getScreenHeight() * 0.7);

        int insetsH = frame.getInsets().top + frame.getInsets().bottom;
        int progressH = progressBar.getPreferredSize().height;
        Component view = serviceList.getViewport().getView();
        int contentH = view == null ? 0 : view.getPreferredSize().height;
        int scrollPaneChromeH = serviceList.getInsets().top + serviceList.getInsets().bottom + 2;

        int needed = insetsH + progressH + contentH + scrollPaneChromeH;

        return Math.min(screenCap, needed);
    }
}
