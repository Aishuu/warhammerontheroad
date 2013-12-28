package fr.eurecom.warhammerontheroad.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import fr.eurecom.warhammerontheroad.application.ConnectionStateListener;
import fr.eurecom.warhammerontheroad.model.Dice;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.WotrService;
import android.os.Environment;
import android.util.Log;
import static fr.eurecom.warhammerontheroad.network.Cmds.*;

/**
 * Class used to communicate with the server. This class is stateless.
 * 
 * @author aishuu
 *
 */
public class NetworkParser implements Runnable {
	private final static String TAG =	"NetworkParser";

	/**
	 * IP address of the server
	 */
	//public final static String SERVER_ADDR = 	"82.236.41.149";
	public final static String SERVER_ADDR = 	"172.24.10.37";
	//public final static String SERVER_ADDR =	"192.168.0.11";

	/**
	 * Port of the server
	 */
	public final static int SERVER_PORT = 		33000;

	/**
	 * Rate at which the client tries to reconnect
	 */
	public final static long RECO_RATE =		5000;
	
	/**
	 * The file has been successfully transmitted
	 */
	public final static int FILE_SUCCESSFULLY_TRANSMITTED	= 0;
	
	/**
	 * The file does not exist
	 */
	public final static int FILE_DOES_NOT_EXIST				= 1;

	private Socket sock;
	private WotrService mService;
	private ArrayList<String> filesToSend;
	private Timer timer;
	private  boolean mainThreadRunning;
	private boolean connected;
	private boolean mustReconnect;
	private ArrayList<String> delayedCommands;
	private final Collection<ConnectionStateListener> connectionStateListeners = new ArrayList<ConnectionStateListener>();

	/**
	 * Default constructor
	 * 
	 * @param mService link to the service
	 */
	public NetworkParser(WotrService mService) {
		super();
		this.sock = null;
		this.mService = mService;
		this.filesToSend = new ArrayList<String>();
		this.delayedCommands = new ArrayList<String>();
		this.timer = null;
		this.mustReconnect = false;
	}

	@Override
	public void run(){
		this.mainThreadRunning = true;
		BufferedReader in;
		try {
			if(this.sock == null || !this.sock.isConnected())
				// open the socket to the server
				connect(NetworkParser.SERVER_ADDR, NetworkParser.SERVER_PORT);

			// autobind
			this.bind(this.mService.getGame().getIdGame());
			
			// BufferedReader from the socket
			in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
			
			// Reconnection part ended
			this.mustReconnect = false;

			// read from socket
			String line = "";
			int r;
			for(;;) {
				if((r = in.read()) == -1)
					throw(new IOException("No more connected !"));
				char ch = (char) r;
				if(ch != '\n') {
					line = line + ch;
					continue;
				}

				// we got a full command
				String[] parts = line.split("#", -1);
				if(parts.length < 2) {
					line = "";
					continue;
				}

				Log.d(TAG, "message : "+line);

				// Error from the server
				if(parts[0].equals(CMD_ERR))
					Log.e(TAG, "Error from server : "+line.split("#", 2)[1]);

				// Message broadcasted
				else if(parts[0].equals(CMD_MESSAGE)) {
					if(this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT || parts.length < 3) {
						line = "";
						continue;
					}
					String msg = line.split("#", 3)[2];
					mService.getChat().receiveMessage(parts[1], msg);
				}

				// User disconnected
				else if(parts[0].equals(CMD_DISCONNECTED)) {
					if(this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT) {
						line = "";
						continue;
					}
					mService.getChat().userDisconnected(parts[1]);
					mService.getGame().userDisconnected(parts[1]);
				}

				// User connected
				else if(parts[0].equals(CMD_CONNECTED)) {
					if(this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT) {
						line = "";
						continue;
					}
					mService.getChat().userConnected(parts[1]);
					mService.getGame().userConnected(parts[1]);
				}
				
				// A command send only to the GM
				else if(parts[0].equals(CMD_SEND_GM)) {
					if(!this.mService.getGame().isGM() || this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT || parts.length < 3) {
						line = "";
						continue;
					}
					String msg = line.split("#", 3)[2];
					this.mService.getGame().parseGMCommand(parts[1], msg);
					
				}
				
				// A command send only to a player
				else if(parts[0].equals(CMD_SEND_TO_PLAYER)) {
					if(parts.length < 3 || this.mService.getName().compareTo(parts[1]) != 0 || this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT) {
						line = "";
						continue;
					}
					String msg = line.split("#", 3)[2];
					this.mService.getChat().receivePrivateMessage(parts[1], msg);
					
				}
				
				// A command broadcasted by a player
				else if(parts[0].equals(CMD_ACTION)) {
					if(this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT || parts.length < 3) {
						line = "";
						continue;
					}
					String msg = line.split("#", 3)[2];
					this.mService.getGame().parseCommand(parts[1], msg);
					
				}

				// Ready to broadcast a file on this port
				else if(parts[0].equals(CMD_PORT)) {
					if(this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT || parts.length < 3) {
						line = "";
						continue;
					}
					for(String f: this.filesToSend)
						if(f.equals(parts[2])) {
							this.filesToSend.remove(f);
							final int port = Integer.parseInt(parts[1]);
							final String filename = f;
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										Socket file_sock = new Socket(InetAddress.getByName(NetworkParser.SERVER_ADDR), port);
										DataOutputStream dos = new DataOutputStream(file_sock.getOutputStream());
										File file=new File(Environment.getExternalStorageDirectory(), filename);
										if(!file.exists()) {
											Log.e(TAG, "File "+filename+" doesn't exist !");
											NetworkParser.this.mService.getChat().fileTransferStatusChanged(filename, NetworkParser.FILE_DOES_NOT_EXIST);
											return;
										}
										InputStream in = new FileInputStream(file);
										byte[] buffer = new byte[1024];
										long done = 0;
										long size = file.length();
										while (done < size) {
											int read = in.read(buffer);
											if (read == -1) {
												throw new IOException("Something went horribly wrong");
											}
											dos.write(buffer, 0, read);
											done += read;
										}
										in.close();
										dos.flush();
										dos.close();
										try {
											file_sock.close();
										} catch(IOException e) {
											Log.d(TAG, "Socket already closed by server.");
										}
									} catch (IOException e) {
										e.printStackTrace();
										Log.e(TAG, "Socket is disconnected...");
									}

								}

							}).start();
							break;
						}
				}

				// A file is available on this port
				else if(parts[0].equals(CMD_FILE)) {
					if(this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT || parts.length < 4) {
						line = "";
						continue;
					}
					final int port = Integer.parseInt(parts[1]);
					final int size = Integer.parseInt(parts[2]);
					final String filename = parts[3];
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								Socket file_sock = new Socket(InetAddress.getByName(NetworkParser.SERVER_ADDR), port);
								InputStream in = file_sock.getInputStream();
								File file=new File(Environment.getExternalStorageDirectory(), filename);
								OutputStream out = new FileOutputStream(file);
								byte[] buffer = new byte[1024];
								int done = 0;
								while (done < size) {
									int read = in.read(buffer);
									if (read == -1) {
										throw new IOException("Something went horribly wrong");
									}
									out.write(buffer, 0, read);
									done += read;
								}
								in.close();
								out.flush();
								out.close();
								try {
									file_sock.close();
								} catch(IOException e) {
									Log.d(TAG, "Socket already closed by server.");
								} finally {
									Log.i(TAG, "File successfully downloaded !");
								}
							} catch(IOException e) {
								e.printStackTrace();
								Log.e(TAG, "Socket is disconnected...");
							}
						}
					}).start();
				}

				// Acknowledgment
				else if(parts[0].equals(CMD_ACK)) {
					if(parts.length < 2) {
						line = "";
						continue;
					}

					// File successfully broadcasted
					if(parts[1].equals(CMD_FILE)) {
						if(parts.length < 3) {
							line = "";
							continue;
						}
						Log.d(TAG, "File "+parts[2]+" successfully broadcasted !");
						this.mService.getChat().fileTransferStatusChanged(parts[2], NetworkParser.FILE_SUCCESSFULLY_TRANSMITTED);
					}

					// Answer to the LIST command
					else if(parts[1].equals(CMD_LIST)) {
						if(parts.length < 3) {
							mService.getGame().listAvailableGames(null);
							line = "";
							continue;
						}
						ArrayList<String> avail = new ArrayList<String>();
						for(int i=2;i<parts.length;i++) {
							if(parts[i].length() < 5)
								continue;

							String l = new String(parts[i].substring(0, 5));

							try {
								Integer.parseInt(l);
								String name = parts[i].substring(5);
								if(name.length() == 0)
									l = l+"   (empty)";
								else
									l = l+" : "+name;
								avail.add(l);
							} catch(NumberFormatException e){
								Log.e(TAG, "This is no fuckin number !");
							}
						}
						mService.getGame().listAvailableGames(avail);
					}

					// Id of the game to be created
					else if(parts[1].equals(CMD_CREATE)) {
						if(this.mService.getGame().getState() != Game.STATE_GAME_CREATE_WAIT || parts.length < 3 || parts[2].length() == 0) {
							line = "";
							continue;
						}
						try {
							int id = Integer.parseInt(parts[2]);
							bind(id);
						}catch(NumberFormatException e){
							Log.e(TAG, "This is no fuckin number !");
						}
					}
				}
				line = "";
			}
		} catch (IOException e) {
			Log.e(TAG, "Socket is disconnected...");
			this.setConnected(false);
			tryToReconnect(NetworkParser.RECO_RATE);
			this.mustReconnect = true;
		} finally {
			try {
				if(this.sock != null)
					this.sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.mainThreadRunning = false;
		}
	}

	/**
	 * Send a command to the Wotr Server
	 * 
	 * @param command The command to send
	 * @param args List of arguments
	 */
	synchronized private void sendCommand(final String command, final String... args) {

		new Thread (new Runnable() {
			@Override
			public void run() {
				DataOutputStream dos;
				try {
					
					dos = new DataOutputStream(NetworkParser.this.sock.getOutputStream());
					dos.write(command.getBytes());
					dos.writeByte('#');

					for(int i = 0; i < args.length; i++) {
						dos.write(args[i].getBytes());
						if(i != args.length-1)
							dos.writeByte('#');
					}
					dos.writeByte('\n');
					dos.flush();
				} catch (IOException e) {
					Log.e(NetworkParser.TAG, "Could not send command...");
					String l = new String(command);
					l += "#";
					for(int i = 0; i < args.length; i++) {
						l += args[i];
						if(i != args.length-1)
							l += "#";
					}
					NetworkParser.this.delayedCommands.add(l);
					NetworkParser.this.setConnected(false);
					tryToReconnect(NetworkParser.RECO_RATE);
				}
				catch (NullPointerException e) {
					Log.e(NetworkParser.TAG, "Could not send command...");
					String l = new String(command);
					l += "#";
					for(int i = 0; i < args.length; i++) {
						l += args[i];
						if(i != args.length-1)
							l += "#";
					}
					NetworkParser.this.delayedCommands.add(l);
					NetworkParser.this.setConnected(false);
					tryToReconnect(NetworkParser.RECO_RATE);
				}
			}}).start();

	}

	/**
	 * Send a command (as a line) to the Wotr Server
	 * 
	 * @param command The command to send
	 */
	synchronized private void sendCommandLine(final String command) {

		new Thread (new Runnable() {
			@Override
			public void run() {
				DataOutputStream dos;
				try {
					dos = new DataOutputStream(NetworkParser.this.sock.getOutputStream());
					dos.write(command.getBytes());
					dos.writeByte('\n');
				} catch (IOException e) {
					Log.e(NetworkParser.TAG, "Could not send command...");
					//NetworkParser.this.delayedCommands.add(command);
				}
			}}).start();

	}

	/**
	 * Connect the socket to the Wotr Server
	 * 
	 * @param addr Ip address of the server
	 * @param port Port of the server
	 * @throws UnknownHostException If addr can't be resolved
	 * @throws IOException If the socket can't be connected
	 */
	private void connect(String addr, int port) throws UnknownHostException, IOException {
		if(this.sock != null)
			try {
				this.sock.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		this.sock = new Socket(InetAddress.getByName(addr), port);
	}

	/**
	 * close the socket
	 */
	public void close() {
		if(this.sock != null)
			new Thread (new Runnable() {
				public void run() {
					try {
						NetworkParser.this.sock.close();
						NetworkParser.this.sock = null;
					} catch (IOException e) {
						Log.e(TAG, "Couldn't close the socket...");
					}
				}}).start();
	}


	/**
	 * Broadcast a message
	 * 
	 * @param message message to broadcast
	 */
	public void sendMessage(String message) {
		if(this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT)
			return;
		this.sendCommand(CMD_MESSAGE, message);
	}
	
	public void sendAction(int action, Dice d, String... args) {
		if(this.mService.getGame().getState() != Game.STATE_GAME_TURN)
			return;
		String ss = "";
		for(String s: args)
			ss+= s+"#";
		this.sendCommand(CMD_ACTION, d.toString(), ss.substring(0, ss.length()-1));
	}

	/**
	 * Bind to a game
	 * 
	 * @param id Id of the game to bind to
	 */
	public void bind(int id) {
		Game game = this.mService.getGame();
		if((!this.mustReconnect || !game.mustBind()) && (this.mustReconnect || game.mustBind()))
			return;
		if(id > 0 && id < 100000) {
			this.sendCommand(CMD_BIND, String.format("%05d", id), this.mService.getName());
			game.bound(id);
		}
		else
			Log.e(TAG, "bind : number "+id+" is too damn big !");
	}

	/**
	 * List available games
	 */
	public void listAvailableGames() {
		this.sendCommand(CMD_LIST);
	}

	/**
	 * Create new game
	 */
	public void create() {
		if(this.mService.getGame().getState() == Game.STATE_NO_GAME)
			return;
		this.sendCommand(CMD_CREATE);
		this.mService.getGame().waitForId();
	}

	/**
	 * Broadcast a file
	 * 
	 * @param filename name of the file to broadcast
	 */
	public void sendFile(String filename) {
		if(this.mService.getGame().getState() == Game.STATE_NO_GAME || this.mService.getGame().getState() == Game.STATE_GAME_CREATE_WAIT)
			return;
		File file=new File(Environment.getExternalStorageDirectory(), filename);
		if(!file.exists() || file.length() == 0) {
			this.mService.getChat().fileTransferStatusChanged(filename, NetworkParser.FILE_DOES_NOT_EXIST);
			return;
		}
		this.sendCommand(CMD_FILE, String.format("%05d", file.length()), filename);
		this.filesToSend.add(filename);
	}

	synchronized public void tryToReconnect(final long timeout) {
		if(this.timer != null)
			return;		// already trying to reconnect
		this.timer =  new Timer();
		this.timer.scheduleAtFixedRate(new TimerTask(){ public void run() {onTimerTick();}}, 0, timeout);				
	}

	private void onTimerTick() {
		Log.i(TAG, "Trying to reconnect...");
		try {

			connect(NetworkParser.SERVER_ADDR, NetworkParser.SERVER_PORT);
			
			this.bind(this.mService.getGame().getIdGame());

			// Send commands not yet sent
			for(String l : delayedCommands)
				this.sendCommandLine(l);
			
			// FIXME: some of the commands may have failed
			this.delayedCommands = new ArrayList<String>();
			
			if(!this.mainThreadRunning)
				new Thread(this).start();

			// TODO: tell the server we are reconnected

			this.timer.cancel();
			this.timer = null;

			this.setConnected(true);
		} catch (IOException e) {
			Log.w(TAG, "Reconnection failed !");
			e.printStackTrace();
		}
	}

	synchronized public void setConnected(boolean connected) {
		if(connected && !this.connected) {
			Log.i(TAG, "connected...");
			fireConnectionRetrieved();
		}
		else if(!connected && this.connected) {
			fireConnectionLost();
			Log.w(TAG, "disconnected...");
		}
		this.connected = connected;
	}

	private void fireConnectionLost() {
		for(ConnectionStateListener l: connectionStateListeners)
			l.onConnectionLost();
	}

	private void fireConnectionRetrieved() {
		for(ConnectionStateListener l: connectionStateListeners)
			l.onConnectionRetrieved();
	}

	public void addConnectionStateListener(ConnectionStateListener listener) {
		this.connectionStateListeners.add(listener);
	}

	public void removeConnectionStateListener(ConnectionStateListener listener) {
		this.connectionStateListeners.remove(listener);
	}
}