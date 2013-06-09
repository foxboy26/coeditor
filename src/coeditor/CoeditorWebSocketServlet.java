package coeditor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import com.google.gson.Gson;

import db.Config;
import storage.KeyValueStore;

public class CoeditorWebSocketServlet extends WebSocketServlet {

    private final Gson gson = new Gson();

    private final KeyValueStore s3 = new KeyValueStore(Config.bucketName);

    private static final long serialVersionUID = 1L;
    
    private final AtomicInteger connectionIds = new AtomicInteger(0);

    private final Map<Integer, CoeditorMessageInbound> connections = new Hashtable<Integer, CoeditorMessageInbound>();

    private final Map<String, Document> documents = new Hashtable<String, Document>();
    
    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
        return new CoeditorMessageInbound(connectionIds.incrementAndGet());
    }

    private final class CoeditorMessageInbound extends MessageInbound {

    		private final int connectionId;
    		private Document document;
    	
        private CoeditorMessageInbound(int id) {
        	this.connectionId = id;
        }

        @Override
        protected void onOpen(WsOutbound outbound) {

          connections.put(connectionId, this);

        }

        @Override
        protected void onClose(int status) {
            connections.remove(this);
        }

        @Override
        protected void onBinaryMessage(ByteBuffer message) throws IOException {
            throw new UnsupportedOperationException(
                    "Binary message not supported.");
        }

        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {

        	Message msg = gson.fromJson(message.toString(), Message.class);
        	
        	System.out.println("[new message]: " + message.toString());
        	
        	String action = msg.action;
        	if (action != null && action.equals("open")) {
        		if (msg.contentType == Message.STRING) {
          		// step 1: open document for client
        			String clientId = msg.clientId;
          		String docId = msg.content;
          		
          		if (open(clientId, docId)) {
          		// step 2: send back HEADTEXT
          		//String headText = "Hello Xixi!";
	          		String headText = documents.get(docId).headText;
	          		ChangeSet testChangeSet = new ChangeSet(headText);
	          		Message response = new Message("server", "response", Message.CHANGESET, gson.toJson(testChangeSet));
	          		sendMessage(gson.toJson(response));
          		} else {
                System.out.println("[open] error: file cannot be opened.");
                Message errorMsg = new Message("server", "error", Message.STRING, "Unknown action: " + action);
                sendMessage(gson.toJson(errorMsg));
          		}
        		}
        	} else if (action != null && action.equals("close")) {
            Message errorMsg = new Message("server", "error", Message.STRING, action + " is not supported");
            sendMessage(gson.toJson(errorMsg));
        	} else if (action != null && action.equals("save")) {
            Message errorMsg = new Message("server", "error", Message.STRING, action + " is not supported");
            sendMessage(gson.toJson(errorMsg));
          } else if (action != null && action.equals("newChange")) {
            Message errorMsg = new Message("server", "error", Message.STRING, action + " is not supported");
            sendMessage(gson.toJson(errorMsg));
          } else if (action != null && action.equals("sync")) {
            Message errorMsg = new Message("server", "error", Message.STRING, action + " is not supported");
            sendMessage(gson.toJson(errorMsg));
          } else {
            System.out.println("[new message] error: no action");
            Message errorMsg = new Message("server", "error", Message.STRING, "Unknown action: " + action);
            sendMessage(gson.toJson(errorMsg));
          }

        }

        private boolean open(String clientId, String docId) {
        	this.document = new Document(docId, s3);
        	
        	this.document.open(connectionId, clientId);
        	
      		if (!documents.containsKey(docId))
      			documents.put(docId, this.document);
      		return true;
        }
        
        private boolean close(String docId) {
        	        	
        	return true;
        }
        
        private void sendMessage(String message) {
        	CharBuffer buffer = CharBuffer.wrap(message);
        	try {
	          this.getWsOutbound().writeTextMessage(buffer);
          } catch (IOException e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
          }
        }
        
        private void broadcast(String message, Set<String> targets) {
            for (String t : targets) {
                connections.get(t).sendMessage(message);
            }
        }
    }
}
