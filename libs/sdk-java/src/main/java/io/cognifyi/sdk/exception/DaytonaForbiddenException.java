// Copyright Cogbox Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.cognifyi.sdk.exception;

/**
 * Raised when the authenticated user lacks permission to perform an operation (HTTP 403).
 *
 * <pre>{@code
 * try {
 *     daytona.sandbox().delete(sandboxId);
 * } catch (CogboxForbiddenException e) {
 *     System.err.println("Not authorized to delete this sandbox");
 * }
 * }</pre>
 */
public class CogboxForbiddenException extends CogboxException {
    /**
     * Creates a forbidden exception.
     *
     * @param message error description from the API
     */
    public CogboxForbiddenException(String message) {
        super(403, message);
    }

    /**
     * @param message error description from the API
     * @param cause root cause
     */
    public CogboxForbiddenException(String message, Throwable cause) {
        super(403, message, cause);
    }
}
