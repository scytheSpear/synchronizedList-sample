/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itsys.portLink.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author user
 */
public class RelationList {

//    private volatile ArrayList<PortLink> relationList = new ArrayList<PortLink>();
    
    private List<PortLink> relationList =Collections.synchronizedList(new ArrayList<PortLink>());
    
    

    public RelationList() {
//        this.relationList = Collections.synchronizedList(new ArrayList<PortLink>());
//        this.relationList = new ArrayList<PortLink>();
    }

    public synchronized void addRelation(PortLink pl) {
        this.relationList.add(pl);

    }

    public synchronized void removeRelation(int i) {
        this.relationList.remove(i);
    }


    public synchronized List<PortLink> getRelationList() {

        return this.relationList;

    }

}
