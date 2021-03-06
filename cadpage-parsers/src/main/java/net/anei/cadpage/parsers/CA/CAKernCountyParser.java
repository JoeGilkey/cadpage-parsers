package net.anei.cadpage.parsers.CA;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

  public class CAKernCountyParser extends FieldProgramParser {

    public CAKernCountyParser() {
      super("KERN COUNTY", "CA", 
            "ADDR X! INFO/N+");
      setupCallList(CALL_LIST);
      setupMultiWordStreets(
          "CHINA LAKE",
          "CUMMINGS VALLEY",
          "HORACE MANN",
          "KING ARTHUR",
          "MT VERNON",
          "OFFICE PARK",
          "SANTA MARIA"
      );
    }
    
    @Override
    public String getFilter() {
      return "Dispatch@co.kern.ca.us,Dispatch@kerncountyfire.org";
    }
    
    private static final Pattern TMT_PTN = Pattern.compile("\\bTMT\\b", Pattern.CASE_INSENSITIVE);
    
    @Override
    public String adjustMapAddress(String addr) {
      addr = TMT_PTN.matcher(addr).replaceAll("TWENTY MULE TEAM");
      return super.adjustMapAddress(addr);
    }
    
    @Override
    public boolean parseMsg(String subject, String body, Data data) {
      if (!subject.equals("!")) return false;
      return parseFields(body.split("\n"), data);
    }

    @Override
    public Field getField(String name) {
      if (name.equals("ADDR")) return new MyAddressField();
      if (name.equals("X")) return new MyCrossField();
      return super.getField(name);
    }
    
    private static final Pattern CALL_ADDR_MAP_PTN = Pattern.compile("([A-Z][A-Z0-9]+)([- ]+)(.*) ([A-Z]{1,2}\\d+(?:-\\d+)?+(?:-[A-Z])?) *(.*)"); 
    private class MyAddressField extends AddressField {
      @Override
      public void parse(String field, Data data) {
        Matcher match = CALL_ADDR_MAP_PTN.matcher(field);
        if (!match.matches()) abort();
        data.strCode = match.group(1);
        String delim = match.group(2);
        field = match.group(3).trim();
        data.strMap = match.group(4);
        data.strPlace = match.group(5);
        
        int pt = field.lastIndexOf(',');
        if (pt >= 0) {
          data.strCity = field.substring(pt+1).trim();
          field = field.substring(0,pt).trim();
        }
        
        pt = field.indexOf(" - ");
        if (pt >= 0) {
          String place = field.substring(0,pt).trim();
          field = field.substring(pt+3).trim();
          if (!data.strPlace.startsWith(place)) {
            if (place.startsWith(data.strPlace)) {
              data.strPlace = place;
            } else {
              data.strPlace = append(place, " - ", data.strPlace);
            }
          }
        }
        
        field = field.replace('@', '&');
        StartType st;
        int flags;
        if (delim.contains("-")) {
          st = StartType.START_CALL;
          flags = FLAG_START_FLD_REQ;
        } else {
          st = StartType.START_ADDR;
          flags = 0;
        }
        parseAddress(st, flags | FLAG_RECHECK_APT | FLAG_ANCHOR_END, field, data);
        if (st == StartType.START_ADDR) {
          data.strCall = convertCodes(data.strCode, CALL_CODES);
        }
      }
      
      @Override
      public String getFieldNames() {
        return "CODE CALL ADDR APT CITY MAP PLACE";
      }
    }
    
    private class MyCrossField extends CrossField {
      @Override
      public void parse(String field, Data data) {
        if (field.startsWith("No Cross Streets Found")) {
          field = field.substring(22).trim();
          if (field.length() == 0) return;
          if (!field.startsWith("X ")) {
            data.strPlace = append(data.strPlace, " - ", field);
            return;
          }
          field = field.substring(2).trim();
        }
        String cross = "";
        int pt = field.lastIndexOf(',');
        if (pt >= 0) {
          cross = field.substring(0,pt).trim();
          field = field.substring(pt+1).trim();
        }
        parseAddress(StartType.START_ADDR, FLAG_ONLY_CROSS, field, data);
        data.strCross = append(cross, ", ", data.strCross);
        data.strPlace = append(data.strPlace, " - ", getLeft());
      }
      
      @Override
      public String getFieldNames() {
        return "X PLACE";
      }
    }
    
    private static final Properties CALL_CODES = buildCodeTable(new String[]{
        "AE",             "ARSON EVENT",
        "AL1",            "ALERT 1",
        "AL2",            "ALERT 2",
        "AL3",            "ALERT 3",
        "AMB2",           "AMBULANCE ONLY CODE 2",
        "AMB3",           "AMBULANCE ONLY CODE 3",
        "AOD",            "ASSIST OTHER DEPARTMENT",
        "C2MA",           "CODE 2 MEDICAL AID",
        "CFA",            "COMMERCIAL FIRE ALARM",
        "CMA",            "CARBON MONOXIDE ALARM",
        "COMP",           "COMPLAINT",
        "EMD",            "EMD",                             // ???
        "FF",             "FLIGHT FOLLOWING",
        "FO",             "FIRE OUT",
        "FWK",            "FIREWORKS",
        "HC",             "HAZARDOUS CONDITION",
        "HM1",            "HAZ MAT 1",
        "HM2",            "HAZ MAT 2",
        "HM3",            "HAZ MAT 3",
        "IB",             "ILLEGAL BURNING",
        "MA",             "MEDICAL AID",
        "MAA",            "MEDICAL AID AIR",
        "MAE",            "MEDICAL AID ECHO RESPONSE",
        "MAEA",           "MEDICAL AID ECHO RESPONSE AIR",
        "MAR",            "MEDICAL AID REINFORCED",
        "NR",             "NO RESPONSE",
        "NTF",            "NOTIFICATION",
        "OCA",            "OUT OF COUNTY ASSIST",
        "OF",             "OUTSIDE FIRE",
        "ORX",            "OFF ROAD RESCUE",
        "PS",             "PUBLIC SERVICE",
        "RFA",            "RESIDENTIAL FIRE ALARM",
        "ROSS",           "OUT OF COUNTY OES REQUEST",
        "RRX",            "REINFORCED RESCUE",
        "RX",             "RESCUE",
        "SF",             "STRUCTURE FIRE",
        "SFR",            "STRUCTURE FIRE REINFORCED",
        "SI",             "SMOKE INVESTIGATION",
        "TC",             "TRAFFIC COLLISION",
        "TEST",           "TEST",
        "UTF",            "UNKNOWN TYPE FIRE",
        "Vegetation A",   "VEGETATION FIRE-URBAN",
        "Vegetation B",   "VEGETATION FIRE-METRO",
        "Vegetation C",   "VEGETATION FIRE-RIO BRAVO",
        "Vegetation D",   "VEGETATION FIRE-NORMAL",
        "Vegetation E",   "VEGETATION FIRE-SEASONAL",
        "Vegetation F",   "VEGETATION FIRE-HIGH",
        "Vegetation G",   "VEGETATION FIRE-EXTREME",
        "VF",             "VEHICLE FIRE",
        "VFR",            "VEHICLE FIRE REINFORCED",
        "VG",             "VEGETATION FIRE"
    });
    
    // Initialize with old call descriptions that have been changed 
    // in the latest tables
    private static final CodeSet CALL_LIST = new CodeSet(
        "ASSIST OTHER DEPT OR AMB",
        "FIRE OUT INVEST. / REPORT",
        "STRUCTURE FIRE / RESPONSE"
    );
    
    static {
      for (Object val : CALL_CODES.values()) {
        CALL_LIST.add((String)val);
      }
    }
  }
