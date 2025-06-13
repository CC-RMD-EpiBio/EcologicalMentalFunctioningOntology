// =================================================
/**
 * MakeManualFromXFeatures creates manual annotations from
 * the x   features for the IPIR_yes 
 * and ComCog_yes Annotations.
 * 
 * Downstream annotators are looking for the x.__yes
 * versions, not the Manual____yes versions. 
 * 
 * The Manual versions are what get mapped in from
 * the GateReader.  
 * 
 *   
 * 
 * @author  GD
 * @created 2023.09.01
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.util.ArrayList;
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
import gov.nih.cc.rmd.inFACT.model.IPIR_yes;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MakeGoldFromIPIRFeatures;




public class MakeManualFromXFeatures extends JCasAnnotator_ImplBase {
 
  
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
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process","started MakeXFromManualFeatures");
    
    String documentId = VUIMAUtil.getDocumentId(pJCas);
    // ----------------------------
    // IPIR
    // ---------------------------
    
   
    // turn manual ipir mentions into x ipir sentence mentions
    List<Annotation> IPIRYess = getManual_IPIR_Yesses( pJCas );
    
    // turn manual comcog mentions into x comcog sentence mentions
    List<Annotation> ComcogYess = getManual_Comcog_Yesses( pJCas );
   
    
   
     
  //   Not needed ? 
    /*
    List<Annotation> comCogYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.manual.Comcog_yes.typeIndexID , false);
    
    if (comCogYess != null && !comCogYess.isEmpty() )

      for ( Annotation comcogYes : comCogYess ) 
        MakeGoldFromComCogFeatures.createComCogYes( pJCas, (gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes, this.comcogAnnotationSetName);
    
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," End MakeXFromManualFeatures");
    this.performanceMeter.stopCounter();
    
    */
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  } // end Method Process() ---------------------------
  
  
   // =================================================
  /**
   * getManual_Comcog_Yesses finds either framework comcog_yes, or manual comcog_yes
   * mentions and turns them into x comcog_yes sentences
   * 
   * 
   * @param pJCas
   * @return List<Annotation>
  */
  // =================================================
  private final List<Annotation> getManual_Comcog_Yesses(JCas pJCas) {
    List<Annotation> returnList = null;
    
    try {
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnList;
  } // end getManual_Comcog_yesses() -----------------


  // =================================================
  /**
   * getManual_IPIR_Yesses returns the manually annotated
   * ipir yes's to be turned into x ipir yes's.  
   * 
   * The issue is that under certain conditions, the reader
   * reads in ipir_yes's as x ipir yes's.  
   * 
   * These will have an annotation set name with something other than framework.
   * 
   * copy these x ipir yes's (with the non framework annotation set name), 
   * to manual ipir yes's.  remove the x ipir yes's
   * and return these manual ipir yes's.
   * 
   * (down stream processes will create x ipir yes's from the
   * manual ipir yes's)
   * 
   * @param pJCas
   * @return List<Annotation>
  */
  // =================================================
   private final List<Annotation> getManual_IPIR_Yesses(JCas pJCas) {
     
     List<Annotation> IPIRYess = null;
   try {
     IPIRYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.manual.IPIRyes.typeIndexID , false);
    
     if ( IPIRYess == null || IPIRYess.isEmpty())
      IPIRYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.model.IPIR_yes.typeIndexID, false );
    
    
    // If we've gotten here, and we are evaluating, and there are ipir yes's and we've not seen any
    // that is because they got picked up as inFACT.x.IPIR_yes's
    // convert them to manual yes's, 
    if ( IPIRYess == null || IPIRYess.isEmpty()) {
      IPIRYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.IPIR_yes.typeIndexID, false );
      if ( IPIRYess != null && !IPIRYess.isEmpty()) {
        List<Annotation> manualYesses = new ArrayList<Annotation>(IPIRYess.size());
        for ( Annotation reallyAManualYes : IPIRYess ) {
          String annotationSetName = ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) reallyAManualYes).getAnnotationSetName() ;
          if ( annotationSetName != null && !annotationSetName.equals(this.ontology_ipir_extraction_annotationSetName)) {
            Annotation aManualIPIRAnnotation = createManualIPIRYes( pJCas, reallyAManualYes);
            manualYesses.add( aManualIPIRAnnotation);
            reallyAManualYes.removeFromIndexes();
           
          } // end if the setname is not this.ontology_ipir_extraction 
        } // end loop thru ipir yess
        // clear the manual but x ipir x's, then move the manual ipir yes's to IPIRYess
        if ( manualYesses != null && !manualYesses.isEmpty()) {
          IPIRYess.clear();
          IPIRYess = manualYesses;
        } // end if
      } // if x ipir yes's found that were really manual ipir yes's
    } // end if there are any ipir yes's
   } catch (Exception e) {
     e.printStackTrace();
   }
   return IPIRYess;
   } // end Method getManual_IPIR_Yesses() ----------------
   
 

  // =================================================
  /**
   * createManualIPIRYes 
   * 
   * @param pJCas
   * @param reallyAManualYes
   * @return gov.nih.cc.rmd.inFACT.manual.IPIRyes
  */
  // =================================================
    private Annotation createManualIPIRYes(JCas pJCas, Annotation pXIPIR_yes) {
      gov.nih.cc.rmd.inFACT.manual.IPIRyes statement = new gov.nih.cc.rmd.inFACT.manual.IPIRyes(pJCas );
   
      statement.setBegin(pXIPIR_yes.getBegin() );
      statement.setEnd(pXIPIR_yes.getEnd() );
      statement.setId(  ((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getId() );
      statement.setAnnotationSetName(  ((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getAnnotationSetName() );
   
      statement.setD730(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getD730());
      statement.setD740(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getD740());
      statement.setD7400(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getD7400());
      statement.setD750(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getD750());
      statement.setD760(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getD760());
      statement.setD770(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getD770());
      statement.setD779(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getD779());
      statement.setInteraction(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getInteraction());

      statement.setTimeBucket(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getTimeBucket());
      statement.setDifficult_to_determine(((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes)pXIPIR_yes).getDifficult_to_determine());
      statement.addToIndexes();
     
   
    return statement;
  } // end Method createManualIPIRYes() ----

  // ----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
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
  
    
  } // end Method initialize() -------
 
  

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
   private String ontology_ipir_extraction_annotationSetName = "ontology_ipir_extraction";
   
 //  private String comcogAnnotationSetName = "comcog_category";
  // private String ipirAnnotationSetName = "ipir_category";
 
   
  
} // end Class LineAnnotator() ---------------

