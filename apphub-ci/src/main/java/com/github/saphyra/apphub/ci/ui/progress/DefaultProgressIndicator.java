package com.github.saphyra.apphub.ci.ui.progress;

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
import java.util.TreeMap;
import java.util.stream.Collectors;

class DefaultProgressIndicator implements ProgressIndicator {
    private static final int PANEL_WIDTH = 500;

    private final Map<String, ProgressService> labels;
    private final ProgressIndicatorContext progressIndicatorContext;
    private final String panelTitle;

    private JFrame frame;
    private JProgressBar progressBar;

    DefaultProgressIndicator(String panelTitle, List<String> labels, ProgressIndicatorContext progressIndicatorContext) {
        this.panelTitle = panelTitle;
        this.progressIndicatorContext = progressIndicatorContext;
        int maxServiceNameLength = labels.stream()
            .mapToInt(String::length)
            .max()
            .orElse(0);

        this.labels = labels.stream()
            .collect(Collectors.toMap(serviceName -> serviceName, serviceName -> new ProgressService(progressIndicatorContext, maxServiceNameLength, serviceName), (a, _) -> a, TreeMap::new));
    }

    @Override
    public void started(String name) {
        labels.get(name)
            .startupInitiated();
    }

    @Override
    public void completed(String name) {
        labels.get(name)
            .startupCompleted();

        updateProgressBar();
    }

    private void updateProgressBar() {
        int finished = (int) labels.values()
            .stream()
            .filter(progressService -> progressService.getStatus() == ProgressStatus.COMPLETED || progressService.getStatus() == ProgressStatus.FAILED)
            .count();

        SwingUtilities.invokeLater(() -> progressBar.setValue(finished));
    }

    @Override
    public void scheduleShutdown() {
        progressIndicatorContext.getExecutorServiceBean()
            .execute(() -> {
                progressIndicatorContext.getSleepService().sleep(10000);
                SwingUtilities.invokeLater(() -> frame.dispose());
            });
    }

    @Override
    public void failed(String name) {
        labels.get(name)
            .startupFailed();

        updateProgressBar();
    }

    public ProgressIndicator run() {
        SwingUtilities.invokeLater(() -> {
            //Progress Bar
            progressBar = createProgressBar(labels.size());

            //Service list
            JScrollPane serviceList = createServiceList(labels.values());

            //Frame
            frame = createFrame(progressBar, serviceList);
        });

        return this;
    }

    private static JScrollPane createServiceList(Collection<ProgressService> services) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        services.forEach(progressService -> panel.add(progressService.getLabel()));
        return new JScrollPane(panel);
    }

    private static JProgressBar createProgressBar(int size) {
        JProgressBar progressBar = new JProgressBar(0, size);

        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(PANEL_WIDTH, 30));

        return progressBar;
    }

    private JFrame createFrame(JProgressBar progressBar, JScrollPane serviceList) {
        JFrame frame = new JFrame(panelTitle);
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
