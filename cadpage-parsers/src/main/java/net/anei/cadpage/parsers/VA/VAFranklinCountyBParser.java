package net.anei.cadpage.parsers.VA;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchSouthernParser;

/**
 * Franklin County, VA
 */
public class VAFranklinCountyBParser extends DispatchSouthernParser {
  
  public VAFranklinCountyBParser() {
    super(CITY_LIST, "FRANKLIN COUNTY", "VA", DSFLAG_BOTH_PLACE | DSFLAG_FOLLOW_CROSS | DSFLAG_LEAD_UNIT, "(?:CO|SQ)\\d+");
    setupMultiWordStreets(MWORD_STREET_LIST);
  }
      
  
  @Override
  public String getFilter() {
    return "911@FCSO.COM,911_DISPATCH@franklincountyva.gov";
  }
  
  @Override
  public int getMapFlags() {
    return MAP_FLG_PREFER_GPS;
  }
  
  @Override
  protected boolean parseMsg(String body, Data data) {
    body = stripFieldStart(body, "FCSO_911:");
    return super.parseMsg(body, data);
  }

  private static final String[] MWORD_STREET_LIST = new String[]{
    "ALLAN KINGERY",
    "ANGEL RIDGE",
    "ANGLE PLANTATION",
    "ASHPONE TAVERN",
    "BACK NINE",
    "BARNY BAY",
    "BAY SHORE",
    "BEARDS CREEK",
    "BEECH MOUNTAIN",
    "BENNETT PLACE",
    "BEVERLY HILLS",
    "BIG OAK",
    "BLACK ROCK",
    "BLACKWATER HILLS",
    "BLUE HERON",
    "BLUE HILLS",
    "BONBROOK MILL",
    "BOOKER T WASHINGTON",
    "BOON BERNARD",
    "BOONES MILL",
    "BRANDY OAK",
    "BRIAR RIDGE",
    "BRIAR SPRINGS",
    "BRICK CHURCH",
    "BRIDGEWATER GRANDE",
    "BRIDGEWATER POINTE",
    "BROOKS MILL",
    "BROOKS POINT",
    "BROWNS POINT",
    "BUFFALO RIDGE",
    "BURNT CHIMNEY",
    "BYRDS MILL",
    "CANVAS BACK",
    "CAROLINA SPRINGS",
    "CARVER LEE",
    "CATHEDRAL OAKS",
    "CHESTNUT ACORN",
    "CHESTNUT GROVE",
    "CHESTNUT HILL",
    "CHESTNUT MEADOWS",
    "CHESTNUT MOUNTAIN",
    "CHIGGER RIDGE",
    "CIRCLE CREEK",
    "CLARK HOLLOW",
    "CLUBHOUSE TOWER",
    "COLD SPRING",
    "COOKS KNOB",
    "COOL SPRING",
    "COOPERS COVE",
    "COOPERS MOUNTAIN",
    "CRAFTS FORD",
    "CRAZY HORSE",
    "CREST VIEW",
    "CROOKED OAK",
    "CROOKED RUN",
    "CROWELL GAP",
    "DAVIS MILL",
    "DILLONS MILL",
    "DOE RUN",
    "DOUBLE BRANCH",
    "DOUBLE OAKS",
    "DOVE FIELD",
    "DRY HILL",
    "DUDLEY AMOS",
    "DUDLEY CREEK",
    "DUSTY HILL",
    "DUTCH WIND",
    "EAGLE MOUNTAIN",
    "EAST CHURCH",
    "EAST COURT",
    "EASTER MOUNTAIN",
    "EDGEWOOD FARM",
    "ELLWOOD WRAY",
    "FACTORY HILL",
    "FAIRWAY OAKS",
    "FAIRY STONE PARK",
    "FANNY COOK",
    "FARM VIEW",
    "FERN RIDGE",
    "FERRUM MOUNTAIN",
    "FIVE MILE MOUNTAIN",
    "FLINT HILL",
    "FLINT RIDGE",
    "FOGGY BOTTOM",
    "FOGGY RIDGE",
    "FORK MOUNTAIN",
    "FOUR BRANCH",
    "FOUR CORNERS",
    "FOX CHASE",
    "FOX TRAIL",
    "FRYING PAN HOLLOW",
    "GILLS CREEK",
    "GILMER BRANCH",
    "GLEN OAK",
    "GOLDEN VIEW",
    "GOOSE DAM",
    "GRASSY HILL",
    "GRAVEYARD KNOB",
    "GREEN LEVEL",
    "GREEN MEADOW",
    "GREEN RIDGE",
    "GREEN VALLEY",
    "GRIFFITH HILL",
    "HALEY SCOTT",
    "HARMONY SCHOOL",
    "HAVEN RIDGE",
    "HAW PATCH",
    "HENRY SCHOOL",
    "HIGH MEADOWS",
    "HILL SIDE",
    "HILLCREST HEIGHTS",
    "HILLTOP VIEW",
    "HOLLAND FARM",
    "HORSESHOE POINT",
    "INDIAN ROCK",
    "IRON BRIDGE",
    "IRON RIDGE",
    "ISLAND POINTE",
    "JACKS MOUNTAIN",
    "JAMES ACRES",
    "JOHN ARTHUR",
    "JOHN BROWN",
    "JOHNNYS RIDGE",
    "JOURNEYS END",
    "JUBAL EARLY",
    "KAY FORK",
    "KEITHS PLACE",
    "KEMP FORD",
    "KERMIT HOLLOW",
    "KEY LAKEWOOD",
    "KING RICHARD",
    "KNOB RIDGE",
    "LAKEWATCH CENTER",
    "LAKEWOOD FOREST",
    "LAPRADE MILL",
    "LARKIN BRANCH",
    "LAUREL BLUFF",
    "LAWS HAVEN",
    "LAWS PARK",
    "LEANING OAK",
    "LIBERTY HALL",
    "LOG CABIN",
    "LONDON RIDGE",
    "LONG BRANCH HILL",
    "LONG ISLAND",
    "LOOKOUT POINTE",
    "LOST MOUNTAIN",
    "LOVELY VALLEY",
    "LUCY WADE",
    "MALLARD POINT",
    "MARIO FARM",
    "MARSHALL HILL",
    "MARVIN GARDENS",
    "MARY COGER",
    "MCKINLEY HILLS",
    "MEADOW BRANCH",
    "MEADOW BROOK",
    "MEADOW RIDGE",
    "MEADOW VIEW",
    "MIDDLE VALLEY",
    "MONTEGO BAY",
    "MONTGOMERY FARMS",
    "MORGANS FORK",
    "MOUNT ZION",
    "MOUNTAIN GAP",
    "MOUNTAIN VALLEY",
    "MUDDY FORK",
    "MULLINS COURT",
    "MURRAY HILL",
    "MURRAY KNOB",
    "NOLENS HILL",
    "NORTH CHURCH",
    "NORTH MAIN",
    "OAK GARDEN",
    "OAK VIEW",
    "OLE TAYLOR",
    "ORCHARD GROVE",
    "OXEN HILL",
    "PARADISE ACRES",
    "PARK PLACE",
    "PARK WAY",
    "PARKER RIDGE",
    "PEA RIDGE",
    "PEACEFUL VALLEY",
    "PENINSULA POINT",
    "PETERS PIKE",
    "PIGG RIVER",
    "PINE GROVE",
    "PLEASANT BREEZE",
    "PLEASANT HILL",
    "POPLAR BRANCH",
    "POPLAR COURT",
    "POPLAR COVE",
    "POTTERS CREEK",
    "POWDER CREEK",
    "POWER DAM",
    "POWER LINE",
    "PRILLAMAN SWITCH",
    "PROVIDENCE CHURCH",
    "QUEEN MOTHERS",
    "RAMBLING ROSE",
    "RED DIRT",
    "RED OAK",
    "RED VALLEY",
    "RIDGE MOUNTAIN",
    "RIVER CREEK",
    "RIVER RIDGE",
    "ROCK HILL",
    "ROCK LILY",
    "ROCKY GLEN",
    "ROCKY SHORE",
    "ROUND HILL",
    "RUNNETT BAG",
    "RUSTIC RIDGE",
    "SAILORS COVE",
    "SALEM SCHOOL",
    "SALLYS RIDGE",
    "SALTHOUSE BRANCH",
    "SANDY WOODY",
    "SAUNDERS FARM",
    "SCHOOL BOARD",
    "SCUFFLING HILL",
    "SHADY GROVE",
    "SHOOTING CREEK",
    "SHOPPERS PRIDE",
    "SIMMONS CREEK",
    "SIX MILE POST",
    "SMITH MOUNTAIN",
    "SMITH MT",
    "SNOW CREEK",
    "SOUTH MAIN",
    "SPANISH OAK",
    "SPINNAKER SAIL",
    "STANLEY BRANCH",
    "STAVE MILL",
    "STONEY MILL",
    "STONEY POINTE",
    "STRAWBERRY BANKS",
    "SUNNY FIELD",
    "SUNSET RIDGE",
    "SUTTON HOLLOW",
    "TAYLOR TYREE",
    "TAYLORS RIDGE",
    "TEEL CREEK",
    "THARP CREEK",
    "THOMPSON RIDGE",
    "THREE FORKS",
    "THREE OAKS",
    "THREE QUARTER POINT",
    "TIMBER LINE",
    "TIMBER RIDGE",
    "TOM BEAL",
    "TOMS KNOB",
    "TRIPPLE CREEK",
    "TRUMAN HILL",
    "TURKEY BRANCH",
    "TURNERS CREEK",
    "TWIN CREEKS",
    "TWIN OAKS",
    "VALLEY HIGH",
    "VALLEY VIEW",
    "VIC TYREE",
    "VILLAGE SPRINGS",
    "VILLAGE VIEW",
    "VIRGIL H GOODE",
    "WALNUT HILL",
    "WATERS EDGE",
    "WEBB MOUNTAIN",
    "WELCOME VALLEY",
    "WEST COLLEGE",
    "WHIPPOORWILL RIDGE",
    "WHISPERING CREEK",
    "WHITE OAK",
    "WHITE TAIL",
    "WILD GOOSE",
    "WILLOW CREEK",
    "WINDING WATERS",
    "WINDING WAY",
    "WINDSOR POINT",
    "WINDWARD POINTE",
    "WINDY OAKS",
    "WINDY RIDGE",
    "WOODLAND FOREST"
  };
  
  private static final String[] CITY_LIST = new String[]{

    // Cities
    "BOONES MILL",
    "ROCKY MOUNT",

    // Unincorporated communities
    "BENT MOUNTAIN",
    "CALLAWAY",
    "FERRUM",
    "GLADE HILL",
    "HALES FORD",
    "HARDY",
    "NORTH SHORE",
    "PENHOOK",
    "REDWOOD",
    "SNOW CREEK",
    "UNION HALL",
    "WESTLAKE CORNER",
    "WIRTZ",
    
    // Bedford County
    "BEDFORD",
    "BIG ISLAND",
    "CHAMBLISSBURG",
    "FOREST",
    "GOODE",
    "HARDY",
    "HUDDLESTON",
    "MONETA",
    "MONTVALE",
    "NEW LONDON",
    "STEWARTSVILLE",
    "THAXTON",
    
    // Floyd County
    "ALUM RIDGE",
    "BURKS FORK",
    "CARTHAGE",
    "CHECK",
    "CONNERS GROVE",
    "COPPER HILL",
    "COPPER VALLEY",
    "COURT HOUSE",
    "DUNCAN",
    "FLOYD",
    "HAYCOCK",
    "HEMLOCK",
    "HUFFVILLE",
    "INDIAN VALLEY",
    "LAUREL BRANCH",
    "LITTLE RIVER",
    "LOCUST VALLEY",
    "MABRY MILL",
    "MARTINSVILLE",
    "PIZARRO",
    "POFF",
    "SHELORS MILL",
    "SIMPSONS",
    "SMART",
    "SOWERS",
    "TERRYS FORK",
    "TURTLE ROCK",
    "UNION",
    "WILLIS",
    
    // Henry County
    "AXTON",
    "BASSETT",
    "CHATMOSS",
    "COLLINSVILLE",
    "FIELDALE",
    "HENRY",
    "HORSEPASTURE",
    "LAUREL PARK",
    "OAK LEVEL",
    "RIDGEWAY",
    "SANDY LEVEL",
    "SPENCER",
    "STANLEYTOWN",
    "VILLA HEIGHTS",
    
    // Patrick County
    "ARARAT",
    "CLAUDVILLE",
    "CRITZ",
    "FAIRYSTONE",
    "MAYBERRY",
    "MEADOWS OF DAN",
    "PATRICK SPRINGS",
    "PENNS STORE",
    "REYNOLDS HOMESTEAD",
    "RUSSELL CREEK",
    "STUART",
    "VESTA",
    "WOOLWINE",
    
    // Bachelors Hall
    "BACHELORS HALL",
    "BLAIRS",
    "BROSVILLE",
    "CHALK LEVEL",
    "CHATHAM",
    "CLIMAX",      //?
    "DRY FORK",
    "GRETNA",      //?
    "GRIT",
    "HURT",                //+
    "KEELING",
    "MOUNT AIRY",
    "MT HERMON",
    "PICKERAL'S CROSSING",
    "PITTSVILLE",
    "RENAN",
    "RINGGOLD",
    "SONANS",
    "STRAIGHTSTONE",
    "TIGHTSQUEEZE",
    "WHITTLES DEPOT",
    
    // Roanoke County
    "BACK CREEK",
    "BENT MOUNTAIN",
    "BONSACK",
    "CATAWBA",
    "CAVE SPRING",
    "CLEARBROOK",
    "FORT LEWIS",
    "GLENVAR",
    "HANGING ROCK",
    "HOLLINS",
    "MASONS COVE",
    "MOUNT PLEASANT",
    "OAK GROVE",
    "PENN FOREST",
    "POAGES MILL",
    "READ MOUNTAIN",
    "VINTON",


  };

}