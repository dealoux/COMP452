import java.util.ArrayList;
import java.util.Arrays;

public class Transition{
    private boolean triggered;
    private State targetState;
    private ArrayList<String> actions;

    // Constructor
    public Transition(State targetState, String[] actions){
        triggered = false;
        this.targetState = targetState;
        this.actions = new ArrayList<>(Arrays.asList(actions));
    }

    // Accessors
    public boolean isTriggered() { return triggered; }
    public State getTargetState() { return targetState; }
    public ArrayList<String> getActions() { return actions; }

    // Setters
    public void setTargetState(State targetState) {
        this.targetState = targetState;
    }

    // This function toggle the triggered flag
    public void setTrigger(boolean flag){ triggered = flag; }
}
