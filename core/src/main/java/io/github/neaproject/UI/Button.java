package io.github.neaproject.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Button extends Control implements Hoverable{

    float width, height;
    Color color, off_color, on_color;

    public Button(Vector2 position, float width, float height, Color color) {
        super(position);
        this.width = width;
        this.height = height;
        this.color = color;
        off_color = new Color(color);
    }

    public Button(Vector2 position, float width, float height, Color color, Control parent) {
        this(position, width, height, color);
        this.parent = parent;
        parent.add_child(this);
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

    }

    @Override
    public void on_unhover() {

    }
}
