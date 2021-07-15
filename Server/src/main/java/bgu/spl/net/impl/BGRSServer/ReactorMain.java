package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.BGRSEncoderDecoder;
import bgu.spl.net.srv.BGRSMessagingProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        //initializing database
        Database database = Database.getInstance();

        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]),
                BGRSMessagingProtocol::new,
                BGRSEncoderDecoder::new
        ).serve();

    }
}
