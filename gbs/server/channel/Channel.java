package gbs.server.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gbs.Packet;
import gbs.server.ClientHandler;
import gbs.server.Lobby;
import gbs.server.Server;

public class Channel {
    private String name;

    private Map<String, Lobby> lobbies = Collections.synchronizedMap(new HashMap<>()); // String lobbyName -> Lobby
                                                                                       // lobbyObject
    private Map<String, String> clientsCurrentLobby = Collections.synchronizedMap(new HashMap<>()); // String clientIDs
                                                                                                    // -> String
                                                                                                    // lobbyName

    private int lobbySize = 3;

    public Channel(String name) {
        this.name = name;
        lobbies = Collections.synchronizedMap(new HashMap<>());
        clientsCurrentLobby = Collections.synchronizedMap(new HashMap<>());
        // this.clientIDs = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setSize(int size) {
        this.lobbySize = size;
    }

    public void add(String clientID) {
        clientsCurrentLobby.put(clientID, null);
        // System.out.println("Added " + clientID + " to " + name + " channel.");
    }

    public void remove(String clientID) {
        // clientIDs.remove(clientID);
        if (clientsCurrentLobby.containsKey(clientID)) {

            if (clientsCurrentLobby.get(clientID) != null) {
                // lobby removal logic
                String currentLobbyName = clientsCurrentLobby.get(clientID);
                Lobby currentLobby = lobbies.get(currentLobbyName);

                currentLobby.remove(clientID); // removes client from their current lobby
                if (currentLobby.empty()) {
                    lobbies.remove(currentLobbyName); // removes lobby if empty
                }
            }
            clientsCurrentLobby.remove(clientID); // removes location

        }
        // System.out.println("Removed " + clientID + " from " + name + " channel.");
    }

    public void onRecieve(Packet packet) {

        String clientID = packet.getSender();
        ClientHandler clientHandler = Server.getClientHandler(clientID);
        String message = packet.getMessage();

        // If packet is a command
        if (packet.getCommand() != null) {
            if (packet.getCommand().equals("joinLobby")) {
                joinLobby(clientID, message);
                return;
            } else if (packet.getCommand().equals("leaveLobby")) {
                leaveLobby(clientID);
                return;
            } else if (packet.getCommand().equals("getLobby")) {
                if (!clientsCurrentLobby.containsKey(clientID)) {
                    clientHandler.sendServerMessage("You're currently not in a lobby.");
                } else {
                    String lobbyName = clientsCurrentLobby.get(clientID);
                    clientHandler.sendServerMessage("You're currently in lobby " + lobbyName + ".");
                }
                return;
            } else if (packet.getCommand().equals("listLobbies")) {
                // Server.sendMessage(clientID, Arrays.toString(getLobbyNames()));
                clientHandler.sendServerMessage(getLobbyNames());
                return;
            } else if (packet.getCommand().equals("listUsers")) {
                String output = "";
                ArrayList<String> clients = new ArrayList<>();
                for (String client : clientsCurrentLobby.keySet()) {
                    String lobby = "";
                    if (clientInLobby(client)) {
                        lobby = "(" + clientsCurrentLobby.get(client) + ")";
                    }
                    clients.add(client + lobby);
                }
                clientHandler.sendServerMessage("Users in " + this.name + " channel: " + clients);
                return;
            }
        }

        // If user is not in a lobby
        if (!clientInLobby(clientID)) {
            for (String otherClient : clientsCurrentLobby.keySet()) {
                // Skip self and others in a lobby
                if (otherClient == clientID || clientInLobby(otherClient)) {
                    continue;
                }
                ClientHandler otherHandler = Server.getClientHandler(otherClient);
                otherHandler.sendPacket(packet);
            }
        }

        // if client in a lobby, pass packet off to lobby
        if (clientInLobby(clientID)) {
            // Is the map returning the Lobby object?
            Lobby currentLobby = lobbies.get(clientsCurrentLobby.get(clientID));
            currentLobby.onRecieve(packet);
        }
        // System.out.println(packet);
    }

    public void sendToOthers(Packet packet) {
        for (String otherClient : clientsCurrentLobby.keySet()) {
            // Skip self
            if (otherClient == packet.getSender() || clientInLobby(otherClient)) {
                continue;
            }
            ClientHandler otherHandler = Server.getClientHandler(otherClient);
            // Packet p = new Packet(clientID, otherClient, message);
            packet.tagWithChannel(this.name);
            // packet.tagWithLobby(name);
            // Server.sendMessage(otherClient, message);
            otherHandler.sendPacket(packet);
        }
    }

    public String getLobbyNames() {
        ArrayList<String> lobbyToStrings = new ArrayList<>();
        for (Lobby lobby : lobbies.values()) {
            lobbyToStrings.add(lobby.toString());
        }
        return "" + lobbyToStrings;
    }

    public boolean clientInChannel(String clientID) {
        return clientsCurrentLobby.containsKey(clientID);
    }

    public boolean clientInLobby(String clientID) {
        return clientsCurrentLobby.containsKey(clientID) && clientsCurrentLobby.get(clientID) != null;
    }

    public void addLobby(Lobby lobby) {
        String name = lobby.getName();
        lobbies.put(name, lobby);
    }

    public boolean labExists(String name) {
        return lobbies.containsKey(name);
    }

    public void joinLobby(String clientID, String message) {

        String potentialLobbyName = message; // .substring(message.indexOf(":")+1);
        ClientHandler clientHandler = Server.getClientHandler(clientID);

        // If the lobby doesn't exist, create one
        if (!labExists(potentialLobbyName)) {
            addLobby(new Lobby(this.name, potentialLobbyName, this.lobbySize));
            clientHandler.sendServerMessage("Created new lobby: " + potentialLobbyName);
        }

        Lobby lobby = lobbies.get(potentialLobbyName);

        // Check if there's room
        if (lobby.isOpen()) {
            lobby.add(clientID);
            clientsCurrentLobby.put(clientID, potentialLobbyName);
            // clientHandler.sendServerMessage("Added to Lobby " + potentialLobbyName);
            Packet p = new Packet("Server", clientID, potentialLobbyName);
            p.tagWithCommand("updateLobby");
            clientHandler.sendPacket(p);
        } else {
            clientHandler.sendServerMessage(potentialLobbyName + " lobby is full.");
        }

    }

    public void leaveLobby(String clientID) {
        ClientHandler clientHandler = Server.getClientHandler(clientID);
        if (!clientsCurrentLobby.containsKey(clientID)) {
            // Server.sendMessage(clientID, "Not in a lobby");
            clientHandler.sendServerMessage("Not in a lobby");

            return;
        }

        String lobbyName = clientsCurrentLobby.get(clientID);
        Lobby oldLobby = lobbies.get(lobbyName);
        oldLobby.remove(clientID);
        clientsCurrentLobby.remove(clientID);
        // Server.sendMessage(clientID, "You've left lobby " + lobbyName);
        clientHandler.sendServerMessage("You've left lobby " + lobbyName);
        Packet p = new Packet("Server", clientID, null);
        p.tagWithCommand("updateLobby");
        clientHandler.sendPacket(p);

        if (oldLobby.empty()) {
            lobbies.remove(lobbyName);
        }
    }

    public String toString() {
        return "" + name + "(" + clientsCurrentLobby.keySet().size() + ")";
    }
}
