// Copyright Cogbox Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.cognifyi.sdk.model;

public class SessionExecuteResponse extends io.cognifyi.toolbox.client.model.SessionExecuteResponse {
    public SessionExecuteResponse() {}

    public SessionExecuteResponse(io.cognifyi.toolbox.client.model.SessionExecuteResponse source) {
        super();
        if (source != null) {
            setCmdId(source.getCmdId());
            setOutput(source.getOutput());
            setStdout(source.getStdout());
            setStderr(source.getStderr());
            setExitCode(source.getExitCode());
        }
    }
}
