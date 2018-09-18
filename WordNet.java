import java.util.ArrayList;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.Queue;

// change IN change SET

public class WordNet {
    
    private final SET<Noun> nounSET;
    private final SAP sap;
    private final ArrayList<String> idList;
    
        //  create a class to store the noun and corresponded id numbers;
    private class Noun implements Comparable<Noun> {
        private final String noun;
        private final ArrayList<Integer> id = new ArrayList<Integer>();

        public Noun(String noun) {
            this.noun = noun;
        }

        @Override
        public int compareTo(Noun that) {
            return this.noun.compareTo(that.noun);
        }
        public ArrayList<Integer> getId() {
            return this.id;
        }
        public void addId(int x) {
            id.add(x);
        }
    }
    
        // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        In inSynsets = new In(synsets);
        In inHypernyms = new In(hypernyms);

        // counting the total number of vertex;
        int maxVertex = 0;
        idList = new ArrayList<>();
        nounSET = new SET<>();

        // start to read synsets.txt
        String line = inSynsets.readLine();

        while (line != null) {
            maxVertex++;
            String[] synsetLine = line.split(",");
            
            // synsetLine[0] is id
            int id = Integer.parseInt(synsetLine[0]);
            
            // synsetLine[1] is noun, which are splited by blank space
            String[] nounSet = synsetLine[1].split(" ");

            for (String nounName : nounSet) {
                Noun n = new Noun(nounName);
                
                if (nounSET.contains(n)) {
                    n = nounSET.ceiling(n);
                    n.addId(id);
                } else {
                    n.addId(id);
                    nounSET.add(n);
                }
            }
            idList.add(synsetLine[1]);
            // continue reading next synsets
            line = inSynsets.readLine();
        }
        
        Digraph G = new Digraph(maxVertex);
        // the candidate root
        boolean[] isNotRoot = new boolean[maxVertex];
        // start to read hypernyms.txt
        line = inHypernyms.readLine();
        
        while (line != null) {
            String[] hypernymsLine = line.split(",");
            
            // hypernymsLine[0] is id
            int v = Integer.parseInt(hypernymsLine[0]);
            isNotRoot[v] = true;
            
            // hypernymsLine[i], i > 0 are v's ancestor
            for (int i = 1; i < hypernymsLine.length; i++) {
                G.addEdge(v, Integer.parseInt(hypernymsLine[i]));
            }
            line = inHypernyms.readLine();
        }
        
        // test fot root: no cycle
        DirectedCycle directedCycle = new DirectedCycle(G);
        if (directedCycle.hasCycle()) {
            throw new java.lang.IllegalArgumentException();
        }
        
        // test for root: no more than one candidate root
        int rootCount = 0;
        for (boolean notRoot : isNotRoot) {
            if (!notRoot) {
                rootCount++;
            }
        }
        if (rootCount > 1) {
            throw new java.lang.IllegalArgumentException();
        }
        
        sap = new SAP(G);
    }
    
    
        // returns all WordNet nouns
    public Iterable<String> nouns() {
        Queue<String> nouns = new Queue<>();
        for (Noun noun : nounSET) {
            nouns.enqueue(noun.noun);
        }
        return nouns;
    }

        // is the word a WordNet noun?
    public boolean isNoun(String word) {
        Noun noun = new Noun(word);
        return nounSET.contains(noun);
    }

        // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA)) {
            throw new java.lang.IllegalArgumentException();
        }
        if (!isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }
        Noun nodeA = nounSET.ceiling(new Noun(nounA));
        Noun nodeB = nounSET.ceiling(new Noun(nounB));
        return sap.length(nodeA.getId(), nodeB.getId());
    }

        // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
        // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA)) {
            throw new java.lang.IllegalArgumentException();
        }
        if (!isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }
        Noun nodeA = nounSET.ceiling(new Noun(nounA));
        Noun nodeB = nounSET.ceiling(new Noun(nounB));
        return idList.get(sap.ancestor(nodeA.getId(), nodeB.getId()));
    }

        // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println(wordnet.distance("1760s", "1790s"));
    }
}
