package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.BGRSEncoderDecoder;
import bgu.spl.net.srv.BGRSMessagingProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        //initializing database
        Database database = Database.getInstance();

        Server.threadPerClient(
                Integer.parseInt(args[0]),
                BGRSMessagingProtocol::new,
                BGRSEncoderDecoder::new
        ).serve();
    }
}
