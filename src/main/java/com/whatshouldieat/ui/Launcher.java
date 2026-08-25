package com.whatshouldieat.ui;

/**
 * Starts the application from an executable JAR without extending JavaFX Application.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Delegates startup to the JavaFX application entry point.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        WhatShouldIEatApp.main(args);
    }
}
