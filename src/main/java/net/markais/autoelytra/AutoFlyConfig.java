package net.markais.autoelytra;

public class AutoFlyConfig extends PersistentResource {
    private static final String DEFAULT_FILE_PATH = "auto_fly_config.json";
    private static AutoFlyConfig instance;

    protected int test = 10;

    public static AutoFlyConfig getInstance() {
        if (instance == null)
            instance = (AutoFlyConfig) load(AutoFlyConfig.class, DEFAULT_FILE_PATH);

        return instance;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
        save();
    }
}
