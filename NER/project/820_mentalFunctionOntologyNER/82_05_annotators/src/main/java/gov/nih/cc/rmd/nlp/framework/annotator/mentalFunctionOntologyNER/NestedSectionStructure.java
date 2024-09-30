// =================================================
/**
 * NestedSectionStructure
 *
 * @author     Guy Divita
 * @created    Sep 6, 2022
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

import gov.nih.cc.rmd.framework.NestedSection;
import gov.nih.cc.rmd.framework.SectionZone;

/**
 * @author divitag2
 *
 */
public class NestedSectionStructure {

  // =================================================
  /**
   * Constructor
   *
   * 
  **/
  // =================================================
  public NestedSectionStructure() {
     indexNumber =  indexNumberCounter++;
     
  }

    
    /**
   * @return the lines
   */
  public final List<Annotation> getLines() {
    return lines;
  }
  /**
   * @param lines the lines to set
   */
  public final void setLines(List<Annotation> lines) {
    this.lines = lines;
  }
  /**
   * @return the sectionName
   */
  public final String getSectionName() {
    return sectionName;
  }
  /**
   * @param sectionName the sectionName to set
   */
  public final void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }
  /**
   * @return the sectionNumber
   */
  public final int getSectionNumber() {
    return sectionNumber;
  }
  /**
   * @param sectionNumber the sectionNumber to set
   */
  public final void setSectionNumber(String sectionNumber) {
    this.sectionNumber = Integer.parseInt( sectionNumber);
  }
  /**
   * @param sectionNumber the sectionNumber to set
   */
  public final void setSectionNumber(int sectionNumber) {
    this.sectionNumber =  sectionNumber;
  }
  /**
   * @return the beginOffset
   */
  public final int getBeginOffset() {
    return beginOffset;
  }
  /**
   * @param beginOffset the beginOffset to set
   */
  public final void setBeginOffset(int beginOffset) {
    this.beginOffset = beginOffset;
  }
  /**
   * @return the endOffset
   */
  public final int getEndOffset() {
    return endOffset;
  }
  /**
   * @param endOffset the endOffset to set
   */
  public final void setEndOffset(int endOffset) {
    this.endOffset = endOffset;
  }

  /**
   * @return the parent
   */
  public final int getParentIndex() {
    return parentIndex;
  }


  /**
   * @param parent the parent to set
   */
  public final void setParentIndex(int parent) {
    this.parentIndex = parent;
  }
  
    /**
   * @return the indexNumber
   */
  public final int getIndexNumber() {
    return indexNumber;
  }


  /**
   * @param indexNumber the indexNumber to set
   */
  public  final void setIndexNumber(int indexNumber) {
    this.indexNumber = indexNumber;
  }


    // =================================================
    /**
     * addLine
     * 
     * @param aLine
    */
    // =================================================
     public final void addLine(Annotation aLine) {
       
       if ( this.beginOffset == -1 )
         this.setBeginOffset(aLine.getBegin());
       
      this.lines.add( aLine);
      this.setEndOffset( aLine.getEnd());
      
    }
     
  /**
     * @return the sectionZone
     */
    public final NestedSection getSectionZone() {
      return sectionZone;
    }


    /**
     * @param sectionZone the sectionZone to set
     */
    public final void setSectionZone(NestedSection sectionZone) {
      this.sectionZone = sectionZone;
    }

    // ----------------------------------------------
     // Global Variables
     // ----------------------------------------------
     List<Annotation > lines = new ArrayList<Annotation> ();
     String sectionName = null;
     int sectionNumber = 0;
     int beginOffset = -1;
     int endOffset = -1;
     int parentIndex = -1;
     NestedSection sectionZone = null;
     int indexNumber = -1;
     static int indexNumberCounter = 0;
      
    // private static List<NestedSectionStructure> structureIndex = null;
    
  } // end Class NestedSectionStructure() -----------


