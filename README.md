# Assignment 4. Patient Zero

Student ID — 251941  
Name — Orynbassar Zharkynbek 
Group — SE-2532

## Run

Use JDK 17 or newer from the project directory.

```sh
java tools/MakeVariant.java 251941 data
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
java tools/Check.java
java -cp out Bench
```

The four files in `data` were generated for Student ID 251941. Run `Main` again after `Check.java` because the checker rewrites the HTML with example data.

## Program output

Output for Student ID 251941.

```text
Mutations: 71
Shared core: 923
Marker hits: 5
First hit: 9790
Border length: 4
Shared words: 431
Tests needed: 35
Cheapest testing: 142
Longest chain: 23
```

The checker passed all nine outputs on each of `tiny`, `small` and `dense`. The lab reference also agreed with all nine outputs for this variant, and the selected people covered every infection link. After running your variant, open `output/outbreak.html` and save a screenshot as `output/outbreak.png`.

## Questions

1. **Edit distance.** The cell `d[i][j]` gives the minimum edits from the first `i` letters of A to the first `j` letters of B. Take the minimum of deletion from above, insertion from the left, and a match or replacement from the diagonal.
2. **LCS.** A subsequence can skip letters as long as the remaining letters keep their order. A substring has to stay contiguous. Insertions can shift a shared letter to a different index, so a comparison at the same index can miss letters that LCS includes.
3. **KMP.** Each prefix-function value gives the length of the longest proper prefix that also ends the current part of the pattern. On a mismatch KMP shortens the matched prefix while keeping the current text position. The marker `TAGCTAATAGC` has `TAGC` as its longest proper prefix that is also a suffix, so its border length is 4.
4. **Rabin–Karp.** Different words can produce the same hash, so each candidate match is checked letter by letter. If many windows have the same hash and fail verification, each window in A can end up comparing against many windows in B.
5. **Tree DP.** If a person is tested, each child can use its cheaper state. If a person is skipped, every child must be tested to cover the links. A high-degree person might cost 10 while three leaves each cost 1, so picking the person with most links can cost more than testing all three leaves.

## AI use

ChatGPT drafted the Java implementations and these explanations, ran the starter checker and benchmark, and prepared the local Git history. Before submitting, revise the draft and put the answers in your own words as required by the assignment.

## Timing

OpenJDK 21, milliseconds, average of three runs from the supplied `Bench`.

| Test | Sizes | Time (ms) |
|---|---|---|
| Naive search | 20,000 / 40,000 / 80,000 | 38.0 / 90.5 / 173.5 |
| KMP | 20,000 / 40,000 / 80,000 | 2.6 / 1.4 / 1.2 |
| Edit distance | 500 / 1,000 / 2,000 / 4,000 | 11.4 / 25.9 / 26.7 / 98.1 |

Doubling text size made naive search about 2.4× and 1.9× slower. The short KMP timings decreased in this run, so these measurements are too noisy to show its linear growth. Edit distance took about 3.7× as long from 2,000 to 4,000, close to the fourfold growth expected when both strings double in length.

## Complexity

For the strain algorithms, `n` and `m` are the lengths of A and B. For KMP they are the text and pattern lengths. Let `h` be the number of hits, `k` the window length, `c` the number of candidate comparisons and `v` the number of people.

| Algorithm | Time | Extra space | Notes |
|---|---|---|---|
| Edit distance | O((n+1)(m+1)) | O((n+1)(m+1)) | Full DP table |
| LCS | O((n+1)(m+1)) | O((n+1)(m+1)) | Full DP table |
| Prefix function | O(m) | O(m) | Prefix table |
| KMP | O(n+m) | O(m+h) | Includes saved hit positions |
| Rabin–Karp | O(n+m+ck) with average constant-time map operations | O(m) | Worst case O(nmk) if many candidates require long checks |
| Tree DP and reconstruction | O(v) | O(v) | Two states per person and DFS stack |
| Longest chain | O(v) | O(v) | DFS stack can reach v on a path |
