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
    <input type="hidden" name="docid" value=<%=docid%> id="docid" />
	</div>
	<div class="container-fluid">
		<div class="row-fluid">
      <!--doc title -->
      <div class="span3 offset2">
          <h4 id="title">Title</h4>
      </div>

      <div class="span3" id="info">
      </div>

      <!--buttons -->
      <div class="span2">
        <div class="span2">
          <a href="#createDialog" role="button" class="btn btn-primary" data-toggle="modal">Create</a>
        </div>
        <div class="span2 offset3">
          <a href="#shareDialog" role="button" class="btn" data-toggle="modal">Share</a>
        </div>
      </div>
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
            <li id="<%= file %>">
              <a href="#" onclick="openDocument('<%= file %>')">
	              <%= file %>
              </a>
              <i class="icon-trash" onclick="deleteDocument('<%= file %>')"></i>
            </li>
						<%
      				}
				%>
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


<div id="shareDialog" class="modal hide fade">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>Share with</h3>
  </div>
  <div class="modal-body">
    <input type="text" value="" name="shareUserName" autofocus="autofocus" placeholder="editor">
  </div>
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
    <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true" onclick="shareDocument()">Share</a>
  </div>
</div>

<div id="createDialog" class="modal hide fade">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>Create a new document</h3>
  </div>
  <div class="modal-body">
    <input type="text" value="" name="title" autofocus="autofocus" placeholder="Untitled document">
  </div>
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
    <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true" onclick="createDocument()">Create</a>
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
