package ge.shitbot.bot.tools;

import java.util.ArrayList;

/// <summary>
/// This class implements String comparison algorithm
/// based on character pair similarity
/// Source: http://www.catalysoft.com/articles/StrikeAMatch.html
/// </summary>
public class SimilarityTool
{
    /// <summary>
    /// Compares the two Strings based on letter pair matches
    /// </summary>
    /// <param name="str1"></param>
    /// <param name="str2"></param>
    /// <returns>The percentage match from 0.0 to 1.0 where 1.0 is 100%</returns>
    public static double compareStrings(String str1, String str2)
    {
        ArrayList pairs1 = wordLetterPairs(str1.toUpperCase());
        ArrayList pairs2 = wordLetterPairs(str2.toUpperCase());

        int intersection = 0;
        int union = pairs1.size() + pairs2.size();

        for (int i=0; i<pairs1.size(); i++) {
            Object pair1=pairs1.get(i);
            for(int j=0; j<pairs2.size(); j++) {
                Object pair2=pairs2.get(j);
                if (pair1.equals(pair2)) {
                    intersection++;
                    pairs2.remove(j);
                    break;
                }
            }
        }

        return (2.0 * intersection) / union;
    }

    private static ArrayList wordLetterPairs(String str) {
        ArrayList allPairs = new ArrayList();
        // Tokenize the string and put the tokens/words into an array
        String[] words = str.split("\\s");
        // For each word
        for (int w=0; w < words.length; w++) {
            // Find the pairs of characters
            String[] pairsInWord = letterPairs(words[w]);
            for (int p=0; p < pairsInWord.length; p++) {
                allPairs.add(pairsInWord[p]);
            }
        }

        return allPairs;
    }

    private static String[] letterPairs(String str) {
        int numPairs = str.length()-1;
        String[] pairs = new String[numPairs];
        for (int i=0; i<numPairs; i++) {
            pairs[i] = str.substring(i,i+2);
        }

        return pairs;
    }

    public static void main(String[] args) {
        Double similarity = SimilarityTool.compareStrings("სეგუნდა B. ჯგუფი 1","სეგუნდა B ჯგუფი I");

        System.out.println("Similarity: " + similarity);
    }
}