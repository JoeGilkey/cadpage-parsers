package net.anei.cadpage.parsers.OR;

import net.anei.cadpage.parsers.GroupBestParser;

/**
 * Washington County, OR
 * Also Clackamas County
 */
public class ORWashingtonCountyParser extends GroupBestParser {
  public ORWashingtonCountyParser() {
    super(new ORWashingtonCountyAParser(), 
          new ORWashingtonCountyBParser(), 
          new ORWashingtonCountyCParser(),
          new ORWashingtonCountyDParser());
  }
  
  static final String[] CITY_LIST = new String[]{
    "ALOHA",
    "AMITY",
    "ARIEL",
    "AUMSVILLE",
    "AURORA",
    "BANKS",
    "BEAVERCREEK",
    "BINGEN",
    "BORING",
    "BRIGHTWOOD",
    "BRIDAL VEIL",
    "BEAVERTON",
    "BUXTON",
    "CANBY",
    "CARLTON",
    "CASCADE LOCKS",
    "CLACKAMAS",
    "COLTON",
    "CORNELIUS",
    "CORBET",
    "DALLESPORT",
    "DAMASCUS",
    "DAYTON",
    "DETROIT",
    "DUFUR",
    "DUNDEE",
    "EAGLE CREEK",
    "ESTACADA",
    "FAIRVIEW",
    "FOREST GROVE",
    "GALES CREEK",
    "GASTON",
    "GATES",
    "GERVAIS",
    "GLADSTONE",
    "GOVERNMENT CAMP",
    "GRESHAM",
    "HAPPY VALLEY",
    "HILLSBORO",
    "HOOD RIVER",
    "HUBBARD",
    "IDANHA",
    "INDEPENDENCE",
    "JEFFERSON",
    "KEIZER",
    "LAFAYETTE",
    "LAKE OSWEGO",
    "LYLE",
    "LYONS",
    "MANNING",
    "MARYLHURST",
    "MAUPIN",
    "MCMINNVILLE",
    "MILL CITY",
    "MILWAUKIE",
    "MOLALLA",
    "MOSIER",
    "MOUNT ANGEL",
    "MT HOOD PARKDALE",
    "MULINO",
    "NEWBERG",
    "NORTH PLAINS",
    "ODELL",
    "OREGON CITY",
    "OC" , "OREGON CITY",
    "ORIENT",
    "PORTLAND",
    "RHODODENDRON",
    "RICKREALL",
    "RIDGEFIELD",
    "SALEM",
    "SANDY",
    "SCAPPOOSE",
    "SCOTTS MILLS",
    "SHERIDAN",
    "SEASIDE",
    "SHERWOOD",
    "SILVERTON",
    "STAYTON",
    "STEVENSON",
    "SAINT PAUL",
    "SUBLIMITY",
    "THE DALLES",
    "TIGARD",
    "TILLAMOOK",
    "TIMBER",
    "TROUTDALE",
    "TUALATIN",
    "TURNER",
    "TYGH VALLEY",
    "UNDERWOOD",
    "US FOREST SVC",
    "VANCOUVER",
    "VERNONIA",
    "WARM SPRINGS",
    "WELCHES",
    "WEST LINN",
    "WHITE SALMON",
    "WILSONVILLE",
    "WOODBURN",
    "YAMHILL",
    "RIVER FEATURES"
  };

}
