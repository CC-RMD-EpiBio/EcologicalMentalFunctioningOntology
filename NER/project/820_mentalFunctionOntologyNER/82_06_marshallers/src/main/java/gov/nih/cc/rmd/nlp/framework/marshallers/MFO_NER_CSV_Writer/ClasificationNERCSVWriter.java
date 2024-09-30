//=================================================
/**
 * ClassificationNERCSVWriter creates a concordance for  mentions output
 * 
 * @author  GD
 * @created 2023.03.20
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.MFO_NER_CSV_Writer;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.NestedSection;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MentalFunctioningOntology;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.vinci.model.Concept;



public class ClasificationNERCSVWriter extends AbstractWriter {





  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   * 
  **/
  // =================================================
  public ClasificationNERCSVWriter(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  } // end Constructor() ----------------------



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

    String fileName = VUIMAUtil.getDocumentId(pJCas);
    fileName =  U.getFileNameOnly(fileName);
    // for each doc
        // instance for each category 
        // count the total each category
        // count the total unique category

    List<String> instanceRows = getInstanceRows( pJCas, fileName);
   
    printInstances( instanceRows);
    
     
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   



// =================================================
  /**
   * printInstances 
   * 
   * @param instanceRows
  */
  // =================================================
  private synchronized void printInstances(List<String> instanceRows) {
    
    for ( int i = 0; i < instanceRows.size(); i++ ) {
      this.instanceOut.print( instanceRows.get(i)+ "\n" );
      this.instanceOut.print( '\n');
    //  this.instanceOut.flush();
    }
  
    //this.instanceOut.flush();
  } // end Method printInstances() ---------------


 
  // =================================================
  /**
   * getInstanceRows returns   fileName | category |mention |beginOffset |End offset |AssertionStatus|SubjectIs|AttributedTo
   * 
   * @param pJCas
   * @return List<String>
  */
  // =================================================
   private List<String> getInstanceRows(JCas pJCas, String pFileName) {
  
      
     List<String> returnVal = new ArrayList<String>();
    
     // grab all Ontology Mentions 
     List<Annotation> instances = UIMAUtil.getAnnotations(pJCas, MentalFunctioningOntology.typeIndexID, true );
     
     if ( instances != null && !instances.isEmpty())
       for ( Annotation instance : instances ) {
         
         
         StringBuffer buff = new StringBuffer();
         
         String sectionName = getSectionName( pJCas, instance);
         if ( sectionName == null)
           sectionName = " ";
         
         String category = U.getClassName( instance.getClass().getName() );
         
         if ( category.equals("MentalFunctioningMention"))
           continue;
         
         String contextLeft = UIMAUtil.getLeftContext(pJCas, instance, 50);
         String contextRight = UIMAUtil.getRightContext(pJCas, instance, 50);
         contextLeft = contextLeft.replaceAll(",",  "-");
         contextRight = contextRight.replaceAll(",",  "-");
         String subjectStatus = (( MentalFunctioningOntology) instance).getSubjectStatus();
         if ( subjectStatus == null) subjectStatus = " ";
         String assertionStatus =  (( MentalFunctioningOntology) instance).getAssertionStatus(); 
         if ( assertionStatus == null) assertionStatus = " ";
         String attributedTo =  String.valueOf((( MentalFunctioningOntology) instance).getAttributedToPatient());
         if ( attributedTo == null) attributedTo = " ";
         String supportingEvidence =  getSupportingEvidence( pJCas, instance ); 
         if (supportingEvidence == null ) supportingEvidence = " ";
         
         String mention = instance.getCoveredText();
         if ( mention != null ) {
           mention = U.normalize(mention);
           mention = mention.replace(COLUMN_DELIMITER, "-" );
          
           if ( this.highlightMentions  ) 
             mention = ">>>" + mention + "<<<";
         }
         String beginOffset =  U.spacePadLeft(8, String.valueOf( instance.getBegin() ));
         String endOffset   =  U.spacePadLeft(8, String.valueOf( instance.getEnd() ));
         buff.append(  U.spacePadLeft(10, String.valueOf(this.rowNum++)));    buff.append( COLUMN_DELIMITER );
         
         if ( this.showFileName ) {
        	 buff.append(  U.spacePadLeft(57, pFileName));    buff.append( COLUMN_DELIMITER );
         }
         
         buff.append(  U.spacePadLeft(30, category ));    buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft( 80, mention));      buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft( 80,supportingEvidence)); buff.append( COLUMN_DELIMITER );
         
      
         buff.append(  U.spacePadLeft(10, sectionName));  buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(10,subjectStatus));  buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(20,assertionStatus));buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(20,attributedTo));  //  buff.append( COLUMN_DELIMITER );
         
    //     fileName, category, mention, supportingEvidence, sectionName, subjectStatus, assertionStatus, attributedTo
        
        
         
         
         returnVal.add( buff.toString());
         
       } // end loop through instances
    
    return returnVal;
  } // end Method getRows() --------------------------
  
  
  // =================================================
  /**
   * getInstanceRows returns   fileName | category |mention |beginOffset |End offset |AssertionStatus|SubjectIs|AttributedTo
   * 
   * @param pJCas
   * @return List<String>
  */
  // =================================================
   private List<String> getInstanceRowsObs(JCas pJCas, String pFileName) {
  
      
     List<String> returnVal = new ArrayList<String>();
    
     // grab all Ontology Mentions 
     List<Annotation> instances = UIMAUtil.getAnnotations(pJCas, MentalFunctioningOntology.typeIndexID, true );
     
     if ( instances != null && !instances.isEmpty())
       for ( Annotation instance : instances ) {
         
         
         StringBuffer buff = new StringBuffer();
         
         String sectionName = getSectionName( pJCas, instance);
         
         String category = U.getClassName( instance.getClass().getName() );
         
         if ( category.equals("MentalFunctioningMention"))
           continue;
         
         String contextLeft = UIMAUtil.getLeftContext(pJCas, instance, 50);
         String contextRight = UIMAUtil.getRightContext(pJCas, instance, 50);
         contextLeft = contextLeft.replaceAll(",",  "-");
         contextRight = contextRight.replaceAll(",",  "-");
         String subjectStatus = (( MentalFunctioningOntology) instance).getSubjectStatus();
         String assertionStatus =  (( MentalFunctioningOntology) instance).getAssertionStatus();
         String attributedTo =  String.valueOf((( MentalFunctioningOntology) instance).getAttributedToPatient());
         String supportingEvidence =  getSupportingEvidence( pJCas, instance ); 
         if ( supportingEvidence == null )
           supportingEvidence = " ";
         else
           System.err.println( "supportingEvidence = " + supportingEvidence) ;
         
         String mention = instance.getCoveredText();
         if ( mention != null ) {
           mention = mention.replaceAll(",", "-" );
           mention = U.spacePadRight(44,  mention );
           if ( this.highlightMentions  ) 
             mention = ">>>" + mention + "<<<";
         }
         String beginOffset =  U.spacePadLeft(8, String.valueOf( instance.getBegin() ));
         String endOffset   =  U.spacePadLeft(8, String.valueOf( instance.getEnd() ));
         
         buff.append(  U.spacePadLeft(57, pFileName));    buff.append( COLUMN_DELIMITER );
         buff.append(  U.spacePadLeft(40, sectionName));  buff.append( COLUMN_DELIMITER );
         buff.append(  U.spacePadLeft(30, category ));    buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(50,  contextLeft));  buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(80,  mention));      buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(50,  contextRight)); buff.append( COLUMN_DELIMITER );
         buff.append(                     beginOffset);   buff.append( COLUMN_DELIMITER );
         buff.append(                       endOffset);   buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(10,subjectStatus));  buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(20,assertionStatus));buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(20,attributedTo));   buff.append( COLUMN_DELIMITER );
         buff.append( supportingEvidence );
         
         returnVal.add( buff.toString());
         
       } // end loop through instances
    
    return returnVal;
  } // end Method getRows() --------------------------

   
  // =================================================
/**
 * getSupportingEvidence - question:  do we want to see the evidence class, or just the mention or both?
 * 
 * @param pJCas
 * @param pMention
 * @return String colon delimited pieces of evidence -   
*/
// =================================================
private String getSupportingEvidence(JCas pJCas, Annotation pMention) {
  
  String returnVal = null;
  
  // for the mention
  //    get the sentence
  //    for the sentence
  //        get Evidence 
  //        for the evidence
  //           print:  [EvidenceClass:evidence],[] ...
  
  //  how to get evidence generically?  - get class name, does it end with evidence?
  
  Annotation sentence = VUIMAUtil.getSentence( pJCas, pMention);
  
  List<Annotation> evidences = null;
  if ( sentence != null ) {
     List<Annotation> allMentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Concept.typeIndexID, sentence.getBegin(), sentence.getEnd(), true);
     
     if ( allMentions != null && !allMentions.isEmpty()) {
       evidences = new ArrayList<Annotation>();
       for ( Annotation mention: allMentions ) {
         if ( mention.getClass().getName().endsWith("Evidence") )
             evidences.add( mention);
       } // loop through all mentions
     }
  }
  
  // format the evidences
  StringBuffer buff = new StringBuffer();
  if ( evidences != null && !evidences.isEmpty())
    for ( Annotation evidence : evidences ) 
      buff.append(  "[" + evidence.getClass().getSimpleName() + ":" + evidence.getCoveredText() + "]" + ":" );
  
  if ( buff != null)
    returnVal = buff.toString();
  
  return returnVal;
} // end Method getSupportingEvidence() --------------



  // =================================================
   /**
    * getSectionName retrieves the highest section name in the instance
    *    looking for something like 12.02 
    *    This was useful for processing the ssa listings, where the
    *    sections names were numbers if we are processing ssa listings.
    *    
    *   
    *    
    *    If processing clincial documents, (the default now) 
    *    we want to go with non-nested section names at the name
    *    and level they occur
    * 
    * @param pJCas
    * @param instance
    * @return String
    */
   // =================================================
   private String getSectionName(JCas pJCas, Annotation pInstance) {
       
     String returnVal = null;
     if ( this.processingListings ) {
       returnVal = getSectionNumber( pJCas, pInstance);
     } else {
       returnVal = getSectionNameAux( pJCas, pInstance );
     }
     return returnVal;
   } // end getSectionName() -----------------------
         
   

// =================================================
  /**
   * getSectionNameAux For this mention, what's the closest
   * embedded section name
   * 
   * This retrieves the last created sectionZone's sectionName.
   * 
   * @param pJCas
   * @param pInstance
   * @return String 
  */
  // =================================================
  private String getSectionNameAux(JCas pJCas, Annotation pInstance) {
  
    String sectionName = null;
    
    List<Annotation> sectionZones = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SectionZone.typeIndexID, pInstance.getBegin(), pInstance.getEnd(), false);

   
    if ( sectionZones!= null && !sectionZones.isEmpty()) 
      for ( Annotation sectionZone : sectionZones ) 
        sectionName = ((SectionZone) sectionZone).getSectionName();
   
    
    return sectionName;
    
  } // end Method getSectionNameAux() --------------


// =================================================
/**
 * getSectionNumber retrieves the highest section name in the instance
 *    looking for something like 12.02 
 *    This was useful for processing the ssa listings, where the
 *    sections names were numbers.
 *    
 *    In clincial documents, we want to go with non-nested sections
 * 
 * @param pJCas
 * @param instance
 * @return String
*/
// =================================================
private String getSectionNumber(JCas pJCas, Annotation pInstance) {
  String returnVal = null;
  
    StringBuffer buff = new StringBuffer();
    List<Annotation> nestedSections = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  NestedSection.typeIndexID, pInstance.getBegin(), pInstance.getEnd(), false);

    if ( nestedSections!= null && !nestedSections.isEmpty()) {
      for ( Annotation section : nestedSections ) {
        // -------------------
        // look for the most specific numbered section
       String sectionName = ((NestedSection) section).getSectionName();
       buff.append(sectionName + ": ");
      }
    }
    if ( buff != null && buff.length()> 0 ) {
      returnVal = buff.toString();
      returnVal = returnVal.substring(0, returnVal.length() -2);
    }
  
  return returnVal; 
} // end Method getSectionNumber() -----------------


//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  
  this.instanceOut.close();
  
  
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}






  //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
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
      
   
    
      
    } // end Method initialize() --------------



    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir ) throws ResourceInitializationException {
      
    
      String args[] = new String[1];
      args[0] = "--ouputDir=" + pOutputDir;
      
      initialize( args);
    
      
      
    } // end Method initialize() --------------


    


    
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
      this.outputDir = U.getOption(pArgs, "--outputDir=", "./output" ); 
      this.outputDir = this.outputDir + "/stats";
      this.processingListings = Boolean.parseBoolean( U.getOption( pArgs,  "--processListings=", "false"));
      this.highlightMentions = Boolean.parseBoolean(U.getOption(pArgs,  "--highlightMentions=", "false")); 
      this.showFileName = Boolean.parseBoolean(U.getOption(pArgs,  "--showFileName=", "false")); 
      String columnDelimiter = U.getOption(pArgs,  "--columnDelimiter=", "PIPE");   //  COMMA|PIPE
      
      if ( columnDelimiter == null )
        COLUMN_DELIMITER = "|";
      else if ( columnDelimiter.equals("COMMA"))
        COLUMN_DELIMITER = ",";
        else if ( columnDelimiter.equals("PIPE"))
          COLUMN_DELIMITER = "|";
      else 
        COLUMN_DELIMITER = "|";
      
      
      try {
        
        U.mkDir(outputDir);
        
      initializeColumnNames( );
      initializeInstanceFiles( );
      
     
      } catch (Exception e) {
        throw new ResourceInitializationException( e);
      }
     
        
    } // end Method initialize() -------
   
    // =================================================
    /**
     * initialize_instanceFile opens up the file that
     * has the instance counts
     * @throws Exception 
     * 
    */
    // =================================================
    private final void initializeInstanceFiles() throws Exception {
      try {
        
        long threadId = Thread.currentThread().getId();
      
        
        String instanceFileName = this.outputDir + "/ClassificationConcordance_" + threadId + ".csv" ; 
        this.instanceOut = new PrintWriter ( instanceFileName );
       
       
        instanceOut.print(  this.InstanceColumnNames + "\n");
       
       
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println (GLog.ERROR_LEVEL, this.getClass(), "initializeInstanceFile", "Something went wrong with opening the instance file " + e.toString());
        throw e;
      }
      
    } // end Method initialie_instanceFile() -----------


    // =================================================
    /**
     * initializeColumnNames 
     * 
    */
    // =================================================
    private void initializeColumnNames() {
      
      StringBuffer buff2 = new StringBuffer();
      buff2.append( U.spacePadRight(10, "RowNum"));         buff2.append(COLUMN_DELIMITER );
      if ( this.showFileName ) {
    	  buff2.append( U.spacePadRight(57, "FileName"));       buff2.append(COLUMN_DELIMITER );
      }
      buff2.append( U.spacePadRight(30, "Category"));       buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(80, "Mention"));        buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(80, "SupportingEvidence"));   buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(10, "Section"));        buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(10, "SubjectIs"));      buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(20, "AssertionStatus"));buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(20, "AttributedTo"));   buff2.append(COLUMN_DELIMITER );
    
      
      //     rowNum, fileName, category, mention, supportingEvidence, sectionName, subjectStatus, assertionStatus, attributedTo
     
      
      this.InstanceColumnNames = buff2.toString();
      
    } // end Method initialize_column_names() ----------

    // ---------------------------------------
    // Global Variables
    // ---------------------------------------
    protected int annotationCtr = 0;
    ProfilePerformanceMeter performanceMeter = null;
   // String Columns[] = null;
    String InstanceColumnNames = null;
    String ColumnNames = null;
    String outputDir = null;
    PrintWriter instanceOut = null;
    String COLUMN_DELIMITER = "\t,";
    int[] totalTable = null;
    int ctr = 0;
    int docCtr = 0;
    boolean processingListings = false;
    boolean highlightMentions = false;
    boolean showFileName = false;
    int rowNum = 1;
    
    
    
     
  

} // end Class toCommonModel
