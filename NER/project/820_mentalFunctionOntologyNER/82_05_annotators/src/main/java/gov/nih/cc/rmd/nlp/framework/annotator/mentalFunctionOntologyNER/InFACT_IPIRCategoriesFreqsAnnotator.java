// =================================================
/**
 * InFACT_ComcogCategoriesPostProcessing filters out mentions
 * that will not be needed downstream.
 * 
 * @author  GD
 * @created 2023.03.14
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;



import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class InFACT_IPIRCategoriesFreqsAnnotator extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process removes the originals that need to be replaced
   * by the newly made Ipir_yes annotations so that
   * the final gate file looks like the original Ipir_yes
   * have been updated with updated features
   * 
   * the criteria for removal/replacement is if
   *   the id, span, [maybe set name - for now, no]
   *  are the same.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();

    String msg0  = "x6|-------------------------------------------------------------------------------------------------------------------------\n";
    String msg1 = 
    	  "x6|" +
  		  U.spacePadLeft(6,"d710") + "|" + 
          U.spacePadLeft(6,"d730") + "|" +
  		  U.spacePadLeft(6,"d740") + "|" +
  		  U.spacePadLeft(6,"d7400") + "|" +
  		  U.spacePadLeft(6,"d750") + "|" +
  		  U.spacePadLeft(6,"d760") + "|" +
  		  U.spacePadLeft(6,"d770") + "|" +
  		  U.spacePadLeft(6,"d779") + "|"  + "\n";
    
    
   
    int d710_d720_count = 0;
    int d730_count = 0;
    int d740_count = 0;
    int d7400_count = 0;
    int d750_count = 0;
    int d760_count = 0;
    int d770_count = 0;
    int d779_count = 0;
    
    
    boolean d710_d720_seen = false;
    boolean d730_seen = false;
    boolean d740_seen = false;
    boolean d7400_seen = false;
    boolean d750_seen = false;
    boolean d760_seen = false;
    boolean d770_seen = false;
    boolean d779_seen = false;
    
    String documentId = VUIMAUtil.getDocumentId(pJCas);
    
    List<Annotation> comCogYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.IPIR_yes.typeIndexID , false);
                                                                
    
    if (comCogYess != null && !comCogYess.isEmpty() )
      for ( Annotation ipirYes : comCogYess ) {
    	
    	  String buff =  ipirYes.getCoveredText();
    	 
        if ( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) ipirYes).getInteraction  () ) {d710_d720_count++;   d710_d720_seen = true; }
        if ( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) ipirYes).getD730         () ) {d730_count++;   d730_seen = true; }
        if ( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) ipirYes).getD740         () ) {d740_count++;   d740_seen = true; }
        if ( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) ipirYes).getD7400        () ) {d7400_count++;  d7400_seen = true; }
        if ( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) ipirYes).getD750         () ) {d750_count++;   d750_seen = true; }
        if ( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) ipirYes).getD760         () ) {d760_count++;   d760_seen = true; }
        if ( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) ipirYes).getD770         () ) {d770_count++;   d770_seen = true; }
        if ( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) ipirYes).getD779         () ) {d779_count++;   d779_seen = true; }
        
        
        	GLog.println("x6|" +  documentId + "|" + buff );
        	 String msg2 = 
        			  "x6|" + 
        			 
        	  		  U.spacePadLeft( 6, String.valueOf(d710_d720_seen))  + "|" + 
        	          U.spacePadLeft( 6, String.valueOf(d730_seen))  + "|" +
        	  		  U.spacePadLeft( 6, String.valueOf(d740_seen))  + "|" +
        	  		  U.spacePadLeft( 6, String.valueOf(d7400_seen))  + "|" +
        	  		  U.spacePadLeft( 6, String.valueOf(d750_seen))  + "|" +
        	  		  U.spacePadLeft( 6, String.valueOf(d760_seen))  + "|" +
        	  		  U.spacePadLeft( 6, String.valueOf(d770_seen))  + "|" +
        	  		  U.spacePadLeft( 6, String.valueOf(d779_seen)) ;
        	  		  
        	  		 
        	 GLog.println( msg0 + msg1 + msg0 + msg2);
        	    
        
       
      }
        
    this._d710_d720_count+= d710_d720_count;
    this._d730_count+= d730_count;
    this._d740_count+= d740_count;
    this._d7400_count+= d7400_count;
    this._d750_count+= d750_count;
    this._d760_count+= d760_count;
    this._d770_count+= d770_count;
    this._d779_count+= d779_count;
    
   
    this.performanceMeter.stopCounter();
    
     
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", " Issue with ipir Categories" + e.toString());
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
   private PerformanceMeter performanceMeter = null;
   
   
   int _d710_d720_count = 0;
   int _d730_count = 0;
   int _d740_count = 0;
   int _d7400_count = 0;
   int _d750_count = 0;
   int _d760_count = 0;
   int _d770_count = 0;
   int _d779_count = 0;
   
  
   
   
   
  
} // end Class InFact_ComcogCategoriesPostProcessing() ---------------

