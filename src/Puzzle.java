import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Puzzle {
    
    /**
     * 15 Puzzle Solver
     * 
     * @param args File to read initial puzzle states from
     */
    public static void main(String args[]) {
        
        // Initial states of puzzles to solve
        List<State> initials = new ArrayList<>();
        
        if (args.length < 1) {
            System.out.println("Error: no input file given");
            System.exit(1);
        }
        
        // Read input from file given on command line
        Scanner s = null;
        try {
            s = new Scanner(new FileInputStream(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't open input file '" + args[0] + "'");
            System.exit(1);
        }

        while (s.hasNextLine()) {
            String line = s.nextLine();
            
            // Skip blank lines
            if (line.isEmpty())
                continue;
            
            Scanner ss = new Scanner(line);
            byte[][] board = new byte[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (ss.hasNextInt()) {
                        board[i][j] = (byte) ss.nextInt();
                    } else {
                        System.out.println("Invalid input file");
                        System.exit(1);
                    }
                }
            }
            ss.close();
            
            initials.add(new State(board));
        }
        s.close();
        
        // Goal state
        State goal = new State(new byte[][] {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}});
        Instant start,end;
        
        // Run solver on each test case
        for (State initial : initials) {
            System.out.println("Running bidirectional weighted A*\n--------------------------");
            start = Instant.now();
            Node[] solution1 = BidiAStarSearch.biDirectionalSolve(initial, goal);
            end = Instant.now();
            if (solution1 == null) {
                System.out.println("No solution Found!");
            } else {
                // Output path
                System.out.println("Path Length: " + (solution1[0].getDepth() + solution1[1].getDepth()));
                System.out.println("Run time: " + Duration.between(start, end));
              //  System.out.print(solution1[0].pathToString());
              //  System.out.print(solution1[1].revPathToStringSkipFirst() + "\n\n");
            }

            System.out.print("\n");
            
            System.out.println("Running MM\n----------");
            start = Instant.now();
            Node[] solution2 = MMsearch.MMSolve(initial, goal);
            end = Instant.now();
            if (solution2 == null) {
                System.out.println("No solution Found!");
            } else {
            	System.out.println("Run time: " + Duration.between(start, end));
            }
            
            System.out.println("\n==========================");

        }
    }
    
}
