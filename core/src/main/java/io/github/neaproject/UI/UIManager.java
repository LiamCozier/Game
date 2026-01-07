package io.github.neaproject.UI;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.neaproject.UI.elements.Control;
import io.github.neaproject.UI.interfaces.Clickable;
import io.github.neaproject.UI.interfaces.Hoverable;
import io.github.neaproject.input.UIInputProcessor;

import java.util.ArrayList;
import java.util.List;

public class UIManager {

    List<Control> nodes;

    Clickable captured_clickable;

    public UIManager() {
        nodes = new ArrayList<>(0);
        captured_clickable = null;
    }

    public void add_node(Control node) {
        nodes.add(node);
        for (Control child: node.get_children()) add_node(child);
    }

    public void add_node(Control node, boolean add_children) {
        nodes.add(node);
        if (add_children) nodes.addAll(node.get_children());
    }

    public void render_all(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Control node: nodes) {
            if (node.is_invisible()) continue;
            node.shape_render(sr);
        }
        sr.end();

        batch.begin();
        for (Control node: nodes) {
            if (node.is_invisible()) continue;
            node.batch_render(batch);
        }
        batch.end();
    }

    public void take_input(UIInputProcessor input, Camera camera) {
        Vector3 mouse_world3 = camera.unproject(new Vector3(input.mouse_position.x, input.mouse_position.y, 0));
        Vector2 mouse_screen = new Vector2(mouse_world3.x, -mouse_world3.y);

        for (Control node: nodes) {

            // cant input to invisible nodes
            if (node.is_invisible()) continue;

            Vector2 position = node.position();
            float width = node.get_width();
            float height = node.get_height();

            boolean inside = mouse_screen.x >= position.x && mouse_screen.x <= position.x + width &&
                mouse_screen.y >= position.y && mouse_screen.y <= position.y + height;

            if (node instanceof Hoverable) {
                Hoverable hoverable = (Hoverable) node;

                if (inside) hoverable.on_hover();
                else if (hoverable.is_hovering()) hoverable.on_unhover();
            }

            if (node instanceof Clickable) {
                Clickable clickable = (Clickable) node;

                if (inside && input.left_just_pressed && captured_clickable == null) {
                    captured_clickable = clickable;
                    clickable.on_click();
                }
            }
        }

        if (input.left_just_released && captured_clickable != null) {
            captured_clickable.on_release();
            captured_clickable = null;
        }
    }
}
