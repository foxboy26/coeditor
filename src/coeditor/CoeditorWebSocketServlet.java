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

public class CoeditorWebSocketServlet extends WebSocketServlet {

    private static final long serialVersionUID = 1L;

    private final AtomicInteger connectionIds = new AtomicInteger(0);
    private final Map<String, CoeditorMessageInbound> connections =
            new Hashtable<String, CoeditorMessageInbound>();

    private Map<String, Document> documents = new Hashtable<String, Document> ();
    
    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,
            HttpServletRequest request) {
        return new CoeditorMessageInbound(connectionIds.incrementAndGet());
    }

    private final class CoeditorMessageInbound extends MessageInbound {

        private final String clientId;
        private Document document;
        
        private CoeditorMessageInbound() {
        	
        }

        @Override
        protected void onOpen(WsOutbound outbound) {

        }

        @Override
        protected void onClose(int status) {
            connections.remove(this);
            String message = String.format("* %s %s",
                    clientId, "has disconnected.");
            broadcast(message, this.document.users);
        }

        @Override
        protected void onBinaryMessage(ByteBuffer message) throws IOException {
            throw new UnsupportedOperationException(
                    "Binary message not supported.");
        }

        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {
            // Never trust the client
        	String msg = message.toString();
        	
        	String action = "open";
        	
        	if (action != null && action.equals("open")) {
        		String clientId = "xxx";
        		String docId = "aaa";
        		open(clientId, docId);
        	}        	
        }

        private boolean open(String docId) {
        	this.document = new Document(docId);
        	
        	this.document.open(this.clientId);
        	
      		if (!documents.containsKey(docId))
      			documents.put(docId, this.document);
      		
      		connections.put(clientId, this);
      		
      		return true;
        }
        
        private boolean close(String docId) {
        	        	
        	return true;
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