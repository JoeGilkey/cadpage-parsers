package net.anei.cadpage.parsers.dispatch;

import java.util.regex.Pattern;

import net.anei.cadpage.parsers.HtmlProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.MsgInfo.MsgType;

/**
 * This parser handles a subclass of HTML parsers with some common attributes
 * 
 *  1) They all include a subject of Automatic R&R Notification: xxxx
 *  2) One of two different specialized INFO_BLK blocks where
 *     dispatcher
 *     dash
 *     information
 *  are either combined in one field or split into 3 different fields
 *  
 *  3) A common TIMES format reporting unit dispatch times 
 */

public class DispatchH05Parser extends HtmlProgramParser {
  
  public DispatchH05Parser(String defCity, String defState, String program) {
    super(defCity, defState, program);
  }
  
  private String times;
  
  @Override
  protected boolean parseHtmlMsg(String subject, String body, Data data) {
    if (!subject.startsWith("Automatic R&R Notification")) return false;
    times = null;
    if (!super.parseHtmlMsg(subject, body, data)) return false;
    if (data.msgType == MsgType.RUN_REPORT && times != null) {
      data.strSupp = append(times, "\n\n", data.strSupp);
    }
    return true;
  }

  @Override
  public Field getField(String name) {
    if (name.equals("INFO_BLK")) return new BaseInfoBlockField();
    if (name.equals("TIMES")) return new BaseTimesField();
    return super.getField(name);
  }
  
  private static final Pattern INFO_DATE_TIME_PTN = Pattern.compile("\\*+\\d\\d?/\\d\\d?/\\d{4}\\*+|\\d\\d?:\\d\\d:\\d\\d");
  private static final Pattern INFO_TIMES_MARK_PTN = Pattern.compile("[A-Z0-9]+: .*");
  private class BaseInfoBlockField extends InfoField {
    
    private boolean enabled = false;
    
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      if (INFO_TIMES_MARK_PTN.matcher(field).matches()) return false;
      parse(field, data);
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (INFO_DATE_TIME_PTN.matcher(field).matches()) {
        enabled = false;
        return;
      }
      if (field.equals("-")) {
        enabled = true;
        return;
      }
      if (!enabled) {
        int pt = field.indexOf(" - ");
        if (pt >= 0) {
          field = field.substring(pt+3).trim();
          enabled = true;
        }
      }
      
      if (enabled) {
        data.strSupp = append(data.strSupp, "\n", field);
      }
    }
  }
  
  private class BaseTimesField extends InfoField {
    @Override
    public void parse(String field, Data data) {
      if (field.startsWith("Cleared:")) data.msgType = MsgType.RUN_REPORT;
      if (times == null) {
        times = field;
      } else {
        times = append(times, "\n", field);
      }
    }
  }
}