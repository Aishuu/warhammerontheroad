package fr.eurecom.warhammerontheroad;

import java.util.ArrayList;
import java.util.Collection;

public class Chat {
    private final Collection<ChatListener> chatListeners = new ArrayList<ChatListener>();
    
    public void receiveMessage(String name, String message) {
    	// TODO: store messages in history
    	fireMessageReceived(name, message);
    }
    
    public void userDisconnected(String name) {
    	// TODO: store in history
    	fireUserDisconnected(name);
    }
    
    public void userConnected(String name) {
    	// TODO: store in history
    	fireUserDisconnected(name);
    }
    
    private void fireUserDisconnected(String name) {
    	for(ChatListener l: chatListeners)
    		l.userDisconnected(name);
    }
    
    private void fireMessageReceived(String name, String message) {
    	for(ChatListener l: chatListeners)
    		l.messageReceived(name, message);
    }
    
    public void addChatListener(ChatListener listener) {
        chatListeners.add(listener);
    }
    
    public void removeChatListener(ChatListener listener) {
        chatListeners.remove(listener);
    }
}
