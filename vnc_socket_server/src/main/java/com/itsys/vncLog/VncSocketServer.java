package com.itsys.vncLog;

import com.itsys.vncLog.entities.VncRelation;
import com.itsys.vncLog.entities.RelationList;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class VncSocketServer {

    private static final Logger LOG = Logger.getLogger(VncSocketServer.class.getName());

//    volatile ArrayList<VncRelation> relationList = new ArrayList<VncRelation>();

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;

        Socket socketclient;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(6222);
        } catch (IOException e) {

            System.err.println("Could not listen on port: 6222");
            System.exit(-1);
        }

        System.out.println("Server is ready ");

//        Handle_Client_Request_Thread t = new Handle_Client_Request_Thread(client, timeInterval);
        RelationList rl = new RelationList();

        while (listening) {

            socketclient = serverSocket.accept();
            LOG.info("接收到请求");
            //get date as filename
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            //check file if exist, create new file if not
            File vncFile = new File("/itsys-data/vnc-data/vnc_log" + date + ".txt");
//            File vncFile = new File("vnc_log" + date + ".txt");
            vncFile.createNewFile();

            File vncRelationFile = new File("/itsys-data/vnc-data/vnc_relation" + date + ".txt");
//            File vncRelationFile = new File("vnc_relation" + date + ".txt");
            vncRelationFile.createNewFile();

            Handle_Client_Request_Thread t = new Handle_Client_Request_Thread(socketclient, vncFile, vncRelationFile, rl);
            t.start();

        }

        serverSocket.close();

    }
}
