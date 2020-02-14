package net.anei.cadpage.parsers.MS;

import java.util.regex.Pattern;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchB2Parser;

public class MSDeSotoCountyAParser extends DispatchB2Parser {
  
  private static final Pattern DIR_BOUND_PTN = Pattern.compile("\\b([NSEW])/B\\b");

  public MSDeSotoCountyAParser() {
    super("911CENTER:||E-911:", CITY_LIST, "DESOTO COUNTY", "MS", B2_FORCE_CALL_CODE);
    setupCallList(CALL_LIST);
    setupMultiWordStreets(MWORD_STREET_LIST);
    setupSpecialStreets(SPECIAL_STREET_LIST);
    setupSaintNames("ANDREWS");
  }
  
  @Override
  public String getFilter() {
    return "cadalerts@eforcesoftware.com";
  }
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    body = DIR_BOUND_PTN.matcher(body).replaceAll("$1B");
    return super.parseMsg(subject, body, data);
  }
  
  private static final Pattern COR_PTN = Pattern.compile("\\bCOR\\b", Pattern.CASE_INSENSITIVE);
  
  @Override
  public String adjustMapAddress(String addr) {
    addr =  COR_PTN.matcher(addr).replaceAll("CORNER");
    return addr;
  }
  
  private static final String[] SPECIAL_STREET_LIST = new String[]{
      "BRAYBOURNE MAIN",
      "GREYHAWK COVE",
      "OXBOURNE",
      "MCA",
      "MITHCELLS COR",
      "ROLLINS COVE",
      "SANDBOURNE",
      "WHITE HAWK CROSS"
  };
  
  private static final String[] MWORD_STREET_LIST = new String[]{
    "AIRPORT INDUSTRIAL",
    "ALEXANDERS RIDGE",
    "ALLEN GLEN",
    "ALLEN RIDGE",
    "AUTUMN POINT",
    "BECKY SUE",
    "BELL RIDGE",
    "BELL WOOD",
    "BELMORE LAKES",
    "BILLY PAT",
    "BUSINESS CENTER",
    "CAMP CREEK",
    "CARA BEND",
    "CARTER LANE",
    "CEDAR CREST",
    "CENTER HILL",
    "CHAMBERLIN OAK",
    "CHAPEL RIDGE",
    "CRAFT GOODMAN FRONTAGE",
    "CREEK SIDE",
    "DEVON PARK",
    "DEW BERRY",
    "EMERALD WAY",
    "EVENING SHADE",
    "FARM POND",
    "FIRST LAKEVIEW",
    "FLICKER RIDGE",
    "FOX GLEN",
    "FOX HUNT",
    "FOX HUNTERS",
    "FOX RUN",
    "GEE GEE",
    "GRASS POND",
    "GRAYS CREEK",
    "GREEN FOREST",
    "GREEN RIVER",
    "GREEN VILLAGE",
    "HACKS CROSS",
    "HAWKS CROSSING",
    "HICKORY NUT",
    "HIGHLAND MEADOWS",
    "HOLLY SPRINGS",
    "HONEY SUCKLE",
    "HORN LAKE",
    "HUNTERS HILL",
    "HUNTERS HORN",
    "HUNTERS RUN",
    "HWY 178",
    "HWY 302",
    "JOE LYON",
    "KEY STONE",
    "KOKO REEF",
    "KYLE DURAN",
    "LAUREL HILL",
    "LAZY CREEK",
    "LEIGH ANN",
    "LOW BRIDGE",
    "MARY JANE",
    "MEADOW RIDGE",
    "METHODIST HOSPITAL",
    "MORGAN MANOR",
    "OAK CROSSING",
    "OAK FOREST",
    "OAK GROVE",
    "OAK HEIGHTS",
    "OAK LEAF",
    "OAK MANOR",
    "PARKVIEW OAKS",
    "PAUL COLEMAN",
    "PIGEON ROOST",
    "PLANTATION RIDGE",
    "PLEASANT HILL",
    "POLK LANE",
    "RACHEL SHEA",
    "RED BANKS",
    "RED MAPLE",
    "REGAL BEND",
    "RIVER BIRCH",
    "ROBERTSON GIN",
    "ROLLING HILLS",
    "SANDIDGE CENTER",
    "SCENIC RIDGE",
    "SECOND HICKORY",
    "SHADY GROVE",
    "SHALLOW CREEK",
    "SPRING CREEK",
    "SPRING HILL",
    "SPRING VALLEY",
    "ST ANDREWS",
    "ST JOSEPH",
    "ST MICHAEL THOMAS",
    "STONE GARDEN",
    "STONE PARK",
    "STONE RIDGE",
    "STONEY BROOK",
    "STRAW BRIDGE",
    "SUMMERS CREEK",
    "SUMMERS PLACE",
    "TALLY HO",
    "TERRY CHASE JUDITH",
    "THE HIGH",
    "TIMBER OAKS",
    "TRINITY GROVE",
    "TRINITY PARK",
    "WEDGE HILL",
    "WHISPERING PINES",
    "WHITE HAWK",
    "WILSON MILL",
    "WIND PARK E",
    "WOODLAND LAKE"

  };
  
  private static final CodeSet CALL_LIST = new CodeSet(
      "1 ABDOMINAL PAIN / PROBLEMS",
      "2 ALLERGIC REACTIONS / STINGS",
      "3 ANIMAL BITES / ATTACKS",
      "4 ASSAULT / RAPE WITH INJURY",
      "5 BACK PAIN (NON TRAUMATIC)",
      "6 BREATHING PROBLEMS",
      "7 BURNS (SCALDS) / EXPLOSION",
      "8 CARBON MONOXIDE INHALATION",
      "9 CARDIAC-RESP ARREST / DEATH",
      "10 CHEST PAIN / DISCOMFORT",
      "11 CHOKING",
      "12 CONVULSIONS / SEIZURES",
      "13 DIABETIC PROBLEMS",
      "14 DROWNING / NEAR DROWNING",
      "15 ELECTROCUTION / LIGHTNING",
      "16 EYE PROBLEMS / INJURIES",
      "17 FALLS WITH INJURY",
      "18 HEADACHE",
      "19 HEART PROBLEMS / A.I.C.D.",
      "20 HEAT / COLD EXPOSURE",
      "21 HEMORRHAGE / LACERATIONS",
      "22 OTHER ENTRAPMENTS (NON MVA)",
      "23 OVERDOSE / POISON (INGEST)",
      "24 PREGNANCY/CHILDBIRTH/MISCARRIAGE",
      "25 SUICIDE ATTEMPT/PSYCHIATRIC",
      "26 SICK PERSON (SPECIFIC DIAG)",
      "27 STAB / SHOT / PENETRATING",
      "28 STROKE (CVA) - TIA",
      "28 STROKE (CVA) – TIA",
      "29 MVA ROLLOVER",
      "29 MVA W/ HAZARDOUS MATERIALS",
      "29 MVA WITH ENTRAPMENT",
      "29 MVA WITH INJURIES",
      "30 TRAUMATIC INJURIES",
      "31 UNCONSCIOUS / FAINTING",
      "32 UNKNOWN MEDICAL PROBLEMS",
      "33 INTERFACILITY MED TRANSFER",
      "911 CALL TRANSFER",
      "911 HANG-UP CALL",
      "ABDOMINAL PAIN",
      "ACCIDENT HIT & RUN",
      "ACCIDENT INV TRAIN",
      "ACCIDENT PATROL VEHICLE",
      "ACCIDENT PEDESTRIAN",
      "ACCIDENT PEDESTRIAN",
      "ACCIDENT PRIV PROPR",
      "ACCIDENT UNKNOWN INJURIES",
      "ACCIDENT UNKOWN INJURIES",
      "ACCIDENT W/ HAZ MAT",
      "ACCIDENT W/FATALITY",
      "ACCIDENT W/INJURIES",
      "ACCIDENT W/OUT INJ",
      "ACCIDENT W/OUT INJURIES",
      "AIRCRAFT EMERGENCY",
      "AIRCRAFT EMERGENCY - CRASH",
      "AIRCRAFT EMERGENCY - STANDBY",
      "ALL OTHER FIRE",
      "ALLERGIC REACTION/ENVENOMATION",
      "ANIMAL/INSECT BITES ATTACK",
      "ASSAULT VICTIM",
      "ASSIST EMS",
      "ASSIST FIRE DEPARTMENT",
      "ASSIST OTHER AGENCY",
      "ATTEMPT TO LOCATE",
      "BACK PAIN",
      "BOMB THREAT",
      "BOMB THREAT",
      "BREATHING PROBLEMS",
      "BURNS/EXPLOSION",
      "CARBON MONO POISONING/HAZMAT",
      "CARBON MONOXIDE DETECTOR",
      "CARDIAC ARREST",
      "CARADIAC ARREST/RESPITORY",
      "CHEST PAINS-CHEST DISCOMFORT",
      "CHIMNEY FIRE",
      "CHOKING PARTY",
      "DIABETIC PROBLEMS",
      "DROWNING",
      "DROWNING/DIVING ACCIDENT",
      "DUMPSTER FIRE",
      "ELECTRIC POWER LINE DOWN",
      "ELECTRICAL SHORT / NO SMOKE",
      "ELECTICAL SHORT/MO SMOKE",
      "ELECTRICAL SHORT/NO SMOKE",
      "ELECTROCUTION/LIGHTNING",
      "EXPLOSION NO FIRE",
      "EXPLOSION WITH FIRE",
      "EYE PROBLEMS/INJURIES",
      "FALLS",
      "FIRE ALARM BUSINESS",
      "FIRE ALARM - COMMERCIAL",
      "FIRE ALARM - RESIDENTIAL",
      "FIRE ALARM RESIDENCE",
      "FIRE ALARM RESIDENCE",
      "FIRE DEPARTMENT LIFTING ASSIST",
      "FLAMMABLE LIQUID SPILL",
      "GAS LEAK (NON-LIQUID)",
      "GAS LEAK-SPILL",
      "GAS LEAK-SPILL",
      "GAS LEAK-SPILL WITH FIRE",
      "GRASS / BRUSH / TREE FIRE",
      "GRASS-BRUSH-TREE FIRE",
      "GRASS-BRUSH-TREE-FIRE",
      "GUNSHOT WOUNDS",
      "HAZ-MAT INCIDENT",
      "HAZARDOUS MATERIAL SPILL/LEAK",
      "HEADACHE",
      "HEART PROBLEMS",
      "HEMORRHAGE/BLEEDING/LACERATION",
      "HOT/COLD EXPOSURE",
      "LIFTING ASSISTANCE (NO INJURY)",
      "MEDICAL ALARM",
      "MOVE UP TONE",
      "MVA PATROL VEHICLE W/INJURIES",
      "MVA/ENTRAPMENT",
      "OVERDOSE/POISONING",
      "POWER LINE DOWN",
      "PREGNANCY/CHILDBIRTH",
      "PSYCHATRIC/ABNORMAL BEHAVIOR",
      "PSYCHIATRIC/ABNORMAL BEHAVIOR",
      "REKINDLE",
      "RESCUE/EXTRICATION",
      "SEIZURES/CONVULSIONS",
      "SERVICE CALL",
      "SICK CALL PERSON",
      "SMELL OF SMOKE - INSIDE",
      "SMELL OF SMOKE - OUTSIDE",
      "SMELL OF SMOKE INSIDE",
      "SMELL OF SMOKE OUTSIDE",
      "STAB WOUNDS",
      "STROKE/CVA",
      "STRUCTURE FIRE",
      "STRUCTURE FIRE",
      "SUICIDE",
      "SUICIDE ATTEMPT",
      "SUICIDE-ATTEMPT",
      "SUICIDE-THREAT",
      "SUSPICIOUS PACKAGE",
      "SUSPICIOUS PERSON",
      "TEST CALL",
      "TRANSFORMER FIRE",
      "TRAUMATIC INJUIRES",
      "UNAUTHORIZED BURNING",
      "UNAUTHORIZED BURNING",
      "UNCONSCIOUS/FAINTING",
      "UNKNOWN MEDICAL",
      "UNLOCK VEHICLE",
      "UNLOCK VEHICLE (CHILD INSIDE)",
      "VEHICLE FIRE",
      "VEHICLE FIRE",
      "VISIBLE SMOKE - INSIDE",
      "VISIBLE SMOKE - OUTSIDE",
      "WATER RESCUE",
      "WELFARE CONCERN"
  );

  static final String[] CITY_LIST = new String[]{
    
    // Cities
    "HERNANDO",
    "HORN LAKE",
    "OLIVE BRANCH",
    "SOUTHAVEN",

    // Towns
    "WALLS",

    // Villages
    "MEMPHIS",

    // Census-designated places
    "BRIDGETOWN",
    "LYNCHBURG",

    // Unincorporated communities
    "COCKRUM",
    "DAYS",
    "EUDORA",
    "LAKE CORMORANT",
    "LAKE VIEW",
    "LEWISBURG",
    "LOVE",
    "MINERAL WELLS",
    "NESBIT",
    "NORFOLK",
    "PLEASANT HILL",
    "WEST DAYS",
    
    // Marshall County
    "BYHALIA",
    
    // Tate County
    "INDEPENDENCE"
  };
}
