package com.evofun.userservice.shared.exception.util;

import com.evofun.userservice.shared.exception.code.ErrorPrefix;
import com.evofun.userservice.shared.exception.model.FieldErrorDto;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExceptionUtils {
    private static final String SERVICE_PREFIX = "USR";

    public static List<FieldErrorDto> extractFieldErrors(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(v -> new FieldErrorDto(
                        v.getPropertyPath().toString(),
                        v.getMessage()
                ))
                .collect(Collectors.toList());
    }

    public static String generateErrorId(ErrorPrefix prefix) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String shortUuid = UUID.randomUUID().toString().substring(0, 6);

        return SERVICE_PREFIX + "-" + prefix.toString() + "-" + timestamp + "-" + shortUuid;
    }
}