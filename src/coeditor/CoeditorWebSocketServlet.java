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

public class CoeditorWebSocketServlet extends WebSocketServlet {

    private static final long serialVersionUID = 1L;

    private Gson gson = new Gson();
    
    private final AtomicInteger connectionIds = new AtomicInteger(0);
    private final Map<String, CoeditorMessageInbound> connections =
            new Hashtable<String, CoeditorMessageInbound>();

    private Map<String, Document> documents = new Hashtable<String, Document> ();
    
    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,
            HttpServletRequest request) {
        return new CoeditorMessageInbound();
    }

    private final class CoeditorMessageInbound extends MessageInbound {

        private Document document;
        
        private CoeditorMessageInbound() {
        	
        }

        @Override
        protected void onOpen(WsOutbound outbound) {

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
        	
        	if (action == null || action.length() == 0)
        		action = "open";
        	
        	if (action != null && action.equals("open")) {
        		if (msg.contentType == Message.STRING) {
          		// step 1: open document for client
        			String clientId = msg.clientId;
          		String docId = msg.content;
          		open(clientId, docId);
          		
          		// step 2: send back HEADTEXT
          		String headText = "Hello Xixi!";
          		ChangeSet testChangeSet = new ChangeSet(headText);
          		sendMessage(gson.toJson(testChangeSet));
          		//sendMessage(this.document.headText);
        		}
        	}
        }

        private boolean open(String clientId, String docId) {
        	this.document = new Document(docId);
        	
        	this.document.open(clientId);
        	
      		if (!documents.containsKey(docId))
      			documents.put(docId, this.document);
      		
      		connections.put(clientId, this);
      		
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
                try {
                    CharBuffer buffer = CharBuffer.wrap(message);
                    connections.get(t).getWsOutbound().writeTextMessage(buffer);
                } catch (IOException ignore) {
                    // Ignore
                }
            }
        }
    }
}