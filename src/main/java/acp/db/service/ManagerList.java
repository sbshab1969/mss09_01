package acp.db.service;

import java.sql.*;
import java.util.List;
import java.util.Map;

import acp.db.DbConnect;
import acp.utils.DialogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ManagerList {
  protected Connection dbConn;
  protected Statement stmt;
  protected ResultSet rs;

  protected String[] headers;
  protected Class<?>[] types;
  protected int cntColumns = 0;

  protected String[] fields;
  protected String strFields;

  protected String tableName;
  protected String pkColumn;
  protected String strAwhere;
  protected Long seqId = 0L;

  protected String strFrom;
  protected String strWhere;
  protected String strOrder;
  
  protected String strQuery;
  protected String strQueryCnt;

  private static Logger logger = LoggerFactory.getLogger(ManagerList.class);

  public ManagerList() {
    dbConn = DbConnect.getConnection();
  }

  public String[] getHeaders() {
    return headers;    
  }

  public Class<?>[] getTypes() {
    return types;    
  }

  public Long getSeqId() {
    return seqId;
  }

  protected abstract void prepareQuery(Map<String,String> mapFilter);
  protected abstract long countRecords();
  protected abstract List<?> queryAll();
  protected abstract List<?> fetchPage(int startPos, int cntRows);

  public void openQueryAll() {
    openCursor();
  }  

  public void openQueryPage() {
    openCursor(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
  }  

  public void closeQuery() {
    closeCursor();
  }

  private void openCursor() {
    // System.out.println("OpenCursor: " + dbConn);
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(strQuery);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
    }
  }

  private void openCursor(int typeCursor, int typeConcur) {
    /*
    ResultSet.TypeCursor:
      TYPE_FORWARD_ONLY
      TYPE_SCROLL_INSENSITIVE
      TYPE_SCROLL_SENSITIVE
      TYPE_FORWARD_ONLY
   ResultSet.TypeConcur:
      CONCUR_READ_ONLY;
      CONCUR_UPDATABLE;
      CONCUR_READ_ONLY;
 */
    // System.out.println("OpenCursor2: " + dbConn);
    try {
      stmt = dbConn.createStatement(typeCursor, typeConcur);
      rs = stmt.executeQuery(strQuery);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
    }
  }

  private void closeCursor() {
    // System.out.println("CloseCursor: " + dbConn);
    try {
      if (stmt != null) {
        stmt.close();
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
    }
    stmt = null;
    rs = null;
  }

}
