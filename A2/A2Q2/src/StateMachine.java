import java.util.ArrayList;

public class StateMachine{
    private State initialState;
    private State currentState;

    // Constructor
    public StateMachine(State initialState){
        this.initialState = initialState;
        currentState = initialState;
    }

    public ArrayList<String> update(){
        State targetState;
        ArrayList<String> actions;

        // Assume no transition is triggered
        Transition triggered = null;

        // Check through each transition and store the first one on that triggers
        for(Transition transition : currentState.getTransitions()){
            if(transition.isTriggered()){
                triggered = transition;
                break;
            }
        }

        if(triggered != null){
            // find the target state
            targetState = triggered.getTargetState();

            // add the exit action of the old state, the transition action and the entry for the new state
            actions = currentState.getExitActions();
            actions.addAll(triggered.getActions());
            actions.addAll(targetState.getEntryActions());

            // complete the transition and return the action list
            currentState = targetState;
        }
        // otherwise just return the current state's action
        else{
            actions = currentState.getActions();
        }

        return actions;
    }

    public State getCurrentState() {
        return currentState;
    }
}
