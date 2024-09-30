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



public class InFACT_ComCogCategoriesFreqsAnnotator extends JCasAnnotator_ImplBase {
 
	 String msg0  = "x6|-------------------------------------------------------------------------------------------------------------------------\n";
	    String msg1 = 
	    	  "x6|" +
	          U.spacePadLeft(38,"FileName") + "|" +
	  		  U.spacePadLeft(6,"d110") + "|" + 
	          U.spacePadLeft(6,"d130") + "|" +
	  		  U.spacePadLeft(6,"d160") + "|" +
	  		  U.spacePadLeft(6,"d163") + "|" +
	  		  U.spacePadLeft(6,"d166") + "|" +
	  		  U.spacePadLeft(6,"d170") + "|" +
	  		  U.spacePadLeft(6,"d172") + "|" +
	  		  U.spacePadLeft(6,"d175") + "|" +
	  		  U.spacePadLeft(6,"d177") + "|" +
	  		  U.spacePadLeft(6,"d179") + "|" +
	  		  U.spacePadLeft(6,"d210") + "|" +
	  		  U.spacePadLeft(6,"d230") + "|" +
	  		  U.spacePadLeft(6,"d240") + "|" +
	  		  U.spacePadLeft(6,"d310") + "|" +
	  		  U.spacePadLeft(6,"d330") + "|" +
	  		  U.spacePadLeft(6,"d350") + "|" +
	  		  U.spacePadLeft(6,"adap") + "|" +
	  		  U.spacePadLeft(6,"aplm") + "|" +
	  		  U.spacePadLeft(6,"pace") + "|" +
	  		  U.spacePadLeft(6,"pers") + "\n";
	    
  
  // -----------------------------------------
  /**
   * process removes the originals that need to be replaced
   * by the newly made Comcog_yes annotations so that
   * the final gate file looks like the original Comcog_yes
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

    String fileName = VUIMAUtil.getDocumentId(pJCas);
    
   
    int d110_count = 0;
    int d130_count = 0;
    int d160_count = 0;
    int d163_count = 0;
    int d166_count = 0;
    int d170_count = 0;
    int d172_count = 0;
    int d175_count = 0;
    int d177_count = 0;
    int d179_count = 0;
    int d210_count = 0;
    int d230_count = 0;
    int d240_count = 0;
    int d310_count = 0;
    int d330_count = 0;
    int d350_count = 0;
    int adap_count = 0;
    int aplm_count = 0;
    int pace_count = 0;
    int pers_count = 0;
    
    boolean d110_seen = false;
    boolean d130_seen = false;
    boolean d160_seen = false;
    boolean d163_seen = false;
    boolean d166_seen = false;
    boolean d170_seen = false;
    boolean d172_seen = false;
    boolean d175_seen = false;
    boolean d177_seen = false;
    boolean d179_seen = false;
    boolean d210_seen = false;
    boolean d230_seen = false;
    boolean d240_seen = false;
    boolean d310_seen = false;
    boolean d330_seen = false;
    boolean d350_seen = false;
    boolean adap_seen = false;
    boolean aplm_seen = false;
    boolean pace_seen = false;
    boolean pers_seen = false;
    String documentId = VUIMAUtil.getDocumentId(pJCas);
    
    List<Annotation> comCogYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.manual.Comcog_yes.typeIndexID , false);
                                      
    int ctr= 0;
    
    if (comCogYess != null && !comCogYess.isEmpty() )
      for ( Annotation comcogYes : comCogYess ) {
    	  String annotationId = ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes)comcogYes).getId();
    	  GLog.println( ctr++ + "|" + annotationId + "| Annotation start=" + comcogYes.getBegin());
    	  String annotationSetName = ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes)comcogYes).getAnnotationSetName();
    	//  GLog.println("x9: AnnotationSetName=" + annotationSetName);
    	  //String buff =  comcogYes.getCoveredText();
    	 /*
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD110_d129    () ) {d110_count++;   d110_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD130_d159    () ) {d130_count++;   d130_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD160         () ) {d160_count++;   d160_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD163         () ) {d163_count++;   d163_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD166         () ) {d166_count++;   d166_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD170         () ) {d170_count++;   d170_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD172         () ) {d172_count++;   d172_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD175         () ) {d175_count++;   d175_seen = true; }} catch (Exception e) {}
        */
        try { 
        	if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD177         () )
        	{	
        		d177_count++;  
        		GLog.println( "offset = " + comcogYes.getBegin());
        		d177_seen = true;
        	}
        	} catch (Exception e) { 
        		e.printStackTrace();
        		System.err.println("issue " + e.toString());
        			}
        /*
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD179         () ) {d179_count++;   d179_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD210_d220    () ) {d210_count++;   d210_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD230         () ) {d230_count++;   d230_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD240         () ) {d240_count++;   d240_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD310_d329    () ) {d310_count++;   d310_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD330_d349    () ) {d330_count++;   d330_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD350_d369    () ) {d350_count++;   d350_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getApplied_memory()) {aplm_count++;   aplm_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getAdaptation   () ) {adap_count++;   adap_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getPacing       () ) {pace_count++;   pace_seen = true; }} catch (Exception e) {}
        try { if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getPersistence  () ) {pers_count++;   pers_seen = true; }} catch (Exception e) {}
    	*/
       
       
      }
    String msg2 = 
			  "x6|" + 
              U.spacePadLeft(38, fileName                  )  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d110_count))  + "|" + 
	          U.spacePadLeft( 6, String.valueOf(d130_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d160_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d163_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d166_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d170_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d172_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d175_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d177_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d179_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d210_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d230_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d240_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d310_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d330_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(d350_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(adap_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(aplm_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(pace_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(pers_count))  + "\n";
	 GLog.println(  msg2);
        
    this._d110_count+= d110_count;
    this._d130_count+= d130_count;
    this._d160_count+= d160_count;
    this._d163_count+= d163_count;
    this._d166_count+= d166_count;
    this._d170_count+= d170_count;
    this._d172_count+= d172_count;
    this._d175_count+= d175_count;
    this._d177_count+= d177_count;
    this._d179_count+= d179_count;
    this._d210_count+= d210_count;
    this._d230_count+= d230_count;
    this._d240_count+= d240_count;
    this._d310_count+= d310_count;
    this._d330_count+= d330_count;
    this._d350_count+= d350_count;
    this._adap_count+= adap_count;
    this._aplm_count+= aplm_count;
    this._pace_count+= pace_count;
    this._pers_count+= pers_count;
   
    
    
     
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", " Issue with comcog Categories" + e.toString());
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
	
    
	String msg2 = 
			  "x6|" + 
              U.spacePadLeft(38, " "                    )  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d110_count))  + "|" + 
	          U.spacePadLeft( 6, String.valueOf(this._d130_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d160_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d163_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d166_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d170_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d172_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d175_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d177_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d179_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d210_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d230_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d240_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d310_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d330_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._d350_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._adap_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._aplm_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._pace_count))  + "|" +
	  		  U.spacePadLeft( 6, String.valueOf(this._pers_count))  + "\n";
	 GLog.println(   msg1 + msg2 + msg1);
     
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
    
    GLog.println(msg0 + msg1);
   
      
  } // end Method initialize() -------
 

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
   
   
   int _d110_count = 0;
   int _d130_count = 0;
   int _d160_count = 0;
   int _d163_count = 0;
   int _d166_count = 0;
   int _d170_count = 0;
   int _d172_count = 0;
   int _d175_count = 0;
   int _d177_count = 0;
   int _d179_count = 0;
   int _d210_count = 0;
   int _d230_count = 0;
   int _d240_count = 0;
   int _d310_count = 0;
   int _d330_count = 0;
   int _d350_count = 0;
   int _adap_count = 0;
   int _aplm_count = 0;
   int _pace_count = 0;
   int _pers_count = 0;
   int missed_appliedMemory = 0;
  
   
  
} // end Class InFact_ComcogCategoriesPostProcessing() ---------------

