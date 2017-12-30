package net.anei.cadpage.parsers.OK;

import net.anei.cadpage.parsers.MsgInfo.Data;

import java.util.regex.Pattern;

import net.anei.cadpage.parsers.HtmlProgramParser;

public class OKYukonParser extends HtmlProgramParser {
  public OKYukonParser() {
    super("YUKON", "OK", "CALL:CALL! PLACE:PLACE! ADDR:ADDRCITY! CROSS_ST:X! ID:ID! PRI:PRI! LAT/LON:GPS! INFO:INFO/N+");
  }

  @Override
  public String getFilter() {
    return "@yukonok.gov";
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("INFO")) return new MyInfoField();
    return super.getField(name);
  }
  
  private static final Pattern DATE_TIME_PTN = Pattern.compile("\\*{3}\\d\\d?/\\d\\d?/\\d{4}\\*{3}|\\d\\d?:\\d\\d:\\d\\d");
  private class MyInfoField extends InfoField {
    
    boolean skip = false;
    
    @Override
    public void parse(String field, Data data) {
      if (skip) {
        if (field.equals("-")) skip = false;
      } else {
        if (DATE_TIME_PTN.matcher(field).matches()) {
          skip = true;
        } else {
          data.strSupp = append(data.strSupp, "/n", field);
        }
      }
    }
  }
}
