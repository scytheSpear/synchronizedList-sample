///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.itsys.portLink.socket;
//
//import com.itsys.portLink.JDBC.JdbcConnection;
//import com.itsys.portLink.entities.PortLink;
//import com.itsys.portLink.entities.RelationList;
//import java.sql.SQLException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Iterator;
//import static java.util.concurrent.TimeUnit.MILLISECONDS;
//import static java.util.concurrent.TimeUnit.MINUTES;
//
///**
// *
// * @author user
// */
//public class LoopRelationListBackup {
//
//    public LoopRelationListBackup() {
//
//    }
//
//    public String loopArraylist(PortLink relation, String time, int timeInterval, JdbcConnection jdbc, RelationList rl) throws ParseException, SQLException {
//        long MAX_DURATION = MILLISECONDS.convert(timeInterval, MINUTES);
////        long MAX_DURATION = timeInterval * 1000;
//        String id = null;
//        RelationList relationList = rl;
//
//        for (Iterator<PortLink> iterator = relationList.relationList.iterator(); iterator.hasNext();) {
////
////            synchronized (iterator) {
//
//            PortLink pLink = iterator.next();
//
//            System.out.println("for loop for relationList");
//
//            if (pLink.getSourceIp().equals(relation.getSourceIp()) && pLink.getTargetIp().equals(relation.getTargetIp()) && pLink.getSourcePort() == relation.getSourcePort()
//                    && pLink.getTargetPort() == relation.getTargetPort() && pLink.getProtocol().equals(relation.getProtocol()) && pLink.getRemark().equals(relation.getRemark())
//                    && (TimeConvert(time).getTime() - pLink.getEndTime().getTime()) <= MAX_DURATION) {
//
//                pLink.setEndTime(TimeConvert(time));
//
//                id = pLink.getId();
//
//                System.out.println("renew end time" + time + " for id " + id);
//
//            } else if (TimeConvert(time).getTime() - pLink.getEndTime().getTime() >= MAX_DURATION) {
//
//                jdbc.insert(pLink);
//                //send relation to my sql
//                iterator.remove();
//                System.out.println("send to Mysql and remove from list");
//            }
////            }
//        }
//
//        return id;
//    }
//
//    private Date TimeConvert(String s) throws ParseException {
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
//
//        return simpleDateFormat.parse(s);
//    }
//
//}
