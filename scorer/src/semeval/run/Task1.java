/*
 * Copyright (C) 2018 Zadeh
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
package semeval.run;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import semeval.evaluationscripts.MetricsForFrameGrouping;
import semeval.utils.EvaluationResult;
import semeval.utils.UtilReadProcessEvaluationFiles;

/**
 * compute the scores for the task 1: grouping verbs to frame categories
 * @author Zadeh
 */
public class Task1 {
    public static void main(String[] args) throws Exception {
      
        if(args.length<2){
            System.err.println("Please provide the following parameters:"
                    + "\nargs0: Path to the gold file for task1;"
                    + "\nargs1: Path to your system result."
                    + "\nOptionally, you can add -verbose to get baseline results");
            return;
        }
        Set<String> argsSet = new HashSet<>(Arrays.asList(args));
        //argsSet.add("-verbose");
        String inputGold = args[0];//"../lr/semeval/trial-public/gold/task-1.txt";
        String systemFile = args[1];//"../lr/semeval/trial-public/test/task-1.txt";
        MetricsForFrameGrouping mfg = null;
        EvaluationResult process = null;
        try {
            UtilReadProcessEvaluationFiles.checkIDs(inputGold, systemFile);
            mfg = new MetricsForFrameGrouping(0, 0, inputGold, systemFile);
            process = mfg.process(systemFile);
        } catch (IOException ex) {
            System.err.println("Exception: " + ex);
            return;
        }

        if (argsSet.contains("-codalab")) {
            double bCubedF1 = process.getbCubedF1();
            PrintWriter pw= new PrintWriter(new FileWriter(new File("output/scores.txt")));
            pw.println("bcubed-f1: "+bCubedF1);
            pw.close();
        } else if (argsSet.contains("-verbose")) {
            String header = process.getHeaderSemEval2019("\t");
            String systemResult = process.toStringSemEval2019("\t");
            System.out.println("\t"+header);
            System.out.println("your-submission\t"+systemResult);
            System.out.println("=== baselines==");
            EvaluationResult bl1CPerHead = mfg.bl1CPerHead();
            System.out.println("OneClustPerLemma\t"+bl1CPerHead.toStringSemEval2019("\t"));
            EvaluationResult bl1CPerInstance = mfg.bl1CPerInstance();
            System.out.println("OneClustPerInst\t"+bl1CPerInstance.toStringSemEval2019("\t"));
            EvaluationResult blAllin1C = mfg.blAllin1C();
            System.out.println("AllInOneCluster\t"+blAllin1C.toStringSemEval2019("\t"));
            
            EvaluationResult createBaselineRandom = mfg.createBaselineRandom(2);
            System.out.println("Random (2clusters)\t"+createBaselineRandom.toStringSemEval2019("\t"));
            int sysCluster = process.getSysClusterNum();
            EvaluationResult createBaselineRandomN = mfg.createBaselineRandom(sysCluster);
            System.out.println("Random ("+sysCluster+"cluster)\t"+createBaselineRandomN.toStringSemEval2019("\t"));
            
        } else {
            String header = process.getHeaderSemEval2019("\t");
            String systemResult = process.toStringSemEval2019("\t");
            System.out.println("\t" + header);
            System.out.println("your-submission\t" + systemResult);
        }
        
    }
    
    
  
}
