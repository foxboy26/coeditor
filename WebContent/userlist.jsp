<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page language="java" import="java.sql.*" %>
<%@ page language="java" import="java.util.ArrayList" %>
<%@ page language="java" import="db.Config" %>

<%-- -------- Open Connection Code -------- --%>
<%

    String docName = request.getParameter("docName");

  
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	String sql = "";
	
	
	try {
    // Load JDBC Driver class file
	    Class.forName(Config.jdbcDriver);
	    
	    conn = DriverManager.getConnection(Config.connectionURL, Config.username, Config.password);
		pstmt = conn.prepareStatement("select users.username from filelist, share, users where filelist.name = ? and share.file = filelist.id and share.user = users.id");
		pstmt.setString(1, docName);
		rs = pstmt.executeQuery();
		
		ArrayList<String> userlist = new ArrayList<String>();
		while(rs.next()){
			userlist.add(rs.getString("username"));
		}
		
      	out.println("[");
      	boolean first = true;
     	for(String name: userlist) {
        	if (first)
          		first = false;
        	else
          		out.println(",");
        	out.print("{\"username\" : \"" + name + "\"}");
      	}
      	out.println("\n]");

      	rs.close();

      	pstmt.close();

      	conn.close();
%>
<%-- -------- Close Connection Code -------- --%>
<%
  } catch (SQLException e) {

    out.println(e.getMessage());

  } finally {
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
