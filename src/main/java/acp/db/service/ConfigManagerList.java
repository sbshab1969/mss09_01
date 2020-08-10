package acp.db.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.*;
import acp.db.utils.*;
import acp.utils.*;

public class ConfigManagerList extends ManagerList {
  private static Logger logger = LoggerFactory.getLogger(ConfigManagerList.class);

  protected List<ConfigClass> cacheObj = new ArrayList<>();

  public ConfigManagerList() {
    headers = new String[] { 
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.SourceName")
      , Messages.getString("Column.DateBegin")
      , Messages.getString("Column.DateEnd")
      , Messages.getString("Column.Comment")
      , Messages.getString("Column.Owner") 
    };
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
      , Date.class
      , Date.class
      , String.class
      , String.class 
    };
    cntColumns = headers.length;

    fields = new String[] { 
        "msso_id"
      , "msso_name"
      , "msso_dt_begin"
      , "msso_dt_end"
      , "msso_comment"
      , "msso_owner"
      , "msso_msss_id"
      , "msss_name" 
    };
    strFields = StrSqlUtils.buildSelectFields(fields, null);

    tableName = "mss_options";
    pkColumn = "msso_id";
    strAwhere = "msso_msss_id=msss_id";
    seqId = 1000L;

    strFrom = "mss_options, mss_source";
    strWhere = strAwhere;
    strOrder = pkColumn;
    // ------------
    prepareQuery(null);
    // ------------
  }

  @Override
  public void prepareQuery(Map<String,String> mapFilter) {
    if (mapFilter != null) {
      setWhere(mapFilter);
    } else {
      strWhere = strAwhere;
    }
    strQuery = StrSqlUtils.buildQuery(strFields, strFrom, strWhere, strOrder);
    strQueryCnt = StrSqlUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
  }

  private void setWhere(Map<String,String> mapFilter) {
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    String vOwner = mapFilter.get("owner"); 
    String vSource = mapFilter.get("source"); 
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!StrUtils.emptyString(vName)) {
      str = "upper(msso_name) like upper('" + vName + "%')";
      phWhere = StrSqlUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!StrUtils.emptyString(vOwner)) {
      str = "upper(msso_owner) like upper('" + vOwner + "%')";
      phWhere = StrSqlUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!StrUtils.emptyString(vSource)) {
      str = "msso_msss_id=" + vSource;
      phWhere = StrSqlUtils.strAddAnd(phWhere, str);
    }
    // ---
    strWhere = StrSqlUtils.strAddAnd(strAwhere, phWhere);
  }
  
  @Override
  public long countRecords() {
    long cntRecords = DbUtils.getValueL(strQueryCnt);
    return cntRecords;    
  }

  @Override
  public List<ConfigClass> queryAll() {
    openQueryAll();  // forward
    cacheObj = fetchAll();
    closeQuery();
    return cacheObj;    
  }

  @Override
  public List<ConfigClass> fetchPage(int startPos, int cntRows) {
    cacheObj = fetchPart(startPos,cntRows);
    return cacheObj;
  }  

  private List<ConfigClass> fetchAll() {
    ArrayList<ConfigClass> cache = new ArrayList<>();
    try {
      while (rs.next()) {
        ConfigClass record = getObject(rs);
        cache.add(record);
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      cache = new ArrayList<>();
    }
    return cache;
  }

  private List<ConfigClass> fetchPart(int startPos, int cntRows) {
    ArrayList<ConfigClass> cache = new ArrayList<>();
    if (startPos <= 0 || cntRows<=0) { 
      return cache;
    }
    try {
      boolean res = rs.absolute(startPos);
      if (res == false) {
        return cache;
      }
      int curRow = 0;
      //------------------------------------------
      do {
        curRow++;
        ConfigClass record = getObject(rs);
        cache.add(record);
        if (curRow>=cntRows) break;
        //----------------------------------------
      } while (rs.next());
      //------------------------------------------
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      cache = new ArrayList<>();
    }
    return cache;
  }
  
  private ConfigClass getObject(ResultSet rs) throws SQLException {
    //---------------------------------------
    Long rsId = rs.getLong("msso_id");
    String rsName = rs.getString("msso_name");
    Date rsDateBegin = rs.getTimestamp("msso_dt_begin");
    Date rsDateEnd = rs.getTimestamp("msso_dt_end");
    String rsComment = rs.getString("msso_comment");
    String rsOwner = rs.getString("msso_owner");
    Long rsSourceId = rs.getLong("msso_msss_id");
    String rsSourceName = rs.getString("msss_name");
    //---------------------------------------
    SourceClass src = new SourceClass();
    src.setId(rsSourceId);
    src.setName(rsSourceName);
    //---------------------------------------
    ConfigClass obj = new ConfigClass();
    obj.setId(rsId);
    obj.setName(rsName);
    obj.setDateBegin(rsDateBegin);
    obj.setDateEnd(rsDateEnd);
    obj.setComment(rsComment);
    obj.setOwner(rsOwner);
    obj.setSource(src);
    //---------------------------------------
    return obj;
  }

}
