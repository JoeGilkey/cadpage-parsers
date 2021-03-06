package net.anei.cadpage.parsers.dispatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

/*
 * ID DATE TIME CALL ADDR CITY
 *   PLACE X+ MAP INFO UNIT!
 *   PLACE X+ MAP INFO UNIT?
 *   PLACE X+ INFO UNIT?
 *   INFO+ UNIT! 
 */

public class DispatchA67Parser extends FieldProgramParser {
  
  public static final int A67_OPT_PLACE = 0x1;
  public static final int A67_OPT_CROSS = 0x2;
  private static final int A67_OPT_MAP = 0x4;
  public static final int A67_SINGLE_CALL = 0x8;
  
  private String prefix;
  private Pattern mapPtn;
  private Pattern unitPtn;
  
  private boolean singleCall;
  
  public DispatchA67Parser(String[] cityList, String defCity, String defState, int flags) {
    this(null, cityList, defCity, defState, flags, null, null);
  }
  
  public DispatchA67Parser(String[] cityList, String defCity, String defState, int flags, String mapPtn, String unitPtn) {
    this(null, cityList, defCity, defState, flags, mapPtn, unitPtn);
  }
  
  public DispatchA67Parser(String prefix, String[] cityList, String defCity, String defState, int flags, String mapPtn, String unitPtn) {
    super(cityList, defCity, defState, null);
    this.prefix = prefix;
    this.mapPtn = mapPtn == null ? null : Pattern.compile(mapPtn);
    boolean reqUnit = unitPtn != null && unitPtn.equals(".*");
    this.unitPtn = unitPtn == null || reqUnit ? null : Pattern.compile(unitPtn);
    StringBuilder sb = new StringBuilder("ID DATE/d TIME ( SELECT/A CALL ADDR CITY? | CALL ADDR/Z CITY | CALL CALL/L ADDR/Z CITY | CALL ADDR | CALL CALL2/L ADDR | CALL CALL/L ADDR | CALL ADDR ) ");

    int tmp = flags & (A67_OPT_PLACE|A67_OPT_CROSS);
    if (mapPtn != null) tmp |= A67_OPT_MAP;
    
    switch (tmp) {
    
    case A67_OPT_PLACE | A67_OPT_CROSS | A67_OPT_MAP:
      sb.append("( PLACE X/Z X/Z INFO MAP! | PLACE X/Z X/Z MAP! | PLACE_X/Z X/Z MAP! | PLACE_X/Z MAP! | MAP! | X X? | PLACE X X? | X+? ) ");
      break;
      
    case A67_OPT_CROSS | A67_OPT_MAP:
      sb.append("( X/Z X/Z INFO MAP! | X/Z X/Z MAP! | X/Z MAP! | MAP! | X X? ) ");
      break;
    
    case A67_OPT_PLACE | A67_OPT_MAP:
      sb.append("( PLACE INFO MAP! | PLACE MAP! | MAP! ) ");
      break;
    
    case A67_OPT_MAP:
      sb.append("MAP? ");
      break;
      
    case A67_OPT_PLACE | A67_OPT_CROSS:
      sb.append("( X X+? | PLACE X X+? | ) ");
      break;
      
    case A67_OPT_CROSS:
      sb.append("X+? ");
      break;
      
    case A67_OPT_PLACE:
      throw new RuntimeException("Unsupported flag combination");
    
    case 0:
      break;
    }
    
    sb.append("INFO/SLS+");
    
    if (unitPtn != null) {
      if (reqUnit) {
        sb.append("? UNIT/Z! END");
      } else {
        sb.append("? UNIT END");
      }
    }
    
    setProgram(sb.toString(), 0);
    singleCall = (flags & A67_SINGLE_CALL) != 0;
  }
  
  private static Pattern CALL_CHG_PTN = Pattern.compile("(CALL CHGFROM [A-Z0-9]+ TO [A-Z0-9]+)/");
  private static Pattern SLASH_DELIM = Pattern.compile("/");
  private static Pattern EXC_DELIM = Pattern.compile("!");
  private static Pattern PIPE_DELIM = Pattern.compile("\\|");

  @Override
  protected boolean parseMsg(String body, Data data) {
    if (prefix !=  null) {
      if (!body.startsWith(prefix)) return false;
      body = body.substring(prefix.length());
    }
    
    String chgInfo = null;
    Matcher match = CALL_CHG_PTN.matcher(body);
    if (match.lookingAt()) {
      chgInfo = match.group(1);
      body = body.substring(match.end()).trim();
    }
    
    if (body.length() < 10) return false;
    char delim = body.charAt(9);
    Pattern delimPtn;
    switch (delim) {
    
    case '/':
      delimPtn = SLASH_DELIM;
      break;
      
    case '|':
      delimPtn = PIPE_DELIM;
      break;
      
    case '!':
      delimPtn = EXC_DELIM;
      break;
      
    default:
      return false;
    
    }
    setSelectValue(singleCall || delim == '!' ? "A" : "B");
    if (!parseFields(delimPtn.split(body), data)) return false;
    if (chgInfo != null) data.strSupp = append(chgInfo, "\n", data.strSupp);
    return true;
  }
  
  @Override
  protected Field getField(String name) {
    if (name.equals("ID")) return new IdField("\\d{9}", true);
    if (name.equals("CALL2")) return new CallField("Gas", true);
    if (name.equals("DATE")) return new DateField("\\d\\d-\\d\\d-\\d\\d", true);
    if (name.equals("TIME")) return new TimeField("\\d\\d:\\d\\d:\\d\\d", true);
    if (name.equals("ADDR")) return new MyAddressField();
    if (name.equals("PLACE")) return new MyPlaceField();
    if (name.equals("PLACE_X")) return new MyPlaceCrossField();
    if (name.equals("X")) return new MyCrossField();
    if (name.equals("MAP")) return new MyMapField();
    if (name.equals("UNIT")) return new MyUnitField();
    return super.getField(name);
  }
  
  private class MyAddressField extends AddressField {
    @Override
    public boolean checkParse(String field, Data data) {
      if ((unitPtn != null && isLastField() && unitPtn.matcher(field).matches())) return false;
      field = stripFieldEnd(field, " INTERSECTN");
      return super.checkParse(field, data);
    }

    @Override
    public void parse(String field, Data data) {
      field = stripFieldEnd(field, " INTERSECTN");
      super.parse(field, data);
    }
  }
  
  private class MyPlaceField extends PlaceField {
    @Override
    public void parse(String field, Data data) {
      if (field.startsWith("WAS ")) {
        data.strSupp = field;
      } else {
        data.strPlace = field;
      }
    }
    
    @Override
    public String getFieldNames() {
      return "PLACE INFO?";
    }
  }

  private static final Pattern CROSS_PTN = Pattern.compile("MM.*|.*\\b\\d*MM\\b.*|ROAD.*|DEAD END|END");
  private class MyPlaceCrossField extends Field {
    @Override
    public void parse(String field, Data data) {
      if (CROSS_PTN.matcher(field).matches() || isValidAddress(field)) {
        data.strCross = field;
      } else {
        data.strPlace = field;
      }
    }

    @Override
    public String getFieldNames() {
      return "PLACE X";
    }
  }
  
  private class MyCrossField extends CrossField {
    @Override
    public boolean checkParse(String field, Data data) {
      if (unitPtn != null && isLastField() && unitPtn.matcher(field).matches()) return false;
      if (CROSS_PTN.matcher(field).matches()) {
        super.parse(field, data);
        return true;
      }
      return super.checkParse(field, data);
    }
  }
  
  private class MyMapField extends SourceField {
    @Override
    public boolean canFail() {
      return true;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      if (!mapPtn.matcher(field).matches()) return false;
      data.strMap = field;
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
  }
  
  private class MyUnitField extends UnitField {
    @Override
    public boolean canFail() {
      return unitPtn != null;
    }
    
    @Override
    public boolean checkParse(String field, Data data) {
      if (!isLastField()) return false;
      if (unitPtn != null && !unitPtn.matcher(field).matches()) return false;
      data.strUnit = field;
      return true;
    }
    
    @Override
    public void parse(String field, Data data) {
      if (!checkParse(field, data)) abort();
    }
  }
}
