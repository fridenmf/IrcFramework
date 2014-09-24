package com.fridenmf.ircframework.core.extendable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.fridenmf.ircframework.core.serverCommunication.IrcServerReader;
import com.fridenmf.ircframework.core.serverCommunication.IrcServerWriter;

/**
 * This class is used if the implementation needs many plugins
 * @author friden
 */
public abstract class IrcBot {
	
	private String user = null;
	private String nick = null; 
	private String description = null;
	
	private IrcServerReader serverReader = null;
	private IrcServerWriter serverWriter = null;
	
	private ArrayList<IrcPlugin> plugins = null;
	
	public IrcBot(){
		plugins = new ArrayList<IrcPlugin>();
	}
	
	public void connect(String host, int port, String nick, String user) {
		connect(host, port, nick, user, null);
	}

	public void connect(String host, int port, String nick, String user, String description) {
		
		try {
			
			Socket socket = new Socket(host, port);
			
			if(serverWriter != null){
				serverWriter.stopWriter();
			}
			serverWriter = new IrcServerWriter(this, socket);
			serverWriter.start();
			
			if(serverReader != null){
				serverReader.stopReader();
			}
			serverReader = new IrcServerReader(this, socket);
			serverReader.start();
			
			startPlugins();
			
			if(nick == null || user == null){
				System.err.print("ERROR: Nick and user must be something else than null");
				System.exit(0);
			}else{
				this.nick = nick;
				this.user = user;
				this.description = description;
				serverWriter.nick(nick);
				serverWriter.user(user, description == null ? "" : description);
			}
			
			serverWriter.pong("pinglepongle");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void startPlugins() {
		for (int i = 0; i < plugins.size(); i++) {
			if(plugins.get(i).isAlreadyStarted()){
				plugins.get(i).restart();
			}else{
				plugins.get(i).start();
			}
		}
	}
	
	private void stopPlugins() {
		for (int i = 0; i < plugins.size(); i++) {
			plugins.get(i).stopPlugin();
		}
	}

	public ArrayList<IrcPlugin> getPlugins(){
		return this.plugins;
	}
	
	public void addPlugin(IrcPlugin ircPlugin){
		this.plugins.add(ircPlugin);
	}
	
	/**
	 * WARNING, do not use this method. It is used from within the framework. Use methods in IrcPlugin instead. 
	 */
//	private void setNick(String nick){
//		this.nick = nick;
//	}
	
	public String getNick(){
		return this.nick;
	}
	
	/**
	 * WARNING, do not use this method. It is used from within the framework. Use methods in IrcPlugin instead. 
	 */
//	public void setUser(String user){
//		this.user = user;
//	}
	
	public String getUser(){
		return this.user;
	}
	
	public String getDescription(){
		return this.description;
	}

	/**
	 * This method accesses underlaying communications, use with care 
	 */
	public IrcServerWriter getIrcWriter() {
		return this.serverWriter;
	}
	
	public IrcServerReader getIrcReader(){
		return this.serverReader;
	}
	
	public void setVerboseIn(boolean b){
		getIrcReader().setVerbose(b);
	}
	
	public void setVerboseOut(boolean b){
		getIrcWriter().setVerbose(b);
	}
	
	/**
	 * This method opens a socket on port 113 to listen for Ident auth from server, and responds with user. 
	 * This socket times out after 2000 ms if no connection was made, returning false. If the server 
	 * successfully connected to socket and the user was sent, true is returned. 
	 */
	public boolean listenAndRespondToAuth(String user){
		try {
			ServerSocket ss = new ServerSocket(113);
			ss.setSoTimeout(2000);
			Socket s = ss.accept();
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			String line = br.readLine();
			String str1 = line.split("[ ]")[0];
			String str2 = line.split("[ ]")[2];
			bw.write(str1 + ", "+str2+" : USERID : UNIX : "+user+"\r\n");
			bw.flush();
			try {
				/* If we shut this down to early the server wont be able to read it */
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			br.close();
			bw.close();
			s.close();
			ss.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void writerIsDead() {
		serverReader.stopReader();
		stopPlugins();
	}

	public void readerIsDead() {
		serverWriter.stopWriter();
		stopPlugins();
	}

}
