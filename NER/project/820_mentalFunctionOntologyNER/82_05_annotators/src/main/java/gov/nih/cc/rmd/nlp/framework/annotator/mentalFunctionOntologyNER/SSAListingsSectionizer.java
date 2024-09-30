// =================================================
/**
 * SSAListingsSectionizer 
 *  creates sections for the sections within the SSA Listings.
 *  This is a necessary annotator because the
 *  ssa listings are not organized like clinical documents
 *  
 *  They have their own unique structure to them. 
 * 
 * @author  G.o.d.
 * @created 2022.09.06
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.NestedSection;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;
import gov.va.chir.model.ListDelimiter;
import gov.va.chir.model.WordToken;



public class SSAListingsSectionizer extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process runs through the text and creates
   * mentalFunctions from mentions.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
      this.performanceMeter.startCounter();

      // remove sections, questions and answers, and slot values
   
      // Iterate through each line
    
   
    
      List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID );
    
     
      NestedSectionStructure levelOneSection = null;
      NestedSectionStructure levelTwoSection = null;
      NestedSectionStructure levelThreeSection = null;
      NestedSectionStructure levelFourSection = null;
      NestedSectionStructure levelFiveSection = null;
      HashMap<Integer, NestedSectionStructure> parentIndex = new HashMap<Integer, NestedSectionStructure>();
    
      if ( lines != null && !lines.isEmpty()) 
        for ( int i = 0; i < lines.size(); i++ ) {
          Annotation aLine = lines.get(i);
          
          String aLineText = aLine.getCoveredText();
        
        // (level1) look for line that starts with a number and has Initial Cap camel cased words  |12.00 Mental Disorders
        // (level2) look for line that starts with a capital letter period space CapCased word     |(A. How ...
        //                                                                                |
        // (level3) look for line that starts with indentation, number period space CapCasted word | __1. The
        //                                                                                |
        // (level4) look for line that starts with indentation, lower letter  spaced CapeCased word| __ __ a. Para
        // (level5) look for line that starts with indentation, roman numerals and CapCased word   | __ __ __ ii. To do 
        
        // Keep a section zoning nesting structure for levels
        
          if      ( lineStartsWithLevel1(pJCas,  aLineText )) {
          
            if ( levelOneSection != null ) 
              createLevelSectionZone( pJCas, levelOneSection, null  );
    
            if ( levelTwoSection != null) {
              createLevelSectionZone( pJCas, levelTwoSection, levelOneSection );
              levelTwoSection = null;
            }
         
            if ( levelThreeSection != null) {
              createLevelSectionZone( pJCas, levelThreeSection, levelTwoSection );
              levelThreeSection = null;
            }
            
            if ( levelFourSection != null) {
              createLevelSectionZone( pJCas, levelFourSection, levelThreeSection );
              levelFourSection = null;
            }
            
            if ( levelFiveSection != null) {
              createLevelSectionZone( pJCas, levelFiveSection, levelFourSection );
              levelFiveSection = null;
            }
        
            levelOneSection = new NestedSectionStructure(  );
            parentIndex.put(  levelOneSection.getIndexNumber(), levelOneSection );
            levelOneSection.setSectionNumber(1);
            levelOneSection.setSectionName( aLineText );
            levelOneSection.addLine(aLine);
          
        }  else if ( lineStartsWithLevel2(pJCas,  aLineText )) {
          if ( levelTwoSection != null )
           createLevelSectionZone( pJCas, levelTwoSection, levelOneSection );
 
          if ( levelThreeSection != null) {
            createLevelSectionZone( pJCas, levelThreeSection, levelTwoSection);
            levelThreeSection = null;
          }
          
          if ( levelFourSection != null) {
            createLevelSectionZone( pJCas, levelFourSection, levelThreeSection);
            levelFourSection = null;
          }
          
          if ( levelFiveSection != null) {
            createLevelSectionZone( pJCas, levelFiveSection, levelFourSection);
            levelFiveSection = null;
          }
          
          levelTwoSection = new NestedSectionStructure();
          parentIndex.put(  levelTwoSection.getIndexNumber(), levelTwoSection );
          levelTwoSection.setSectionName( aLineText );
          levelTwoSection.setSectionNumber(2);
          levelTwoSection.addLine(aLine);
          
          
        }  else if ( lineStartsWithLevel3(pJCas,  aLineText )) {
          if ( levelThreeSection != null )
            createLevelSectionZone( pJCas, levelThreeSection , levelTwoSection);
  
           if ( levelFourSection != null) {
             createLevelSectionZone( pJCas, levelFourSection, levelThreeSection);
             levelFourSection = null;
           }
           
           if ( levelFiveSection != null) {
             createLevelSectionZone( pJCas, levelFiveSection, levelFourSection);
             levelFiveSection = null;
           }
           
           levelThreeSection = new NestedSectionStructure();
           parentIndex.put(  levelThreeSection.getIndexNumber(), levelThreeSection );
           levelThreeSection.setSectionName( aLineText );
           levelThreeSection.setSectionNumber(3);
           levelThreeSection.addLine(aLine);
          
        }  else if ( lineStartsWithLevel4(pJCas,  aLine, aLineText)) {
          if ( levelFourSection != null )
            createLevelSectionZone( pJCas, levelFourSection, levelThreeSection );
  
           if ( levelFiveSection != null) {
             createLevelSectionZone( pJCas, levelFiveSection, levelFourSection);
             levelFiveSection = null;
           }
           levelFourSection = new NestedSectionStructure();
           parentIndex.put(  levelFourSection.getIndexNumber(), levelFourSection );
           levelFourSection.setSectionName( aLineText );
           levelFourSection.setSectionNumber(4);
           levelFourSection.addLine(aLine);
          
          
          
          
        }  else if ( lineStartsWithLevel5(pJCas,  aLineText )) {
          createLevelSectionZone( pJCas, levelFiveSection, levelFourSection );
          
          levelFiveSection = new NestedSectionStructure();
          parentIndex.put(  levelFiveSection.getIndexNumber(), levelFiveSection );
          levelFiveSection.setSectionName( aLineText );
          levelFiveSection.setSectionNumber(2);
          levelFiveSection.addLine(aLine);
          
          
        }  else
        {
          if ( levelOneSection != null )    levelOneSection.addLine(   aLine);
          if ( levelTwoSection != null )    levelTwoSection.addLine(   aLine);
          if ( levelThreeSection != null )  levelThreeSection.addLine( aLine);
          if ( levelFourSection != null )   levelFourSection.addLine(  aLine);
          if ( levelFiveSection != null )   levelFiveSection.addLine(  aLine);
        } 
      } // end loop through lines 
      
      if ( levelOneSection != null ) 
        createLevelSectionZone( pJCas, levelOneSection , null );

      if ( levelTwoSection != null) {
        createLevelSectionZone( pJCas, levelTwoSection, levelOneSection );
        levelTwoSection = null;
      }
   
      if ( levelThreeSection != null) {
        createLevelSectionZone( pJCas, levelThreeSection , levelTwoSection);
        levelThreeSection = null;
      }
      
      if ( levelFourSection != null) {
        createLevelSectionZone( pJCas, levelFourSection, levelThreeSection );
        levelFourSection = null;
      }
      
      if ( levelFiveSection != null) {
        createLevelSectionZone( pJCas, levelFiveSection, levelFourSection );
        levelFiveSection = null;
      }
  
      
      // fill in the parent indexes for the section zones 
      fillParentIndexes( pJCas, parentIndex);
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", "Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

// =================================================
  /**
   * fillParentIndexes iterates through the nested structures, and fills in the parent
   * section zones from the parent Indexes
   * 
   * @param pJCas
   * @param parentIndex
   * 
   * 
   * 
   * parent index  0  1   2   3   4   5   6
   *     
   * NestedSection[0].parent ->  null
   * NestedSection[1].parent ->  0
   * NestedSection[2].parent ->  1
  */
  // =================================================
  private void fillParentIndexes(JCas pJCas, HashMap<Integer, NestedSectionStructure> parentIndex) {
    
   
    for ( int i = 0; i < parentIndex.size(); i++ ) {
      NestedSectionStructure aStructure = parentIndex.get(i);
      
      if ( aStructure != null ) {
      
      
        NestedSection focus = aStructure.getSectionZone();
        int parentIndexId = aStructure.getParentIndex();
        NestedSectionStructure xx = parentIndex.get( parentIndexId);
        if ( xx != null ) {
         
          
          NestedSection parentSectionZone = xx.getSectionZone();
          focus.setParentSection( (SectionZone) parentSectionZone);
          focus.setTempStructure( parentSectionZone.getSectionName());
        } // end if there is a parent structure
      } // end if there is a structure
      } // end loop thrugh structures
      
      // -------------------------
      // loop again to trace back decendents
      for ( int i = 0; i < parentIndex.size(); i++ ) {
        NestedSectionStructure aStructure = parentIndex.get(i);
        
        if ( aStructure != null ) {
          NestedSection focus = aStructure.getSectionZone();
          FSArray decendentSectionZonesFSArray = getDecendentSectionZones( pJCas, focus );
          focus.setNestedSection( decendentSectionZonesFSArray);
        }
      } // end loop thru the nested structures again
    
    
  } // end Method fillParentIndexes() --------------


// =================================================
/**
 * getDecendentSectionZones 
 * 
 * @param focus
 * @return
*/
// =================================================
private final FSArray getDecendentSectionZones(JCas pJCas, NestedSection focus) {
  
  FSArray decendentSectionZonesFSArray = null;
  List<SectionZone> decendents = new ArrayList<SectionZone>();
  SectionZone parent = focus.getParentSection();

  if ( parent != null )
    while ( parent != null ) {
      decendents.add( parent );
      parent = ((NestedSection) parent).getParentSection();
     }
  
    if ( decendents != null && !decendents.isEmpty() )
      decendentSectionZonesFSArray = UIMAUtil.list2FsArray(pJCas, decendents );
  return decendentSectionZonesFSArray;
} // end Method getDecendentSectionZones() ---------


// =================================================
/**
 * getDecendentSectionZones [TBD] summary
 * 
 * @param focus
 * @return
*/
// =================================================

private List<NestedSection> getDecendentSectionZonesAux(NestedSection focus) {
  // TODO Auto-generated method stub
  return null;
}


// =================================================
  /**
   * createLevelSectionZone 
   * 
   * @param pJCas
   * @param pNestedSectionStructure
   * @param pParentSection 
  */
  // =================================================
  private void createLevelSectionZone(JCas pJCas, NestedSectionStructure pNestedSectionStructure, NestedSectionStructure pParentSection) {
   
    NestedSection statement = new NestedSection( pJCas);
    
    statement.setBegin( pNestedSectionStructure.getBeginOffset());
    statement.setEnd(   pNestedSectionStructure.getEndOffset());
    statement.setId( this.getClass().getName()+ "_" + this.annotationCtr++ );
    statement.setSectionName( pNestedSectionStructure.getSectionName());
    statement.setIndentation( pNestedSectionStructure.getSectionNumber() );
    if ( pParentSection != null ) {
      statement.setParentNestedSectionStructure( pParentSection.getIndexNumber());
      pNestedSectionStructure.setParentIndex(   pParentSection.getIndexNumber());
    }
    statement.addToIndexes();
    
    pNestedSectionStructure.setSectionZone( statement);
  
    
  } // end Method createLevelSectionZone() ----------


// =================================================
  /**
   * lineStartsWithLevel1 
   *  (level1) look for line that starts with a number and has Initial Cap camel cased words  
   *  for example: 
   *     12.00 Mental Disorders
   * 
   * @param pJCas
   * @param pLineText
   * @return boolean
  */
  // =================================================
  private final boolean lineStartsWithLevel1(JCas pJCas, String pLineText) {
    boolean returnVal = false;
    boolean firstTokenIsNumber = false;
    boolean looksLikeSectionName = false;
    boolean  indented = true;
    
    try {
    
      
    indented = U.isIndented( pLineText);
   
    String tokens[] = U.split( pLineText.trim(), " " );
    
    if ( tokens != null && tokens.length > 0 )
      if ( U.isNumberRelaxed(tokens[0]))
         firstTokenIsNumber = true;
    
     // looksLikeSectionName = looksLikeSectionName( tokens ) ;
    
      if ( firstTokenIsNumber && !indented )
        returnVal = true;
     
    } catch (Exception e) {
      e.printStackTrace();
    }
      
    return returnVal;
    
  } // end Method lineStartsWithLevel1() -------------


  // =================================================
/**
 * looksLikeSectionName returns true if each of the tokens are
 * either
 *    number
 *    function words (prep,determiners, ... )
 *    All Cap token
 *    Initial Cap token
 *   
 * 
 * @param pTokens
 * @return boolean
*/
// =================================================
private final boolean looksLikeSectionName(String[] pTokens) {
  boolean returnVal = true;
  
    if ( pTokens != null && pTokens.length > 0 )
      for ( String token : pTokens ) {
        if ( U.isNumber( token )) continue;
        if ( U.isAllCaps( token )) continue;
        if ( U.isInitialCap( token)) continue;
        if ( U.isFunctionWord( token )) continue;
        if ( U.isOnlyPunctuation( token )) continue;
      
        returnVal = false;
        break;
     
    } else 
      returnVal = false;
  
  return returnVal;
} // end Method looksLikeSectionName() -------------


  // =================================================
  /**
   * lineStartsWithLevel2 
   *   (level2) look for line that starts with a capital letter period space CapCased word     |A. How ...
   *   This level looks like a question and an answer 
   *   The first line ends with a ?
   *   
   * 
   * @param pLineText
   * @return boolean 
  */
  // =================================================
  private final boolean lineStartsWithLevel2(JCas pJCas, String pLineText ) {
   boolean returnVal = false;
   
   String tokens[] = U.split( pLineText.trim(), " " );
   boolean firstTokenIsUpperCaseLetter = false;
   boolean secondTokenIsUpperCasedToken = false;
   boolean endsWithQuestionMark = false;
   boolean endsWithColon = false;
   int someTokensLowerCased = 0;
   
   if ( tokens != null && tokens.length > 2 ) {
     if ( U.isAllCaps(tokens[0]))
       firstTokenIsUpperCaseLetter = true;
   
   if ( U.isInitialCap(tokens[1]))
     secondTokenIsUpperCasedToken = true;
   
   for ( int i = 2; i < tokens.length; i++ )
     if ( U.isAllLowerCase( tokens[i])) 
       someTokensLowerCased++;
   
   if ( pLineText.trim().endsWith("?"))
     endsWithQuestionMark = true;
   
   if ( pLineText.trim().endsWith(":"))
     endsWithColon = true;
   
   if (  firstTokenIsUpperCaseLetter && 
       secondTokenIsUpperCasedToken &&
       ( endsWithQuestionMark || endsWithColon) &&
       someTokensLowerCased > 1 )
     returnVal = true;
   
   }
  
    
   
   return returnVal;
  } // end Method lineStartsWithLevel2() ------------


  // =================================================
  /**
   * lineStartsWithLevel3 
   *              look for line that starts with indentation, number period space CapCasted word | __1. The
   
   * @param pJCas
   * @param pLineText
   * @return boolean
  */
  // =================================================
  private final boolean lineStartsWithLevel3(JCas pJCas, String pLineText) {
    
    boolean returnVal = false;
    
    
    String tokens[] = U.split( pLineText.trim(), " " );
    boolean firstTokenIsNumber = false;
    boolean isIndented = false;
    boolean secondTokenIsUpperCasedToken = false;
    int someTokensLowerCased = 0;
    
    if ( tokens != null && tokens.length > 2 ) {
      
      isIndented = U.isIndented(pLineText);
      
      
      if ( U.isNumberRelaxed(tokens[0].trim()))
        firstTokenIsNumber = true;
    
    if ( U.isInitialCap(tokens[1]))
      secondTokenIsUpperCasedToken = true;
    
    for ( int i = 2; i < tokens.length; i++ )
      if ( U.isAllLowerCase( tokens[i])) 
        someTokensLowerCased++;
    
    if (  firstTokenIsNumber && 
          isIndented &&
          secondTokenIsUpperCasedToken &&
          someTokensLowerCased > 0 )
      returnVal = true;
    
    }
   
    
    
    return returnVal ;
  } // end Method lineStartsWithLevel3() -------------


  // =================================================
  /**
   * lineStartsWithLevel4  
   *       look for line that starts with indentation, lower letter  spaced CapeCased word| __ __ a. Para
   * @param pJCas
   * @param pLine
   * @param pLineText
   * @return boolean
  */
  // =================================================
  private final boolean lineStartsWithLevel4(JCas pJCas, Annotation pLine , String pLineText) {
    
    boolean returnVal = false;
    
    String tokens[] = U.split( pLineText.trim(), " " );
    boolean startsWithListMarker = false;
    boolean firstTokenIsLowerCaseListMarker = false;
    boolean secondTokenIsUpperCasedToken = false;
    boolean indented = false;
    int someTokensLowerCased = 0;
    
   
    indented = U.isIndented(pLineText);
    
    if ( tokens != null && tokens.length > 2 ) {
      startsWithListMarker = doesStartWithListMarker( pLineText );
      if ( U.isAllLowerCase(tokens[0])) 
        firstTokenIsLowerCaseListMarker = true;
    
    if ( U.isInitialCap(tokens[1]))
      secondTokenIsUpperCasedToken = true;
    
    for ( int i = 2; i < tokens.length; i++ )
      if ( U.isAllLowerCase( tokens[i])) 
        someTokensLowerCased++;
    
    if (indented &&
        startsWithListMarker &&  
        firstTokenIsLowerCaseListMarker && 
        secondTokenIsUpperCasedToken &&
        someTokensLowerCased > 1 )
      returnVal = true;
    
    }
    
    
    return returnVal ;
  } // end Method lineStartsWithLevel4() -------------


  // =================================================
  /**
   * doesStartWithListMarker returns true if this line starts with a list marker
   * This method assumes that the sentence annotator has been run and has marked
   * list markers
   * 
   * 
   * @param pJCas
   * @param pLine
   * @return boolean
  */
  // =================================================
  private final boolean doesStartWithListMarkerObs(JCas pJCas, Annotation pLine) {
    boolean returnVal = false;
    
    try {
    
      List<Annotation> wordTokens = UIMAUtil.getAnnotationsBySpan( pJCas, WordToken.typeIndexID, pLine.getBegin(), pLine.getEnd() );
      if ( wordTokens != null && !wordTokens.isEmpty()) {
        Annotation firstWord = wordTokens.get(0);
        List<Annotation> theFirstListMarker = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ListDelimiter.typeIndexID, firstWord.getBegin(), firstWord.getEnd() );
        if ( theFirstListMarker != null && !theFirstListMarker.isEmpty() )
          returnVal = true;
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.debug_println("Something went wrong trying to figure out if the line starts with a list marker " + e.toString());
    }
      
    
    return returnVal;
  } // end Method doesStartWithListMarker() ---------

  
  // =================================================
  /**
   * doesStartWithListMarker returns true if this line starts with a list marker
   *    a.
   *    a)
   *    3.
   *    3)
   *    33.
   *    ii.
   *    xi.
   *    A.
   *    B.
   *    A)
   * 
   * @param pLineText
   * @return boolean
  */
  // =================================================
  private final boolean doesStartWithListMarker( String pLineText) {
    boolean returnVal = false;
   
    String[] tokens = U.split( pLineText.trim(), " ");
      if ( tokens != null && tokens.length > 1) {
        String firstWord = tokens[0].trim();
        if ( firstWord != null && (firstWord.endsWith(".") || firstWord.endsWith(")" )|| firstWord.endsWith("-"))) {
          firstWord = firstWord.substring(0, firstWord.length() -1);
          
          if ( U.isRomanNumerals(firstWord ))
            returnVal = true;
          else if ( U.isAllLowerCase(firstWord) || U.isAllCaps(firstWord) )
            if ( firstWord.length() < 3)
              returnVal = true;
              
          else if ( U.isNumber( firstWord ) || U.isRealNumber(firstWord))
            if ( firstWord.length() < 6)
              returnVal = true;
       }
     }
     
   
    return returnVal;
  } // end Method doesStartWithListMarker() ---------



  // =================================================
  /**
   * lineStartsWithLevel5 
   *           look for line that starts with indentation, roman numerals and CapCased word   | __ __ __ ii. To do 
   * 
   * @param pJCas
   * @param pLineText
   * @return boolean
  */
  // =================================================
  private final boolean lineStartsWithLevel5(JCas pJCas, String pLineText ) {
    
    boolean returnVal = false;
   
    String[] tokens = U. split( pLineText, " "); 
    boolean isRomanNumber = false;
    if ( tokens != null && tokens.length > 0 )
     isRomanNumber = U.isRomanNumerals( tokens[0].trim());
    
    if ( isRomanNumber )
      returnVal = true;
    
    return returnVal ;
  } // end Method lineStartsWithLevel5() -------------


  
  

//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}

  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
   
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
   
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  
  
  
  
} // end Class LineAnnotator() ---------------

