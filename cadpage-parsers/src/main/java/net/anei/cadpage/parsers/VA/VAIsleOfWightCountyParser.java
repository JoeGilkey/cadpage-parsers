package net.anei.cadpage.parsers.VA;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchOSSIParser;


public class VAIsleOfWightCountyParser extends DispatchOSSIParser {
  
  public VAIsleOfWightCountyParser() {
    super("ISLE OF WIGHT COUNTY", "VA",
          "FYI? ( BOLO BOLO? ADDR? " + 
               "|  CANCEL ADDR SKIP " + 
               "| CALL ( ADDR | PLACE ADDR | ADDR ) " +
               "| ADDR APT? DIST? ( CALL! | X X? CALL! | PLACE X X? CALL! | PLACE CALL! | PLACE PLACE CALL! | CALL! ) ( X X? | ) ) INFO/N+");
    setupGpsLookupTable(GPS_LOOKUP_TABLE);
    addRoadSuffixTerms("CRES");
  }
  
  @Override
  public String getFilter() {
    return "@isleofwightUS.net";
  }
  
  private static final Pattern DIST_PLACE_PTN = Pattern.compile("(DIST:.*?) - (.*)");
  
  @Override
  public boolean parseMsg(String body, Data data) {
    body = body.replace('\n', ' ');
    if (!body.startsWith("CAD:")) body = "CAD:" + body;
    if (!super.parseMsg(body, data)) return false;
    Matcher match = DIST_PLACE_PTN.matcher(data.strPlace);
    if (match.matches()) data.strPlace = match.group(2) + " - " + match.group(1);
    return true;
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("BOLO")) return new MyBoloField();
    if (name.equals("APT")) return new MyAptField();
    if (name.equals("DIST")) return new PlaceField("DIST:.*");
    if (name.equals("CALL")) return new MyCallField();
    if (name.equals("X")) return new MyCrossField();
    return super.getField(name);
  }
  
  private static final Pattern BOLO_PTN = Pattern.compile("BOLO|BE ON THE LOOKOUT.*");
  private class MyBoloField extends CallField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      if (!BOLO_PTN.matcher(field).matches()) return false;
      if (data.strCall.length() == 0) data.strCall = field;
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
  }
  
  private static final Pattern APT_PTN = Pattern.compile("(?:APT|ROOM|RM|SUITE|LOT|#) *(.*)|[A-Z]*[0-9]+[- ]?[A-Z0-9]*|[A-Z]{1,2}|.* FLOOR|.* FLR");
  private class MyAptField extends AptField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      if (field.startsWith("1050")) return false;
      Matcher match = APT_PTN.matcher(field);
      if (!match.matches()) return false;
      String apt = match.group(1);
      if (apt == null) {
        apt = field;
        if (apt.length() > 5 &&
            !apt.endsWith(" FLOOR") &&
            !apt.endsWith(" FLR")) return false;
      }
      parse(apt, data);
      return true;
    }
  }
  
  private class MyCallField extends CallField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      if (!CALL_SET.contains(field)) return false;
      parse(field, data);
      return true;
    }
  }
  
  // Cross street doesn't permit name ending with PLACE on
  // the theory that this is probably a place name
  private class MyCrossField extends CrossField {
    @Override
    public boolean checkParse(String field, Data data) {
      if (field.endsWith(" PLACE")) return false;
      return super.checkParse(field, data);
    }
  }
  
  @Override
  public boolean checkCall(String call) {
    if (call.equals("CANCEL") || call.equals("BOLO") || call.startsWith("BE ON THE LOOKOUT")) return true;
    return CALL_SET.contains(call);
  }
  
  private static final Properties GPS_LOOKUP_TABLE = buildCodeTable(new String[]{
      "214 EAST ST",                          "+36.978188,-76.645103",
      "315 EAST ST",                          "+36.979602,-76.646173",
      "3377 HOMESTEAD DR",                    "+36.788948,-76.864822",
      "9033 MARGARET DR",                     "+36.842812,-76.772552",
      "314 MIDDLE ST",                        "+36.979919,-76.646790"
  });

  private static final Set<String> CALL_SET = new HashSet<String>(Arrays.asList(
      "1050",
      "1050PI",
      "ABDOMINAL PAIN",
      "ABRASIONS BRUISES ETC",
      "ACCIDENT UNKNOWN INJURIES",
      "ACCIDENT WITH INJURIES",
      "ALARM - MEDICAL",
      "ALLERGIC REACTIONS",
      "ARCING WIRES DOWN POWER LINES",
      "ARMED ROBBERY",
      "ASLTWI",
      "ASSIST BOATER",
      "ATSUIC",
      "BACK PAIN (NON TRAUMATIC)",
      "BOLO",
      "BLEEDING OR HEMORRHAGING",
      "BRUSH FIRE",
      "BURNING COMPLAINT",
      "CAR FIRE",
      "CARBON MONOXIDE ALARM",
      "CARDIAC ARREST",
      "CHEST PAIN",
      "CHILD OR ANIMAL LOCKED IN CAR",
      "CITIZN",
      "DIABETIC PROBLEMS",
      "DIFFICULTY BREATHING",
      "FALLS AND RELATED INJURIES",
      "FDBACK",
      "FIGHT IN PROGRESS",
      "FIRE ALARM",
      "FIRE DEPT COMMUNITY RELATIONS",
      "FIRE MUTUAL AID",
      "FIRE OTHER NOT LISTED",
      "FRACTURES OR BROKEN BONES",
      "HAZARDOUS MATERIALS INCIDENT",
      "HEADAC",
      "HEART",
      "INFO",
      "INGEST POISONS OR TOXINS",
      "LIFT ASSIST",
      "MDSTBY",
      "MISSING PERSON ADULT-JUVENILE",
      "OBSTETRICS",
      "OVERDOSE",
      "PAIN",
      "POSSIBLE DOA",
      "PSYCHIATRIC PROBLEMS",
      "RESCUE MUTUAL AID",
      "ROBBERY",
      "SEIZURE",
      "SHOOTING",
      "SICK / ILL OR RESCUE",
      "SMELL OR ODOR OF SMOKE",
      "STROKE",
      "STRUCTURE FIRE",
      "SUICIDE",
      "SUSPIC",
      "UNCONSCIOUS OR FAINTING",
      "WIND"
  ));
}