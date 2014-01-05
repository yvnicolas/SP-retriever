/**
 * 
 */
package com.dynamease.serviceproviders.user;

/**
 * Interface to access current user context info. Written for correct handling of session-scoped
 * spring beans with services as in section 5.5.4 of spring reference manual : the ability to access
 * a session-scoped object in a singleton-scope object needs the session-scoped object to be
 * injected as an interface in the singleton object and the scope value mentionning the interface
 * proxy aop mechanism.
 * 
 * @author Yves Nicolas
 * 
 */
public interface CurrentUserContext {

    public void connect(String id);

    public void disconnect();

    public String getId();

    public boolean isConnected();
}
