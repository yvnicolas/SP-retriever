package com.dynamease.serviceproviders.user;

import java.io.Serializable;

public class CurrentUserContextImpl implements CurrentUserContext, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    private String currentId = null;

    public CurrentUserContextImpl() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void connect(String id) {
        currentId = id;
     
    }

    @Override
    public void disconnect() {
        currentId=null;

    }

    @Override
    public String getId() {
        
        return currentId;
    }

    @Override
    public boolean isConnected() {
       
        return !(currentId==null);
    }

}
