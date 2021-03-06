package net.anei.cadpage.parsers.IA;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

public class IACerroGordoCountyParser extends FieldProgramParser {
  
  public IACerroGordoCountyParser() {
    super("CERRO GORDO COUNTY", "IA",
          "Message:INFO! Time:DATE_TIME_PLACE! Address:ADDRCITY! Nearest_intersection:X!");
  }
  
  @Override
  public String getFilter() {
    return "no-reply@zuercherportal.com,zuercher@cgcounty.org";
  }
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    if (!subject.startsWith("Call Type:")) return false;
    data.strCall = subject.substring(10).trim();
    body = stripFieldEnd(body, " Please respond immediately.");
    int pt = body.lastIndexOf("\n\n");
    if (pt >= 0) body = body.substring(pt+2).trim();
    return super.parseMsg(body, data);
  }
  
  @Override
  public String getProgram() {
    return "CALL " + super.getProgram();
  }
  
  @Override
  public Field  getField(String name) {
    if (name.equals("INFO")) return new MyInfoField();
    if (name.equals("DATE_TIME_PLACE")) return new MyDateTimePlaceField();
    if (name.equals("ADDRCITY")) return new MyAddressCityField();
    if (name.equals("X")) return new MyCrossField();
    return super.getField(name);
  }
  
  private class MyInfoField extends InfoField {
    @Override
    public void parse(String field, Data data) {
      if (field.equals("None")) return;
      super.parse(field, data);
    }
  }
  
  private static final Pattern DATE_TIME_PLACE_PTN = Pattern.compile("(\\d\\d/\\d\\d/\\d\\d) (\\d\\d:\\d\\d)\\b *(.*)");
  private class MyDateTimePlaceField extends Field {
    @Override
    public void parse(String field, Data data) {
      Matcher match = DATE_TIME_PLACE_PTN.matcher(field);
      if (!match.matches()) abort();
      data.strDate = match.group(1);
      data.strTime = match.group(2);
      data.strPlace = match.group(3);
    }
    
    @Override
    public String getFieldNames() {
      return "DATE TIME PLACE";
    }
  }
  
  private static final Pattern ADDR_ST_ZIP = Pattern.compile("(.*), *([A-Z]{2})(?: (\\d{5}))?");
  private class MyAddressCityField extends AddressCityField {
    @Override
    public void parse(String field, Data data) {
      String zip = null;
      Matcher match = ADDR_ST_ZIP.matcher(field);
      if (match.matches()) {
        field = match.group(1).trim();
        data.strState = match.group(2);
        zip = match.group(3);
      }
      super.parse(field, data);
      if (data.strCity.length() == 0 && zip != null) data.strCity = zip;
    }
    
    @Override
    public String getFieldNames() {
      return super.getFieldNames() + " ST";
    }
  }
  
  private class MyCrossField extends CrossField {
    @Override
    public void parse(String field, Data data) {
      field = stripFieldEnd(field, "None");
      super.parse(field, data);
    }
  }
}
