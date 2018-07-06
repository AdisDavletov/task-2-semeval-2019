/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semeval.udtreebank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class Sentence {

    private String wsjPSDID;
    private List<TerminalToken> sentenceTerminals;
    
    public Sentence(List<TerminalToken> sentenceTerminals) {
        this.sentenceTerminals = new ArrayList<>();
        for (TerminalToken tt : sentenceTerminals) {
            if (tt != null) {
                this.sentenceTerminals.add(tt);
            }
        }

    }
    
    

     public Sentence(List<TerminalToken> sentenceTerminals, String id) {
        this.sentenceTerminals = new ArrayList<>();
        for (TerminalToken tt : sentenceTerminals) {
            if (tt != null) {
                this.sentenceTerminals.add(tt);
            }
        }
        this.wsjPSDID=id;

    }
     
    public Map<Integer, String> getDependantToHead(int targetHead) {
        Map<Integer, String> list = new HashMap<>();
        
        for (TerminalToken tt : sentenceTerminals) {
            int itsHead = tt.getItsHead();
            if (itsHead == targetHead) {
                int position = tt.getPosition();
                String type = tt.getDepType();
                if(list.containsKey(position)){
                    throw new RuntimeException("A single position can be only in gramatical realtionship to head, problem with " + targetHead);
                }
                list.put(position,type);
            }
        }
        return list;
    }
    public void setSentenceTerminals(List<TerminalToken> sentenceTerminals) {
        this.sentenceTerminals = sentenceTerminals;
    }

    public void setWsjPSDID(String wsjPSDID) {
        this.wsjPSDID = wsjPSDID;
    }

    public String getWsjPSDID() {
        return wsjPSDID;
    }

    public Sentence() {

        this.sentenceTerminals = new ArrayList<>();

        // depRel = new HashSet();
    }

    public void addNextTerminal(TerminalToken t) {
        sentenceTerminals.add(t);

        //depRel.add(t.getDepType());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.sentenceTerminals.size(); i++) {
            sb.append(sentenceTerminals.get(i).toString());
        }
        return sb.toString();
    }

    public String toStringAnnotated() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.sentenceTerminals.size(); i++) {
            sb.append(sentenceTerminals.get(i).toStringAnnotated()).append("\n");
        }
        return sb.toString();
    }

    public String toStringCoNLL() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.sentenceTerminals.size(); i++) {
            sb.append(sentenceTerminals.get(i).toStringConll()).append("\n");
        }
        return sb.toString();
    }

    public boolean isEmpty() {
        return sentenceTerminals.isEmpty();
    }

    public List<TerminalToken> getSentenceTerminals() {
        return sentenceTerminals;
    }

}
