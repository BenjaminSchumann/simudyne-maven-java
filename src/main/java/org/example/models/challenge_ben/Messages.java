package org.example.models.challenge_ben;

import simudyne.core.graph.Message;

public class Messages {

    public static class BuyOrderPlaced extends Message {}

    public static class SellOrderPlaced extends Message {}

    public static class PriceChange extends Message.Double {}
}