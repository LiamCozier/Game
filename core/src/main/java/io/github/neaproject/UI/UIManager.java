package io.github.neaproject.UI;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.neaproject.input.UIInputProcessor;

import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UIManager {

    List<Control> nodes;

    public UIManager() {
        nodes = new ArrayList<>(0);
    }

    public void add_node(Control node) {
        nodes.add(node);
        nodes.addAll(node.get_children());
    }

    public void add_node(Control node, boolean add_children) {
        nodes.add(node);
        if (add_children) nodes.addAll(node.get_children());
    }

    public void render_all(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Control node: nodes) {node.shape_render(sr);}
        sr.end();

        batch.begin();
        for (Control node: nodes) {node.batch_render(batch);}
        batch.end();
    }

    public void take_input(UIInputProcessor input, Camera camera) {

        for (Control node: nodes) {

            Class<?>[] interfaces = node.getClass().getInterfaces();

            // if hoverable
            if (node instanceof Hoverable) {
                Hoverable hoverable = (Hoverable) node;

                Vector3 position = camera.unproject(new Vector3(input.mouse_position.x, input.mouse_position.y, 0));
                Vector2 screen_position = new Vector2 (position.x, position.y);
                Vector2 lower_bound = new Vector2(position.x, position.y);
                Vector2 upper_bound = new Vector2(position.x + node.get_width(), position.y - node.get_height());

                System.out.println(lower_bound);
                System.out.println(upper_bound);
                System.out.println(input.mouse_position);

                if (input.mouse_position.x > lower_bound.x && input.mouse_position.x < upper_bound.x &&
                    input.mouse_position.y > lower_bound.y && input.mouse_position.y < upper_bound.y) {
                    hoverable.on_hover();
                } else if(hoverable.is_hovering()) {
                    hoverable.on_unhover();
                }
            }
        }

    }
}
