// Copyright Cogbox Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.cognifyi.sdk;

import io.cognifyi.sdk.exception.CogboxAuthenticationException;
import io.cognifyi.sdk.exception.CogboxBadRequestException;
import io.cognifyi.sdk.exception.CogboxConflictException;
import io.cognifyi.sdk.exception.CogboxConnectionException;
import io.cognifyi.sdk.exception.CogboxException;
import io.cognifyi.sdk.exception.CogboxForbiddenException;
import io.cognifyi.sdk.exception.CogboxNotFoundException;
import io.cognifyi.sdk.exception.CogboxRateLimitException;
import io.cognifyi.sdk.exception.CogboxServerException;
import io.cognifyi.sdk.exception.CogboxTimeoutException;
import io.cognifyi.sdk.exception.CogboxValidationException;

import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ExceptionMapper {
    private ExceptionMapper() {
    }

    static <T> T callMain(MainSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (io.cognifyi.api.client.ApiException e) {
            throw map(e.getCode(), e.getResponseBody(), e);
        }
    }

    static void runMain(MainRunnable runnable) {
        try {
            runnable.run();
        } catch (io.cognifyi.api.client.ApiException e) {
            throw map(e.getCode(), e.getResponseBody(), e);
        }
    }

    static <T> T callToolbox(ToolboxSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (io.cognifyi.toolbox.client.ApiException e) {
            throw map(e.getCode(), e.getResponseBody(), e);
        }
    }

    static void runToolbox(ToolboxRunnable runnable) {
        try {
            runnable.run();
        } catch (io.cognifyi.toolbox.client.ApiException e) {
            throw map(e.getCode(), e.getResponseBody(), e);
        }
    }

    static CogboxException map(int statusCode, String responseBody, Throwable cause) {
        // Only treat status==0 as a transport failure when the ApiException
        // wraps an underlying Throwable; client-side ApiExceptions thrown for
        // parameter validation also have status==0 but no wrapped cause.
        if (statusCode == 0 && (responseBody == null || responseBody.isEmpty())
                && cause != null && cause.getCause() != null) {
            return mapTransportFailure(cause);
        }
        String message = extractMessage(responseBody, statusCode);
        if (statusCode == 0 && (responseBody == null || responseBody.isEmpty())
                && cause != null && cause.getMessage() != null && !cause.getMessage().isEmpty()) {
            message = cause.getMessage();
        }
        switch (statusCode) {
            case 400:
                return new CogboxBadRequestException(message, cause);
            case 401:
                return new CogboxAuthenticationException(message, cause);
            case 403:
                return new CogboxForbiddenException(message, cause);
            case 404:
                return new CogboxNotFoundException(message, cause);
            case 409:
                return new CogboxConflictException(message, cause);
            case 422:
                return new CogboxValidationException(message, cause);
            case 429:
                return new CogboxRateLimitException(message, cause);
            default:
                if (statusCode >= 500) {
                    return new CogboxServerException(statusCode, message, cause);
                }
                return new CogboxException(statusCode, message, cause);
        }
    }

    private static CogboxException mapTransportFailure(Throwable cause) {
        Throwable root = rootCause(cause);
        String message = rootMessage(root);
        if (root instanceof SocketTimeoutException) {
            return new CogboxTimeoutException("Request timed out: " + message, cause);
        }
        return new CogboxConnectionException("Connection failed: " + message, cause);
    }

    private static Throwable rootCause(Throwable t) {
        Throwable current = t;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private static String rootMessage(Throwable t) {
        String msg = t.getMessage();
        if (msg != null && !msg.isEmpty()) {
            return msg;
        }
        return t.getClass().getSimpleName();
    }

    /**
     * Extracts a human-readable message from a raw JSON response body.
     * Looks for a "message" or "error" field; falls back to the raw body or a generic message.
     */
    private static String extractMessage(String responseBody, int statusCode) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "Request failed with status " + statusCode;
        }
        // Try to extract "message" field from JSON
        Matcher messageMatcher = Pattern.compile("\"message\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                .matcher(responseBody);
        if (messageMatcher.find()) {
            return messageMatcher.group(1);
        }
        // Try to extract "error" field from JSON
        Matcher errorMatcher = Pattern.compile("\"error\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                .matcher(responseBody);
        if (errorMatcher.find()) {
            return errorMatcher.group(1);
        }
        return responseBody;
    }

    @FunctionalInterface
    interface MainSupplier<T> {
        T get() throws io.cognifyi.api.client.ApiException;
    }

    @FunctionalInterface
    interface MainRunnable {
        void run() throws io.cognifyi.api.client.ApiException;
    }

    @FunctionalInterface
    interface ToolboxSupplier<T> {
        T get() throws io.cognifyi.toolbox.client.ApiException;
    }

    @FunctionalInterface
    interface ToolboxRunnable {
        void run() throws io.cognifyi.toolbox.client.ApiException;
    }
}