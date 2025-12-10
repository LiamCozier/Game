package io.github.neaproject.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Panel extends Control{

    float width, height;
    Color fill_color;
    Color outline_color;

    public Panel(Vector2 position, float width, float height, Color fill_color, Color outline_color) {
        super(position);
        this.width = width;
        this.height = height;
        this.fill_color = fill_color;
        this.outline_color = outline_color;
    }

    public Panel(Vector2 position, float width, float height, Color fill_color, Color outline_color, Control parent) {
        this(position, width, height, fill_color, outline_color);
        this.parent = parent;
    }

    @Override
    public void shape_render(ShapeRenderer sr) {
        Vector2 position = this.position();

        // Set color based on render mode
        if (sr.getCurrentType() == ShapeRenderer.ShapeType.Filled) {
            sr.setColor(fill_color);
        } else {
            sr.setColor(outline_color);
        }

        sr.rect(position.x, -position.y-height, width, height);
    }

    @Override
    public void batch_render(SpriteBatch batch) {}


}
