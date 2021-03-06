package net.anei.cadpage.parsers.PA;

import net.anei.cadpage.parsers.FieldProgramParser;
import net.anei.cadpage.parsers.MsgInfo.Data;

public class PASnyderCountyBParser extends FieldProgramParser {
  
  public PASnyderCountyBParser() {
    this("SNYDER COUNTY");
  }
  
  PASnyderCountyBParser(String defCity) {
    super(defCity, "PA", 
          "CALL PLACE ADDRCITY GPS1 GPS2 APT X! Box_Area:BOX! Notes:INFO! Units_Due:UNIT END");
  }
  
  @Override
  public String getFilter() {
    return "CSR911@csr911.org";
  }
  
  @Override
  public int getMapFlags() {
    return MAP_FLG_PREFER_GPS;
  }
  
  @Override
  protected boolean parseMsg(String body, Data data) {
    if (!parseFields(body.split(";"), data)) return false;
    data.strCity = stripFieldEnd(data.strCity,  " BORO");
    return true;
  }

}
