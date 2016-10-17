package net.anei.cadpage.parsers.MN;

import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

public class MNStevensCountyParser extends FieldProgramParser {
  
  public MNStevensCountyParser() {
    super("STEVENS COUNTY", "MN", 
          "CALL:CALL! PLACE:PLACE? ADDR:ADDR! CITY:CITY! ID:ID! PRI:PRI! INFO:INFO+");
  }
  
  @Override
  protected boolean parseMsg(String body, Data data) {
    return parseFields(body.split(";"), data);
  }

}
