<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<jsp:include page="common/header.jsp" />
<body>

<%@ page language="java" import="java.sql.*" %>
<%@ page language="java" import="java.util.ArrayList" %>
<%@ page language="java" import="db.Config" %>

<script type="text/javascript">
	
</script>
<%-- -------- Open Connection Code -------- --%>
<%
  try {
    // Load JDBC Driver class file
    Class.forName(Config.jdbcDriver);

    // Open a connection to the database using DriverManager
    Connection conn = DriverManager.getConnection(Config.connectionURL, Config.username, Config.password);
    
    String curFile = request.getParameter("file");
    //System.out.println(curFile);
    
    String name = (String)session.getAttribute("username");
    
    String docName = request.getParameter("title");   
    
    String action = request.getParameter("name");   
    
    Statement state = conn.createStatement();
    
    PreparedStatement pstmt = null;
    String sql = "";

    ResultSet rs = null;
    
    //getuserid
    sql = "select id from users where username = '" + name + "'";
	rs = state.executeQuery(sql);
	rs.next();
	int userid = rs.getInt(1);
	//System.out.println(userid);
	
	int docid = -1;
	
	if(curFile != null){
		//get docid
		sql = "select id from filelist where name = '" + curFile + "'";
		rs = state.executeQuery(sql);
		rs.next();
		docid = rs.getInt(1);
		session.setAttribute("docid", docid);		
	}
	
	
    if(action != null && action.equals("create")){
    	if(docName != null){
    		conn.setAutoCommit(false);
    		
    		//update filelist    		
    		pstmt = conn.prepareStatement("INSERT INTO filelist (name, path) VALUES (?, ?)");
    		pstmt.setString(1, docName);
    		pstmt.setString(2, "C://" + docName + '"');    		
    		pstmt.executeUpdate();   
    		
    		
    		//get docid
    		sql = "select id from filelist where name = '" + docName + "'";
			rs = state.executeQuery(sql);
			rs.next();
			docid = rs.getInt(0);
			session.setAttribute("docid", docid);
    				
    		//update share
    		pstmt = conn.prepareStatement("INSERT INTO share (user, file, owner) VALUES (?, ?, ?)");
    		pstmt.setString(1, Integer.toString(userid));
    		pstmt.setString(2, Integer.toString(docid)); 
    		pstmt.setString(3, Integer.toString(userid));
    		pstmt.executeUpdate();

    	      // Commit transaction
    	    conn.commit();
    	    conn.setAutoCommit(true);
    		
    	}
    }
    
    sql = "select filelist.name, filelist.id from users, share, filelist where username ='" + name + "' and users.id = share.user and share.file = filelist.id";
    rs = state.executeQuery(sql);
    /* if(name != null){
    	if(button.equals("Login")){
    		if(rs.next()){
          		session.setAttribute("username", name);
            	response.sendRedirect("coeditor.jsp");
    		}
        	else 
          		response.sendRedirect("login.jsp"); 
    	}
		if(button.equals("Sign-up")) {
			 session.setAttribute("username", name);
			 // Begin transaction
		     conn.setAutoCommit(false);

		      // Create the prepared statement and use it to
		      // INSERT user values INTO the user table.
		     PreparedStatement pstmt = conn
		      .prepareStatement("INSERT INTO users (username) VALUES (?)");
		      
		     System.out.println(name);

		      pstmt.setString(1, name);
		      int rowCount = pstmt.executeUpdate();

		      // Commit transaction
		      conn.commit();
		      conn.setAutoCommit(true);

		      // Close the Statement
		      pstmt.close();
		}
		  
    }  */
%>
	<div >
		<input type="hidden" value=<%=userid%> id="userid">
		<input type="hidden" value=<%=docid%> id="docid">
	</div>
	<div class="container-fluid">
  		<div class="row-fluid">
  		  <form action="coeditor.jsp" method="get">
  		  
	  		<!--doc title -->
	  		<div class="span5 offset2">
	  			<div class="row-fluid">
		  			<div class="span1" id="username">
			          <label class="control-label">Title</label>
			         </div>
			          <div class="span11">
			            <input type="text" value="" name="title" autofocus="autofocus">
			          </div>
		        </div>
	  		</div>	
	    		
	    		
	    	<!--buttons -->
	    		<div class="span3">
	    				<div class="span3 offset6">
	      					<input type="submit" class="btn btn-primary" name="create" id = "create" value = "create">
	      				</div>
	      				<div class="span3">
	          				<input type="submit" class="btn" name="share" id ="share" value = "share">
	          			</div>
	    		</div>
	    	</form>
	  	</div>
  		<div class="row-fluid">
  		<!--editable filelist -->    
    		<div class="span2">
    			<div class="well sidebar-nav">
          			<ul class="nav nav-list">
            			<li class="nav-header">Filelist</li>
      			<%
      				while(rs.next()){
      					String file = rs.getString("name");
      					int id = rs.getInt("id");
      					//System.out.println(file);
      			%>
				  		<li><a href="#" name = "file" onclick = "getUserList(this)"><%=file%></a></li>
				<%
      				}
				%>
			<!-- <li class="divider"></li> -->	  
					</ul>
				</div>
    		</div>
    		
    	<!--text area -->  
    		<div class="span8">
      			<textarea class="field span12" id="coeditor" rows="23" placeholder="Enter a short synopsis"></textarea>
      			<div id="console-container">
	        		<div id="console">
	        		</div>
    			</div>
    		</div>
    		
    	<!--related userlist -->  
    		<div class="span2">
      			<div class="well sidebar-nav">
          			<ul class="nav nav-list" id = "userlist">
            			<li class="nav-header">Userlist</li>
			<!-- <li class="divider"></li> -->	  
					</ul>
				</div>
    		</div>
  		</div>
	</div>

<%-- -------- Close Connection Code -------- --%>
<%
    // Close the ResultSet
    rs.close();

    // Close the Statement
    state.close();
    
    pstmt.close();

    // Close the Connection
    conn.close();
  } catch (SQLException sqle) {
      out.println(sqle.getMessage());
  } catch (Exception e) {
      out.println(e.getMessage());
  }
%>

  <script src="js/jquery-1.9.1.js"></script>
  <script src="js/bootstrap.min.js"></script>
  <script src="js/coeditor.js"></script>
</body>
</html>
