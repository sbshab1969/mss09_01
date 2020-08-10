package acp.db.service;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.*;
import acp.utils.DialogUtils;

public class FileLoadManagerEdit {
  private Connection dbConn;

  private static Logger logger = LoggerFactory.getLogger(FileLoadManagerEdit.class);

  public FileLoadManagerEdit() {
    dbConn = DbConnect.getConnection();
  }

  public FileLoadClass select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select mssf_id,mssf_name,mssf_md5,mssf_owner");
    sbQuery.append(",mssf_dt_create,mssf_dt_work");
    sbQuery.append(",mssf_msso_id,msso_name");
    sbQuery.append(",mssf_rec_all records_all, mssf_rec_er records_err");
//    sbQuery.append(",extract(mssf_statistic,'statistic/records/all/text()').getStringVal() records_all");
//    sbQuery.append(",extract(mssf_statistic,'statistic/records/error/text()').getStringVal() records_err");
    sbQuery.append("  from mss_files,mss_options");
    sbQuery.append(" where mssf_msso_id=msso_id");
    sbQuery.append("   and mssf_id=?");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    FileLoadClass filesObj = null;
    try {
      PreparedStatement ps = dbConn.prepareStatement(strQuery);
      ps.setLong(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        // Long rsqId = rsq.getLong("mssf_id");
        String rsqName = rsq.getString("mssf_name");
        String rsqMd5 = rsq.getString("mssf_md5");
        Timestamp rsqDateCreate = rsq.getTimestamp("mssf_dt_create");
        Timestamp rsqDateWork = rsq.getTimestamp("mssf_dt_work");
        String rsqOwner = rsq.getString("mssf_owner");
        Long rsqConfigId = rsq.getLong("mssf_msso_id");
        String rsqConfigName = rsq.getString("msso_name");
        String rsqRecAll = rsq.getString("records_all");
        String rsqRecErr = rsq.getString("records_err");
        // ---------------------
        ConfigClass configObj = new ConfigClass();
        configObj.setId(rsqConfigId);
        configObj.setName(rsqConfigName);
        // ---------------------
        filesObj = new FileLoadClass();
        filesObj.setId(objId);
        filesObj.setName(rsqName);
        filesObj.setMd5(rsqMd5);
        filesObj.setDateCreate(rsqDateCreate);
        filesObj.setDateWork(rsqDateWork);
        filesObj.setOwner(rsqOwner);
        filesObj.setConfigId(rsqConfigId);
        filesObj.setConfig(configObj);
        // -----
        ArrayList<String> statList = new ArrayList<>();
        statList.add(rsqRecAll);
        statList.add(rsqRecErr);
        filesObj.setStatList(statList);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e, logger);
      filesObj = null;
    }
    // ------------------------------------------------------
    return filesObj;
  }

}
