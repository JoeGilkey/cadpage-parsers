package net.anei.cadpage.parsers.OH;

import net.anei.cadpage.parsers.GroupBestParser;

/*
 * Logan County, OH
 */

public class OHLoganCountyParser extends GroupBestParser {
  
  public OHLoganCountyParser() {
    super(new OHLoganCountyAParser(), new OHLoganCountyBParser(), new OHLoganCountyCParser());
  }
}
