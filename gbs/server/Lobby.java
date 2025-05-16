package gbs.server;

import java.util.ArrayList;

import gbs.Packet;

public class Lobby {

    private String name;
    private String channelName;
    private int size;

    private ArrayList<String> clientIDs;

    public Lobby(String channelName, String name, int size) {
        this.channelName = channelName;
        this.name = name;
        this.size = size;

        clientIDs = new ArrayList<>();
    }

    public boolean isOpen() {
        return currentSize() < size;
    }

    public boolean empty() {
        return clientIDs.size() == 0;
    }

    public int currentSize() {
        return clientIDs.size();
    }

    public String getName() {
        return this.name;
    }

    public String getChannelName() {
        return channelName;
    }

    public void add(String clientID) {
        clientIDs.add(clientID);
        Packet p = new Packet(clientID, "none", clientID);
        p.tagWithChannel(channelName);
        p.tagWithLobby(name);
        p.tagWithType("joinLobby");
        sendToOthers(p);
    }

    public void remove(String clientID) {
        clientIDs.remove(clientID);
        Packet p = new Packet(clientID, "none", clientID);
        p.tagWithChannel(channelName);
        p.tagWithLobby(name);
        p.tagWithType("leaveLobby");
        sendToOthers(p);
    }

    public void onRecieve(Packet packet) {
        // System.out.println("Lobby.onRecieve***");
        sendToOthers(packet);
    }

    public void sendToOthers(Packet packet) {
        // System.out.println("sending to others: " + packet);
        for (String otherClient: clientIDs) {
            // Skip self
            if (otherClient == packet.getSender()) {
                continue;
            }
            ClientHandler otherHandler = Server.getClientHandler(otherClient);
            packet.setReciever(otherClient);
            otherHandler.sendPacket(packet);
        }
    }

    @Override
    public String toString() {
        return this.getName() + "(" + this.currentSize() + "/" + this.size + ")" + clientIDs;
    }
}
