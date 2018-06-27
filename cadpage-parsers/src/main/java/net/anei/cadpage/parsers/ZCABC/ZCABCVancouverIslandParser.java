package net.anei.cadpage.parsers.ZCABC;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

public class ZCABCVancouverIslandParser extends FieldProgramParser {

  public ZCABCVancouverIslandParser() {
    this("", "BC");
  }
  
  public ZCABCVancouverIslandParser(String defCity, String defState) {
    super(defCity, defState, "CALL? ADDR/ZSC CITY DATETIME!");
    setupCallList(CALL_LIST);
    setupMultiWordStreets(MWORD_STREET_LIST);
    setupGpsLookupTable(GPS_LOOKUP_TABLE);
  }
  
  @Override
  public String getFilter() {
    return "paging@ni911.ca";
  }
  
  @Override
  public String getLocName() {
    if (this.getClass() == ZCABCVancouverIslandParser.class) return "Vancouver Island, BC";
    return super.getLocName();
  }
  
  private static final Pattern SRC_PTN = Pattern.compile("(ARRAS|BEAVER CREEK|CAMPBELL RIVER|CHERRY CREEK|CHETWYND|COMOX|COURTENAY|CUMBERLAND|DAWSON CREEK|DENMAN ISLAND|FANNY BAY|HORNBY ISLAND|MOBERLY LAKE|OYSTER RIVER|POUCE COUPE|PT ALBERNI|PT HARDY|SPROAT LAKE|TOFINO|TOMSLAKE|UCLUELET|UNION BAY) *(.*)");
  private static final Pattern GPS_PTN = Pattern.compile("\\(?([-+]?[\\d:\\.]+),([-+]?[\\d:\\.]+)\\)");
  private static final Pattern TRAIL_GPS_PTN = Pattern.compile("(.*)\\{(.*)\\}");
  private static final Pattern GPS_PTN2 = Pattern.compile("([-+]?\\d+)(\\d{6}),([-+]?\\d+)(\\d{6})");
  
  @Override 
  public boolean parseMsg(String subject, String body, Data data) {
    
    if(!subject.equals("Fire Dispatch")) return false;
    
    // Clean the body of any email text
    int newLine = body.indexOf('\n');
    if(newLine >= 0) {
      body = body.substring(0, newLine);
    }

    // Strip off leading source name
    Matcher match = SRC_PTN.matcher(body);
    if (match.matches()) {
      data.strSource = match.group(1);
      body = match.group(2);
    }
    
    // Process trailing GPS coordinates
    match = TRAIL_GPS_PTN.matcher(body);
    if (match.matches()) {
      body = match.group(1).trim();
      String gps = match.group(2).replace(" ", "");
      gps = gps.replace(" ", "");
      match = GPS_PTN2.matcher(gps);
      if (match.matches()) {
        setGPSLoc(match.group(1)+'.'+match.group(2)+','+match.group(3)+'.'+match.group(4), data);
      }
    }
    
    // GPS coordinates contain a comma which must be escaped
    body = GPS_PTN.matcher(body).replaceAll("($1|$2)");
    
    String[] fields = body.split(",");
    return parseFields(fields, data);
  }
  
  @Override
  public String getProgram() {
    return "SRC " + super.getProgram() + " GPS";
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("ADDR")) return new MyAddressField();
    if (name.equals("DATETIME")) return new MyDateTimeField();
    return super.getField(name);
  }

  private static final Pattern ADDR_GPS_PTN = Pattern.compile("(.*?) *((?:\\bL)?\\([^\\)A-Za-z]+\\))");
  private static final Pattern ADDR_SPEC_PTN = Pattern.compile("(.*)\\{(.*)\\}");
  private class MyAddressField extends AddressField {
    
    @Override
    public void parse(String field, Data data) {
      Matcher match = ADDR_GPS_PTN.matcher(field);
      if (match.matches()) {
        data.strCall = append(data.strCall, " / ", match.group(1));
        data.strAddress = match.group(2).replace('|', ',');
      }
      
      else if ((match = ADDR_SPEC_PTN.matcher(field)).matches()) {
        data.strCall = append(data.strCall, " / ", match.group(1).trim());
        parseAddress(match.group(2).trim(), data);
      }

      else if(data.strCall.length() > 0) {
        parseAddress(field, data);
      }
      else {
        super.parse(field, data);
      }
    }
  }
  
  private static final Pattern DATE_TIME_PTN = Pattern.compile("(?:BC )?(\\d\\d/\\d\\d/\\d{4}) (\\d\\d:\\d\\d:\\d\\d)(?: +(.*))?");
  private class MyDateTimeField extends DateTimeField {
    
    @Override 
    public boolean canFail() {
      return true;
    }
    
    @Override 
    public boolean checkParse(String field, Data data) {
      Matcher match = DATE_TIME_PTN.matcher(field);
      if (!match.matches()) return false;
      data.strDate = match.group(1);
      data.strTime = match.group(2);
      String unit = getOptGroup(match.group(3));
      int pt = unit.indexOf("X-ST:");
      if (pt >= 0) {
        data.strCross = unit.substring(pt+5).trim();
        unit = unit.substring(0, pt).trim();
      }
      data.strUnit = unit;
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if(!checkParse(field, data)) super.abort();
    }
    
    @Override
    public String getFieldNames() {
      return "DATE TIME UNIT X";
    }
  }
  
  private static final String[] MWORD_STREET_LIST = new String[]{
    "ACLE BEACH",
    "AVRO ARROW",
    "BAY VIEW",
    "BEAR CAT",
    "BEAVER CK",
    "BEAVER CREEK",
    "BEAVER HARBOUR",
    "BEN HAPPNER",
    "BLACK BEAR",
    "BLACK CREEK",
    "BLUE JAY",
    "BOMBER BASE",
    "BOUCHER LAKE",
    "BRIND AMOUR",
    "BUCKLEY BAY FRONTAGE",
    "BUCKLEY BAY",
    "BUENA VISTA",
    "CAMPBELL RIVER",
    "CENTRAL LAKE",
    "CENTRAL LK",
    "CHERRY CK",
    "CHERRY CREEK",
    "CHESTERMAN BEACH",
    "CIFIC RIM",
    "CIVIC CORE",
    "COAL HARBOUR",
    "COLLEGE CAMPUS",
    "COMOX LAKE",
    "COMOX LOGGING",
    "COMOX VALLEY",
    "COUGAR SMITH",
    "COUNTRY PLACE",
    "CROWN ISLAND",
    "CROWN ISLE",
    "DISCOVERY HARBOUR",
    "DOLLY VARDEN",
    "DOVE CREEK",
    "ELMA BAY",
    "ESOWISTA IR",
    "FORBIDDEN PLATEAU",
    "FOREST GROVE",
    "GARTLEY POINT",
    "GEORGIA STRAIT",
    "GLACIER VIEW",
    "GLEN EAGLE",
    "GOLD RIVER",
    "HARDY BAY",
    "HART WABI",
    "HIGH SALAL",
    "HILLVIEW ACCESS",
    "HORNE LAKE",
    "INLAND ISLAND",
    "IRACLE BEACH",
    "IRON RIVER",
    "JAMES PAUL",
    "JENSEN COVE",
    "JENSENS BAY",
    "KEITH WAGNER",
    "KYE BAY",
    "LACEY LAKE",
    "LACY LAKE",
    "LAKE TRAIL",
    "LEA SMITH",
    "LITTLE BEAR",
    "LITTLE RIVER",
    "LITTLE TRIBUNE BAY",
    "LONE CONE",
    "LONG BEACH PARK ACCESS",
    "MACKENZIE BEACH",
    "MAPLE RIDGE",
    "MARINE VISTA",
    "MARTIN PARK",
    "MCCOY LAKE",
    "MCIVOR LAKE",
    "MEDICINE WOMAN",
    "MEDICINE WOMEN",
    "MIDDLE POINT",
    "MIRACLE BEACH",
    "MOBERLY HEIGHTS",
    "MOX VALLEY",
    "MYSTERY BEACH",
    "NORTH ACCESS",
    "OCEAN PARK",
    "OYSTER GARDEN",
    "OYSTER RIVER",
    "PACIFIC RIM",
    "PAPER MILL",
    "PARK ACCESS",
    "PIDGEON LAKE",
    "PORT AGUSTA",
    "PORT ALBERNI",
    "PORT AUGUSTA",
    "PT ALBERNI",
    "R RIVER",
    "RADAR HILL",
    "RALPH HUTTON",
    "RIVER BEND",
    "ROCK BAY",
    "ROY CREEK",
    "SALMON POINT",
    "SAND PINES",
    "SEA LION",
    "SEA TERRACE",
    "SHINGLE SPIT",
    "SHIPS POINT",
    "SHOEMAKER BAY",
    "SHOOTING STAR",
    "ST ANDREWS",
    "ST ANNS",
    "ST JOHN'S POINT",
    "ST JOHNS POINT",
    "STIRLING ARM",
    "TATE CREEK",
    "TAYLOR ARM",
    "THE POINT",
    "TONQUIN PARK",
    "TRADING POST",
    "TRIBUNE BAY PROVINCIAL",
    "TSULQUATE IR",
    "VALLEY VIEW",
    "VETERANS MEMORIAL",
    "VILLAGE CONNECTER",
    "VISTA BAY",
    "WALKER FRONTAGE",
    "WALTER GAGE",
    "WILLIAMS BEACH",
    "WILLIAMS BEACH",
    "YEW WOOD"
  };
  
  private static final CodeSet CALL_LIST = new CodeSet(
      "911 ANSWER",
      "ABANDONED 911",
      "ALARMS NON EMERGENCY",
      "ALARMS",
      "AVIATION INCIDENT",
      "BEACH/BRUSH",
      "BEACH/BRUSH NON EMERG",
      "BEACH/BRUSH/MISC OUT EMERG",
      "BEACH/BRUSH/MISC OUT NON EMERG",
      "BEACH/BRUSH/MISC OUT",
      "BOMB THREAT",
      "CARBON MONOXIDE NON EMERG",
      "CARBON MONOXIDE NON EMERGENCY",
      "CARBON MONOXIDE",
      "CHIMNEY FIRE",
      "DUPLICATE",
      "DUTY INVESTIGATION",
      "DUTY OFFICER",
      "FIRST ALARM - A",
      "FIRST ALARM - B",
      "FIRST ALARM - C",
      "FIRST RESP A",
      "FIRST RESP ASSIST D/E",
      "FIRST RESP ASSIST EMERGENCY",
      "FIRST RESP ASSIST ROUTINE",
      "FIRST RESP B",
      "FIRST RESP C",
      "FIRST RESP D",
      "FIRST RESP DELAY B/C",
      "FIRST RESP DELAY D/E",
      "FIRST RESP E",
      "FIRST RESPONDER DELAY B/C",
      "FIRST RESPONDER DELAY D/E",
      "FIRST RESPONDER UNKNOWN",
      "FUEL - LEAK/SPILL/OTH",
      "FUEL - LEAK/SPILL/OTH NON EMER",
      "FUEL - LEAK/SPILL/OTHER NON EMERG",
      "FUEL - LEAK/SPILL/OTHER",
      "GARBAGE CONTAINER",
      "HAZMAT NON EMERGENCY",
      "HAZMAT",
      "HYDRO TROUBLE",
      "MARINE INCIDENT",
      "MARINE",
      "MOTOR VEHICLE ACCIDENT",
      "MOTOR VEHICLE FIRE",
      "MV FIRE",
      "MVI / EXTRICATION",
      "MVI PED STRUCK",
      "MVI PORT",
      "MVI",
      "MVI/EXTRICATION",
      "NATURAL GAS LINE BREAK",
      "NATURAL GAS/PROP NON EMERG",
      "NATURAL GAS/PROPANE EMERGENCY",
      "NATURAL GAS/PROPANE NON EMERG",
      "NATURAL GAS/PROPANE",
      "OUTDOOR FIRE",
      "PUBLIC SERVICE",
      "RESCUE -ELEVATOR",
      "RESCUE - ROAD",
      "RESCUE -CONFINED",
      "RESCUE -HIGH ANGLE",
      "RESCUE -LOW ANGLE/BCAS ASSIST",
      "RESCUE -MARINE",
      "RESCUE -SWIFT WATER",
      "RESCUE LOW ANGLE/BCAS ASSIST",
      "RESCUE - ROAD",
      "RESCUE ROAD",
      "STRUCTURE - SMOKE",
      "STRUCTURE - SMOKE(FIRE IS OUT)",
      "STRUCTURE  - FIRE",
      "STRUCTURE - ELECTRICAL TROUBLE",
      "STRUCTURE - FIRE",
      "STRUCTURE - SMOKE ODOUR",
      "STRUCTURE - SMOKE",
      "STRUCTURE FIRE",
      "STRUCTURE ODOUR",
      "STRUCTURE SMOKE",
      "TEST",
      "TSUNAMI WARNING"
  );
  
  @Override
  protected String adjustGpsLookupAddress(String address) {
    return address.toUpperCase();
  }

  private static final Properties GPS_LOOKUP_TABLE = buildCodeTable(new String[]{
      "HMCS QUADRA",            "+49.662330,-124.914198",
      "10696 TAYLOR ARM DR",    "+49.275430,-124.985284"

  });
}
