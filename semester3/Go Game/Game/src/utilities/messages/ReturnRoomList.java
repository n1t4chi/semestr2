/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import java.io.Serializable;
import java.util.ArrayList;
import utilities.messages.data.RoomInfo;

/**
 * Message class specialised for sending room info data
 * @author n1t4chi
 */
public class ReturnRoomList implements Message{
    /**
     * List of rooms.
     */
    private ArrayList<RoomInfo> list ;
    /**
     * Constructor, Should be used when manually adding room info.
     */
    public ReturnRoomList() {
        list = new ArrayList<>();
    }
    /**
     * Adds one entry into room list stored by this object.
     * @param room Room info to add
     * @throws NullPointerException when given room  is null pointer.
     */
    public void addRoomInfo(RoomInfo room){
        if(room==null) throw new NullPointerException("Null room");
        list.add(room);
    }
    
    /**
     * Default constructor. Ready to send.
     * @param list ArrayList of room info.
     * @throws NullPointerException when given list is null pointer.
     */
    public ReturnRoomList(ArrayList<RoomInfo> list) {
        if(list==null) throw new NullPointerException("Null list");
        this.list = list;
    }

    /**
     * Returns list of rooms. getMessage works the same but does not return ArrayList
     * @return list of rooms.
     */
    public ArrayList<RoomInfo> getList() {
        return list;
    }
            
    
    @Override
    public Serializable getMessage() {
        return getList();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.RETURN_LIST_ROOMS;
    }

    @Override
    public String toString() {
        String rtrn = "There are currently "+list.size()+" rooms<br>";
        int it=1;
        for(RoomInfo ri : list){
            rtrn+="["+it+"]"+ri+"<br>";
            it++;
        }
        return rtrn;
    }
    
    
}
