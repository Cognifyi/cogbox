// Copyright Cogbox Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.cognifyi.sdk.model;

public class FileInfo extends io.cognifyi.toolbox.client.model.FileInfo {
    public FileInfo() {}

    public FileInfo(io.cognifyi.toolbox.client.model.FileInfo source) {
        super();
        if (source != null) {
            setName(source.getName());
            setSize(source.getSize());
            setMode(source.getMode());
            setModTime(source.getModTime());
            setIsDir(source.getIsDir());
        }
    }
}
