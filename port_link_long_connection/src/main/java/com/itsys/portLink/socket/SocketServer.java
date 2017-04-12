package com.itsys.portLink.socket;

import java.io.FileInputStream;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class SocketServer {

    private static final Logger LOG = Logger.getLogger(SocketServer.class);

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("port-server-conf.properties"));
        } catch (IOException e) {
            System.err.println("Could not read properties file");
            System.exit(-1);
        }

        int port = Integer.valueOf(properties.getProperty("port"));
        System.out.println("read port value " + port + " from properties file");
        int timeInterval = Integer.valueOf(properties.getProperty("timeInterval"));
        System.out.println("read timeInterval in minutes, value " + timeInterval + " from properties file");

        ServerSocket serverSocket = null;
        Socket client = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();

            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }

        System.out.println("Server is ready ");


        while (true) {
            try {
                client = serverSocket.accept();
                Handle_Client_Request_Thread s = new Handle_Client_Request_Thread(client, timeInterval);
                s.start();
//                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
