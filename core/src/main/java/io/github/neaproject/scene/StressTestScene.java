package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.Panel;
import io.github.neaproject.UI.elements.TextBox;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;
import io.github.neaproject.physics.shape.BoxShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StressTestScene extends Scene {

    public StressTestScene(SceneManager manager) {super(manager);}

    OrthographicCamera camera;
    OrthographicCamera ui_camera;

    UIManager ui_manager;

    ShapeRenderer sr;
    SpriteBatch batch;

    // physics
    Stage stage;

    float time;
    List<Integer> fps_list = new ArrayList<>(0);
    List<Integer> body_count_list = new ArrayList<>(0);

    private void init_ui() {
        ui_manager = new UIManager();

        Panel root = new Panel("root", new Vector2(0,0), 256, 128, new Color(0,0,0,0.5f));
        new TextBox("body_info_text", new Vector2(16, 0), 256, 64, "", new Color(1, 1, 1, 1), 0.125f, Align.left, root);
        new TextBox("fps_info_text", new Vector2(16, 64), 256, 64, "", new Color(1, 1, 1, 1), 0.125f, Align.left, root);


        ui_manager.add_node(root);
    }

    @Override
    public void on_open() {
        // cameras
        float height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        float ui_height = 1080;
        float ui_ppu = Gdx.graphics.getHeight() / ui_height;
        float ui_width = Gdx.graphics.getWidth() / ui_ppu;
        ui_camera = new OrthographicCamera(ui_width, ui_height);
        ui_camera.translate(new Vector2(ui_width, -ui_height).scl(0.5f));
        ui_camera.update();

        init_ui();

        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        stage = new Stage();
        stage.world.add_body(
            new RigidBody(
                new Vector2(0, -9.5f),
                new Vector2(0, 0),
                new BoxShape(35.6f, 1),
                0, 0,
                0, false
            )
        );
        stage.world.add_body(
            new RigidBody(
                new Vector2(-17.5f, 0.5f),
                new Vector2(0, 0),
                new BoxShape(1, 19),
                0, 0,
                0, false
            )
        );
        stage.world.add_body(
            new RigidBody(
                new Vector2(17.5f, 0.5f),
                new Vector2(0, 0),
                new BoxShape(1, 19),
                0, 0,
                0, false
            )
        );
    }

    @Override
    public void update(float dt) {
        ui_manager.tick(dt);

        stage.tick(dt);
        TextBox body_info_text = (TextBox) ui_manager.get_node("body_info_text");
        TextBox fps_info_text = (TextBox) ui_manager.get_node("fps_info_text");
        body_info_text.set_text("Bodies: " + stage.world.get_body_count());
        body_count_list.add(stage.world.get_body_count());

        fps_list.add(Math.min((int) (1/dt), 200));
        float fps = 0;
        int size = fps_list.size();
        List<Integer> tmp = fps_list.subList(size-Math.min(size,50), size-1);
        for (float i: tmp) fps += i/Math.min(size,50);
        fps_info_text.set_text("FPS: " + (int) fps);

        time += dt;
        if (time >= 0.5f) {
            time -= 0.5f;
            Random r = new Random();
            for (int i=0; i<1; i++) {
                stage.world.add_body(
                    new RigidBody(
                        new Vector2(r.nextInt(20)-10, 20),
                        new Vector2(0, 0),
                        new BoxShape(1, 1),
                        0, r.nextFloat(6.28f),
                        1, true
                    )
                );
            }
        }

        if (fps < 10 && size > 50) {
            Integer[] array1 = body_count_list.toArray(new Integer[0]);
            Integer[] array2 = fps_list.toArray(new Integer[0]);

            System.out.print("x_1=[");
            for (int i=0; i<array1.length; i++) {
                System.out.print(array1[i] + (i!=array2.length-1 ? ", ": ""));
            }
            System.out.println("]");
            Gdx.app.exit();

            System.out.print("y_1=[");
            for (int i=0; i<array2.length; i++) {
                System.out.print(array2[i] + (i!=array2.length-1 ? ", ": ""));
            }
            System.out.println("]");
            Gdx.app.exit();
        }

    }

    @Override
    public void render() {
        // enable alpha channel
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ScreenUtils.clear(0, 0.4f, 0.8f, 1f);

        sr.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        stage.render(sr, batch);

        sr.setProjectionMatrix(ui_camera.combined);
        batch.setProjectionMatrix(ui_camera.combined);
        ui_manager.render_all(sr, batch);

    }

    @Override
    public void on_close() {
        sr.dispose();
        batch.dispose();
    }
}
