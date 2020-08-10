package acp.db.service;

import java.sql.ResultSet;
import java.sql.SQLException;

//import java.sql.Date;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.VarClass;
import acp.db.utils.*;
import acp.utils.*;

public class VarManagerList extends ManagerList {
  private static Logger logger = LoggerFactory.getLogger(VarManagerList.class);

  protected List<VarClass> cacheObj = new ArrayList<>();

  public VarManagerList() {
    headers = new String[] { 
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.Type")
      , Messages.getString("Column.Number")
      , Messages.getString("Column.Varchar")
      , Messages.getString("Column.Date") };
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
      , Double.class
      , String.class
      , Date.class
    };
    cntColumns = headers.length;

    fields = new String[] { "mssv_id", "mssv_name", "mssv_type"
        ,"mssv_valuen", "mssv_valuev", "mssv_valued" };
    strFields = StrSqlUtils.buildSelectFields(fields, null);

    tableName = "mss_vars";
    pkColumn = "mssv_id";
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
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!StrUtils.emptyString(vName)) {
      str = "upper(mssv_name) like upper('" + vName + "%')";
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
  public List<VarClass> queryAll() {
    openQueryAll();  // forward
    cacheObj = fetchAll();
    closeQuery();
    return cacheObj;    
  }

  @Override
  public List<VarClass> fetchPage(int startPos, int cntRows) {
    cacheObj = fetchPart(startPos,cntRows);
    return cacheObj;
  }  

  private List<VarClass> fetchAll() {
    ArrayList<VarClass> cache = new ArrayList<>();
    try {
      while (rs.next()) {
        VarClass record = getObject(rs);
        cache.add(record);
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      cache = new ArrayList<>();
    }
    return cache;
  }

  private List<VarClass> fetchPart(int startPos, int cntRows) {
    ArrayList<VarClass> cache = new ArrayList<>();
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
        VarClass record = getObject(rs);
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

  private VarClass getObject(ResultSet rs) throws SQLException {
    //---------------------------------------
    Long rsId = rs.getLong("mssv_id");
    String rsName = rs.getString("mssv_name");
    String rsType = rs.getString("mssv_type");
//    double rsValuen = rs.getDouble("mssv_valuen");
    String strValuen = rs.getString("mssv_valuen");
    Double rsValuen = null;
    if (strValuen != null) {
      rsValuen = Double.valueOf(strValuen);
    }
    String rsValuev = rs.getString("mssv_valuev");
    // Date rsValued = rs.getDate("mssv_valued");
    Date rsValued = rs.getTimestamp("mssv_valued");
    //---------------------------------------
    VarClass obj = new VarClass();
    obj.setId(rsId);
    obj.setName(rsName);
    obj.setType(rsType);
    obj.setValuen(rsValuen);
    obj.setValuev(rsValuev);
    obj.setValued(rsValued);
    //---------------------------------------
    return obj;    
  }

}
