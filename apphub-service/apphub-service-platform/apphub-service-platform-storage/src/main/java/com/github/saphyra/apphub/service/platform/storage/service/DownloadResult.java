package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
import lombok.Builder;
import lombok.Value;

import java.io.InputStream;

@Value
@Builder
public class DownloadResult {
    InputStream inputStream;
    StoredFile storedFile;
    FtpClientWrapper ftpClient;
}
