package fr.bretzel.quake.error;

import fr.bretzel.quake.api.IConfig;

public class InvalidConfiguration extends RuntimeException {

    private IConfig config;
    private String reg;

    public InvalidConfiguration(IConfig config, String reg) {
        super("An error is detected for config: " + config.getFile() + " failed to load '" + reg + "'.");
        this.config = config;
        this.reg = reg;
    }

    public IConfig getConfig() {
        return config;
    }

    public String getReg() {
        return reg;
    }
}
