package acp.db.service;

import java.sql.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.SourceClass;
import acp.db.utils.DbUtils;
import acp.utils.DialogUtils;

public class SourceManagerEdit {
  private Connection dbConn;

  private static Logger logger = LoggerFactory.getLogger(SourceManagerEdit.class);

  public SourceManagerEdit() {
    dbConn = DbConnect.getConnection();
  }

  public List<String[]> getSources() {
    String strQuery = "select msss_id, msss_name from mss_source order by msss_name";
    List<String[]> arrayString = DbUtils.getListString(strQuery);
    return arrayString;
  }
  
  public SourceClass select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select msss_id, msss_name");
    sbQuery.append("  from mss_source");
    sbQuery.append(" where msss_id=?");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    SourceClass sourceObj = null;
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        String rsqName = rsq.getString("msss_name");
        // ---------------------
        sourceObj = new SourceClass();
        sourceObj.setId(objId);
        sourceObj.setName(rsqName);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      sourceObj = null;
    }
    // ------------------------------------------------------
    return sourceObj;
  }

  public Long insert(SourceClass newObj) {
    // ------------------------------------------------------
    Long objId = DbUtils.getValueL("select msss_seq.nextval from dual");
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_source");
    sbQuery.append(" (msss_id, msss_name, msss_dt_create, msss_dt_modify, msss_owner)");
    sbQuery.append(" values (?, ?, sysdate, sysdate, user)");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ps.setString(2, newObj.getName());
      // --------------------------
      ps.executeUpdate();
      // --------------------------
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      objId = null;
    }
    // -----------------------------------------------------
    return objId;
  }

  public boolean update(SourceClass newObj) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_source");
    sbQuery.append("   set msss_name=?");
    sbQuery.append("      ,msss_dt_modify=sysdate");
    sbQuery.append("      ,msss_owner=user");
    sbQuery.append(" where msss_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setString(1, newObj.getName());
      ps.setLong(2, newObj.getId());
      // --------------------------
      ps.executeUpdate();
      // --------------------------
      ps.close();
      res = true;
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------------------
    return res;
  }

  public boolean delete(Long objId) {
    boolean res = false;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("delete from mss_source where msss_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      // --------------------------
      ps.executeUpdate();
      // --------------------------
      ps.close();
      res = true;
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------------------
    return res;
  }
}
