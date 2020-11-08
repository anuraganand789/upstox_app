package com.upstox.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.nio.charset.CharacterCodingException;
import java.net.http.HttpRequest;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.Socket;
import java.net.ServerSocket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.List;
import java.util.regex.Pattern;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
*  This is an implementation for a WebSocket using Socket classes in java.
*  It can make establish a connection with a incoming socket. 
*  DataFrame handling is not properly implemented
*/
public class SocketService{

    /**
    *  Contains a queue of subscribers
    */
    private final BlockingQueue<SocketChannel> subscribers = new ArrayBlockingQueue<>(10);
    /**
    *  This string is used for calculatig a response key for to be sent to the websocket client
    */
    private static final String magicKey = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"; 

    /**
    *  Reads request from websocket client
    *  @param channel the socket channel which needs to be read
    */
    private String readRequest(final SocketChannel channel) throws IOException {
	final StringBuilder request = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(512);
        while (channel.read(buffer) != -1) {
            // read the complete HTTP request headers, there should be no body
            CharBuffer decoded;
            buffer.flip();
            try {
                decoded = ISO_8859_1.newDecoder().decode(buffer);
            } catch (CharacterCodingException e) {
                throw new UncheckedIOException(e);
            }
            request.append(decoded);
            if (Pattern.compile("\r\n\r\n").matcher(request).find()) return request.toString();
            buffer.clear();
        }
        return request.toString();
    } 

    /**
    *  Writes a respose to the target socketchannel, by wrapping it in a FrameData.
    *  @param channel socket channel where data needs to be written
    *  @param respose  the list of respone which needs to be written to the socketchannel
    */
    private void writeResponse(SocketChannel channel, List<String> response) throws IOException {
        String s = response.stream().collect(Collectors.joining("\r\n")) + "\r\n\r\n";
        ByteBuffer encoded;
        try {
            encoded = ISO_8859_1.newEncoder().encode(CharBuffer.wrap(s));
        } catch (CharacterCodingException e) {
	    e.printStackTrace();
            throw new UncheckedIOException(e);
        }
        
	final byte[] strData = s.getBytes();

        String res= "Framedata{ frame-opcode:" + 1 + 
	                  ", frame-fin:"  + 1 + 
	                  ", frame-rsv1:" + 0 + 
	    	      ", frame-rsv2:" + 0 + 
	                  ", frame-rsv3:" + 0 + 
	                  ", frame-payload-length:" + strData.length + 
	                  "], frame-payload-data:" + s + '}';
        channel.write(ByteBuffer.wrap(res.getBytes()));
    }
    /**
    *  reads data from the socket and returns a string of those response
    *  @param socketChannel the channel from where data needs to read
    */
    private static String readSocketChannel(final SocketChannel socketChannel) throws IOException{
        StringBuilder requestString = new StringBuilder(100);
	ByteBuffer buffer = ByteBuffer.allocate(512);

	String strValue;
	while(socketChannel.read(buffer) > 0){
	    buffer.flip();
	    strValue = new String(buffer.array());
	    buffer.clear();
	    System.out.print(strValue);
	    requestString.append(strValue);
	}
	buffer.clear();
	return requestString.toString();
    }

    /**
    *  Handler class for incoming socket connection request
    */
    private class SocketHandler implements Runnable{

	private final SocketChannel socketChannel;

	public SocketHandler(final SocketChannel socketChannel){
	    this.socketChannel = socketChannel;
	    try{
	        this.socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
                this.socketChannel.configureBlocking(false);
	    }catch(IOException ex){
	        ex.printStackTrace();
	    }
	}

        @Override
	public void run(){
	    MessageDigest sha1 = null;
	    try{
	        sha1 = MessageDigest.getInstance("SHA-1");
	    }catch(NoSuchAlgorithmException ex){
		ex.printStackTrace();
	        return;
	    }
	    try {
		boolean handshakeCompleted = false;
		List<String> strings = new ArrayList<>(); 
                //final String requestString = readSocketChannel(socketChannel);
                String requestString = "";
	        try{
		    requestString = readRequest(socketChannel);
		}catch(IOException ex){
		    ex.printStackTrace();
		}
		final String[] headers = requestString.toString().split("\r\n");
		List<String> headerList = Arrays.asList(headers);

		StringWriter socketWriter = new StringWriter(100);
		if(!headerList.isEmpty()){
		    String webSocketKey = headerList.stream().filter(str -> str.startsWith("Sec-WebSocket-Key")).findFirst().get().trim();
		    webSocketKey = webSocketKey.split(":")[1].trim();
		    socketWriter.write("HTTP/1.1 101 Switching Protocols");
		    socketWriter.write("\r\n");
                    socketWriter.write("Upgrade: websocket");
		    socketWriter.write("\r\n");
                    socketWriter.write("Connection: Upgrade");
		    socketWriter.write("\r\n");
		    webSocketKey += magicKey;
		    sha1.reset();
		    sha1.update(webSocketKey.getBytes(ISO_8859_1));
		    webSocketKey = Base64.getEncoder().encodeToString(sha1.digest());
                    socketWriter.write("Sec-WebSocket-Accept:" + webSocketKey);
		    socketWriter.write("\r\n");
		    socketWriter.write("\r\n");
		    socketWriter.flush();
		    socketChannel.write(ByteBuffer.wrap(socketWriter.toString().getBytes()));
		    System.out.println("handshake completed");
		}
	        socketWriter = null; 
		while(socketChannel.isConnected()){
			try{
			       Thread.sleep(100);
			       writeResponse(socketChannel, Arrays.asList("hi"));
		               final String strValue = readRequest(socketChannel);
			       System.out.println("from client " + strValue);
		               if(!strValue.trim().isEmpty()){
			           writeResponse(socketChannel, Arrays.asList("hi"));
		                }
			   }catch(IOException | InterruptedException ex){
			       ex.printStackTrace();
			   }
		}
	    }catch(IOException ex){
	        ex.printStackTrace();
	    }
	}
    }
    
    public static void main(final String ... args) throws IOException{
        final SocketService obj = new SocketService();
	final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 9000));
	while(true){
	    final SocketChannel socketChannel = serverSocketChannel.accept();
	    obj.subscribers.add(socketChannel);
	    new Thread(obj.new SocketHandler(socketChannel)).start();  
	}
    }
}
