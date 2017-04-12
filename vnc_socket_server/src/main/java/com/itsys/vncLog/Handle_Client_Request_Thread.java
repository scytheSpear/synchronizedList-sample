package com.itsys.vncLog;

import com.itsys.vncLog.entities.VncRelation;
import com.itsys.vncLog.entities.RelationList;
import com.itsys.vncLog.jdbc.JdbcConnection;
import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handle_Client_Request_Thread extends Thread {

    private static final Logger LOG = Logger.getLogger(Handle_Client_Request_Thread.class.getName());

    private Socket socket = null;
    private BufferedReader rdr = null;
    private JdbcConnection jdbc = null;
    private RelationList relationList = null;
    private File vncFile = null;
    private File vncRelationFile = null;

//    single thread handled
//    private volatile ArrayList<FtpRelation> relationList = new ArrayList<FtpRelation>();
    public Handle_Client_Request_Thread(Socket s, File vF, File vrF, RelationList rl) {
        super("HandleClientRequestThread");
        socket = s;
        vncFile = vF;
        vncRelationFile = vrF;
        jdbc = new JdbcConnection();
        relationList = rl;
    }

    public synchronized void writeToFile(String str, File f) {
        try {
            System.out.println("create file writer for file " + f.getName());
            FileWriter fw = new FileWriter(f, true);
            fw.write(str);
            fw.write(System.getProperty("line.separator"));
            fw.flush();
            fw.close();
            System.out.println("writed to file");

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public Date TimeConvert(String s) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return simpleDateFormat.parse(s);
    }

    @Override
    public void run() {
//     socket= so;
        System.out.println("Thread: " + Thread.currentThread().getId());
        try {

            rdr = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));

            while (rdr.ready()) {
//             BufferedInputStream bis = new BufferedInputStream(socket
//                    .getInputStream());
                String clientIp = socket.getRemoteSocketAddress().toString().substring(1).split(":")[0];
                String serverIp = "";
                String vncLog = "N/A";
                String protocol = "vnc";
                String time = "";
                String id = "0";

                String line = rdr.readLine();

                if (line.trim() != null) {
//                    System.out.println("damengSQL from Client socket thread : " + Thread.currentThread().getId());
                    String log = line.trim();
                    System.out.println("message: " + log);

                    if (log.startsWith("connect")) {
                        String[] logArray = log.split("\\s+", 4);

                        serverIp = logArray[1];
                        time = logArray[2] + " " + logArray[3];

                        VncRelation relation = VncRelation.builder()
                                .clientIp(clientIp)
                                .serverIp(serverIp)
                                .startTime(time)
                                .endTime("0")
                                .id(String.valueOf(System.currentTimeMillis()))
                                .build();

                        relationList.addRelation(relation);

                        try {
                            String insertSQLString = jdbc.insert(relation);
                            if (insertSQLString != null) {
                                writeToFile(insertSQLString, vncRelationFile);
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Handle_Client_Request_Thread.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else if (log.startsWith("disconnect")) {

                        String[] logArray = log.split("\\s+", 4);

                        serverIp = logArray[1];
                        time = logArray[2] + " " + logArray[3];

                        VncRelation relation = VncRelation.builder()
                                .clientIp(clientIp)
                                .serverIp(serverIp)
                                .startTime("0")
                                .endTime(time)
                                .id(String.valueOf(System.currentTimeMillis()))
                                .build();

                        relationList.LoopArraylist(relation, jdbc, vncRelationFile);

                    } else if (log.startsWith("log")) {

                        String[] logArray = log.split("\\s+", 5);
                        try {
                            serverIp = logArray[1].replaceAll("[^\\d.:]", "");
                            time = logArray[2] + " " + logArray[3];
                            vncLog = log.split("%::%", 2)[1];

                        } catch (Exception e) {
                        }
                        LOG.log(Level.INFO, "LOG is : {0}", vncLog);

                        if (clientIp != "" && serverIp != "" && time != "" && vncLog != "") {

                            String formatedLog = String.format("%s\t%s\t%s\t%s\t%s", clientIp.split(":")[0], serverIp.split(":")[0], vncLog, protocol, time);
                            System.out.println("formated message: " + formatedLog);

                            VncRelation relation = VncRelation.builder()
                                    .clientIp(clientIp)
                                    .serverIp(serverIp)
                                    .startTime(time)
                                    .endTime(time)
                                    .id(String.valueOf(System.currentTimeMillis()))
                                    .build();

                            id = relationList.FindRelationId(relation);

                            System.out.println(relationList.GetRelationList().toString());

                            try {
                                String logWithId = formatedLog + "\t" + id;
                                writeToFile(logWithId, vncFile);

                            } catch (Exception e) {
                            }

                        } else {
                            LOG.info("message include null message");
                        }
                    } else {
                        LOG.info("bad input message");
                    }
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(Handle_Client_Request_Thread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Handle_Client_Request_Thread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rdr.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
