import java.awt.*;
import java.util.ArrayList;

public class Entity {
    private int pos;
    private Image image;
    private ArrayList<String> actions;
    public ArrayList<State> states;
    public StateMachine stateMachine;

    public Entity(int pos, Image image){
        this.pos = pos;
        this.image = image;
        states = new ArrayList<>();
        actions = new ArrayList<>();
    }

    // Accessors
    public int getPosition() {
        return pos;
    }
    public ArrayList<String> getActions() {
        return actions;
    }

    // Setters
    public void setPosition(int pos) {
        this.pos = pos;
    }

    public void setActions(ArrayList<String> actions) {
        this.actions = actions;
    }
}