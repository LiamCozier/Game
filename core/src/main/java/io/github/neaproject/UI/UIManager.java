package io.github.neaproject.UI;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.neaproject.UI.elements.Control;
import io.github.neaproject.UI.elements.InputTextBox;
import io.github.neaproject.UI.interfaces.Clickable;
import io.github.neaproject.UI.interfaces.Hoverable;
import io.github.neaproject.input.UIInputProcessor;

import java.util.ArrayList;
import java.util.List;

public class UIManager {

    private final List<Control> nodes;
    private boolean nodes_dirty;

    private Clickable captured_clickable;
    public boolean input_captured;

    private InputTextBox captured_input_text_box;
    private boolean input_text_captured;

    private Control focused_node;
    private List<Control> focus_nodes;

    public UIManager() {
        nodes = new ArrayList<>(0);
        nodes_dirty = true;

        captured_clickable = null;
        input_captured = false;
        captured_input_text_box = null;
        input_text_captured = false;

        focused_node = null;
        focus_nodes = null;
    }

    private void sort_nodes() {
        nodes_dirty = false;

        for (int i = 1; i < nodes.size(); i++) {
            Control key = nodes.get(i);
            int key_z = key.get_z();

            int j = i - 1;
            while (j >= 0 && nodes.get(j).get_z() > key_z) {
                nodes.set(j + 1, nodes.get(j));
                j--;
            }
            nodes.set(j + 1, key);
        }
    }

    public void add_node(Control node) {
        nodes_dirty = true;
        nodes.add(node);
        for (Control child: node.get_children()) add_node(child);
    }

    public void add_node(Control node, boolean add_children) {
        nodes_dirty = true;
        nodes.add(node);
        if (add_children) nodes.addAll(node.get_children());
    }

    public void render_all(ShapeRenderer sr, SpriteBatch batch) {
        if (nodes_dirty) sort_nodes();

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

    public void tick(float dt) {
        if (nodes_dirty) sort_nodes();
        for (Control node: nodes) node.animator.tick(dt);
    }

    public void take_input(UIInputProcessor input, Camera camera) {
        if (nodes_dirty) sort_nodes();
        input_captured = false;

        Vector3 mouse_world3 = camera.unproject(new Vector3(input.mouse_position.x, input.mouse_position.y, 0));
        Vector2 mouse_screen = new Vector2(mouse_world3.x, -mouse_world3.y);

        List<Control> nodes;
        if (focused_node == null) {
            nodes = this.nodes;
        } else {
            nodes = focus_nodes;
        }
        for (Control node: nodes) {

            // cant input to invisible nodes
            if (node.is_invisible()) continue;

            Vector2 position = node.position();
            float width = node.get_width();
            float height = node.get_height();

            boolean inside = mouse_screen.x >= position.x && mouse_screen.x <= position.x + width &&
                mouse_screen.y >= position.y && mouse_screen.y <= position.y + height;
            if (inside) input_captured = true;

            if (node instanceof Hoverable hoverable) {

                if (inside) hoverable.on_hover();
                else if (hoverable.is_hovering()) hoverable.on_unhover();
            }

            if (node instanceof Clickable clickable) {

                if (inside && input.left_just_pressed && captured_clickable == null) {
                    captured_clickable = clickable;
                    clickable.on_click();
                }
            }

            if (node instanceof InputTextBox box) {
                captured_input_text_box = box;
                box.type(input.type_text.toString());
            }

        }

        if (input.left_just_released && captured_clickable != null) {
            captured_clickable.on_release();
            captured_clickable = null;
        }

        // clear text every frame after use
        input.type_text.setLength(0);
    }

    public Control get_node(String identifier) {
        for (Control node: nodes) {
            if (node.get_identifier().equals(identifier)) return node;
        }
        return null;
    }

    public void set_focus(Control node) {
        this.focused_node = node;
        this.focus_nodes = node.flatten_children();
        this.focus_nodes.add(this.focused_node);
    }

    public void set_focus(String identifier) {
        this.set_focus(get_node(identifier));
    }

    public void unfocus() {
        this.focused_node = null;
        this.focus_nodes = null;
    }

    public Control get_focus() {
        return focused_node;
    }

    public boolean is_focused() {
        return get_focus() != null;
    }

    public List<Control> get_focus_tree() {
        return focus_nodes;
    }

}
