package bgu.spl.net.srv;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class BGRSMessagingProtocol implements MessagingProtocol<Serializable> {
    private boolean shouldTerminate = false;

    @Override
    public Serializable process(Serializable msg) {
        Serializable output = ((Command)msg).execute(Database.getInstance());
        
        return output;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void terminate(){
        shouldTerminate = true;
    }
}
