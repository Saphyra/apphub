package com.github.saphyra.apphub.ci.ui.startup;

import com.github.saphyra.apphub.ci.utils.concurrent.FutureWrapper;
import lombok.Getter;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class StartupService {
    private final StartupIndicatorContext startupIndicatorContext;
    private final int maxServiceNameLength;
    private final String serviceName;

    @Getter
    private StartupStatus status = StartupStatus.WAITING;
    private LocalDateTime startedAt;
    private volatile LocalDateTime finishedAt;
    @Getter
    private JLabel label;
    private FutureWrapper<Void> futureWrapper;

    StartupService(StartupIndicatorContext startupIndicatorContext, int maxServiceNameLength, String serviceName) {
        this.startupIndicatorContext = startupIndicatorContext;
        this.maxServiceNameLength = maxServiceNameLength;
        this.serviceName = serviceName;
        label = new JLabel();
        label.setFont(new Font("Monospaced", Font.PLAIN, 16));
        label.setText(getLabelText());
    }

    public void startupInitiated() {
        status = StartupStatus.IN_PROGRESS;
        startedAt = LocalDateTime.now();
        SwingUtilities.invokeLater(() -> label.setText(getLabelText()));

        futureWrapper = startupIndicatorContext.getExecutorServiceBean()
            .execute(() -> {
                while (isNull(finishedAt)) {
                    startupIndicatorContext.getSleepService().sleep(50);

                    SwingUtilities.invokeLater(() -> label.setText(getLabelText()));
                }
            });
    }

    public void startupCompleted() {
        status = StartupStatus.STARTED;
        finishedAt = LocalDateTime.now();
        label.setText(getLabelText());
        SwingUtilities.invokeLater(() -> label.setText(getLabelText()));
    }

    private String getLabelText() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(withTrailingSpaces(serviceName, maxServiceNameLength));
        sb.append(" | ");
        sb.append(withTrailingSpaces(status.name(), StartupStatus.maxLength()));
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
}
