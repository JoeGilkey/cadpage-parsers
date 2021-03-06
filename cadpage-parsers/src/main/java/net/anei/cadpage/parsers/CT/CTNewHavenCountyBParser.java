package net.anei.cadpage.parsers.CT;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

public class CTNewHavenCountyBParser extends FieldProgramParser {
  
  public CTNewHavenCountyBParser() {
    this("NORTH BRANFORD", "CT");
  }
  
  public CTNewHavenCountyBParser(String defCity, String defState) {
    super(CITY_LIST, defCity, defState,
          "ID SELECT/1 ( CALL ADDR2 " +
                      "| CALL PLACE/Z ADDR1/Z APT/Z CITY/Z ZIP " +
                      "| CALL ADDR1/Z APT/Z CITY/Z ZIP " +
                      "| CODE? CALL ADDR1 DUP? APT? CITY ) " +
          "EMPTY+? ( MAP_X UNIT/Z DATETIME! | UNIT/Z DATETIME! | DATETIME! ) INFO/N+");
    setupCallList(CALL_LIST);
    setupMultiWordStreets(MWORD_STREET_LIST);
    setupSpecialStreets(
        "FOX GLEN",
        "NEW HARTFORD TOWN LINE",
        "NORTH WINDHAM LINE"
     );
  }
  
  @Override
  public String getAliasCode() {
    return "CTNewHavenCountyB";
  }
  
  @Override
  public String getFilter() {
    return "FirePaging@hamdenfirefighters.org,paging@branfordfire.com,paging@easthavenfire.com,paging@easthavenpolice.com,paging@mail.nbpolicect.org,paging@nbpolicect.org,noreply@nexgenpss.com,pdpaging@farmington-ct.org,noreply@whpd.com,page@watertownctpd.com,FirePaging@hamdenfirefighters.org,paging@townofstratford.com,ngpager@rockyhillct.gov,pubsafetypaging@uconn.edu,publicsafety@uchc.edu";
  }

  private static final Pattern DELIM1 = Pattern.compile(" *\\| *");
  private static final Pattern MARKER = Pattern.compile("(\\d{10}) +(?:(S\\d{2}) +)?");
  private static final Pattern DATE_TIME_PTN = Pattern.compile(" +(\\d{6}) (\\d\\d:\\d\\d)(?:[ ,]|$)"); 
  private static final Pattern TRUNC_DATE_TIME_PTN = Pattern.compile(" +\\d{6} [\\d:]+$| +\\d{1,6}$"); 
  private static final Pattern ADDR_ST_MARKER = Pattern.compile("(.*) (\\d{5} .*)");
  private static final Pattern I_NN_HWY_PTN = Pattern.compile("\\b(I-?\\d+) +HWY\\b");
  private static final Pattern ADDR_END_MARKER = Pattern.compile("Apt ?#:|(?=(?:Prem )?Map -)", Pattern.CASE_INSENSITIVE);
  private static final Pattern APT_PTN = Pattern.compile("\\S+(?: \\d+)?\\b");
  private static final Pattern MAP_PFX_PTN = Pattern.compile("(?: *(?:Prem )?Map -*)+", Pattern.CASE_INSENSITIVE);
  private static final Pattern MAP_PTN = Pattern.compile("(?:\\d{1,2}(?:[ ,+]+\\d{1,4})?(?:[- ]*[A-Z]{2} *\\d{1,3})?|[- ]*[A-Z]{2} *\\d{1,3}|(?:[CMT]-?\\d+(?:, [A-Z]\\d[A-Z])?\\b[ &]*)+)\\b *");
  private static final Pattern MAP_EXTRA_PTN = Pattern.compile("\\(Prem Map -*(.*?)\\)", Pattern.CASE_INSENSITIVE);
  private static final Pattern CALL_CODE_PTN = Pattern.compile("(.*) - (\\d{1,2}[A-Z]\\d{1,2}[A-Z]?)");
  private static final Pattern CODE_CALL_PTN = Pattern.compile("(\\d{1,2}[A-Z]\\d{1,2}[A-Z]?) - (.*)");
  
  @Override
  public boolean parseMsg(String body, Data data) {
    
    body = stripFieldStart(body, "no subject / ");
    
    body = body.replace('\n', ' ');
    
    // See if this is one of the delimited field formats
    String[] flds = DELIM1.split(body);
    if (flds.length > 4) {
      if (!parseFields(flds, data)) return false;
    }
    
    else {
      Matcher match = MARKER.matcher(body);
      if (!match.lookingAt()) return false;
      setFieldList("ID SRC CODE CALL ADDR APT MAP X CITY UNIT DATE TIME INFO");
      data.strCallId = match.group(1);
      data.strSource = getOptGroup(match.group(2));
      body = body.substring(match.end());
      
      match =  DATE_TIME_PTN.matcher(body);
      if (match.find()) {
        String date = match.group(1);
        data.strDate = date.substring(2,4) + "/" + date.substring(4,6) + "/" + date.substring(0,2);
        data.strTime = match.group(2);
        data.strSupp = body.substring(match.end()).trim();
        body = body.substring(0,match.start());
      } else {
        match = TRUNC_DATE_TIME_PTN.matcher(body);
        if (match.find()) body = body.substring(0,match.start());
      }
      
      // Look for an identifiable end of address marker
      //  Either an Apt or map construct
      String field = null;
      String apt = null;
      match = ADDR_END_MARKER.matcher(body);
      if (match.find()) {
        field = body.substring(match.end()).trim();
        body = body.substring(0,match.start()).trim();
        
        // If this was an app construct, pull out the apartment
        String mark = match.group();
        if (mark.length() > 0) {
          match = ADDR_END_MARKER.matcher(field);
          if (match.find()) {
            apt = field.substring(0,match.start()).trim();
            field = field.substring(match.end()).trim();
          } else {
            match = APT_PTN.matcher(field);
            if (match.lookingAt()) {
              apt = match.group();
              field = field.substring(match.end()).trim();
            }
          }
        }
      }
      
      body = cleanCity(body, data);
      
      // Now start working on the address
      StartType st = StartType.START_CALL;
      match = ADDR_ST_MARKER.matcher(body);
      if (match.matches()) {
        st = StartType.START_ADDR;
        data.strCall = match.group(1).trim();
        body = match.group(2);
      }
      
      // Remove I-nn HWY construct that causes problems
      body = I_NN_HWY_PTN.matcher(body).replaceAll("$1");
      
      // See what we can do with the address
      int flags = FLAG_NO_IMPLIED_APT;
      if (st == StartType.START_CALL) flags |= FLAG_START_FLD_REQ;
      if (field != null) flags |= FLAG_NO_CITY | FLAG_ANCHOR_END;
      else flags |= FLAG_PAD_FIELD;
      parseAddress(st, flags, body, data);
      if (apt != null) data.strApt = append(data.strApt, "-", apt);
      
      // Several different cases to consider
      // Case 1 - We found an address terminator earlier
      // Everything will have to be parsed from the leftover field, including a
      // possible city name
      boolean noCross = false;
      boolean parseCity = false;
      if (field != null) {
        parseCity = true;
      }
      
      // Case 2 - we did not find an address terminator
      else {
        field = getLeft();
        
        // Case 2A - but we found a city
        // In which case we need to parse cross street info from the pad field
        // and the leftover field contains only unit info
        if (data.strCity.length() > 0) {
          
          String pad = getPadField();

          // If pad starts with a left paren, append parenthesised section to address.
          if (pad.startsWith("(")) {
            int pt = pad.lastIndexOf(')');
            if (pt >= 0) {
              data.strAddress = append(data.strAddress, " ", pad.substring(0, pt+1).trim());
              pad = pad.substring(pt+1).trim();
            }
          }

          // What is left is occasionally a city name, but usually a cross street
          if (isCity(pad)) {
            data.strCity = pad;
          } else {
            parseCross(pad, data);
          }
        }
        
        // Case 2A - no city
        // Everything needs to be parsed from leftover field
        // But we know that it does not contain a city name
        else {
          noCross = isMBlankLeft();
        }
      }
      
      // Of the three identified cases, option 2A is the only one that has
      // parsed a city name, and is the only one that does not require us to
      // parse information from the leftover field
      if (data.strCity.length() == 0) {
        
        // Try to parse map information from leftover field
        match = MAP_PFX_PTN.matcher(field);
        if (match.lookingAt()) {
          field = field.substring(match.end());
          noCross = field.startsWith("   ");
          field = field.trim();
          match = MAP_PTN.matcher(field);
          if (match.lookingAt()) {
            data.strMap = stripFieldEnd(match.group().trim(), "&");
            field = field.substring(match.end());
            noCross = field.startsWith("  ");
            field = field.trim();
          }
        }
        
        // Now we have to split what is left into a cross street and unit
        // If there is a premium map marker between them, things get easy
        field = stripFieldStart(field, "/");
        match = MAP_EXTRA_PTN.matcher(field);
        if (match.find()) {
          parseCross(field.substring(0, match.start()).trim(), data);
          field = field.substring(match.end()).trim();
          if (data.strMap.length() == 0) data.strMap = match.group(1).trim();
        }
        
        // If not, our best approach is to looking for the first multiple blank delimiter.
        // which is a heck of a lot easier to do now that double blanks are preserved by
        // the getLeft() method.
        else {
          if (!noCross) {
            int pt = field.indexOf("  ");
            if (pt >= 0) {
              String cross = field.substring(0,pt);
              if (parseCity) {
                parseAddress(StartType.START_OTHER, FLAG_ONLY_CITY | FLAG_ANCHOR_END, cross, data);
                cross = getStart();
              }
              parseCross(cross, data);
              field = field.substring(pt+2).trim();
            }
            
            // If we didn't find one, we will have to use the smart address parser to figure out where
            // the cross street information ends
            else {
              flags = FLAG_ONLY_CROSS;
              if (parseCity) flags |= FLAG_ONLY_CITY;
              Result res = parseAddress(StartType.START_ADDR, flags, field);
              if (res.isValid()) {
                res.getData(data);
                field = res.getLeft();
              }
            }
          }
        }
        
        // If we have not found a city, see if there is one here
        if (parseCity && data.strCity.length() == 0) {
          parseAddress(StartType.START_OTHER, FLAG_ONLY_CITY, field, data);
          if (data.strCity.length() > 0) {
            data.strApt = append(data.strApt, "-", getStart());
            field = getLeft();
          }
        }
      }
      
      // Whatever is left becomes the unit
      data.strUnit = field.replaceAll("  +", " ");
      
      data.strCity = convertCodes(data.strCity, CITY_CODES);
    }
    
    // Clean up call code description
    Matcher match = CODE_CALL_PTN.matcher(data.strCall);
    if (match.matches()) {
      data.strCode = match.group(1);
      data.strCall = match.group(2).trim();
    } else if ((match = CALL_CODE_PTN.matcher(data.strCall)).matches()) {
      data.strCode = match.group(2);
      data.strCall = match.group(1).trim();
    } else {
      int pt = data.strCall.indexOf(" - ");
      if (pt >= 0) {
        String part1 = data.strCall.substring(0, pt).trim();
        String part2 = data.strCall.substring(pt+3).trim();
        if (part1.equals(part2)) data.strCall = part1;
      }
    }

//    if (data.strCode.length() > 0) {
//      String call = CALL_CODES.getCodeDescription(data.strCode, true);
//      if (call != null) data.strCall = call;
//    }
    
    return true;
  }
    
  private static final Pattern CROSS_SLASH_PTN = Pattern.compile(" */ *");

  private void parseCross(String cross, Data data) {
    cross = stripFieldStart(cross, "&");
    cross = stripFieldEnd(cross, "&");
    cross = stripFieldStart(cross, "/");
    cross = stripFieldEnd(cross, "/");
    cross = CROSS_SLASH_PTN.matcher(cross).replaceAll(" / ");
    data.strCross = append(data.strCross, " / ", cross);
  }
  
  @Override
  public String getProgram() {
    return super.getProgram().replace("CALL", "CODE CALL");
  }
  
  private String cleanCity(String addr, Data data) {
    addr = SR_PTN.matcher(addr).replaceAll("SQ");
    Matcher match = CITY_CODE_PTN.matcher(addr);
    if (match.find()) {
      data.strCity = convertCodes(match.group(1), CITY_CODES);
      addr = match.replaceAll(" ").trim();
    }
    return addr;
  }
  private static final Pattern SR_PTN = Pattern.compile("\\bSR\\b");
  private static final Pattern CITY_CODE_PTN = Pattern.compile(" *: *(FARM|UNVL)\\b *");
  
  @Override
  public Field getField(String name) {
    if (name.equals("ID")) return new IdField("\\d{10}");
    if (name.equals("CODE")) return new CodeField("\\d{1,2}[A-Z]\\d{1,2}[A-Z]?");
    if (name.equals("PLACE")) return new MyPlaceField();
    if (name.equals("ADDR1")) return new MyAddress1Field();
    if (name.equals("ADDR2")) return new MyAddress2Field();
    if (name.equals("DUP")) return new MyDupField();
    if (name.equals("APT")) return new MyAptField();
    if (name.equals("ZIP")) return new SkipField("\\d{5}");
    if (name.equals("MAP_X")) return new MyMapCrossField();
    if (name.equals("UNIT")) return new MyUnitField();
    if (name.equals("DATETIME")) return new MyDateTimeField();
    return super.getField(name);
  }
  
  private Pattern MBLANK_PTN = Pattern.compile(" {2,}");
  private Pattern LEAD_ZERO_PTN = Pattern.compile("^0+");
  
  private String cleanAddress(String addr) {
    addr = MBLANK_PTN.matcher(addr).replaceAll(" ");
    addr = LEAD_ZERO_PTN.matcher(addr).replaceFirst("");
    return addr;
  }
  
  private class MyPlaceField extends PlaceField {
    @Override
    public void parse(String field, Data data) {
      field = cleanAddress(field);
      if (field.equals(cleanAddress(getRelativeField(+1)))) return;
      super.parse(field, data);
    }
  }
  
  private class MyAddress1Field extends AddressField {
    @Override
    public void parse(String field, Data data) {
      field = cleanAddress(field);
      super.parse(field, data);;
    }
  }

  private static final Pattern ADDRESS_ZIP_PTN = Pattern.compile("(.*) (\\d{5})");
  private class MyAddress2Field extends AddressField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      field = cleanAddress(field);
      Matcher match = ADDRESS_ZIP_PTN.matcher(field);
      if (!match.matches()) return false;
      field = match.group(1).trim();
      String zip = match.group(2);
      
      parseAddress(StartType.START_ADDR, FLAG_ANCHOR_END, field, data);
      if (data.strCity.length() == 0) data.strCity = zip;
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
    
    @Override
    public String getFieldNames() {
      return "ADDR APT CITY";
    }
  }
  
  private class MyDupField extends SkipField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      return field.equals(getRelativeField(-1));
    }
  }
  
  private static final Pattern APT_PTN2 = Pattern.compile("(?:APT|RM|ROOM|LOT|UNIT)#? *(.*)", Pattern.CASE_INSENSITIVE);
  private class MyAptField extends AptField {
    @Override
    public void parse(String field, Data data) {
      Matcher match = APT_PTN2.matcher(field);
      if (match.matches()) field = match.group(1);
      super.parse(field, data);
    }
  }
  
  private class MyMapCrossField extends Field {
    @Override
    public void parse(String field, Data data) {
      Matcher match = MAP_PFX_PTN.matcher(field);
      if (match.lookingAt()) {
        field = field.substring(match.end()).trim();
        match = MAP_PTN.matcher(field);
        if (match.lookingAt()) {
          data.strMap = stripFieldEnd(match.group().trim(), "&");
          field = field.substring(match.end()).trim();
        }
      }
      
      match = MAP_EXTRA_PTN.matcher(field);
      if (match.find()) {
        field = field.substring(0,match.start()).trim();
        if (data.strMap.length() == 0) data.strMap = match.group(1).trim();
      }
      data.strCross = field;
    }
    
    @Override
    public String getFieldNames() {
      return "MAP X";
    }
  }
  
  private class MyUnitField extends UnitField {
    @Override
    public void parse(String field, Data data) {
      field = MBLANK_PTN.matcher(field).replaceAll(" ");
      super.parse(field, data);
    }
  }
  
  private static final Pattern DATE_TIME_PTN2 = Pattern.compile("(\\d\\d[-/]\\d\\d[-/]\\d{4}) (\\d\\d:\\d\\d(?::\\d\\d)?)\\b.*");
  private class MyDateTimeField extends DateTimeField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      Matcher match = DATE_TIME_PTN2.matcher(field);
      if (!match.matches()) return false;;
      data.strDate = match.group(1).replace('-', '/');
      data.strTime = match.group(2);
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
  }
  
  @Override
  public String adjustMapAddress(String address) {
    address = GILBERT_EXT.matcher(address).replaceAll("GILBERT RD EXT");
    return super.adjustMapAddress(address);
  }
  private static final Pattern GILBERT_EXT = Pattern.compile("\\bGILBERT EXT\\b", Pattern.CASE_INSENSITIVE);

//  private static CodeTable CALL_CODES = new StandardCodeTable();

  private static final String[] MWORD_STREET_LIST = new String[]{
      "ALLINGS CROSSING",
      "APPLE HILL",
      "ASBURY RIDGE",
      "ASHFORD CENTER",
      "AUSTIN RYER",
      "AUTUMN RIDGE",
      "AVALON GATE",
      "BABCOCK HILL",
      "BAHRE CORNER",
      "BATTERSON PARK",
      "BAY PATH",
      "BEACH HILL",
      "BEACON HILL",
      "BEACON POINT",
      "BEAVER HEAD",
      "BEL AIRE",
      "BELLA VISTA",
      "BIDWELL FARM",
      "BIRCH KNOLL",
      "BLACKS HILL",
      "BLISS MEMORIAL",
      "BLUE RIDGE",
      "BOOTH HILL",
      "BOSTON POST",
      "BOULDER RIDGE",
      "BREEZY HILL",
      "BRIAR HILL",
      "BROCKETTS POINT",
      "BRUSHY PLAIN",
      "BUENA VISTA",
      "BUNKER HILL",
      "BURNT HILL",
      "BUSINESS PARK",
      "BUTTON SHOP",
      "CANTON SPRINGS",
      "CAPTAIN THOMAS",
      "CEDAR HILL",
      "CEDAR HOLLOW",
      "CEDAR KNOLLS",
      "CEDAR LAKE",
      "CEDAR SWAMP",
      "CENTURY HILLS",
      "CHARLTON HILL",
      "CHERRY ANN",
      "CHERRY HILL",
      "CIDER MILL",
      "CINNAMON RDG BARNS HILL",
      "CLEAR LAKE",
      "CLINT ELDREDGE",
      "COLD SPRING",
      "COLT HWY",
      "COMMERCE CENTER",
      "COOPER HILL",
      "COPE FARMS",
      "COPPER BEECH",
      "COSEY BEACH",
      "DANIELS FARM",
      "DAY SPRING",
      "DAYTON HILL",
      "DEER POND",
      "DEER RUN",
      "DORAL FARM",
      "DUNBAR HILL",
      "DUNNE WOOD",
      "EAST GATE",
      "EAST HILL",
      "EAST MAIN",
      "EAST ROCK PARK",
      "EAST SHORE",
      "ELI YALE",
      "ELM COMMONS",
      "FANS ROCK",
      "FARM GLEN",
      "FARM RIVER",
      "FARM SPRINGS",
      "FARMINGTON CHASE",
      "FAWN HILL",
      "FELLSMERE FARM",
      "FIELD STONE",
      "FIRST AVE FIRST",
      "FISHER HILL",
      "FLANDERS RIVER",
      "FLAT ROCK",
      "FLYING POINT",
      "FOOTE HILL",
      "FOREST HILLS",
      "FOREST VIEW",
      "FOX HILL",
      "FOX RIDGE",
      "FOXON HILL",
      "FRESH MEADOW",
      "GAYLORD MOUNTAIN",
      "GEO WASHINGTON",
      "GEORGE WASHINGTON",
      "GLEN DEVON",
      "GLEN HAVEN",
      "GLEN RIDGE",
      "GRAHM MANOR",
      "GRAY FOX",
      "GRAY LEDGE",
      "GREAT HILL",
      "GREAT MEADOW",
      "GREAT OAK",
      "GREEN GARDEN",
      "GREEN GLEN",
      "GREEN HILL",
      "HALF ACRE",
      "HALF KING",
      "HALL ACRES",
      "HAMDEN HILLS",
      "HANES HILL",
      "HANG DOG",
      "HANKS HILL",
      "HARBOR VIEW",
      "HART RIDGE",
      "HAYCOCK POINT",
      "HEMLOCK NOTCH",
      "HICKORY HILL",
      "HIDDEN BROOK",
      "HIDDEN OAK",
      "HIGH HILL",
      "HIGH POINT",
      "HIGH RIDGE",
      "HIGH TOP",
      "HIGHWOOD CROSSING",
      "HILLSIDE VIEW",
      "HINMAN MEADOW",
      "HONEY POT",
      "HOOP POLE",
      "HOPE HILL",
      "HORSEBARN HILL",
      "HOTCHKISS GROVE",
      "HUNTER'S RIDGE",
      "HUNTERS CROSSING BARNES HILL",
      "HUNTINGTON OLD GREEN",
      "INDIAN HILL",
      "INDIAN NECK",
      "INDIAN SPRINGS",
      "ISLAND VIEW",
      "JIM CALHOUN",
      "JOHNNYCAKE MTN",
      "JONES HILL",
      "JUNIPER POINT",
      "KATHERINE GAYLORD",
      "KATIE JOE",
      "KAYE VUE",
      "KIDDS CAVE",
      "KILLAMS POINT",
      "KING HILL",
      "KINGS COLLEGE",
      "KNOB HILL",
      "KROL FARM",
      "LAKE GARDA",
      "LANES POND",
      "LANTERN VIEW",
      "LEEDER HILL",
      "LEETES ISLAND",
      "LINDEN POINT",
      "LITTLE BAY",
      "LITTLE OAK",
      "LITTLEBROOK CROSSING",
      "LOCH NESS",
      "LONG HILL CROSS",
      "LONG HILL",
      "MALLARD BROOK",
      "MANSFIELD GROVE",
      "MAPLE RIDGE",
      "MARY BELLE",
      "MEADOW WOOD",
      "MEETING HOUSE HILL",
      "MILL PLAIN",
      "MILL POND HEIGHTS",
      "MILLER FARMS",
      "MOUNT CARMEL",
      "MOUNT PLEASANT",
      "MOUNTAIN SPRING",
      "MOUNTAIN TOP PASS",
      "MOUNTAIN TOP",
      "MOUNTAIN VIEW",
      "MT CARMEL",
      "MTN SPRING",
      "MULBERRY HILL",
      "NORTH ATWATER",
      "NORTH BRANFORD",
      "NORTH EAGLEVILLE",
      "NORTH EAGLVILLE",
      "NORTH HIGH",
      "NORTH HILLSIDE",
      "NORTH LAKE",
      "NORTH MAIN",
      "NORTH MOUNTAIB",
      "NORTH MOUNTAIN",
      "NORTH PETERS",
      "NORTH WINDHAM",
      "NOTCH HILL",
      "O'MEARA FARMS",
      "OAK HILL",
      "OAK HOLLOW",
      "OAK RIDGE",
      "OPENING HILL",
      "ORCHARD HILL",
      "OXEN HILL",
      "PARK POND",
      "PARK RIDGE",
      "PARKER FARMS",
      "PARSONAGE HILL",
      "PAUL SPRING",
      "PAWSON LANDING",
      "PAWSON PARKWAY",
      "PAWSON TRAIL",
      "PEAT MEADOW",
      "PINE HOLLOW",
      "PINE HURST",
      "PINE ORCHARD",
      "PINE ROCK",
      "PINE TREE",
      "PINE VIEW",
      "PINE WOOD",
      "POLLY DAN",
      "POND VIEW",
      "POPLAR HILL",
      "PORTAGE CROSSING",
      "PRATTLING POND",
      "PROSPECT HILL",
      "PUNCH BROOK",
      "PUTTING GREEN",
      "QUARRY DOCK",
      "QUEEN'S PEAK",
      "RED OAK HILL",
      "REEDS GAP",
      "RES LEDGEWOOD",
      "ROBERT FROST",
      "ROCK PASTURE",
      "ROCKY LEDGE",
      "ROLLING WOOD",
      "ROSE HILL",
      "SAGAMORE COVE",
      "SAW MILL",
      "SCHOOL GROUND",
      "SCHOOL HOUSE",
      "SCOTT SWAMP",
      "SEA HILL",
      "SECRET LAKE",
      "SHINGLE MILL",
      "SHORT BEACH",
      "SHORT ROCKS",
      "SILAS DEANE",
      "SILVER SANDS",
      "SILVERMINE ACRES",
      "SIR THOMAS",
      "SKIFF ST EXT",
      "SKY VIEW",
      "SLEEPING GIANT",
      "SOUNDVIEW VIEW",
      "SOUTH EAGLEVILLE",
      "SOUTH END",
      "SOUTH FOREST",
      "SOUTH MAIN",
      "SOUTH MONTOWESE",
      "SOUTH QUAKER",
      "SOUTH RIDGE",
      "SOUTH STRONG",
      "SPENO RDG FRANCE",
      "SPICE BUSH",
      "SPRING COVE",
      "SPRING ROCK",
      "ST MONICA",
      "STONE HILL",
      "STONY CREEK",
      "STONY HILL",
      "SUMMER ISLAND",
      "SUNSET BEACH",
      "SUNSET HILL",
      "SUNSET MANOR",
      "SYBIL CREEK",
      "TALCOTT NOTCH",
      "TEDWIN FARMS",
      "TEN ROD",
      "THE MEWS CENTURY HILLS",
      "THIMBLE FARMS",
      "THIMBLE ISLAND",
      "TIMBER BROOK",
      "TOWERS LOOP",
      "TOWN FARM",
      "TOWN LINE",
      "TOWNE HOUSE",
      "TRAP FALLS",
      "TROUT BROOK",
      "TUNXIS MEAD",
      "TUNXIS VILLAGE",
      "TURTLE BAY",
      "TWIN LAKE",
      "TWIN LAKES",
      "TWO MILE",
      "VALLE VIEW",
      "VALLEY BROOK",
      "VALLEY VIEW",
      "VAN HORN",
      "VICTOR HILL",
      "WALDEN GREEN",
      "WARNER HILL",
      "WASHINGTON MANOR",
      "WATCH HILL",
      "WAVERLY PARK",
      "WESLEY HEIGHTS",
      "WEST AVON",
      "WEST CAMPUS",
      "WEST CHIPPEN HILL",
      "WEST CLARK",
      "WEST DISTRICT",
      "WEST FARMS",
      "WEST GATE",
      "WEST MAIN",
      "WEST MOUNTAIN",
      "WEST SIDE",
      "WEST SLOPE",
      "WEST SPRING",
      "WEST WOODS",
      "WHISPERING HILLS",
      "WHITE HOLLOW",
      "WHITE PLAINS",
      "WINDMILL HILL",
      "WOLCOTT WOODS",
      "WOLF PIT",
      "WOOD ACRES",
      "WOODCHUCK HILL",
      "WOODS HILL",
      "YOUNG'S APPLE ORCHARD"
  };
  
  private static final CodeSet CALL_LIST = new CodeSet(
      "2CO ALARM SOUNDING - NO SYMPTOMS",
      "2FIRE - MISCELLANEOUS",
      "2MEDICAL - MISCELLANEOUS",
      "2MEDICAL - UNRESPONSIVE",
      "ABD PAIN/PROB P-2",
      "ABDOMINAL PAIN - PRI 1 -",
      "ABDOMINAL PAIN - PRI 2 -",
      "ABNORMAL BREATHING DIFFICULTY SPEAKING - ASTHMA",
      "ACCIDENT - MV INJ ALPHA",
      "ACCIDENT - MV INJ BRAVO",
      "ACCIDENT - MV INJ BRAVO RED",
      "ACCIDENT- MV INJ DELTA",
      "ACCIDENT MV W/INJURIES",
      "ACTIVATED FIRE ALARM",
      "AFA",
      "AFA - CHECK ALARM",
      "AFA - COMMERCIAL/INDUSTRIAL",
      "AFA - HIGH LIFE HAZARD",
      "AFA -  MULTI-FAMILY RESIDENTIAL",
      "AFA - RESIDENTIAL",
      "AFA DAY RESPONSE",
      "AFA NIGHT RESPONSE",
      "AFA RESIDENTIAL",
      "ALARM-FIRE",
      "ALARM - FIRE",
      "ALARMS-HOLDUP/PANIC/DURESS",
      "ALLERG/STING",
      "ALPHA MEDICAL",
      "ALS EMS RESPONSE",
      "ANML BITE - SUPRFICIAL BITES - PRI 2 -",
      "ASSAULT - NOT DNGRS",
      "AUTO ACC RESPONSE",
      "AUTOMATIC FIRE ALARM",
      "BACK PAIN - PRI 2",
      "BEHAVORIAL",
      "BEHAVORIAL (UNKNOWN) - PRI 1 -",
      "BLS EMS RESPONSE",
      "BREATH PROB P-1",
      "BREATH PROB- P-1 ABNORMAL",
      "BRUSH FIRE",
      "BRUSH FIRE/CAMP FIRE",
      "BRUSH FIRE TWIN",
      "BUILDING DAMAGE",
      "BUILDING LOCKOUT",
      "C.O. ALARM",
      "CAR FIRE",
      "CAR LOCKOUT",
      "CARBON MONOXIDE",
      "CARDIAC / RESP. ARREST",
      "CHARLIE MEDICAL",
      "CHARLIE MEDICAL TF1",
      "CHARLIE MEDICAL TF3",
      "CHARLIE MEDICAL TF4A",
      "CHECK APPLIANCE",
      "CHECK ELECTRICAL HAZARD",
      "CHECK ELECTRICAL ODOR",
      "CHECK ODOR - INSIDE",
      "CHEST PAIN P-1",
      "CHEST PAIN- P-1 CLAMMY OR COLD SWEATS",
      "CHEST PAIN  - PRIORITY 1 -",
      "CHEST PAIN CLAMMY - PRI. 1 -",
      "CHEST PAIN DIFFICULTY SPEAKING",
      "CHEST PAIN, NOT ALERT - PRI. 1 -",
      "CHEST PAIN, SOB - PRI. 1 -",
      "CO ALARM (NO SYMPTOMS)",
      "CO ALARM NO/UNK MEDICAL SX",
      "CO ALARM NO MEDICAL SX -  MULTI-FAMILY RESIDENTIAL",
      "CO DETECTOR/NO MED SYMPTOMS",
      "CO W/O SYMPTOMS",
      "CODE P-1",
      "COVER ASSIGNMENT (IN CITY)",
      "COVER/RELOCATE TO FIRE HQ",
      "DELTA MEDICAL TF1",
      "DIABETIC",
      "DIABETIC (ALERT)  - PRI 2 -",
      "DIABETIC, AMS - PRI 1 -",
      "DIABETIC, SOB - PRI 1 -",
      "DIFF. BREATHING - PRI 1 -",
      "DIFF. BREATHING, NOT ALERT - PRI 1 -",
      "E-MUTUAL AID AMBULANCE REQUEST",
      "E-MUTUAL AID MEDIC INTERCEPT REQUEST",
      "EFD IN PROGRESS",
      "ELECTRICAL ISSUE - INVEST",
      "ELEVATOR RESCUE",
      "EMD IN PROGRESS",
      "EMS ASSIST",
      "EMS INCIDENT",
      "EPD IN PROGRESS",
      "EYE INJURY (MEDICAL) - PRI 2 -",
      "EYE PROB/INJ- P-2 MOD EYE INJURIES",
      "F-BOAT COLLISION PEOPLE IN WATER - COASTAL",
      "FAINTING >35 W/CARDIAC HX",
      "FAINTING,  ALERT",
      "FAINTING,  ALERT - PRI 2 -",
      "FALL P-1",
      "FALL P-2",
      "FALL- P-2 POSS DANG BODY AREA  STILL DOWN",
      "FALL - PRI 2 -",
      "FALL (NOT ALERT)",
      "FALL, POSS. DANG. AREA- PRI 1 -",
      "FALL, PUB ASST - PRI 2 -",
      "FALL PUBLIC ASSIST(NO INJURY)",
      "FALL, UNKNOWN",
      "FALL, UNKNOWN - PRI 1 -",
      "FIRE - BRUSH FIRE",
      "FIRE - BRUSH / OUTSIDE",
      "FIRE - CO ALARM",
      "FIRE - MV",
      "FIRE - OTHER",
      "FALL - PRI 2 -",
      "FIRE - SMOKE/GAS INVEST INSIDE",
      "FIRE - SMOKE/GAS INVEST OUTSIDE",
      "FIRE - STRUCTURE FIRE",
      "FIRE - VEHICLE",
      "FIRE ALARM",
      "FIRE ALARM  00B12",
      "FIRE ALARM COMMERCIAL",
      "FIRE ALARM COMMERCIAL/WATERFLOW/APARTMENTS",
      "FIRE ALARM-HIGH RISE/TARGET HAZARD",
      "FIRE ALARM-MULTIPLE DEVICES",
      "FIRE ALARM RESIDENTIAL (ONE & TWO FAMILY HOMES)",
      "FIRE APPLIANCE",
      "FIRE DEPARTMENT UNLOCK",
      "FIRE CALL",
      "FIRE OUT REPORT W/ ODOR OF SMOKE",
      "FIRE RESPONSE MUTUAL AID",
      "FIRE STRUCTURE",
      "F-STRUCTURE FIRE - RESIDENTIAL",
      "F-STRUCTURE FIRE - RESIDENTIAL (SINGLE)",
      "F-STRUCTURE FIRE (FIRE OUT)",
      "F-STRUCTURE FIRE APPLIANCE (CONTAINED)",
      "F-STRUCTURE FIRE RESIDENTIAL (SINGLE)",
      "F-STRUCTURE FIRE RESIDENTIAL (MULTI)",
      "F-STRUCTURE FIRE RESIDENTIAL (MULTI)    - ODOR OF SMOKE",
      "F-STRUCTURE FIRE UNKNOWN SITUATION (INVESTIGATION)",
      "FLUID SPILL",
      "FUEL SPILL",
      "FUEL SPILL - INLAND WATER - OUTSIDE",
      "FUEL SPILL - MINOR - OUTSIDE",
      "FUEL SPILL (SMALL LESS THAN 50G)",
      "HAZMAT - ACTIVE CHEMICAL LEAK / SPILL",
      "HAZMAT - ACTIVE GAS LEAK",
      "HAZMAT-CONTAINED- CHEMICAL",
      "HAZMAT - INVESTIGATION",
      "HAZMAT - UNCONTAINED",
      "HEAD ACHE P-1",
      "HEART PROB",
      "HEAVY SMOKE INVESTIGATION OUTSIDE",
      "HEM. / LAC. DIFF. BREATHING - PRI 1 -",
      "HEMORRHAGE THROUGH TUBES",
      "HEMORR/LAC P-1",
      "HEMORR/LAC- P-1 NOT ALERT  MEDICAL",
      "ILLEGAL BURNING",
      "INEFFECTIVE BREATHING - PRI 1 -",
      "INJURY P-1",
      "INTERFACILITY",
      "INVESTIGATE(OTHER NON-EMERGENT)",
      "INVESTIGATION",
      "LIFT ASSIST",
      "LIFT ASSIST (NO FALL)",
      "LOCK IN/OUT - BUILDING",
      "LOCK IN - VEHICLE",
      "LOCKOUT/LOCKIN EMERGENCY",
      "LOCKOUT - VEHICLE",
      "LONG FALL",
      "MARINE RESCUE",
      "MEDICAL ALPHA",
      "MEDICAL BRAVO",
      "MEDICAL CHARLIE",
      "MEDICAL DELTA",
      "MEDICAL ALARM ACTIVATION - PRI 1 -",
      "MEDICAL ALERT ALARM (NO VOICE)",
      "MEDICAL - ALS CALL",
      "MEDICAL-A RESPONSE",
      "MEDICAL-A RESPONSE N I-95  RAMP 43 NORTH/FIRST AVE",
      "MEDICAL - BLS CALL",
      "MEDICAL - BLS CALL 0LLOT",
      "MEDICAL - BLS CALL NORTH",
      "MEDICAL-B RESPONSE",
      "MEDICAL CALL",
      "MEDICAL CALL ALPHA RESPONSE",
      "MEDICAL CALL BRAVO RESPONSE",
      "MEDICAL CALL CHARLIE RESPONSE",
      "MEDICAL CALL DELTA RESPONSE",
      "MEDICAL CALL ECHO RESPONSE",
      "MEDICAL  CANTON",
      "MEDICAL-C RESPONSE",
      "MEDICAL-D RESPONSE",
      "MEDICAL EMERGENCY",
      "MEDICAL MEDA",
      "MEDICAL MEDB",
      "MEDICAL MEDC",
      "MEDICAL MEDD",
      "MEDICAL MEDE",
      "MEDICAL ON UCH PROPERTIES",
      "MEDICAL OTHER LOCATIONS NOT LISTED",
      "MEDICAL SIMSBURY",
      "MOTOR VEHICLE ACCIDENT",
      "MOTOR VEHICLE LOCK OUT",
      "MOUNTAIN / TECHNICAL RESCUE",
      "MUTA - E-MUTUAL AID AMBULANCE REQUEST",
      "MUTP - E-MUTUAL AID MEDIC INTERCEPT REQUEST",
      "MUTUAL AID",
      "MUTUAL AID - FIRE",
      "MUTUAL AID INCIDENT MEDICAL",
      "MUTUAL AID - MEDICAL",
      "MUTUAL AID PARAMEDIC",
      "MUTUAL AID SOUTH",
      "MUTUAL AID STANDBY",
      "MV ACCIDENT WEST",
      "MVA",
      "MVA/ INJURIES REPORTED - RADIO",
      "MVA-ENTRAPMENT-RADIO",
      "MVA (EXTRICATION OR ROLLOVER)",
      "MVA - INJURY",
      "MVA - NO INJURY",
      "MVA-ROLLOVER - RADIO",
      "MVA W/ INJURIES",
      "MVA W/INJURIES",
      "MVA WITH INJURIES",
      "MVA WITHOUT INJURIES",
      "MV CRASH-TRAFFIC CRASH (NO INJURY)",
      "MV CRASH-TRAFFIC CRASH (NO INJURY)-B",
      "MV CRASH-TRAFFIC CRASH (WITH INJURY)",
      "MV CRASH-TRAFFIC CRASH (WITH INJURY)-B",
      "NATURAL GAS LEAK",
      "NATURAL / LP GAS -  ODOR - MULTI-FAMILY RESIDENTIAL",
      "NATURAL / LP GAS - ODOR OUTSIDE",
      "NATURAL / LP GAS -  ODOR - RESIDENTIAL",
      "NATURAL/LP GAS - LEAK/ODOR - COMMERCIAL/INDUST BLDG",
      "OD/INGEST P-1",
      "ODOR OF NATURAL GAS",
      "ODOR OF SMOKE INDOORS",
      "ODOR OF SMOKE OUTDOORS",
      "OMEGA MEDICAL",
      "OUTSIDE FIRE",
      "OUTSIDE FIRE - EXTINGUISHED",
      "OUTSIDE FIRE - INVEST -UNKNOWN",
      "OVERDOSE UNCON.",
      "PERSON DOWN P-2",
      "PERSON STUCK IN ELEVATOR",
      "POSS HEART - PRI 1 -",
      "POSS HEART, SOB",
      "POST CHOKING",
      "POST SEIZURE",
      "PROPANE LEAK",
      "PUBLIC ASSIST",
      "PUBLIC ASSISTANCE FD",
      "PUBLIC SERVICE",
      "PUBLIC SERVICE (FIRE)",
      "RESCUE - ELEVATOR ENTRAPMENT",
      "RESIDENTIAL LOCKOUT",
      "RESET FIRE ALARM",
      "SEIZURE P-1",
      "SEIZURE(S)",
      "SERVICE CALL - NON-EMERGENCY",
      "SICK CALL P-1",
      "SICK CALL P-2",
      "SICK CALL- P-2 NO PRIORITY SYMPTOMS",
      "SICK CALL - ABNORMAL B/P - PRI 2 -",
      "SICK CALL, AMS - PRI 1 -",
      "SICK CALL,  COND 2-11 NOT IDENTIFIED",
      "SICK CALL DIFF BREATHING  - PRI 1 -",
      "SICK CALL - DIZZINESS - PRI 2 -",
      "SICK CALL, GEN. WEAKNESS",
      "SICK CALL IMMOBILITY",
      "SICK CALL, NOT ALERT",
      "SICK CALL NOT WELL / ILL",
      "SICK CALL OTHER PAIN",
      "SICK CALL VOMITING - PRI 2 -",
      "SICK PERSON NAUSEA",
      "SICK PERSON NO PRIORITY SYMPTOMS",
      "SMOKE IN A BUILDING",
      "SMOKE INVESTIGATION INSIDE",
      "SMOKE ODOR INVESTIGATION",
      "SPORTS EVENT DETAIL",
      "STATE TASK FORCE 51 WEST",
      "STILL",
      "STILL - ONE ENGINE",
      "STROKE",
      "STROKE - NOT ALERT",
      "STROKE - SPEECH PRBLM - PRI 1 -",
      "STROKE/TIA P-1",
      "STRUCTURE FIRE",
      "Structure Fire COMMERCIAL/  hazmat",
      "STRUCTURE FIRE COMMERCIAL/INDUSTRIAL",
      "STRUCTURE FIRE RESIDENTIAL (MULTI)",
      "Structure Fire Residential (multi)   -Trapped (s)",
      "Structure Fire Residential (single)",
      "STRUCTURE FIRE RESIDENTIAL (SINGLE)",
      "TEST - TEST1234567890",
      "TRAFFIC/TRANS ACCID/ INJURIES",
      "Traffic Stop",
      "TRANSFORM FIRE",
      "TRAUMATIC INJ., NOT DANG.",
      "UNCON. EFF. BREATHING",
      "UNCON/FAINT- P-1 NOT ALERT",
      "UNCON/FAINTING NOT ALERT - PRI 1 -",
      "UNCON/FAINTING, SOB - PRI 1 -",
      "UNKNOWN",
      "UNKNOWN - PT MOVING/TALKING - PRI 2 -",
      "UNKNOWN MEDICAL - PRI 1",
      "VEHICLE FIRE",
      "WATER CONDITION",
      "WATER PROBLEM RESIDENTIAL",
      "WATER RESCUE",
      "WATERCRAFT OR BOATER IN DISTRESS",
      "WELFARE CHECK",
      "WELFARE CHECK - FD",
      "WIRE DOWN",
      "WIRES DOWN",
      "WIRES DOWN/BURNING",
      "WIRES DOWN - CHECK FOR HAZARDS"
  );

  private static final String[] CITY_LIST = new String[]{
      
      // New Haven County
      "BURLINGTON",
      "BRANFORD",
      "BRISTOL",
      "CANTON",
      "EAST HAVEN",
      "FARMINGTON",
      "GUILFORD",
      "HAMDEN",
      "MILFORD",
      "NEW HAVEN",
      "NORTH BRANFORD",
      "NORTH HAVEN",
      "NORTHFORD",
      "UNIONVILLE",
      "WALLINGFORD",
      "WEST HARTFORD",
      "WEST HAVEN",
      "WLFD",
      
      // Fairfield County
      "BRIDGEPORT",  
      "EASTON",
      "MONROE",
      "SHELTON",
      "STRATFORD",
      "TRUMBULL",
      "WESTON",
      
      // Hartford County
      "AVON",
      "BERLIN",
      "BLOOMFIELD",
      "BRISTOL",
      "BROAD BROOK",
      "BURLINGTON",
      "CANTON",
      "EAST GRANBY",
      "EAST HARTFORD",
      "EAST WINDSOR",
      "ENFIELD",
      "FARMINGTON",
      "GLASTONBURY",
      "GRANBY",
      "HARTFORD",
      "HARTLAND",
      "MANCHESTER",
      "MARLBOROUGH",
      "NEW BRITAIN",
      "NEWINGTON",
      "PLAINVILLE",
      "ROCKY HILL",
      "SIMSBURY",
      "SOUTH WINDSOR",
      "SOUTHINGTON",
      "SUFFIELD",
      "UNIONVILLE",
      "WEST HARTFORD",
      "WETHERSFIELD",
      "WINDSOR",
      "WINDSOR LOCKS",
      "WINDSOR LOCKS EAST",
      
      // Middlesex County
      "CROMWELL",
      "DURHAM",
      
      // Litchfield County
      "TORRINGTON",

      "BARKHAMSTED",
      "BETHLEHEM",
      "BRIDGEWATER",
      "CANAAN",
      "COLEBROOK",
      "CORNWALL",
      "GOSHEN",
      "HARWINTON",
          "NORTHWEST HARWINTON",
      "KENT",
          "SOUTH KENT",
      "LITCHFIELD",
          "BANTAM",
      "MORRIS",
      "NEW HARTFORD",
      "NEW MILFORD",
          "GAYLORDSVILLE",
      "NORFOLK",
      "NORTH CANAAN",
      "PLYMOUTH",
          "TERRYVILLE",
      "ROXBURY",
      "SALISBURY",
          "LAKEVILLE",
          "LIME ROCK",
      "SHARON",
      "THOMASTON",
      "WARREN",
      "WASHINGTON",
          "NEW PRESTON",
      "WATERTOWN",
          "OAKVILLE",
      "WINCHESTER",
          "WINSTED",
      "WOODBURY",
          "HOTCHKISSVILLE",
      
      // New London County
      "FRANKLIN",
      "LEBANON",
      
      // Tolland County
      "ANDOVER",
      "BOLTON",
      "COLUMBIA",
      "COVENTRY",
      "ELLINGTON",
      "HEBRON",
      "MANSFIELD",
      "SOMERS",
      "STAFFORD",
      "TOLLAND",
      "UNION",
      "VERNON",
      "WILLINGTON",

      "COVENTRY LAKE",
      "SOUTH COVENTRY",
      "CRYSTAL LAKE",
      "STAFFORD SPRINGS",
      "STORRS",
      "CENTRAL SOMERS",
      "ROCKVILLE",
      "MASHAPAUG",
      
      "WAREHOUSE POINT",

      "UCONN",
      
      // Windham county
      "ASHFORD",
      "BROOKLYN",
          "EAST BROOKLYN",
      "CANTERBURY",
      "CHAPLIN",
      "EASTFORD",
      "HAMPTON",
      "KILLINGLY",
          "DANIELSON",
      "PLAINFIELD",
          "CENTRAL VILLAGE",
          "MOOSUP",
          "PLAINFIELD VILLAGE",
          "WAUREGAN",
      "POMFRET",
      "PUTNAM",
          "PUTNAM DISTRICT",
      "SCOTLAND",
      "STERLING",
          "ONECO",
      "THOMPSON",
          "NORTH GROSVENOR DALE",
          "QUINEBAUG",
      "WINDHAM",
          "NORTH WINDHAM",
          "SOUTH WINDHAM",
          "WILLIMANTIC",
          "WINDHAM CENTER",
      "WOODSTOCK",
          "SOUTH WOODSTOCK"

  };
  
  private static final Properties CITY_CODES = buildCodeTable(new String[]{
      "FARM", "FARMINGTON",
      "UNVL", "UNIONVILLE",
      "WLFD", "WALLINGFORD"
  });

}
