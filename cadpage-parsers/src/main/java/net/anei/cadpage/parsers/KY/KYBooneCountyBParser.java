package net.anei.cadpage.parsers.KY;

public class KYBooneCountyBParser extends KYStatePoliceAParser {
  
  public KYBooneCountyBParser() {
    super("BOONE COUNTY");
  }
  
  @Override
  public String getLocName() {
    return "Boone County, KY";
  }
}
