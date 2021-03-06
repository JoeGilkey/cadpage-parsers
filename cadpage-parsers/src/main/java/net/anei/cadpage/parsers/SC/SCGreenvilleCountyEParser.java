package net.anei.cadpage.parsers.SC;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.MsgInfo.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.SmartAddressParser;

public class SCGreenvilleCountyEParser extends SmartAddressParser {
  
  public SCGreenvilleCountyEParser() {
    super(SCGreenvilleCountyParser.CITY_LIST, "GREENVILLE COUNTY", "SC");
    setupCallList(CALL_LIST);
    setupMultiWordStreets(MWORD_STREET_LIST);
    removeWords("GATEWAY", "PLACE", "ROAD");
  }
  
  @Override
  public String getFilter() {
    return "InformCADPaging@Greenvillecounty.org,@whfd.org";
  }

  private static final Pattern MISSING_BLANK_PTN = Pattern.compile("(?<=Mutual Aid/Assist Outside Agen|Motor Vehicle Collision/Injury|Struct Fire Resi Single Family|Vehicle Fire Comm/Box/Mot Home)(?! )");
  private static final Pattern MASTER1 = Pattern.compile("(.*?)\\(C\\) (.*?)((?:Non-)?Emergency|Bravo) (\\d{4,6}-\\d{6})\\b *(.*)");
  private static final Pattern MASTER2 = Pattern.compile("Incident Type:(.*?) Location:(.*)");
  private static final Pattern INFO_DELIM_PTN = Pattern.compile(" \\[\\d+\\] ");
  private static final Pattern CITY_DASH_PTN = Pattern.compile("(?<=[A-Z])-(?= )");
  private static final Pattern APT_PTN = Pattern.compile("[A-Z]?\\d{1,5}[A-Z]?|[A-Z]");
  private static final Pattern INFO_BRK_PTN = Pattern.compile("\\[1?\\d\\]");
  private static final Pattern INFO_GPS_PTN = Pattern.compile(".*\\bLAT: ([-+]?\\d{2,3}\\.\\d{6,}) LON: ([-+]?\\d{2,3}\\.\\d{6,})\\b.*"); 
  private static final Pattern INFO_JUNK_PTN = Pattern.compile("\\*+ADD'L Wireless Info : .*|Automatic Case Number\\(s\\) issued for Incident #.*|\\*+Class of Seri?vi?ce.*");
  
  @Override
  protected boolean parseMsg(String body, Data data) {
    
    // Rule out some other alert formats
    if (body.startsWith("CAD:")) return false;
    

    body = stripFieldStart(body, "*");
    body = body.replace(" GREER` ", " GREER ");
    Matcher match = MASTER2.matcher(body);
    if (match.matches()) {
      setFieldList("CALL ADDR APT CITY UNIT");
      data.strCall = match.group(1).trim();
      String addr = match.group(2).trim();
      int pt = addr.indexOf("(C)");
      if (pt >= 0) {
        String cityUnit = addr.substring(pt+3).trim();
        addr = addr.substring(0,pt).trim();
        parseAddress(addr, data);
        parseAddress(StartType.START_ADDR, FLAG_ONLY_CITY, cityUnit, data);
        data.strUnit = getLeft();
      } else {
        parseAddress(StartType.START_ADDR,addr, data);
        data.strUnit = getLeft();
      }
      return true;
    }

    boolean good = false;
    String extra = null;
    match = INFO_DELIM_PTN.matcher(body);
    if (match.find()) {
      good = true;
      extra = body.substring(match.end()).trim();
      body = body.substring(0, match.start());
      if (body.endsWith(" ") || body.contains("   ")) return false;
    }
    
    if (body.contains(";")) return false;
    body = MISSING_BLANK_PTN.matcher(body).replaceFirst(" ");
    
    match = MASTER1.matcher(body);
    if (match.matches()) {
      good = true;
      setFieldList("CALL CITY ADDR APT PLACE PRI ID X GPS INFO");
      data.strCall = match.group(1).trim();
      String cityAddr = match.group(2).trim();
      data.strPriority = match.group(3);
      data.strCallId = match.group(4);
      data.strCross = match.group(5);
      
      parseAddress(StartType.START_ADDR, FLAG_ONLY_CITY, cityAddr, data);
      parseAddress(StartType.START_ADDR, FLAG_NO_CITY, getLeft(), data);
      data.strPlace = getLeft();
      if (data.strAddress.length() == 0) {
        String addr = data.strCall;
        data.strCall = "";
        parseAddress(StartType.START_CALL, FLAG_START_FLD_REQ | FLAG_NO_CITY | FLAG_ANCHOR_END, addr, data);
      }
    }
    
    else {
      setFieldList("CALL ADDR APT CITY PLACE GPS INFO");
      int pt = body.indexOf("(C)");
      if (pt >= 0) {
        good = true;
        String cityPlace = body.substring(pt+3).trim();
        body = body.substring(0, pt).trim();
        parseAddress(StartType.START_CALL, FLAG_IGNORE_AT | FLAG_START_FLD_NO_DELIM | FLAG_NO_CITY | FLAG_ANCHOR_END, body, data);
        cityPlace = CITY_DASH_PTN.matcher(cityPlace).replaceFirst("");
        parseAddress(StartType.START_ADDR, FLAG_ONLY_CITY, cityPlace, data);
        data.strPlace = getLeft();
      } else {
        parseAddress(StartType.START_CALL, FLAG_START_FLD_REQ | FLAG_START_FLD_NO_DELIM | FLAG_IGNORE_AT, body, data);
        data.strPlace = getLeft();
        if (data.strCity.length() > 0) good = true;
      }
    }
    
    if (APT_PTN.matcher(data.strPlace).matches()) {
      data.strApt = append(data.strApt, "-", data.strPlace);
      data.strPlace = "";
    }
    
    if (extra != null) {
      for (String part : INFO_BRK_PTN.split(extra)) {
        part = part.trim();
        part = stripFieldEnd(part, ",");
        part = stripFieldEnd(part, "[Shared]");
        
        match = INFO_GPS_PTN.matcher(part);
        if (match.matches()) {
          setGPSLoc(match.group(1)+','+match.group(2), data);
          continue;
        }
        
        if (INFO_JUNK_PTN.matcher(part).matches()) continue;
        
        data.strSupp = append(data.strSupp, "\n", part);
      }
    }
    
    return good;
  }
  
  private static final String[] MWORD_STREET_LIST = new String[]{
      "ANDERSON RIDGE",
      "ANSEL SCHOOL",
      "ASSEMBLY VIEW",
      "AUTUMN HILL",
      "BAILEY MILL",
      "BAMBER GREEN",
      "BATES CROSSING",
      "BAY HILL",
      "BEAVER DAM",
      "BELUE MILL",
      "BENT CREEK",
      "BERRY GLEN",
      "BERRY MILL",
      "BLACKBERRY VALLEY",
      "BLUE RIDGE",
      "BOILING SPRINGS",
      "BONNIE WOODS",
      "BROCKMAN MCCLIMON",
      "BROOK GLENN",
      "BROOKE LEE",
      "BROOKS POINTE",
      "BRUSHY CREEK",
      "BUNKER HILL",
      "BURNT NORTON",
      "BUTLER SPRINGS",
      "CALLAHAN MOUNTAIN",
      "CAMP CREEK",
      "CAMPBELL MILL",
      "CCC CAMP",
      "CEDAR GLENN",
      "CEDAR GROVE",
      "CEDAR LANE",
      "CEDAR PINES",
      "CHANDLER CREEK",
      "CHICK SPRINGS",
      "CLEAR SPRINGS",
      "CLIMBING ROSE",
      "CONNORS CREEK",
      "COTTAGE CREEK",
      "COUNTRY CLUB",
      "COUNTRY COVE",
      "CRIMSON GLORY",
      "CROWN EMPIRE",
      "DRY POCKET",
      "DUCK POND",
      "DUNCAN CHAPEL",
      "DUNKLIN BRIDGE",
      "EAGLE CREEK",
      "EASTON MEADOW",
      "EDWARDS MILL",
      "ELSTAR LOOP",
      "FAIRVIEW CHURCH",
      "FARRS BRIDGE",
      "FAWN CREEK",
      "FEWS BRIDGE",
      "FIVE FORK PLAZA",
      "FIVE FORKS",
      "FORK SHOALS",
      "FOUR OAK",
      "FOX RIDGE",
      "GAP CREEK",
      "GLASSY FALLS",
      "GLASSY MOUNTAIN",
      "GLOBAL COMMERCE",
      "GOODWIN BRIDGE",
      "GREEN VALLEY",
      "GROCE MEADOW",
      "GSP LOGISTICS",
      "GUM SPRINGS",
      "HAMMETT BRIDGE",
      "HARRISON BRIDGE",
      "HART CUT",
      "HIDDEN SPRINGS",
      "HILLSIDE CHURCH",
      "HOKE SMITH",
      "HOLLY HILL",
      "HUNTS BRIDGE",
      "J VERNE SMITH",
      "JENKINS BRIDGE",
      "JOHN THOMAS",
      "JONES KELLEY",
      "JORDAN EBENEZER",
      "JUG FACTORY",
      "KEELER BRIDGE",
      "KEELER MILL",
      "KELSEY GLEN",
      "KNIGHTS SPUR",
      "KNOB CREEK",
      "LAKE CUNNINGHAM",
      "LAKE SHORE",
      "LAND GRANT",
      "LAUREN WOOD",
      "LEE VAUGHN",
      "LEMON CREEK",
      "LEXINGTON PLACE",
      "LINDSEY LAKE",
      "LIONS CLUB",
      "LISMORE PARK",
      "LITTLE TEXAS",
      "LOCUST HILL",
      "LOFTY RIDGE",
      "LONG SHOALS",
      "MAGNOLIA PLACE",
      "MAYS BRIDGE",
      "MCCULLOUGH SCHOOL",
      "MCKITTRICK BRIDGE",
      "MEADOW FORK",
      "MITER SAW",
      "MOODY BRIDGE",
      "MOUNTAIN CREEK",
      "MOUNTAIN CREST",
      "MOUNTAIN LAKE",
      "MOUNTAIN VIEW",
      "MOUNTAIN VIEW SCHOOL",
      "MUSH CREEK",
      "NEELY MILL",
      "OAK GROVE",
      "OAK HAVEN",
      "OAK RIDGE",
      "OAK WIND",
      "PALM SPRINGS",
      "PARIS CREEK",
      "PARIS VIEW",
      "PARNELL BRIDGE",
      "PELHAM SQUARE",
      "PETER MCCORD",
      "PILGRIMS POINT",
      "PINE KNOLL",
      "PINE VIEW",
      "PLACID FOREST",
      "PLEASANT HILL",
      "PLUMLEY SUMMIT",
      "POPLAR VALLEY",
      "PREWETTE HILL",
      "QUAIL RUN",
      "RED HILL",
      "REID SCHOOL",
      "RILEY SMITH",
      "RIO VISTA",
      "RIVER FALLS",
      "RIVER OAKS",
      "ROBERTS HILL",
      "ROBIN HOOD",
      "ROCKY KNOLL",
      "ROE FORD",
      "ROPER MOUNTAIN",
      "RUTLEDGE LAKE",
      "SALLY GILREATH",
      "SALUDA LAKE",
      "SANDY FLAT",
      "SILVER CREEK",
      "SIMPLY LESS",
      "SPRING PARK",
      "SQUIRES CREEK",
      "ST ANDREWS",
      "ST AUGUSTINE",
      "ST MARK",
      "STANDING SPRINGS",
      "STATE PARK",
      "TALL PINES",
      "TEA OLIVE",
      "TERRY CREEK",
      "TERRY SHOP",
      "TEX MCCLURE",
      "THREE OAK",
      "TIMBER GLEN",
      "TRAMMELL MOUNTAIN",
      "TRIDENT MAPLE",
      "TROTTERS FIELD",
      "TUBBS MOUNTAIN",
      "TURTLE CREEK",
      "VALLEY CREEK",
      "VALLEY VIEW",
      "VICTOR HILL",
      "WADE HAMPTON",
      "WATKINS BRIDGE",
      "WHITE HORSE",
      "WILD ORCHARD",
      "WILLIAM SETH",
      "WINDSOR CREEK",
      "WINDY OAK",
      "WOODS CHAPEL"
  };
  
  private static final CodeSet CALL_LIST = new CodeSet(
      "ABDOMINAL PAIN",
      "ALARM CARBON MONOXIDE",
      "ALARM COMMERCIAL",
      "ALARM HIGH LIFE HAZARD",
      "ALARM HIGH RISE",
      "ALARM MULTI OCCUPANCY",
      "ALARM RESIDENTIAL",
      "ALLERGIES/ENVENOMATIONS",
      "ANIMAL BITE/ATTACK",
      "ASSAULT/SEXUAL ASSAU",
      "ASSAULT/SEXUAL ASSAULT",
      "BACK PAIN (NON TRAUMATIC)",
      "BREATHING PROB_D1",
      "BREATHING PROBLEMS",
      "ALARM CARBON MONOXID",
      "CARDIAC/RESP ARREST",
      "CARDIAC/RESP. ARRES",
      "CARDIAC/RESP. ARREST",
      "CHEST PAIN / CHEST D",
      "CHEST PAIN / CHEST DISCOMFOR",
      "CHOKING",
      "CITIZEN ASSIST/SERVI",
      "CITIZEN ASSIST/SERVI",
      "CITIZEN ASSIST/SERVICE CALL",
      "CO/INHALATION/HAZMAT",
      "CONFINED SPACE/STRUCT COLLAPSE(A)",
      "CONVULSIONS/SEIZURES",
      "DIABETIC PROBLEMS",
      "ELECTRICAL HAZARD",
      "ELECTROCUTION/LIGHTN",
      "ELEVATOR/ESCALATOR RESCUE",
      "EMS STANDBY",
      "EXPLOSION",
      "EYE PROBLEM/INJURY",
      "FALL_A2",
      "FALL_B1",
      "FALL_D4",
      "FALLS",
      "FUEL SPILL/FUEL ODOR",
      "GAS LEAK/GAS ODOR",
      "GSW/PENETRATING_D2-G",
      "GSW/PENETRATING_D4-G",
      "HAZMAT",
      "HEADACHE",
      "HEART PROBLEMS",
      "HEAT/COLD EXPOSURE",
      "HEMORRHAGE/LACERATIO",
      "HEMORRHAGE/LACERATION",
      "INACCESSIBLE/ENTRAPMENT",
      "MEDICAL ALARM",
      "MOTOR VEHICLE COLL W/ ENTRAP",
      "MOTOR VEHICLE COLLIS",
      "MOTOR VEHICLE COLLISION",
      "MOTOR VEHICLE COLLISION/INJURY",
      "MUTUAL AID/ASSIST OUTSIDE AGEN",
      "ODOR (STRANGE/UNKNOWN)",
      "OUTSIDE FIRE",
      "OUTSIDE FIRE DUMPSTER/RUBISH",
      "OUTSIDE FIRE WADE",
      "OUTSIDE FIRE WILDLAND",
      "OVERDOSE/POISON_D1",
      "OVERDOSE/POISONING",
      "OVERDOSE/POISONING_D1",
      "PREGNANCY/CHILDBIRTH",
      "PSYCHIATRIC/ABNORMAL BEHAVIOR",
      "SEIZURE_D2",
      "SICK PERSON",
      "SMOKE INVESTIGATION",
      "SMOKE INVESTIGATION OUTSIDE",
      "STAB/GSW/PENETRATING",
      "STAB/GSW/PENETRATING INJURY",
      "STROKE/TIA",
      "STRUCT FIRE HIGH LIFE HAZARD",
      "STRUCT FIRE RESI MUL",
      "STRUCT FIRE RESI MULTI FAMILY",
      "STRUCT FIRE RESI SINGLE FAMILY",
      "STRUCTURE FIRE COMMERCIAL",
      "TRAFFIC/TRANSPORT",
      "TRAFFIC/TRANSPORT IN",
      "TRAFFIC/TRANSPORT INCIDENT",
      "TRAUMATIC INJURY",
      "UNCONSCIOUS/FAINTING",
      "UNKNOWN PROBLEM",
      "VEHICLE FIRE",
      "VEHICLE FIRE COMM/BOX/MOT HOME",
      "WATER RESCUE/SINKING VEHICLE",
      "WATERCRAFT EMERG/COLLISION"
 );
}
