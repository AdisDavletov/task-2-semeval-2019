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

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2015
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.Iterator;

//import de.lmu.ifi.dbs.elki.data.Cluster;
//import de.lmu.ifi.dbs.elki.data.Clustering;
//import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
//import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
//import de.lmu.ifi.dbs.elki.math.MeanVariance;
//import de.lmu.ifi.dbs.elki.utilities.BitsUtil;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class storing the contingency table and related data on two clusterings.
 *
 * @author Erich Schubert
 * @since 0.5.0
 *
 * @apiviz.landmark
 *
 * @apiviz.uses Clustering
 * @apiviz.composedOf PairCounting
 * @apiviz.composedOf Entropy
 * @apiviz.composedOf EditDistance
 * @apiviz.composedOf BCubed
 * @apiviz.composedOf SetMatchingPurity
 */
public class ClusterContingencyTable {

    /**
     * Noise cluster handling
     */
    protected boolean breakNoiseClusters = false;

    /**
     * Self pairing
     */
    protected boolean selfPairing = true;

    /**
     * Number of clusters.
     */
    protected int size1 = -1, size2 = -1;

    /**
     * Contingency matrix
     */
    protected double[][] contingency = null;
     protected String[][] contingencyStringLabel = null;

    /**
     * Noise flags
     */
    protected long[] noise1 = null, noise2 = null;

    /**
     * Pair counting measures
     */
    protected PairCounting paircount = null;

    /**
     * Entropy-based measures
     */
    protected Entropy entropy = null;

    /**
     * Set matching purity measures
     */
    protected SetMatchingPurity smp = null;

    /**
     * Edit-Distance measures
     */
    protected EditDistance edit = null;

    /**
     * BCubed measures
     */
    protected BCubed bcubed = null;

    /**
     * Constructor.
     *
     * @param selfPairing Build self-pairs
     * @param breakNoiseClusters Break noise clusters into individual objects
     */
    public ClusterContingencyTable(boolean selfPairing, boolean breakNoiseClusters) {
        super();
        this.selfPairing = selfPairing;
        this.breakNoiseClusters = breakNoiseClusters;
    }

    public ClusterContingencyTable() {
        //selfPairing = false;
    }

    /**
     * Process two clustering results, the method is adapted from the original
     * ELKI process method. However, it is simplified so that each cluster can
     * be presented as a set and each instance can be denoted by a string.
     *
     * @param cs1
     * @param cs2
     */
    public void processWithLabels(final Collection<Set<String>> cs1, final Collection<Set<String>> cs2) {
        size1 = cs1.size();
        size2 = cs2.size();
        contingency = new double[size1 + 2][size2 + 2];
        contingencyStringLabel = new String[size1 + 2][size2 + 2];
        // Fill main part of matrix
   
        final Iterator<Set<String>> itx = cs2.iterator();
        for (int i2 = 0; itx.hasNext(); i2++) {
            final Set<String> c2 = itx.next();
            contingency[size1 + 1][i2] = c2.size();
            contingencyStringLabel[size1 + 1][i2] =  "the label set in cluster 2 ";//c2.size();
            contingency[size1 + 1][size2] += c2.size();
            contingencyStringLabel[size1 + 1][i2] +=  " the label set in cluster 2";//c2.size();
        }


        Iterator<Set<String>> it1 = cs1.iterator();
        for (int i1 = 0; it1.hasNext(); i1++) {
            final Set<String> c1 = it1.next();
//      final DBIDs ids = DBIDUtil.ensureSet(c1.getIDs());
            contingency[i1][size2 + 1] = c1.size();
            contingency[size1][size2 + 1] += c1.size();

            Iterator<Set<String>> it2 = cs2.iterator();
            for (int i2 = 0; it2.hasNext(); i2++) {
                final Set<String> c2 = it2.next();
                int count = intersectionSize(c1, c2);
                contingency[i1][i2] = count;
                contingency[i1][size2] += count;
                contingency[size1][i2] += count;
                contingency[size1][size2] += count;
            }
        }
    }


      public void process(final Collection<Set<String>> cs1, final Collection<Set<String>> cs2) {
        size1 = cs1.size();
        size2 = cs2.size();
        contingency = new double[size1 + 2][size2 + 2];
       
        // Fill main part of matrix
   
        final Iterator<Set<String>> itx = cs2.iterator();
        for (int i2 = 0; itx.hasNext(); i2++) {
            final Set<String> c2 = itx.next();
            contingency[size1 + 1][i2] = c2.size();
         
            contingency[size1 + 1][size2] += c2.size();
         
        }


        Iterator<Set<String>> it1 = cs1.iterator();
        for (int i1 = 0; it1.hasNext(); i1++) {
            final Set<String> c1 = it1.next();
//      final DBIDs ids = DBIDUtil.ensureSet(c1.getIDs());
            contingency[i1][size2 + 1] = c1.size();
            contingency[size1][size2 + 1] += c1.size();

            Iterator<Set<String>> it2 = cs2.iterator();
            for (int i2 = 0; it2.hasNext(); i2++) {
                final Set<String> c2 = it2.next();
                int count = intersectionSize(c1, c2);
                contingency[i1][i2] = count;
                contingency[i1][size2] += count;
                contingency[size1][i2] += count;
                contingency[size1][size2] += count;
            }
        }
    }


    private int intersectionSize(Set<String> c1, Set<String> c2) {
       
       // Long collect = c1.parallelStream().filter(c2::contains).parallel().collect(Collectors.counting());
        int countIntersect = 0;
       

        for (String id : c2) {
            if (c1.contains(id)) {
              
                countIntersect++;
            }
        }
             
        
        return countIntersect;// 
        
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (contingency != null) {
            for (int i1 = 0; i1 < size1 + 2; i1++) {
                if (i1 >= size1) {
                    buf.append("------\n");
                }
                for (int i2 = 0; i2 < size2 + 2; i2++) {
                    if (i2 >= size2) {
                        buf.append("| ");
                    }
                    buf.append(contingency[i1][i2]).append(' ');
                }
                buf.append('\n');
            }
        }
        return buf.toString();
    }

    /**
     * Get (compute) the pair counting measures.
     *
     * @return Pair counting measures
     */
    public PairCounting getPaircount() {
        if (paircount == null) {
            paircount = new PairCounting(this);
        }
        return paircount;
    }

    /**
     * Get (compute) the entropy based measures
     *
     * @return Entropy based measures
     */
    public Entropy getEntropy() {
        if (entropy == null) {
            entropy = new Entropy(this);
        }
        return entropy;
    }

    /**
     * Get (compute) the edit-distance based measures
     *
     * @return Edit-distance based measures
     */
    public EditDistance getEdit() {
        if (edit == null) {
            edit = new EditDistance(this);
        }
        return edit;
    }

    /**
     * The BCubed based measures
     *
     * @return BCubed measures
     */
    public BCubed getBCubed() {
        if (bcubed == null) {
            bcubed = new BCubed(this);
        }
        return bcubed;
    }

    /**
     * The set-matching measures
     *
     * @return Set-Matching measures
     */
    public SetMatchingPurity getSetMatching() {
        if (smp == null) {
            smp = new SetMatchingPurity(this);
        }
        return smp;
    }

    /**
     * Compute the average Gini for each cluster (in both clusterings -
     * symmetric).
     *
     * @return Mean and variance of Gini
     */
    public MeanVariance averageSymmetricGini() {
        MeanVariance mv = new MeanVariance();
        for (int i1 = 0; i1 < size1; i1++) {
            double purity = 0.0;
            if (contingency[i1][size2] > 0) {
                final double cs = contingency[i1][size2]; // sum, as double.
                for (int i2 = 0; i2 < size2; i2++) {
                    double rel = contingency[i1][i2] / cs;
                    purity += rel * rel;
                }
                mv.put(purity, cs);
            }
        }
        for (int i2 = 0; i2 < size2; i2++) {
            double purity = 0.0;
            if (contingency[size1][i2] > 0) {
                final double cs = contingency[size1][i2]; // sum, as double.
                for (int i1 = 0; i1 < size1; i1++) {
                    double rel = contingency[i1][i2] / cs;
                    purity += rel * rel;
                }
                mv.put(purity, cs);
            }
        }
        return mv;
    }

    /**
     * Utility class.
     *
     * @author Erich Schubert
     *
     * @apiviz.exclude
     */
    public static final class Util {

        /**
         * F-Measure
         *
         * @param precision Precision
         * @param recall Recall
         * @param beta Beta value
         * @return F-Measure
         */
        public static double fMeasure(double precision, double recall, double beta) {
            final double beta2 = beta * beta;
            return (1 + beta2) * precision * recall / (beta2 * precision + recall);
        }

        /**
         * F1-Measure (F-Measure with beta = 1)
         *
         * @param precision Precision
         * @param recall Recall
         * @return F-Measure
         */
        public static double f1Measure(double precision, double recall) {
            return 2 * precision * recall / (precision + recall);
        }
    }
}
