package com.github.saphyra.apphub.service.platform.storage.ftp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FtpClientWrapper implements AutoCloseable {
    private final FTPClient client;

    @SneakyThrows
    public FtpClientWrapper(FtpClientProperties properties) {
        FTPClient client = new FTPClient();

        client.connect(properties.getServer(), properties.getPort());

        client.enterLocalPassiveMode();

        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new IOException("Exception in connecting to FTP Server. ReplyCode: " + reply);
        }

        boolean loginSuccess = client.login(properties.getUsername(), properties.getPassword());

        if (!loginSuccess) {
            throw new RuntimeException("FTP login failed.");
        }

        this.client = client;
    }

    @SneakyThrows
    public InputStream downloadFile(String fileName) {
        client.setFileType(FTP.BINARY_FILE_TYPE);
        return client.retrieveFileStream(fileName);
    }

    @SneakyThrows
    public void storeFile(String fileName, InputStream file) {
        client.setFileType(FTP.BINARY_FILE_TYPE);
        try (file) {
            client.storeFile(fileName, file);
        }
    }

    @SneakyThrows
    public void deleteFile(String fileName) {
        client.deleteFile(fileName);
    }

    @SneakyThrows
    @Override
    public void close() {
        client.disconnect();
    }

    @SneakyThrows
    public List<FTPFile> listFiles(String path) {
        return Arrays.asList(client.listFiles(path));
    }
}
