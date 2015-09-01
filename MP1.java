import java.io.File;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];
       
        //TODO

        // Get the raw data input from the input file. Catch some exceptions if they turn up.
        List<String> raw_input = null;
        try {
            raw_input = Files.readAllLines(Paths.get(this.inputFileName), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Pull the indicies we are supposed to deal with based on the magical math done by getIndexes(). I guess we can catch an exception if it happens to throw one.
        Integer[] user_indicies = null;
        try {
            user_indicies = this.getIndexes();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Now we can tokenize based on the delimiters given.
        List<String> tokenized = new ArrayList<String>();
        for (Integer index : user_indicies) {
            StringTokenizer st = new StringTokenizer(raw_input.get(index), this.delimiters);
            while (st.hasMoreElements()) {
                tokenized.add((String)st.nextElement());
            }
        }

        // Remove extra whitespace and drop everything to lowercase.
        List<String> cleaned = new ArrayList<String>();
        for (String token : tokenized) {
            cleaned.add(token.replaceAll("\\s+","").toLowerCase());
        }
        //System.out.println("Cleaned length: "+String.valueOf(cleaned.size()));
        
        // Remove the stop words. I know it is pointless to make the new variable name without a deep copy but whatever...
        List<String> only_go_words = cleaned;
        for (String stop_word : stopWordsArray) {
            only_go_words.removeAll(Collections.singleton(stop_word));
        }
        //System.out.println("Only Go Words length: "+String.valueOf(only_go_words.size()));

        // Count the frequency of words and drop that junk into a map <String, Integer>
        Set<String> unique_words = new HashSet<String>(only_go_words);
        Map<String,Integer> word_count_map = new HashMap<String,Integer>();
        for (String word : unique_words) {
            int count = Collections.frequency(only_go_words, word);
            word_count_map.put(word, count);
        }

        // Now time to sort this stuff. Compare the counts first and if counts are equal then compare the strings. Fun.
        List<Map.Entry<String, Integer>> word_count_list = new LinkedList<>(word_count_map.entrySet());
        Collections.sort( word_count_list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
                
                if (o2.getValue() == o1.getValue()) {
                    return (o1.getKey()).compareTo(o2.getKey());
                }
                else {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            }
        } );

        // Put the top 20 entries into the return array. Cross Fingers.
        for(int i=0; i<20; i++) {
            ret[i] = word_count_list.get(i).getKey();
        }

        // This is here for making sure I don't screw up along the way. Yay.
        //for (String item : only_go_words) {
        //    System.out.println(item);
        //}

        return ret;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
