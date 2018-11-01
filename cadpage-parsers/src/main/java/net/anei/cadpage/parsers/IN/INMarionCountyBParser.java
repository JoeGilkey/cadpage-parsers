package net.anei.cadpage.parsers.IN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchA52Parser;

public class INMarionCountyBParser extends DispatchA52Parser {
  
  public INMarionCountyBParser() {
    super("MARION COUNTY", "IN");
  }
  
  @Override
  public String getFilter() {
    return "MotorolaCAD@page.indy.gov,CAD@page.indy.gov";
  }
  
  private static final Pattern MARKER = Pattern.compile("(?:Motorola )?CAD: *");
  
  @Override
  protected boolean parseMsg(String body, Data data) {
    Matcher match = MARKER.matcher(body);
    if (!match.lookingAt()) return false;
    body = body.substring(match.end());
    return super.parseMsg(body, data);
  }

}