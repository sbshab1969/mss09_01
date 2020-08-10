package acp.db.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import acp.db.DbConnect;
import acp.utils.*;

public class DbUtils {

  public static String getValueV(String query) {
    String res = null;
    try {
      Connection dbConn = DbConnect.getConnection();
      Statement stmt = dbConn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      rs.next();
      res = rs.getString(1);
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

  public static int getValueN(String query) {
    int res = -1;
    try {
      Connection dbConn = DbConnect.getConnection();
      Statement stmt = dbConn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      rs.next();
      res = rs.getInt(1);
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

  public static Long getValueL(String query) {
    Long res = null;
    try {
      Connection dbConn = DbConnect.getConnection();
      Statement stmt = dbConn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      rs.next();
      res = rs.getLong(1);
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
      res = null;
    }
    return res;
  }

  public static List<String[]> getListString(String query) {
    ArrayList<String[]> cache = new ArrayList<>();
    int cntCols = 2; 
    try {
      Connection dbConn = DbConnect.getConnection();
      Statement stmt = dbConn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        //---------------------------------------
        String[] record = new String[cntCols];
        for (int i = 0; i < cntCols; i++) {
          record[i] = rs.getString(i+1);
        }
        cache.add(record);
        //---------------------------------------
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      cache = new ArrayList<>();
      DialogUtils.errorPrint(e);
    }
    return cache;
  }

  public static String getUser() {
    String res = null;
    try {
      Connection dbConn = DbConnect.getConnection();
      Statement stmt = dbConn.createStatement();
      ResultSet rs = stmt.executeQuery("select user from dual");
      rs.next();
      res = rs.getString(1);
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }
  
  public static Timestamp getSysdate() {
    Timestamp res = null;
    try {
      Connection dbConn = DbConnect.getConnection();
      Statement stmt = dbConn.createStatement();
      ResultSet rs = stmt.executeQuery("select sysdate from dual");
      rs.next();
      res = rs.getTimestamp(1);
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

}
