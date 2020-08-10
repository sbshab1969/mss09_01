package acp.db.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.ToptionClass;
import acp.utils.DialogUtils;

public class ToptionManagerEdit {

  private Connection dbConn;

  private static Logger logger = LoggerFactory.getLogger(ToptionManagerEdit.class);

  private String path;
  private ArrayList<String> attrs;
  private int attrSize;
  private int attrMax = 5;
  private String attrPrefix;

  public ToptionManagerEdit(String path, ArrayList<String> attrs) {
    dbConn = DbConnect.getConnection();
    this.path = path;
    this.attrs = attrs;
    this.attrSize = attrs.size();
    String[] pathArray = path.split("/");
    this.attrPrefix = pathArray[pathArray.length - 1];
  }

  public String getPath() {
    return path;
  }

  public ArrayList<String> getAttrs() {
    return attrs;
  }

  public int getAttrSize() {
    return attrSize;
  }

  public int getAttrMax() {
    return attrMax;
  }

  public String getAttrPrefix() {
    return attrPrefix;
  }

  public ToptionClass select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select t.* from table(mss.spr_option_id(?,?,?,?,?,?,?)) t");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    ToptionClass toptObj = null;
    int j = 0;
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      // ----------------------------------
      j++;
      ps.setLong(j, objId);
      j++;
      ps.setString(j, path);
      // ----------------------------------
      for (int i = 0; i < attrSize; i++) {
        j++;
        ps.setString(j, attrs.get(i));
      }
      for (int i = attrSize; i < attrMax; i++) {
        j++;
        ps.setString(j, "");
      }
      // ----------------------------------
      ResultSet rsq = ps.executeQuery();
      // ----------------------------------
      if (rsq.next()) {
        Long rsqId = rsq.getLong("config_id");
        // -------------------------
        ArrayList<String> pArr = new ArrayList<>();
        for (int i = 0; i < attrSize; i++) {
          j = i + 1;
          String pj = rsq.getString("p" + j);
          pArr.add(pj);
        }
        // -------------------------
        // Date rsqDateBegin = rsq.getDate("date_begin");
        // Date rsqDateEnd = rsq.getDate("date_end");
        Date rsqDateBegin = rsq.getTimestamp("date_begin");
        Date rsqDateEnd = rsq.getTimestamp("date_end");
        // ---------------------
        toptObj = new ToptionClass();
        toptObj.setId(rsqId);
        toptObj.setArrayP(pArr);
        toptObj.setDateBegin(rsqDateBegin);
        toptObj.setDateEnd(rsqDateEnd);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      toptObj = null;
    }
    // ------------------------------------------------------
    return toptObj;
  }

  public boolean updatePar(ToptionClass objOld, ToptionClass objNew) {
    boolean res = false;
    Long objId = objOld.getId();
    ArrayList<String> recOldValue = objOld.getPArray();
    ArrayList<String> recNewValue = objNew.getPArray();
    // -----------------------------------------
    String where = "";
    for (int i = 0; i < attrSize; i++) {
      if (recOldValue.get(i) != null) {
        where += "[@" + attrs.get(i) + "=\"" + recOldValue.get(i) + "\"]";
      }
    }
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options set ");
    sbQuery.append("msso_config=updateXml(msso_config");
    for (int i = 0; i < attrSize; i++) {
      if (!recNewValue.get(i).equals("")) {
        String param = path + where + "/@" + attrs.get(i);
        sbQuery.append(",'" + param + "',?");
      }
    }
    sbQuery.append(") where msso_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      int j = 0;
      for (int i = 0; i < attrSize; i++) {
        if (!recNewValue.get(i).equals("")) {
          ps.setString(++j, recNewValue.get(i));
        }
      }
      ps.setLong(++j, objId);
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
