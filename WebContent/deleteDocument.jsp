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

		String docName = request.getParameter("docName");

		//System.out.println("delete file : " + docName);

		if (docName != null) {
			conn.setAutoCommit(false);

			//System.out.println("begin db");

			//delete share
      String docId = "";
      pstmt = conn
          .prepareStatement("select id from filelist where name = ?");
      pstmt.setString(1, docName);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        docId = rs.getString("id");
        pstmt = conn
            .prepareStatement("DELETE FROM share WHERE file = ?");
        pstmt.setString(1, docId);
        pstmt.executeUpdate();
        
        pstmt = conn
            .prepareStatement("DELETE FROM filelist WHERE name = ?");
        pstmt.setString(1, docName);
        pstmt.executeUpdate();
      }

			//System.out.println("share");
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
