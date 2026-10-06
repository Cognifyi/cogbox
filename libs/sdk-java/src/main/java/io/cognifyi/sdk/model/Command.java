// Copyright Cogbox Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.cognifyi.sdk.model;

public class Command extends io.cognifyi.toolbox.client.model.Command {
    public Command() {}

    public Command(io.cognifyi.toolbox.client.model.Command source) {
        super();
        if (source != null) {
            setId(source.getId());
            setCommand(source.getCommand());
            setExitCode(source.getExitCode());
        }
    }
}
