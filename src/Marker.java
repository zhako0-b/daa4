import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {
    private static final long BASE = 257;
    private static final long MOD = 1_000_000_007;

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int[] pi = new int[p.length()];
        for (int i = 1; i < p.length(); i++) {
            pi[i] = advance(p.charAt(i), p, pi, pi[i - 1]);
        }
        return pi;
    }

    private static int advance(char letter, String p, int[] pi, int matched) {
        while (matched > 0 && letter != p.charAt(matched)) {
            matched = pi[matched - 1];
        }
        return letter == p.charAt(matched) ? matched + 1 : matched;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        if (p.isEmpty() || p.length() > text.length()) {
            return new int[0];
        }
        int[] pi = prefixFunction(p);
        List<Integer> hits = new ArrayList<>();
        int matched = 0;
        for (int i = 0; i < text.length(); i++) {
            matched = advance(text.charAt(i), p, pi, matched);
            if (matched == p.length()) {
                hits.add(i - p.length() + 1);
                matched = pi[matched - 1];
            }
        }
        int[] positions = new int[hits.size()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = hits.get(i);
        }
        return positions;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k <= 0 || k > a.length() || k > b.length()) {
            return 0;
        }
        long leadingPower = 1;
        for (int i = 1; i < k; i++) {
            leadingPower = leadingPower * BASE % MOD;
        }
        Map<Long, List<Integer>> windows = new HashMap<>();
        long hash = initialHash(b, k);
        for (int i = 0; i <= b.length() - k; i++) {
            if (i > 0) {
                hash = roll(hash, b.charAt(i - 1), b.charAt(i + k - 1), leadingPower);
            }
            windows.computeIfAbsent(hash, key -> new ArrayList<>()).add(i);
        }
        int count = 0;
        hash = initialHash(a, k);
        for (int i = 0; i <= a.length() - k; i++) {
            if (i > 0) {
                hash = roll(hash, a.charAt(i - 1), a.charAt(i + k - 1), leadingPower);
            }
            for (int start : windows.getOrDefault(hash, Collections.emptyList())) {
                if (sameWord(a, i, b, start, k)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private static long initialHash(String s, int k) {
        long hash = 0;
        for (int i = 0; i < k; i++) {
            hash = (hash * BASE + s.charAt(i)) % MOD;
        }
        return hash;
    }

    private static long roll(long hash, char outgoing, char incoming, long leadingPower) {
        long remaining = (hash - outgoing * leadingPower % MOD + MOD) % MOD;
        return (remaining * BASE + incoming) % MOD;
    }

    private static boolean sameWord(String a, int i, String b, int j, int k) {
        for (int offset = 0; offset < k; offset++) {
            if (a.charAt(i + offset) != b.charAt(j + offset)) {
                return false;
            }
        }
        return true;
    }
}
