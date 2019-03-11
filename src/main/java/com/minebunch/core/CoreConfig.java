package com.minebunch.core;

import com.minebunch.core.utils.storage.Config;
import lombok.Getter;

@Getter
public class CoreConfig extends Config {
    private String serverName;
    private String siteName;

    public CoreConfig(CorePlugin core) {
        super(core, "config");
        load();
    }

    public void load() {
        serverName = getString("server.name");
        siteName = getString("server.site");
    }
}
