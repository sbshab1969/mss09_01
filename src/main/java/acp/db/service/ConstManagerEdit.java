package acp.db.service;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.ConstClass;
import acp.db.utils.DbUtils;
import acp.utils.DialogUtils;

public class ConstManagerEdit {
  private Connection dbConn;

  private static Logger logger = LoggerFactory.getLogger(ConstManagerEdit.class);

  public ConstManagerEdit() {
    dbConn = DbConnect.getConnection();
  }

  public ConstClass select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select mssc_id, mssc_name, mssc_value");
    sbQuery.append("  from mss_const");
    sbQuery.append(" where mssc_id=?");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    ConstClass constObj = null;
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        String rsqName = rsq.getString("mssc_name");
        String rsqValue = rsq.getString("mssc_value");
        // ---------------------
        constObj = new ConstClass();
        constObj.setId(objId);
        constObj.setName(rsqName);
        constObj.setValue(rsqValue);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      constObj = null;
    }
    // ------------------------------------------------------
    return constObj;
  }

  public Long insert(ConstClass newObj) {
    // ------------------------------------------------------
    Long objId = DbUtils.getValueL("select mssc_seq.nextval from dual");
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_const");
    sbQuery.append(" (mssc_id, mssc_name, mssc_value)");
    sbQuery.append(" values (?, upper(?), ?)");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ps.setString(2, newObj.getName());
      ps.setString(3, newObj.getValue());
      // --------------------------
      // int ret = ps.executeUpdate();
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

  public boolean update(ConstClass newObj) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_const");
    sbQuery.append("   set mssc_name=upper(?)");
    sbQuery.append("      ,mssc_value=?");
    sbQuery.append(" where mssc_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setString(1, newObj.getName());
      ps.setString(2, newObj.getValue());
      ps.setLong(3, newObj.getId());
      // --------------------------
      // int ret = ps.executeUpdate();
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
    sbQuery.append("delete from mss_const where mssc_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      // --------------------------
      // int ret = ps.executeUpdate();
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
