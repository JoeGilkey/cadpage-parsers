package net.anei.cadpage.parsers.MI;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

public class MIOaklandCountyCParser extends FieldProgramParser {

  public MIOaklandCountyCParser() {
    this("OAKLAND COUNTY", "MI");
  }

  MIOaklandCountyCParser(String defCity, String defState) {
    super(defCity, defState, 
          "URL CODE ADDR ADDR2? APT? PLACE+? PHONE END");
  }
  
  @Override
  public String getAliasCode() {
    return "MIOaklandCounty";
  }
  
  @Override
  public String getFilter() {
    return "CAD_Do-Not-Reply@oakgov.com";
  }
  
  @Override
  public boolean parseMsg(String subject, String body, Data data) {
    int pt = body.indexOf(" /");
    if (pt < 0) return false;
    String[] flds = body.substring(pt).trim().split("/");
    flds[0] = body.substring(0,pt).trim();
    if (!parseFields(flds, data)) return false;
    data.strCall = convertCodes(data.strCode, CALL_CODES);
    return true;
  }
  
  @Override
  public String getProgram() {
    return super.getProgram().replace("CODE", "CODE CALL");
  }
  
  @Override
  public Field getField(String name) {
    if (name.equals("URL")) return new InfoUrlField("https://apps.clemis.org/.*", true);
    if (name.equals("ADDR2")) return new MyAddress2Field();
    if (name.equals("APT")) return new MyAptField();
    if (name.equals("PLACE")) return new MyPlaceField();
    if (name.equals("PHONE")) return new PhoneField("CALLBK= *(.*)", true);
    return super.getField(name);
  }
  
  private static final Pattern ADDR_STREET_NO_PTN = Pattern.compile("[-0-9]+ +.*");
  private class MyAddress2Field extends AddressField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override 
    public boolean checkParse(String field, Data data) {
      
      // Treat as send part of intersection if the first address does not
      // appear to have street number.  The checkAddress call is there to
      // prevent 124 ST from being treated as a complete address
      if (!ADDR_STREET_NO_PTN.matcher(field).matches() && 
          checkAddress(data.strAddress) != STATUS_STREET_NAME) return false;
      data.strAddress = append(data.strAddress, " & ", field);
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
  }
  
  private static final Pattern APT_PTN = Pattern.compile("(?:APT|SUITE|LOT|RM|ROOM) *(.*)", Pattern.CASE_INSENSITIVE);
  private class MyAptField extends AptField {
    
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      Matcher match = APT_PTN.matcher(field);
      if (!match.matches()) return false;
      String apt = match.group(1);
      if (!apt.equals(data.strApt)) data.strApt = append(data.strApt,"-", apt);
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
  }
  
  private class MyPlaceField extends PlaceField {
    @Override
    public void parse(String field, Data data) {
      
      // Could be a place name or could be a cross street
      if (field.startsWith("X OF ")) {
        data.strCross = append(data.strCross, " / ", field.substring(5).trim());
        return;
      }
      
      if (isValidAddress(field)) {
        data.strCross = append(data.strCross," / ", field);
        return;
      }
      
      data.strPlace = append(data.strPlace, "/", field);
    }
    
    @Override
    public String getFieldNames() {
      return "X PLACE";
    }
  }
  
  private static final Properties CALL_CODES = buildCodeTable(new String[]{
        "AC",         "ASSIST CITIZEN",
        "BOX_COM",    "BOX ALARM COMMERCIAL",
        "BOX_RESD",   "BOX ALARM RESIDENTIAL",
        "BT",         "BOMB THREAT",
        "BURNIN",     "BURNING COMPLAINT",
        "CO",         "CARBON MONOXIDE",
        "CR",         "CONFINED SPACE RESCUE",
        "DC",         "DUTY CALL",
        "EXPLSN",     "EXPLOSION",
        "FAC",        "FIRE ALARM COMMERCIAL",
        "FAIRLG",     "LARGE AIRCRAFT: CRASH/FIRE",
        "FAIRSM",     "SMALL AIRCRAFT CRASH/FIRE",
        "FALARM",     "FIRE ALARM",
        "FAR",        "FIRE ALARM RESIDENTIAL",
        "FBOAT",      "BOAT FIRE",
        "FBOMB",      "BOMB: THREAT/STANDBY",
        "FBURNC",     "BURNING COMPLAINT",
        "FCOALM",     "CO ALARM",
        "FDUTYO",     "DO: CALL/NOTIFICATION",
        "FDUTYO",     "DO: CALL/NOTIFICATION",
        "FELEVA",     "ELEVATOR MALFUNCTION",
        "FELHZD",     "ELECTRICAL HAZARD",
        "FEXPLO",     "EXPLOSION",
        "FEXTRI",     "EXTRICATION",
        "FFUELS",     "FUEL SPILL",
        "FGASLK",     "NATURAL GAS LEAK",
        "FHAZMT",     "HAZ MAT: SPILL/LEAK/RUPTURE",
        "FINVEST",    "FIRE INVESTIGATION",
        "FLGHTN",     "LIGHTNING STRIKE",
        "FMUTAI",     "MUTUAL AID",
        "FODORS",     "ODOR: INSIDE/OUTSIDE",
        "FOTHER",     "FIRE: OTHER/MISC",
        "FOUTSI",     "OUTISDE FIRE: BRUSH/TRASH",
        "FPIACC",     "PIA/EXTRICATION",
        "FRESCU",     "RESCUE: ABOVE/BELOW GRADE",
        "FSMOKE",     "SMOKE VISIBLE OUTSIDE",
        "FSRVCE",     "ASSIST: POLICE/CITIZEN",
        "FSTRUC",     "STRUCTURE FIRE",
        "FSYSIM",     "FIRE SYSTEM IMPAIRMENT",
        "FSYSTR",     "FIRE SYSTEM TROUBLE",
        "FTRAIN",     "TRAIN: DERAILMENT/FIRE",
        "FTRANS",     "TRANSFORMER: EXPLOSION/FIRE",
        "FUEL",       "FUEL SPILL",
        "FULL_COM",   "FULL ALARM COMMERCIAL",
        "FULL_RES",   "FULL ALARM RESIDENTIAL",
        "FULL_RESD",  "FULL ALARM RESIDENTIAL",
        "FVEHIC",     "VEHICLE FIRE",
        "FWATER",     "RESCUE: WATER/ICE/FLOOD",
        "FWEATH",     "WEATHER: ALERT/STANDBY",
        "FWIRES",     "WIRES: DOWN/ARCING/SPARKING",
        "GASLK",      "GAS LEAK",
        "HR",         "HIGH ANGLE RESCUE",
        "HZ",         "HAZMAT",
        "LIFT",       "LIFT ASSIST",
        "MA ",        "MEDICAL ALARM",
        "MABAS",      "MABAS",
        "ME",         "MEDICAL (NO EMD)",
        "MEA",        "ALPHA",
        "MEB",        "BRAVO",
        "MEC",        "CHARLIE",
        "MED",        "DELTA",
        "MEE",        "ECHO",
        "MEO",        "OMEGA",
        "MET",        "MEDICAL TRANSFER",
        "MUTAID",     "MUTUAL AID",
        "OF",         "OUTSIDE FIRE",
        "OI",         "ODOR INVESTIGATION",
        "PA",         "POLICE ASSIST",
        "PI",         "INJURY ACCIDENT",
        "PLANE",      "PLANE INCIDENT",
        "RC",         "COMMERCIAL STRUCTURE FIRE",
        "RR",         "RESIDENTIAL STRUCTURE FIRE",
        "SMOKE",      "SMOKE INV (OUTSIDE)",
        "STILL RES",  "STILL ALARM RESIDENTIAL",
        "STILL_COM",  "STILL ALARM COMMERCIAL",
        "STILL_RESD", "STILL ALARM RESIDENTIAL",
        "TECHR",      "TECH RESCUE",
        "TRAIN",      "TRAIN INCIDENT",
        "TREE",       "DOWN TREE",
        "TRENCH",     "TRENCH RESCUE",
        "VF",         "VEHICLE FIRE",
        "WIRE",       "DOWN WIRES",

  });
}
