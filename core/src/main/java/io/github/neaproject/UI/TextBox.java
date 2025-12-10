package io.github.neaproject.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class TextBox extends Control{

    float width, height;
    String text;
    BitmapFont font;

    public TextBox(Vector2 position, float width, float height, String text, Color color) {
        super(position);
        this.width = width;
        this.height = height;

        Texture texture = new Texture(Gdx.files.internal("fonts/mono.png"));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.text = text;
        font = new BitmapFont(Gdx.files.internal("fonts/mono.fnt"), new TextureRegion(texture));
        font.getData().setScale(1f);
        font.setColor(color);
    }

    public TextBox(Vector2 position, float width, float height, String text, Color color, Control parent) {
        this(position, width, height, text, color);
        this.parent = parent;
    }

    @Override
    public void shape_render(ShapeRenderer sr) {}

    @Override
    public void batch_render(SpriteBatch batch) {
        Vector2 position = this.position();

        font.draw(batch, this.text, position.x, -position.y, this.width, 1, true);
    }



}
