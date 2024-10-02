package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.BehaviorEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.EmotionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.IPIRCategories;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.InFACT_IPIRCategoriesPostProcessing;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MentalFunctionOntologyCategoryFilter;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MentalFunctionOntologyNERAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.PageClassifierAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.SupportEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.UseInFACTSentencesAnnotator;
//import gov.nih.cc.rmd.nlp.framework.annotator.pos.POSAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.U;

// =================================================
/**
 * MentalFunctioningOntologyNERPipeline runs the pipeline for mentalFunctionOntologyNER
 *        
 * 
 * @author  G.o.d.
 * @created 2022.03.28
 *
 */


  public class InFACTIPIRCategorizationPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public InFACTIPIRCategorizationPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public InFACTIPIRCategorizationPipeline()  throws Exception {
     super( );
      
    } // End Constructor() -----------------------------
    
    
	// =======================================================
	/**
	 * createPipeline defines the pipeline
	 * 
	 * @param pArgs
	 * @return FrameworkPipeline
	 */
	// =======================================================
	@Override
  public FrameworkPipeline createPipeline(String[] pArgs) {

	    FrameworkPipeline pipeline = new FrameworkPipeline(pArgs);
	    UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);
	    String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.mentalFunctionOntologyNER.IPIRCategoriesModel"); //  "gov.nih.cc.rmd.FrameworkAndCtakes");
      pipeline.setTypeDescriptorClassPath( typeDescriptor);                                                     //<--- in the 82_03-type.descriptor/src/main/resources ... folder
	   

      pipeline.add(              PageClassifierAnnotator.class.getCanonicalName(), argsParameter);
      
      FrameworkRMDSentenceNoFilterPipeline.setPipeline(pipeline, pArgs);
    
      pipeline.add(              UseInFACTSentencesAnnotator.class.getCanonicalName(), argsParameter);
      
      
 //     pipeline.add(              POSAnnotator.class.getCanonicalName(), argsParameter);
  
      pipeline.add(              BehaviorEvidenceAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(              EmotionEvidenceAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(              SupportEvidenceAnnotator.class.getCanonicalName(), argsParameter);
      
	    pipeline.add(              MentalFunctionOntologyNERAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              MentalFunctionOntologyCategoryFilter.class.getCanonicalName(), argsParameter);  
	    
	    pipeline.add(              IPIRCategories.class.getCanonicalName(), argsParameter);  
	    pipeline.add(              InFACT_IPIRCategoriesPostProcessing.class.getCanonicalName(), argsParameter ); //<--- for inFACT GATE files
	    
	    pipeline.add(              FilterWriter.class.getCanonicalName(), argsParameter);  
	    
   
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

