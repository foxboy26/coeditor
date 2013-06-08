<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<jsp:include page="common/header.jsp" />
<script src="js/coeditor.js"></script>
<body>

<%@ page language="java" import="java.sql.*" %>
<%@ page language="java" import="java.util.ArrayList" %>
<%@ page language="java" import="db.Config" %>
<%-- -------- Open Connection Code -------- --%>
<%
  try {
    // Load JDBC Driver class file
    Class.forName(Config.jdbcDriver);

    // Open a connection to the database using DriverManager
    Connection conn = DriverManager.getConnection(Config.connectionURL, Config.username, Config.password);
    
    String curFile = request.getParameter("file");
    
    String name = (String)session.getAttribute("username");
    
    Statement statement = conn.createStatement();
    String sql = "select filelist.name from users, share, filelist where username ='" + name + "' and users.id = share.user and share.file = filelist.id";

    ResultSet rs = statement.executeQuery(sql);
    
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
	<div class="container-fluid">
  		<div class="row-fluid">
  		
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
      					<input type="button" class="btn btn-primary" name="button" value="create">
      				</div>
      				<div class="span3">
          				<input type="button" class="btn" name="button" value="share">
          			</div>
    		</div>
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
      					System.out.println(file);
      			%>
				  		<li><a href="coeditor.jsp?file=<%= file %>"><%=file%></a></li>
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
    		</div>
    		<div id="console-container">
        		<div id="console"></div>
    		</div>
    	<!--related userlist -->  
    		<div class="span2">
      			<div class="well sidebar-nav">
          			<ul class="nav nav-list">
            			<li class="nav-header">Userlist</li>
      			<%
      				if(curFile != null){
      					sql = "select users.username from filelist, share, users where filelist.name = '" + curFile + "' and share.file = filelist.id and share.user = users.id";
      					rs = statement.executeQuery(sql);
      					while(rs.next()){
      						String user = rs.getString("username");
      						System.out.println(user);
      			%>
				  		<li><%=user%></li>
				<%
      					}
      				}
				%>
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
    statement.close();

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
</body>
</html>
