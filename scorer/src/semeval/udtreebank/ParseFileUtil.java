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
package semeval.udtreebank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Zadeh
 */
public class ParseFileUtil {
    public static void main(String[] args) throws IOException, Exception {
        String file = "../lr/semeval/ptb-conllu.txt";
        Map<String, Sentence> readSentencesToMap = readSentencesToMap(file);
        Stream<String> lines = Files.lines(Paths.get("../lr/semeval/trial-public/gold/task-1.txt"));
        lines.forEach(line->{
            String[] split = line.split(" ");
            Sentence sentence = readSentencesToMap.get(split[0]);
            System.err.println(sentence.getWsjPSDID());
            System.err.println(sentence.toStringCoNLL());
            //System.err.println("");
        });
        
    }
    
    
    public static  Map<String,Sentence> readSentencesToMap(String file) throws IOException, Exception{
        ParsedFileReader pfr = new ParsedFileReader(file);
        Sentence nextSentence =null;
        Map<String,Sentence> mapSentenceIDToSentence= new HashMap<>();
        while((nextSentence = pfr.getNextSentence())!=null){
            mapSentenceIDToSentence.put(nextSentence.getWsjPSDID(),nextSentence);
        };
        return mapSentenceIDToSentence;
    }
   
}
