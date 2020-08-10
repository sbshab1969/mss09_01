package acp.db.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.ConfigClass;
import acp.db.utils.*;
import acp.utils.*;

public class ConfigManagerEdit {
  private Connection dbConn;

  private static Logger logger = LoggerFactory.getLogger(ConfigManagerEdit.class);

  public ConfigManagerEdit() {
    dbConn = DbConnect.getConnection();
  }

  public ConfigClass select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select msso_id, msso_name,msso_dt_begin,msso_dt_end,msso_comment,msso_msss_id");
    sbQuery.append("  from mss_options");
    sbQuery.append(" where msso_id=?");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    ConfigClass configObj = null;
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        String rsqName = rsq.getString("msso_name");
        Date rsqDateBegin = rsq.getTimestamp("msso_dt_begin");
        Date rsqDateEnd = rsq.getTimestamp("msso_dt_end");
        String rsqComment = rsq.getString("msso_comment");
        Long rsqSourceId = rsq.getLong("msso_msss_id");
        // ---------------------
        configObj = new ConfigClass();
        configObj.setId(objId);
        configObj.setName(rsqName);
        configObj.setDateBegin(rsqDateBegin);
        configObj.setDateEnd(rsqDateEnd);
        configObj.setComment(rsqComment);
        configObj.setSourceId(rsqSourceId);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      configObj = null;
    }
    // ------------------------------------------------------
    return configObj;
  }
  
  public String getCfgName(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select msso_name from mss_options t where msso_id=?");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    String configName = "";
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        configName = rsq.getString("msso_name");
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      configName = "";
    }
    // ------------------------------------------------------
    return configName;
  }

  public String getCfgStr(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select t.msso_config.getStringVal() msso_conf");
    sbQuery.append("  from mss_options t where msso_id=?");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    String configStr = null;
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        configStr = rsq.getString("msso_conf");
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      configStr = null;
    }
    // ------------------------------------------------------
    return configStr;
  }

  public Long insert(ConfigClass newObj) {
    // ------------------------------------------------------
    Long objId = DbUtils.getValueL("select msso_seq.nextval from dual");
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_options (");
    sbQuery.append("msso_id, msso_name, msso_config");
    sbQuery.append(",msso_dt_begin, msso_dt_end, msso_comment");
    sbQuery.append(",msso_dt_create, msso_dt_modify, msso_owner, msso_msss_id)");
    sbQuery.append(" values (?, ?, XMLType(?), ?, ?, ?");
    sbQuery.append(", sysdate, sysdate, user, ?)");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    String emptyXml = "<?xml version=\"1.0\"?><config><sverka.ats/></config>";
    Timestamp tsBegin = StrSqlUtils.util2ts(newObj.getDateBegin());
    Timestamp tsEnd = StrSqlUtils.util2ts(newObj.getDateEnd());
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ps.setString(2, newObj.getName());
      ps.setString(3, emptyXml);
      ps.setTimestamp(4, tsBegin);
      ps.setTimestamp(5, tsEnd);
      ps.setString(6, newObj.getComment());
      ps.setLong(7, newObj.getSourceId());
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

  public boolean update(ConfigClass newObj) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    sbQuery.append("   set msso_name=?");
    sbQuery.append("      ,msso_dt_begin=?");
    sbQuery.append("      ,msso_dt_end=?");
    sbQuery.append("      ,msso_comment=?");
    sbQuery.append("      ,msso_dt_modify=sysdate");
    sbQuery.append("      ,msso_owner=user");
    sbQuery.append("      ,msso_msss_id=?");
    sbQuery.append(" where msso_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    Timestamp tsBegin = StrSqlUtils.util2ts(newObj.getDateBegin());
    Timestamp tsEnd = StrSqlUtils.util2ts(newObj.getDateEnd());
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setString(1, newObj.getName());
      ps.setTimestamp(2, tsBegin);
      ps.setTimestamp(3, tsEnd);
      ps.setString(4, newObj.getComment());
      ps.setLong(5, newObj.getSourceId());
      ps.setLong(6, newObj.getId());
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

  public boolean updateCfgObj(ConfigClass newObj) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    // sbQuery.append("   set msso_config=?"); // error
    sbQuery.append("   set msso_config=XMLType(?)");
    sbQuery.append("      ,msso_dt_modify=sysdate");
    sbQuery.append("      ,msso_owner=user");
    sbQuery.append(" where msso_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setString(1, newObj.getConfigR());
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

  public boolean updateCfgStr(Long objId, String txtConf) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    // sbQuery.append("   set msso_config=?"); // OK
    sbQuery.append("   set msso_config=XMLType(?)");
    sbQuery.append("      ,msso_dt_modify=sysdate");
    sbQuery.append("      ,msso_owner=user");
    sbQuery.append(" where msso_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setString(1, txtConf);
      ps.setLong(2, objId);
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
    sbQuery.append("delete from mss_options where msso_id=?");
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

  public boolean copy(Long objId) {
    boolean res = false;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_options");
    sbQuery.append(" (select msso_seq.nextval, msso_name || '_copy'");
    sbQuery.append(", msso_config");
    sbQuery.append(", msso_dt_begin, msso_dt_end, msso_comment");
    sbQuery.append(", sysdate, sysdate, user, msso_msss_id");
    sbQuery.append(" from mss_options where msso_id=?)");
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
