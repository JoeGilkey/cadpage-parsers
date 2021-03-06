package net.anei.cadpage.parsers.WV;

import java.util.regex.Pattern;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchB2Parser;



public class WVLincolnCountyParser extends DispatchB2Parser {
  
  public WVLincolnCountyParser() {
    super("LINCOLN 911:", CITY_LIST, "LINCOLN COUNTY", "WV", B2_OPT_CALL_CODE);
    setupMultiWordStreets(
        "A PARK",
        "BALL PARK",
        "BIG LAUREL",
        "BLUE BIRD",
        "CHARLIE NELSON",
        "COAL RIVER",
        "DR STEELE FARM",
        "EAST LYNN",
        "ELLIS FARM",
        "END OF MONDAY",
        "FOUR MILE",
        "HILL VIEW",
        "INDIAN VALLEY",
        "JOSH BRANCH CEMETARY",
        "LADY BUG",
        "LEES LAKE",
        "LINCOLN PANTHER",
        "LITTLE COAL RIVER",
        "LITTLE LAUREL",
        "LOW GAP",
        "LOWER MUD RIVER",
        "MARY JANE",
        "MIDWAY SCHOOL",
        "MUD RIVER",
        "NINE MILE",
        "ONE MILE",
        "PIN OAK",
        "ROBERT C BYRD",
        "SAND LICK", 
        "SILVER MINE",
        "SIX MILE",
        "STONES TRAILER",
        "SUGAR TREE",
        "TEN MILE",
        "TIMBER WOLF",
        "TWO MILE",
        "UPPER MUD RIVER",
        "WEST PARK"
    );
    setupCallList(CALL_LIST);
    setupDoctorNames("STEELE");
  }
  
  @Override
  public String getFilter() {
    return "LINCOLN911@e911.org";
  }
  
  @Override
  public String adjustMapAddress(String addr) {
    addr = STRAIGHT_FRK_PTN.matcher(addr).replaceAll("STRAIGHT FORK RD");
    addr = SLASH_BR_BELLOMY_RD_PTN.matcher(addr).replaceAll("BELLOMY RD");
    return addr;
  }
  private static final Pattern STRAIGHT_FRK_PTN = Pattern.compile("\\bSTRAIGHT +FO?RK\\b", Pattern.CASE_INSENSITIVE);
  private static final Pattern SLASH_BR_BELLOMY_RD_PTN = Pattern.compile("\\bSLASH BR BELLOMY RD\\b", Pattern.CASE_INSENSITIVE);
  
  
  @Override
  protected boolean parseMsg(String body, Data data) {
    if (!super.parseMsg(body, data)) return false;
    data.strCross = TO_PTN.matcher(data.strCross).replaceAll(" / ");
    return true;
  }
  private static final Pattern TO_PTN = Pattern.compile(" +TO +", Pattern.CASE_INSENSITIVE);
  
  @Override
  public String adjustMapCity(String city) {
    if (city.equalsIgnoreCase("FEZ")) city = "BRANCHLAND";
    return city;
  }
  
  private static CodeSet CALL_LIST = new CodeSet(
      "ABDOMINAL PAIN",
      "ALLERGIC REACTION",
      "ANIMAL COMPLAINT",
      "AUTO ACCIDENT NO INJURIES",
      "AUTO ACCIDENT UNKNOWN INJURIES",
      "AUTO ACCIDENT W/ENTRAPMENT",
      "AUTO ACCIDENT WITH INJURIES",
      "BACK PAIN",
      "BOX ALARM",
      "BLEEDING NON TRAUMA",
      "BREATHING DIFFICULTY",
      "BRUSH FIRE",
      "CARDIAC ARREST",
      "CHEST PAINS",
      "CHOKING",
      "CODE 3 CHEST PAIN",
      "CODE 3 DIFFICULTY BREATHING",
      "CODE 3 SEIZURE",
      "CODE 3 SYNCOPE/FAINTING",
      "CODE 3 TRAUMA",
      "CODE3 UNCONSCIOUS/UNRESPONSIVE",
      "DISTURBANCE",
      "FIGHT",
      "FIRE INVESTIGATION",
      "FLASH FLOOD WARNING",
      "FLOOD WARNING",
      "GAS LEAK",
      "GUNSHOTS",
      "HOUSE FIRE",
      "LANDING ZONE SECURED",
      "LINE DOWN",
      "LOCK OUT",
      "LOCK OUT WITH ENTRAPMENT",
      "MENTAL PATIENT",
      "MUTUAL AID TO OTHER CNTY/DEPT",
      "OTHER FIRE CALL",
      "OTHER POLICE CALL",
      "OVERDOSE",
      "PEDESTRIAN HIT",
      "PUBLIC ASSISTANCE NON EMERGENC",
      "RELEASED FROM",
      "SEIZURES",
      "SHOOTING",
      "SICK UNKNOWN",
      "SMOKE INVESTIGATION",
      "STORM DAMAGE",
      "STROKE",
      "STRUCTURE FIRE",
      "SUICIDE ATTEMPT",
      "TEST",
      "TRAFFIC PROBLEM",
      "TRAILER FIRE",
      "TRASH FIRE",
      "TRAUMA",
      "TREE DOWN",
      "VEHICLE FIRE",
      "WATER RESCUE"
  );
  
  private static final String[] CITY_LIST = new String[]{
    
    // Incorporated cities and towns

    "HAMLIN",
    "WEST HAMLIN",

    // Unincorporated communities

    "ALKOL",
    "ALUM CREEK",
    "ATENVILLE",
    "BIG UGLY",
    "BRANCHLAND",
    "DOLLIE",
    "EDEN PARK",
    "FERRELLSBURG",
    "FEZ",
    "FOURTEEN",
    "FRY",
    "GILL",
    "GREEN SHOAL",
    "GRIFFITHSVILLE",
    "HARTS",
    "LEET",
    "MIDKIFF",
    "MYRA",
    "PALERMO",
    "PLEASANT VIEW",
    "RANGER",
    "RECTOR",
    "SIAS",
    "SOD",
    "SPURLOCKVILLE",
    "SUMERCO",
    "SWEETLAND",
    "WARREN",
    "WEWANTA",
    "WOODVILLE",
    "YAWKEY",
    
    "CABELL COUNTY",
      "CULLODEN",
    
    "LOGAN COUNTY",
    
    "PUTNAM COUNTY",
    
    "KANAWHA COUNTY",
      "ALUM CRK",
      "TORNADO"
  };
}
