package net.anei.cadpage.parsers.IN;

import net.anei.cadpage.parsers.GroupBestParser;

public class INHancockCountyParser extends GroupBestParser {
  
  public INHancockCountyParser() {
    super(new INHancockCountyAParser(), new INHancockCountyBParser());
  }

}
