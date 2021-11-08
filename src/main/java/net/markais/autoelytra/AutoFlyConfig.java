package net.markais.autoelytra;

enum InteractionMode {
    NEVER, ALWAYS, LAST
};

public class AutoFlyConfig extends PersistentResource {
    private transient static final String DEFAULT_FILE_PATH = "auto_fly_config.json";
    private transient static AutoFlyConfig instance;

    private InteractionMode landingMode = InteractionMode.LAST;
    private InteractionMode disconnectMode = InteractionMode.NEVER;

    public static AutoFlyConfig getInstance() {
        if (instance == null)
            instance = (AutoFlyConfig) load(AutoFlyConfig.class, DEFAULT_FILE_PATH);

        return instance;
    }

    public InteractionMode getLandingMode() {
        return landingMode;
    }

    public void setLandingMode(InteractionMode mode) {
        this.landingMode = mode;
        save();
    }

    public InteractionMode getDisconnectMode() {
        return disconnectMode;
    }

    public void setDisconnectMode(InteractionMode mode) {
        this.disconnectMode = mode;
        save();
    }
}
