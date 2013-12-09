package fr.eurecom.warhammerontheroad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.util.Log;
import static fr.eurecom.warhammerontheroad.Cmds.*;

public class NetworkParser extends Thread {
	private final static String TAG =	"NetworkParser";

	public final static String SERVER_ADDR = 	"82.236.41.149";
	public final static int SERVER_PORT = 		33000;

	Socket sock;
	WotrService mService;

	public NetworkParser(WotrService mService) {
		super();
		this.sock = null;
		this.mService = mService;
	}

	@Override
	public void run(){
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
			String line = "";
			int r;
			while ((r = in.read()) != -1) {
				char ch = (char) r;
				if(ch != '\n') {
					line = line + ch;
					continue;
				}

				String[] parts = line.split("#", -1);
				if(parts.length < 2) {
					line = "";
					continue;
				}

				Log.d(TAG, "message : "+line);

				if(parts[0].equals(CMD_ERROR))
					Log.e(TAG, "Error from server : ".concat(parts[1]));
				else if(parts[0].equals(CMD_MESSAGE)) {
					if(parts.length < 3) {
						line = "";
						continue;
					}
					mService.receiveMessage(parts[1], parts[2]);
				}
				else if(parts[0].equals(CMD_DISCONNECTED))
					mService.userDisconnected(parts[1]);
				else if(parts[0].equals(CMD_CONNECTED))
					mService.userConnected(parts[1]);
				else if(parts[0].equals(CMD_ANS_CREATE)) {
					if(parts[1].length() == 0) {
						line = "";
						continue;
					}
					// TODO: check if it really is a number
					int id = Integer.parseInt(parts[1]);
					mService.bind(id);
					mService.getGame().setIdGame(id);
				}
				else if(parts[0].equals(CMD_ANS_LIST)) {
					ArrayList<String> avail = new ArrayList<String>();
					for(int i=1;i<parts.length;i++) {
						if(parts[i].length() < 5)
							continue;
						// TODO: check if integer
						String l = new String(parts[i].substring(0, 5));
						String name = parts[i].substring(5);
						if(name.length() == 0)
							l = l+"   (empty)";
						else
							l = l+" : "+name;
						avail.add(l);
					}
					mService.getGame().listAvailableGames(avail);
				}
				line = "";
			}
		} catch (IOException e) {
			Log.e(TAG, "Socket is disconnected...");
		}

	}

	public void sendCommand(String command, String... args) throws IOException {
		if(this.sock == null || !this.sock.isConnected())
			throw(new IOException("Socket not connected"));

		DataOutputStream dos = new DataOutputStream(this.sock.getOutputStream());
		dos.write(command.getBytes());
		dos.writeByte('#');

		for(int i = 0; i < args.length; i++) {
			dos.write(args[i].getBytes());
			if(i != args.length-1)
				dos.writeByte('#');
		}
		dos.writeByte('\n');
	}

	public void connect(String addr, int port) throws IOException {
		this.sock = new Socket(InetAddress.getByName(addr), port);
	}

	public void close() {
		if(this.sock != null)
			try {
				this.sock.close();
				this.sock = null;
			} catch (IOException e) {
				Log.e(TAG, "Couldn't close the socket...");
			}
	}
}
