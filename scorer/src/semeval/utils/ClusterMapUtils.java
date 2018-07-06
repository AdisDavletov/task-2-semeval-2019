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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ClusterMapUtils {

    /**
     * Flatten a lex-hierarchy map to a flattened one.
     *
     * @param map
     * @return
     */
    public static Map<String, Set<String>> flattenAMap(Map<String, Map<String, Set<String>>> map) {
        Map<String, Set<String>> mapFlattened = new ConcurrentHashMap<>();

        //for (String lexHead :
        map.keySet().parallelStream().forEach(lexHead -> {
            Map<String, Set<String>> clustersForHead = map.get(lexHead);

            //for (String clusterLabel : 
            clustersForHead.keySet().forEach(clusterLabel -> {

                if (mapFlattened.containsKey(clusterLabel)) {

                    mapFlattened.get(clusterLabel)
                            .addAll(clustersForHead.get(clusterLabel));
                } else {

                    Set<String> toAdd = ConcurrentHashMap.newKeySet();
                    toAdd.addAll(clustersForHead.get(clusterLabel));
                    Set<String> putIfAbsent = mapFlattened.putIfAbsent(
                            clusterLabel, toAdd);
                    if (putIfAbsent != null) {
                        // System.err.println("Thread Race situation ..." );
                        putIfAbsent.addAll(toAdd);
                    }
                }
            });
        });
        return mapFlattened;
    }

    /**
     * The method removes those instances that appear only in one of the
     * datasets, for errors due to finding heads and arguments, instances in
     * gold and sys-generated can be different
     *
     * @param minClusterSampleSize
     * @param goldClusters
     * @param systemClusters
     * @return
     */
    public static boolean trimClusters(
            int minClusterSampleSize,
            Map<String, Set<String>> goldClusters,
            Map<String, Set<String>> systemClusters) {
        Set<String> instanceCommon = new HashSet<>();
        goldClusters.values().stream().forEach((cluster) -> {
            instanceCommon.addAll(cluster);
        });
        Set<String> instancesIn2 = new HashSet<>();
        systemClusters.values().stream().forEach((cluster) -> {
            instancesIn2.addAll(cluster);
        });
        instanceCommon.retainAll(instancesIn2);
        Iterator<String> iteratorSystem = systemClusters.keySet().iterator();
        while (iteratorSystem.hasNext()) {
            String nextClusterLabel = iteratorSystem.next();
            Set<String> get = systemClusters.get(nextClusterLabel);
            get.retainAll(instanceCommon);
            if (get.isEmpty()) {
                iteratorSystem.remove();
            }
        }
        boolean changed = false;
        Iterator<String> iteratorGold = goldClusters.keySet().iterator();
        while (iteratorGold.hasNext()) {
            String nextClusterLabel = iteratorGold.next();
            Set<String> get = goldClusters.get(nextClusterLabel);
            get.retainAll(instanceCommon);

            if (get.size() < minClusterSampleSize) {

                iteratorGold.remove();
                changed = true;
            }
        }
        if (changed) {
            return trimClusters(minClusterSampleSize, goldClusters, systemClusters);
        } else {
            return !instanceCommon.isEmpty();
        }
    }

    public static boolean checkClustersConsistancy(
            Map<String, Set<String>> goldClusters,
            Map<String, Set<String>> systemClusters) {
        int countInstancesGolds = 0;
        Set<String> instancesGold = new HashSet<>();
        for (Set<String> cluster : goldClusters.values()) {
            countInstancesGolds += cluster.size();
            instancesGold.addAll(cluster);
        };
        if (instancesGold.size() != countInstancesGolds) {
            
            throw new RuntimeException(countInstancesGolds+" \\= "+ instancesGold.size()+ "There is inconsistencies in your gold clusters ... there is at least one instance that appears in two clusters");
        }

        int countInstancesSystem = 0;
        Set<String> instancesInSystem = new HashSet<>();
        for (Set<String> cluster : systemClusters.values()) {
            countInstancesSystem += cluster.size();
            instancesInSystem.addAll(cluster);
        };
        if (instancesInSystem.size() != countInstancesSystem) {
//            throw new RuntimeException("There is inconsistencies in your system clusters ... there is at least one instance that appears in two clusters "
//                    + countInstancesSystem + " vs " + countInstancesGolds);
//            System.err.println(("There is inconsistencies in your system clusters ... there is at least one instance that appears in two clusters "
//                    + countInstancesSystem + " vs " + countInstancesGolds));
        Logger.getAnonymousLogger().log(Level.FINE, "There are inconsistencies in your system clusters ... there is at least one instance that appears in two clusters {0} vs {1}", new Object[]{countInstancesSystem, countInstancesGolds});
            return false;
        }
        if (countInstancesSystem != countInstancesGolds) {
            //throw new RuntimeException("There is inconsistencies between gold and system !!! " + countInstancesSystem + " vs " + countInstancesGolds);
            //System.err.println("There are inconsistencies between gold and system !!! " + countInstancesSystem + " vs " + countInstancesGolds);
            Logger.getAnonymousLogger().log(Level.FINE, "There are inconsistencies between gold and system !!! {0} vs {1}", new Object[]{countInstancesSystem, countInstancesGolds});
            return false;
        }
        int sizeBefore = instancesGold.size();
        instancesGold.retainAll(instancesInSystem);
        int sizeAfter = instancesGold.size();
        if (sizeBefore != sizeAfter) {
            throw new RuntimeException("There are inconsistencies between gold and system !!!");
        }
        return true;
    }

    public static int countInstancesMapofMap(Map<String, Map<String, Set<String>>> map) {
        AtomicInteger ai = new AtomicInteger();
        map.entrySet().parallelStream().forEach((Map.Entry<String, Map<String, Set<String>>> entrySet) -> {
            entrySet.getValue().entrySet().parallelStream().forEach(set -> {
                ai.addAndGet(set.getValue().size());
            });
        });
        return ai.intValue();
    }

    public static int countInstancesMap(Map<String, Set<String>> map) {
        AtomicInteger ai = new AtomicInteger();
        map.entrySet().parallelStream().forEach((Map.Entry<String, Set<String>> set) -> {

            ai.addAndGet(set.getValue().size());
        });
        return ai.intValue();
    }

    public static boolean trimHeadClusters(
            int minClusterCadrdinality, int minClusterSampleSize,
            Map<String, Map<String, Set<String>>> goldClusters,
            Map<String, Map<String, Set<String>>> systemClusters) {
        // first remove those with different lexicalization of head
        Iterator<String> iterator = systemClusters.keySet().iterator();
        while (iterator.hasNext()) {
            String lexHead = iterator.next();
            if (!goldClusters.containsKey(lexHead)) {
                //Logger.getAnonymousLogger().log(Level.INFO, "Removed head " + lexHead +" from the evaluation set");
                iterator.remove();
            }
        }

        Iterator<String> iteratorGoldk = goldClusters.keySet().iterator();
        while (iteratorGoldk.hasNext()) {
            String lexHead = iteratorGoldk.next();
            if (!systemClusters.containsKey(lexHead)) {
                //Logger.getAnonymousLogger().log(Level.INFO, "Removed head " + lexHead +" from the evaluation set");
                iteratorGoldk.remove();
            }
        }

        ///////
        Set<String> instanceCommon = new HashSet<>();
        goldClusters.values().stream().forEach((cluster) -> {
            cluster.values().forEach(instanceSet -> {
                instanceCommon.addAll(instanceSet);
            });

        });
        Set<String> instancesIn2 = new HashSet<>();
        systemClusters.values().stream().forEach((cluster) -> {
            cluster.values().forEach(instanceSet -> {
                instancesIn2.addAll(instanceSet);
            });
        });
        instanceCommon.retainAll(instancesIn2);
        Iterator<String> iteratorSystem = systemClusters.keySet().iterator();
        while (iteratorSystem.hasNext()) {
            String nextClusterLabel = iteratorSystem.next();
            Map<String, Set<String>> headClusters = systemClusters.get(nextClusterLabel);
            Iterator<String> headClustersIT = headClusters.keySet().iterator();
            while (headClustersIT.hasNext()) {
                String next = headClustersIT.next();
                Set<String> clusterInstances = headClusters.get(next);
                clusterInstances.retainAll(instanceCommon);
                if (clusterInstances.isEmpty()) {
                    headClustersIT.remove();
                }
            }
            if (headClusters.isEmpty()) {
                iteratorSystem.remove();
            }
        }
        boolean changed = false;
        Iterator<String> iteratorGold = goldClusters.keySet().iterator();
        while (iteratorGold.hasNext()) {
            String nextClusterLabel = iteratorGold.next();
            Map<String, Set<String>> getGoldHeadClusters = goldClusters.get(nextClusterLabel);
            Iterator<String> goldHeadClusterKeys = getGoldHeadClusters.keySet().iterator();
            while (goldHeadClusterKeys.hasNext()) {
                Set<String> goldCluster = getGoldHeadClusters.get(goldHeadClusterKeys.next());
                goldCluster.retainAll(instanceCommon);
                if (goldCluster.size() < minClusterSampleSize) {
                    goldHeadClusterKeys.remove();
                    changed = true;
                }
            }

            if (getGoldHeadClusters.size() < minClusterCadrdinality || getGoldHeadClusters.isEmpty()) {
                changed = true;
                iteratorGold.remove();
            }
        }
        if (changed) {
            return trimHeadClusters(minClusterCadrdinality, minClusterSampleSize, goldClusters, systemClusters);
        } else {
            return !instanceCommon.isEmpty();
        }
    }

}
