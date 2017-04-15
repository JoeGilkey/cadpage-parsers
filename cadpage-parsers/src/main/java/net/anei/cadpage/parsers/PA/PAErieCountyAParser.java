package net.anei.cadpage.parsers.PA;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anei.cadpage.parsers.CodeSet;
import net.anei.cadpage.parsers.MsgInfo.Data;
import net.anei.cadpage.parsers.dispatch.DispatchB2Parser;



public class PAErieCountyAParser extends DispatchB2Parser {
  
  private static final Pattern MARKER2 = Pattern.compile("[0-9A-Z]+ *>");
  private static final Pattern MASTER3 = Pattern.compile("([ A-Za-z]+) / ([A-Z0-9]+ *> *[^:>]*?)>(.*)");
  private static final Pattern CITY_SUFFIX = Pattern.compile("(?:BORO|CITY|VILLAGE|TWP|CO)\\b *");
 
  public PAErieCountyAParser() {
    super(PAErieCountyParser.CITY_LIST, "ERIE COUNTY", "PA", B2_OPT_CALL_CODE);
    setupCallList(CALL_LIST);
    setupMultiWordStreets(MWORD_STREET_LIST);
  }
  
  @Override
  protected boolean isPageMsg(String body) {
    return true;
  }

  @Override
  protected boolean parseMsg(String subject, String body, Data data) {
    
    // Dummy do loop
    do {
      if (body.startsWith("ERIE911:")) {
        data.strSource = "ERIE911";
        body = body.substring(8).trim();
        break;
      }
      
      if (subject.length() > 0) {
        if (MARKER2.matcher(body).lookingAt()) {
          data.strSource = subject;
          break;
        }
      }
      
      Matcher match = MASTER3.matcher(body);
      if (match.matches()) {
        data.strSource = match.group(1).trim();
        body = match.group(2) + '@' + match.group(3);
        break;
      }
      
      return false;
    } while (false);

    body = body.replace("=20", " ").trim();
    boolean result = super.parseMsg(body, data);
    if (result) {
      Matcher match = CITY_SUFFIX.matcher(data.strName);
      if (match.lookingAt()) data.strName = data.strName.substring(match.end());
      result =  
          (data.strCross.length() > 0 || 
           data.strCallId.length() > 0);
      if (!result) {
        int pt = body.indexOf('>');
        result = (pt >= 0 && pt <= 20);
      }
    }
    if (!result && data.strSource.equals("ERIE911")) {
      data.parseGeneralAlert(this, body.trim());
      data.strSource = "ERIE911";
      result = true;
    }
    return result;
  }
  
  @Override
  public String getProgram() {
    return "SRC " + super.getProgram();
  }
  
  private static final String[] MWORD_STREET_LIST = new String[]{
    "BEAR CREEK",
    "CAMBRIDGE SPRINGS",
    "CARRIAGE HILL",
    "CHERRY HILL",
    "CROSS STATION",
    "EDGE PARK",
    "ELK CREEK",
    "ELK PARK",
    "FAIR OAKS",
    "FIELD VALLEY",
    "FOREST GLEN",
    "FRANKLIN CENTER",
    "GENEVA MARIE",
    "GLENWOOD PARK",
    "GOLF CLUB",
    "GREEN OAKS",
    "HALF MOON",
    "HANNA HALL",
    "HARLEY DAVIDSON",
    "HASKELL HILL",
    "HOPSON HILL",
    "IMPERIAL POINT",
    "JOHN WILLIAMS",
    "KAHKWA CLUB",
    "KIMBALL HILL",
    "KINTER HILL",
    "LAKE PLEASANT",
    "LAKE SHORE",
    "MCGAHEN HILL",
    "NICKLE PLATE",
    "OLD RIDGE",
    "OLD STATE",
    "OLD WATTSBURG",
    "OLD ZUCK",
    "PENELEC PARK",
    "PIN OAK",
    "PINE LEAF",
    "PINE TREE",
    "PINE VALLEY",
    "RILEY SIDING",
    "SHERROD HILL",
    "SPRING LAKE",
    "SPRING VALLEY",
    "SPRUCE TREE",
    "STONE QUARRY",
    "SUNRISE LAKES",
    "TAYLOR RIDGE",
    "UNION AMITY",
    "UNION LEBOEUF",
    "VILLAGE COMMON",
    "WALNUT CREEK",
    "WASHINGTON TOWNE",
    "WOLF RUN",
    "WOODLAND HILL"
  };
 
  private static final CodeSet CALL_LIST = new CodeSet(
      "ABD PN - FEM PAIN ABOVE NAV>45",
      "ABDOM PAIN - FEM 12-50 W/FAINT",
      "ABDOM PAIN FAINT/NEAR > 50",
      "ABDOM PAIN KNOWN AORTIC ANEURY",
      "ABDOMINAL PAIN - NOT ALERT",
      "ABDOMINAL PAIN BLS P2",
      "ABDOMINAL PAIN DELTA RESPONSE",
      "ABDOM PN MALE ABOVE NAVEL =>35",
      "AIRCRAFT EMERGENCY ALERT II",
      "ALARM - COMM/INDUSTRY GEN/FIRE",
      "ALARM - COMM/INDUSTRY PULL STA",
      "ALARM - COMM/INDUSTRY TROUBLE",
      "ALARM - COMM/INDUSTRY WATERFLO",
      "ALARM - HI LIFE HAZARD",
      "ALARM - HI-LIFE HAZ GEN/FIRE",
      "ALARM - HI-LIFE HAZ OTHER",
      "ALARM - HI-LIFE HAZ SMOKE DET",
      "ALARM - HI-LIFE HAZ UNK SITU",
      "ALARM - HIGH RISE GENERAL/FIRE",
      "ALARM - MULTI RESID GEN/FIRE",
      "ALARM - MULTI RESID SMOKE DET",
      "ALARM - MULTI RESID WATERFLOW",
      "ALARM - SINGLE RESID CO ALARM",
      "ALARM - UNK SIT-SMOKE DETECT",
      "ALARM BRAVO RESPONSE",
      "ALARM- COMM/INDUSTRY SMOKE DET",
      "ALARM-COMM/INDUSTRY OTHER TYPE",
      "ALARM-COMMERCIAL CHARLEY RESPO",
      "ALARM-NON-DWELL BLDG SMOKE DET",
      "ALARM-SINGLE RES SMOKE DETECT",
      "ALLERGIES-DIFF BREATH/SWALLOW",
      "ALLERGIES-INJ HIST SEV REACT",
      "ALLERGIES-SWARMING ATTACK",
      "ALLERGIES/ENVENOM-NOT ALERT",
      "ALLERGY-DIFF SPEAK BTW BREATH",
      "ALLERGY/ENVENOM NO DIFF BREATH",
      "ANIMAL BITES/ATTACK-SER HEMORR",
      "ASSAULT - NOT ALERT",
      "ASSAULT-NOT DANGER BODY AREA",
      "ASSAULT-POSS DANGER BODY AREA",
      "ASSAULT/SEXUAL ASSAULT DELTA",
      "AUTO-PEDESTRIAN",
      "BACK PAIN ALPHA RESPONSE",
      "BACK PAIN-NON TRAUMATIC",
      "BACK PAIN-TRAM-NON RECENT>6HRS",
      "BRAVO OVERRIDE",
      "BREATH PROB-ASTHMA-DIFF SPEAK",
      "BREATH PROB-DIFF SPEAK BTW BRE",
      "BREATHING PROB-ASTHMA-CLAMMY",
      "BREATHING PROB-INEFF BREATHING",
      "BREATHING PROBLEMS",
      "BREATHING PROBLEMS OVERRIDE",
      "BREATHING PROBLEMS-ABNORMAL",
      "BREATHING PROBLEMS-CLAMMY",
      "BREATHING PROBLEMS-NOT ALERT",
      "CARBON MONOXIDE ALARM",
      "CARDIAC/RESP ARR-OBVIOUS DEATH",
      "CARDIAC/RESP ARR/DEATH-COLD/ST",
      "CARDIAC/RESP ARREST / DEATH",
      "CARDIAC/RESP ARREST BRAVO RESP",
      "CARDIAC/RESP ARREST-INEFF BREA",
      "CARDIAC/RESP/DEATH-NOT BREATH",
      "CARDIAC/RESP/DEATH-UNDERWATER",
      "CARDIAC ABNORM BREATHING",
      "CHEST PAIN",
      "CHEST PAIN - BREATH NORMAL=>35",
      "CHEST PAIN - BREATH NORMAL=3D>35",
      "CHEST PAIN - CARDIAC HISTORY",
      "CHEST PAIN - CLAMMY",
      "CHEST PAIN - DELTA OVERRIDE",
      "CHEST PAIN - PATIENT NOT ALERT",
      "CHEST PAIN ALPHA RESPONSE",
      "CHEST PAIN BRAVO RESPONSE",
      "CHEST PAIN CHARLEY RESPONSE",
      "CHEST PAIN DELTA RESPONSE",
      "CHEST PAIN DIFF SPEAK BTW BRE",
      "CHEST PAIN-DIFFICULT BREATHING",
      "CHOKING ECHO OVERRIDE",
      "CITIZEN ASSIST - NON MEDICAL",
      "CITIZEN ASSIST ALPHA RESPONSE",
      "CITIZEN ASSIST-OUTSIDE RESOURC",
      "CITIZEN ASSIST-DOWNED TREE/OBJ",
      "CITIZEN ASSIST-UNK SITUATION",
      "CO/INH-ALERT-W/O DIFF BR-GAS",
      "CO/INHALE/HZMT-UNCONS/ARR-CHEM",
      "COLD EXPOSURE - UNK STATUS",
      "CONV / SEIZ - MULTI/CONTINUOUS",
      "CONV/SEIZ-EFF BREATH NOT VERIF",
      "CONV/SEIZ-EPI-CONTINUOUS/MULTI",
      "DIAB PROB - ABNORMAL BEHAVIOR",
      "DIABETIC PROB-ALERT/NORM BEHAV",
      "DIABETIC PROBLEMS",
      "DIABETIC PROBLEMS - NOT ALERT",
      "DIABETIC PROBLEMS-UNCONCIOUS",
      "DIFF BREATHING",
      "ELEC HAZ/PWR REPT DISCONNECTED",
      "ELECTRICAL HAZ BRAVO OVERRIDE",
      "ELECTRICAL HAZ-NEAR WATER",
      "ELECTRICAL HAZ-UNKNOWN SITUA",
      "ELECTRICAL HAZARD - ELEC ODOR",
      "ELECTRICAL HAZARD ALPHA RESPO",
      "ELECTRICAL HAZARD W/ARCING",
      "EXTINGUISHED STR FIRE ODOR",
      "FALLS",
      "FALLS - ALPHA OVERRIDE",
      "FALLS - BRAVO OVERRIDE",
      "FALLS - CHARLEY OVERRIDE",
      "FALLS - DELTA OVERRIDE",
      "FALLS - ALPHA RESPONSE",
      "FALLS - BRAVO RESPONSE",
      "FALLS - CHARLE RESPONSE",
      "FALLS - DELTA RESPONSE",
      "FALLS - NOT ALERT",
      "FALLS - POSS DANGER BODY AREA",
      "FALLS - SERIOUS HEMORRHAGE",
      "FALLS - UNCONSCIOUS / ARREST",
      "FALLS - UNKNOWN STATUS",
      "FALLS (GRD/FLR) POSS DANGER BO",
      "FALLS (GRND/FLOOR) UNK STATUS",
      "FALLS (GROUND/FLOOR) NOT ALERT",
      "FALLS LONG - ADULT/CH>10-29'",
      "FALLS- NON-RECENT =>6HRS W/INJ",
      "FALLS- NOT DANGEROUS BODY AREA",
      "FALLS-ON GRD/FL-NOT DANGER BOD",
      "FALLS-PUBLIC ASSIST NO INJ/PRI",
      "FALLS(ON GRD/FL)-PUBLIC ASSIST",
      "FIRE/GENERAL ALARM-COMM STRUC",
      "FUEL SPILL SM CONTAINED",
      "FUEL SPILL- SM CONTAIN OUTSIDE",
      "GAS LEAK/ODOR NAT/LP-OUTSIDE",
      "GAS LEAK/ODOR-COMMERCIAL-OUTSI",
      "GAS LEAK/ODOR-RESIDENT-OUTSIDE",
      "GAS LEAK/ODOR-RESIDENTIAL",
      "GAS ODOR ONLY - OUTSIDE",
      "GAS ODOR-COMMERCIAL/INDUSTRIAL",
      "GUNSHOT ALPHA OVERRIDE",
      "GUNSHOT BRAVO OVERRIDE",
      "GUNSHOT CHARLEY OVERRIDE",
      "GUNSHOT DELTA OVERRIDE",
      "HEADACHE - NOT ALERT",
      "HEADACHE - SPEECH PROBLEMS",
      "HEADACHE CHANGE BEHAVIOR <=3HR",
      "HEADACHE SUDDEN ONSET SEVERE P",
      "HEART PROB / A.I.C.D. CLAMMY",
      "HEART PROB/AICD - CARDIAC HIST",
      "HEART PROB/AICD CHARLEY RESPON",
      "HEART PROB/AICD - NOT ALERT",
      "HEART PROB/AICD - UNK STATUS",
      "HEMORR/LACE-POSS DANGER HEMORR",
      "HEMORR/LACER - SERIOUS HEMORRH",
      "HEMORR/LACERA - ABNORM BREATH",
      "HEMORR/LACERA - BLEED DISORDER",
      "HEMORR/LACERA - BLOOD THINNERS",
      "HEMORR/LACERA - DANGER HEMORRA",
      "HEMORR/LACERA - DELTA OVERRIDE",
      "HEMORR/LACERA - NOT DANGEROUS",
      "HEMORR/LACERATIONS - NOT ALERT",
      "HEMORRHAGE / LACERATIONS",
      "HRT PROB/AICD-CHEST PAIN =>35",
      "HRT PROB/AICD DIFF SPEAK BTW B",
      "LG BRUSH/GRASS FIRE-UNKN EXPO",
      "MAJOR INCIDENT - MVA DELTA RES",
      "MED DEFAULT FOR PROQA",
      "MUTL AID TO INC-MULTI UNITS P2",
      "MUTUAL AID/ASSIST OUTSIDE AGEN",
      "MVA - INJURIES -UNK # PATIENT",
      "MVA - MAJOR INCIDENT",
      "MVA - NOT ALERT",
      "MVA - PINNED / ENTRAPPED",
      "MVA - UNKNOWN STATUS",
      "MVA - WITH INJURIES",
      "MVA -EJECTION- HIGH MECHANISM",
      "MVA- ALPHA OVERRIDE",
      "MVA- ALPHA RESPONSE",
      "MVA- AUTO-BICYCLE / MOTORCYCLE",
      "MVA- OTHER HAZARDS",
      "MVA- OTHER HAZARDS / UNKN # PT",
      "MVA- ROLLOVERS",
      "MVA- SERIOUS HEMOR / MULTI PT",
      "MVA- TRAPPED / UNKN # PTS",
      "MVA- UNKN STATUS / UNKN # PTS",
      "MVA-INJ-UNK # PATS & ADD RESPO",
      "MVA-TRAPPED MULTI PT/ADD RESPO",
      "OD/POISON - UNKN STATUS,INTENT",
      "OD/POISON-ABNORMAL BREATH-INT",
      "OD/POISON-ABNORMAL BREATHING",
      "OD/POISON-CHANGE COLOR-INTENTI",
      "OD/POISON-NOT ALERT-VIOLENT",
      "OD/POISON-NOT ALERT,ACCIDENTAL",
      "OD/POISON-NOT ALERT,INTENTIONA",
      "OD/POISON-UNCONSCIOUS-INTENTIO",
      "ODOR (STRANGE/UNK)-INSIDE",
      "OUTSIDE FIRE",
      "OUTSIDE FIRE - EXTINGUISHED",
      "OUTSIDE FIRE - SMALL",
      "OUTSIDE FIRE ALPHA RESPONSE",
      "OUTSIDE FIRE BRAVO RESPONSE",
      "OUTSIDE FIRE- LG BRUSH/GRASS",
      "OUTSIDE FIRE- TRANSFORMER FIRE",
      "OUTSIDE FIRE-UNK SIT-RESID EXP",
      "OUTSIDE ODOR/UNK SOURCE",
      "OVERDOSE/POISON-OD ACCIDENTIAL",
      "OVERDOSE/POISON-OD INTENTIONAL",
      "POISONING W/O PRI SYMP-ACCIDEN",
      "PREG/BIRTH-DLVY IMMINENT>5 MOS",
      "PREGNANCY-1ST TRI HEMORR/MISCA",
      "PREGNANCY-HI RISK COMPLICATION",
      "PSYCH-UNK STAT, VIOLENT/WEAPON",
      "PSYCH/ABNORM BEH/SUIC ALPHA",
      "PSYCH/ABNORM BEH/SUIC UNK STAT",
      "PSYCH/ABNORM BEH/SUICIDE OVERR",
      "PSYCH/SUIC-UNK STATUS,VIOLENT",
      "PSYCH/SUICID-NOT ALERT,VIOLENT",
      "PSYCH/SUICIDAL-NOT ALERT",
      "PSYCHIATRIC BRAVO RESPONSE",
      "RES (SINGLE) HEAT DETECTOR",
      "RESIDENTIAL FIRE ALARM-GENERAL",
      "ROAD CLOSING",
      "SEIZURES - DIABETIC",
      "SEIZURES - NOT SEIZING NOW",
      "SEIZURES-EFF BREATH NOT VERIFY",
      "SEIZURES-EPI-BREATH NOT VERIFY",
      "SEIZURES-EPILEPTIC-DIABETIC",
      "SEIZURES-EPILEPTIC-NOT SEIZING",
      "SEIZURES-NOT SEIZING-EFF BREAT",
      "SICK PERSON",
      "SICK PERSON ALPHA RESPONSE",
      "SICK PERSON BRAVO RESPONSE",
      "SICK PERSON CHARLEY RESPONSE",
      "SICK PERSON DELTA RESPONSE",
      "SICK PERSON - CONSTIPATION",
      "SICK PERSON - FEVER / CHILLS",
      "SICK PERSON - GENERAL WEAKNESS",
      "SICK PERSON - NAUSEA",
      "SICK PERSON - NOT ALERT",
      "SICK PERSON - TRANSPORT ONLY",
      "SICK PERSON - UNWELL / ILL",
      "SICK PERSON - UNKNOWN STATUS",
      "SICK PERSON - VOMITING",
      "SICK PERSON DELTA RESPONSE",
      "SICK PERSON W/ NO PRIORITY SYP",
      "SICK PERSON- DIZZINESS/VERTIGO",
      "SICK PERSON-ABNORMAL BREATHING",
      "SICK PERSON-ALT LVL OF CONSC",
      "SICK PERSON-NEW ONSET IMMOBILI",
      "SICK PERSON-PENIS PROBLEM/PAIN",
      "SIGNAL SIGN OUT",
      "SMOKE INV/OUTSIDE HEAVY SMOKE",
      "SMOKE INVEST (OUTSIDE) CHARLEY",
      "STROKE (CVA) - NOT ALERT <2HRS",
      "STROKE (CVA) BREATH NORM > 35",
      "STROKE (CVA)-NOT ALERT-UNK TIM",
      "STROKE-ABNORMAL BREATH <2HRS",
      "STROKE-SUD SEV HEADACHE <2HRS",
      "STROKE-SUDD VISION PROB <2HRS",
      "STROKE-SUDDEN PARALYSIS <2HRS",
      "STROKE-SUDDEN WEAK/NUMB <2HRS",
      "STRUC FIRE-APPLIANCE-CONTAINED",
      "STRUC FIRE-CHIMNEY-SMOKE ODOR",
      "STRUC FIRE-HI LIFE HAZARD",
      "STRUC FIRE-LG NON DWELLING",
      "STRUC FIRE-MOBILE HOME",
      "STRUC FIRE-MULTI RESID-SMOKE",
      "STRUC FIRE-MULTI RESIDENTIAL",
      "STRUC FIRE-SINGLE RESID-SMOKE",
      "STRUC FIRE-SINGLE RES-TRAPPED",
      "STRUC FIRE-SINGLE RESIDENTIAL",
      "STRUC FIRE-SM NON DWELLING",
      "STRUC FIRE-UNK SITUATION/INVES",
      "STRUCTURE FIRE - OVERRIDE",
      "STRUCTURE FIRE CHARLEY RESPONS",
      "STRUCTURE FIRE DELTA RESPONSE",
      "STRUCTURE FIRE-COMMERCIAL/INDU",
      "SYNC EPISODE-ALERT/ABNORM BREA",
      "SYNC EPISODE-ALERT>35 CARD HX",
      "SYNC EPISODE-FEM 12-50 W/ABDOM",
      "SYNCOPE/ALERT < 35 W/O CARDIAC",
      "SYNCOPE/ALERT >35 W/O CARDIAC",
      "TRAFFIC CONTROL",
      "TRAFFIC/TRANS ACC OVERRIDE",
      "TRAFFIC/TRANSPORT ACC OVERRIDE",
      "TRANSFORMER FIRE-UNK EXPOSURE",
      "TRAUMA INJ-CHEST/NECK,DIFF BRE",
      "TRAUMA INJ-POSS DANGER BODY AR",
      "TRAUMA INJURY-NOT DANGER BODY",
      "TRAUMATIC INJ-SERIOUS HEMORRHA",
      "TRAUMATIC INJURY ALPHA RESPONS",
      "TRAUMATIC INJURIES DELTA RESPO",
      "TREE DOWN",
      "UNCONSC-AGONAL/INEFF BREATH",
      "UNCONSCIOUS/FAINT - NOT ALERT",
      "UNCONSCIOUS/FAINT DELTA OVERRI",
      "UNCONSCIOUS/FAINTING DELTA",
      "UNCONSCIOUS CHANGING COLOR",
      "UNCONSCIOUS EFFECT BREATHING",
      "UNCONSCIOUS INEFFECT BREATHING",
      "UNKN PROB-MED ALRM,NO PT INFO",
      "UNKN PROBLEM-LIFE STATUS QUES",
      "UNKN PROBLEM-STAND,SIT,MOVING",
      "UNKN PROBLEM-UNKN STATUS",
      "UNKNOWN PROBLEM",
      "UNKNOWN PROBLEM BRAVO RESPONSE",
      "UNKNOWN PROBLEM DELTA RESPONSE",
      "VEH FIRE-LARGE FUEL/ FIRE LOAD CROSS",
      "VEH FIRE-THREATENED STRUCTURE",
      "VEHICLE FIRE",
      "VEHICLE FIRE ALPHA RESPONSE",
      "VEHICLE FIRE BRAVO RESPONSE",
      "VEHICLE FIRE CHARLEY RESPONSE",
      "VEHICLE FIRE DELTA RESPONSE",
      "VEHICLE FIRE - \"B\" OVERRIDE",
      "VEHICLE FIRE-EXTINGUISHED",
      "WATER RESCUE DELTA RESPONSE",
      "WATER RESCUE-COASTAL WATER",
      "WIRES DOWN-NO SMOKE OR ARCING",
      "WIRES DOWN-PWR DISCONNECTED",
      "??????????????????????????????"
  );
}
  