package net.markais.autoelytra;

public class Waypoint {
    public int x;
    public int z;
    public String name;

    public Waypoint(int x, int z, String name) {
        this.x = x;
        this.z = z;
        this.name = name;
    }

    public Waypoint(int x, int z) {
        this(x, z, null);
    }

    public double squaredDistance(int x, int z) {
        int dx = this.x - x;
        int dz = this.z - z;

        return dx*dx + dz*dz;
    }

    public String toString() {
        if (name != null) return String.format("%s (%d, %d)", name, x, z);
        return String.format("(%d, %d)", x, z);
    }
}
