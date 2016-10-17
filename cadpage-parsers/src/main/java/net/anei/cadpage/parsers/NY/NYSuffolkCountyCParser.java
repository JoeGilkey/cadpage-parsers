package net.anei.cadpage.parsers.NY;


import java.util.regex.Pattern;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchRedAlertParser;


public class NYSuffolkCountyCParser extends DispatchRedAlertParser {
  
  private static final Pattern DIR_SLASH_BOUND = Pattern.compile("\\b([NSEW])/B\\b");
  
  public NYSuffolkCountyCParser() {
    super("SUFFOLK COUNTY","NY");
    setupMultiWordStreets(
        "BRIDGEHAMPTON SAG HARBOR",
        "CAPTAINS NECK",
        "COUNTRY CLUB",
        "CRANE NECK",
        "CROOKED HILL",
        "DUCK POND",
        "FAIRFIELD POND",
        "FISHERMANS BEACH",
        "HEALTH SCIENCES",
        "KELLIS POND",
        "KIMOGENOR POINT",
        "LITTLE NECK",
        "LOUIS KOSSUTH",
        "NASSAU POINT",
        "NORTH MAGEE",
        "NORTH SEA",
        "PAUMANACK VILLAGE",
        "PINE TREE",
        "POINTE MECOX",
        "SAGAPONACK MAIN",
        "SAGG MAIN",
        "SAND DUNE",
        "SCUTTLE HOLE",
        "SEBONIC INLET",
        "SNAKE HOLLOW",
        "TIMBER RIDGE",
        "VETERANS MEMORIAL",
        "WAINSCOTT MAIN",
        "WAINSCOTT STONE",
        "WILD GOOSE"
    );
  }
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    
    // Anything starting with TYPE: belongs to variant A
    if (body.startsWith("TYPE:")) return false;
    
    // Anything starting with three asterisks belongs to variant B
    if (body.startsWith("***")) return false;
    
    // They use a strange E/B convention
    return super.parseMsg(subject, DIR_SLASH_BOUND.matcher(body).replaceAll("$1B"), data);
  }
}
