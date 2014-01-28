package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;
import java.util.Collection;

import fr.eurecom.warhammerontheroad.application.ChatListener;

public class Chat {
    private final Collection<ChatListener> chatListeners = new ArrayList<ChatListener>();
    
    public void receiveMessage(String name, String message) {
    	// TODO: store messages in history
    	fireMessageReceived(name, message);
    }
    
    public void receivePrivateMessage(String name, String message) {
    	// TODO: store messages in history
    	firePrivateMessageReceived(name, message);
    }
    
    public void userDisconnected(String name) {
    	// TODO: store in history
    	fireUserDisconnected(name);
    }
    
    public void userConnected(String name) {
    	// TODO: store in history
    	fireUserConnected(name);
    }
    
    public void fileTransferStatusChanged(String name, int status) {
    	for(ChatListener l: chatListeners)
    		l.fileTransferStatusChanged(name, status);
    }
    
    private void fireUserDisconnected(String name) {
    	for(ChatListener l: chatListeners)
    		l.userDisconnected(name);
    }
    
    private void fireUserConnected(String name) {
    	for(ChatListener l: chatListeners)
    		l.userConnected(name);
    }
    
    private void fireMessageReceived(String name, String message) {
    	for(ChatListener l: chatListeners)
    		l.messageReceived(name, message);
    }
    
    private void firePrivateMessageReceived(String name, String message) {
    	for(ChatListener l: chatListeners)
    		l.privateMessageReceived(name, message);
    }
    
    public void addChatListener(ChatListener listener) {
        chatListeners.add(listener);
    }
    
    public void removeChatListener(ChatListener listener) {
        chatListeners.remove(listener);
    }
}
