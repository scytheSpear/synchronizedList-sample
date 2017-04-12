/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itsys.vncLog.entities;

import com.itsys.vncLog.Handle_Client_Request_Thread;
import com.itsys.vncLog.jdbc.JdbcConnection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class RelationList {

    volatile ArrayList<VncRelation> relationList;

    public RelationList() {
        relationList = new ArrayList<VncRelation>();
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

    public void addRelation(VncRelation fr) {
        relationList.add(fr);

    }

    public void removeRelation(VncRelation fr, JdbcConnection jdbc) {

        relationList.remove(fr);
    }

    public String FindRelationId(VncRelation relation) throws ParseException {
        String id = null;

        for (Iterator<VncRelation> iterator = relationList.iterator(); iterator.hasNext();) {

            VncRelation s = iterator.next();
            System.out.println("for loop for relationList");
//            System.out.println("rl= " + s.toString());
//            System.out.println("relation = " + relation.toString());

            if (s.getClientIp().equals(relation.getClientIp()) && s.getServerIp().equals(relation.getServerIp())
                     && s.getEndTime() == "0") {

                id = s.getId();

                System.out.println("get relation id for vnc log");
            }
        }

        return id;
    }

    public void LoopArraylist(VncRelation relation, JdbcConnection jdbc, File file) throws ParseException {
        String id = null;

        for (Iterator<VncRelation> iterator = relationList.iterator(); iterator.hasNext();) {

            VncRelation s = iterator.next();
            System.out.println("for loop for relationList");
            System.out.println(s.toString());

            if (s.getClientIp().equals(relation.getClientIp()) && s.getServerIp().equals(relation.getServerIp())
                    && s.getEndTime() == "0") {

                s.setEndTime(relation.getEndTime());

                id = s.getId();

                System.out.println("renew end time" + s.getEndTime() + " for id " + id);

                try {
                    String sqlString = jdbc.update(s);
                    if (sqlString != null) {
                        writeToFile(sqlString, file);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Handle_Client_Request_Thread.class.getName()).log(Level.SEVERE, null, ex);
                }

                iterator.remove();
                System.out.println("send to Mysql and remove from list");
            }
        }
    }

    public Date TimeConvert(String s) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return simpleDateFormat.parse(s);
    }

    public ArrayList<VncRelation> GetRelationList() {
        return relationList;
    }

}
