package net.markais.autoelytra;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class PlayerController {
    private static final int CHESTPLATE_SLOT = 38;
    private static final int TICKS_BETWEEN_ROCKETS = 3;

    private static Vec3d previousPosition;
    private static Vec3d currentVelocity;
    private static double currentSpeed;
    private static int ticksSinceRocket = 0;

    private static ClientWorld world;
    private static ClientPlayerEntity player;
    private static ClientPlayerInteractionManager interactionManager;
    private static ClientPlayNetworkHandler networkHandler;

    public static void connect() {
        MinecraftClient client = MinecraftClient.getInstance();
        world = client.world;
        player = client.player;
        interactionManager = client.interactionManager;
        networkHandler = client.getNetworkHandler();
    }

    public static double getX() {
        return player.getX();
    }

    public static double getY() { return player.getY(); }

    public static double getZ() { return player.getZ(); }

    public static boolean isFalling() {
        return currentVelocity.y < 0;
    }

    public static boolean isGrounded() {
        return player.isOnGround();
    }

    public static boolean isColliding() {
        return player.verticalCollision || player.horizontalCollision;
    }

    public static boolean isFlying() {
        return player.isFallFlying();
    }

    public static double getSpeed() {
        return currentSpeed;
    }

    public static Vec3d getVelocity() {
        return currentVelocity;
    }

    public static Vec3d getPosition() {
        return player.getPos();
    }

    public static void jump() {
        player.jump();
    }

    public static void update() {
        computeVelocity();
        ticksSinceRocket++;

//        equipElytra(); // TODO: ??
    }

    private static void computeVelocity() {
        Vec3d newPosition = player.getPos();

        if (previousPosition == null)
            previousPosition = newPosition;

        currentVelocity = newPosition.subtract(previousPosition);
        currentSpeed = (float) currentVelocity.length();

        previousPosition = newPosition;
    }

    public static boolean pitchTowardsAngle(float angle, float speed) {
        float angleDifference = angle - player.getPitch();
        int direction = (int) Math.signum(angleDifference);

        if (Math.abs(angleDifference) < speed) {
            player.setPitch(angle);
            return true;
        }

        player.setPitch(player.getPitch() + direction * speed);

        return false;
    }

    public static boolean yawTowardsAngle(float angle, float speed) {
        // Get the direction for the shortest rotation
        float angleDifference = (angle - player.getYaw() + 540) % 360 - 180;
        int direction = (int) Math.signum(angleDifference);

        if (Math.abs(angleDifference) <= speed) {
            player.setYaw(angle);
            return true;
        }

        player.setYaw(player.getYaw() + direction * speed);

        return false;
    }

    public static float getAngleTowards(float x, float z) {
        float deltaX = x - (float) player.getPos().x;
        float deltaZ = z - (float) player.getPos().z;

        return (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
    }

    public static void activateElytra() {
        var packet = new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING);
        networkHandler.sendPacket(packet);
    }

    public static Map<Integer, ItemStack> getItems(Item item) {
        PlayerInventory inventory = player.getInventory();

        Map<Integer, ItemStack> items = new HashMap<>();

        for (int i = 0; i < 9 * 5; i++) {
            ItemStack stack = inventory.getStack(i);

            if (stack.getItem() == item)
                items.put(i, stack);
        }

        return items;
    }

    private static void swapItems(int from, int to) {
        interactionManager.clickSlot(player.playerScreenHandler.syncId, from < 9 ? from + 36 : from, to, SlotActionType.SWAP, player);
    }

    private static void selectSlot(int slot) {
        PlayerInventory inventory = player.getInventory();
        inventory.selectedSlot = slot;
    }

    private static boolean selectRocket() {
        PlayerInventory inventory = player.getInventory();

        // Check if there are no rockets in the correct slot
        if (inventory.getStack(AutoFlyConfig.getInstance().getRocketSlot()).getItem() != Items.FIREWORK_ROCKET) {

            // Find rockets in inventory
            Map<Integer, ItemStack> rockets = getItems(Items.FIREWORK_ROCKET);

            if (rockets.isEmpty()) return false;

            // Put the rockets in the correct slot
            int slot = rockets.keySet().iterator().next();
            swapItems(slot, AutoFlyConfig.getInstance().getRocketSlot());
        }

        // Select the slot
        selectSlot(AutoFlyConfig.getInstance().getRocketSlot());

        return true;
    }

    private static int calculateDurability(ItemStack item) {
        return item.getMaxDamage() - item.getDamage();
    }

    public static boolean equipElytra() {
        PlayerInventory inventory = player.getInventory();

        ItemStack chestplate = inventory.getStack(CHESTPLATE_SLOT);

        // Check if a durable elytra is already equipped
        if (chestplate.getItem() == Items.ELYTRA
                && calculateDurability(chestplate) > AutoFlyConfig.getInstance().getElytraChangeDurability())
            return true;

        // Find and equip a durable elytra
        Map<Integer, ItemStack> elytras = getItems(Items.ELYTRA);

        for (Map.Entry<Integer, ItemStack> entry : elytras.entrySet()) {
            int slot = entry.getKey();
            ItemStack stack = entry.getValue();

            int durability = stack.getMaxDamage() - stack.getDamage();

            if (durability > AutoFlyConfig.getInstance().getElytraChangeDurability()) {
                swapItems(slot, CHESTPLATE_SLOT);
                return true;
            }
        }

        return false;
    }

    public static boolean useRocket() {
        if (ticksSinceRocket < TICKS_BETWEEN_ROCKETS)
            return false;

        if (!selectRocket())
            return false;

        interactionManager.interactItem(player, world, Hand.MAIN_HAND);
        ticksSinceRocket = 0;

        return true;
    }

    public static boolean hasSufficientRockets() {
        Map<Integer, ItemStack> rockets = getItems(Items.FIREWORK_ROCKET);

        int count = 0;

        for (Map.Entry<Integer, ItemStack> entry : rockets.entrySet()) {
            ItemStack stack = entry.getValue();
            count += stack.getCount();
        }

        return count > AutoFlyConfig.getInstance().getMinRocketCount();
    }

    public static boolean hasSufficientElytras() {
        Map<Integer, ItemStack> elytras = getItems(Items.ELYTRA);

        int totalDurability = 0;

        for (Map.Entry<Integer, ItemStack> entry : elytras.entrySet()) {
            ItemStack stack = entry.getValue();

            int durability = calculateDurability(stack);
            durability = Math.max(0, durability - AutoFlyConfig.getInstance().getElytraChangeDurability());
            totalDurability += durability;
        }

        return totalDurability > AutoFlyConfig.getInstance().getMinElytraDurability();
    }

    public static void disconnect(String text) {
        var packet = new DisconnectS2CPacket(new LiteralText(text));
        networkHandler.onDisconnect(packet);
    }
}
