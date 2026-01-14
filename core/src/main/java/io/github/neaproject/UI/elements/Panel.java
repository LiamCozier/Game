package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Panel extends Control{

    Color color;

    public Panel(Vector2 position, float width, float height, Color color) {
        super(position, width, height);
        this.color = color;
    }

    public Panel(Vector2 position, float width, float height, Color color, Control parent) {
        this(position, width, height, color);
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


}
