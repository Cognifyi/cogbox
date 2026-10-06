// Copyright Cogbox Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.cognifyi.sdk;

/**
 * Progress information for a streaming upload.
 */
public final class UploadProgress {
    private final long bytesSent;

    public UploadProgress(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    /** Cumulative bytes sent so far. */
    public long getBytesSent() {
        return bytesSent;
    }
}
