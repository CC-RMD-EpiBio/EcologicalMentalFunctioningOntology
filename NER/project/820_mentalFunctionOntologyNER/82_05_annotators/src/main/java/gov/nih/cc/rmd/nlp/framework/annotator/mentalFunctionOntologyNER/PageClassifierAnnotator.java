// =================================================
/**
 * PageClassifierAnnotator uses regular expressions and
 * exact matches defined in the csv resource (--pagecategoryPatternsDir= ./resources/vinciNLPFramework/pageClassification )
 * to mark pages that match these patterns
 * 
 * This method marks the documentHeader, Page, Section, Line
 * with  
 *  processMe=true  or processMe=false 
 *    
 * or it removes all annotations if 
 * processMe=false
 *   
 * 
 * @author  GD
 * @created 2023.02.15
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.va.chir.model.DocumentHeader;




public class PageClassifierAnnotator extends JCasAnnotator_ImplBase {
 
  
  
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
 
    DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
    documentHeader.setProcessMe(true);
   
    String documentContent = pJCas.getDocumentText();
    
    // ----------------------------------------
    // Doc classification and filtering at the same time
    StringBuffer docCategories = new StringBuffer();
    boolean finalProcessMe = true;
    for ( CategoryPattern candidatePattern :  this.categoryPatterns.docPatterns ) {
      String matchedPatternNameAndProcessMe =  candidatePattern.classify( documentContent );
      
      if ( matchedPatternNameAndProcessMe != null ) {
        String[] cols = U . split (matchedPatternNameAndProcessMe );
        String matchedPatternName = cols[0];
        String processMe = cols[1];
        
        String mBegin = cols[2];
        String mEnd   = cols[3];
        String mCaught = cols[4];
        
        docCategories.append("[" + matchedPatternName + ":" + mCaught + "]");
        if ( processMe.contains("false") )
          finalProcessMe = false;
      
        
        // I could make annotations out of the above pieces 
        
      }
    }
    
    // -------------------------------------------------
    // Page classification and filtering at the same time
    // ----------------------------------------
    // Doc classification and filtering at the same time

    for ( CategoryPattern candidatePattern :  this.categoryPatterns.pagePatterns ) {
   
      String matchedPatternNameAndProcessMe =  candidatePattern.classify( documentContent );
      
      if ( matchedPatternNameAndProcessMe != null ) {
        String[] cols = U . split (matchedPatternNameAndProcessMe );
        String matchedPatternName = cols[0];
        String processMe = cols[1];
        String mBegin = cols[2];
        String mEnd   = cols[3];
        String mCaught = cols[4];
        docCategories.append("[" + matchedPatternName + ":" + mCaught + "]");
     
        if ( processMe.contains("false") )
          finalProcessMe = false;
        // documentHeader.setDocumentType(matchedPatternName);
       //  documentHeader.setProcessMe( Boolean.valueOf(processMe));
      }
    }
    
    documentHeader.setDocumentType(docCategories.toString());
    documentHeader.setProcessMe(  finalProcessMe );
   
    // documentHeader.setDocumentType(pageCategories.toString());
    
    // -------------------------------------------------
    // Section classification and filtering at the same time
    // [TBD]
    
    // -------------------------------------------------
    // Line classification and filtering at the same time
    // [TBD]
    
  
  
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   



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
   *                
   *   This has an optional parameter, a resource directory with pattern files in it
   *   --pagecategoryPatternsDir= ./resources/vinciNLPFramework/pageFiltering
   *
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    String pageCategoryPatternsFileName = U.getOption(pArgs, "--pageCategoryPatternsFileName=", "resources/vinciNLPFramework/pageFiltering/ssaFormsPatterns.csv");
    
   
    
    try {
      this.categoryPatterns = new CategoryPatterns ( pageCategoryPatternsFileName );
    } catch (Exception e) {
      throw new ResourceInitializationException();
    }
   
   
      
  } // end Method initialize() -------
 
  


  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
   private CategoryPatterns categoryPatterns = null;
  
   
  
} // end Class LineAnnotator() ---------------

