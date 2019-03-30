package net.anei.cadpage.parsers.AR;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.HtmlDecoder;
import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.ReverseCodeSet;

public class ARHotSpringCountyParser extends FieldProgramParser {
  
  private HtmlDecoder decoder = new HtmlDecoder();
  
  public ARHotSpringCountyParser() {
    super("HOT SPRING COUNTY", "AR", 
          "( RUN_REPORT/R RUN_REPORT! CALL:CALL! PLACE:PLACE! ADDR:ADDR! CITY:CITY! ID:ID! Cross-Street:X! ( Notes:INFO! | INFO:INFO! ) " +
          "| CALL:CALL! PLACE:PLACE? ADDR:ADDR! CITY:CITY! XY:GPS? ID:ID? PRI:PRI? DATE:DATETIME! TIME:TIME? X:X? INFO:INFO! " + 
          "| Category:CALL! Address:ADDR2! Intersection:X? Business_Name:PLACE! Event_#:ID! Date_/_Time:DATETIME! Notes:INFO " + 
          ") INFO/N+? CAD END");
  }
  
  @Override
  public String getFilter() {
    return "DISPATCH@HOTSPRINGDEM.ORG";
  }
  
  @Override
  public int getMapFlags() {
    return MAP_FLG_PREFER_GPS;
  }
  
  @Override
  protected boolean parseHtmlMsg(String subject, String body, Data data) {
    if (body.startsWith("<html>")) {
      if (!body.contains("\tActiveReports Document\n")) return false;
      String[] flds = decoder.parseHtml(body);
      List<String> list = new ArrayList<String>();
      StringBuilder sb = new StringBuilder();
      boolean info = false;
      for (String fld : flds) {
        if (fld.startsWith("©")) break;
        if (sb == null) {
          list.add(fld);
          continue;
        }
        if (fld.endsWith(":")) {
          if (sb.length() > 0) {
            list.add(sb.toString());
            sb.setLength(0);
          }
        }
        if (sb.length() > 0) sb.append(' ');
        sb.append(fld);
        if (fld.equals("INFO:") || fld.equals("Notes:")) info = true;
        else if (info) {
          list.add(sb.toString());
          sb = null;
        }
      }
      if (sb != null && sb.length() > 0) list.add(sb.toString());
      return parseFields(list.toArray(new String[list.size()]), data);
    }
    
    return super.parseHtmlMsg(subject, body, data);
  }

  @Override
  protected boolean parseMsg(String body, Data data) {
    int pt = body.indexOf('�');
    if (pt >= 0) body = body.substring(0, pt).trim();
    
    if (body.startsWith("CALL:") || body.startsWith("RUN REPORT")) {
      return super.parseFields(body.split("\n"), data);
    }
    
    if (body.startsWith("Category:")) {
      body = body.replace('\n', ' ');
      return super.parseMsg(body, data);
    }
    
    return false;
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("RUN_REPORT")) return new SkipField("RUN REPORT");
    if (name.equals("CALL"))  return new MyCallField();
    if (name.equals("ADDR2"))  return new MyAddress2Field();
    if (name.equals("CITY")) return new MyCityField();
    if (name.equals("DATETIME")) return new MyDateTimeField();
    if (name.equals("TIME")) return new TimeField("\\d\\d:\\d\\d:\\d\\d", true);
    if (name.equals("X")) return new MyCrossField();
    if (name.equals("CAD")) return new IdField("CAD#[- ]+(.*)", true);
    return super.getField(name);
  }
  
  private class MyCallField extends CallField {
    @Override
    public void parse(String field, Data data) {
      field = stripFieldEnd(field, "/");
      super.parse(field, data);
    }
  }
  
  private class MyCityField extends CityField {
    @Override
    public void parse(String field, Data data) {
      if (field.equalsIgnoreCase("COUNTY")) return;
      super.parse(field, data);
    }
  }
  
  private static final Pattern ADDR_PTN = Pattern.compile("(\\d+)([^ ].*)");
  private class MyAddress2Field extends AddressField {
    @Override
    public void parse(String field, Data data) {
      
      // Extract city from end of field
      String city = CITY_LIST.getCode(field);
      if (city != null) {
        field = field.substring(0, field.length()-city.length()).trim();
        if (!city.equals("COUNTY")) data.strCity = city;
      }
      
      // Replace blank between house number and street name
      Matcher match = ADDR_PTN.matcher(field);
      if (match.matches()) field =  match.group(1) + ' ' + match.group(2);
      
      // parse address
      super.parse(field, data);
    }
    
    @Override
    public String getFieldNames() {
      return super.getFieldNames() + " CITY";
    }
  }
  
  private static final Pattern DATE_TIME_PTN = Pattern.compile("(\\d\\d?/\\d\\d?/\\d{4})(?: (\\d\\d?:\\d\\d:\\d\\d [AP]M))?");
  private static final DateFormat TIME_FMT = new SimpleDateFormat("hh:mm:ss aa");
  private class MyDateTimeField extends DateTimeField {
    @Override
    public void parse(String field, Data data) {
      Matcher match = DATE_TIME_PTN.matcher(field);
      if (!match.matches()) abort();
      data.strDate = match.group(1);
      String time = match.group(2);
      if (time != null) setTime(TIME_FMT, time, data);
    }
  }
  
  private class MyCrossField extends CrossField {
    @Override
    public void parse(String field, Data data) {
      field = field.replace('@', '/');
      super.parse(field, data);
    }
  }
  
  private static final ReverseCodeSet CITY_LIST = new ReverseCodeSet(
      
      "COUNTY",

      // Cities
      "MALVERN",
      "ROCKPORT",

      // Towns
      "DONALDSON",
      "FRIENDSHIP",
      "MAGNET COVE",
      "MIDWAY",
      "PERLA",

      //Unincorporated community
      "BISMARCK"
  );
}
