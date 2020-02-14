package net.anei.cadpage.parsers.NC;


import java.util.regex.Pattern;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchSouthernParser;



public class NCHaywoodCountyParser extends DispatchSouthernParser {
  
  public NCHaywoodCountyParser() {
    super(null, "HAYWOOD COUNTY", "NC", DSFLG_ADDR | DSFLG_ADDR_TRAIL_PLACE2 | DSFLG_X | DSFLG_OPT_CODE | DSFLG_TIME | DSFLG_NO_INFO);
    removeWords("COVE", "PARK");
    setupCities(CITY_LIST);
    setupMultiWordStreets(MWORD_STREET_LIST);
    setupSaintNames("APTS");
  }

  @Override
  public String getFilter() {
    return "CAD@haywoodnc.net,4702193544";
  }
  
  @Override
  protected boolean parseMsg(String body, Data data) {
    body = stripFieldStart(body, "CAD:");
    if (!super.parseMsg(body, data)) return false;
    int pt = data.strCall.indexOf(' ');
    if (pt >= 0) {
      data.strSupp = data.strCall.substring(pt+1).trim();
      data.strCall = data.strCall.substring(0,pt);
    }
    return true;
  }
  
  @Override
  public String adjustMapAddress(String addr) {
    return STONEY_PK_PTN.matcher(addr).replaceAll("STONEY PARK");
  }
  private static final Pattern STONEY_PK_PTN = Pattern.compile("\\bSTONEY +PK\\b", Pattern.CASE_INSENSITIVE);
  
  @Override
  public String adjustMapCity(String city) {
    if (city.equalsIgnoreCase("NEWPORT")) city = "HAYWOOD COUNTY";
    return city;
  }
  
  private static final String[] MWORD_STREET_LIST = new String[]{
      "ABBOTT MOORE",
      "ACRE WOOD",
      "ACRES VIEW",
      "ALLEN FARM",
      "ALLENS CREEK",
      "ANDERSON CREEK",
      "APPLE CREEK",
      "ARROWOOD ACRES",
      "AVERY PATRICK",
      "BALD CREEK",
      "BALSAM LOOKOUT",
      "BALSAM RIDGE",
      "BARN LOFT",
      "BARN VIEW",
      "BEAR HUNTER",
      "BELLE MEADE",
      "BERRY PATCH",
      "BETHEL VIEW",
      "BIG BRANCH",
      "BIG COVE",
      "BLACK BEAR",
      "BLACK BRANCH",
      "BLACK CAMP GAP",
      "BLAZING STAR",
      "BLUCHER HILL",
      "BLUE RIDGE",
      "BLUEBIRD HILL",
      "BOYD MOUNTAIN",
      "BRIAR PATCH",
      "BRUSHY CREEK",
      "BUCKEYE BRANCH",
      "BUCKEYE COVE",
      "BUNNY RUN",
      "BURNETTE COVE",
      "C E SMATHERS",
      "CABIN COVE",
      "CABIN CREEK",
      "CALHOUN RIDGE",
      "CALM CREEK",
      "CAMP BRANCH",
      "CAMP HOPE",
      "CANEBRAKE CREEK",
      "CANSADIE TOP",
      "CARING PLACE",
      "CAROLINA CONNECTOR",
      "CAROLINA MTN",
      "CASS KELL",
      "CASTLE CREEK",
      "CATHEY COVE",
      "CEDAR BEND",
      "CEDAR HILL",
      "CEDAR ROCK",
      "CENTER PIGEON",
      "CHAMBERS MTN",
      "CHAMBERS VIEW",
      "CHAPEL VIEW",
      "CHESTNUT FLATS",
      "CHESTNUT HILL",
      "CHESTNUT MOUNTAIN",
      "CHESTNUT PARK",
      "CHIMNEY RIDGE",
      "CLARK CEMETERY",
      "COFFEE BRANCH",
      "COLD CREEK",
      "COLEMAN MTN",
      "CONNECTOR 103",
      "CONNECTOR 104",
      "CONNECTOR 106",
      "CONNECTOR 112",
      "CONNECTOR 114",
      "CONNER RIDGE",
      "COUNTRY CLUB",
      "COUNTRY HAVEN",
      "COVE CREEK",
      "COYOTE HOLLOW",
      "COZY CREEK",
      "CRABTREE CHURCH",
      "CRABTREE FARM",
      "CRABTREE MTN",
      "CREDIT UNION",
      "CROSS CREEK",
      "CROSSROAD HILL",
      "CRYMES COVE",
      "DEE ANN",
      "DEER RUN",
      "DIX CREEK",
      "DOC GRAHAM",
      "DOCK RATCLIFFE",
      "DOWNS COVE",
      "DREAM MOUNTAIN",
      "DRY BRANCH",
      "DRY SPRING",
      "DUCKETT COVE",
      "DUTCH COVE",
      "EAGLES NEST",
      "EAST N MAIN",
      "EASTERN SKY",
      "EDEN BROOK",
      "ELM TREE",
      "ERNEST CARVER",
      "EVANS COVE",
      "EVERGREEN FALLS",
      "FALL CREEK",
      "FALLING SPRING",
      "FANNIE MAE",
      "FERGUSON CONNECTOR",
      "FIE TOP",
      "FILTER PLANT",
      "FINES CREEK",
      "FLAT ROCK",
      "FORD HOLLOW",
      "FOREST SERVICE",
      "FORK RANCH",
      "FOX RUN",
      "FRANCIS ORCHARD",
      "FRANK DAVIS",
      "FRANK MEHAFFEY",
      "FRIENDLY ACRES",
      "FROG POND",
      "GADDIS BRANCH",
      "GEORGE SUTTON",
      "GIBSON BRANCH",
      "GINSENG HOLLOW",
      "GLADE MOUNTAIN",
      "GLEN MEADOW",
      "GOLD FINCH",
      "GOLDEN WILLOW",
      "GRASSY BALD",
      "GREEN PASTURE",
      "GREEN VALLEY",
      "GROUNDHOG HILL",
      "HALL TOP",
      "HAPPY ACRES",
      "HARLEY CREEK",
      "HARLEY DAVIDSON",
      "HAWK HAVEN",
      "HAYNES HILL",
      "HAYSTACK HILL",
      "HEMLOCK HILL",
      "HEMLOCK SPRINGS",
      "HENRY HOLLOW",
      "HENSON COVE",
      "HICKORY NUT",
      "HIDDEN VALLEY",
      "HIGH ACRES",
      "HIGH COUNTRY",
      "HIGH HOPES",
      "HIGH RIDGE",
      "HILL N DALE",
      "HILL VIEW",
      "HILLTOP FARM",
      "HOBBLE HILL",
      "HOME PLACE",
      "HORSE COVE",
      "HORSECART PATH",
      "HOWARD VALLEY",
      "HOWELL MILL",
      "HUNGRY CREEK",
      "HURRICANE RIDGE",
      "HYATT CREEK",
      "HYDER MOUNTAIN",
      "INDIAN SPRINGS",
      "IRON DUFF",
      "IVY BRANCH",
      "J F MORRIS",
      "J FARM",
      "J R SAYLES",
      "JACK PINE",
      "JACKSON POND",
      "JAMES HILL",
      "JENNY GAP",
      "JESS COVE",
      "JIMS COVE",
      "JODY COVE",
      "JOE CARVER",
      "JOE PRESSLEY",
      "JOHN ROCK",
      "JOHN WAYNE",
      "JOHNS CREEK",
      "JOHNSON BRANCH",
      "JONATHAN CREEK CONNECTOR",
      "JONATHAN CREEK",
      "JONES COVE",
      "JUMPING BRANCH",
      "JUNALUSKA OAKS",
      "KIMS COVE",
      "LADY HUNTINGDON",
      "LAKE LOGAN",
      "LAST COYOTE",
      "LAUREL BRANCH",
      "LAUREL BROOK",
      "LAUREL RIDGE",
      "LEA PLANT",
      "LEE GRANT",
      "LEROY GEORGE",
      "LIBERTY CHURCH",
      "LINER COVE",
      "LINER CREEK",
      "LITTLE COVE",
      "LITTLE CREEK",
      "LITTLE MOUNTAIN",
      "LIVESTOCK MARKET",
      "LOCUST GROVE",
      "LOG CABIN",
      "LOGANS LEAP",
      "LONESOME PINE",
      "LONESOME PINES",
      "LONG BRANCH",
      "LOW GAP",
      "LUM BOONE",
      "LYNNE BIRCH",
      "MAPLE GROVE CH",
      "MAPLE KNOLL",
      "MAPLE SPRINGS",
      "MARSHALL HILL",
      "MARTINS CREEK",
      "MARY GALE",
      "MATTIE MARION",
      "MAUNEY COVE",
      "MAX PATCH",
      "MAYAPPLE MOUNTAIN",
      "MCELROY COVE",
      "MEADOW VIEW",
      "MENDEN HALL",
      "MERIDA GAP",
      "METCALF MOUNTAIN",
      "MILL CREEK",
      "MILL WHISTLE",
      "MINGUS COVE",
      "MINGUS HILL",
      "MONTE VISTA",
      "MOODY FARM",
      "MORNING DEW",
      "MORNING GLORY",
      "MORNING STAR",
      "MOUNT STERLING",
      "MOUNT VALLEY",
      "MOUNTAIN LAKE",
      "MOUNTAIN VIEW",
      "MUNDY FIELD",
      "MUSTANG ALLEY",
      "NARROW GAUGE",
      "NATIVE TROUT",
      "NED COVE",
      "NOLAND GAP",
      "OAK PARK",
      "ORION DAVIS",
      "OSBORNE FARM",
      "OWL RIDGE",
      "OXNER COVE",
      "PANTHER CREEK",
      "PARADISE MTN",
      "PEBBLE BROOK",
      "PIGEON GAP",
      "PIGEON VALLEY",
      "PINEY GROVE",
      "PINEY MOUNTAIN",
      "PINEY RIDGE",
      "PINK LAUREL",
      "PISGAH CREEK",
      "PISGAH MOUNTAIN",
      "PLEASANT HILL",
      "PLOTT CREEK",
      "PLOTT FARM",
      "PLOTT VALLEY",
      "POINT OF VIEW",
      "POISON IVY",
      "POLYANNA CREEK",
      "PRECIOUS METAL",
      "PUMP HOUSE",
      "PUTNAM PHILLIPS",
      "RABBIT SHADOW",
      "RABBIT SKIN",
      "RACKING HORSE",
      "RADIO HILL",
      "RAINY BRANCH",
      "RAMBLING RIDGE",
      "RATCLIFF COVE",
      "RATTLESNAKE BRANCH",
      "RAY MEMORIAL GARDENS HALE",
      "RED BANK",
      "RED FOX",
      "RED MAPLE",
      "REED COVE",
      "REED CREEK",
      "REUBEN BRANCH",
      "REYNOLDS SCHOOL",
      "RICE COVE",
      "RICH COVE",
      "RICHLAND CREEK",
      "RIDDLE COVE",
      "RIDING HORSE",
      "RIVER POINT",
      "ROBERTS SHOP",
      "ROBINS NEST",
      "ROCKY BRANCH",
      "ROCKY HOLLOW",
      "ROCKY LANE",
      "ROGERS COVE",
      "ROGERS HILL",
      "ROLLING ACRES",
      "ROSE GARDEN",
      "ROTH STREAM",
      "ROUND ROCK",
      "RUMBLING GAP",
      "RUSH FORK",
      "RUSTIC HEIGHTS",
      "RUSTIC RIDGE",
      "RYAN RIDGE",
      "SANDY BOTTOMS",
      "SENG BRANCH",
      "SERENITY MTN",
      "SHADY BROOK",
      "SHALLOW CREEK",
      "SHANDY DUSTY",
      "SHARP MOUNTAIN",
      "SHELTON COVE",
      "SILO RIDGE",
      "SILVER BLUFF",
      "SILVERS COVE",
      "SKI LODGE",
      "SLEEPY HOLLOW",
      "SMATHERS COVE",
      "SMITH FARM",
      "SMOKEY COVE",
      "SMOKEY MEADOWS",
      "SMOKEY PARK",
      "SNEAKING CREEK",
      "SNOW FLAKE",
      "SNOW WHITE",
      "SOCO ACRES",
      "SOLITARY MEADOW",
      "SORRELLS COVE",
      "SPY ROCK",
      "SQUAW RIDGE",
      "SQUIRREL HILL",
      "STAMEY COVE",
      "STILL POND",
      "STILLHOUSE COVE",
      "STONE HAVEN",
      "SUGAR PLUM",
      "SULPHUR SPRINGS",
      "SUMMER PLACE",
      "SUNSET RIDGE",
      "SUTTON TOWN",
      "SWISS VALLEY",
      "TALL TIMBER",
      "TATER HILL FARM",
      "TEST FARM",
      "THE HARD",
      "THE HAWKS NEST",
      "THE HIGH",
      "THOMPSON COVE",
      "THUNDER RIDGE",
      "TIGER LILY",
      "TIMBER RIDGE",
      "TIMBERLANE CONNECTOR",
      "TINKER BELL",
      "TOBACCO BARN",
      "TOWN CENTER",
      "TRI LAKES",
      "TRIPLE CREEK",
      "TROUT POND",
      "TUMBLING FORK",
      "TURKEY CREEK",
      "TURNER BRANCH",
      "TUSCOLA SCHOOL",
      "TWIN BROOK",
      "TWIN MAPLES",
      "TWIN OAKS",
      "UNDERWOOD COVE",
      "UP OVER",
      "UTAH MOUNTAIN",
      "VALLEY BROOK",
      "VALLEY HILL",
      "WADY BRANCH",
      "WAGON WHEEL",
      "WAITSEL CONNECTOR",
      "WALMART PLAZA",
      "WALNUT CREEK",
      "WALNUT RIDGE",
      "WALNUT TRAIL",
      "WELLS EVENTS",
      "WESLEY CREEK",
      "WHISTLING OAK",
      "WHITE OAK",
      "WHY WORRY",
      "WILD ROSE",
      "WILKINSON PASS",
      "WILLIE BILLS",
      "WILLIS COVE",
      "WILLOW TREE",
      "WILLS COVE",
      "WILSON COVE",
      "WINDSWEPT RIDGE",
      "WINDY HILL",
      "WOLF RIDGE",
      "WOODLAND CHURCH",
      "WORLEY COVE",
      "WOUNDED KNEE",
      "YATES COVE",
      "YELLOW PATCH",
      "YONDER MOUNTAIN",
      "ZEMRY CALDWELL"
  };
  
  private static String[] CITY_LIST = new String[]{
    "CANTON",
    "CLYDE",
    "LAKE JUNALUSKA",
    "MAGGIE VALLEY",
    "NEWPORT",
    "WAYNESVILLE",
    "WEST CANTON",
    
    "BUNCOMBE"
  };
  
}