<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<jsp:include page="common/header.jsp" />
<head>
<script src="js/jquery-1.9.1.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/coeditor.js"></script>
</head>
<body>

	<%@ page language="java" import="java.sql.*"%>
	<%@ page language="java" import="java.util.ArrayList"%>
	<%@ page language="java" import="db.Config"%>

	<script type="text/javascript">
	
</script>
	<%-- -------- Open Connection Code -------- --%>
	<%
  Connection conn = null;
  ResultSet  rs = null;
  PreparedStatement pstmt = null;
  
  try {
    // Load JDBC Driver class file
    Class.forName(Config.jdbcDriver);

    // Open a connection to the database using DriverManager
    conn = DriverManager.getConnection(Config.connectionURL, Config.username, Config.password);
    
    String curFile = request.getParameter("file");
    
    String username = (String) session.getAttribute("username");
    
    String userid = (String) session.getAttribute("userid");
    
    if (username == null || userid == null) {
      response.sendRedirect("login.jsp");
    }
    
    String action = request.getParameter("name");   
    
    
	  //System.out.println(userid);
	
	int docid = -1;
	
	if(curFile != null){
		//get docid
		pstmt = conn.prepareStatement("select id from filelist where name = ?");
		pstmt.setString(1, curFile);
		rs = pstmt.executeQuery();
		rs.next();
		docid = rs.getInt(1);
		session.setAttribute("docid", docid);		
	}

    
    pstmt = conn.prepareStatement(
      "select filelist.name, filelist.id from users, share, filelist " + 
      "where username = ? and users.id = share.user and share.file = filelist.id");
    pstmt.setString(1, username);
    rs = pstmt.executeQuery();
%>
	<div>
    <input type="hidden" name="userid" value=<%=userid%> id="userid" />
    <input type="hidden" value=<%=docid%> id="docid" />
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
							<input type="text" value="" name="title" autofocus="autofocus" placeholder="New document">
						</div>
					</div>
				</div>

				<!--buttons -->
				<div class="span3">
					<div class="span3 offset6">
						<input type="button" class="btn btn-primary" name="create" value="create" onclick="createDocument()">
					</div>
					<div class="span3">
						<input type="button" class="btn" name="share" value="share" onsubmit="">
					</div>
				</div>
			</form>
		</div>
		<div class="row-fluid">
			<!--editable filelist -->
			<div class="span2">
				<div class="well sidebar-nav">
					<ul class="nav nav-list" id="filelist">
						<li class="nav-header">Filelist</li>
						<%
      				while(rs.next()){
      					String file = rs.getString("name");
      					int id = rs.getInt("id");
      					//System.out.println(file);
      			%>
            <li><a href="#" name="file" onclick="openDocument(<%= file %>)"><%= file %></a></li>
						<%
      				}
				%>
						<!-- <li class="divider"></li> -->
					</ul>
				</div>
			</div>

			<!--text area -->
			<div class="span8">
				<textarea class="field span12" id="coeditor" rows="15"
					placeholder="Enter a short synopsis"></textarea>
				<div id="console-container">
					<div id="console"></div>
				</div>
			</div>

			<!--related userlist -->
			<div class="span2">
				<div class="well sidebar-nav">
					<ul class="nav nav-list" id="userlist">
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
		  pstmt.close();

		  // Close the Connection
		  conn.close();
		} catch (SQLException e) {

		  // Wrap the SQL exception in a runtime exception to propagate
		  // it upwards
		  //throw new RuntimeException(e);
		  String errorMsg = e.getMessage();
		  //TODO: Parse error message to display user-friendly messages.
	%>
	<!-- Display error message -->
	<div class="container">
		<div class="alert alert-error">
			<button type="button" class="close"
				onclick="window.location.href='products.jsp'">&times;</button>
			<strong>Error!</strong>
			<%=errorMsg%>
		</div>
	</div>
	<%
		}
		finally {
		  // Release resources in a finally block in reverse-order of
		  // their creation

		  if (rs != null) {
		    try {
		      rs.close();
		    } catch (SQLException e) { } // Ignore
		    rs = null;
		  }
		  if (pstmt != null) {
		    try {
		      pstmt.close();
		    } catch (SQLException e) { } // Ignore
		    pstmt = null;
		  }
		  if (conn != null) {
		    try {
		      conn.close();
		    } catch (SQLException e) { } // Ignore
		    conn = null;
		  }
		}
	%>
</body>
</html>
