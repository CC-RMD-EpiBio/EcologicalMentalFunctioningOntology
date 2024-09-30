// =================================================
/**
 * CategoryPatterns 
 *
 * @author     Guy Divita
 * @created    Feb 18, 2023
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.util.ArrayList;
import java.util.List;


import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.*;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.CategoryPattern.PatternScope;

/**
 * @author divitag2
 *
 */
public class CategoryPatterns {

  public  List<CategoryPattern> docPatterns = null;
  public  List<CategoryPattern> pagePatterns = null;
  public  List<CategoryPattern> sectionPatterns = null;
  public  List<CategoryPattern> linePatterns = null;

  
  // =================================================
  /**
   * Constructor
   *
   * @param pPageCategoryPatternsFile
   * @throws Exception 
   * 
  **/
  // =================================================
  public CategoryPatterns(String pPageCategoryPatternsFile) throws Exception {
    
    initialize( pPageCategoryPatternsFile );
   
    
  } // end Constructor() ------------------------------

  
  // =================================================
  /**
   * Constructor
   *
   * @param pPageCategoryPatternsDir
   * @throws Exception 
   * 
  **/
  // =================================================
  public CategoryPatterns(String[] pArgs ) throws Exception {
    
    initialize( pArgs );
    
   
    
  } // end Constructor() ------------------------------

//=================================================
 /**
  * initialize 
  * @param pArgs
 * @throws Exception 
 */
 // =================================================
 public  void initialize(String[]  pArgs) throws Exception {


   String pageCategoryPatternsFileName = U.getOption(pArgs, "--pageCategoryPatternsFileName=", "./resources/vinciNLPFramework/pageFiltering/ssaFormsPatterns.csv");
   initialize(pageCategoryPatternsFileName );
   
 } // end Method initialize() ------------------------
  
  // =================================================
  /**
   * initialize 
   * @param pPageCategoryPatternsFile
   * @throws Exception 
  */
  // =================================================
  public  void initialize(String pPageCategoryPatternsFile) throws Exception {

    this.docPatterns = new ArrayList<CategoryPattern>();
    this.pagePatterns = new ArrayList<CategoryPattern>();
    this.sectionPatterns = new ArrayList<CategoryPattern>();
    this.linePatterns = new ArrayList<CategoryPattern>();
    
    loadPageFilteringPatterns( pPageCategoryPatternsFile );
    
  } // end Method initialize() ----------------------


  // =================================================
  /**
   * loadPageFilteringPatterns absorbs the csv file 
   * into a structure to use to filter out stuff
   * 
   * @param pPageCategoryPatternsFileName
   * @throws Exception 
  */
  // =================================================
  private  final void loadPageFilteringPatterns(String pPageCategoryPatternsFileName) throws Exception {
   
    try {
      
      loadPageFilteringPatternsAux( pPageCategoryPatternsFileName );
        
      
      
    } catch (Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, getClass(), "loadPageFilteringPatterns", "Issue loading the page filtering patterns " + e.toString());
      throw new Exception();
    }
    
  } // end Method loadPageFilteringPatterns() -------



  // =================================================
  /**
   * loadPageFilteringPatternsAux absorbs all csv files in the pageCategoryPatternsDir
   * into a structure to use to filter out stuff
   * 
   * @param aFileResource
   * @throws Exception 
  */
  // =================================================
  private final void loadPageFilteringPatternsAux(String aFileResource) throws Exception {
    
    try {
      
      String[] rows = U.readClassPathResourceIntoStringArray(aFileResource );
      
      if ( rows != null && rows.length > 0 )
        for ( int i = 0; i < rows.length; i++ ) 
          if ( rows[i] != null &&  !rows[i].startsWith("#") && rows[i].trim().length() > 0 ) {
            loadRow( rows[i]);
          }
     
      
    } catch (Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, getClass(), "loadPageFilteringPatterns", "Issue loading the page filtering patterns " + e.toString());
      throw new Exception();
    }
      
    
  } // end Method loadPageFilteringPatterns() --------

//=================================================
 /**
  * loadRow loads a pattern row
  * 
  * @param pRow
  * @throws Exception 
 */
 // =================================================
  public void loadRow(String pRow) throws Exception {
    
    try {
      CategoryPattern pattern = new CategoryPattern(  pRow);
    
      PatternScope spanScope =  pattern.getpatternScope();
    
      switch ( spanScope ) {
        case DOC     : this.docPatterns.add(       pattern); break;
        case PAGE    : this.pagePatterns.add (     pattern); break;
        case SECTION : this.sectionPatterns.add (  pattern); break;
        case LINE    : this.linePatterns.add(      pattern); break;
        default        : this.linePatterns.add(      pattern); 
      } // end switch()  
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadRow", "Issue loading row " + pRow + " " + e.toString() );
      throw e;
      
    }
      
 } // end Method load() ------------------------------

  
  
  

  // =================================================
  /**
   * main [TBD] summary
   * 
   * @param args
  */
  // =================================================

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }


  // =================================================
  /**
   * filterOutPattern 
   * 
   * @param pScanScope  (doc|page|section|line)  from 
   * @param pContent
   * @return boolean   true if the pattern is found
  */
  // =================================================
  public  boolean filterOutPattern(PatternScope pSpanScope, String pContent) {
   
    boolean returnVal = false;
    
    switch ( pSpanScope ) {
      case DOC     : returnVal = matchFromList( this.docPatterns, pContent ); break;
      case PAGE    : returnVal = matchFromList( this.pagePatterns, pContent ); break;
      case SECTION : returnVal = matchFromList( this.sectionPatterns, pContent ); break;
      case LINE    : returnVal = matchFromList( this.linePatterns, pContent ); break;
      default      :             matchFromList( this.linePatterns, pContent ); 
    } // end switch()  
      
    
    return returnVal;
  } // end Method filterOutPattern() -----------------


  // =================================================
  /**
   * matchFromList iterates through the pattern list 
   * to find a match for this content
   * 
   * @param pCategoryPatterns
   * @param pContent
   * @return boolean
  */
  // =================================================
  private final boolean matchFromList(List<CategoryPattern> pCategoryPatterns, String pContent) {
   boolean returnVal = false;
   
   if ( pCategoryPatterns != null && !pCategoryPatterns.isEmpty() )
     for ( CategoryPattern aRegEx : pCategoryPatterns ) {
      String aClass = aRegEx.classify(pContent);
      if ( aClass != null ) {
        returnVal = true;
        break;
      }
     }
   
   return returnVal;
  } // end Method matchFromList() ---------------------

} // end Class CategoryPatterns() ----------------------
