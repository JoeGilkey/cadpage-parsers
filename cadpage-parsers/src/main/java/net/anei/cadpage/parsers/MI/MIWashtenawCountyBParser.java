package net.anei.cadpage.parsers.MI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.MsgInfo.MsgType;
import net.anei.cadpage.parsers.FieldProgramParser;

public class MIWashtenawCountyBParser extends FieldProgramParser {
  
  public MIWashtenawCountyBParser() {
    super("WASHTENAW COUNTY", "MI", 
          "( Call:CALL! Incident:ID! Address:ADDRCITY! Coordinates:GPS! Address_Comment:PLACE! Resource:UNIT! Response:PRI! Notes:INFO! INFO/N+ " +
          "| CALL:CALL! PLACE:PLACE? ADDR:ADDRCITY! ADDR_COMMENT:PLACE? ID:ID? PRI:PRI? DATE:DATETIME? INFO:INFO? Additional_Info:INFO?" +
          "| INCIDENT_COMPLETE! Location:ADDRCITY! Location_Comment:PLACE! Nature:CALL? INFO/N+ )");
  }
  
  @Override
  public String getFilter() {
    return "noreply@emergenthealth.org,cadpaging@emergenthealth.org";
  }
  
  private static final Pattern SUBJECT_PTN = Pattern.compile("(New Incident|Update to Incident|Incident Completed|Incident Cancelled) - (\\d+)");
  private static final Pattern MASTER = Pattern.compile("New Incident:\n(.*?) - (.*?) at(?: (.*))?");
  private static final Pattern TRAIL_ST_ZIP_PTN = Pattern.compile("(.*?)(?:, *([A-Z]{2}))?(?: +(\\d{5}))");
  private static final Pattern ADDR_DELIM = Pattern.compile(" *, *");
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    Matcher match = SUBJECT_PTN.matcher(subject);
    if (!match.matches()) return false;
    String type = match.group(1);
    data.strCallId = match.group(2);
    
    if (body.startsWith("CALL:") || body.startsWith("Call:")) {
      String[] flds = body.split("\n");
      if (flds.length >= 3) {
        if (!parseFields(flds, data)) return false;
      }
      else {
        body = body.replace("ID:", " ID:");
        if (!super.parseMsg(body, data)) return false;
      }
    }
    
    else if (body.startsWith("Incident ")) {
      if (!super.parseFields(body.split("\n"), data)) return false;
    }
    
    else {
      setFieldList("PRI CALL PLACE ADDR APT CITY ST INFO");
      
      match = MASTER.matcher(body);
      if (!match.matches()) return false;
      data.strPriority = match.group(1);
      data.strCall = match.group(2);
      String addr = getOptGroup(match.group(3));
      
      int pt = addr.indexOf(" -  - ");
      if (pt >= 0) {
        data.strSupp = addr.substring(pt+6).trim();
        addr = addr.substring(0,pt).trim();
      }
      
      pt = addr.indexOf('(');
      if (pt >= 0) {
        data.strPlace = addr.substring(0,pt).trim();
        int pt2 = addr.indexOf(')', pt+1);
        if (pt2 < 0) return false;
        addr = addr.substring(pt+1, pt2).trim();
      }
      
      String zip = null;
      match = TRAIL_ST_ZIP_PTN.matcher(addr);
      if (match.matches()) {
        addr = match.group(1).trim();
        data.strState = getOptGroup(match.group(2));
        zip = match.group(3);
      }
      
      String[] parts = ADDR_DELIM.split(addr);
      switch (parts.length) {
      case 1:
        if (data.strPlace.length() > 0) {
          parseAddress(data.strPlace, data);
          data.strCity = parts[0];
          data.strPlace = "";
        } else {
          parseAddress(parts[0], data);
        }
        break;
      
      case 2:
        parseAddress(parts[0], data);
        data.strCity = parts[1];
        break;
        
      case 3:
        parseAddress(parts[0], data);
        data.strPlace = append(parts[1], " - ", data.strPlace);
        data.strCity = parts[2];
        break;
      
      default:
        return false;
      }
      
      if (data.strCity.length() == 0 && zip != null) data.strCity = zip;
    }
    
    if (type.equals("Incident Completed")) {
      data.msgType = MsgType.RUN_REPORT;
    } else if (type.equals("Incident Cancelled")) {
      data.msgType = MsgType.RUN_REPORT;
      data.strCall = append("Cancelled", " - ", data.strCall);
    } else if (type.equals("Update to Incident")) {
      data.strCall = append("(UPDATE)", " ", data.strCall);
    }
    return true;
  }
  
  @Override
  public String getProgram() {
    return "CALL? ID " + super.getProgram();
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("ADDRCITY")) return new MyAddressCityField();
    if (name.equals("GPS")) return new MyGPSField();
    if (name.equals("DATETIME")) return new MyDateTimeField();
    if (name.equals("INCIDENT_COMPLETE")) return new SkipField("Incident \\d+ Completed", true);
    return super.getField(name);
  }
  
  private static final Pattern ADDR_ZIP_PTN = Pattern.compile("(.*?) +(\\d{5})");
  private class MyAddressCityField extends AddressCityField {
    @Override
    public void parse(String field, Data data) {
      String zip = null;
      Matcher match = ADDR_ZIP_PTN.matcher(field);
      if (match.matches()) {
        field = match.group(1);
        zip = match.group(2);
      }
      super.parse(field, data);
      if (data.strCity.length() == 0 && zip != null) data.strCity = zip;
    }
  }
  
  private class MyGPSField extends GPSField {
    @Override
    public void parse(String field, Data data) {
      field = field.replace(';', ',');
      super.parse(field, data);
    }
  }
  
  private static final Pattern DATE_TIME_PTN = Pattern.compile("(\\d\\d?-\\d\\d?-\\d{4}) (\\d\\d?:\\d\\d)");
  private class MyDateTimeField extends DateTimeField {
    @Override
    public void parse(String field, Data data) {
      Matcher match = DATE_TIME_PTN.matcher(field);
      if (!match.matches()) abort();
      data.strDate = match.group(1).replace('-', '/');
      data.strTime = match.group(2);
    }
  }
}
