// =================================================
/**
 * CategoryPattern
 *
 * @author     Guy Divita
 * @created    Feb 18, 2023
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

public class CategoryPattern {
  
  public  enum PatternScope { DOC, SECTION, PAGE, LINE };
  

  // =================================================
  /**
   * Constructor
   *
   * @param pPatternName
   * @param ppatternScope
   * @param pProcessMe
   * @param pThePattern
   * @throws Excpetion
   * 
  **/
  // =================================================
  public CategoryPattern(String pPatternName, PatternScope ppatternScope, boolean pProcessMe, String pThePattern) throws Exception {
    
    this.patternName= pPatternName;
    this.patternScope = ppatternScope;
    this.processMe = pProcessMe;
    this.thePattern= pThePattern;
    this.p = Pattern.compile(this.thePattern);
     
  } // end Constructor() -----------------------------
  
  // =================================================
  /**
   * Constructor
   *
   * @param pRow
   * @throws Exception 
   * 
  **/
  // =================================================
  public CategoryPattern(String pRow) throws Exception {
    
    try {
    String[] cols = U.split(pRow);
    String patternName = cols[0].trim();
    String scope = cols[1].trim();
    String processMe_ = cols[2].trim();
    String thePattern  = cols[3].trim();
    
    PatternScope pt =  CategoryPattern.PatternScope.valueOf(scope );
    
    this.patternName= patternName;
    this.patternScope = pt;
    this.processMe = Boolean.valueOf( processMe_);
    this.thePattern= thePattern;
    this.p = Pattern.compile(this.thePattern);
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println ( GLog.ERROR_LEVEL, this.getClass(), "Contructor", "Issue creating a categoryPattern " + e.toString() );
      throw new Exception();
    }
    
  } // end Constructor() -------------------------------

  /**
   * @return the patternName
   */
  public final String getPatternName() {
    return patternName;
  }
  /**
   * @param patternName the patternName to set
   */
  public final void setPatternName(String patternName) {
    this.patternName = patternName;
  }
  /**
   * @return the patternScope
   */
  public final PatternScope getpatternScope() {
    return patternScope;
  }
  /**
   * @param patternScope the patternScope to set
   */
  public final void setpatternScope(PatternScope patternScope) {
    this.patternScope = patternScope;
  }
  /**
   * @return the thePattern
   */
  public final String getThePattern() {
    return thePattern;
  }
  /**
   * @param thePattern the thePattern to set
   */
  public final void setThePattern(String thePattern) {
    this.thePattern = thePattern;
  }


  
  /**
   * @return the patternScope
   */
  public final PatternScope getPatternScope() {
    return patternScope;
  }

  /**
   * @param patternScope the patternScope to set
   */
  public final void setPatternScope(PatternScope patternScope) {
    this.patternScope = patternScope;
  }

  

  // =================================================
  /**
   * classify returns the pattern name that matches some
   * content within the unit  
   * If the pattern has a name, in the second field is processMe
   * 
   *   someName|false
   * 
   *   While I could create annotations that are where the
   *   patterns match, I want this to be a quick and dirty
   *   yes, no, tool.  Somewhere between lightweight,
   *   and efficient, and somewhat featured.  
   *   By compiling the patterns into regex's this makes
   *   it a little memory heavy, but fast.
   *   I could do a scanner which would only require
   *   memory when the pattern is being used, but that
   *   would be slower, but memory lightweight.
   * 
   * @param pContent
   * @return String    patternName|processMe
  */
  // =================================================
  public final String classify(String pContent) {
    
    String returnVal = null;
    String patternName = null;
   
    Matcher m = this.p.matcher(pContent );
    
    if ( m.find() ) {
      patternName = this.patternName;
      String matchedText = pContent.substring( m.start() ,m.end() );
      matchedText = U.normalizePipesAndNewLines(matchedText);
      returnVal = patternName + "|" + this.processMe + "|" + m.start() + "|" + m.end() + "|" + matchedText ;
    }
    
    return returnVal; 
  } // end Method classify() -------------------------
  
//---------------------------------
 // Global Variables
 // ---------------------------------

 private String patternName = null;
 private PatternScope patternScope = null;
 private boolean processMe = false;
 private String thePattern = null;
 private Pattern p = null;
 
  

} // end Class CategoryPattern() -----------------------
