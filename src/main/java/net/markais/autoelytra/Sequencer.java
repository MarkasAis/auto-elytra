package net.markais.autoelytra;

import com.google.common.reflect.TypeToken;
import net.minecraft.text.LiteralText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sequencer extends PersistentResource {
    private transient static final String DEFAULT_FILE_PATH = "sequencer.json";
    private transient static Sequencer instance;

    private List<Waypoint> waypoints = new ArrayList<>();

    private Waypoint currentWaypoint;

    public static Sequencer getInstance() {
        if (instance == null)
            instance = (Sequencer) load(Sequencer.class, DEFAULT_FILE_PATH);

        return instance;
    }

    public void loadSequence(String filePath) {
        Object result = null;
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

    public void fly() {

    }

    public void fly(Waypoint waypoint) {
        currentWaypoint = waypoint;
    }
}
