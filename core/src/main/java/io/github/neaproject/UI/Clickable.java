package io.github.neaproject.UI;

public interface Clickable {
    void on_click();
    void on_release();
    boolean is_holding();
}
