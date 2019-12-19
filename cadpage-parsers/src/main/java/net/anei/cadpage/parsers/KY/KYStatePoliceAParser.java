package net.anei.cadpage.parsers.KY;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.SplitMsgOptions;
import net.anei.cadpage.parsers.SplitMsgOptionsCustom;
import net.anei.cadpage.parsers.dispatch.DispatchB3Parser;



public class KYStatePoliceAParser extends DispatchB3Parser {
  
  private boolean srcFound;
  
  public KYStatePoliceAParser() {
    this("");
  }

  public KYStatePoliceAParser(String defCity) {
    super(CITY_LIST, defCity, "KY", B2_CROSS_FOLLOWS);
    setupCallList(CALL_LIST);
    setupSaintNames("HWY");
    setupMultiWordStreets(MWORD_STREET_LIST);
    setupProtectedNames(
        "EAST ELKHORN LOWER BRANCH",
        "BEAVER CRK",
        "LANE BLAIR",
        "PIKE TECHNICAL SERVICES"
    );
    removeWords("UNKNOWN", "STREET", "-");
    allowBadChars("()");
  }
  
  @Override
  public String getAliasCode() {
    return "KYStatePoliceA";
  }

  private static final Pattern REJECT_PREFIX_PTN = Pattern.compile("(?:CARROLLCOUNTY911|HARRISON_COUNTY_911|PIKEVILLE9-1-1):");
  private static final Pattern PREFIX_PTN = Pattern.compile("(?:KSP CAMPBELLSBURG|KSP DRY RIDGE E-911|KSP FRANKFORT|KSP POST 9|911-CENTER):");
  
  @Override
  public String getFilter() {
    return "KSPCAMPBELLSBURG@P05.gov,KSPCAMPBELLSBURG@P05.org,KSPDRYRIDGEE-911@P06.gov,911-CENTER@P13.gov,KSPPOST9@P09.gov";
  }
  
  @Override
  public SplitMsgOptions getActive911SplitMsgOptions() {
    return new SplitMsgOptionsCustom(){
      @Override public boolean splitBlankIns() { return false; }
    };
  }

  @Override
  protected int getExtraParseAddressFlags() {
    return FLAG_CHECK_STATUS | FLAG_AT_PLACE;
  }
  
  private boolean subPrefix;
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    
    // Reject alerts with the wrong prefix
    if (REJECT_PREFIX_PTN.matcher(body).lookingAt()) return false;
    
    Matcher match = PREFIX_PTN.matcher(subject);
    subPrefix = srcFound = match.lookingAt();
    if (srcFound) subject = subject.substring(match.end());
    if (!srcFound) {
      match = PREFIX_PTN.matcher(body);
      srcFound = match.lookingAt();
      if (srcFound) body = body.substring(match.end());
    }
    body = body.replace('\n', ' ');
    body = body.replace(" FIXED ATTMO 8006356840 OPT4 ", " ");
    if (!super.parseMsg(subject, body, data)) return false;
    if (data.strCity.endsWith(" CO")) data.strCity += "UNTY";
    data.strCity = convertCodes(data.strCity, FIX_CITY_TABLE);
    String st = CITY_ST_TABLE.getProperty(data.strCity);
    if (st != null) data.strState = st;
    return true;
  }
  
  @Override
  public String getProgram() {
    return super.getProgram().replace("CITY", "CITY ST");
  }

  @Override
  protected boolean isPageMsg(String body) {
    if (srcFound) return true;
    if (body.contains(" Cad:")) return true;
    return super.isPageMsg(body);
  }

  @Override
  protected boolean parseAddrField(String field, Data data) {
    if (!subPrefix) field = field.replace('@', '&');
    return super.parseAddrField(field, data);
  }
  
  @Override
  public String adjustMapCity(String city) {
    String tmp = MAP_CITY_TABLE.getProperty(city.toUpperCase());
    if (tmp != null) return tmp;
    return city;
  }
  
  private static final String[] MWORD_STREET_LIST = new String[]{
      "3 C",
      "A L FARMER",
      "ABBOTT CREEK",
      "ABNER FORK",
      "ABNER MOUNTAIN",
      "AETNA FURNACE",
      "ALBERT MOORE",
      "ALDO ANNIE",
      "ALLEN BANNER",
      "ALLEN K",
      "AMOS NEWSOME",
      "ARKANSAS CREEK",
      "ARNOLD FORK",
      "ARNOLDS CREEK",
      "ARROWHEAD ESTATES",
      "ASH BEE TREE",
      "ASSEMBLY CHURCH",
      "ASTOR FIELDS",
      "BACK ALLEY",
      "BACON CREEK",
      "BAKER FORK",
      "BAKER WILLIAMS",
      "BALD KNOB",
      "BALL PARK",
      "BANTA'S FORK",
      "BARNES SCHOOL",
      "BART SMITH",
      "BATON ROUGE",
      "BEAR FORK",
      "BEAVER CREEK",
      "BEAVER JUNCTION",
      "BEAVER VALLEY",
      "BEECH GROVE",
      "BELLS RIDGE",
      "BENT BRANCH",
      "BENT MOUNTAIN",
      "BERT HALL",
      "BETHEL RIDGE",
      "BETTY LOU",
      "BIG BEAR",
      "BIG BLUE SPRINGS",
      "BIG BLUE",
      "BIG BRANCH",
      "BIG CARD",
      "BIG CREEK",
      "BIG TWIN CREEK",
      "BIG WINDY",
      "BISHOP RIDGE",
      "BLACK GEM",
      "BLACK HAWK",
      "BLANCHE DAVIS",
      "BOB BISHOP",
      "BOB GREEN",
      "BOB TURNER",
      "BOILING SPRINGS",
      "BOLDMAN TOWER",
      "BOLTON SCHOOL",
      "BONNIE VIEW",
      "BOWLING GREEN",
      "BOWLINGS CREEK",
      "BRANHAMS CREEK",
      "BRAY RIDGE",
      "BREEDING CREEK",
      "BRUSH CREEK",
      "BRYAN GRIFFIN",
      "BRYAN STATION",
      "BUCKNER HOLLOW SOUTH",
      "BUCKS RUN",
      "BULLIT HILL",
      "BULLITT HILL",
      "BUNNELL CROSSING",
      "BURIAL SHEPHERED",
      "BURKHARDT BOTTOM",
      "BURL SHEPHERD",
      "BURNING FORK",
      "BUTCHER KNIFE",
      "BUTLER INN",
      "BUTTON RIDGE",
      "C NELSON",
      "CABANA SHORES",
      "CABIN CREEK",
      "CALDWELL NORTH",
      "CAMP BRANCH",
      "CAMP CREEK",
      "CANADA TOWER",
      "CANE FORK",
      "CANE RUN",
      "CANEY CREEK",
      "CANEY FORK",
      "CARBON GLOW",
      "CARD MOUNTAIN",
      "CARMON CREEK",
      "CARR CREEK DAM",
      "CARROLL ANN",
      "CATHOLIC HILL",
      "CECIL HALL",
      "CEDAR BLUFF",
      "CEDAR CREEK",
      "CEDAR HILLS",
      "CEDAR MORE",
      "CEDAR RUN",
      "CEMETERY HILL",
      "CENTER CREEK",
      "CHAPMAN FORK",
      "CHARLES GIVEN",
      "CHARLES JAGGERS",
      "CHARLIE BUTTS",
      "CHARLIE MORAN",
      "CHARLIE PURVIS",
      "CHEROKEE CREEK",
      "CHERRY HILL",
      "CHERRY SPRING",
      "CHESTNUT GROVE",
      "CHESTNUT OAK",
      "CHIPMAN RIDGE",
      "CHURCH HOUSE",
      "CHURCH OF CHRIST",
      "CLARKS CREEK",
      "CLAXON RIDGE",
      "CLEAR CREEK",
      "CLEAR FORK",
      "CLEO DAVIS",
      "CLICK BRANCH",
      "COCKRELL FORK",
      "COCKRELLS TRACE",
      "COLES FORK",
      "COMBS BRANCH",
      "COOPERS BOTTOM",
      "CORINTH SHORE",
      "CORN CREEK",
      "COUNTRY CLUB",
      "COW CREEK",
      "CRACKER BOTTOM",
      "CRAFT COLLY",
      "CRESCENT RIDGE",
      "CRIPPLE CREEK",
      "CRITTENDEN MT ZION",
      "CROSS MAIN",
      "CROSS ROAD",
      "CUB RUN",
      "CULL PEPPER",
      "CULLS RIDGE",
      "DANIELS CREEK",
      "DAVIS LAKE",
      "DAWSON BRANCH",
      "DAYS INN",
      "DELLA RILEY",
      "DINAH BLAIR",
      "DIRTY TURTLE OFF",
      "DIVIDE RIDGE",
      "DIVIDED RIDGE",
      "DOE RIDGE",
      "DOG WOOD",
      "DON FREEMAN",
      "DORTON HILL",
      "DORTON JENKINS",
      "DREW VILLAGE",
      "DRY CREEK",
      "DRY RIDGE MT ZION",
      "DRY RIDGE",
      "DUERS MILL",
      "DUNN MAZIE",
      "EAGLE CREEK CIRCLE",
      "EAGLE CREEK",
      "EAGLE HILL",
      "EAGLE POINT",
      "EAGLE RIDGE",
      "EAGLE TUNNEL",
      "ED MURRAY",
      "EDDY CRK",
      "EDEN SHALE",
      "ELDEEN STUMP",
      "ELK LAKE RESORT",
      "ELKHORN CREEK",
      "ELLEN KAY",
      "ELLISTON MT ZION",
      "ELM GROVE",
      "ELSWICK FORK",
      "FAIR BOURNE",
      "FAIRVIEW CHURCH PASCAL",
      "FALCON MOTEL",
      "FALLEN TIMBER",
      "FALLING SPRINGS CHURCH",
      "FALLING SPRINGS",
      "FALLIS GEST",
      "FASHION RIDGE",
      "FATHER RINEY",
      "FERRELLS CREEK",
      "FIGETT BEND",
      "FILTER PLANT",
      "FIRE TRAIL",
      "FIRST HILL",
      "FISHER RIDGE",
      "FIVE LICK",
      "FLAT CREEK",
      "FLAT ROCK",
      "FLATWOODS TOWER",
      "FLETCHER FORK",
      "FLINT RIDGE",
      "FLOUR CREEK",
      "FOLSOM JONESVILLE",
      "FORD MOUNTAIN",
      "FORDS BRANCH",
      "FOREST GLEN",
      "FOREST HILL",
      "FOREST HILLS",
      "FORTNER RIDGE",
      "FOX CREEK",
      "FOX LAIR",
      "FOX RUN",
      "FRANK ADKINS",
      "FREEL TACKETT",
      "FRENCHMANS KNOB",
      "FROZEN MILL",
      "GADDIE CEMETERY",
      "GAINES VILLAGE",
      "GAP HILL",
      "GARNIE HALL",
      "GAS FORK",
      "GAULT TERRY",
      "GEORGE MASON",
      "GEORGE THOMAS",
      "GIBSON SUTTON",
      "GILEAD FAIRVIEW",
      "GOBBLERS RUN",
      "GOLD CITY",
      "GOLDS VALLEY SPUR",
      "GOLDS VALLEY",
      "GOLF COURSE",
      "GOOSE CREEK",
      "GRATZ CEMETARY",
      "GREEN MEADOW",
      "GREEN MEADOWS",
      "GREEN PERRY CEMT",
      "GREEN SOUTH",
      "GROUND HOG",
      "HACK BROWN",
      "HADDIX DEPOT",
      "HALES BRANCH",
      "HAMMOND SCHOOL",
      "HAPPY HOLLOW",
      "HARDSHELL CANEY",
      "HARDY CREEK",
      "HARPERS FERRY",
      "HARRIS RIDGE",
      "HARRISON NEWSOME",
      "HARVE VARNEY",
      "HATCHER VALLEY",
      "HAWKS NEST",
      "HENRY CLAY",
      "HENRY COUNTY",
      "HENRY HUTTON",
      "HERMAN GREEN",
      "HERMAN GREENE",
      "HICKORY HILL",
      "HICKS BRANCH",
      "HIGH RIDGE",
      "HILL SPRING",
      "HILLMAN FERRY",
      "HILLTOP RIDGE",
      "HINDMAN HILLS",
      "HIRAM BAILEY",
      "HOGG RIDGE",
      "HOLBROOK HOLLOW",
      "HOLLOW TREE",
      "HOLLY HILLS",
      "HOME RUN",
      "HONEY FORK",
      "HOPEWELL CHURCH",
      "HORSESHOE BEND",
      "HOWARD LEACH",
      "HUMES RIDGE",
      "HUNDRED ACRE POND",
      "HUNDRED ACRE",
      "HUNTING CREEK",
      "HUNTS BRANCH",
      "HYLTON CHURCH",
      "I H GILL",
      "INDIAN CREEK",
      "INDIAN HILL",
      "INDIAN HILLS",
      "INDUSTRAIL PARK",
      "INDUSTRIAL PARK",
      "IRON HILL",
      "ISLAND CREEK",
      "IUKA FERRY",
      "J C JONES",
      "J HARPER",
      "J L BORDERS",
      "J L FARMER",
      "J N LEE",
      "JACK MCGUIRE",
      "JACK THOMASON",
      "JACK WEST",
      "JACKSON BRANCH",
      "JESSE BRANCH",
      "JIM BANKS",
      "JIMMIES CREEK",
      "JOE MCCOY",
      "JOES BRANCH",
      "JOES CREEK",
      "JOES KNOB TOWER",
      "JOHN J JOHNSON",
      "JOHN LOGSDON",
      "JOHN M STUMBO",
      "JOHN MARTIN",
      "JOHNS CREEK",
      "JOHNS FARM",
      "JOHNSON BOTTOM",
      "JOKER PHILLIPS",
      "JONES HILL",
      "JOYCE RIDGE",
      "JULIUS MILLS",
      "KAYS BRANCH",
      "KEEFER LAWRENCEVILLE",
      "KELLY FORK",
      "KELLY MOUNTAIN",
      "KELLY SPENCER",
      "KENNY PERRY",
      "KETTLECAMP BRANCH",
      "KIMPER TOWER",
      "KING KELLY COLEMAN",
      "KNOB FORK",
      "KNOXVILLE GARDNERSVILLE",
      "L H S",
      "LAKE ACCESS",
      "LAKE BARKLEY",
      "LAKE JERICHO",
      "LAKE SHORE",
      "LAKE SPRINGS",
      "LATON TURNER",
      "LAUREL FORK",
      "LAZY K",
      "LEA VIEW",
      "LEANING OAK",
      "LECOMPTE BOTTOM",
      "LECOMPTES BOTTOM",
      "LEFT BEAVER CREEK",
      "LEFT FORK BLACKBERRY",
      "LEFT FORK HAPPY",
      "LEMON LIME",
      "LEMON NORTHCUTT",
      "LETCHER CO",
      "LEXINGTON TRAILS",
      "LHER- RIVERS",
      "LIBERTY LAWSON",
      "LIBERTY SPARTA",
      "LICK BRANCH TALBERT",
      "LICK BRANCH-ARMORY",
      "LICK CREEK TOWER",
      "LICK CREEK",
      "LICK MOUNTAIN",
      "LINCOLN RIDGE",
      "LITTLE BLUE SPRINGS",
      "LITTLE FORK",
      "LITTLE MOUNT CHURCH",
      "LITTLE MOUNT",
      "LITTLE OAK",
      "LOCUST GROVE",
      "LOGSDON VALLEY",
      "LONDON PACE SINK",
      "LONE OAK",
      "LONE STAR",
      "LONG BRANCH FORK",
      "LONG BRANCH",
      "LONG FORK",
      "LONGS CREEK",
      "LOTTS CREEK",
      "LOUIS GARDNER",
      "LUKE VARNEY",
      "LUSBY MILL",
      "LUTHER HATFIELD",
      "MACON KESSINGER",
      "MADDOX RIDGE",
      "MAE BRANCH",
      "MARROWBONE CREEK",
      "MARTIN BRANCH",
      "MARTIN PIERCE",
      "MASON CORDOVA",
      "MASON ESTATES",
      "MASON SIPPLE",
      "MAY VILLAGE",
      "MCDOWELLS BRANCH",
      "MCKENDREE CHAPEL",
      "MEADOW BRANCH",
      "MEADOW BROOK",
      "MEADOW CREEK",
      "MEADOW LARK",
      "MEDICAL CENTER",
      "MEDICAL PLAZA",
      "MIDDLE FORK",
      "MILL BEND",
      "MILL CREEK",
      "MILLARD CHURCH",
      "MILLER POND",
      "MILLERS BRANCH ELKATAWA",
      "MILTON BEDFORD",
      "MINI FARMS",
      "MONTGOMERY CREEK",
      "MOON BAY",
      "MORGAN CREEK",
      "MORTON RIDGE",
      "MOSSY BOTTOM",
      "MOUNT CARMEL",
      "MOUNT PLEASANT",
      "MOUNT RAIDER",
      "MOUNT VERNON",
      "MOUNTAIN BRANCH",
      "MOUNTAIN BREEZE",
      "MOUNTAIN ISLAND",
      "MOUNTIAN ISLAND",
      "MT CARMEL",
      "MT OLIVET",
      "MT PLEASANT",
      "MT VERNON",
      "MT WASHINGTON",
      "MULLINS BRANCH",
      "MURPHY BOTTOM",
      "NAPOLEON ZION STATION",
      "NEALY CREEK",
      "NIM SMITH",
      "NORMANDY RD",
      "NORTH MAYO",
      "NORTH POINT",
      "NORTH SECOND RIVER",
      "NORTHTOWN CHURCH",
      "NOTCH LICK",
      "NUBBIN RIDGE",
      "NURSING HOME",
      "OAK RIDGE",
      "OGDEN RIDGE",
      "OIL FIELD",
      "ORGAN CREEK",
      "PARK RIDGE",
      "PATTON BRANCH",
      "PATTONS CREEK",
      "PAUL FULLER",
      "PAYNES RUN",
      "PEACEFUL HOLLOW",
      "PEACH TREE",
      "PEBBLE CREEK",
      "PEDEN MILL",
      "PERKINS MADDEN",
      "PERRY SPRINGS",
      "PERRYS TOWN",
      "PETER FORK",
      "PETERS BRANCH",
      "PHELPS 632",
      "PHELPS TOWER",
      "PHIL HAWKINS",
      "PHILLIPS BRANCH",
      "PIGEON ROOST",
      "PILOT KNOB",
      "PINE RIDGE",
      "PLEASANT GROVE",
      "PLEASANT VALLEY",
      "PLUM RIDGE",
      "POINT OF ROCK",
      "POINT PLEASANT CEMETARY",
      "POINT PLEASANT",
      "POMPEY HILL",
      "POND CREEK",
      "POOR BOTTOM",
      "POPLAR CREEK",
      "PORT AUTHORITY",
      "PORT ROYAL",
      "PORT WOODEN",
      "POSSUM TROT",
      "POTTERS GROCERY",
      "POUNDING MILL",
      "POWELLS CREEK",
      "POWERSVILLE HARRISON CO",
      "PRIZOR POINT",
      "PROFESSIONAL PARK",
      "QUAIL RIDGE",
      "QUILLEN SHEPHERD",
      "RABBIT HUNTER",
      "RAILROAD HILL",
      "RATLIFF CREEK",
      "RATLIFF SCHOOL",
      "RED BIRD",
      "RED BUCK ESTES",
      "RED CREEK",
      "RED DOG",
      "RED FOX",
      "RED ISON",
      "RED OAK",
      "REEDER SCHOOL",
      "REGINA BELCHER",
      "RICHMOND HILL",
      "RIDGE LINE",
      "RIGHT FORK CANEY CREEK",
      "RIGHT FORK OF BRUSHY",
      "RIGHT FORK PETER FORK",
      "RIGHT FORK ROCKHOUSE",
      "RIGHT FORK SYCAMORE",
      "RIGHT FORK UPPER POMPEY",
      "RIGHT GREASY CAMP",
      "RIGHT TINKER FORK",
      "RILEY BRANCH",
      "RISING SON",
      "RIVER COURT",
      "RIVER FRONT",
      "RIVERDALE TRAILER",
      "ROAD CREEK",
      "ROBERT BRANSTETTER",
      "ROBERT STINSON",
      "ROBINSON CREEK",
      "ROCK HOUSE LOOP",
      "ROCKY HILL",
      "ROLLING ROAD",
      "ROWLETTS CAVE SPRINGS",
      "ROY HUNTER",
      "RUNNELS BRANCH",
      "RUSSELL BRANCH",
      "RYE STRAW",
      "SALISBURY BRANCH",
      "SALT WELL",
      "SAM GOODMAN",
      "SANDLICK BRANCH",
      "SANDSTONE RIDGE",
      "SASSAFRAS CREEK",
      "SAYLOR POINT",
      "SCHOOL HOLLOW",
      "SECOND HILL",
      "SEWER PLANT",
      "SHADY LAND CHURCH",
      "SHADY LANE CHURCH",
      "SHEPHERD BRANCH",
      "SHERMAN MT ZION",
      "SHERMAN NEWTOWN",
      "SHOP FORK",
      "SHORT FORK",
      "SIMPSON RIDGE",
      "SIMS CEMETERY",
      "SIX MILE CREEK",
      "SMILEY FORK",
      "SMITH FORK",
      "SOUTH FORK",
      "SOUTH JOHNSON BOTTOM",
      "SOUTH RICE",
      "SOUTH RIVER",
      "SOUTH SIDE",
      "SOUTH WILSON",
      "SOUTHSIDE MALL",
      "SPRING HILL",
      "SPRING OAK",
      "SPRINGPORT FERRY",
      "SPRUCE PINE",
      "ST ESTES",
      "STAGGER FORK",
      "STEEL FORK",
      "STEER FORK",
      "STEPHENS HILL",
      "STEVENS BRANCH",
      "STEWART RIDGE",
      "STILL HOUSE",
      "STOKER BRANCH",
      "STONE BROOK",
      "STONE COAL",
      "SULPHUR BEDFORD",
      "SULPHUR GAP",
      "SUNNYSIDE GOTTS",
      "SUNSET VIEW",
      "SUSAN COOK",
      "SUTTON BOTTOM LEFT",
      "SUTTON BOTTOM RIGHT",
      "SWEET OWEN",
      "SWOPE NATLEE E",
      "TACKETT FORK",
      "TAR RIDGE",
      "TAYLOR FARM",
      "TEN MILE",
      "TERRY BASTIN",
      "THOMAS CIRCLE",
      "THORN HILL",
      "THREE MILE",
      "THREE SPRINGS",
      "THREE WAY",
      "TOLER CREEK",
      "TOM ADKINS",
      "TOM BIGGS",
      "TOMMY NELSON",
      "TOMMY REED",
      "TOOHEY RIDGE",
      "TOP QUALITY",
      "TOWN BRANCH",
      "TOWN HILL",
      "TRACE CANEY",
      "TROUT RIDGE",
      "TUG FORK",
      "TURKEY CREEK",
      "TURKEY PEN",
      "TURNER STATION",
      "TURNERS STATION",
      "TURTLE BRANCH",
      "TWIN OAK",
      "TWO MILE",
      "TYREE CHAPEL",
      "VERONA MT ZION",
      "VILLAGE VIEW",
      "WAGNER STATION",
      "WALNUT GROVE SCHOOL",
      "WALNUT HILL",
      "WATER PLANT",
      "WEDDINGTON BRANCH",
      "WEST 5TH",
      "WEST FORK",
      "WHEELER HORTON",
      "WHITE CHAPEL",
      "WHITE OAK",
      "WHITE PINE",
      "WILLIAM EVERAGE",
      "WILLIAM FORD",
      "WILLIE BEVINS",
      "WILLOW NEAVE",
      "WILLOW TREE",
      "WINDMILL RESTAURANT",
      "WISES LANDING",
      "WOLF PEN",
      "WOLFPEN BRANCH",
      "WOLFPEN CREEK",
      "WOLFPIT BRANCH",
      "WOODED HILLS",
      "WOODED RIDGE",
      "WOODMAN CREEK",
      "WRIGHTS HOLW",
      "YELLOW HILL",
      "ZION HILL"
  };
  
  private static final CodeSet CALL_LIST = new CodeSet(
      "911 HANGUP OR UNVERIFIED",
      "ABANDONDED VEHICLE",
      "ABDOMINAL PAINS",
      "ACCIDENT PROPERTY DAMAGE",
      "ACCIDENT WITH INJURIES",
      "AIRCRAFT ACCIDENT",
      "ALARMS",
      "ALL EMS/MEDICAL CALLS",
      "ALL EMS TRANSPORTS",
      "ANIMAL BITE",
      "ANIMAL COMPLAINT",
      "ARREST BLOTTER",
      "ARSON",
      "ASSAULT",
      "ATTEMPT TO LOCATE/CONTACT",
      "ATV ON HIGHWAY",
      "BE OUT AT / 10-6",
      "BACK PAIN",
      "BOATER ASSISTS",
      "BOATING COMPLAINTS/VIOLATIONS",
      "BURGLAR ALARM",
      "BURGLARY",
      "CAD ERRORS/PROBLEMS",
      "CHILD PORNOGRAPHY",
      "CHILD RESTRAINT VIOLATIONS",
      "CIVIL COMPLAINT",
      "CIVIL COMPLAINTS",
      "CO2 ALARMS",
      "CODE ENFORCMENT",
      "COLLAPSE",
      "COMPLIANCE CHECKS",
      "CONTROLLED BURN",
      "COUNTERFEIT MONEY",
      "CRIMINAL ABUSE/CHILD",
      "CRIMINAL INVESTIGATION",
      "CRIMINAL MISCHIEF",
      "CUSTODY DISPUTE",
      "DATA DRIVEN ENFORCEMENT PROGRA",
      "DEATH",
      "DIABETIC",
      "DIFFICULTY BREATHING",
      "DISTURBANCE",
      "DOMESTIC ABUSE",
      "DRIVE OFFS",
      "DROWNING INCIDENTS",
      "DRUG INFO",
      "DRUG OVERDOSE",
      "DUI COMPLAINT",
      "EMERGENCY MEDICAL TRANSPORT",
      "EMERGENCY RELAY",
      "ESCAPE",
      "ESCORT",
      "EXPLOSION",
      "EPO/DVO VIOLATION",
      "EXTRA PATROL REQUEST",
      "F&W MISC",
      "F&W PATROL",
      "FATAL TRAFFIC ACCIDENT",
      "FATAL TFC ACCIDENTS",
      "FIGHT",
      "FIRE",
      "FIRE ALARM",
      "FIRE ALARMS",
      "FIRE - GENERAL USE",
      "FIRE STRUCTURE HOUSE OR BUSINE",
      "FIRE STRUCTURE HOUSE OR BUSN",
      "FIREWORKS COMPLAINT",
      "FORGERY",
      "FOR INFORMATIONAL PURPOSES",
      "FRAUD INCIDENTS",
      "FUEL SPILL",
      "FW BOATING ACCIDENT",
      "GAS LEAK",
      "GUNSHOT",
      "HARASSMENT",
      "HAZARDOUS MATERIALS INCIDENT",
      "HOSTAGE SITUATION",
      "IDENTITY THEFT",
      "ILLEGAL BURNING",
      "ILLEGAL BURNING INCIDENTS",
      "ILLEGAL GAMBLING",
      "INFORMATION ONLY",
      "INFORMATIONAL CALL",
      "INTOXICATED PERSON",
      "INVESTIGATION FOLLOW UP",
      "INVESTIGATION FOLLOW-UP",
      "JUVENILE BEYOND CONTROL",
      "LIFTING ASSISTANCE",
      "LOCKED OUT (CAR & HOME)",
      "LOCKED OUT (HOME & VEHICLE)",
      "LOITERING",
      "LOUD PARTY/MUSIC COMPLAINT",
      "MEDICAL ALARMS/ALERTS",
      "MEDICAL ALERT/ALARMS",
      "MEDICAL ALERTS/ALARMS",
      "MENTALLY ILL PERSON",
      "METH LABS",
      "MISCELLANEOUS COMPLAINT",
      "MISCEALLANEOUS EMS CALLS",
      "MISCELLANEOUS EMS CALLS",
      "MISCELLANEOUS TRAFFIC COMPLNTS",
      "MISCELLANEOUS TRAFFIC COMPS",
      "MISSING PERSON",
      "MOTORIST ASSIST",
      "NO OPERATORS LICENSE",
      "NONEMERGENCY MEDICAL TRANSPORT",
      "OFFICER ASSIST",
      "PEDESTRIAN RELATED COMPLAINTS",
      "PROCESS SERVICE",
      "PROCESS SVC (EXCEPT WARRANT)",
      "PROPERTY DAMAGE ACCIDENT",
      "PROPERTY DISPUTE",
      "PROWLER",
      "PURSUIT",
      "RAPE",
      "RECKLESS DRIVER",
      "RECKLESS DRIVING",
      "RECOVERED/FOUND/LOST PROPERTY",
      "REPOSSESSION",
      "REQUEST AMBULANCE",
      "RUN REPORT",
      "RUNAWAY",
      "SCHOOL VISITS",
      "SECURITY/SURVEILLANCE DETAILS",
      "SEVERE WEATHER REPORTS/DAM",
      "SEX OFFEND REG FAIL TO COMPLY",
      "SEXUAL ABUSE CHILD",
      "SHOOTING",
      "SHOOTING INCIDENTS",
      "SHOPLIFTING",
      "SHOTS FIRED/HEARD",
      "SOCIAL SERVICE REFERRALS",
      "SPEEDING COMPLAINT",
      "STALKING COMPLAINT",
      "SUICIDE ATTEMPT OR THREAT",
      "SURVEILLANCE/SPEC DETAILS",
      "SUSPICIOUS INCIDENT",
      "SUSPICIOUS PERSON",
      "SUSPICIOUS VEHICLE",
      "SUSP INDIVIDUAL ON SCENE (CLI)",
      "TERRORISTIC THREATENING",
      "THEFT COMPLAINT",
      "THEFT OF MEDICATION",
      "TRAFFIC CHECKPOINT",
      "TRAFFIC HAZARD",
      "TRAFFIC STOP",
      "TRAFFIC STOP (CLI)",
      "TRAIN ACCIDENT",
      "TRESPASSING",
      "TROUBLE CALL",
      "UNAUTHORIZED USE OF VEH",
      "UNCONSCIOUS",
      "UTILITY TROUBLE/EMERGENCIES",
      "WANTED PERSON",
      "WARRANT SERVICE",
      "WATER RESCUE",
      "WELFARE CHECK",
      "WOODLAND FIRE INCIDENTS",
      "WOODLAND/WILDFIRE INCIDENTS",
      "WORK ZONE ENFORCEMENT DETAILS"
  );

  private static final String[] CITY_LIST = new String[]{
    
    // Barren County
    "BARREN",
    "BARREN CO",
    "BARREN COUNTY",
    "CAVE CITY",
    "GLASGOW",
    
    // Boone County
    "BOONE",
    "BOONE CO",
    "BOONE COUNTY",
    "FLORENCE",
    "UNION",
    "WALTON",
    "BURLINGTON",
    "OAKBROOK",
    "BIG BONE",
    "BULLITTSVILLE",
    "HAMILTON",
    "HEBRON",
    "PETERSBURG",
    "RABBIT HASH",
    "RICHWOOD",
    "VERONA",
    
    // Bourbon County
    "BOURBON",
    "BOURBON CO",
    "BOURBON COUNTY",
    "CANE RIDGE",
    "CENTERVILLE",
    "CLITONVILLE",
    "LITTLE ROCK",
    "MILLERSBURG",
    "NORTH MIDDLETOWN",
    "PARIS",

    // Bracken County
    "BRACKEN",
    "BRACKEN CO",
    "BRACKEN COUNTY",
    "AUGUSTA",
    "BROOKSVILLE",
    "GERMANTOWN",
    "FOSTER",
    "MILFORD",
    
    // Breathitt County
    // Brethitt County
    "BRETHITT",
    "BRETHITT CO",
    "BRETHITT COUNTY",
    "BREATHITT",
    "BREATHITT CO",
    "BREATHITT COUNTY",
    "ALTRO",
    "BAYS",
    "CANEY",
    "CANOE",
    "CHENOWEE",
    "CLAYHOLE",
    "CROCKETTSVILLE",
    "ELKATAWA",
    "EVANSTON",
    "FLINTVILLE",
    "FUGATES FORK",
    "HADDIX",
    "HARDSHELL",
    "HAYES BRANCH",
    "JACKSON",
    "LOST CREEK",
    "MORRIS FORK",
    "NED",
    "NIX BRANCH",
    "NOBLE",
    "NOCTOR",
    "OAKDALE",
    "QUICKSAND",
    "RIVERSIDE",
    "ROSE BRANCH",
    "ROUSSEAU",
    "SEBASTIANS BRANCH",
    "SMITH BRANCH",
    "SOUTH FORK",
    "TALBERT",
    "TURNERS CREEK",
    "VANCLEVE",
    "WAR CREEK",
    "WATTS",
    "WHICK",
    "WILSTACY",
    "WOLF COAL",
    
    // Caldwell County
    "CALDWELL",
    "CALDWELL CO",
    "CALDWELL COUNTY",
    "PRINCETON",
    
    // Campbell County
    "CAMPBELL",
    "CAMPBELL CO",
    "CAMPBELL COUNTY",
    "BELLEVUE",
 
    // Carroll County
    "CARROLL",
    "CARROLL CO",
    "CARROLL COUNTY",
    "CARROLLTON",
    "ENGLISH",
    "GHENT",
    "PRESTONVILLE",
    "SANDERS",
    "WORTHVILLE",
    
    // Floyd County
    "FLOYD",
    "FLOYD CO",
    "FLOYD COUNTY",
    "LANGLEY",
    "PRESTONBURG",
    
    "ALLEN",
    "AUXIER",
    "BANNER",
    "BEAVER",
    "BETSY LAYNE",
    "BEVINSVILLE",
    "BOLDMAN",
    "BYPRO",
    "CRAYNOR",
    "DANA",
    "DAVID",
    "DRIFT",
    "DWALE",
    "EASTERN",
    "GALVESTON",
    "GARRETT",
    "GRETHEL",
    "HAROLD",
    "HI HAT",
    "HIPPO",
    "HONAKER",
    "HUEYSVILLE",
    "IVEL",
    "LACKEY",
    "MARTIN",
    "MCDOWELL",
    "MELVIN",
    "PRESTONSBURG",
    "PRINTER",
    "STANVILLE",
    "TEABERRY",
    "TRAM",
    "TOLER CREEK",
    "WATERGAP",
    "WAYLAND",
    "WAULAND",  // Mispelled
    "WEEKSBURY",
    "WHEELWRIGHT",
    
    // Franklin County
    "FRANKLIN",
    "FRANKLIN CO",
    "FRANKLIN COUNTY",
    "BRIDGEPORT",
    "FORKS OF ELKHORN",
    "FRANKFORT",
    "JETT",
    "SWITZER",

    // Gallatin COunty
    "GALLATIN",
    "GALLATIN CO",
    "GALLATIN COUNTY",
    "GLENCOE",
    "SPARTA",
    "WARSAW",
    
    // Grant County
    "GRANT",
    "GRANT CO",
    "GRANT COUNTY",
    "CORINTH",
    "CRITTENDEN",
    "DRY RIDGE",
    "JONESVILLE",
    "WILLIAMSTOWN",
    
    // Green County
    "GREEN",
    "GREEN CO",
    "GREEN COUNTY",
    "SUMMERSVILLE",
    
    // Graves County
    "GRAVES",
    "GRAVES CO",
    "GRAVES COUNTY",
    "FELICIANA",
    "BELL CITY",
    "BOAZ",
    "CLEAR SPRINGS",
    "CUBA",
    "DOGWOOD",
    "DUBLIN",
    "DUKEDOM",
    "FOLSOMDALE",
    "HICKORY",
    "FANCY FARM",
    "FARMINGTON",
    "FELICIANA",
    "KALER",
    "LOWES",
    "LYNNVILLE",
    "MAYFIELD",
    "MELBER",
    "POTTSVILLE",
    "SEDALIA",
    "SYMSONIA",
    "VIOLA",
    "WATER VALLEY",
    "WEST VIOLA",
    "WESTPLAINS",
    "WINGO",
    
    // Hardin County
    "HARDIN",
    "HARDIN CO",
    "HARDIN COUNTY",
    "BIG SPRING",
    "BLUE BALL",
    "CECILIA",
    "COLESBURG",
    "DEVER HOLLOW",
    "EASTVIEW",
    "ELIZABETHTOWN",
    "FORT KNOX",
    "GLENDALE",
    "HARCOURT",
    "HARDIN SPRINGS",
    "HOWE VALLEY",
    "HOWELL SPRING",
    "MILL CREEK",
    "MULDRAUGH",
    "NEW FRUIT",
    "NOLIN",
    "OLD STEPHENSBURG",
    "QUAKER VALLEY",
    "RADCLIFF",
    "RED MILLS",
    "RINEYVILLE",
    "SONORA",
    "ST JOHN",
    "STAR MILLS",
    "STEPHENSBURG",
    "SUMMITT",
    "TIP TOP",
    "TUNNEL HILL",
    "UPTON",
    "VERTREES",
    "VINE GROVE",
    "WEST POINT",
    "WHITE MILLS",
    "YOUNGERS CREEK",
   
    // Harlan County
    "HARLAN",
    "HARLAND CO",
    "HARLAND COUNTY",
    "CUMBERLAND",
    
    // Harrison County
    "HARRISON",
    "HARRISON CO",
    "HARRISON COUNTY",
    "BERRY",
    "CYNTHIANA",
    "BRECKINRIDGE",
    "BROADWELL",
    "BUENA VISTA",
    "COLVILLE",
    "CONNERSVILLE",
    "HOOKTOWN",
    "LAIR",
    "LEES LICK",
    "LEESBURG",
    "KELAT",
    "MORNINGGLORY",
    "ODDVILLE",
    "POINDEXTER",
    "RUDDELS MILLS",
    "RUTLAND",
    "SHADYNOOK",
    "SHAWHAN",
    "SUNRISE",
    
    // Hart County
    "HART",
    "HART CO",
    "HART COUNTY",
    "BONNIEVILLE",
    "CANMER",
    "CUB RUN",
    "HAMMONVILLE",
    "HARDYVILLE",
    "HORSE CAVE",
    "LEGRANDE",
    "MONROE",
    "MUNFORDVILLE",
    "PRICEVILLE",
    "ROWLETTS",
    "UNO",

    // Henry County
    "HENRY",
    "HENRY CO",
    "HENRY COUNTY",
    "BETHLEHEM",
    "CAMPBELLSBURG",
    "DEFOE",
    "EMINENCE",
    "FRANKLINTON",
    "LOCKPORT",
    "NEW CASTLE",
    "PENDLETON",
    "PLEASUREVILLE",
    "PORT ROYAL",
    "SMITHFIELD",
    "SULPHUR",
    "TURNERS STATION",
    
    // Hopkins County
    "HOPKINS",
    "HOPKINS CO",
    "HOPKINS COUNTY",
    "GRAPEVINE",
    
    // Jefferson County
    "JEFFERSON",
    "JEFFERSON CO",
    "JEFFERSON COUNTY",
    "ANCHORAGE",
    "AUDUBON PARK",
    "BANCROFT",
    "BARBOURMEADE",
    "BEECHWOOD VILLAGE",
    "BELLEMEADE",
    "BELLEWOOD",
    "BLUE RIDGE MANOR",
    "BRIARWOOD",
    "BROAD FIELDS",
    "BROECK POINTE",
    "BROWNSBORO FARM",
    "BROWNSBORO VILLAGE",
    "BUECHEL",
    "CAMBRIDGE",
    "CHERRYWOOD VILLAGE",
    "COLDSTREAM",
    "CREEKSIDE",
    "CROSSGATE",
    "DOUGLASS HILLS",
    "DRUID HILLS",
    "FAIRDALE",
    "FAIRMEADE",
    "FERN CREEK",
    "FINCASTLE",
    "FISHERVILLE",
    "FOREST HILLS",
    "GLENVIEW HILLS",
    "GLENVIEW MANOR",
    "GLENVIEW",
    "GOOSE CREEK",
    "GRAYMOOR-DEVONDALE",
    "GREEN SPRING",
    "HERITAGE CREEK",
    "HICKORY HILL",
    "HIGHVIEW",
    "HILLS AND DALES",
    "HOLLOW CREEK",
    "HOLLYVILLA",
    "HOUSTON ACRES",
    "HURSTBOURNE ACRES",
    "HURSTBOURNE",
    "INDIAN HILLS",
    "JEFFERSONTOWN",
    "KEENELAND",
    "KINGSLEY",
    "LANGDON PLACE",
    "LINCOLNSHIRE",
    "LOUISVILLE",
    "LYNDON",
    "LYNNVIEW",
    "MANOR CREEK",
    "MARYHILL ESTATES",
    "MEADOW VALE",
    "MEADOWBROOK FARM",
    "MEADOWVIEW ESTATES",
    "MIDDLETOWN",
    "MOCKINGBIRD VALLEY",
    "MOORLAND",
    "MURRAY HILL",
    "NEWBURG",
    "NORBOURNE ESTATES",
    "NORTHFIELD",
    "NORWOOD",
    "OKOLONA",
    "OLD BROWNSBORO PLACE",
    "PARKWAY VILLAGE",
    "PENILE",
    "PLANTATION",
    "PLEASURE RIDGE PARK",
    "PLYMOUTH VILLAGE",
    "POPLAR HILLS",
    "PROSPECT",
    "RICHLAWN",
    "RIVERWOOD",
    "ROLLING FIELDS",
    "ROLLING HILLS",
    "SENECA GARDENS",
    "SHIVELY",
    "SOUTH PARK VIEW",
    "SPRING MILL",
    "SPRING VALLEY",
    "SPRINGLEE",
    "ST DENNIS",
    "ST MATTHEWS",
    "ST REGIS PARK",
    "STRATHMOOR MANOR",
    "STRATHMOOR VILLAGE",
    "SYCAMORE",
    "TEN BROECK",
    "THORNHILL",
    "VALLEY STATION",
    "VAN LEAR",
    "WATTERSON PARK",
    "WELLINGTON",
    "WEST BUECHEL",
    "WESTWOOD",
    "WHIPPS MILLGATE",
    "WILDWOOD",
    "WINDY HILLS",
    "WOODLAND HILLS",
    "WOODLAWN PARK",
    "WORTHINGTON HILLS",
    
    // Kenton County
    "KENTON",
    "KENTON CO",
    "KENTON COUNTY",
    "BROMLEY",
    "COVINGTON",
    "CRESCENT SPRINGS",
    "CRESTVIEW HILLS",
    "EDGEWOOD",
    "ELSMERE",
    "ERLANGER",
    "FAIRVIEW",
    "FORT MITCHELL",
    "FORT WRIGHT",
    "INDEPENDENCE",
    "KENTON VALE",
    "LAKESIDE PARK",
    "LATONIA LAKES",
    "LATONIA",
    "LUDLOW",
    "NICHOLSON",
    "PARK HILLS",
    "RYLAND HEIGHTS",
    "TAYLOR MILL",
    "VILLA HILLS",
    "VISALIA",
    "WALTON",
    
    // Knott County
    "KNOTT",
    "KNOTT CO",
    "KNOTT COUNTY",
    "ANCO",
    "BRINKLEY",
    "CARRIE",
    "DEMA",
    "EMMALENA",
    "FISTY",
    "GARNER",
    "HINDMAN",
    "HOLLYBUSH",
    "KITE",
    "LARKSLANE",
    "LEBURN",
    "LITTCARR",
    "LITT CARR",
    "MALLIE",
    "MOUSIE",
    "PINETOP",
    "PINE TOP",
    "PIPPA PASSES",
    "RAVEN",
    "RED FOX",
    "REDFOX",
    "SASSAFRAS",
    "SOFT SHELL",
    "TOPMOST",
    "SPIDER",
    "VEST",
    "VICCO",
    "WISCOAL",
    
    // LaRue County
    "LARUE",
    "LARUE CO",
    "LARUE COUNTY",
    "MAGNOLIA",
    
    // Letcher County
    "LETCHER",
    "LETCHER CO",
    "LETCHER COUNTY",
    "BLACKEY",
    "BURDINE",
    "DUNHUM",
    "FLEMING-NEON",
    "EOLIA",
    "ERMINE",
    "GASKILL",
    "ISOM",
    "JENKINS",
    "LETCHER",
    "MAYKING",
    "MCROBERTS",
    "MILLSTONE",
    "NEON",
    "PAYNE GAP",
    "SECO",
    "WHITESBURG",
    
    // Livingston County
    "LIVINGSTON",
    "LIVINGSTON CO",
    "LIVINGSTON COUNTY",
    "GRAND RIVERS",
    
    // Lyon County
    "LYON",
    "LYON CO",
    "LYON COUNTY",
    "EDDYVILLE",
    "EDDVYILLE",  // Misspelled :(
    "KUTTAWA",
    
    // Magoffin County
    "MAGOFFIN",
    "MAGOFFIN CO",
    "MAGOFFIN COUNTY",
    "SALYERSVILLE",
    
    // Marshall County
    "MARSHALL",
    "MARSHALL CO",
    "MARSHALL COUNTY",
    "GILBERTSVILLE",
    
    // McCracken County
    "MCCRACKEN",
    "MCCRACKEN CO",
    "MCCRACKEN COUNTY",
    "PADUCAH",
    
    // Morgan County
    "MORGAN",
    "MORGAN CO",
    "MORGAN COUNTY",
    "MAYTOWN",
    
    // Nicholas County
    "NICHOLAS",
    "NICHOLAS CO",
    "NICHOLAS COUNTY",

    // Oldham County
    "OLDHAM",
    "OLDHAM CO",
    "OLDHAM COUNTY",
    "BALLARDSVILLE",
    "BROWNSBORO",
    "BUCKNER",
    "CENTERWOOD",
    "CRESTWOOD",
    "FLODSBURG",
    "GOSHEN",
    "LA GRANGE",
    "LAGRANGE",
    "ORCHARD GRASS HILLS",
    "PEWEE VALLEY",
    "PROSPECT",
    "RIVER BLUFF",
    "WESTPORT",
    
    // Owen County
    "OWEN",
    "OWEN CO",
    "OWEN COUNTY",
    "GRATZ",
    "MONTEREY",
    "OWENTON",
    "HESLER",
    "LONG RIDGE",
    "LUSBYS MILL",
    "NEW LIBERTY",
    "PERRY PARK",
    "PLEASANT HOME",
    "POPLAR GROVE",
    "SQUIRESVILLE",
    "WHEATLEY",
    
    // Owsley Countyu
    "OWSLEY",
    "OWSLEY CO",
    "OWSLEY COUNTY",
    "BOONEVILLE",

    // Pendleton County
    "PENDLETON",
    "PENDLETON CO",
    "PENDLETON COUNTY",
    "BUTLER",
    "DEMOSSVILLE",
    "FALMOUTH",
    "SILGO",
    
    // Perry County
    "PERRY",
    "PERRY CO",
    "PERRY COUNTY",
    "AMBURGEY",
    "ARY",
    "AVAWAM",
    "BUCKHORN",
    "COMBS",
    "CHAVIES",
    "CORNETTSVILLE",
    "GAYS CREEK",
    "HAZARD",
    "LEATHERWOOD",
    "LOTTS CREEK",
    "SAUL",
    "VICCO",
    "VIPER",
    
    // Pike County
    "PIKE CO",
    "PIKE COUNTY",
    "AFLEX",
    "ASHCAMP",
    "BEEFHIDE",
    "BELCHER",
    "BELFRY",
    "BLACKBERRY",
    "CANADA",
    "CANADE",  // Misspelled
    "CEDARVILLE",
    "COAL RUN",
    "DORTON",
    "ELKHORN",
    "ELKHORN CITY",
    "ELKHORN CREEK",
    "FEDS CREEK",
    "FEDSCREEK",
    "FERRELLS CREEK",
    "FORDS BRANCH",
    "FREEBURN",
    "GARDEN VILLAGE",
    "GULNARE",
    "HARDY",
    "HELLIER",
    "JAMBOREE",
    "JOES CREEK",
    "JONANCY",
    "KIMPER",
    "LICK CREEK",
    "LOOKOUT",
    "MAJESTIC",
    "MCCARR",
    "META",
    "MOUTHCARD",
    "PHELPS",
    "PHYLLIS",
    "PIKEVILLE",
    "RACCOON",
    "SHELBIANA",
    "SIDNEY",
    "SOUTH WILLIAMSON",
    "STONE",
    "STOPOVER",
    "SYDNEY",
    "TURKEY CREEK",
    "VARNEY",
    "VIRGIE",
    
    // Robertson county
    "ROBERTSON",
    "ROBERTSON CO",
    "ROBERTSON COUNTY",
    "KENTONTOWN",
    "MOUNT OLIVET",
    "PIQUA",

    // Scott County
    "SCOTT",
    "SCOTT CO",
    "SCOTT COUNTY",
    "GEORGETOWN",
    "SADIEVILLE",
    "STAMPING GROUND",
    
    // Shelby County
    "SHELBY",
    "SHELBY CO",
    "SHELBY COUNTY",
    "BAGDAD",
    "CHESTNUT GROVE",
    "CHRISTIANBURG",
    // "CLARK",
    "CLAY VILLAGE",
    "CROPPER",
    "FINCHVILLE",
    "HARRISONVILLE",
    "HEMP RIDGE",
    "HOOPER",
    "MT EDEN",
    "MULBERRY",
    "OLIVE BRANCH",
    "PEYTONA",
    "PLEASUREVILLE",
    "SCOTTS STATION",
    "SHELBYVILLE",
    "SIMPSONVILLE",
    "SOUTHVILLE",
    "TODDS POINT",
    "WADDY",
    
    // Simpson County
    "SIMPSON",
    "SIMPSON CO",
    "SIMPSON COUNTY",
    "FRANKLIN",
    "GOLD CITY",
    "MIDDLETON",
    "NEOSHEO",
    "PRICES MILL",
    "PROVIDENCE",
    "SALMONS",
    
    // Spencer County
    "SPENCER",
    "SPENCER CO",
    "SPENCER COUNTY",
    "ELK CREEK",
    "LITTLE MOUNT",
    "MOUNT EDEN",
    "RIVALS",
    "TAYLORSVILLE",
    "WATERFORD",
    "YODER",
  
    // Trigg County
    "TRIGG",
    "TRIGG CO",
    "TRIGG COUNTY",
    
    // Trimble County
    "TRIMBLE",
    "TRIMBLE CO",
    "TRIMBLE COUNTY",
    "BEDFORD",
    "LOCUST",
    "MILTON",
    "WISES LANDING",
    
    // Warren County
    "WARREN",
    "WARREN CO",
    "WARREN COUNTY",
    "BOWLING GREEN",
    
    // Wolfe County
    "WOLFE",
    "WOLFE CO",
    "WOLFE COUNTY",
    "ROGERS",
    
    // Robertson County, TN
    "ROBERTSON",
    "ROBERTSON CO",
    "ROBERTSON COUNTY",
    "ORLINDA"
  };
  
  private static final Properties FIX_CITY_TABLE = buildCodeTable(new String[]{
      "CANADE",             "CANADA",
      "EDDVYILLE",          "EDDYVILLE",
      "WAULAND",            "WAYLAND"
  });
  
  private static final Properties CITY_ST_TABLE = buildCodeTable(new String[]{
      "ROBERTSON",          "TN",
      "ROBERTSON COUNTY",   "TN",
      "ORLINDA",            "TN"
  });
  
  private static final Properties MAP_CITY_TABLE = buildCodeTable(new String[]{
      "JOES CREEK",      "PIKEVILLE",
      "LOTTS CREEK",     "HAZARD",
      "TOLER CREEK",     "HAROLD"
  });
}
