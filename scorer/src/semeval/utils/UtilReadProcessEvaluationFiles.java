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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class UtilReadProcessEvaluationFiles {

    /**
     * Create the single class baseline for lexical head instances from an input
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Map<String, Set<String>> createSingleClassClusterBaseLine(String file) throws IOException {
        Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
      
        
            Stream<String> lines;
            lines = Files.lines(Paths.get(file));
            lines.forEach(line -> {
                String[] split = line.split(" ");
                String sentID = split[0];
                String headLocation = split[1];
                String uniqueIDForInstance = sentID + "-" + headLocation;
                String assignedCluster = "all-in-one-class";// split[2].split("\\.")[1];
                if (clusterKeyIDMap.containsKey(assignedCluster)) {
                    clusterKeyIDMap.get(assignedCluster).add(uniqueIDForInstance);
                } else {
                    Set<String> idsForThisCluster = new HashSet<>();
                    idsForThisCluster.add(uniqueIDForInstance);
                    clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
                }
            });

            lines.close();
        
        return clusterKeyIDMap;
    }

    public static Set<String> createHeadSet(String file) throws IOException {
        Set<String> heads = ConcurrentHashMap.newKeySet();
        Stream<String> lines = Files.lines(Paths.get(file));
               lines. forEach(line -> {
            String[] split = line.split(" ");
            String sentID = split[0];
            String headLocation = split[1];
            String head = split[2].split("\\.")[0];
            String uniqueIDForInstance = sentID + "-" + headLocation;
            heads.add(head);

        });
               lines.close();
        return heads;
    }

//    public static Map<String, Map<String, Set<String>>> readParseResultToHeadClustersSingleClassBaseline(String file) throws IOException {
//        Map<String, Map<String, Set<String>>> verbInstances = new HashMap<>();
//        Files.lines(Paths.get(file)).forEach(line -> {
//            String[] split = line.split(" ");
//            String sentID = split[0];
//            String headLocation = split[1];
//            String uniqueIDForInstance = sentID + "-" + headLocation;
//            String head = split[2].split("\\.")[0];
//            String assignedCluster = "all-in-one-cluster";
//            //split[2].split("\\.")[1];
//            if (verbInstances.containsKey(head)) {
//                Map<String, Set<String>> clusterKeyIDMap = verbInstances.get(head);
//                if (clusterKeyIDMap.containsKey(assignedCluster)) {
//                    clusterKeyIDMap.get(assignedCluster).add(uniqueIDForInstance);
//                } else {
//                    Set<String> idsForThisCluster = new HashSet<>();
//                    idsForThisCluster.add(uniqueIDForInstance);
//                    clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
//                }
//            } else {
//                Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
//                Set<String> idsForThisCluster = new HashSet<>();
//                idsForThisCluster.add(uniqueIDForInstance);
//                clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
//                verbInstances.put(head, clusterKeyIDMap);
//            }
//        });
//        return verbInstances;
//    }
    /**
     * Reads a gold or system generated file to a map. The key for the map is
     * the lexical head The value for the map is another map. For this inner
     * map, the key is the cluster label and the value is the set of instances
     * that are labeled with this label.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Map<String, Map<String, Set<String>>> readParseResultToHeadClusters(String file) throws IOException {
        Map<String, Map<String, Set<String>>> lexHeadMapToClusterSets = new ConcurrentHashMap<>();
        Stream<String> parallel = Files.lines(Paths.get(file)).parallel();
       parallel .forEach(line -> {
            String[] split = line.split(" ");
            String sentID = split[0];
            String headLocation = split[1];
            String uniqueIDForInstance = sentID + "-" + headLocation;
            String head = split[2].split("\\.", 2)[0];
            String assignedCluster = split[2].split("\\.", 2)[1];

            if (lexHeadMapToClusterSets.containsKey(head)) {
                Map<String, Set<String>> clusterKeyIDMap = lexHeadMapToClusterSets.get(head);
                if (clusterKeyIDMap.containsKey(assignedCluster)) {
                    clusterKeyIDMap.get(assignedCluster).add(uniqueIDForInstance);
                } else {
                    Set<String> idsForThisCluster = ConcurrentHashMap.newKeySet();
                    idsForThisCluster.add(uniqueIDForInstance);
                    Set<String> putIfAbsent = clusterKeyIDMap.putIfAbsent(assignedCluster, idsForThisCluster);
                    if (putIfAbsent != null) {
                        putIfAbsent.addAll(idsForThisCluster);
                        //  System.err.println("Situation 1 ");
                    }
                }
            } else {
                Set<String> idsForThisCluster = ConcurrentHashMap.newKeySet();
                idsForThisCluster.add(uniqueIDForInstance);
                Map<String, Set<String>> clusterKeyIDMap = new ConcurrentHashMap<>();
                Set<String> putIfAbsentInner = clusterKeyIDMap.putIfAbsent(assignedCluster, idsForThisCluster);
                if (putIfAbsentInner != null) {
                    putIfAbsentInner.addAll(idsForThisCluster);
                    //  System.err.println("Situation 2 ");
                }
                Map<String, Set<String>> putIfAbsentOuter = lexHeadMapToClusterSets.putIfAbsent(head, clusterKeyIDMap);
                if (putIfAbsentOuter != null) {

                    Set<String> get = putIfAbsentOuter.get(assignedCluster);
                    if (get != null) {
                        get.addAll(idsForThisCluster);
                    } else {
                        Set<String> putIfAbsent2 = putIfAbsentOuter.putIfAbsent(assignedCluster, idsForThisCluster);
                        if (putIfAbsent2 != null) {
                            putIfAbsent2.addAll(idsForThisCluster);
                        }
                        // System.err.println("Situation 4..");
                    }
                    // System.err.println("Situation 3 ");
                }
            }
        });
       parallel.close();
        return lexHeadMapToClusterSets;
    }

    public static Map<String, Set<String>> readParseResultTo1VerbPerClusterClusters(String file) throws IOException {
        Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
        Path get = Paths.get(file);
        try {
            Stream<String> lines = Files.lines(get);
            lines.forEach(line -> {
                String[] split = line.split(" ");
                String sentID = split[0];
                String headLocation = split[1];
                String uniqueIDForInstance = sentID + "-" + headLocation;
                String head = split[2].split("\\.", 2)[0];
                String assignedCluster = split[2].split("\\.", 2)[1];

            //if (verbInstances.containsKey(head)) {
                //Map<String, Set<String>> clusterKeyIDMap = verbInstances.get(head);
                if (clusterKeyIDMap.containsKey(head)) {
                    clusterKeyIDMap.get(head).add(uniqueIDForInstance);
                } else {
                    //System.err.println("HEad " + head);
                    Set<String> idsForThisCluster = new HashSet<>();
                    idsForThisCluster.add(uniqueIDForInstance);
                    Set<String> putIfAbsent = clusterKeyIDMap.putIfAbsent(head, idsForThisCluster);
                    if (putIfAbsent != null) {
                        System.err.println("Yo ... error");
                    }
                }
//            } else {
//                Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
//                Set<String> idsForThisCluster = new HashSet<>();
//                idsForThisCluster.add(uniqueIDForInstance);
//                clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
//                verbInstances.put(head, clusterKeyIDMap);
//            }
            });

            lines.close();

        } catch (Exception f) {
            System.err.println("Catched exc " + f);

        }
        
        return clusterKeyIDMap;
    }

    public static Map<String, Set<String>> readParseResultToFrameNetClusters(String file) throws IOException {
        Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(file));
        lines.forEach(line -> {
            String[] split = line.split(" ");
            String sentID = split[0];
            String headLocation = split[1];
            String uniqueIDForInstance = sentID + "-" + headLocation;
            String head = split[2].split("\\.", 2)[0];
            String assignedCluster = split[2].split("\\.", 2)[1];
            assignedCluster = head + assignedCluster;
            //if (verbInstances.containsKey(head)) {
            //Map<String, Set<String>> clusterKeyIDMap = verbInstances.get(head);
            if (clusterKeyIDMap.containsKey(assignedCluster)) {
                clusterKeyIDMap.get(assignedCluster).add(uniqueIDForInstance);
            } else {
                Set<String> idsForThisCluster = new HashSet<>();
                idsForThisCluster.add(uniqueIDForInstance);
                clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
            }
//            } else {
//                Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
//                Set<String> idsForThisCluster = new HashSet<>();
//                idsForThisCluster.add(uniqueIDForInstance);
//                clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
//                verbInstances.put(head, clusterKeyIDMap);
//            }
        });
        lines.close();
        return clusterKeyIDMap;
    } 
    
    public static Map<String, Set<String>> readParseResultToClusters(String file) throws IOException {
        Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(file));
        lines.forEach(line -> {
            String[] split = line.split(" ");
            String sentID = split[0];
            String headLocation = split[1];
            String uniqueIDForInstance = sentID + "-" + headLocation;
            String head = split[2].split("\\.", 2)[0];
            String assignedCluster = split[2].split("\\.", 2)[1];
            assignedCluster = assignedCluster;
            //if (verbInstances.containsKey(head)) {
            //Map<String, Set<String>> clusterKeyIDMap = verbInstances.get(head);
            if (clusterKeyIDMap.containsKey(assignedCluster)) {
                clusterKeyIDMap.get(assignedCluster).add(uniqueIDForInstance);
            } else {
                Set<String> idsForThisCluster = new HashSet<>();
                idsForThisCluster.add(uniqueIDForInstance);
                clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
            }
//            } else {
//                Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
//                Set<String> idsForThisCluster = new HashSet<>();
//                idsForThisCluster.add(uniqueIDForInstance);
//                clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
//                verbInstances.put(head, clusterKeyIDMap);
//            }
        });
        lines.close();
        return clusterKeyIDMap;
    }

    public static Map<String, Map<String, Set<String>>> readParseResultToHeadClusters(String file, String lemma) throws IOException {
        Map<String, Map<String, Set<String>>> verbInstances = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(file));
        lines.forEach(line -> {
            String[] split = line.split(" ");
            String sentID = split[0];
            String headLocation = split[1];
            String uniqueIDForInstance = sentID + "-" + headLocation;
            String head = split[2].split("\\.", 2)[0];
            String assignedCluster = split[2].split("\\.", 2)[1];
            if (lemma.equalsIgnoreCase(head)) {
                if (verbInstances.containsKey(head)) {
                    Map<String, Set<String>> clusterKeyIDMap = verbInstances.get(head);
                    if (clusterKeyIDMap.containsKey(assignedCluster)) {
                        clusterKeyIDMap.get(assignedCluster).add(uniqueIDForInstance);
                    } else {
                        Set<String> idsForThisCluster = new HashSet<>();
                        idsForThisCluster.add(uniqueIDForInstance);
                        clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
                    }
                } else {
                    Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
                    Set<String> idsForThisCluster = new HashSet<>();
                    idsForThisCluster.add(uniqueIDForInstance);
                    clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
                    verbInstances.put(head, clusterKeyIDMap);
                }
            }
        });
        lines.close();
        return verbInstances;
    }

    public static Map<String, Map<String, Set<String>>> readParseResultToHeadClusters(String file, Set<String> lemmaSet) throws IOException {
        Map<String, Map<String, Set<String>>> verbInstances = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(file));
       lines .forEach(line -> {
            String[] split = line.split(" ");
            String sentID = split[0];
            String headLocation = split[1];
            String uniqueIDForInstance = sentID + "-" + headLocation;
            String head = split[2].split("\\.", 2)[0];
            String assignedCluster = split[2].split("\\.", 2)[1];
            if (lemmaSet.contains(head)) {
                if (verbInstances.containsKey(head)) {
                    Map<String, Set<String>> clusterKeyIDMap = verbInstances.get(head);
                    if (clusterKeyIDMap.containsKey(assignedCluster)) {
                        clusterKeyIDMap.get(assignedCluster).add(uniqueIDForInstance);
                    } else {
                        Set<String> idsForThisCluster = new HashSet<>();
                        idsForThisCluster.add(uniqueIDForInstance);
                        clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
                    }
                } else {
                    Map<String, Set<String>> clusterKeyIDMap = new HashMap<>();
                    Set<String> idsForThisCluster = new HashSet<>();
                    idsForThisCluster.add(uniqueIDForInstance);
                    clusterKeyIDMap.put(assignedCluster, idsForThisCluster);
                    verbInstances.put(head, clusterKeyIDMap);
                }
            }
        });
       lines.close();
        return verbInstances;
    }

    public static Map<String, Map<String, Set<String>>> readParseResultToHeadClusterArguments(String file) throws IOException {
        Map<String, Map<String, Set<String>>> headArgInstances = new HashMap<>();

        Stream<String> lines = Files.lines(Paths.get(file));
       lines.forEach((String line) -> {
            if (line.startsWith("#")) {
                String[] split = line.split(" ");
                String sentID = split[0];
                String headLocation = split[1];
                String uniqueIDForInstancePartial = sentID + "-" + headLocation;
                String head = split[2].split("\\.", 2)[0];
                Map<String, Set<String>> argInstances;
                if (headArgInstances.containsKey(head)) {
                    argInstances = headArgInstances.get(head);
                } else {
                    argInstances = new HashMap<>();
                    headArgInstances.put(head, argInstances);
                }
                //String assignedCluster = split[2].split("\\.")[1];
                // for arguments
                for (int i = 3; i < split.length; i++) {
                    String argumentBit = split[i];
                    String[] splitArg = argumentBit.split("-:-");
                    String argLocation = splitArg[1];
                    String assignedCluster = splitArg[2];
                    String uniqueIDForInstance = uniqueIDForInstancePartial + "-" + argLocation;
                    if (argInstances.containsKey(assignedCluster)) {
                        argInstances.get(assignedCluster).add(uniqueIDForInstance);
                    } else {
                        Set<String> idsForThisCluster = new HashSet<>();
                        idsForThisCluster.add(uniqueIDForInstance);
                        argInstances.put(assignedCluster, idsForThisCluster);
                    }
                }

            } else {
                System.err.println("Malformed input");
            }
        });
        lines.close();
        return headArgInstances;
    }

    /**
//     * Input is the parse file output is a map, containing clusters in the form of (cluster identfier, {sentID + "-" + headLocation+"-"+argLocation})
* That is argument instances that are assgined to this cluster
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Map<String, Set<String>> readParseResultToArgClusters(String file) throws IOException {
        Map<String, Set<String>> argInstances = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(file));
        lines.forEach((String line) -> {
            if (line.startsWith("#")) {
                String[] split = line.trim().split(" ");
                String sentID = split[0];
                String headLocation = split[1];
                String uniqueIDForInstancePartial = sentID + "-" + headLocation;
                //String head = split[2].split("\\.", 2)[0];
                //String assignedCluster = split[2].split("\\.")[1];
                // for arguments
                for (int i = 3; i < split.length; i++) {
                    String argumentBit = split[i];
                    String[] splitArg = argumentBit.split("-:-");
                    String argLocation = splitArg[1];
                    String assignedCluster = splitArg[2];
                    String uniqueIDForInstance = uniqueIDForInstancePartial + "-" + argLocation;
                    if (argInstances.containsKey(assignedCluster)) {
                        argInstances.get(assignedCluster).add(uniqueIDForInstance);
                    } else {
                        Set<String> idsForThisCluster = new HashSet<>();
                        idsForThisCluster.add(uniqueIDForInstance);
                        argInstances.put(assignedCluster, idsForThisCluster);
                    }
                }

            } else {
                System.err.println("Malformed input " + line);
            }
        });
        lines.close();
        return argInstances;
    }
    
    public static Map<String, Set<String>> readParseResultToFrameArgClusters(String file) throws IOException {
        Map<String, Set<String>> argInstances = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(file));
        lines.forEach((String line) -> {
            if (line.startsWith("#")) {
                String[] split = line.trim().split(" ");
                String sentID = split[0];
                String headLocation = split[1];
                String uniqueIDForInstancePartial = sentID + "-" + headLocation;
                //String head = split[2].split("\\.", 2)[0];
                String assignedHeadCluster = split[2].split("\\.")[1];
                // for arguments
                for (int i = 3; i < split.length; i++) {
                    String argumentBit = split[i];
                    String[] splitArg = argumentBit.split("-:-");
                    String argLocation = splitArg[1];
                    String assignedCluster = splitArg[2] +"-"+assignedHeadCluster;
                    String uniqueIDForInstance = uniqueIDForInstancePartial + "-" + argLocation;
                    if (argInstances.containsKey(assignedCluster)) {
                        argInstances.get(assignedCluster).add(uniqueIDForInstance);
                    } else {
                        Set<String> idsForThisCluster = new HashSet<>();
                        idsForThisCluster.add(uniqueIDForInstance);
                        argInstances.put(assignedCluster, idsForThisCluster);
                    }
                }

            } else {
                System.err.println("Malformed input " + line);
            }
        });
        lines.close();
        return argInstances;
    }

    public static Map<String, Set<String>> readParseResultToSenseArgClusters(String file) throws IOException {
        Map<String, Set<String>> argInstances = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(file));
        lines.forEach((String line) -> {
            if (line.startsWith("#")) {
                String[] split = line.split(" ");
                String sentID = split[0];
                String headLocation = split[1];
                String uniqueIDForInstancePartial = sentID + "-" + headLocation;
                String head = split[2].split("\\.", 2)[0];
                String assignedHeadCluster = split[2].split("\\.", 2)[1];
                // for arguments
                for (int i = 3; i < split.length; i++) {
                    String argumentBit = split[i];
                    String[] splitArg = argumentBit.split("-:-");
                    String argLocation = splitArg[1];
                    String assignedCluster = head + assignedHeadCluster + "-" + splitArg[2];
                    //String assignedCluster = head+"-"+ splitArg[2];
                    String uniqueIDForInstance = uniqueIDForInstancePartial + "-" + argLocation;
                    if (argInstances.containsKey(assignedCluster)) {
                        argInstances.get(assignedCluster).add(uniqueIDForInstance);
                    } else {
                        Set<String> idsForThisCluster = new HashSet<>();
                        idsForThisCluster.add(uniqueIDForInstance);
                        argInstances.put(assignedCluster, idsForThisCluster);
                    }
                }

            } else {
                System.err.println("Malformed input");
            }
        });
        lines.close();
        return argInstances;
    }

    /**
     * Input is the parse file output is a map, each key of the map is a lexical
     * head, the values are the clusters for this head in the input file the
     * structure of the inner map is as follows: keys are the cluster
     * identifiers, values are set of the instances (i.e., the exact location of
     * the instance)
     *
     * @param file
     * @param type
     * @return
     * @throws IOException
     */

//    public static void main(String[] args) throws IOException {
//        Map<String, Set<String>> gold = readParseResultToArgClusters("../final_sdp_data/effective_gold_frames.txt");
//        Map<String, Set<String>> syntaxBased = readParseResultToArgClusters("../final_sdp_data/effective_test_frames.txt");
//        Map<String, Set<String>> realData = readParseResultToArgClusters("../final_sdp_data/3-5-lnd-0-iteration-frames.txt");
//        for (String key : gold.keySet()) {
//            System.out.println(key + "\t" + gold.get(key).size());
//        }
//        System.out.println("-----");
//
//        for (String key : syntaxBased.keySet()) {
//            System.out.println(key + "\t" + syntaxBased.get(key).size());
//        }
//
//        System.out.println("filtering now ... ");
//
//        ClusterMapUtils.trimClusters(0, gold, realData);
//        ClusterMapUtils.trimClusters(0, gold, syntaxBased);
//        System.out.println("-----");
//        for (String key : gold.keySet()) {
//            System.out.println(key + "\t" + gold.get(key).size());
//        }
//        System.out.println("-----");
//
//        for (String key : syntaxBased.keySet()) {
//            System.out.println(key + "\t" + syntaxBased.get(key).size());
//        }
//
//    }

    /**
     * Make sure the same set of records are in the system submission and the gold data
     * @param inputGold
     * @param systemFile
     * @return
     * @throws IOException 
     */
    public static boolean checkIDs(String inputGold, String systemFile) throws IOException{
         Set<String> goldIDs = readFilesToIDSet(inputGold);
        Set<String> systemIDs = readFilesToIDSet(systemFile);

        if (systemIDs.size() > goldIDs.size() || systemIDs.size() < goldIDs.size()) {
            throw new RuntimeException("mismatch between the gold records and the submitted file, please fix the problem.");
        } else {
            goldIDs.removeAll(systemIDs);
            if (!goldIDs.isEmpty()) {
                throw new RuntimeException("mismatch between the gold records and the submitted file, please fix the problem.");
            }
        }
        return true;
    }
    
    public static boolean checkIDsForArguments(String inputGold, String systemFile) throws IOException{
        Set<String> goldIDs = readFilesToIDSet(inputGold);
        Set<String> systemIDs = readFilesToIDSet(systemFile);

        if (systemIDs.size() > goldIDs.size() || systemIDs.size() < goldIDs.size()) {
            throw new RuntimeException("mismatch between the gold records and the submitted file, please fix the problem.");
        } else {
            goldIDs.removeAll(systemIDs);
            if (!goldIDs.isEmpty()) {
                throw new RuntimeException("mismatch between the gold records and the submitted file, please fix the problem.");
            }
        }
        
        Set<String> goldArgs = readFilesToArgumentIDSet(inputGold);
        Set<String> sysArgs = readFilesToArgumentIDSet(systemFile);
        if (sysArgs.size() > goldArgs.size() || sysArgs.size() < goldArgs.size()) {
            throw new RuntimeException("mismatch between the number of arguments in the gold records and the submitted file, please fix the problem.");
        } else {
            goldArgs.removeAll(sysArgs);
            if (!goldIDs.isEmpty()) {
                throw new RuntimeException("mismatch between the arguments of gold records and the submitted file, please fix the problem.");
            }
        }
        
        return true;
    }
    /**
     * Read the files and make the ID Set
     * @param inputFile
     * @return
     * @throws IOException 
     */
    private static Set<String> readFilesToIDSet(String inputFile) throws IOException {
        Stream<String> lines = Files.lines(Paths.get(inputFile));
        Set<String> idSet = new HashSet<>();
        lines.forEach(line -> {
            String[] split = line.split(" ");
            if(split.length<3){
                   throw new RuntimeException("Malformed record in line: "+line+"\nthe minimum length is 3" );
            }
            String key = split[0] + split[1];
            if (!idSet.contains(key)) {
                idSet.add(key);
            } else {
                throw new RuntimeException("A duplicate key, i.e., " + key.replace("-", " ") + " is found in your file: " + inputFile + "\nPlease fix this problem");
            }
        });
        lines.close();
        return idSet;
    }

     private static Set<String> readFilesToArgumentIDSet(String inputFile) throws IOException {
        Stream<String> lines = Files.lines(Paths.get(inputFile));
        Set<String> idSet = new HashSet<>();
        lines.forEach(line -> {
            String[] split = line.split(" ");
             if(split.length<3){
                   throw new RuntimeException("Malformed record in line: "+line+"\nthe minimum length is 3" );
            }
            for (int i = 3; i < split.length; i++) {
                String position = split[i].split("-:-")[1];
                String keyMain = split[0] + split[1]+"-"+position;
                if (!idSet.contains(keyMain)) {
                    idSet.add(keyMain);
                } else {
                    throw new RuntimeException("A duplicate key-arg-position, i.e., " + keyMain.replace("-", " ") + " is found in your file: " + inputFile + "\nPlease fix this problem");
                }
            }
            
        });
        lines.close();
        return idSet;
    }
    public static void removeMostFrequentSet(Map<String, Set<String>> set) {
        int max = 0;
        String maxKey = null;
        for (String key : set.keySet()) {
            Set<String> get = set.get(key);
            if (get.size() > max) {
                maxKey = key;
                max = get.size();
            }
        }
        System.out.println("removed " + maxKey + " " + max);
        set.remove(maxKey);
    }

}
