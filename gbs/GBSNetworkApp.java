package gbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class GBSNetworkApp extends GBSApp {

    private String clientID;

    private Socket socket;
    // private BufferedReader userInput;
    // private PrintWriter out;
    // private BufferedReader in;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private String assumedChannel;
    private String assumedLobby;

    private boolean seeInfoMessages;

    public boolean connect(String ipAddress, String clientID) {
        return connect(ipAddress, 12345, clientID);
    }

    public boolean connect(String ipAddress, int port, String clientID) {
        try {
            this.socket = new Socket(ipAddress, port);
            this.outStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.inStream = new ObjectInputStream(this.socket.getInputStream());

            this.clientID = clientID;
            this.outStream.writeObject(new Packet(clientID, "Server", clientID));

            // Start a thread to read messages from server
            new Thread(() -> {
                Packet packet;
                try {
                    while ((packet = (Packet) inStream.readObject()) != null) {
                        onPacketRecieve(packet);
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("Error receiving custom packet.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (UnknownHostException e) {
            System.out.println("Couldn't find IP address");
            return false;
        } catch (IOException e) {
            System.out.println("Couldn't connect, ask your teacher...");
            return false;
        }

        return true;
    }

    public String sendAndWait(Packet packet) {
        try {
            packet.tagWithCommand("request");
            this.outStream.writeObject(packet);
            Packet response = null;
            while ((response = (Packet) inStream.readObject()) != null) {
                if (response.getType().equals("response")) {
                    break;
                }
            }
            return response.getMessage();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void seeInfo(boolean b) {
        seeInfoMessages = b;
    }

    // Room joining/leaving
    public void setChannel(String newChannel) {
        Packet setChannelPacket = new Packet(clientID, "Server", newChannel);
        setChannelPacket.tagWithCommand("setChannel");
        sendCustomPacket(setChannelPacket);
    }

    public void joinLobby(String newLobby) {
        Packet setChannelPacket = new Packet(clientID, "Server", newLobby);
        setChannelPacket.tagWithCommand("joinLobby");
        sendCustomPacket(setChannelPacket);
    }

    public void joinBotLobby(String newLobby) {
        Packet setChannelPacket = new Packet(clientID, "Server", newLobby);
        setChannelPacket.tagWithCommand("joinBotLobby");
        sendCustomPacket(setChannelPacket);
    }

    public void leaveLobby() {
        Packet setChannelPacket = new Packet(clientID, "Server", "none");
        setChannelPacket.tagWithCommand("leaveLobby");
        sendCustomPacket(setChannelPacket);
    }

    public void printChannels() {
        send("listChannels:");
    }

    public void printUsersInChannel() {
        send("listUsers:");
    }

    public void send(String message) {

        int commandIndex = message.indexOf(":");

        if (commandIndex != -1) {
            Packet p = new Packet(clientID, "Server", message.substring(commandIndex + 1));
            p.tagWithCommand(message.substring(0, commandIndex));
            sendCustomPacket(p);
        } else {
            Packet p = new Packet(clientID, "Server", message);
            p.tagWithType("user");
            sendCustomPacket(p);
        }
    }

    public void send(String type, String message) {
        Packet p = new Packet(clientID, "Server", message);
        p.tagWithType(type);
        sendCustomPacket(p);
    }

    
    // To be overloaded
    public void onRecieve(String type, String message) {

    }





    private void sendCustomPacket(Packet packet) {
        if (assumedChannel != null) {
            packet.tagWithChannel(assumedChannel);
        }
        if (assumedLobby != null) {
            packet.tagWithLobby(assumedLobby);
        }
        try {
            this.outStream.writeObject(packet);
            if (seeInfoMessages) {
                System.out.println(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onPacketRecieve(Packet packet) {

        // Check if the server is trying to communicate with GBSNetworkApp
        if (packet.getCommand() != null) {

            String command = packet.getCommand();

            if (command.equals("updateLobby")) {
                assumedLobby = packet.getMessage();
            } else if (command.equals("updateChannel")) {
                assumedChannel = packet.getMessage();
            }

            return;
        }

        if (packet.getType().equals("info") && seeInfoMessages) {
            System.out.println(packet);
        }

        // Give the message to the user
        if (!packet.getType().equals("info")) {
            this.onRecieve(packet.getType(), packet.getMessage());
        }
    }
}
