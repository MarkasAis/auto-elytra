package net.markais.autoelytra;

enum InteractionMode { NEVER, ALWAYS, LAST };
enum FollowMode { NEAREST, ITERATIVE }

public class AutoFlyConfig extends PersistentResource {
    private transient static final String DEFAULT_FILE_PATH = "auto_fly_config.json";
    private transient static AutoFlyConfig instance;

    private InteractionMode landingMode = InteractionMode.LAST;
    private InteractionMode disconnectMode = InteractionMode.NEVER;
    private FollowMode followMode = FollowMode.ITERATIVE;

    private int liftOffAltitude = 250;
    private int unsafeAltitude = 140;
    private int disconnectAltitude = 100;

    private int rocketSlot = 2;
    private int minRocketCount = 16;
    private int elytraChangeDurability = 5;
    private int minElytraDurability = 50;
    private float yawSpeed = 10;

    private float liftOffPitch = -90;
    private float rocketPitchSpeed = 10;
    private float minRocketSpeed = 0.1f;

    class AutoFlyParams {
        float ascentPitch = -60.83442666699369f;
        float ascentPitchSpeed = 5.652967917242289f;
        float minAscentSpeed = 1.4437164811489673f;

        float descentPitch = 32.39337937794761f;
        float descentPitchSpeed = 0.6294351653481497f;
        float maxDescentSpeed = 1.5813419060751501f;
    }
    private AutoFlyParams autoFlyParams = new AutoFlyParams();

    public static AutoFlyConfig getInstance() {
        if (instance == null)
            instance = (AutoFlyConfig) load(AutoFlyConfig.class, DEFAULT_FILE_PATH);

        return instance;
    }

    public FollowMode getFollowMode() {
        return followMode;
    }

    public void setFollowMode(FollowMode mode) {
        this.followMode = mode;
        save();
        FlyManager.getInstance().recalculateCurrentWaypoint();
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

    public int getLiftOffAltitude() { return liftOffAltitude; }

    public void setLiftOffAltitude(int altitude) {
        liftOffAltitude = altitude;
        save();
    }

    public int getUnsafeAltitude() { return unsafeAltitude; }

    public void setUnsafeAltitude(int altitude) {
        unsafeAltitude = altitude;
        save();
    }

    public int getDisconnectAltitude() { return disconnectAltitude; }

    public void setDisconnectAltitude(int altitude) {
        disconnectAltitude = altitude;
        save();
    }

    public int getRocketSlot() { return rocketSlot; }

    public void setRocketSlot(int slot) {
        rocketSlot = slot;
        save();
    }

    public int getElytraChangeDurability() { return elytraChangeDurability; }

    public void setElytraChangeDurability(int durability) {
        elytraChangeDurability = durability;
        save();
    }

    public int getMinRocketCount() { return minRocketCount; }

    public void setMinRocketCount(int count) {
        minRocketCount = count;
        save();
    }

    public int getMinElytraDurability() { return minElytraDurability; }

    public void setMinElytraDurability(int durability) {
        minElytraDurability = durability;
        save();
    }

    public float getYawSpeed() { return yawSpeed; }

    public void setYawSpeed(float speed) {
        yawSpeed = speed;
        save();
    }

    public float getLiftOffPitch() { return liftOffPitch; }

    public float getRocketPitchSpeed() { return rocketPitchSpeed; }

    public float getMinRocketSpeed() { return minRocketSpeed; }

    public float getAscentPitch() { return autoFlyParams.ascentPitch; }

    public float getAscentPitchSpeed() { return autoFlyParams.ascentPitchSpeed; }

    public float getMinAscentSpeed() { return autoFlyParams.minAscentSpeed; }

    public float getDescentPitch() { return autoFlyParams.descentPitch; }

    public float getDescentPitchSpeed() { return autoFlyParams.descentPitchSpeed; }

    public float getMaxDescentSpeed() { return autoFlyParams.maxDescentSpeed; }

    public boolean shouldDisconnect(boolean isLast) {
        return disconnectMode == InteractionMode.ALWAYS || (disconnectMode == InteractionMode.LAST && isLast);
    }

    public boolean shouldLand(boolean isLast) {
        return landingMode == InteractionMode.ALWAYS || (landingMode == InteractionMode.LAST && isLast);
    }
}
