package net.markais.autoelytra;

import net.minecraft.text.LiteralText;

import java.util.Map;
import java.util.TreeMap;

public class WaypointLibrary extends PersistentResource {
    private transient static final String DEFAULT_FILE_PATH = "waypoint_library.json";
    private transient static WaypointLibrary instance;

    private final Map<String, Waypoint> waypoints = new TreeMap<>();

    public static WaypointLibrary getInstance() {
        if (instance == null)
            instance = (WaypointLibrary) load(WaypointLibrary.class, DEFAULT_FILE_PATH);

        return instance;
    }

    public void addWaypoint(Waypoint waypoint) {
        if (waypoints.containsKey(waypoint.name)) {
            ChatCommands.sendPrivateMessage(new LiteralText("Waypoint with that name already exists!"));
        } else {
            waypoints.put(waypoint.name, waypoint);
            ChatCommands.sendPrivateMessage(new LiteralText(String.format("Waypoint %s added!", waypoint)));
        }
        save();
    }

    public void removeWaypoint(String name) {
        Waypoint waypoint = waypoints.remove(name);
        if (waypoint != null) {
            ChatCommands.sendPrivateMessage(new LiteralText(String.format("Waypoint %s removed!", waypoint)));
        } else {
            ChatCommands.sendPrivateMessage(new LiteralText("Waypoint with that name does not exists!"));
        }
        save();
    }

    public Waypoint getWaypoint(String name) {
        Waypoint waypoint = waypoints.get(name);

        if (waypoint == null) {
            ChatCommands.sendPrivateMessage(new LiteralText("Waypoint with that name does not exists!"));
        }

        return waypoint;
    }

    public void listWaypoints() {
        if (waypoints.isEmpty()) {
            ChatCommands.sendPrivateMessage(new LiteralText("You do not have any waypoints!"));
        } else {
            ChatCommands.sendPrivateMessage(new LiteralText("Your Waypoints:"));
            waypoints.forEach((name, waypoint) -> {
                ChatCommands.sendPrivateMessage(new LiteralText(String.format(" - %s", waypoint)));
            });
        }
    }
}
