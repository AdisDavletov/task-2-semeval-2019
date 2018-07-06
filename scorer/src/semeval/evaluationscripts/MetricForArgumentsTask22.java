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
package semeval.evaluationscripts;

import semeval.utils.EvaluationResult;
import semeval.utils.ClusterMapUtils;
import semeval.utils.UtilReadProcessEvaluationFiles;
import semeval.lmu.ifi.dbs.elki.evaluation.clustering.GeneralClusterEvaluation;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class MetricForArgumentsTask22 {

    //private EvaluationResult evrSum;
//    final private int minNumberOfClusterInGold;
    final private int minClusterSampleSize;
    private final Map<String, Set<String>> fileToFilterMap;

    public MetricForArgumentsTask22(int minClusterSampleSize, String fileToFilter) throws IOException {
        // this.minNumberOfClusterInGold = minNumberOfClusterInGold;
        this.minClusterSampleSize = minClusterSampleSize;
        fileToFilterMap = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(fileToFilter);

    }

    /**
     * This methods gives the baseline of all in 1 cluster for a given golddata, in which the gold records are filtered by those that appeared in the second file.  
     * Normally, we pass golddata for both arguments
     * @param goldDataset
     * @param realParsedData
     * @return
     * @throws IOException 
     */
    public EvaluationResult createBaselineAllIn1Cluster(String goldDataset,
            String realParsedData) throws IOException {
        Map<String, Set<String>> goldFrames = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(goldDataset);
        Map<String, Set<String>> systemFrames = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(realParsedData);

        Map<String, Set<String>> systemFramesAllInOne = new HashMap<>(1);
        Set<String> allInstances = new HashSet<>();
        for (String key : systemFrames.keySet()) {
            allInstances.addAll(systemFrames.get(key));
        }
        systemFramesAllInOne.put("allIn1Clusetr", allInstances);
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, fileToFilterMap);
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, systemFramesAllInOne);
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(goldFrames, systemFramesAllInOne);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

    public EvaluationResult createBaseline1ClusterPerInstance(String goldDataset, String realParsedData) throws IOException {
        Map<String, Set<String>> goldFrames = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(goldDataset);
        Map<String, Set<String>> systemFrames = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(realParsedData);
        Map<String, Set<String>> systemOneClusterPerInstance = new HashMap<>();
        for (Set<String> instances : systemFrames.values()) {
            for (String instance : instances) {
                Set<String> cluster = new HashSet<>();
                cluster.add(instance);
                systemOneClusterPerInstance.put(instance, cluster);
            }
        }
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, fileToFilterMap);
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, systemOneClusterPerInstance);
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(goldFrames, systemOneClusterPerInstance);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

    public EvaluationResult createBaseline1ClusterPerGramaticalRelationship(
            String goldDataset,
            String syntacBasedFile) throws IOException {

        Map<String, Set<String>> goldFrames = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(goldDataset);
        Map<String, Set<String>> systemFramesSyntactic = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(syntacBasedFile);
        //Map<String, Set<String>> systemFramesRealInstances = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(realParsedData);

        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, fileToFilterMap);
        //ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, systemFramesRealInstances);
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, systemFramesSyntactic);
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(goldFrames, systemFramesSyntactic);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

    public EvaluationResult createBaselineRandom(String goldDataset,
            String realParsedData, int clusterNum) throws IOException {

        Map<String, Set<String>> goldFrames = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(goldDataset);
        Map<String, Set<String>> systemFramesRealInstances = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(realParsedData);
        Map<String, Set<String>> randomSet = new HashMap<>();
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, fileToFilterMap);
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, systemFramesRealInstances);
        Random r = new Random();
        for (Set<String> clusterSet : systemFramesRealInstances.values()) {
            //System.out.println(clusterSet.size());
            for (String inst : clusterSet) {
                int nextInt = r.nextInt(clusterNum);
                if (randomSet.containsKey(nextInt + "")) {
                    randomSet.get(nextInt + "").add(inst);
                } else {
                    Set<String> ss = new HashSet<>();
                    ss.add(inst);
                    randomSet.put(nextInt + "", ss);

                }
            }
        }

        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(goldFrames, randomSet);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

    public EvaluationResult process(String goldDataset, String file2) throws IOException {
        Map<String, Set<String>> goldFrames = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(goldDataset);

        Map<String, Set<String>> sysFrames = UtilReadProcessEvaluationFiles.readParseResultToArgClusters(file2);

        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, fileToFilterMap);
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, sysFrames);
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(goldFrames, sysFrames);
        EvaluationResult evrSum = gceval.getEvr();
        evrSum.refreshStats();
        return evrSum;
    }

    public EvaluationResult process(Map<String, Set<String>> goldFrames, Map<String, Set<String>> sysFrames) throws IOException {
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, fileToFilterMap);
        ClusterMapUtils.trimClusters(minClusterSampleSize, goldFrames, sysFrames);
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(goldFrames, sysFrames);
        EvaluationResult evrSum = gceval.getEvr();
        return evrSum;
    }

}
