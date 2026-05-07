package com.github.saphyra.apphub.service.platform.storage.client;

import com.github.saphyra.apphub.service.platform.storage.client.ftp.FtpClientWrapper;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.nonNull;

@Value
@Builder
public class DownloadResult implements Closeable {
    InputStream inputStream;
    @Nullable
    FtpClientWrapper ftpClient;

    @Override
    public void close() throws IOException {
        inputStream.close();
        if (nonNull(ftpClient)) {
            ftpClient.close();
        }
    }
}
