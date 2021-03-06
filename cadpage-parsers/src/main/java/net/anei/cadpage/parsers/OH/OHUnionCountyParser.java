package net.anei.cadpage.parsers.OH;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.ReverseCodeSet;
import net.anei.cadpage.parsers.SplitMsgOptions;
import net.anei.cadpage.parsers.SplitMsgOptionsCustom;

public class OHUnionCountyParser extends FieldProgramParser {
  
  public OHUnionCountyParser() {
    super(CITY_LIST, "UNION COUNTY", "OH",
           "CALL ADDR/S ( CITY/Z ST_ZIP | CITY | ST_ZIP? ) X/Z+? X2! INFO/CS+");
  }
  
  @Override
  public String getFilter() {
    return "idnetworks@co.union.oh.us";
  }
  
  @Override
  public SplitMsgOptions getActive911SplitMsgOptions() {
    return new SplitMsgOptionsCustom(){
      @Override public boolean splitBlankIns() { return false; }
      @Override public boolean mixedMsgOrder() { return true; }
      @Override public int splitBreakLength() { return 130; }
      @Override public int splitBreakPad() { return 2; }
    };
  }

  @Override
  public boolean parseMsg(String subject, String body, Data data) {
    if (!body.endsWith(",")) data.expectMore = true;
    return parseFields(body.split(","), data);
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("CALL")) return new MyCallField();
    if (name.equals("ADDR")) return new MyAddressField();
    if (name.equals("CITY")) return new MyCityField();
    if (name.equals("ST_ZIP")) return new SkipField("OH(?: +\\d{5})?", true);
    if (name.equals("X2")) return new MyCrossField();
    if (name.equals("INFO")) return new MyInfoField();
    return super.getField(name);
  }
  
  private class MyCallField extends CallField {
    @Override
    public void parse(String field, Data data) {
      
      // If message is split into two messages and the trailing part does not
      // contain a comma, then putting them back together in the wrong order
      // results in a long call description ending in the real call description
      // which should also be rejected
      String call = CALL_LIST.getCode(field);
      if (call != null && call.length() < field.length()) abort();
      super.parse(field, data);
    }
  }
  
  private class MyAddressField extends AddressField {
    @Override
    public void parse(String field, Data data) {
      
      // If the message happens to end with a comma, and happens to get split
      // into two messages, then putting them back together in the wrong order
      // will push the call description into the address field.  Which should be rejected
      if (CALL_LIST.getCode(field) != null) abort();
      super.parse(field, data);
    }
  }
  
  private class MyCityField extends CityField {
    @Override
    public void parse(String field, Data data) {
      if (field.contains("/")) abort();
      super.parse(field, data);
    }
  }
  
  private class MyCrossField extends CrossField {
    @Override
    public boolean checkParse(String field, Data data) {
      int pt = field.indexOf("//");
      if (pt < 0) return false;
      String p1 = field.substring(0,pt).trim();
      String p2 = field.substring(pt+2).trim();
      if (!p1.equals(data.strCity)) super.parse(p1, data);
      super.parse(p2, data);
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
  }
  
  private class MyInfoField extends InfoField {
    @Override
    public void parse(String field, Data data) {
      if (data.strCity.startsWith(field)) return;
      super.parse(field, data);
    }
  }
  
  @Override
  public CodeSet getCallList() {
    return CALL_LIST;
  }

  private static final ReverseCodeSet CALL_LIST = new ReverseCodeSet(
      "911 FOLLOW UP",
      "911 UNKNOWN",
      "ABDOMINAL PAIN/PROBLEMS",
      "ADULT CPR",
      "AIRCRAFT CRASH",
      "ALARM DROP",
      "ALLERGIC REACTION",
      "ATTEMPTED SUICIDE",
      "ASSAULT",
      "ASSIST OTHER UNIT",
      "BACK PAIN",
      "BLEEDING",
      "BREATHING/ RECENT OPEN HEART",
      "BURGLARY IN PROGRESS",
      "BURNS",
      "CARBON MONOXIDE ALARM",
      "CARBON MONOXIDE INHALATION",
      "CHECK WELL BEING",
      "CHEST PAINS",
      "CHOKING",
      "COMMERCIAL FIRE",
      "COMMERCIAL FIRE ALARM",
      "CPR IN PROGRESS",
      "DEAD ON ARRIVAL",
      "DIABETIC PROBLEMS",
      "DIFFICULTY BREATHING",
      "DISABLED VEHICLE",
      "DISPUTE",
      "DOG BITE",
      "DOMESTIC",
      "DRUGS",
      "DUMPSTER FIRE",
      "DRUNK",
      "EXPLOSION",
      "EYE PROBLEMS/INJURY",
      "FALL/BACK INJURY",
      "FATAL CRASH",
      "FIRE",
      "FIRE ALARM",
      "FRACTURES",
      "GENERAL ILLNESS",
      "GRASS FIRE",
      "HAZARDOUS SPILL",
      "HEAD/NECK/SPINE INJURY",
      "HEADACHE",
      "HEART PROBLEMS",
      "HOSPITAL TRANSPORT",
      "INDUSTRIAL/MACHINE ACCIDENT",
      "INFANT CPR",
      "INJURY CRASH",
      "LACERATIONS",
      "LEFT SIDE",
      "LIFT ASSIST",
      "MEDICAL ALARM",
      "MENTAL HEALTH",
      "MISCELLANEOUS",
      "NATURAL GAS/PROPANE LEAK",
      "NATURE UNKNOWN",
      "NUISANCE CONTROL BURN",
      "OVERDOSE",
      "PERSON WITH A KNIFE",
      "POISONING",
      "POLE FIRE",
      "PREGNANCY/CHILDBIRTH",
      "PROPERTY DAMAGE CRASH",
      "ROAD HAZARD",
      "ROBBERY",
      "SEIZURES",
      "SERVICE RUN",
      "SICK PERSON",
      "SKATEBOARDERS",
      "SPECIAL DETAIL-NON SPECIFIC",
      "STABBING",
      "STROKE",
      "SUSPICIOUS CIRCUMSTANCES",
      "SUSPICIOUS PERSON",
      "THREATENED SUICIDE",
      "TRANSFORMER FIRE",
      "TRAUMATIC INJURY",
      "UNCONSCIOUS/ FAINTING",
      "UNKNOWN PROBLEM (MEDICAL)",
      "VEHICLE FIRE",
      "WALK IN",
      "WATER RESCUE",
      "WIRES DOWN"
  );
  
  private static final String[] CITY_LIST = new String[]{
    
    // Cities
    "DUBLIN",
    "MARYSVILLE",
    
    // Villages
    "MAGNETIC SPRINGS",
    "MILFORD CENTER",
    "PLAIN CITY",
    "RICHWOOD",
    "UNIONVILLE CENTER",
    
    // Unincorporated communities
    "ALLEN CENTER",
    "ARNOLD",
    "BRIDGEPORT",
    "BROADWAY",
    "BYHALIA",
    "CHUCKERY",
    "CLAIBOURNE",
    "DIPPLE",
    "ESSEX",
    "IRWIN",
    "JEROME",
    "LUNDA",
    "NEW CALIFORNIA",
    "NEW DOVER",
    "PEORIA",
    "PHARISBURG",
    "POTTERSBURG",
    "RAYMOND",
    "SOMERSVILLE",
    "WATKINS",
    "WOODLAND",
    "YORK CENTER",
    
    // Townships
    "ALLEN TWP",
    "CLAIBOURNE TWP",
    "DARBY TWP",
    "DOVER TWP",
    "JACKSON TWP",
    "JEROME TWP",
    "LEESBURG TWP",
    "LIBERTY TWP",
    "MILLCREEK TWP",
    "PARIS TWP",
    "TAYLOR TWP",
    "UNION TWP",
    "WASHINGTON TWP",
    "YORK TWP"
  };
}
