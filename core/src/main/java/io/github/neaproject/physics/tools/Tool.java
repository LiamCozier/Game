package io.github.neaproject.physics.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Tool {
    void update(float dt);
    void render(ShapeRenderer sr);
}
