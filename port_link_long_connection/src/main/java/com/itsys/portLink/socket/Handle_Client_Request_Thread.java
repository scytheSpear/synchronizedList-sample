package com.itsys.portLink.socket;

import com.itsys.portLink.JDBC.JdbcConnection;
import com.itsys.portLink.entities.PortLink;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import com.itsys.portLink.entities.RelationList;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class Handle_Client_Request_Thread extends Thread {

    private static final Logger LOG = Logger.getLogger(Handle_Client_Request_Thread.class);
    private Socket socket = null;
    private BufferedReader rdr = null;
    private RelationList relationList = null;
    private int timeInterval = 0;

    public Handle_Client_Request_Thread(Socket s, int ti) {
        this.socket = s;
        this.relationList = new RelationList();
//        this.relationList = rl;
        this.timeInterval = ti;
    }

    @Override
    public void run() {
        try {

            socket.setKeepAlive(true);

            System.out.println("servletSocket" + Thread.currentThread().getName());
            rdr = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));

            while (true) {

                if (rdr != null) {
                    String line = rdr.readLine();
                    if (line != null) {
                        System.out.println("SSHLink from Client socket thread " + Thread.currentThread().getName());
                        String a = line.trim();
                        System.out.println(a);
//                        LOG.debug("client :" + a);
//                    LOG.debug("connection dataSource");

                        if (a != null && !a.matches("\\s+")) {

                            AnalyzeThread analyze = new AnalyzeThread(a);
                            analyze.start();

                        } else {
                            System.out.println("recive blank line at Thread:" + Thread.currentThread().getName());
                        }

                    } else {
//                    break;
                        Thread.sleep(500);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Handle_Client_Request_Thread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rdr.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class AnalyzeThread extends Thread {

        private String str;
        private JdbcConnection jdbc;

        public AnalyzeThread(String rawLog) {
            this.str = rawLog;
            this.jdbc = new JdbcConnection();
        }

        private String matchIpv4(String ip) {
            Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d{1,5})");
            Matcher m1 = IP_PATTERN.matcher(ip);

            if (m1.find()) {
                return m1.group();
            } else {
                return "0.0.0.0:00000";
            }
        }

        private Date getDates(String str) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            Date date = null;
            try {
                date = format.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//        System.out.println(date);
            return date;
        }

        @Override
        public void run() {

            if (str == null) {
                LOG.error("");
                return;
            }

            try {
                String[] st = str.split("\\s+");

                if (st != null && st.length >= 7) {

                    System.out.println("com.itsys.portLink.socket.AnalyzeThread.run()" + Thread.currentThread().getId() + Arrays.toString(st));

                    String[] sourceString = matchIpv4(st[3]).split(":");
                    String sourceIp = sourceString[0];
                    String sourcePort = sourceString[1];
                    String[] targetString = matchIpv4(st[4]).split(":");
                    String targetIp = targetString[0];
                    String targetPort = targetString[1];
                    String protocol = st[2];
                    String remark = st[0];
                    String time = st[1];
                    String id = null;

                    if (sourceIp != null && targetIp != null && time != null && sourcePort != null
                            && targetPort != null && protocol != null) {

                        PortLink pLink = PortLink.builder()
                                .protocol(protocol)
                                .sourceIp(sourceIp)
                                .targetIp(targetIp)
                                .targetPort(Integer.parseInt(targetPort))
                                .sourcePort(Integer.parseInt(sourcePort))
                                .startTime(getDates(time))
                                .endTime(getDates(time))
                                .remark(remark)
                                .id(Thread.currentThread().getId() + String.valueOf(System.currentTimeMillis()))
                                .build();
//                    synchronized(relationList){
//                    LoopRelationList lrl= new LoopRelationList();
                        id = LoopRelationList.loopArraylist(pLink, time, timeInterval, jdbc, relationList);
//                    }
//                        System.out.println("relations remain in list " + relationList.getRelationList());

                        if (id == null) {
                            relationList.addRelation(pLink);
                            id = pLink.getId();
                            System.out.println("add new relation to list id " + id);

                        }
                    }
                }
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(AnalyzeThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AnalyzeThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
