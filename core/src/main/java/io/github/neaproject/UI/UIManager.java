package io.github.neaproject.UI;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
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
}
