package net.markais.autoelytra;

import com.google.common.reflect.TypeToken;
import net.minecraft.text.LiteralText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlyManager extends PersistentResource {
    private transient static final String DEFAULT_FILE_PATH = "sequencer.json";
    private transient static FlyManager instance;

    private StateMachine stateMachine = new StateMachine(FlyState.IDLE);

    private enum FollowMode { NEAREST, ITERATIVE }
    private FollowMode followMode = FollowMode.ITERATIVE;

    private enum ActiveState { STOPPED, PAUSED, ACTIVE }
    private ActiveState activeState = ActiveState.STOPPED;

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
    }

    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
        ChatCommands.sendPrivateMessage(new LiteralText("Waypoint added!"));
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
//        switch (followMode) {
//            case ITERATIVE: return waypoints.get(0);
//            case NEAREST: return waypoints.stream().sorted((a, b) -> {return 1;}).
//        };
        return null;
    }

    public Waypoint getCurrentWaypoint() {
        if (isSequence && currentWaypoint == null)
            currentWaypoint = selectNextWaypoint();

        return currentWaypoint;
    }

    public boolean hasCurrentWaypoint() {
        return getCurrentWaypoint() != null;
    }

    public boolean isLastWaypoint() {
        if (!isSequence) return false;
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

    public void fly(String name) {
        Waypoint waypoint = WaypointLibrary.getInstance().getWaypoint(name);
        fly(waypoint);
    }

    public void fly(Waypoint waypoint) {
        if (waypoint == null) return;

        currentWaypoint = waypoint;
        isSequence = false;
        stateMachine.setState(FlyState.LIFT_OFF);
    }

    public void flySequence() {
        currentWaypoint = null;
        isSequence = true;
        stateMachine.setState(FlyState.LIFT_OFF);
    }

    public void update() {
        if (activeState == ActiveState.ACTIVE)
            stateMachine.update();
    }

    public void pause() {
        if (activeState != ActiveState.ACTIVE) {
            ChatCommands.sendPrivateMessage(new LiteralText("No flight to be paused."));
            return;
        }

        activeState = ActiveState.PAUSED;
    }

    public void stop() {
        if (activeState == ActiveState.STOPPED) {
            ChatCommands.sendPrivateMessage(new LiteralText("No flight to be stopped."));
            return;
        }

        completeCurrentWaypoint(false);
        activeState = ActiveState.STOPPED;
    }

    public void resume() {
        if (activeState != ActiveState.PAUSED) {
            ChatCommands.sendPrivateMessage(new LiteralText("No flight has been paused."));
            return;
        }

        activeState = ActiveState.ACTIVE;
        ChatCommands.sendPrivateMessage(new LiteralText(String.format("Flight resumed: %s", currentWaypoint)));
    }
}
