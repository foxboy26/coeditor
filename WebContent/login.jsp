<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<jsp:include page="common/header.jsp" />

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
    
    String name = request.getParameter("username");
    
    String button = request.getParameter("button");
    
 //   System.out.println(button);
    
    Statement statement = conn.createStatement();

    ResultSet rs = statement.executeQuery("select * from users where username='" + name + "'");
    
    if(name != null){
    	if(button.equals("Login")){
    		if(rs.next()){
          		session.setAttribute("username", name);
          		session.setAttribute("userid", rs.getString("id"));
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
		      
		      response.sendRedirect("coeditor.jsp"); 
		}
		  
    } 
%>
	
	<div class="container">
    <div class="span10">
      <form class="form-horizontal" action="login.jsp" method="post">
        <legend>Login</legend>
        <div class="control-group" id="username">
          <label class="control-label">username</label>
          <div class="controls">
            <input type="text" value="" name="username" placeholder="username" autofocus="autofocus">
          </div>
        </div>

        <div class="form-actions">
          <input type="submit" class="btn btn-primary" name="button" value="Login">
          <input type="submit" class="btn" name="button" value="Sign-up">
        </div>
      </form>
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
