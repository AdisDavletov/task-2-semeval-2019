/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semeval.udtreebank;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ParsedFileReader {

    private BufferedReader br;
    private String nextLine;
    private boolean hasNext;

    public ParsedFileReader(String file) throws FileNotFoundException, IOException {
        br = new BufferedReader(new FileReader(file));
        hasNext = true;
//       String l;
//        if ((l=br.readLine()) != null) {
//            hasNext = true;
//            System.err.println("Reading " + l);
//        } else {
//            hasNext = false;
//        }
    }

    public Sentence getNextSentence() throws IOException, Exception {
        if (!hasNext) {
            return null;
        }
        String line;
        if ((line = br.readLine()) != null) {
            Sentence sentence = new Sentence();
            if (line.startsWith("#")) {
                sentence.setWsjPSDID(line.trim());
            } else {
                throw new Exception("Expected sentenceID");
            }
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    hasNext = true;
                    return sentence;
                    //break;
                } else {
                    sentence.addNextTerminal(TerminalToken.fromLine(line));
                }

            }
            return sentence;

        } else {

            br.close();
            hasNext = false;
            return null;
        }

    }

    public void close() throws IOException {
        this.br.close();
    }

    @Override
    protected void finalize() throws Throwable {
        br.close();
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }

}
