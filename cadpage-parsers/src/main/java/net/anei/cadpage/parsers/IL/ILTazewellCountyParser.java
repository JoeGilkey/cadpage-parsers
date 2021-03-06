package net.anei.cadpage.parsers.IL;

import java.util.Properties;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchOSSIParser;


public class ILTazewellCountyParser extends DispatchOSSIParser {
  
  public ILTazewellCountyParser() {
    super(CITY_CODES, "TAZEWELL COUNTY", "IL",
         "( CANCEL ADDR CITY! " +
         "| FYI SRC DATETIME CALL ADDR! CITY? X X ) INFO+");
  }
  
  @Override
  public String getFilter() {
    return "cad@tazewell911.com";
  }
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    if (subject.length() > 0 && body.startsWith("CAD:")) {
      body = "CAD:" + subject + ": " + body.substring(4);
    } else if (body.startsWith("FYI:") || body.startsWith("Update:") || body.startsWith("CANCEL")) {
      body = "CAD:" + body;
    }
    return super.parseMsg(body, data);
  }

  @Override
  public Field getField(String name) {
    if (name.equals("SRC")) return new SourceField("[A-Z]{3,4}", true);
    if (name.equals("DATETIME")) return new DateTimeField("\\d\\d/\\d\\d/\\d{4} \\d\\d:\\d\\d:\\d\\d", true);
    return super.getField(name);
  }

  private static Properties CITY_CODES = buildCodeTable(new String[]{
      "AR", "ARMINGTON",
      "BA", "BARTONVILLE",
      "CR", "CREVE COEUR",
      "CU", "CUBA",
      "DA", "DANVERS",
      "DC", "DEER CREEK",
      "DE", "DELAVAN",
      "EM", "EMDEN",
      "EP", "EAST PEORIA",
      "EU", "EUREKA",
      "GO", "GOODFIELD",
      "GR", "GROVELAND",
      "GT", "GERMANTOWN HILLS",
      "GV", "GREEN VALLEY",
      "HO", "HOPEDALE",
      "MA", "MACKINAW",
      "ME", "METAMORA",
      "MH", "MARQUETTE HEIGHTS",
      "MI", "MINIER",
      "MN", "MANITO",
      "MO", "MORTON",
      "NP", "NORTH PEKIN",
      "PE", "PEKIN",
      "PH", "PEORIA HEIGHTS",
      "PO", "PEORIA",
      "SB", "SPRING BAY",
      "SJ", "SAN JOSE",
      "SP", "SOUTH PEKIN",
      "ST", "STANFORD",
      "TR", "TREMONT",
      "WA", "WASHINGTON"
 
  });
}
