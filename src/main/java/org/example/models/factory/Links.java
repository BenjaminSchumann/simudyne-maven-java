package org.example.models.factory;

import simudyne.core.graph.Link;

import java.util.LinkedList;

public class Links {
    public static class NormalLink extends Link.Empty{}

    public static class LinkMachineToConveyor extends Link {
        // these links hold the conveyor waiting queue so machine can access it directly
        public LinkedList<Product> queue = new LinkedList<>();
    }
}
