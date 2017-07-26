/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.util;


import java.io.Serializable;
import org.zoodb.api.impl.ZooPC;

/**
 * All classes that are stored in the backend need a unique identifier,
 * all further classes inherit from this
 * @author dominik.kopczynski
 */
public class IdObject extends ZooPC implements Serializable {
    
    private static final long serialVersionUID = -7906158551970915613l;
    
    /**
     * unique identifier
     */
    private long id;
    /**
     * indecates if the object is already stored in the db
     */
    private boolean storedInDB = false;
    /** 
     * flag if object is a first level object or not
     */
    private boolean firstLevel = false;
    
    public IdObject(){}
    
    public void setId(long id){
        zooActivateWrite();
        this.id = id;
    }
    
    public boolean getFirstLevel(){
        zooActivateRead();
        return firstLevel;
    }
    
    public void setFirstLevel(boolean firstLevel){
        zooActivateWrite();
        this.firstLevel = firstLevel;
    }
    
    public long getId(){
        zooActivateRead();
        return id;
    }
    
    public boolean getStoredInDB(){
        zooActivateRead();
        return storedInDB;
    }
    
    
    public void setStoredInDB(boolean storedInDB){
        zooActivateWrite();
        this.storedInDB = storedInDB;
    }
}