package net.anei.cadpage.parsers.SC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

public class SCCharlestonCountyBParser extends FieldProgramParser {
  
  public SCCharlestonCountyBParser() {
    super("CHARLESTON COUNTY", "SC", 
          "UNIT ADDR X/Z? CITY CALL! CH! ID! DATETIME");
  }
  
  @Override
  public String getFilter() {
    return "20691";
  }
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    body = stripFieldStart(body, ":");
    return parseFields(body.split("\n"), data);
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("ID")) return new IdField("\\d\\d-\\d{7}", true);
    if (name.equals("CALL")) return new MyCallField();
    if (name.equals("CH")) return new ChannelField("(?:Incident Channel:|INC-) *(.*)", true);
    if (name.equals("DATETIME")) return new MyDateTimeField();
    return super.getField(name);
  }
  
  private class MyCallField extends CallField {
    @Override
    public void parse(String field, Data data) {
      field = stripFieldStart(field, "*");
      super.parse(field, data);
    }
  }
  
  private static final Pattern DATE_TIME_PTN = Pattern.compile("(\\d{4})-(\\d\\d)-(\\d\\d) +(\\d\\d?:\\d\\d:\\d\\d)");
  private class MyDateTimeField extends DateTimeField {
    @Override
    public void parse(String field, Data data) {
      Matcher match = DATE_TIME_PTN.matcher(field);
      if (!match.matches()) abort();
      data.strDate = match.group(2)+'/'+match.group(3)+'/'+match.group(1);
      data.strTime = match.group(4);
    }
  }
  
  @Override
  public String adjustMapCity(String city) {
    if (city.equalsIgnoreCase("ST PAULS")) return "";
    if (city.equalsIgnoreCase("UNINCORPORATED")) return "";
    return city;
  }
}
