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
          
        closeDocument();
        
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

        String clientId = msg.clientId;
        String action = msg.action;
        if (action != null && action.equals("open")) {
          // step 1: open document for client
          String docId = msg.content;
          if (openDocument(docId, clientId)) {
          // step 2: send back HEADTEXT
            String headText = documents.get(docId).headText;
            System.out.println(headText);
            ChangeSet testChangeSet = new ChangeSet(headText);

            Message response = new Message("server", "response", gson.toJson(testChangeSet));
            sendMessage(gson.toJson(response));

          } else {

            System.out.println("[open] error: file cannot be opened.");

            sendErrorMessage("[open] error: file cannot be opened.");
          }
        } else if (action != null && action.equals("close")) {
          
          closeDocument();
          
          sendErrorMessage(action + " is not supported");
        
        } else if (action != null && action.equals("save")) {
          
          saveDocument();
          
          sendErrorMessage(action + " is not supported");
        
        } else if (action != null && action.equals("newChange")) {
          
          broadcast("haha");
          
          sendErrorMessage(action + " is not supported");

        } else if (action != null && action.equals("sync")) {

          sendErrorMessage(action + " is not supported");

        } else {

          System.out.println("[new message] error: no action");
          sendErrorMessage(action + " is not supported");
        }
      }

      private boolean openDocument(String docId, String clientId) {
        this.document = new Document(docId, s3);
        
        this.document.open(connectionId, clientId);
        
        if (!documents.containsKey(docId))
          documents.put(docId, this.document);
        return true;
      }
      
      private boolean closeDocument() {
        
        if (document.isOpen) {
          this.document.close(connectionId);
          return true;
        }
        else
          return false;
      }
      
      private boolean saveDocument() {
        this.document.save();
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

      private void sendErrorMessage(String message) {
        Message errorMsg = new Message("server", "error", message);
        sendMessage(gson.toJson(errorMsg));
      }
      
      private void broadcast(String message) {
        Set<Integer> targets = this.document.getActiveUser();
        for (int t : targets) {
          connections.get(t).sendMessage(message);
        }
      }
  }
}
