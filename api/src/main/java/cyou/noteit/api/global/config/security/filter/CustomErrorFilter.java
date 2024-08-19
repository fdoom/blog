package cyou.noteit.api.global.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import cyou.noteit.api.global.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomErrorFilter extends GenericFilterBean {
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (CustomException e) {
            handleCustomException(response, e);
        } catch (Exception e) {
            handleGenericException(response, e);
        }
    }

    private void handleCustomException(HttpServletResponse response, CustomException e) throws IOException {
        ErrorCode errorCode = e.getErrorCode();
        log.error("Filter CustomException caught: {} - {}", errorCode.getStatus(), errorCode.getMessage());

        response.setStatus(errorCode.getStatus().value());
        writeErrorResponse(response, errorCode.getStatus(), errorCode.getMessage());
    }

    private void handleGenericException(HttpServletResponse response, Exception e) throws IOException {
        log.error("Filter Exception caught: {}", e.getMessage(), e);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        writeErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(
                    ErrorResponse.builder()
                            .status(status)
                            .message(message)
                            .build()
            ));
            writer.flush();
        }
    }
}
