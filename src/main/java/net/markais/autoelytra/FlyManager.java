package net.markais.autoelytra;

import com.google.common.reflect.TypeToken;
import net.minecraft.text.LiteralText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlyManager extends PersistentResource {
    private transient static final String DEFAULT_FILE_PATH = "sequencer.json";
    private transient static FlyManager instance;

    private transient final StateMachine stateMachine = new StateMachine(FlyState.IDLE);

//    private enum ActiveState { STOPPED, PAUSED, ACTIVE }
//    private ActiveState activeState = ActiveState.STOPPED;

    private boolean isSequence = false;
    private List<Waypoint> waypoints = new ArrayList<>();
    private Waypoint currentWaypoint;

    public static FlyManager getInstance() {
        if (instance == null)
            instance = (FlyManager) load(FlyManager.class, DEFAULT_FILE_PATH);

        return instance;
    }

    public void loadSequence(String filePath) {
        try {
            waypoints = (List<Waypoint>) Utils.load(new TypeToken<List<Waypoint>>(){}.getType(), filePath);
            ChatCommands.sendPrivateMessage(new LiteralText("Waypoint sequence loaded!"));
            save();
        } catch (IOException e) {
            ChatCommands.sendPrivateMessage(new LiteralText("Waypoint sequence could not be loaded!"));
        }
    }

    public void saveSequence(String filePath) {
        try {
            Utils.save(waypoints, filePath);
            ChatCommands.sendPrivateMessage(new LiteralText("Waypoint sequence saved!"));
        } catch (IOException e) {
            ChatCommands.sendPrivateMessage(new LiteralText("Waypoint sequence could not be saved!"));
        }
    }

    public void clearSequence() {
        waypoints = new ArrayList<>();
        ChatCommands.sendPrivateMessage(new LiteralText("Waypoint sequence cleared!"));
        save();
    }

    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
        ChatCommands.sendPrivateMessage(new LiteralText("Waypoint added!"));
        save();
    }

    public void addWaypoint(String name) {
        Waypoint waypoint = WaypointLibrary.getInstance().getWaypoint(name);
        if (waypoint != null) addWaypoint(waypoint);
        else ChatCommands.sendPrivateMessage(new LiteralText("Waypoint not found!"));
    }

    public void listWaypoints() {
        if (waypoints.isEmpty()) {
            ChatCommands.sendPrivateMessage(new LiteralText("There are no waypoints in the sequence!"));
        } else {
            ChatCommands.sendPrivateMessage(new LiteralText("Waypoint sequence:"));
            waypoints.forEach(waypoint -> {
                ChatCommands.sendPrivateMessage(new LiteralText(String.format(" - %s", waypoint)));
            });
        }
    }

    private Waypoint selectNextWaypoint() {
        if (waypoints.isEmpty()) return null;

        return switch (AutoFlyConfig.getInstance().getFollowMode()) {
            case ITERATIVE -> waypoints.get(0);
            case NEAREST -> waypoints.stream().min((a, b) -> {
                double x = PlayerController.getX();
                double z = PlayerController.getZ();
                return (int) Math.signum(a.squaredDistance(x, z) - b.squaredDistance(x, z));
            }).orElse(null);
        };
    }

    public Waypoint getCurrentWaypoint() {
        if (currentWaypoint == null)
            recalculateCurrentWaypoint();

        return currentWaypoint;
    }

    public void recalculateCurrentWaypoint() {
        if (isSequence) currentWaypoint = selectNextWaypoint();
        save();

        printCurrentWaypoint();
    }

    public boolean hasCurrentWaypoint() {
        return getCurrentWaypoint() != null;
    }

    public boolean isLastWaypoint() {
        if (!isSequence) return true;
        return waypoints.size() <= 1;
    }

    public void completeCurrentWaypoint(boolean success) {
        if (isSequence) {
            waypoints.remove(currentWaypoint);
        }

        if (success) ChatCommands.sendPrivateMessage(new LiteralText(String.format("Waypoint reached: %s", currentWaypoint)));
        else ChatCommands.sendPrivateMessage(new LiteralText(String.format("Flight canceled to: %s", currentWaypoint)));
        currentWaypoint = null;
    }

    public void printCurrentWaypoint() {
        ChatCommands.sendPrivateMessage(new LiteralText(String.format("Current Waypoint: %s", getCurrentWaypoint())));
    }

    public void fly(String name) {
        Waypoint waypoint = WaypointLibrary.getInstance().getWaypoint(name);
        fly(waypoint);
    }

    public void fly(Waypoint waypoint) {
        if (waypoint == null) return;

        currentWaypoint = waypoint;
        isSequence = false;
        stateMachine.setState(FlyState.LIFT_OFF);

        printCurrentWaypoint();
    }

    public void flySequence() {
        currentWaypoint = null;
        isSequence = true;
        stateMachine.setState(FlyState.LIFT_OFF);
    }

    public void update() {
        stateMachine.update();
    }

    public void pause() {
        if (stateMachine.getState() == FlyState.IDLE) {
            ChatCommands.sendPrivateMessage(new LiteralText("No flight to be paused."));
            return;
        }

        stateMachine.setState(FlyState.IDLE);
    }

    public void cancel() {
        if (stateMachine.getState() == FlyState.IDLE) {
            ChatCommands.sendPrivateMessage(new LiteralText("No active flight to be canceled."));
            return;
        }

        completeCurrentWaypoint(false);
        stateMachine.setState(FlyState.IDLE);
    }

    public void resume() {
        if (stateMachine.getState() != FlyState.IDLE || getCurrentWaypoint() == null) {
            ChatCommands.sendPrivateMessage(new LiteralText("No flight has been paused."));
            return;
        }

        stateMachine.setState(FlyState.LIFT_OFF);
        ChatCommands.sendPrivateMessage(new LiteralText(String.format("Flight resumed: %s", currentWaypoint)));
    }
}
