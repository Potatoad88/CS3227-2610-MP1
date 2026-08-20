/**
 * Provides the What Should I Eat JavaFX desktop application.
 */
module com.whatshouldieat {
    requires javafx.controls;
    requires java.prefs;

    exports com.whatshouldieat.ui to javafx.graphics;
}
