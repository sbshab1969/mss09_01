package acp.forms;

import java.util.ArrayList;
import javax.swing.*;

import acp.db.domain.*;
import acp.db.service.*;
import acp.forms.frame.*;
import acp.forms.utils.*;
import acp.utils.*;

public class XmlTableEdit extends FrameEdit {
  private static final long serialVersionUID = 1L;

  private ToptionManagerEdit tableManager;
  private ToptionClass recOldObj;

  private ArrayList<JLabel> lblList = new ArrayList<JLabel>();
  private ArrayList<JTextField> textList = new ArrayList<JTextField>();

  public XmlTableEdit(ToptionManagerEdit tblManager) {
    tableManager = tblManager;

    initPnlData();
    initFormNone();
    pack();
    setToCenter();
  }

  private void initPnlData() {
    ArrayList<String> attrs = tableManager.getAttrs();
    int attrSize = tableManager.getAttrSize();
    String attrPrefix = tableManager.getAttrPrefix();

    pnlData.setLayout(new SpringLayout());
    for (int i = 0; i < attrSize; i++) {
      // ------------------
      String lblName = FieldConfig.getString(attrPrefix + "." + attrs.get(i));
      JLabel lbl = new JLabel(lblName, JLabel.TRAILING);
      lblList.add(lbl);
      pnlData.add(lbl);
      // ------------------
      JTextField edt = new JTextField(30);
      textList.add(edt);
      pnlData.add(edt);
      // ------------------
      lbl.setLabelFor(edt);
    }
    SpringUtilities.makeCompactGrid(pnlData, attrSize, 2, 10, 10, 10, 10);
  }

  protected void setEditableData() {
    if (act != ACT_NONE) {
      setEditableFields(true);
    } else {
      setEditableFields(false);
    }
  }

  private void setEditableFields(boolean flag) {
    for (int i = 0; i < textList.size(); i++) {
      if (textList.get(i).getText().equals("")) {
        textList.get(i).setEditable(false);
      } else {
        textList.get(i).setEditable(flag);
      }
    }
  }

  protected void clearData() {
    for (int i = 0; i < textList.size(); i++) {
      textList.get(i).setText("");
    }
  }

  protected boolean fillData() {
    if (act == ACT_EDIT) {
      // ---------------------------------
      recOldObj = tableManager.select(recId);
      if (recOldObj == null) {
        return false;
      }
      // ---------------------------------
      ArrayList<String> pArr = recOldObj.getPArray();
      for (int i = 0; i < pArr.size(); i++) {
        String val = pArr.get(i);
        textList.get(i).setText(val);
      }
      // ---------------------------------
    }  
    return true;
  }

  protected boolean validateData() {
    ArrayList<String> oldVal = recOldObj.getPArray();
    for (int i = 0; i < textList.size(); i++) {
      // --------------------------------------
      String vLabel = lblList.get(i).getText();
      String vText = textList.get(i).getText();
      String vOldText = oldVal.get(i);
      // --------------------------------------
      if (vOldText != null && vText.equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
            + vLabel);
        return false;
      }
      // --------------------------------------
    }
    return true;
  }

  protected ToptionClass getObj() {
    ArrayList<String> pArr = new ArrayList<>();
    for (int i = 0; i < textList.size(); i++) {
      String vText = textList.get(i).getText();
      pArr.add(vText);
    }
    // ------------------
    ToptionClass formObj = new ToptionClass();
    formObj.setId(recId);
    formObj.setArrayP(pArr);
    // ------------------
    return formObj;
  }

  protected boolean saveObj() {
    boolean res = false;
    ToptionClass recNewObj = getObj();
    if (act == ACT_EDIT) {
      res = tableManager.updatePar(recOldObj, recNewObj);
    }
    return res;
  }

}
