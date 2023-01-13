package com.github.saphyra.apphub.service.platform.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class FileUploadHelper {
    ServletFileUpload servletFileUpload() {
        return new ServletFileUpload();
    }
}
