package acp.forms;

import java.awt.*;

import javax.swing.*;

import acp.db.domain.*;
import acp.db.service.*;
import acp.forms.frame.*;
import acp.utils.*;

public class ConstEdit extends FrameEdit {
  private static final long serialVersionUID = 1L;

  private ConstManagerEdit tableManager;

  private JLabel lblName = new JLabel(Messages.getString("Column.Name"));
  private JLabel lblValue = new JLabel(Messages.getString("Column.Value"));
  private JTextField txtName = new JTextField(30);
  private JTextField txtValue = new JTextField(30);

  public ConstEdit(ConstManagerEdit tblManager) {
    tableManager = tblManager;

    initPnlData();
    initFormNone();
    pack();
    setToCenter();
  }

  private void initPnlData() {
    pnlData.setLayout(new GridBagLayout());

    GridBagConstraints cons = new GridBagConstraints();

    cons.insets = new Insets(10, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 0;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblName, cons);
    cons.gridx = 1;
    cons.gridy = 0;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(txtName, cons);

    cons.insets = new Insets(2, 10, 10, 10);
    cons.gridx = 0;
    cons.gridy = 1;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblValue, cons);
    cons.gridx = 1;
    cons.gridy = 1;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(txtValue, cons);

    lblName.setLabelFor(txtName);
    lblValue.setLabelFor(txtValue);
  }

  protected void setEditableData() {
    if (act == ACT_NEW) {
      txtName.setEditable(true);
      txtValue.setEditable(true);
    } else if (act == ACT_EDIT) {
      txtName.setEditable(false);
      txtValue.setEditable(true);
    } else {
      txtName.setEditable(false);
      txtValue.setEditable(false);
    }
  }

  protected void clearData() {
    txtName.setText("");
    txtValue.setText("");
  }

  protected boolean fillData() {
    if (act == ACT_EDIT) {
      // ---------------------------------
      ConstClass recObj = tableManager.select(recId);
      if (recObj == null) {
        return false;
      }
      // ---------------------------------
      txtName.setText(recObj.getName());
      txtValue.setText(recObj.getValue());
      // ---------------------------------
    }  
    return true;
  }

  protected boolean validateData() {
    String vName = txtName.getText();
    String vValue = txtValue.getText();
    // --------------------
    if (vName.equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.Name"));
      return false;
    }
    if (vValue.equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.Value"));
      return false;
    }
    // --------------------
    return true;
  }

  protected ConstClass getObj() {
    String vName = txtName.getText();
    String vValue = txtValue.getText();
    // --------------------
    ConstClass formObj = new ConstClass();
    formObj.setId(recId);
    formObj.setName(vName);
    formObj.setValue(vValue);
    // --------------------
    return formObj;
  }

  protected boolean saveObj() {
    boolean res = false;
    ConstClass formObj = getObj();
    if (act == ACT_NEW) {
      Long objId = tableManager.insert(formObj);
      if (objId != null) {
        res = true;
      }
    } else if (act == ACT_EDIT) {
      res = tableManager.update(formObj);
    }
    return res;
  }
  
}
