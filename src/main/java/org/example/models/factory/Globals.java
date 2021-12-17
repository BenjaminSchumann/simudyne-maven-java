package org.example.models.factory;

import simudyne.core.abm.GlobalState;
import simudyne.core.annotations.Input;

public final class Globals extends GlobalState {
    // globals inputs and constants here

    @Input(name = "Initial products")
    public int numInitialProducts = 1000;

    public boolean systemFinished = false;
}
