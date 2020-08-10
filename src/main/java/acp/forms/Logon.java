package acp.forms;

import java.awt.event.*;
import java.util.Properties;

import javax.swing.*;

import acp.db.DbConnect;
import acp.forms.frame.*;
import acp.forms.utils.*;
import acp.utils.*;

public class Logon extends FrameLogon {
  private static final long serialVersionUID = 1L;
  
  private String[] listConfig;

  private JLabel lblUser = new JLabel(Messages.getString("Column.User"),JLabel.TRAILING);
  private JLabel lblPassword = new JLabel(Messages.getString("Column.Password"),JLabel.TRAILING);
  private JLabel lblDatabase = new JLabel(Messages.getString("Column.FileConfig"),JLabel.TRAILING);

  private JTextField txtUser = new JTextField(20);
  private JPasswordField txtPassword = new JPasswordField(20);
  private JComboBox<String> cmbDatabase = new JComboBox<>();

  public Logon() {
    initPnlData();
    initFormNone();
    pack();
    setToCenter();
  }

  private void initPnlData() {
    pnlData.setLayout(new SpringLayout());
    
    pnlData.add(lblUser);
    pnlData.add(txtUser);
    lblUser.setLabelFor(txtUser);

    pnlData.add(lblPassword);
    pnlData.add(txtPassword);
    lblPassword.setLabelFor(txtPassword);

    pnlData.add(lblDatabase);
    pnlData.add(cmbDatabase);
    lblDatabase.setLabelFor(cmbDatabase);

    SpringUtilities.makeCompactGrid(pnlData, 3, 2, 10, 10, 10, 10);
  }  
  
  protected void clearData() {
    txtUser.setText("");
    txtPassword.setText("");
  }
 
  protected boolean fillData() {
    initListDb();
    // -------------------------------
    Properties props = DbConnect.getParams();
    if (props != null) {
      String vIndex = props.getProperty(DbConnect.DB_INDEX,"-1");
      int index = Integer.valueOf(vIndex);
      if (index>=0 && index < cmbDatabase.getItemCount()) {
        cmbDatabase.setSelectedIndex(index);
      }  
    } else {
      if (cmbDatabase.getItemCount()>0 ) {
        cmbDatabase.setSelectedIndex(0);
      }
    }
    // -------------------------------
    return true;
  }

  private void initListDb() {
    // -------------------------------
    if (listConfig == null) {
      listConfig = DbConnect.getFileList();
    } 
    // -------------------------------
    cmbDatabase.removeAllItems();
    for (String conf : listConfig) {
      cmbDatabase.addItem(conf);
    }
//    cmbDatabase.addItem(null);
    
    cmbDatabase.setMaximumRowCount(3);
    cmbDatabase.setSelectedIndex(-1);
    // -------------------------------
    MyActionListener myActionListener = new MyActionListener();
    cmbDatabase.addActionListener(myActionListener);
    // -------------------------------
  }
  
  protected Properties getProperty() {
    String user = txtUser.getText();
    String passwd = new String(txtPassword.getPassword());
    // -------------------------------------------
    if (currProp != null) {
      currProp.setProperty(DbConnect.DB_USER, user);
      currProp.setProperty(DbConnect.DB_PASSWORD, passwd);
    }  
    // -------------------------------------------
    return currProp; 
  }

  protected void setProperty(Properties props) {
    currProp = props;
    if (props != null) {
      txtUser.setText(props.getProperty(DbConnect.DB_USER));
      txtPassword.setText(props.getProperty(DbConnect.DB_PASSWORD));
    } else {
      clearData();
    }
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JComboBox<?> cmb = (JComboBox<?>) e.getSource();
      int index = cmb.getSelectedIndex();
      if (index>=0 && index < cmbDatabase.getItemCount()) {
        String item = (String) cmb.getSelectedItem();
        // ---------------------------------------------
        Properties props = new Properties();
        props.setProperty(DbConnect.DB_INDEX, String.valueOf(index));
        props.setProperty(DbConnect.DB_NAME, item);
        // ----------------------
        DbConnect.readCfg(props);  // user, password
        // ----------------------
        setProperty(props);
      } else {
        setProperty(null);
      }
    }
  }

}
