<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page language="java" import="java.sql.*"%>
<%@ page language="java" import="java.util.ArrayList"%>
<%@ page language="java" import="db.Config"%>

<%-- -------- Open Connection Code -------- --%>
<%
	Connection conn = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;

	try {
		// Load JDBC Driver class file
		Class.forName(Config.jdbcDriver);

		// Open a connection to the database using DriverManager
		conn = DriverManager.getConnection(Config.connectionURL,
		    Config.username, Config.password);

		String userName = request.getParameter("userName");
		String docName = request.getParameter("docName");

		System.out.println("create file : " + userName + " name: " + docName);

		if (docName != null) {
			conn.setAutoCommit(false);

			System.out.println("begin db");

			//get docid
			String docId = "";
			pstmt = conn
			    .prepareStatement("select id from filelist where name = ?");
			pstmt.setString(1, docName);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				docId = rs.getString("id");
				
	      System.out.println("get id");
	      
	      String userId = "";
	      pstmt = conn
	          .prepareStatement("select id from users where username = ?");
	      pstmt.setString(1, userName);
	      rs = pstmt.executeQuery();
	      if (rs.next()) {
	      	userId = rs.getString("id");
		      //update share
		      pstmt = conn
		          .prepareStatement("INSERT INTO share (user, file, owner) VALUES (?, ?, ?)");
		      pstmt.setString(1, userId);
		      pstmt.setString(2, docId);
		      pstmt.setBoolean(3, true);
		      pstmt.executeUpdate();
	      }
			}


			System.out.println("share");
			// Commit transaction
			conn.commit();
			conn.setAutoCommit(true);

			out.println("\"success\"");

			// Close the ResultSet
			rs.close();

			// Close the Statement
			pstmt.close();

			// Close the Connection
			conn.close();
		}
	} catch (SQLException e) {

		// Wrap the SQL exception in a runtime exception to propagate
		// it upwards
		//throw new RuntimeException(e);
		String errorMsg = e.getMessage();
		//TODO: Parse error message to display user-friendly messages.
	} finally {
		// Release resources in a finally block in reverse-order of
		// their creation

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			} // Ignore
			rs = null;
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
			} // Ignore
			pstmt = null;
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			} // Ignore
			conn = null;
		}
	}
%>
