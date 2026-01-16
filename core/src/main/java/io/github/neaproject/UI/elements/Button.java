package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.interfaces.Clickable;
import io.github.neaproject.UI.interfaces.Hoverable;

public class Button extends Control implements Hoverable, Clickable {

    protected Color color, off_color, on_color;
    protected boolean hovering, pressing;
    protected Runnable click_action, release_action;

    public Button(String identifier, Vector2 position, float width, float height, Color color) {
        super(identifier, width, height, position);
        this.color = color;
        off_color = new Color(color);
        on_color = color.cpy().add(0.1f, 0.1f, 0.1f, 1);
    }

    public Button(String identifier, Vector2 position, float width, float height, Color color, Control parent) {
        this(identifier, position, width, height, color);
        this.parent = parent;
        parent.add_child(this);
        this.z_order = parent.z_order+1;
    }

    @Override
    public void shape_render(ShapeRenderer sr) {
        Vector2 position = this.position();
        sr.setColor(color);
        sr.rect(position.x, -position.y-height, width, height);
    }
    @Override
    public void batch_render(SpriteBatch batch) {}


    @Override
    public void on_hover() {
        color.set(on_color.cpy());
        hovering = true;
    }

    @Override
    public void on_unhover() {
        color.set(off_color.cpy());
        hovering = false;
    }

    @Override
    public boolean is_hovering() {
        return hovering;
    }

    @Override
    public void on_click() {
        pressing = true;

        if (click_action == null) return;
        click_action.run();
    }

    @Override
    public void on_release() {
        if (release_action == null) return;
        release_action.run();
        pressing = false;
    }

    @Override
    public boolean is_holding() {return pressing;}

    public void set_click_action(Runnable r) {
        this.click_action = r;
    }

    public void set_release_action(Runnable r) {
        this.release_action = r;
    }


}
