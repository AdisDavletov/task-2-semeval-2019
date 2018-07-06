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
package semeval.oth.scripts;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;
import semeval.udtreebank.ParseFileUtil;
import semeval.udtreebank.Sentence;

/**
 *
 * @author Zadeh
 */
public class GenerateArgSyntacticBaseline {

    public static void main(String[] args) throws Exception {
        genSyntHeadBaselineForTask21();
        genSyntHeadBaselineForTask22();
    }

    public static void genSyntHeadBaselineForTask21() throws Exception {
        Map<String, Sentence> readSentencesToMap = ParseFileUtil.readSentencesToMap("../lr/semeval/ptb-conllu.txt");
        Stream<String> lines = Files.lines(Paths.get("../lr/semeval/trial-public/gold/task-2.1.txt"));
        PrintWriter pw = new PrintWriter(new FileWriter("../lr/semeval/trial-public/baseline-files/task-2.1.lexhead-gr.txt"));
        lines.forEach(line -> {
            String[] split = line.split(" ");
            Sentence sentence = readSentencesToMap.get(split[0]);

            int headOfFrmaePosition = Integer.parseInt(split[1]);

            Map<Integer, String> dependantToHead = sentence.getDependantToHead(headOfFrmaePosition);
            StringBuilder syntacticRecord = new StringBuilder();
            for (int i = 0; i < 2; i++) {
                String string = split[i];
                syntacticRecord.append(string).append(" ");
            }
            String[] headInfo = split[2].split("\\.");
            syntacticRecord.append(headInfo[0]).append(".").append(headInfo[0]).append(" ");
            for (int i = 3; i < split.length; i++) {
                String[] semanticDepInfo = split[i].split("-:-");
                int depPosition = Integer.parseInt(semanticDepInfo[1]);
                String syntacticType = "null";
                if (dependantToHead.containsKey(depPosition)) {
                    syntacticType = dependantToHead.get(depPosition);

                }
                syntacticRecord.append(semanticDepInfo[0]).append("-:-").append(depPosition).append("-:-").append(syntacticType).append(" ");
            }
            String syntaxBaselineRecord = syntacticRecord.toString().trim();
            pw.println(syntaxBaselineRecord);

        });
        pw.close();
    }

    public static void genSyntHeadBaselineForTask22() throws Exception {
        Map<String, Sentence> readSentencesToMap = ParseFileUtil.readSentencesToMap("../lr/semeval/ptb-conllu.txt");
        Stream<String> lines = Files.lines(Paths.get("../lr/semeval/trial-public/gold/task-2.2.txt"));
        PrintWriter pw = new PrintWriter(new FileWriter("../lr/semeval/trial-public/baseline-files/task-2.2.gr.txt"));
        lines.forEach(line -> {
            String[] split = line.split(" ");
            Sentence sentence = readSentencesToMap.get(split[0]);

            int headOfFrmaePosition = Integer.parseInt(split[1]);

            Map<Integer, String> dependantToHead = sentence.getDependantToHead(headOfFrmaePosition);
            StringBuilder syntacticRecord = new StringBuilder();
            for (int i = 0; i < 2; i++) {
                String string = split[i];
                syntacticRecord.append(string).append(" ");
            }
            String[] headInfo = split[2].split("\\.");
            syntacticRecord.append(headInfo[0]).append(".").append("NA").append(" ");
            for (int i = 3; i < split.length; i++) {
                String[] semanticDepInfo = split[i].split("-:-");
                int depPosition = Integer.parseInt(semanticDepInfo[1]);
                String syntacticType = "null";
                if (dependantToHead.containsKey(depPosition)) {
                    syntacticType = dependantToHead.get(depPosition);

                }
                syntacticRecord.append(semanticDepInfo[0]).append("-:-").append(depPosition).append("-:-").append(syntacticType).append(" ");
            }
            String syntaxBaselineRecord = syntacticRecord.toString().trim();
            pw.println(syntaxBaselineRecord);

        });
        pw.close();
    }
}
