package io.github.neaproject.UI.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class TextBox extends Control{

    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();
    public int alignment;

    public TextBox(String identifier, Vector2 position, float width, float height, String text, float scale, int alignment, Color color) {
        super(identifier, width, height, position);

        this.alignment = alignment;

        font = new BitmapFont(Gdx.files.internal("fonts/mono.fnt")); // loads all pages

        for (TextureRegion region : font.getRegions()) {
            Texture texture = region.getTexture();
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        }

        font.setUseIntegerPositions(true);
        font.getData().setScale(scale);
        font.setColor(new Color(color));

        layout.setText(font, text, Color.WHITE, width, alignment, true);
    }

    public TextBox(String identifier, Vector2 position, float width, float height, String text, Color color, float scale, int alignment, Control parent) {
        this(identifier, position, width, height, text, scale, alignment, color);
        this.parent = parent;
        parent.add_child(this);
        this.z_order = parent.z_order+1;
    }

    public void set_text(String text) {
        layout.setText(font, text, Color.WHITE, width, alignment, true);
    }

    @Override
    public void shape_render(ShapeRenderer sr) {}

    @Override
    public void batch_render(SpriteBatch batch) {
        Vector2 position = this.position();

        font.draw(batch, layout, (int) position.x, (int) (-position.y + layout.height/2 - height/2));
    }



}
