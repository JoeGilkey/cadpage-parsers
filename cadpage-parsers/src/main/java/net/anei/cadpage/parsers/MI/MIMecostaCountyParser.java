package net.anei.cadpage.parsers.MI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchA3Parser;


/**
Mecosta County, MI

 */
public class MIMecostaCountyParser extends FieldProgramParser {
  
  public MIMecostaCountyParser() {
    this("MECOSTA COUNTY", "MI");
  }
  
  public MIMecostaCountyParser(String defCity, String defState) {
    super(defCity, defState, 
          "ADDR ( CITY ST_ZIP? | ) X APT_PLACE CALL UNIT! ( NONE END | INFO/CS+ )");
  }
  
  @Override
  public String getFilter() {
    return "zuercher@mcd911.org";
  }

  @Override
  public String getAliasCode() {
    return "MIMceolaCounty";
  }
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    
    if (!subject.startsWith("Respond:")) return false;
    data.strCall = subject.substring(8).trim();
    
    return parseFields(body.split(","), data);
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("CITY")) return new MyCityField();
    if (name.equals("ST_ZIP")) return new MyStateZipField();
    if (name.equals("APT_PLACE")) return new MyAptPlaceField();
    if (name.equals("UNIT")) return new MyUnitField();
    if (name.equals("INFO")) return new MyInfoField();
    if (name.equals("NONE")) return new SkipField("None", true);
    return super.getField(name);
  }
  
  private class MyCityField extends CityField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      if (field.contains("(")) return false;
      if (field.contains(" and ")) return false;
      parse(field,  data);
      return true;
    }
  }
  
  private static final Pattern ST_ZIP_PTN = Pattern.compile("([A-Z]{2})(?: +(\\d{5}))?");
  private class MyStateZipField extends Field {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      Matcher match = ST_ZIP_PTN.matcher(field);
      if (!match.matches()) return false;
      data.strState = match.group(1);
      if (data.strCity.length() == 0) data.strCity = getOptGroup(match.group(2));
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
    
    @Override
    public String getFieldNames() {
      return "ST CITY?";
    }
  }
  
  private static final Pattern APT_PTN = Pattern.compile("(?:APT|RM|ROOM|LOT) *(.*)|(\\d{1,5}[A-Z]?|[A-Z])");
  private class MyAptPlaceField extends Field {

    @Override
    public void parse(String field, Data data) {
      if (field.equals("None")) return;
      Matcher match = APT_PTN.matcher(field);
      if (match.matches()) {
        String apt = match.group(1);
        if (apt == null) apt = match.group(2);
        data.strApt = append(data.strApt, "-", apt);
      } else {
        data.strPlace = field;
      }
    }

    @Override
    public String getFieldNames() {
      return "APT PLACE";
    }
    
  }
  
  private class MyUnitField extends UnitField {
    @Override
    public void parse(String field, Data data) {
      field = field.replace("; ", ",").replace(';', ',');
      super.parse(field, data);
    }
  }
  
  private static final Pattern INFO_DATETIME_PTN = Pattern.compile("(?:^|; +)\\d\\d?/\\d\\d?/\\d\\d +\\d\\d?:\\d\\d:\\d\\d - *");
  private class MyInfoField extends InfoField {
    @Override
    public void parse(String field, Data data) {
      if (field.equals("None")) return;
      field = INFO_DATETIME_PTN.matcher(field).replaceAll("\n").trim();
      super.parse(field, data);
    }
  }
}
