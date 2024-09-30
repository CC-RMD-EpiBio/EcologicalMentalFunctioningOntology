// =================================================
/**
 * MentalFunctionOntologyNERAnnotator creates MentalFunction ontology derived annotatons in text
 * 
 * @author  G.o.d.
 * @created 2022.03.28
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.NestedSection;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MentalFunctioningOntology;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Section;



public class MentalFunctionOntologyNERCSVWriterObs extends JCasAnnotator_ImplBase {
 
  
  
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
    int[] summaryTable = getSummaryTable(pJCas );
    String  summaryRow = getSummaryRow ( fileName , summaryTable );
    String[] summarySectionRows = null;
    if ( this.detailedSummary )
      summarySectionRows = getSummarySectionTable( pJCas  );
    
    printInstances( instanceRows);
   
    printSummary( summaryRow , summarySectionRows);
    
    updateTotalTable ( summaryTable );
     
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

// =================================================
  /**
   * printSummary 
   * 
   * @param summaryRow
   * @param summarySectionRows 
  */
  // =================================================
   private synchronized void printSummary(String summaryRow, String[] summarySectionRows) {
    this.summaryOut.print(  summaryRow + "\n");
    if ( this.detailedSummary )
      for ( int i = 0; i < summarySectionRows.length; i++)
        this.summaryOut.print( summarySectionRows[i] + "\n");
    
    this.summaryOut.flush();
    
  } // end Method printSummary() --------------------


// =================================================
  /**
   * printInstances 
   * 
   * @param instanceRows
  */
  // =================================================
  private synchronized void printInstances(List<String> instanceRows) {
    
    for ( int i = 0; i < instanceRows.size(); i++ )
      this.instanceOut.print( U.zeroPad(this.ctr++,10) + COLUMN_DELIMITER +  instanceRows.get(i)+ "\n" );
  
    this.instanceOut.flush();
  } // end Method printInstances() ---------------


// =================================================
  /**
   * updateTotalTable 
   * 
   * @param fileTable
  */
  // =================================================
   private void updateTotalTable(int[] fileTable) {
    
     int columnTotals = 0;
     for ( int i = 0; i < fileTable.length; i++) {
       this.totalTable[i]+= fileTable[i];
       columnTotals+= fileTable[i];
     }
     this.totalTable[ fileTable.length] = columnTotals;
     
    
  } // end Method updateTotalTable() ---------------


// =================================================
  /**
   * getSummaryRow 
   * @param pJCas
   * @return in[]
  */
  // =================================================
  private int[] getSummaryTable(JCas pJCas ) {
    int[] returnVal = new int[ Columns.length ];
    
   
    for ( int i = 0; i < Columns.length; i++ ) {
      String category = Columns[i];
   
      int labelId = -1;
      try {
     
       labelId = UIMAUtil.getLabelTypeId(  category);
   
      } catch (Exception e) {
        e.printStackTrace();
      }
      if( labelId > 0  ) {
        
        List<Annotation> instances = UIMAUtil.getAnnotations( pJCas, labelId, false );
     
        if ( instances != null && !instances.isEmpty()) {
          List<Annotation> uniquedInstances  = UIMAUtil.uniqueAnnotationList(instances);
          returnVal[i] = uniquedInstances.size();
        }
      } else {
        System.err.println("category = " + category + " has a null label ");
      
      }
    }
    return returnVal;
    
  } // end getSummaryTable() --------------------------

  
//=================================================
 /**
  * getSummarySectionTable
  * @param pJCas
  * @return in[]
 */
 // =================================================
 private String[] getSummarySectionTable(JCas pJCas ) {
   
   List<String> rows = new ArrayList<String>();
   String nextSectionName = null;
   String sectionName = null;
   System.err.println("here");
   
   List<Annotation> combinedSections = null;
   
   List<Annotation> sections = UIMAUtil.getAnnotations( pJCas, NestedSection.typeIndexID, false);
   if ( sections != null && !sections.isEmpty())
     for ( Annotation section : sections ) {
       
       String potentialSectionName = ((NestedSection)section).getSectionName();
       
       if ( isNewSection( potentialSectionName )) {
         sectionName = nextSectionName;
          nextSectionName = ((NestedSection)section).getSectionName();
          if ( combinedSections != null && !combinedSections.isEmpty()) {
            int[] row = getSummaryTableAux(pJCas, combinedSections);
            if ( sectionName == null )
              sectionName = " ";
            else 
              sectionName = U.spacePadRight(20, sectionName);
            String buff = getSummaryRow( sectionName, row ) ;
            rows.add( "|->" + buff );
          }
          combinedSections = new ArrayList<Annotation>();
          combinedSections.add(  section );
       } else {
        if ( combinedSections != null )
          combinedSections.add( section);
        else {
          System.err.print("what's this seciton ?" );
        }
       }
     }
     if ( combinedSections != null && !combinedSections.isEmpty()) {
       int[] row = getSummaryTableAux(pJCas, combinedSections);
       if ( sectionName == null )
         sectionName = " ";
       else 
         sectionName = U.spacePadRight(20, sectionName);
       String buff = getSummaryRow( sectionName, row ) ;
       rows.add( "|->" + buff );
     }    
     
     String[] returnVal = rows.toArray( new String[rows.size()]);
   
     return returnVal;
   
 } // end getSummaryTable() --------------------------

  
   // =================================================
  /**
   * isNewSection returns true if the section is of the form d.d
   * 
   * @param potentialSectionName
   * @return boolean
  */
  // =================================================
   private boolean isNewSection(String potentialSectionName) {
 
     boolean returnVal = false;
    
     if ( potentialSectionName != null && !potentialSectionName.isEmpty()) {
       String[] cols = U. split (potentialSectionName, ".");
       if ( cols != null && cols.length >= 2 ) {
         String[] cols2 = U.split(cols[1], " ");
         if ( cols2 != null && cols2.length >= 1  && U.isNumber(cols[0]) && U.isNumber(cols2[0] ) )
         returnVal = true;
       }
     }
   
    return returnVal;
  } // end Method isNewSection() ------------------


//=================================================
/**
 * getSummaryRow 
 * @param pJCas
 * @param pCombinedSections
 * @return in[]
*/
// =================================================
private int[] getSummaryTableAux(JCas pJCas, List<Annotation> pCombinedSections ) {
  int[] returnVal = new int[ Columns.length ];
  
  
  int combinedSectionsBegin = 0;
  int combinedSectionsEnd = 0;
  int[] p = UIMAUtil.getMaxSpans(pCombinedSections);
  combinedSectionsBegin = p[0];
  combinedSectionsEnd = p[1];
  
 
  for ( int i = 0; i < Columns.length; i++ ) {
    String category = Columns[i];
 
    int labelId = -1;
    try {
   
     labelId = UIMAUtil.getLabelTypeId(  category);
 
    } catch (Exception e) {
      e.printStackTrace();
    }
    if( labelId > 0  ) {
      
      List<Annotation> instances = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  labelId, combinedSectionsBegin, combinedSectionsEnd, false );
   
      if ( instances != null && !instances.isEmpty()) {
        List<Annotation> uniquedInstances  = UIMAUtil.uniqueAnnotationList(instances);
        returnVal[i] = uniquedInstances.size();
      }
    } else {
      System.err.println("category = " + category + " has a null label ");
    
    }
  }
  return returnVal;
  
} // end getSummaryTable() --------------------------
    // =================================================
  /**
   * getSummaryTable
   * @param pFileName
   * @param pTable
   * @return String
   */
  // =================================================
  private String getSummaryRow( String pFileName, int[] pTable ) {
        
    String returnVal = null;
        StringBuffer buff = new StringBuffer();
          
        buff.append( U.zeroPad( this.docCtr++, 10));    buff.append(COLUMN_DELIMITER);
        buff.append( U.spacePadRight( 30, pFileName )); buff.append(COLUMN_DELIMITER);
        int total = 0;
        for ( int i = 0; i < pTable.length; i++ ) {
          buff.append( U.spacePadLeft(30,  String.valueOf(pTable[i])));
          buff.append(COLUMN_DELIMITER);
          total+= pTable[i];
        }
        buff.append( U.spacePadLeft(30,  String.valueOf(total)));
          
        returnVal = buff.toString();
       
        return returnVal;
        
      } // end getSummaryRow() --------------------------
  
  // =================================================
  /**
   * printLastRow
   *
   */
  // =================================================
  private synchronized void printLastRow( ) {
        
   
    String returnVal = null;
    StringBuffer buff = new StringBuffer();
    
    buff.append( U.zeroPad( this.docCtr++, 10));   buff.append(COLUMN_DELIMITER);
    buff.append( U.spacePadRight( 30, "Totals" )); buff.append(COLUMN_DELIMITER);
    int total = 0;
    for ( int i = 0; i < this.Columns.length; i++ ) {
      buff.append( U.spacePadLeft(30,  String.valueOf(this.totalTable[i])));
      buff.append(COLUMN_DELIMITER);
      total+= totalTable[i];
    }
    buff.append( U.spacePadLeft(30,  String.valueOf(total)));
    buff.append("\n");
      
    returnVal = buff.toString();
   
    this.summaryOut.print(  returnVal );
        
  } // end printLastRow() --------------------------


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
         
         String category = U.getClassName( instance.getClass().getName() );
         
         if ( category.equals("MentalFunctioningMention"))
           continue;
         
         String contextLeft = UIMAUtil.getLeftContext(pJCas, instance, 50);
         String contextRight = UIMAUtil.getRightContext(pJCas, instance, 50);
         contextLeft = contextLeft.replaceAll(",",  "-");
         contextRight = contextRight.replaceAll(",",  "-");
         String subjectStatus = (( MentalFunctioningOntology) instance).getSubjectStatus();
         String assertionStatus =  (( MentalFunctioningOntology) instance).getAssertionStatus();
         
         String mention = instance.getCoveredText();
         if ( mention != null ) {
           mention = mention.replaceAll(",", "-" );
           mention = U.spacePadRight(44,  mention );
           if ( this.highlightMentions  ) 
             mention = ">>>" + mention + "<<<";
         }
         String beginOffset =  U.spacePadLeft(8, String.valueOf( instance.getBegin() ));
         String endOffset   =  U.spacePadLeft(8, String.valueOf( instance.getEnd() ));
         
         buff.append(  U.spacePadLeft(54, pFileName));   buff.append( COLUMN_DELIMITER );
         buff.append(  U.spacePadLeft(40, sectionName));   buff.append( COLUMN_DELIMITER );
         buff.append(  U.spacePadLeft(30, category ));   buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(50,  contextLeft));  buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(50,  mention));      buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(50,  contextRight)); buff.append( COLUMN_DELIMITER );
         buff.append(                     beginOffset);   buff.append( COLUMN_DELIMITER );
         buff.append(                       endOffset);   buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(10,subjectStatus));  buff.append( COLUMN_DELIMITER );
         buff.append( U.spacePadLeft(20,assertionStatus));buff.append( COLUMN_DELIMITER );
         
         returnVal.add( buff.toString());
         
       } // end loop through instances
    
    return returnVal;
  } // end Method getRows() --------------------------

   
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
  
  printLastRow();
  this.summaryOut.close();
  
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
    this.detailedSummary = Boolean.parseBoolean( U.getOption(pArgs, "--detailedSummary=", "true"));
    this.outputDir = U.getOption(pArgs, "--outputDir=", "./output" ); 
    this.outputDir = this.outputDir + "/stats";
    this.processingListings = Boolean.parseBoolean( U.getOption( pArgs,  "--processListings=", "false"));
    this.highlightMentions = Boolean.parseBoolean(U.getOption(pArgs,  "--highlightMentions=", "false")); 
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
    
    this.totalTable = new int[ Columns.length  + 1];  // the extra is for the totals column;
    
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
    
      
      String instanceFileName = this.outputDir + "/InstanceFile_" + threadId + ".csv" ; 
      String summaryFileName  = this.outputDir + "/SummaryFile_" + threadId + ".csv" ; 
      this.instanceOut = new PrintWriter ( instanceFileName );
      this.summaryOut = new PrintWriter ( summaryFileName );
      
     
      instanceOut.print(  this.InstanceColumnNames + "\n");
      summaryOut.print(   this.ColumnNames + "\n");
     
     
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
    
    
   // this.Columns = U.split(MentalFunctionOntologyNERAnnotator.MentalFunctioningOntologyOutputTypes, ":" );
    
    this.Columns = U.split(MentalFunctionOntologyNERAnnotator.MentalFunctioningOntologyOutputTypes_JP, ":" );
    
    
    StringBuffer buff = new StringBuffer();
   
    buff.append(  U.spacePadRight(10,"DocCtr"));   buff.append(COLUMN_DELIMITER);
    buff.append(  U.spacePadRight(30,"FileName")); buff.append(COLUMN_DELIMITER);
    for ( int i = 0; i < this.Columns.length; i++ ) {
      String col = this.Columns[i].trim();
      this.Columns[i] = this.Columns[i].trim();
      buff.append(  U.spacePadRight(30, col.trim()));
      buff.append(COLUMN_DELIMITER);
    }  
    buff.append( "Totals");
    this.ColumnNames = buff.toString();
    
    StringBuffer buff2 = new StringBuffer();
    buff2.append( U.spacePadRight(10, "RowNum"));         buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(30, "FileName"));       buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(10, "Section"));        buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(30, "Category"));       buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(50, "LeftContext"));    buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(50, "Mention"));        buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(50, "RightContext"));   buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(8, "Begin"));           buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(8, "End"));             buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(10, "SubjectIs"));      buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(20, "AssertionStatus"));buff2.append(COLUMN_DELIMITER );
    buff2.append( U.spacePadRight(20, "AttributedTo"));   
    
    // fileName | category |mention |beginOffset |End offset |AssertionStatus|SubjectIs|AttributedTo
    this.InstanceColumnNames = buff2.toString();
    
  } // end Method initialize_column_names() ----------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  String Columns[] = null;
  String InstanceColumnNames = null;
  String ColumnNames = null;
  String outputDir = null;
  PrintWriter instanceOut = null;
  boolean detailedSummary = false;
  PrintWriter summaryOut = null;
  String COLUMN_DELIMITER = "\t,";
  int[] totalTable = null;
  int ctr = 0;
  int docCtr = 0;
  boolean processingListings = false;
  boolean highlightMentions = false;
  
  
  
   
  
  
  
} // end Class LineAnnotator() ---------------

