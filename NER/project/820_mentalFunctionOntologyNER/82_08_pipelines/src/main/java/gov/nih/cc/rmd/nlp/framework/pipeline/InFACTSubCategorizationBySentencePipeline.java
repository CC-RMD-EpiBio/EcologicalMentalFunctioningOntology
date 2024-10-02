package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.AssertionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotatorWithBlankLines;
import gov.nih.cc.rmd.nlp.framework.annotator.PersonAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator2;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator3;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueWithinListAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValuesInSectionsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TermAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenSlashRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDAPanelSectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SectionNameInTermAttributeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceSectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.UnlabledSectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.AbsoluteDateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateAndTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateAndTimeByTokenAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateByLookupAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.BehaviorEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.ComCogCategories;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.EmotionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.IPIRCategories;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.InFACT_ComCogCategoriesPostProcessing;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.InFACT_IPIRCategoriesPostProcessing;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MakeXFromManualFeatures;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MentalFunctionOntologyNERAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.SupportEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.UseInFACTSentencesAnnotator;
// import gov.nih.cc.rmd.nlp.framework.annotator.pos.POSAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.BTRIS_RedactionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.PersonTokensAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.PunctuationTermsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.RegexShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.TermShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.shapes.UnitOfMeasureAnnotator;
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


  public class InFACTSubCategorizationBySentencePipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public InFACTSubCategorizationBySentencePipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public InFACTSubCategorizationBySentencePipeline()  throws Exception {
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
	    String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.mentalFunctionOntologyNER.SubCategoriesModel"); //  "gov.nih.cc.rmd.FrameworkAndCtakes");
      pipeline.setTypeDescriptorClassPath( typeDescriptor);                                                     //<--- in the 82_03-type.descriptor/src/main/resources ... folder
	   
     
     
     
      // pipeline.add(              PageClassifierAnnotator.class.getCanonicalName(), argsParameter);
      
   
      pipeline.add(              MakeXFromManualFeatures.class.getCanonicalName(), argsParameter);
      pipeline.add(              UseInFACTSentencesAnnotator.class.getCanonicalName(), argsParameter);
      
      
      //-----------------------

      
      pipeline.add(   LineAnnotatorWithBlankLines.class.getCanonicalName(), argsParameter ) ;
      pipeline.add(          RegexShapeAnnotator.class.getCanonicalName(), argsParameter  );
      pipeline.add(          AbsoluteDateAnnotator.class.getCanonicalName(), argsParameter );
      pipeline.add(           DateAndTimeAnnotator.class.getCanonicalName(), argsParameter );
    
      pipeline.add(                TokenAnnotator.class.getCanonicalName(), argsParameter );
      pipeline.add(      BTRIS_RedactionAnnotator.class.getCanonicalName(), argsParameter );
      pipeline.add(     TokenSlashRepairAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(         DateByLookupAnnotator.class.getCanonicalName(), argsParameter );
      pipeline.add(   DateAndTimeByTokenAnnotator.class.getCanonicalName(), argsParameter );
      
     // pipeline.add(             CheckBoxAnnotator.class.getCanonicalName(), argsParameter ) ;
       pipeline.add(            SlotValueAnnotator.class.getCanonicalName(), argsParameter );
       pipeline.add(            SlotValueRepairAnnotator2.class.getCanonicalName(), argsParameter );
       pipeline.add(            SlotValueRepairAnnotator3.class.getCanonicalName(), argsParameter );
       pipeline.add(            SlotValueWithinListAnnotator.class.getCanonicalName(), argsParameter );
       pipeline.add(            SlotValuesInSectionsAnnotator.class.getCanonicalName(), argsParameter );
       
       
      
      pipeline.add(                 TermAnnotator.class.getCanonicalName(), argsParameter );
      pipeline.add(    AssertionEvidenceAnnotator.class.getCanonicalName(), argsParameter);
      
      pipeline.add(        UnitOfMeasureAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(            TermShapeAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(      PunctuationTermsAnnotator.class.getCanonicalName(), argsParameter);
      
      pipeline.add(               PersonAnnotator.class.getCanonicalName(), argsParameter); 
      
      pipeline.add(         PersonTokensAnnotator.class.getCanonicalName(), argsParameter); //<--- new tokens are created in term - need to do it again
    
      
      pipeline.add(     CCDASectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(CCDAPanelSectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(          CCDASectionsAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add( SentenceSectionRepairAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add( UnlabledSectionRepairAnnotator.class.getCanonicalName(), argsParameter);
      
   // pipeline.add(   QuotedUtteranceAnnotator.class.getCanonicalName(), argsParameter ) ;
      
     
      pipeline.add(            AssertionAnnotator.class.getCanonicalName(), argsParameter);  
      pipeline.add(SectionNameInTermAttributeAnnotator.class.getCanonicalName(), argsParameter);  
      
     // ------------------------
      
      
      
    
      
   
       
  //    pipeline.add(              POSAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(              BehaviorEvidenceAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(              EmotionEvidenceAnnotator.class.getCanonicalName(), argsParameter);
      pipeline.add(              SupportEvidenceAnnotator.class.getCanonicalName(), argsParameter);
      
	    pipeline.add(              MentalFunctionOntologyNERAnnotator.class.getCanonicalName(), argsParameter);
	 
	    pipeline.add(              IPIRCategories.class.getCanonicalName(), argsParameter);  
	    
	    pipeline.add(              ComCogCategories.class.getCanonicalName(), argsParameter);  
     
   
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

