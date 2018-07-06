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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import semeval.evaluationscripts.MetricForArgumentsTask22;
import semeval.utils.EvaluationResult;
import semeval.utils.UtilReadProcessEvaluationFiles;

/**
 * compute the scores for the task 1: grouping verbs to frame categories
 *
 * @author Zadeh
 */
public class Task22 {

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Please provide the following parameters:"
                    + "\nargs1: Path to the gold file for task1;"
                    + "\nargs2: Path to your system result."
                    + "\nargs3: Path to the file for gramatical-baseline (this is optional)"
                    + "\nTo see the baselines, add -verbose to the end of your arguments");
            return;
        }
        Set<String> argsSet = new HashSet<>(Arrays.asList(args));
        //argsSet.add("-verbose");
        String inputGold = args[0];//"../lr/semeval/trial-public/gold/task-2.2.txt";
        String systemFile = args[1];//"../lr/semeval/trial-public/test/task-2.2.txt";
        String syntaxBaseFile = null;
        if (args.length > 2 && !args[2].startsWith("-")) {
            syntaxBaseFile = args[2];//"../lr/semeval/trial-public/baseline-files/task-2.2.gr.txt";
        }
        MetricForArgumentsTask22 mfg = null;
        EvaluationResult process = null;
        try {
            UtilReadProcessEvaluationFiles.checkIDs(inputGold, systemFile);
            UtilReadProcessEvaluationFiles.checkIDsForArguments(inputGold, systemFile);

            mfg = new MetricForArgumentsTask22(0, inputGold);
            process = mfg.process(inputGold, systemFile);
        } catch (IOException ex) {
            System.err.println("Exception: " + ex);
            return;
        }

//        if (argsSet.contains("-codalab")) {
//            double bCubedF1 = process.getbCubedF1();
//            PrintWriter pw= new PrintWriter(new FileWriter(new File("output/scores.txt")));
//            pw.println("bcubed-f1: "+bCubedF1);
//            pw.close();
//        } else 
        if (argsSet.contains("-verbose")) {
            String header = process.getHeaderSemEval2019("\t");
            String systemResult = process.toStringSemEval2019("\t");
            System.out.println("\t" + header);
            System.out.println("your-submission\t" + systemResult);
            System.out.println("=== baselines==");
            if (syntaxBaseFile != null) {
                EvaluationResult bl1CPerGramRel = mfg.createBaseline1ClusterPerGramaticalRelationship(inputGold, syntaxBaseFile);
                System.out.println("OneClustPerGrType\t" + bl1CPerGramRel.toStringSemEval2019("\t"));
            } else {
                System.out.println("OneClustPerGrType\t No Baseline File for this has been specified -- pass as arg3 to the scorer");
            }
            EvaluationResult bl1CPerInstance = mfg.createBaseline1ClusterPerInstance(inputGold, systemFile);
            System.out.println("OneClustPerInst\t" + bl1CPerInstance.toStringSemEval2019("\t"));
            EvaluationResult blAllin1C = mfg.createBaselineAllIn1Cluster(inputGold, systemFile);
            System.out.println("AllInOneCluster\t" + blAllin1C.toStringSemEval2019("\t"));
//            
            EvaluationResult createBaselineRandom = mfg.createBaselineRandom(inputGold, systemFile, 2);
            System.out.println("Random (2clusters)\t" + createBaselineRandom.toStringSemEval2019("\t"));
            int sysCluster = process.getSysClusterNum();
            EvaluationResult createBaselineRandomN = mfg.createBaselineRandom(inputGold, systemFile, sysCluster);
            System.out.println("Random (" + sysCluster + "cluster)\t" + createBaselineRandomN.toStringSemEval2019("\t"));

        } else {
            String header = process.getHeaderSemEval2019("\t");
            String systemResult = process.toStringSemEval2019("\t");
            System.out.println("\t" + header);
            System.out.println("your-submission\t" + systemResult);
        }

    }

}
