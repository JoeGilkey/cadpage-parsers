package net.anei.cadpage.parsers.NC;

import net.anei.cadpage.parsers.GroupBestParser;
import net.anei.cadpage.parsers.GroupBlockParser;


public class NCNashCountyParser extends GroupBestParser {
  
  public NCNashCountyParser() {
    super(new NCNashCountyAParser(), new NCNashCountyBParser());
  }
}
