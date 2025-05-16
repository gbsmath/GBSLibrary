package gbs.server;

import java.util.ArrayList;

import gbs.Packet;
import gbs.server.bot.Bot;
import gbs.server.channel.Channel;

public class BotLobby extends Lobby {

    private ArrayList<Bot> bots;
    private String name;

    public BotLobby(Channel channel, String name, int playerSize, int botSize) {
        super(channel.getName(), name, playerSize + botSize);
        bots = new ArrayList<>();
        this.name = name + "-AI";
    }

    @Override
    public String getName() {
        return name;
    }

    public void addBot(Bot bot) {
        bots.add(bot);
        Packet p = new Packet(bot.getName(), "none", bot.getName());
        p.tagWithChannel(getChannelName());
        p.tagWithLobby(name);
        p.tagWithType("joinLobby");
        sendToOthers(p);
        // System.out.println(p);
    }

    @Override
    public int currentSize() {
        return super.currentSize() + bots.size();
    }

    @Override
    public boolean empty() {
        return super.currentSize() == 0; // meaning no clients
    }

    @Override
    public void onRecieve(Packet packet) {
        // System.out.println("BotLobby.onRecieve");
        // super.onRecieve(packet);
        sendToOthers(packet);
        sendToBots(packet);
    }

    public void sendToBots(Packet packet) {
        for (Bot b: bots) {
            b.onRecieve(packet);
        }
    }

    @Override
    public String toString() {
        return super.toString() + bots;
    }
}
