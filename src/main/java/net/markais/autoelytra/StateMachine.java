package net.markais.autoelytra;

interface State {
    void onStateEnter(StateMachine stateMachine, State previousState);
    void onStateUpdate(StateMachine stateMachine);
    void onStateExit(StateMachine stateMachine, State nextState);
}

public class StateMachine {
    private State currentState;

    public StateMachine(State initialState) {
        setState(initialState);
    }

    public State getState() {
        return currentState;
    }

    public void setState(State state) {
        State previousState = currentState;

        if (previousState != null)
            previousState.onStateExit(this, state);

        currentState = state;
        currentState.onStateEnter(this, previousState);
    }

    public void update() {
        if (currentState != null)
            currentState.onStateUpdate(this);
    }
}
