package gbs.server.channel;

import gbs.Packet;
import gbs.server.BotLobby;
import gbs.server.bot.Connect4Bot;

public class Lab08CChannel extends Channel {
    
    
    
    public Lab08CChannel() {
        super("lab08c");
        setSize(2);
    }

    @Override
    public void onRecieve(Packet packet) {
        super.onRecieve(packet);
        if (packet.getCommand() != null) {
            if (packet.getCommand().equals("joinBotLobby")) {
                

                BotLobby newBotLobby = new BotLobby(this, packet.getMessage(), 1, 1);
                // newBotLobby.add(packet.getSender());
                addLobby(newBotLobby);
                joinLobby(packet.getSender(), packet.getMessage()+"-AI");
                newBotLobby.addBot(new Connect4Bot(newBotLobby));
            }
        }
        
    }

    
}
