/* 
 * Copyright (C) 2017 Behrang QasemiZadeh <zadeh at phil.hhu.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package semeval.utils;

import semeval.lmu.ifi.dbs.elki.evaluation.clustering.ClusterContingencyTable;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class EvaluationResult {

    private static final double SCALE = 100;
    protected int goldClusterNum;
    protected int goldInstance;

    ClusterContingencyTable cct;
    
    protected int sysClusterNum;
    protected int sysInstance;

    protected double purity; //
    protected double inversePurity;//
    protected double puIpuf1;//

    protected double bcp; // bcubed precision
    protected double bcr; // bcubed recall
    protected double bCubedF1; // bCubedF1

    private double vmeasure; //

    protected double precision; //
    protected double recall;//
    protected double pairCountF1pf;

    protected double adjstRandIdx; //
    protected double editDst;//

    public void refreshStats() {
        purity = cct.getSetMatching().purity();
        inversePurity = cct.getSetMatching().inversePurity();
        puIpuf1 = cct.getSetMatching().f1Measure();

        bcp = cct.getBCubed().precision();
        bcr = cct.getBCubed().recall();
        bCubedF1 = cct.getBCubed().f1Measure();

        vmeasure = cct.getEntropy().getVMeasure();

        precision = cct.getPaircount().precision();
        recall = cct.getPaircount().recall();
        pairCountF1pf = cct.getPaircount().f1Measure();

        adjstRandIdx = cct.getPaircount().adjustedRandIndex();
        editDst = cct.getEdit().f1Measure();

    }
    // these are set from the parsing process
    private double likelihood;
    private double likelihoodForBestParses;
    private double likelihoodForChosenNBestParses;
    private double changeInLikelihood;
    private int countValidParses;
    private int learningIteration;
    private int numberOfBinRules;
    private int numberOFUnaryRules;
    private double processTime;

    public void setLikelihoodForChosenNBestParses(double likelihoodForChosenNBestParses) {
        this.likelihoodForChosenNBestParses = likelihoodForChosenNBestParses;
    }

    public double getProcessTime() {
        return processTime;
    }

    public double getLikelihoodForChosenNBestParses() {
        return likelihoodForChosenNBestParses;
    }

    public void setNumberOFUnaryRules(int numberOFUnaryRules) {
        this.numberOFUnaryRules = numberOFUnaryRules;
    }

    public void setNumberOfBinRules(int numberOfBinRules) {
        this.numberOfBinRules = numberOfBinRules;
    }

    public int getNumberOFUnaryRules() {
        return numberOFUnaryRules;
    }

    public int getNumberOfBinRules() {
        return numberOfBinRules;
    }

    public void setLearningIteration(int learningIteration) {
        this.learningIteration = learningIteration;
    }

    public int getLearningIteration() {
        return learningIteration;
    }

    public void setLikelihood(double likelihood) {
        this.likelihood = likelihood;
    }

    public void setCountValidParses(int countValidParses) {
        this.countValidParses = countValidParses;
    }

    public int getCountValidParses() {
        return countValidParses;
    }

    public void setLikelihoodForBestParses(double likelihoodForBestParses) {
        this.likelihoodForBestParses = likelihoodForBestParses;
    }

    public double getLikelihoodForBestParses() {
        return likelihoodForBestParses;
    }

    public double getLikelihood() {
        return likelihood;
    }

    public void setChangeInLikelihood(double changeInLikelihood) {
        this.changeInLikelihood = changeInLikelihood;
    }

    public double getChangeInLikelihood() {
        return changeInLikelihood;
    }

    public EvaluationResult() {
    }

    public EvaluationResult(int goldClusterNum,
            int goldInstance, int sysClusterNum,
            int sysInstance, double pairCountF1,
            double bCubedF1, double purity, double inversePurity, double puIpuf1, ClusterContingencyTable cct) {
        this.goldClusterNum = goldClusterNum;
        this.goldInstance = goldInstance;
        this.sysClusterNum = sysClusterNum;
        this.sysInstance = sysInstance;
        this.pairCountF1pf = pairCountF1;
        this.bCubedF1 = bCubedF1;
        this.purity = purity;
        this.inversePurity = inversePurity;
        this.puIpuf1 = puIpuf1;
        this.cct = cct;

    }

    public int getGoldClusterNum() {
        return goldClusterNum;
    }

    public int getGoldInstance() {
        return goldInstance;
    }

    public int getSysClusterNum() {
        return sysClusterNum;
    }

    public int getSysInstance() {
        return sysInstance;
    }

////////////////////////
    public double getPurity() {
        return purity * SCALE;
    }

    public double getInversePurity() {
        return inversePurity * SCALE;
    }

    public double getPuIpuf1() {
        return puIpuf1 * SCALE;
    }
///////////////    

    public double getBCubedPrecision() {
        return cct.getBCubed().precision() * SCALE;
    }

    public double getBCubedRecall() {
        return cct.getBCubed().recall() * SCALE;
    }

    public double getbCubedF1() {
        return bCubedF1 * SCALE;
    }

    /////////////////////////
    public double getPairCountPrecision() {
        return cct.getPaircount().precision() * SCALE;
    }

    public double getPairCountRecall() {
        return cct.getPaircount().recall() * SCALE;
    }

    public double getPairCountF1() {
        return pairCountF1pf * SCALE;
    }

    public double getPairCountRandIndex() {
        return cct.getPaircount().randIndex() * SCALE;
    }

    public double getPairCountAdjustedRandIndex() {
        return cct.getPaircount().adjustedRandIndex() * SCALE;
    }
    ////////////////

    public double getEntropyVMeasure() {
        return cct.getEntropy().getVMeasure() * SCALE;
    }

    public double getEntropyNormalizedVariOfInfo() {
        return cct.getEntropy().normalizedVariationOfInformation() * SCALE;
    }

    public double getEntropyVariOfInfo() {
        return cct.getEntropy().variationOfInformation() * SCALE;
    }

    ////
    public String getHeader() {
        return "\t#goldInstance\t#sysInstance\t#goldClusterNum\t#sysClusterNum \t:\tpurity\tinversePurity\tpuIpuf1\tbCubedF1\tpairCountF1";
    }

    public String getHeaderClusterInstNumber(String separator) {
        return "goldInstance" + separator + "sysInstance" + separator + "goldClusterNum" + separator + "sysClusterNum";
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("##.##");

        return "\t" + goldInstance + "\t& "
                + sysInstance + "\t& "
                + goldClusterNum + "\t& "
                + sysClusterNum + "\t&\t"
                + df.format(purity * SCALE) + "\t& "
                + df.format(inversePurity * SCALE) + "\t& "
                + df.format(puIpuf1 * SCALE) + "\t&\t "
                + df.format(cct.getBCubed().precision() * SCALE) + "\t&"
                + df.format(cct.getBCubed().recall() * SCALE) + "\t& "
                + df.format(bCubedF1 * SCALE) + "\t & \t"
                + df.format(cct.getEntropy().getVMeasure() * SCALE) + "\t & \t"
                + df.format(cct.getPaircount().precision() * SCALE) + "\t & "
                + df.format(cct.getPaircount().recall() * SCALE) + "\t & "
                + df.format(pairCountF1pf * SCALE) + "\t & "
                + df.format(cct.getPaircount().adjustedRandIndex() * SCALE) + "\t & "
                + df.format(cct.getEdit().f1Measure() * SCALE);
    }

    public String toString(String delimiter) {
        DecimalFormat df = new DecimalFormat("##.##");

        return delimiter + goldInstance + delimiter
                + sysInstance + delimiter
                + goldClusterNum + delimiter
                + sysClusterNum + delimiter
                + df.format(purity * SCALE) + delimiter
                + df.format(inversePurity * SCALE) + delimiter
                + df.format(puIpuf1 * SCALE) + delimiter
                + df.format(cct.getBCubed().precision() * SCALE) + delimiter
                + df.format(cct.getBCubed().recall() * SCALE) + delimiter
                + df.format(bCubedF1 * SCALE) + delimiter
                + df.format(cct.getEntropy().getVMeasure() * SCALE) + delimiter
                + df.format(cct.getPaircount().precision() * SCALE) + delimiter
                + df.format(cct.getPaircount().recall() * SCALE) + delimiter
                + df.format(pairCountF1pf * SCALE);

    }
    
    public String getHeaderSemEval2019(String delimiter) {
        return "#goldInstance"
                + delimiter
                + "#sysInstance"
                + delimiter
                + "#goldClusterNum"
                + delimiter
                + "#sysClusterNum"
                + delimiter
                + "purity"
                + delimiter
                + "inversePurity"
                + delimiter
                + "puIpuf1"
                + delimiter
                + "BCubed-Precision"
                + delimiter
                + "BCubed-Recall"
                + delimiter
                + "BCubed-f1";
    }

    public String toStringSemEval2019(String delimiter) {
        DecimalFormat df = new DecimalFormat("##.##");

        return goldInstance + delimiter
                + sysInstance + delimiter
                + goldClusterNum + delimiter
                + sysClusterNum + delimiter
                + df.format(purity * SCALE) + delimiter
                + df.format(inversePurity * SCALE) + delimiter
                + df.format(puIpuf1 * SCALE) + delimiter
                + df.format(cct.getBCubed().precision() * SCALE) + delimiter
                + df.format(cct.getBCubed().recall() * SCALE) + delimiter
                + df.format(bCubedF1 * SCALE) + delimiter
//                + df.format(cct.getEntropy().getVMeasure() * SCALE) + delimiter
//                + df.format(cct.getPaircount().precision() * SCALE) + delimiter
//                + df.format(cct.getPaircount().recall() * SCALE) + delimiter
//                + df.format(pairCountF1pf * SCALE)
                ;

    }
    public String toStringTACL(String delimiter, boolean refreshstat) {
        DecimalFormat df = new DecimalFormat("##.##");
        if (refreshstat) {
            refreshStats();
        }
        return delimiter
                + sysClusterNum + delimiter
                + this.sysInstance + delimiter
                + df.format(purity * SCALE) + delimiter
                + df.format(inversePurity * SCALE) + delimiter
                + df.format(puIpuf1 * SCALE) + delimiter
                + df.format(bcp * SCALE) + delimiter
                + df.format(bcr * SCALE) + delimiter
                + df.format(bCubedF1 * SCALE) + delimiter
                + df.format(vmeasure * SCALE) + delimiter
                + df.format(precision * SCALE) + delimiter
                + df.format(recall * SCALE) + delimiter
                + df.format(pairCountF1pf * SCALE) + delimiter
                + df.format(adjstRandIdx * SCALE) + delimiter
                + df.format(editDst * SCALE);

    }

     public String toStringStarSem(String delimiter) {
        DecimalFormat df = new DecimalFormat("##.##");
//        if (refreshstat) {
//            refreshStats();
//        }
        return delimiter
                + sysClusterNum + delimiter
              //  + this.sysInstance + delimiter
                + df.format(purity * SCALE) + delimiter
                + df.format(inversePurity * SCALE) + delimiter
                + df.format(puIpuf1 * SCALE) + delimiter
               // + df.format(bcp * SCALE) + delimiter
               // + df.format(bcr * SCALE) + delimiter
                + df.format(bCubedF1 * SCALE) 
               // + df.format(vmeasure * SCALE) + delimiter
               // + df.format(precision * SCALE) + delimiter
               // + df.format(recall * SCALE) + delimiter
              //  + df.format(pairCountF1pf * SCALE) + delimiter
              //  + df.format(adjstRandIdx * SCALE) + delimiter
              //  + df.format(editDst * SCALE)
                ;

    }

     public String toStringTACL(String delimiter) {
        DecimalFormat df = new DecimalFormat("##.##");

        return delimiter 
                + sysClusterNum + delimiter
                + df.format(purity * SCALE) + delimiter
                + df.format(inversePurity * SCALE) + delimiter
                + df.format(puIpuf1 * SCALE) + delimiter
                + df.format(cct.getBCubed().precision() * SCALE) + delimiter
                + df.format(cct.getBCubed().recall() * SCALE) + delimiter
                + df.format(bCubedF1 * SCALE) + delimiter
                + df.format(cct.getEntropy().getVMeasure() * SCALE) + delimiter
                + df.format(cct.getPaircount().precision() * SCALE) + delimiter
                + df.format(cct.getPaircount().recall() * SCALE) + delimiter
                + df.format(cct.getPaircount().f1Measure()* SCALE)+ delimiter
                
                + df.format(cct.getPaircount().adjustedRandIndex()* SCALE)+ delimiter
                + df.format(cct.getEdit().f1Measure()* SCALE)
                
                ;

    }
     
    public String getHeader(String delimiter) {
        return delimiter + "gi" + delimiter
                + "si" + delimiter
                + "gc" + delimiter
                + "sc" + delimiter
                + "pu" + delimiter
                + "ipu" + delimiter
                + "puif" + delimiter
                + "bcp" + delimiter
                + "bcr" + delimiter
                + "bcf" + delimiter
                + "vm" + delimiter
                + "pr" + delimiter
                + "r" + delimiter
                + "prrf";
    }

    /**
     * For tables used in the TACL submission
     *
     * @param delimiter
     * @return
     */
    public String getHeaderTACL(String delimiter) {
        return delimiter
                //+"gi" + delimiter
                // + "si" + delimiter
                //+ "gc" + delimiter
                + "sc" + delimiter
                + "pu" + delimiter
                + "ipu" + delimiter
                + "fpu" + delimiter
                + "bcp" + delimiter
                + "bcr" + delimiter
                + "bcf" + delimiter
                + "vm" + delimiter
                + "pp" + delimiter
                + "pr" + delimiter
                + "pf" + delimiter
                + "ari" + delimiter
                + "dst" + delimiter;
    }

    /**
     * returns results in a human readable format
     * @param split
     * @return 
     */
    public String toStringShort(String split) {
        refreshStats();
        DecimalFormat df = new DecimalFormat("##.##");
        double scale = 100;
        return "gi:" + goldInstance + split
                + "si:" + sysInstance + split
                + "gc:" + goldClusterNum + split
                + "sc:" + sysClusterNum + split
                + "pu:" + df.format(purity * scale) + split
                + "ipu:" + df.format(inversePurity * scale)
                + split
                + "fpu:" + df.format(puIpuf1 * scale) + split
                + "bcp:" + df.format(bcp * scale) + split
                + "bcr:" + df.format(bcr * scale) + split
                + "bcf:" + df.format(bCubedF1 * scale) + split
                + "vm:" + df.format(vmeasure * scale) + split
                + "pp:" + df.format(precision * scale) + split
                + "pr:" + df.format(recall * scale) + split
                + "pf:" + df.format(pairCountF1pf * scale) + split
                + "aRi:" + df.format(adjstRandIdx * scale) + split
                + "dst:" + df.format(editDst * scale);
    }

    public String getInstClusterCounts(String separator) {
        return goldInstance + separator + sysInstance + separator + goldClusterNum + separator + sysClusterNum;
    }

    /**
     * returns the value for pu, ipu, fpu, bcubedf1 and unsuperivsed f1
     * @param separator
     * @return 
     */
    public String getMajorMetrics(String separator) {
        DecimalFormat df = new DecimalFormat("##.##");
        double scale = 100;
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(purity * scale) + separator + df.format(inversePurity * scale) + separator + df.format(puIpuf1 * scale) + separator
                + df.format(bCubedF1 * scale) + separator + df.format(pairCountF1pf * scale);
    }

    public String getMajorMetricsHeader(String prefix, String separator) {
        return prefix + "purity" + separator + prefix + "inversePurity" + separator + prefix + "puIpuf1" + separator
                + prefix + "bCubedF1" + separator + prefix + "pairCountF1";
    }

    public void sumEvaluatinResult(EvaluationResult evr1) {
        this.goldClusterNum += evr1.goldClusterNum;
        this.goldInstance += evr1.goldInstance;
        this.sysClusterNum += evr1.sysClusterNum;
        this.sysInstance += evr1.sysInstance;

        this.purity += evr1.purity;
        this.inversePurity += evr1.inversePurity;
        this.puIpuf1 += evr1.puIpuf1;
        
        
        this.bcp += evr1.bcp;
        this.bcr += evr1.bcr;
        this.bCubedF1 += evr1.bCubedF1;

        vmeasure+= evr1.vmeasure;
        
        this.precision += evr1.precision;
        this.recall += evr1.recall;
        this.pairCountF1pf += evr1.pairCountF1pf;

        this.adjstRandIdx += evr1.adjstRandIdx;
        this.editDst += evr1.editDst;
    }

    /**
     * Assuming that the sum keeps the count for div run, we divide them to have
     * an ar. mean
     *
     * @param div
     */
    public void normalizeSumEvalByDivTo(int div) {
        this.goldClusterNum /= div;
        this.goldInstance /= div;
        this.sysClusterNum /= div;
        this.sysInstance /= div;

        this.purity /= div;
        this.inversePurity /= div;
        this.puIpuf1 /= div;

        this.bcp /= div;
        this.bcr /= div;
        this.bCubedF1 /= div;

        vmeasure/= div;
        
        this.precision /= div;
        this.recall /= div;
        this.pairCountF1pf /= div;

        this.adjstRandIdx /= div;
        this.editDst /= div;
    }

    public void maxEvaluatinResult(EvaluationResult evr1) {
        goldClusterNum = Math.max(this.goldClusterNum, evr1.goldClusterNum);
        goldInstance = Math.max(this.goldInstance, evr1.goldInstance);
        sysClusterNum = Math.max(this.sysClusterNum, evr1.sysClusterNum);
        sysInstance = Math.max(this.sysInstance, evr1.sysInstance);

        purity = Math.max(this.purity, evr1.purity);
        inversePurity = Math.max(this.inversePurity, evr1.inversePurity);
        puIpuf1 = Math.max(this.puIpuf1, evr1.puIpuf1);

        bcp = Math.max(this.bcp, evr1.bcp);
        bcr = Math.max(this.bcr, evr1.bcr);
        bCubedF1 = Math.max(this.bCubedF1, evr1.bCubedF1);
        
        vmeasure = Math.max(this.vmeasure, evr1.vmeasure);
        
        precision = Math.max(this.precision, evr1.precision);
        recall = Math.max(this.recall, evr1.recall);
        pairCountF1pf = Math.max(this.pairCountF1pf, evr1.pairCountF1pf);

        adjstRandIdx = Math.max(this.adjstRandIdx, evr1.adjstRandIdx);
        editDst = Math.max(this.editDst, evr1.editDst);
    }

    
    public void minEvaluatinResult(EvaluationResult evr1) {
        goldClusterNum = Math.min(this.goldClusterNum, evr1.goldClusterNum);
        goldInstance = Math.min(this.goldInstance, evr1.goldInstance);
        sysClusterNum = Math.min(this.sysClusterNum, evr1.sysClusterNum);
        sysInstance = Math.min(this.sysInstance, evr1.sysInstance);

        purity = Math.min(this.purity, evr1.purity);
        inversePurity = Math.min(this.inversePurity, evr1.inversePurity);
        puIpuf1 = Math.min(this.puIpuf1, evr1.puIpuf1);

        bcp = Math.min(this.bcp, evr1.bcp);
        bcr = Math.min(this.bcr, evr1.bcr);
        bCubedF1 = Math.min(this.bCubedF1, evr1.bCubedF1);

        vmeasure = Math.min(this.vmeasure, evr1.vmeasure);
        
        precision = Math.min(this.precision, evr1.precision);
        recall = Math.min(this.recall, evr1.recall);
        pairCountF1pf = Math.min(this.pairCountF1pf, evr1.pairCountF1pf);

        adjstRandIdx = Math.min(this.adjstRandIdx, evr1.adjstRandIdx);
        editDst = Math.min(this.editDst, evr1.editDst);
    }

    public void devideByXToNormalize(double denominator) {
        this.pairCountF1pf /= denominator;
        this.bCubedF1 /= denominator;
        this.purity /= denominator;
        this.inversePurity /= denominator;
        this.puIpuf1 /= denominator;
    }

    public ClusterContingencyTable getCct() {
        return cct;
    }

//    public void setProcessTime(double processTime) {
//        this.processTime = processTime;
//    }
    public void setTimeStamp(long processTime) {
        this.processTime = processTime;
    }

}
