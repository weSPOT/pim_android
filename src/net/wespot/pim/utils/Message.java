package net.wespot.pim.utils;

import org.celstec.arlearn.delegators.INQ;
import org.celstec.dao.gen.AccountLocalObject;

import java.util.HashMap;
import java.util.Map;

/**
 * ****************************************************************************
 * Copyright (C) 2014 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Angel Suarez
 * Date: 13/10/14
 * ****************************************************************************
 */

public class Message {
    String author;
    Long id;
    String message;
    long dateTime;
    boolean isMine;
    boolean sync;
    public boolean isStatusMessage;
    Map<String, String> accountNamesID = new HashMap<String, String>();


    public Message(Long id, String message, String author, long dateTime, boolean sync ){
        super();
        this.id = id;
        this.message = message;
        this.isMine = isMine;
        this.author = author;
        this.sync = sync;

        if (!accountNamesID.containsKey(this.author)){
            AccountLocalObject accountLocalObject = INQ.accounts.getAccount(this.author);
            if (accountLocalObject!=null){
                accountNamesID.put(accountLocalObject.getFullId(), accountLocalObject.getName());
            }else{
                accountNamesID.put(this.author,"-");
            }
        }


        if (INQ.accounts.getLoggedInAccount().getFullId().equals(this.author) ) {
            this.isMine = true;
        }else{
            this.isMine = false;
        }

        this.author = accountNamesID.get(this.author);

        this.dateTime = dateTime;
        this.isStatusMessage = false;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isStatusMessage() {
        return isStatusMessage;
    }

    public void setStatusMessage(boolean isStatusMessage) {
        this.isStatusMessage = isStatusMessage;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
