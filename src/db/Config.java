package db;

public class Config {
  /* MySQL Config */
	public static final String jdbcDriver = "com.mysql.jdbc.Driver";
	//public static final String connectionURL = "jdbc:mysql://localhost:3306/coeditor";
	public static final String connectionURL = "jdbc:mysql://cse223b.c4lmrmwihaxn.us-west-2.rds.amazonaws.com:3306/coeditor";
	public static final String username = "cse223b";
	public static final String password = "cse223b!!";

  /* Amazon S3 Config */
	public static final String bucketName = "cse223bproj";

  /* Mode */
  public static boolean DEBUG_MODE = true;
}
