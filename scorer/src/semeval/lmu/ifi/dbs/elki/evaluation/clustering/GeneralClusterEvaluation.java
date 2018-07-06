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
package semeval.lmu.ifi.dbs.elki.evaluation.clustering;

import semeval.utils.ClusterMapUtils;
import semeval.utils.EvaluationResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class GeneralClusterEvaluation {

    private final Map<String, Set<String>> goldClusters;
    private final Map<String, Set<String>> systemClusters;
    private final EvaluationResult evr;

    public GeneralClusterEvaluation(
            Map<String, Set<String>> goldC, Map<String, Set<String>> systemC) {
        this.goldClusters = new HashMap<>(goldC);
        this.systemClusters = new HashMap<>(systemC);

        if (ClusterMapUtils.checkClustersConsistancy(goldClusters, systemClusters)) {
            ClusterContingencyTable cct = new ClusterContingencyTable();
            Collection<Set<String>> valuesGold = goldClusters.values();
            Collection<Set<String>> valuessys = systemClusters.values();

            int goldClusterNum = goldClusters.size();
            int sysClusterNum = systemClusters.size();
            int goldInstance
                    = getInstancesSet(valuesGold).size();
            int sysInstance
                    = getInstancesSet(valuessys).size();
            //System.err.println("in");
            cct.process(valuessys, valuesGold);
            //  System.err.println("out");
            SetMatchingPurity setMatching = cct.getSetMatching();
            double purity = setMatching.purity();
            double inversePurity = setMatching.inversePurity();
            double puIpuf1
                    = setMatching.f1Measure();
            //fMeasureSecond();

            BCubed bCubed = cct.getBCubed();
            PairCounting paircount = cct.getPaircount();

            EditDistance edit = cct.getEdit();

            Entropy entropy = cct.getEntropy();
            //puIpuf1= cct.getEntropy().entropyNMISum();
            // System.err.println("**  " + goldInstance+" "+sysInstance+" " +(goldInstance-sysInstance));
            double pairCountF1 = paircount.f1Measure();
            double bCubedF1 = bCubed.f1Measure();

            //bCubed.f1Measure();
            //edit.f1Measure();
            //System.out.println(paircount.recall());
            //setMatching.f1Measure();
            evr = new EvaluationResult(goldClusterNum, goldInstance,
                    sysClusterNum, sysInstance, pairCountF1, bCubedF1, purity, inversePurity, puIpuf1, cct);

        } else {
            evr = null;
        }

    }

    /**
     *
     * @return Evaluation results if it exist, otherwise null. By *if it exist*,
     * I mean the data samples are both in gold and test sets.
     */
    public EvaluationResult getEvr() {
        return evr;
    }

    private Set<String> getInstancesSet(Collection<Set<String>> instanceClustr) {
        Set<String> sss = new HashSet<>();
        for (Set<String> s : instanceClustr) {
            for (String x : s) {
                sss.add(x);
            }
        }
        return sss;
    }

    public void printCluster(Map<String, Set<String>> clusters) {
        for (String key : clusters.keySet()) {
            Set<String> get = clusters.get(key);

            for (String inst : get) {
                System.err.println(key + " " + inst);
            }
            // clusters.put(key, newSet);
        }
    }

}
