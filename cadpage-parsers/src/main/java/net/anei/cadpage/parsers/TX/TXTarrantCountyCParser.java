package net.anei.cadpage.parsers.TX;

import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchA18Parser;


public class TXTarrantCountyCParser extends DispatchA18Parser {
  
  public TXTarrantCountyCParser() {
    super(TXTarrantCountyParser.CITY_LIST, "TARRANT COUNTY","TX");
  }
 
  @Override
  public String getFilter() {
    return "crimespage@lakeworthtx.org";
  }

  @Override
  protected boolean parseMsg(String body, Data data) {
    int pt = body.indexOf("\n\n\n");
    if (pt >= 0) body = body.substring(0, pt).trim();
    return super.parseMsg(body, data);
  }
}
