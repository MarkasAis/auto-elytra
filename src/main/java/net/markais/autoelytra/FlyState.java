package net.markais.autoelytra;

import net.minecraft.util.math.Vec3d;

public enum FlyState implements State {
    IDLE {

        @Override
        public void onStateEnter(StateMachine stateMachine, State previousState) {
            System.out.println("IDLE");
        }

        @Override
        public void onStateUpdate(StateMachine stateMachine) { }

        @Override
        public void onStateExit(StateMachine stateMachine, State nextState) { }

    },
    LIFT_OFF {

        @Override
        public void onStateEnter(StateMachine stateMachine, State previousState) {
            System.out.println("LIFT OFF");

            if (handleOutOfResources(stateMachine)) return;

            if (!FlyManager.getInstance().hasCurrentWaypoint())
                stateMachine.setState(FlyState.IDLE);
        }

        @Override
        public void onStateUpdate(StateMachine stateMachine) {
            var config = AutoFlyConfig.getInstance();

            if (PlayerController.pitchTowardsAngle(config.getLiftOffPitch(), config.getRocketPitchSpeed())) {
                if (PlayerController.isGrounded()) {
                    PlayerController.jump();
                } else {
                    stateMachine.setState(FlyState.ASCENT_TO_SAFE_ALTITUDE);
                }
            }
        }

        @Override
        public void onStateExit(StateMachine stateMachine, State nextState) { }

    },
    ASCENT_TO_SAFE_ALTITUDE {

        private boolean liftOff = false;

        @Override
        public void onStateEnter(StateMachine stateMachine, State previousState) {
            System.out.println("ASCENT TO SAFE ALTITUDE");
            liftOff = (previousState == FlyState.LIFT_OFF);
        }

        @Override
        public void onStateUpdate(StateMachine stateMachine) {
            var config = AutoFlyConfig.getInstance();

            handleElytra();
            if (handleWaypoint(stateMachine)) return;

            if (!liftOff) {
                if (handleCriticalAltitude(stateMachine)) return;
                if (handleCollision(stateMachine)) return;
            }

            float ascentAngle = liftOff ? config.getLiftOffPitch() : config.getAscentPitch();

            if (PlayerController.pitchTowardsAngle(ascentAngle, config.getRocketPitchSpeed())) {
                if (PlayerController.getSpeed() < config.getMinRocketSpeed() || PlayerController.isFalling()) {
                    PlayerController.useRocket();
                }
            }

            if (PlayerController.getPosition().y >= config.getSafeAltitude())
                stateMachine.setState(FlyState.ASCENT);
        }

        @Override
        public void onStateExit(StateMachine stateMachine, State nextState) { }

    },
    ASCENT {

        @Override
        public void onStateEnter(StateMachine stateMachine, State previousState) {
            System.out.println("ASCENT");
        }

        @Override
        public void onStateUpdate(StateMachine stateMachine) {
            var config = AutoFlyConfig.getInstance();

            handleElytra();
            if (handleWaypoint(stateMachine)) return;
            if (handleCollision(stateMachine)) return;
            if (handleOutOfResources(stateMachine)) return;
            if (handleCriticalAltitude(stateMachine)) return;

            PlayerController.pitchTowardsAngle(config.getAscentPitch(), config.getAscentPitchSpeed());

            if (PlayerController.getSpeed() <= config.getMinAscentSpeed())
                stateMachine.setState(FlyState.DESCENT);
        }

        @Override
        public void onStateExit(StateMachine stateMachine, State nextState) { }

    },
    DESCENT {


        @Override
        public void onStateEnter(StateMachine stateMachine, State previousState) {
            System.out.println("DESCENT");
        }

        @Override
        public void onStateUpdate(StateMachine stateMachine) {
            var config = AutoFlyConfig.getInstance();

            handleElytra();
            if (handleWaypoint(stateMachine)) return;
            if (handleCollision(stateMachine)) return;
            if (handleOutOfResources(stateMachine)) return;
            if (handleCriticalAltitude(stateMachine)) return;

            PlayerController.pitchTowardsAngle(config.getDescentPitch(), config.getDescentPitchSpeed());

            if (PlayerController.getSpeed() >= config.getMaxDescentSpeed())
                stateMachine.setState(FlyState.ASCENT);
        }

        @Override
        public void onStateExit(StateMachine stateMachine, State nextState) { }

    },
    LANDING {

        @Override
        public void onStateEnter(StateMachine stateMachine, State previousState) {
            System.out.println("LANDING");
        }

        @Override
        public void onStateUpdate(StateMachine stateMachine) {
            var config = AutoFlyConfig.getInstance(); // TODO: fix hardcoded

            if (PlayerController.isGrounded()) {
                FlyManager.getInstance().completeCurrentWaypoint();
                stateMachine.setState(FlyState.IDLE);
                return;
            }

            yawTowardsWaypoint(90);

            handleElytra();

            PlayerController.pitchTowardsAngle(25, 10);
        }

        @Override
        public void onStateExit(StateMachine stateMachine, State nextState) { }

    };

    private static boolean handleOutOfResources(StateMachine stateMachine) {
        boolean hasElytras = PlayerController.hasSufficientElytras();
        boolean hasRockets = PlayerController.hasSufficientRockets();

        if (!hasElytras || !hasRockets) {
            stateMachine.setState(FlyState.IDLE);

            if (!PlayerController.isGrounded())
                PlayerController.disconnect("Out of resources!");

            return true;
        }

        return false;
    }

    private static void handleElytra() {
        PlayerController.equipElytra();

        if (!PlayerController.isFlying())
            PlayerController.activateElytra();
    }

    private static String getFormattedPosition() {
        Vec3d position = PlayerController.getPosition();

        return "(" + position.x + ", " + position.y + ", " + position.z + ")";
    }

    private static boolean handleCollision(StateMachine stateMachine) {
        if (PlayerController.isColliding()) {
            stateMachine.setState(FlyState.IDLE);

            PlayerController.disconnect("Collision! " + getFormattedPosition());

            return true;
        }

        return false;
    }

    private static boolean handleCriticalAltitude(StateMachine stateMachine) {
        var config = AutoFlyConfig.getInstance();

        if (PlayerController.getPosition().y <= config.getUnsafeAltitude()) {
            if (PlayerController.getPosition().y <= config.getCriticalAltitude()) {
                PlayerController.disconnect("Critical altitude! " + getFormattedPosition());

                return true;
            }

            if (stateMachine.getState() != FlyState.ASCENT_TO_SAFE_ALTITUDE) {
                stateMachine.setState(FlyState.ASCENT_TO_SAFE_ALTITUDE);

                return true;
            }
        }

        return false;
    }

    private static boolean handleWaypoint(StateMachine stateMachine) {
        if (!FlyManager.getInstance().hasCurrentWaypoint()) {
            stateMachine.setState(FlyState.IDLE);

            return true;
        }

        yawTowardsWaypoint();

        return handleArrival(stateMachine);
    }

    private static boolean handleArrival(StateMachine stateMachine) {
        var config = AutoFlyConfig.getInstance();
        var sequencer = FlyManager.getInstance();
        var waypoint = FlyManager.getInstance().getCurrentWaypoint();

        if (waypoint.squaredDistance(PlayerController.getX(), PlayerController.getZ()) < 3*3) { // TODO
            boolean last = sequencer.isLastWaypoint();

            if (config.shouldDisconnect(last)) {
                PlayerController.disconnect("Arrived at goal! " + getFormattedPosition());
                sequencer.completeCurrentWaypoint();
            } else if (config.shouldLand(last)) {
                stateMachine.setState(FlyState.LANDING);
            } else {
                sequencer.completeCurrentWaypoint();

                if (last)
                    stateMachine.setState(FlyState.IDLE);
            }

            return true;
        }

        return false;
    }

    private static void yawTowardsWaypoint(float speed) {
        Waypoint waypoint = FlyManager.getInstance().getCurrentWaypoint();
        float angle = PlayerController.getAngleTowards((float)waypoint.x, (float)waypoint.z);
        PlayerController.yawTowardsAngle(angle, speed);
    }

    private static void yawTowardsWaypoint() {
        yawTowardsWaypoint(10);
    }
}
