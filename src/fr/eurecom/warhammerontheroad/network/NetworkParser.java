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
import java.util.Timer;
import java.util.TimerTask;

import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.WotrService;
import android.os.Environment;
import android.util.Log;
import static fr.eurecom.warhammerontheroad.network.Cmds.*;

/**
 * Class used to communicate with the server. This class is stateless.
 *  The thread will stop when the connection is lost and you can instantiate 
 *  a new NetworkParser and set the autobind so that it will automatically bind 
 *  to the game after connecting.
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

	private Socket sock;
	private WotrService mService;
	private boolean autobind;
	private ArrayList<String> filesToSend;
	private Timer timer;

	/**
	 * Default constructor
	 * 
	 * @param mService link to the service
	 */
	public NetworkParser(WotrService mService) {
		super();
		this.sock = null;
		this.mService = mService;
		this.autobind = false;
		this.filesToSend = new ArrayList<String>();
	}

	/**
	 * Tell the endpoint if it should try to autobind at startup
	 * 
	 * @param autobind wether it should autobind
	 */
	public void setAutobind(boolean autobind) {
		this.autobind = autobind;
	}

	@Override
	public void run(){
		BufferedReader in;
		try {
			// open the socket to the server
			connect(NetworkParser.SERVER_ADDR, NetworkParser.SERVER_PORT);

			// autobind
			if(this.autobind)
				this.bind(this.mService.getGame().getIdGame());

			// BufferedReader from the socket
			in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));

			// read from socket
			String line = "";
			int r;
			while ((r = in.read()) != -1) {
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
				// TODO: add error codes
				if(parts[0].equals(CMD_ERROR))
					Log.e(TAG, "Error from server : ".concat(parts[1]));

				// Message broadcasted
				else if(parts[0].equals(CMD_MESSAGE)) {
					if(parts.length < 3) {
						line = "";
						continue;
					}
					String msg = line.split("#", 3)[2];
					mService.getChat().receiveMessage(parts[1], msg);
				}

				// User disconnected
				else if(parts[0].equals(CMD_DISCONNECTED)) {
					mService.getChat().userDisconnected(parts[1]);
					mService.getGame().userDisconnected(parts[1]);
				}

				// User connected
				else if(parts[0].equals(CMD_CONNECTED)) {
					mService.getChat().userConnected(parts[1]);
					mService.getGame().userConnected(parts[1]);
				}

				// Id of the game to be created
				else if(parts[0].equals(CMD_ANS_CREATE)) {
					if(parts[1].length() == 0) {
						line = "";
						continue;
					}
					try {
						int id = Integer.parseInt(parts[1]);
						bind(id);
						mService.getGame().setIdGame(id);
					}catch(NumberFormatException e){
						Log.e(TAG, "This is no fuckin number !");
					}
				}

				// Answer to the LIST command
				else if(parts[0].equals(CMD_ANS_LIST)) {
					ArrayList<String> avail = new ArrayList<String>();
					for(int i=1;i<parts.length;i++) {
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

				// Ready to broadcast a file on this port
				else if(parts[0].equals(CMD_PORT)) {
					if(parts.length < 3) {
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
											return; // TODO: notify user
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
					// TODO: no such file was sent
				}

				// A file is available on this port
				else if(parts[0].equals(CMD_FILE)) {
					if(parts.length < 4) {
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
						Log.d(TAG, "File successfully broadcasted !");
						// TODO: notify sender
					}
				}
				line = "";
			}
		} catch (IOException e) {
			Log.e(TAG, "Socket is disconnected...");

			// TODO: connection lost
		} finally {
			this.mService.setConnected(false);
		}
	}

	/**
	 * Send a command to the Wotr Server
	 * 
	 * @param command The command to send
	 * @param args List of arguments
	 */
	private void sendCommand(final String command, final String... args) {
		if(this.sock == null || !this.sock.isConnected()) {
			Log.e(TAG, "Socket not connected !");

			// TODO: connection lost
			return;
		}

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
				} catch (IOException e) {
					// TODO: connection lost
					Log.e(NetworkParser.TAG, "Could not send command...");
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
		this.sendCommand(CMD_MESSAGE, message);
	}

	/**
	 * Bind to a game
	 * 
	 * @param id Id of the game to bind to
	 */
	public void bind(int id) {
		Game game = this.mService.getGame();
		if(game.getState() != Game.STATE_NO_GAME && game.getState() != Game.STATE_GAME_CREATE_WAIT)
			return;
		if(id < 100000) {
			this.sendCommand(CMD_BIND, String.format("%05d", id), this.mService.getName());
			game.change_state(Game.STATE_GAME_CREATED);
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
		this.sendCommand(CMD_CREATE);
		this.mService.getGame().change_state(Game.STATE_GAME_CREATE_WAIT);
	}

	/**
	 * Broadcast a file
	 * 
	 * @param filename name of the file to broadcast
	 */
	public void sendFile(String filename) {
		File file=new File(Environment.getExternalStorageDirectory(), filename);
		if(!file.exists() || file.length() == 0)
			return;	// TODO: notify the caller
		this.sendCommand(CMD_FILE, String.format("%05d", file.length()), filename);
		this.filesToSend.add(filename);
	}

	public void tryToReconnect(final long timeout) {
		if(this.timer != null)
			return;		// already trying to reconnect
		this.timer =  new Timer();
		this.timer.scheduleAtFixedRate(new TimerTask(){ public void run() {onTimerTick();}}, 0, timeout);				
	}

	private void onTimerTick() {
		Log.i(TAG, "Trying to reconnect...");
		try {
			// open the socket to the server
			connect(NetworkParser.SERVER_ADDR, NetworkParser.SERVER_PORT);

			// autobind
			if(this.autobind)
				this.bind(NetworkParser.this.mService.getGame().getIdGame());

			this.timer.cancel();
			this.timer = null;
			new Thread(this).start();
		} catch (IOException e) {
			Log.w(TAG, "Reconnection failed !");
			e.printStackTrace();
		}
	}

}