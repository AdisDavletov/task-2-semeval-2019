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

import semeval.lmu.ifi.dbs.elki.evaluation.clustering.GeneralClusterEvaluation;
import semeval.utils.UtilReadProcessEvaluationFiles;
import semeval.utils.ClusterMapUtils;
import semeval.utils.EvaluationResult;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class MetricsForFrameGrouping {

//    private EvaluationResult evrSum;
    final private int minNumberOfSensesInGold;
    final private int minClusterSampleSize;
    // final private String pathToGoldFile;
    // final private String pathToSystemInitFile;
    private Map<String, Map<String, Set<String>>> goldFramesPartionedByLexHead;
    private Map<String, Map<String, Set<String>>> sysInitFramesPartitionedByLexHead;
    private Map<String, Set<String>> flattenedGoldFrames;
    private Map<String, Set<String>> flattenedSystemInitFrames;

    /**
     * Read some frame files in the expected format, find the subset common
     * between them and do the cluster evaluation
     *
     * @param minNumberOfSensesInGold
     * @param minClusterSampleSize
     * @param goldFile
     * @param systemInitFile
     * @throws IOException
     */
    public MetricsForFrameGrouping(int minNumberOfSensesInGold,
            int minClusterSampleSize, String goldFile, String systemInitFile) throws IOException {
        this.minNumberOfSensesInGold = minNumberOfSensesInGold;
        this.minClusterSampleSize = minClusterSampleSize;
        //   this.pathToGoldFile = goldFile;
        //  this.pathToSystemInitFile = systemInitFile;
        goldFramesPartionedByLexHead = UtilReadProcessEvaluationFiles.readParseResultToHeadClusters(goldFile);
        sysInitFramesPartitionedByLexHead = UtilReadProcessEvaluationFiles.readParseResultToHeadClusters(systemInitFile);
        trimAndFlattenMaps();
    }

    public MetricsForFrameGrouping(int minNumberOfSensesInGold, int minClusterSampleSize,
            Map<String, Map<String, Set<String>>> goldFramesPartionedByLexHead,
            Map<String, Map<String, Set<String>>> sysInitFramesPartitionedByLexHead,
            Map<String, Set<String>> flattenedGoldFrames,
            Map<String, Set<String>> flattenedSystemInitFrames) {

        this.minNumberOfSensesInGold = minNumberOfSensesInGold;
        this.minClusterSampleSize = minClusterSampleSize;
        this.goldFramesPartionedByLexHead = goldFramesPartionedByLexHead;
        this.sysInitFramesPartitionedByLexHead = sysInitFramesPartitionedByLexHead;
        trimAndFlattenMaps();
    }

    public Map<String, Set<String>> getFlattenedGoldFrames() {
        return flattenedGoldFrames;
    }

    public MetricsForFrameGrouping(int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void trimAndFlattenMaps() {
        ClusterMapUtils.trimHeadClusters(minNumberOfSensesInGold, minClusterSampleSize, goldFramesPartionedByLexHead, sysInitFramesPartitionedByLexHead);
        flattenedGoldFrames = ClusterMapUtils.flattenAMap(goldFramesPartionedByLexHead);
        flattenedSystemInitFrames = ClusterMapUtils.flattenAMap(sysInitFramesPartitionedByLexHead);
        ClusterMapUtils.trimClusters(minClusterSampleSize, flattenedGoldFrames, flattenedSystemInitFrames);
    }

    public EvaluationResult blAllin1C() throws IOException {
        Set<String> allInstances = ConcurrentHashMap.newKeySet();
        for (String key : this.flattenedSystemInitFrames.keySet()) {
            Set<String> instances = this.flattenedSystemInitFrames.get(key);
            allInstances.addAll(instances);
        }
        ConcurrentHashMap<String, Set<String>> allIn1ClusterMap = new ConcurrentHashMap<>(1);
        allIn1ClusterMap.put("allIn1Cluster", allInstances);
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(this.flattenedGoldFrames, allIn1ClusterMap);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

//    public EvaluationResult createBaselineOneClusterPerInstance() throws IOException {
////        Map<String, Map<String, Set<String>>> goldFrames = UtilReadProcessEvaluationFiles.readParseResultToHeadClusters(goldDataset);
////        Map<String, Map<String, Set<String>>> sysFrames = UtilReadProcessEvaluationFiles.readParseResultToHeadClusters(realParsedData);
//        ClusterMapUtils.trimHeadClusters(minNumberOfSensesInGold, minClusterSampleSize, goldFrames, sysFrames);
//        //Map<String, Set<String>> goldClustersAll = new HashMap<>(1);
//       // Map<String, Set<String>> systemClustersAll = new HashMap<>(1);
//        for (String keyHeadVerb : goldFrames.keySet()) {
//            Map<String, Set<String>> goldClusters = goldFrames.get(keyHeadVerb);
//            for (String key : goldClusters.keySet()) {
//                goldClustersAll.put(keyHeadVerb + "." + key, goldClusters.get(key));
//            }
//            if (sysFrames.containsKey(keyHeadVerb)) {
//                Map<String, Set<String>> systemClustersForThisHead = sysFrames.get(keyHeadVerb);
//                for (String key : systemClustersForThisHead.keySet()) {
//
//                    for (String instance : systemClustersForThisHead.get(key)) {
//                        Set<String> thisInstanceCluset = new HashSet<>();
//                        thisInstanceCluset.add(instance);
//                        systemClustersAll.put(instance, thisInstanceCluset);
//                    }
//                }
//
//            }
//        }
//        ClusterMapUtils.trimClusters(minClusterSampleSize, goldClustersAll, systemClustersAll);
//        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(goldClustersAll, systemClustersAll);
//        EvaluationResult evr = gceval.getEvr();
//        return evr;
//    }
    /**
     * Put instances from the gold set into a randomBucket number of clusters
     *
     * @param randomBucket
     * @return
     * @throws IOException
     */
    public EvaluationResult createBaselineRandom(int randomBucket) throws IOException {
        Random rnd = new Random();
        Map<String, Set<String>> randomClusters = new ConcurrentHashMap<>();
        for (int i = 0; i < randomBucket; i++) {
            Set<String> newKeySet = ConcurrentHashMap.newKeySet();
            randomClusters.put(Integer.toString(i), newKeySet);
        }
        flattenedGoldFrames.values().parallelStream().forEach(goldCluster -> {
            goldCluster.forEach(instance -> {
                String nextRndCluster = Integer.toString(rnd.nextInt(randomBucket));
                Set<String> get = randomClusters.get(nextRndCluster);
                get.add(instance);
            });
        }
        );
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(flattenedGoldFrames, randomClusters);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

    /**
     * For each lexical head, put its instances into a randomBucket number of
     * instances
     *
     * @param randomBucket
     * @return
     * @throws IOException
     */
    public EvaluationResult createBaselineRandomPerHead(int randomBucket) throws IOException {
        Random rnd = new Random();

        Map<String, Set<String>> randomClusters = new ConcurrentHashMap<>();
        goldFramesPartionedByLexHead.keySet().parallelStream().forEach(head -> {
            for (int i = 0; i < randomBucket; i++) {
                randomClusters.put(head + "." + i, ConcurrentHashMap.newKeySet());
            }
        });

        goldFramesPartionedByLexHead.keySet().forEach(head -> {
            goldFramesPartionedByLexHead.get(head).values().parallelStream().forEach(instanceSet -> {
                instanceSet.parallelStream().forEach(instance -> {
                    String nextRndCluster = head + "." + rnd.nextInt(randomBucket);
                    randomClusters.get(nextRndCluster).add(instance);
                });
            });
        });

        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(flattenedGoldFrames, randomClusters);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

    /**
     * Baseline of a cluster per lexical head
     *
     * @return
     * @throws IOException
     * @throws Exception
     */
    public EvaluationResult bl1CPerHead() throws IOException, Exception {
        Map<String, Set<String>> aClusterPerHeadMap = new ConcurrentHashMap<>();
        for (String lexicalHead : this.goldFramesPartionedByLexHead.keySet()) {

            if (aClusterPerHeadMap.containsKey(lexicalHead)) {
                throw new Exception("Illogical data flow! fix evaluation problem");
            }
            Map<String, Set<String>> headClusters = goldFramesPartionedByLexHead.get(lexicalHead);
            Set<String> allInstancesForThisHead = ConcurrentHashMap.newKeySet();

            headClusters.keySet().parallelStream().forEach(key -> {
                allInstancesForThisHead.addAll(headClusters.get(key));
            });

            aClusterPerHeadMap.put(lexicalHead, allInstancesForThisHead);
        }
        ClusterMapUtils.trimClusters(minClusterSampleSize, this.flattenedSystemInitFrames, aClusterPerHeadMap);

        // System.out.println("---> size " + ClusterMapUtils.countInstancesMap(aClusterPerHeadMap));
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(flattenedGoldFrames, aClusterPerHeadMap);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

    public EvaluationResult bl1CPerInstance() throws IOException, Exception {
        Map<String, Set<String>> aClusterPerHeadMap = new ConcurrentHashMap<>();
        AtomicInteger ai = new AtomicInteger();

        for (String lexicalHead : this.goldFramesPartionedByLexHead.keySet()) {

//            if (aClusterPerHeadMap.containsKey(lexicalHead)) {
//                throw new Exception("Illogical data flow! fix evaluation problem");
//            }
            Map<String, Set<String>> headClusters = goldFramesPartionedByLexHead.get(lexicalHead);

            headClusters.keySet().parallelStream().forEach((String key) -> {
                Set<String> getSet = headClusters.get(key);
                for (String s : getSet) {
                    Set<String> allInstancesForThisHead = ConcurrentHashMap.newKeySet();
                    allInstancesForThisHead.add(s);
                    aClusterPerHeadMap.put(lexicalHead + ai.incrementAndGet(), allInstancesForThisHead);
                }
            });

        }
        ClusterMapUtils.trimClusters(minClusterSampleSize, this.flattenedSystemInitFrames, aClusterPerHeadMap);

        // System.out.println("---> size " + ClusterMapUtils.countInstancesMap(aClusterPerHeadMap));
        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(flattenedGoldFrames, aClusterPerHeadMap);
        EvaluationResult evr = gceval.getEvr();
        return evr;
    }

    public EvaluationResult process(String sysgeneratedFile) throws IOException {
        Map<String, Map<String, Set<String>>> sysFrames = UtilReadProcessEvaluationFiles.readParseResultToHeadClusters(sysgeneratedFile);
        Map<String, Set<String>> flattenAMap = ClusterMapUtils.flattenAMap(sysFrames);
        boolean checkClustersConsistancy = ClusterMapUtils.checkClustersConsistancy(flattenAMap, this.flattenedSystemInitFrames);
        if (!checkClustersConsistancy) {
            
            // warn that baselines are different now
            // but anyway continue and compute everything
            // trimming one of the sets is enough as the trimAndFlattenMaps takes care after rest
            //Logger.getAnonymousLogger().log(Level.FINE, "WARNING!!! You need new baselines!");
            ClusterMapUtils.trimHeadClusters(minNumberOfSensesInGold, minClusterSampleSize,
                    this.goldFramesPartionedByLexHead, sysFrames);
            trimAndFlattenMaps();
            ClusterMapUtils.trimClusters(minClusterSampleSize, this.flattenedGoldFrames, flattenAMap);
        }

        GeneralClusterEvaluation gceval = new GeneralClusterEvaluation(this.flattenedGoldFrames, flattenAMap);
        EvaluationResult evrSum = gceval.getEvr();
        evrSum.refreshStats();
        return evrSum;
    }


}
