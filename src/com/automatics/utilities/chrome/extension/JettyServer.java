package com.automatics.utilities.chrome.extension;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class JettyServer 
{
	private Server server;
	public JettyServer() {
		this(4500);
		   WebSocketHandler wsHandler = new WebSocketHandler() {
	            @Override
	            public void configure(WebSocketServletFactory factory) {
	                //factory.register(MyWebSocketHandler.class);
	            	factory.register(WebSocketHandlerForAddIn.class);
	            }
	        };
	        server.setHandler(wsHandler);
	         
	}
	public JettyServer(Integer runningPort) {
		server = new Server(runningPort);
	}
	
	public void setHandler(ContextHandlerCollection contexts) {
		server.setHandler(contexts);
	}
	
	public void start() throws Exception {
		server.start();
	}
	
	public void stop() throws Exception {
		server.stop();
		server.join();
	}
	
	public boolean isStarted() {
		return server.isStarted();
	}
	
	public boolean isStopped() {
		return server.isStopped();
	}
	
}
