package org.example.models.factory;

import simudyne.core.graph.Message;

public class Messages {
    public static class Msg_ReadyForProduct extends Message {
    }
    public static class Msg_ProductForMachine extends Message.Object<Product> {
    }
}
