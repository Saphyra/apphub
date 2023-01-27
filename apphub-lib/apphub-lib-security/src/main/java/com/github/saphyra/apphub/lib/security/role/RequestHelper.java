package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class RequestHelper {
    private final ObjectMapperWrapper objectMapperWrapper;

    public boolean isRestCall(HttpServletRequest request) {
        String header = request.getHeader(Constants.REQUEST_TYPE_HEADER);
        return Constants.REQUEST_TYPE_VALUE.equals(header);
    }

    public void sendRestError(HttpServletResponse response, HttpStatus status, ErrorResponse errorResponse) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapperWrapper.writeValueAsString(errorResponse));
        writer.flush();
        writer.close();
    }
}
