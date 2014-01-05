package com.dynamease.serviceproviders.user;

public class CurrentUserContextImpl implements CurrentUserContext {
    
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
