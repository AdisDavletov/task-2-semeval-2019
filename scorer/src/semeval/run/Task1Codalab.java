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
public class Task1Codalab {
    
    
    public static void main(String[] args) throws Exception {

        String inputGold = args[0] + "ref";
        String systemFile = args[0] + "res";
        String output = args[1] + "scores.txt";

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

        double bCubedF1 = process.getbCubedF1();
        PrintWriter pw = new PrintWriter(new FileWriter(new File(output)));
        pw.println("bcubed-f1: " + bCubedF1);
        pw.close();

    }


  
}
