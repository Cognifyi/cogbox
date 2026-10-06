// Copyright Cogbox Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.cognifyi.sdk.model;

public class Session extends io.cognifyi.toolbox.client.model.Session {
    public Session() {}

    public Session(io.cognifyi.toolbox.client.model.Session source) {
        super();
        if (source != null) {
            setSessionId(source.getSessionId());
            setCommands(source.getCommands());
        }
    }
}
