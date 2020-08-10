package acp.forms;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

import acp.db.service.*;
import acp.forms.frame.*;
import acp.forms.utils.*;
import acp.utils.*;

public class About extends FrameView {
  private static final long serialVersionUID = 1L;

  private VarManagerEdit tableManager;
 
  private JEditorPane epText = new JEditorPane();
  private JPanel pnlAbout;
  private JScrollPane spAbout;

  public About() {
    setSize(600, 450);
//  setResizable(true);

    tableManager = new VarManagerEdit();
    
    initPnlData();
    initFormNone();
//    pack();
    setToCenter();
  }

  private void initPnlData() {
    pnlData.setLayout(new BorderLayout());

    epText.setEditorKit(new HTMLEditorKit());
    epText.setEditable(false);
    epText.setText("");

    pnlAbout = new JPanel();
    pnlAbout.setLayout(new SpringLayout());
    pnlAbout.add(epText);
    SpringUtilities.makeGrid(pnlAbout, 1, 1, 0, 0, 0, 0);

    spAbout = new JScrollPane(pnlAbout);
    pnlData.add(spAbout);  
  }

  protected void initFormBefore() {
    setTitle(Messages.getString("Title.About"));
  }

  protected boolean fillData() {
    Map<String,String> varMap = new HashMap<>();
    tableManager.fillVars(varMap);
    //String text = "";
    String text = fillText(varMap);
    epText.setText(text);
    return true;
  }

  private String fillText(Map<String,String> varMap) {
    // ------------------------------------
    String versionMss = varMap.get("VERSION");
    String versionMssDate = varMap.get("VERSION_DATE");
    String certSystem = varMap.get("CERT_SYSTEM");
    String certProduct = varMap.get("CERT_PRODUCT");
    String certTu = varMap.get("CERT_TU");
    String certPartNumb = varMap.get("CERT_PARTNUMBER");
    String certAddress = varMap.get("CERT_ADDRESS");
    String certPhone = varMap.get("CERT_PHONE");
    String certFax = varMap.get("CERT_FAX");
    String certEmail = varMap.get("CERT_EMAIL");
    String certEmailSup = varMap.get("CERT_EMAIL_SUPPORT");
    String certWww = varMap.get("CERT_WWW");
    // ------------------------------------
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head></head><body style=\"font: Sans 10pt\">");
    sb.append("<table width=\"100%\">");
    sb.append("<tr><td colspan=\"3\" align=\"center\"><h3>" + certSystem
        + "</h3></td></tr>");
    sb.append("<tr><td colspan=\"3\" align=\"center\"><h4>Комплекс \""
        + certProduct + "\"</h4></td></tr>");
    sb.append("<tr><td colspan=\"2\">Релиз ПО:</td>");
    sb.append("<td>" + versionMss + " от " + versionMssDate + "</td></tr>");
    sb.append("<tr><td colspan=\"2\">Технические условия:</td>");
    sb.append("<td>" + certTu + "</td></tr>");
    sb.append("<tr><td colspan=\"2\">Заводской номер:</td>");
    sb.append("<td>" + certPartNumb + "</td></tr>");
    sb.append("<tr><td colspan=\"3\">Контактная информация:</td></tr>");
    sb.append("<tr><td colspan=\"3\">");
    sb.append("Межрегиональный филиал информационно-сетевых технологий ОАО \"Уралсвязьинформ\"</td></tr>");
    sb.append("<tr><td colspan=\"3\">" + certAddress + "</td></tr>");
    sb.append("<tr><td width=\"30\">&nbsp;</td><td colspan=\"2\" width=\"90%\">тел.: "
        + certPhone + "</td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">факс: " + certFax
        + "</td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">e-mail: <a href=\""
        + certEmail + "\">");
    sb.append(certEmail + "</a></td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">support e-mail: <a href=\""
        + certEmailSup + "\">");
    sb.append(certEmailSup + "</a></td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\"><a href=\"" + certWww
        + "\">" + certWww + "</a></td></tr>");
    sb.append("</table></body></html>");
    // ------------------------------------
    return sb.toString();
  }

}
