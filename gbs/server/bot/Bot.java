package gbs.server.bot;

import gbs.Packet;
import gbs.server.BotLobby;

public class Bot {

    private BotLobby lobby;
    private String name;

    public Bot(BotLobby lobby) {
        super();
        this.lobby = lobby;
        name = "Bot" + (int) (1 + Math.random() * 1000);
    }

    public String getName() {
        return name;
    }

    public void send(Packet packet) {
        lobby.sendToOthers(packet);
    }

    public void send(String type, String message) {
        Packet p = new Packet(getName(), "other", message);
        p.tagWithType(type);
        p.tagWithLobby(lobby.getName());
        p.tagWithChannel(lobby.getChannelName());
        send(p);
        System.out.println("-> " + p);
    }

    public void onRecieve(Packet packet) {
        System.out.println("I'm a bot and I recieved: " + packet.getType() + ":" + packet.getMessage());
    }

    public void thinkTime(double seconds) {
        try {
            Thread.sleep((long) (seconds*1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
