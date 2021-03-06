package net.anei.cadpage.parsers.OH;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

public class OHWayneCountyDParser extends FieldProgramParser {
  
  public OHWayneCountyDParser() {
    this("WAYNE COUNTY", "OH");
  }
  
  protected OHWayneCountyDParser(String defCity, String defState) {
    super(defCity, defState,
          "CALL:CALL! PLACE:PLACE! ADDR:ADDR! CITY:CITY! ID:ID! UNIT:UNIT! INFO:INFO! INFO/N+");
  }
  
  @Override
  public String getAliasCode() {
    return "OHWayneCountyD";
  }

  @Override
  public String getFilter() {
    return "info@sundance-sys.com";
  }
  
  private static final Pattern FLAG_PTN = Pattern.compile("\\*{2,}([-/ A-Z0-9]+)\\*{2,} *");
  
  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    if (!subject.equals("From: WarCOGUser")) return false;
    String flag = "";
    Matcher match = FLAG_PTN.matcher(body);
    if (match.lookingAt()) {
      flag = match.group(1);
      body = body.substring(match.end());
    }
    if (!parseFields(body.split("\n"), data)) return false;
    data.strCall = append(flag, " - ", data.strCall);
    return true;
  }

}
