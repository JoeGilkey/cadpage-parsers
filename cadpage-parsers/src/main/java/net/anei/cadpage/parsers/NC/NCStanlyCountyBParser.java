package net.anei.cadpage.parsers.NC;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.SplitMsgOptions;
import net.anei.cadpage.parsers.SplitMsgOptionsCustom;
import net.anei.cadpage.parsers.dispatch.DispatchOSSIParser;


public class NCStanlyCountyBParser extends DispatchOSSIParser {
  
  public NCStanlyCountyBParser() {
    super(CITY_LIST, "STANLY COUNTY", "NC",
          "( UNIT ENROUTE ADDR CITY2 CALL! END " +
          "| CANCEL ADDR CITY_PLACE! X+? " +
          "| FYI? ID? CODE_CALL ADDR! ( END " + 
                                     "| APT CITY/Y! " +
                                     "| PLACE CITY/Y! " +
                                     "| CITY/Y! " +
                                     ") ( PLACE APT X+? | APT X+? | INFO1 | PLACE INFO1 | X X? | PLACE X+? ) " + 
          ") INFO/N+");
    setupMultiWordStreets("DR MARTIN LUTHER KING JR");
    addRoadSuffixTerms("CONNECTOR");
  }
  
  @Override
  public String getFilter() {
    return "CAD@stanlycountync.gov";
  }
  
  @Override
  public SplitMsgOptions getActive911SplitMsgOptions() {
    return new SplitMsgOptionsCustom();
  }

  @Override
  protected boolean parseMsg(String body, Data data) {
    if (body.contains(",Enroute,")) body = body.replace(',', ';');
    if (!super.parseMsg(body, data)) return false;
    
    // Eliminate some NCStanlyCountA alerts that get through
    if (data.strCity.length() == 0 && 
        (data.strCall.contains("/") || data.strDate.length() > 0)) return false;
    return true;
  }

  @Override
  public Field getField(String name) {
    if (name.equals("ENROUTE")) return new CallField("Enroute");
    if (name.equals("CITY2")) return new MyCity2Field();
    if (name.equals("CITY_PLACE")) return new MyCityPlaceField();
    if (name.equals("ID")) return new IdField("\\d{3,}");
    if (name.equals("CODE_CALL")) return new MyCodeCallField();
    if (name.equals("APT")) return new MyAptField();
    if (name.equals("INFO1")) return new InfoField("(?!DIST:).*[a-z].*");
    return super.getField(name);
  }
  
  private class MyCity2Field extends Field {
    @Override
    public void parse(String field, Data data) {
      String city = CITY_CODES.getProperty(field);
      if (city == null) abort();
      data.strCity = city;
    }
    
    @Override
    public String getFieldNames() {
      return "CITY";
    }
  }
  
  private static final Pattern APT_PTN = Pattern.compile("(?:ROOM|APT|LOT|RM) +(.*)|[A-Z]?\\d{1,4}[A-Z]?");
  
  private class MyCityPlaceField extends Field {
    @Override
    public void parse(String field, Data data) {
      if (field.length() >= 3) {
        String city = CITY_CODES.getProperty(field.substring(0,3));
        if (city != null) {
          data.strCity = city;
          field = field.substring(3).trim();
        }
        Matcher match = APT_PTN.matcher(field);
        if (match.matches()) {
          String apt = match.group(1);
          if (apt != null) field = apt;
          data.strApt = append(data.strApt, "-", field);
        } else {
          data.strPlace = field;
        }
      }
    }
    
    @Override
    public String getFieldNames() {
      return "CITY PLACE APT";
    }
  }
  
  private static final Pattern CODE_CALL_PTN = Pattern.compile("([A-Z0-9]+)-(\\S.*)");
  private class MyCodeCallField extends Field {
    @Override
    public void parse(String field, Data data) {
      Matcher match = CODE_CALL_PTN.matcher(field);
      if (match.matches()) {
        data.strCode = match.group(1);
        field = match.group(2);
      }
      data.strCall = field;
    }

    @Override
    public String getFieldNames() {
      return "CODE CALL";
    }
  }
  
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
      if (apt != null) field = apt;
      super.parse(field, data);
      return true;
    }
  }
  
  private static final String[] CITY_LIST = new String[]{
      
      // Cities
      "ALBEMARLE",
      "LOCUST",

      // Towns
      "BADIN",
      "MISENHEIMER",
      "NEW LONDON",
      "NORWOOD",
      "OAKBORO",
      "RED CROSS",
      "RICHFIELD",
      "STANFIELD",

      // Townships
      "ALMOND",
      "BIG LICK",
      "CENTER",
      "ENDY",
      "FURR",
      "HARRIS",
      "NORTH ALBEMARLE",
      "RIDENHOUR",
      "SOUTH ALBEMARLE",
      "TYSON",

      // Census-designated places
      "AQUADALE",
      "MILLINGPORT",

      // Other unincorporated communities
      "BEETSVILLE",
      "BIG LICK",
      "COTTONVILLE",
      "ENDY",
      "FINGER",
      "FROG POND",
      "KINGVILLE",
      "LAMBERT",
      "PALESTINE",
      "PALMERVILLE",
      "PLYLER",
      "PORTER",
      "RIDGECREST",
      "TUCKERTOWN",
  };
  
  private static final Properties CITY_CODES = buildCodeTable(new String[]{
      "ALB",  "ALBEMARLE",
      "BAD",  "BADIN",
      "GLH",  "GOLD HILL",
      "LOC",  "LOCUST",
      "MID",  "MIDLAND",
      "MIS",  "MISENHEIMER",
      "MTP",  "MT PLEASANT",
      "NEW",  "NEW LONDON",
      "NOR",  "NORWOOD",
      "OAK",  "OAKBORO",
      "RFD",  "RICHFIELD",
      "SFD",  "STANFIELD"
  });
}