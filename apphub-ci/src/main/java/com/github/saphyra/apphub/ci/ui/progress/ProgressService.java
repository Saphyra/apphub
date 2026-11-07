package com.github.saphyra.apphub.ci.ui.progress;

import com.github.saphyra.apphub.ci.util.concurrent.FutureWrapper;
import lombok.Getter;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class ProgressService {
    private final ProgressIndicatorContext progressIndicatorContext;
    private final int maxServiceNameLength;
    private final String serviceName;

    @Getter
    private ProgressStatus status = ProgressStatus.WAITING;
    private LocalDateTime startedAt;
    private volatile LocalDateTime finishedAt;
    @Getter
    private JLabel label;
    private FutureWrapper<Void> futureWrapper;

    ProgressService(ProgressIndicatorContext progressIndicatorContext, int maxServiceNameLength, String serviceName) {
        this.progressIndicatorContext = progressIndicatorContext;
        this.maxServiceNameLength = maxServiceNameLength;
        this.serviceName = serviceName;
        label = new JLabel();
        label.setFont(new Font("Monospaced", Font.PLAIN, 16));
        label.setText(getLabelText());
    }

    public void startupInitiated() {
        status = ProgressStatus.IN_PROGRESS;
        startedAt = LocalDateTime.now();
        SwingUtilities.invokeLater(() -> label.setText(getLabelText()));

        futureWrapper = progressIndicatorContext.getExecutorServiceBean()
            .execute(() -> {
                while (isNull(finishedAt)) {
                    progressIndicatorContext.getSleepService().sleep(50);

                    SwingUtilities.invokeLater(() -> label.setText(getLabelText()));
                }
            });
    }

    public void startupCompleted() {
        status = ProgressStatus.COMPLETED;
        finishedAt = LocalDateTime.now();
        label.setText(getLabelText());
        SwingUtilities.invokeLater(() -> label.setText(getLabelText()));
    }

    private String getLabelText() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(withTrailingSpaces(serviceName, maxServiceNameLength));
        sb.append(" | ");
        sb.append(withTrailingSpaces(status.name(), ProgressStatus.maxLength()));
        sb.append(" | ");

        if (nonNull(startedAt)) {
            LocalDateTime endTime = nonNull(finishedAt) ? finishedAt : LocalDateTime.now();

            long startupTime = Duration.between(startedAt, endTime).toMillis();
            sb.append(startupTime / 1000d);
            sb.append(" s");
        }

        return sb.toString();
    }

    private String withTrailingSpaces(String serviceName, int maxServiceNameLength) {
        StringBuilder sb = new StringBuilder(serviceName);
        int spacesToAdd = maxServiceNameLength - serviceName.length();
        sb.append(" ".repeat(Math.max(0, spacesToAdd)));
        return sb.toString();
    }

    public void startupFailed() {
        status = ProgressStatus.FAILED;
        finishedAt = LocalDateTime.now();
        label.setText(getLabelText());
        SwingUtilities.invokeLater(() -> label.setText(getLabelText()));
    }
}
