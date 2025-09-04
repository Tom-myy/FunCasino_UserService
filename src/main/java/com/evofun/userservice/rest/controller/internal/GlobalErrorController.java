/*
package com.evofun.userservice.rest.controller.internal;

import com.evofun.userservice.common.error.ErrorCode;
import com.evofun.userservice.common.error.ErrorDto;
import com.evofun.userservice.common.error.ExceptionUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//TODO use it if wanna see 404\405 as обычные HTTP-ошибки, а не исключениями...

@RestController
public class GlobalErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorDto> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String originalUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (status != null && Integer.parseInt(status.toString()) == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDto(
                            ErrorCode.RESOURCE_NOT_FOUND,
                            null,
                            "Endpoint not found with request URI '" + originalUri + "'.",
                            null
                    ));
        }

        if (status != null && Integer.parseInt(status.toString()) == 405) {
            return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(new ErrorDto(
                            ErrorCode.METHOD_NOT_SUPPORTED,
                            null,
                            "Method '" + request.getMethod() + "' not supported for this endpoint.",
                            null)
                    );
        }

        String errorId = ExceptionUtils.generateErrorId("UNKNOWN");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(
                        ErrorCode.UNKNOWN_ERROR,
                        errorId,
                        "Internal server error. Please contact support.",
                        null
                ));
    }
}*/
