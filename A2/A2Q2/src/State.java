import java.util.ArrayList;
import java.util.Arrays;

public class State {
    private ArrayList<String> actions;
    private ArrayList<String> entryActions;
    private ArrayList<String> exitActions;
    private ArrayList<Transition> transitions;

    // Constructor
    public State(String[] actions, String[] entryActions, String[] exitActions, Transition[] transitions){
        this.actions = new ArrayList<>(Arrays.asList(actions));
        this.entryActions = new ArrayList<>(Arrays.asList(entryActions));
        this.exitActions = new ArrayList<>(Arrays.asList(exitActions));
        this.transitions = new ArrayList<>(Arrays.asList(transitions));
    }

    // Accessors
    public ArrayList<String> getActions() {
        return actions;
    }

    public ArrayList<String> getEntryActions() {
        return entryActions;
    }

    public ArrayList<String> getExitActions() {
        return exitActions;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public Transition getTransition(Transition transition) {
        for (Transition t : transitions) {// loops through transitions
            if (t.getActions().equals(transition.getActions()));
            return t;
        }
        return null;
    }// getTransition(Transition) method
}
