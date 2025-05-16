package gbs.server;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import gbs.Packet;

public class ClientHandler implements Runnable {
    private Socket socket;
    // private PrintWriter out;
    // private BufferedReader in;
    private String clientID;
    private String currentChannel;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.currentChannel = "none";
    }

    @Override
    public void run() {
        try {
            this.outStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.inStream = new ObjectInputStream(this.socket.getInputStream());

            // Read client ID
            Packet helloPacket = (Packet) this.inStream.readObject();
            clientID = helloPacket.getMessage();
            Server.registerClient(clientID, this);
            Server.moveToChannel(clientID, this, "general");

            Packet packet;
            while ((packet = (Packet) this.inStream.readObject()) != null) {
                onRecieve(packet);
            }
        } catch (EOFException e) {

        }
        catch (SocketException e) {
            // System.out.println("Connection lost with " + clientID + ".");
        } catch (IOException e) {
            // e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error receiving custom packet from " + clientID + ".");
        } finally {
            cleanup();
        }
    }

    public void onRecieve(Packet packet) {

        // Check for server commands
        if (packet.getCommand() != null) {
            if (packet.getCommand().equals("setChannel")) {
                String newChannel = packet.getMessage();
                if (!Server.isChannel(newChannel)) {
                    sendServerMessage(newChannel + " not a valid channel name.");
                    return;
                }
                Server.moveToChannel(clientID, this, newChannel);
                return;
            } else if (packet.getCommand().equals("listChannels")) {
                sendServerMessage("Channel list: " + Arrays.toString(Server.getChannelNames()));
                return;
            } else if (packet.getCommand().equals("currentChannel")) {
                sendServerMessage("You're currently in the " + currentChannel + " channel.");
                return;
            }
        }

        // Pass off to channel
        Server.getChannel(this.currentChannel).onRecieve(packet);
    }

    public void setCurrentChannel(String newChannelName) {
        this.currentChannel = newChannelName;
    }

    public String getCurrentChannel() {
        return currentChannel;
    }

    public void sendServerMessage(String message) {
        // System.out.println("[Server] -> [" + clientID + "]: " + message);
        try {
            Packet p = new Packet("Server", clientID, message);
            p.tagWithChannel(currentChannel);
            outStream.writeObject(p);
            // System.out.println(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet) {
        sendCustomPacket(packet);
    }




    
    private void sendCustomPacket(Packet packet) {
        try {
            this.outStream.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cleanup() {
        try {
            Server.removeClient(clientID);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return clientID;
    }
}
