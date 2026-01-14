package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Switch extends Button {

    private int states;
    private int current_state;

    private Runnable[] state_actions;

    public Switch(Vector2 position, float width, float height, Color color, int states) {
        super(position, width, height, color);
        this.states = Math.max(states, 2);
        current_state = 0;
        state_actions = new Runnable[states];

        this.set_release_action(this::increment_state);
    }

    public Switch(Vector2 position, float width, float height, Color color, int states, Control parent) {
        this(position, width, height, color, states);
        this.parent = parent;
        parent.add_child(this);
        this.z_order = parent.z_order+1;
    }

    private void run_current_state() {
        Runnable action = state_actions[current_state];
        if (action != null) action.run();
    }

    public int get_state() {
        return this.current_state;
    }

    public void set_state(int state) {
        run_current_state();
        this.current_state = state % states;
    }

    public void increment_state() {
        this.set_state(this.get_state()+1);
    }

    public void decrement_state() {
        this.set_state(this.get_state()-1);
    }

    public void set_state_action(int index, Runnable action) {
        this.state_actions[index] = action;
    }




}
