package net.markais.autoelytra;

enum InteractionMode {
    NEVER, ALWAYS, LAST
};

public class AutoFlyConfig extends PersistentResource {
    private transient static final String DEFAULT_FILE_PATH = "auto_fly_config.json";
    private transient static AutoFlyConfig instance;

    private InteractionMode landingMode = InteractionMode.LAST;
    private InteractionMode disconnectMode = InteractionMode.NEVER;

    private int rocketSlot = 2;
    private int minRocketCount = 16;
    private int elytraChangeDurability = 5;
    private int minElytraDurability = 50;

    private float liftOffPitch = -90;
    private float rocketPitchSpeed = 10;
    private float minRocketSpeed = 0.1f;

    private int safeAltitude = 250;

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

    public int getRocketSlot() { return rocketSlot; }

    public int getElytraChangeDurability() { return elytraChangeDurability; }

    public int getMinRocketCount() { return minRocketCount; }

    public int getMinElytraDurability() { return minElytraDurability; }

    public float getLiftOffPitch() { return liftOffPitch; }

    public float getRocketPitchSpeed() { return rocketPitchSpeed; }

    public float getMinRocketSpeed() { return minRocketSpeed; }

    public int getSafeAltitude() { return safeAltitude; }

    public float getAscentPitch() { return -60.83442666699369f; }

    public float getAscentPitchSpeed() { return 5.652967917242289f; }

    public float getMinAscentSpeed() { return 1.4437164811489673f; }

    public float getDescentPitch() { return 32.39337937794761f; }

    public float getDescentPitchSpeed() { return 0.6294351653481497f; }

    public float getMaxDescentSpeed() { return 1.5813419060751501f; }

    public int getUnsafeAltitude() { return 140; }

    public int getCriticalAltitude() { return 100; }

    public boolean shouldDisconnect(boolean isLast) {
        return disconnectMode == InteractionMode.ALWAYS || (disconnectMode == InteractionMode.LAST && isLast);
    }

    public boolean shouldLand(boolean isLast) {
        return landingMode == InteractionMode.ALWAYS || (landingMode == InteractionMode.LAST && isLast);
    }
}
