package gbs.server.channel;

import gbs.Packet;

public class LabXChannel extends Channel {

    public LabXChannel() {
        super("labxs");
        setSize(4);
    }

    

    @Override
    public void onRecieve(Packet packet) {
        super.onRecieve(packet);

        
    }

    
    


}
