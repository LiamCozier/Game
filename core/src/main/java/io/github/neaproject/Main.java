package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.physics.BoxShape;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Shape;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    RigidBody box;

    @Override
    public void create() {
        box = new RigidBody(
            Vector2.Zero.cpy(),
            Vector2.Zero.cpy(),
            new BoxShape(10, 10),
            (float)Math.PI/4,
            0,
            1
        );

        System.out.println(box.get_polygon().toString());
    }

    @Override
    public void render() {

    }

    @Override
    public void dispose() {
    }
}
