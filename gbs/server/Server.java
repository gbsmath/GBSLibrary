package gbs.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gbs.Packet;
import gbs.server.channel.Channel;
import gbs.server.channel.Lab08CChannel;
import gbs.server.channel.LabXChannel;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, ClientHandler> clients = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, Channel> channels = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, String> clientsCurrentChannel = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            String ipAddress = getIPAddressString();

            System.out.println("Server hosted at @ " + ipAddress + " running on port " + PORT);

            addChannels();
            listenForNewClients(serverSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getIPAddressString() {
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                for (InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    private static void addChannels() {
        channels.put("general", new Channel("general"));
        channels.put("labxs", new LabXChannel());
        channels.put("lab08c", new Lab08CChannel());
    }

    private static void listenForNewClients(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            new Thread(clientHandler).start();
        }
    }

    public static void moveToChannel(String clientID, ClientHandler handler, String newChannel) {
        String oldChannel = handler.getCurrentChannel();

        if (oldChannel.equals(newChannel)) {
            handler.sendServerMessage("Already in " + newChannel + " channel");
        }

        // none is default channel for new client
        if (oldChannel.equals("none")) {
            // channels.get(newChannel).add(clientID);
            // addClientToChannel(clientID, handler, newChannel);
            handler.sendServerMessage("Added you to the " + newChannel + " channel.");
        }
        // Otherwise move to the other channel
        else {
            channels.get(oldChannel).remove(clientID);
            // channels.get(newChannel).add(clientID);
            handler.sendServerMessage("Transferred you from " + oldChannel + " to " + newChannel + ".");
        }
        addClientToChannel(clientID, handler, newChannel);

    }

    private static void addClientToChannel(String clientID, ClientHandler clientHandler, String newChannel) {
        clientHandler.setCurrentChannel(newChannel); // updates server side client entity
        clientsCurrentChannel.put(clientID, newChannel); // updates location of client
        channels.get(newChannel).add(clientID);
        Packet p = new Packet("Server", clientID, newChannel);
        p.tagWithCommand("updateChannel");
        clientHandler.sendPacket(p);
    }

    public static boolean isChannel(String name) {
        return channels.containsKey(name);
    }

    public static Channel getChannel(String name) {
        return channels.get(name);
    }

    public static String[] getChannelNames() {
        return Arrays.copyOf(channels.keySet().toArray(), channels.keySet().toArray().length, String[].class);
    }

    // Register a new client
    public static void registerClient(String clientId, ClientHandler handler) {
        clients.put(clientId, handler);
        System.out.println(clientId + " connected.");
    }

    // Remove a client from the list
    public static void removeClient(String clientID) {
        channels.get(clientsCurrentChannel.get(clientID)).remove(clientID); // removes client from their current channel
        // channels.get();
        clients.remove(clientID);
        System.out.println(clientID + " disconnected.");
    }

    public static ClientHandler getClientHandler(String clientID) {
        return clients.get(clientID);
    }
}
