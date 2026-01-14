package io.github.neaproject.UI;

import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.elements.Control;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class UIAnimator {

    public enum EasingType {
        Linear,
        ExponentialIn,
        ExponentialOut,
        ExponentialInOut
    }


    private class Animation {
        Control parent;
        EasingType easing_type;

        Vector2 translation;
        float applied;
        float duration;
        float t;
        float prev_t;

        private Animation(Control parent, EasingType easing_type, Vector2 translation, float duration) {
            this.parent = parent;
            this.easing_type = easing_type;
            this.translation = translation;
            this.duration = duration;

            applied = 0f;
            t = 0f;
        }

        private boolean tick(float dt) {
            prev_t = t;
            t += dt / duration;

            float u = Math.min(1f, t); // (or t/duration later)
            float target = ease(u);     // total fraction that should be applied by now

            float delta = target - applied;
            applied = target;

            animate(delta);

            return t >= 1f;
        }


        private float ease(float time) {
            switch (easing_type) {
                case Linear:
                    return time;

                case ExponentialIn:
                    if (time <= 0f) return 0f;
                    if (time >= 1f) return 1f;
                    return (float) Math.pow(2.0f, 10.0f * (time - 1.0f));

                case ExponentialOut:
                    if (time <= 0f) return 0f;
                    if (time >= 1f) return 1f;
                    return 1f - (float) Math.pow(2.0f, -10.0f * time);

                case ExponentialInOut:
                    if (time <= 0f) return 0f;
                    if (time >= 1f) return 1f;

                    if (time < 0.5f) {
                        return 0.5f * (float) Math.pow(2.0f, 20.0f * time - 10.0f);
                    } else {
                        return 1f - 0.5f * (float) Math.pow(2.0f, -20.0f * time + 10.0f);
                    }
            }
            return time;
        }


        private void animate(float delta_time) {
            parent.translate(translation.cpy().scl(delta_time));
        }
    }

    Control parent;
    List<Animation> animations;

    public UIAnimator(Control parent) {
        animations = new ArrayList<>(0);
        this.parent = parent;
    }

    public void tick(float dt) {
        // use iterator because list modifications happen during iteration
        ListIterator<Animation> iterator = animations.listIterator();

        while (iterator.hasNext()) {
            Animation animation = iterator.next();
            boolean remove = animation.tick(dt);
            if (remove) iterator.remove();
        }
    }

    public void translate(EasingType easing_type, Vector2 translation, float duration) {
        animations.add(new Animation(
            parent,
            easing_type,
            translation,
            duration
        ));
    }

}
