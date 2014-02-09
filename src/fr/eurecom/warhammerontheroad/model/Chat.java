package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;
import java.util.Collection;

import fr.eurecom.warhammerontheroad.application.ChatListener;

public class Chat {
    private final Collection<ChatListener> chatListeners;
    private ArrayList<ChatMessage> messages;
    private Game game;
    
    public Chat(Game game) {
    	this.game = game;
    	this.chatListeners =  new ArrayList<ChatListener>();
    	this.messages = new ArrayList<ChatMessage>();
    }
    
    public void registerMessage(ChatMessage cm) {
    	this.messages.add(cm);
    }
    
    public void receiveMessage(String name, String message) {
    	ChatMessage cm = new ChatMessage(name, message, this.game, false);
    	this.messages.add(cm);
    	fireMessageReceived(cm);
    }
    
    public void receivePrivateMessage(String name, String message) {
    	ChatMessage cm = new ChatMessage(name, message, this.game, false);
    	this.messages.add(cm);
    	fireMessageReceived(cm);
    }
    
    public void userDisconnected(String name) {
    	ChatMessage cm = new ChatMessage(name+" disconnected...");
    	this.messages.add(cm);
    	fireMessageReceived(cm);
    }
    
    public void userConnected(String name) {
    	ChatMessage cm = new ChatMessage(name+" is now connected !");
    	this.messages.add(cm);
    	fireMessageReceived(cm);
    }
    
    public void fileTransferStatusChanged(String name, int status) {
    	for(ChatListener l: chatListeners)
    		l.fileTransferStatusChanged(name, status);
    }
    
    private void fireMessageReceived(ChatMessage cm) {
    	for(ChatListener l: chatListeners)
    		l.messageReceived(cm);
    }
    
    public void addChatListener(ChatListener listener) {
        chatListeners.add(listener);
    }
    
    public void removeChatListener(ChatListener listener) {
        chatListeners.remove(listener);
    }
    
    public String describeAsHTML() {
    	String s = "";
    	for(ChatMessage cm: this.messages)
    		s += cm.describeAsHTML() + "<br />";
    	return s;
    }
}
