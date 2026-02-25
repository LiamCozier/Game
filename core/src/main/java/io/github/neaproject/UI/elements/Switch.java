package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Switch extends Button {

    protected final int states;
    protected int current_state;

    protected final Runnable[] state_actions;
    protected IconRenderer[] state_renderers;

    public Switch(String identifier, Vector2 position, float width, float height, Color color, int states) {
        super(identifier, position, width, height, color);
        this.states = Math.max(states, 2);
        state_actions = new Runnable[states];
        state_renderers = new IconRenderer[states];
        set_state(0);

        this.set_release_action(this::increment_state);
    }

    public Switch(String identifier, Vector2 position, float width, float height, Color color, int states, Control parent) {
        this(identifier, position, width, height, color, states);
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
        super.set_icon_renderer(this.state_renderers[current_state]);
    }

    public void set_state(int state, boolean trigger) {
        if (trigger) run_current_state();
        this.current_state = state % states;
        super.set_icon_renderer(this.state_renderers[current_state]);
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

    public void set_state_renderer(int index, IconRenderer renderer) {
        this.state_renderers[index] = renderer;

        if (index == current_state) {
            super.set_icon_renderer(renderer);
        }
    }





}
