package net.anei.cadpage.parsers.KY;

public class KYRobertsonCountyParser extends KYStatePoliceAParser {
  
  public KYRobertsonCountyParser() {
    super("ROBERTSON COUNTY");
  }
  
  @Override
  public String getLocName() {
    return "Robertson County, KY";
  }
}
