package io.github.neaproject.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

//    @Override
//    public void render(ShapeRenderer sr, OrthographicCamera camera) {
//
//        // Pixels per unit based on viewport width
//        float ppu = Gdx.graphics.getWidth() / (camera.viewportWidth * camera.zoom);
//
//        // Convert world position to screen position
//        float screenX = (position().x - (camera.position.x - camera.viewportWidth  / 2f)) * ppu;
//        float screenY = (position().y - (camera.position.y - camera.viewportHeight / 2f)) * ppu;
//
//        float screenW = width  * ppu;
//        float screenH = height * ppu;
//
//        // Set color based on render mode
//        if (sr.getCurrentType() == ShapeRenderer.ShapeType.Filled) {
//            sr.setColor(fill_color);
//        } else {
//            sr.setColor(outline_color);
//        }
//
//        // LibGDX UI coords = bottom-left
//        sr.rect(screenX, screenY, screenW, screenH);
//    }

    @Override
    public void render(ShapeRenderer sr, OrthographicCamera camera) {



        float ppu = camera.viewportHeight / (Gdx.graphics.getHeight());

        float screenW = width  * ppu;
        float screenH = height * ppu;

        Vector2 offset = new Vector2(ppu * Gdx.graphics.getWidth(), ppu);
        Vector2 world_position = position().sub(offset);

        // Set color based on render mode
        if (sr.getCurrentType() == ShapeRenderer.ShapeType.Filled) {
            sr.setColor(fill_color);
        } else {
            sr.setColor(outline_color);
        }

        sr.rect(world_position.x, world_position.y, screenW, screenH);
    }


}
