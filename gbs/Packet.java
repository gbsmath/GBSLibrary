package gbs;

import java.io.Serializable;

public class Packet implements Serializable {
    private String sender;
    private String reciever;
    private String message;

    private String channel;
    private String lobby;
    private String command;
    private String type;

    public Packet(String sender, String reciever, String message) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.type = "info";
    }

    public void tagWithLobby(String lobbyName) {
        this.lobby = lobbyName;
    }

    public void tagWithChannel(String channelName) {
        this.channel = channelName;
    }

    public void tagWithCommand(String command) {
        this.command = command;
    }

    public void tagWithType(String type) {
        this.type = type;
    }

    public void setReciever(String newReciever) {
        this.reciever = newReciever;
    }

    public String getMessage() {
        return this.message;
    }

    public String getSender() {
        return sender;
    }

    public String getReciever() {
        return reciever;
    }

    public String getChannel() {
        return channel;
    }

    public String getLobby() {
        return lobby;
    }

    public String getCommand() {
        return command;
    }

    public String getType() {
        return type;
    }

    // [labxs](A) user827 -> user182: message
    // [labxs] user827 -> user182: message
    // user827 -> user182: message
    public String toString() {
        String output = "";

        output += this.type;

        if (channel != null) {
            output += "[" + channel + "]";
        }
        if (lobby != null) {
            output += "(" + lobby + ")";
        } 
        output += " " + sender;
        if (command!=null && command.equals("msg")) {
            output += " -> " + reciever;
        }
        output += ": ";

        if (command != null && !command.equals("msg")) {
            output += command + ":";
        }
        output += message;

        return output;
    }
}
