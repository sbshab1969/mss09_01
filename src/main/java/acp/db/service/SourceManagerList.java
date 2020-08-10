package acp.db.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.SourceClass;
import acp.db.utils.*;
import acp.utils.*;

public class SourceManagerList extends ManagerList {
  private static Logger logger = LoggerFactory.getLogger(SourceManagerList.class);

  protected List<SourceClass> cacheObj = new ArrayList<>();

  public SourceManagerList() {
    headers = new String[] { 
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.Owner") 
    };
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
    };
    cntColumns = headers.length;

    fields = new String[] { "msss_id", "msss_name", "msss_owner" };
    strFields = StrSqlUtils.buildSelectFields(fields, null);

    tableName = "mss_source";
    pkColumn = "msss_id";
    strAwhere = null;
    seqId = 1000L;

    strFrom = tableName;
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
    String vOwner = mapFilter.get("owner");;
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!StrUtils.emptyString(vName)) {
      str = "upper(msss_name) like upper('" + vName + "%')";
      phWhere = StrSqlUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!StrUtils.emptyString(vOwner)) {
      str = "upper(msss_owner) like upper('" + vOwner + "%')";
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
  public List<SourceClass> queryAll() {
    openQueryAll();  // forward
    cacheObj = fetchAll();
    closeQuery();
    return cacheObj;    
  }

  @Override
  public List<SourceClass> fetchPage(int startPos, int cntRows) {
    cacheObj = fetchPart(startPos,cntRows);
    return cacheObj;
  }  

  private List<SourceClass> fetchAll() {
    ArrayList<SourceClass> cache = new ArrayList<>();
    try {
      while (rs.next()) {
        SourceClass record = getObject(rs);
        cache.add(record);
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      cache = new ArrayList<>();
    }
    return cache;
  }

  private List<SourceClass> fetchPart(int startPos, int cntRows) {
    ArrayList<SourceClass> cache = new ArrayList<>();
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
        SourceClass record = getObject(rs);
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

  private SourceClass getObject(ResultSet rs) throws SQLException {
    //---------------------------------------
    Long rsId = rs.getLong("msss_id");
    String rsName = rs.getString("msss_name");
    String rsOwner = rs.getString("msss_owner");
    //---------------------------------------
    SourceClass obj = new SourceClass();
    obj.setId(rsId);
    obj.setName(rsName);
    obj.setOwner(rsOwner);
    //---------------------------------------
    return obj;
  }

}
