import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AtePuzzle {
    private static Scanner kb;
    private static Random rand;
    private static final byte[] goal = {0,1,2,3,4,5,6,7,8};
    private static final int goalHash = Arrays.hashCode(goal);

    //Tile moves, used shifted bytes
    private static final byte up	= 1<<0;
    private static final byte down	= 1<<1;
    private static final byte left	= 1<<2;
    private static final byte right	= 1<<3;

    //Possible tile moves on where the tile is at on the board. possibleMoves[0] = tile is at top left corner and first postition
    private static byte[] possibleMoves = {  down|right,		left|down|right,	left|down,
            up|down|right,	left|up|down|right, left|up|down,
            up|right,	left|up|right,	left|up };


    //Look up table for the manhattan heuristic implementation
    private static byte[][] manhattanLookup = { {0, 1, 2, 1, 2, 3, 2, 3, 4}, {1, 0, 1, 2, 1, 2, 3, 2, 3},
            {2, 1, 0, 3, 2, 1, 4, 3, 2}, {1, 2, 3, 0, 1, 2, 1, 2, 3},
            {2, 1, 2, 1, 0, 1, 2, 1, 2}, {3, 2, 1, 2, 1, 0, 3, 2, 1},
            {2, 3, 4, 1, 2, 3, 0, 1, 2}, {3, 2, 3, 2, 1, 2, 1, 0, 1},
            {4, 3, 2, 3, 2, 1, 2, 1, 0}
    };

    public static void main(String[] args) {
        int choice;
        kb = new Scanner(System.in);
        rand = new Random();

        do{
            System.out.println("\nEnter a choice");
            System.out.println("1) Randomly create puzzles");
            System.out.println("2) Enter a puzzle manually");
            System.out.println("3) Read in test case files");
            System.out.println("4) Exit the program");
            System.out.print("\nYour choice: ");
            choice = kb.nextInt();

            switch (choice) {
                case 1:
                    createRandomPuzzles();
                    break;
                case 2:
                    userEntersPuzzle();
                    break;
                case 3:
                    readInPuzzles();
                    break;
                case 4:
                    choice = 4;
                    break;
                default: System.out.println("Nothing valid was entered. Try again.");
            }
        } while (choice != 4);
    }


    //Misplaced heuristic, doesnt take into consideration the distance in terms of correctness
    private static Heuristic<byte[]> misplacedHeuristic = (board) -> {
        int misplacedTiles = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) { continue; }
            if (board[i] != goal[i]) { misplacedTiles++; }
        }
        return misplacedTiles;
    };


    //Manhattan heuristic, sums up distances
    private static Heuristic<byte[]> distanceHeuristic = (board) -> {
        int totalDistance = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) { continue; }

            totalDistance += manhattanLookup[i][board[i]];
        }
        return totalDistance;
    };


    //Reads in files with 200 test cases at a specified depth, prints piece of total table in report
    private static void readInPuzzles(){
        kb = new Scanner(System.in);
        String line;

        puzzleResult h2;
        puzzleResult h1;
        byte[] board = new byte[goal.length];

        double avgSearchCostH1 = 0;
        double avgSearchCostH2 = 0;
        double avgRunTimeH1 = 0;
        double avgRunTimeH2 = 0;

        System.out.print("\nTest cases at what depth (even upto 20): ");
        String which = kb.nextLine();
        String whichFile = which + ".txt";


        try{
            FileReader fr = new FileReader(whichFile);
            BufferedReader br = new BufferedReader(fr);
            int count = 0;

            while (count < 200) {
                if ((line = br.readLine()) != null) {
                    for (int i = 0; i < line.length(); i++) {
                        board[i] = (byte) Character.getNumericValue(line.charAt(i));
                    }

                    h2 = search(board, distanceHeuristic);
                    h1 = search(board, misplacedHeuristic);

                    avgSearchCostH1 += (double)h1.expandedNodes;
                    avgSearchCostH2 += (double)h2.expandedNodes;
                    avgRunTimeH1 += (double)(h1.timeFinished - h1.timeStarted);
                    avgRunTimeH2 += (double)(h2.timeFinished - h2.timeStarted);

                    count++;
                }
            }


        }
        catch(FileNotFoundException ex){
            System.out.println("lol nothing here");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("\n%3s %10s %10s %18s %18s", " d  |", "   A*(h1) SC |   ", "A*(h2) SC |   ", "A*(h1) RunTime (ms) |   ", "A*(h1) RunTime (ms) ");
        System.out.printf("\n%-3s %12s %15s %18s %22s\n", which, (avgSearchCostH1 / 200), (avgSearchCostH2 / 200), (avgRunTimeH1 / 200), (avgRunTimeH2 / 200));
    }


    //Creates random puzzles and averages them all at the end
    private static void createRandomPuzzles() {
        byte[] board;
        double avgDepth = 0;
        double avgSearchCostH1 = 0;
        double avgSearchCostH2 = 0;
        double avgRunTimeH1 = 0;
        double avgRunTimeH2 = 0;

        System.out.print("\nNumber of random puzzles to create: ");
        int howMany = kb.nextInt();
        System.out.printf("%-10s %-8s %-15s %-15s %-15s \n", "d | ", "A*(h1) SC |   ", "A*(h2) SC |   ", "A*(h1) RunTime (ms) |   ", "A*(h1) RunTime (ms) ");

        for (int i = 0; i < howMany; i++) {
            do { board = generateRandomPuzzle(); } while (!isSolvable(board));

            puzzleResult h1 = search(board, misplacedHeuristic);
            puzzleResult h2 = search(board, distanceHeuristic);

            // System.out.println(h1.resultDepth + " | " + h2.resultDepth);

            avgDepth += (double)h1.resultDepth;
            avgSearchCostH1 += (double)h1.expandedNodes;
            avgSearchCostH2 += (double)h2.expandedNodes;
            avgRunTimeH1 += (double)(h1.timeFinished - h1.timeStarted);
            avgRunTimeH2 += (double)(h2.timeFinished - h2.timeStarted);

            System.out.printf("%3d %14s %16s %21s %25s%n", h2.resultDepth, h1.expandedNodes, h2.expandedNodes, (h1.timeFinished - h1.timeStarted), (h2.timeFinished - h2.timeStarted));

        }
        System.out.println("\nAverage depth of all puzzles: " + (avgDepth / (double)howMany));
        System.out.println("Average search cost of Misplaced heuristic: " + (avgSearchCostH1 / (double)howMany));
        System.out.println("Average search cost of Distance heuristic: " + (avgSearchCostH2 / (double)howMany));
        System.out.println("Average run time of Misplaced heuristic: " + (avgRunTimeH1 / (double)howMany));
        System.out.println("Average run time of Distance heuristic: " + (avgRunTimeH2 / (double)howMany));
    }


    private static byte[] generateRandomPuzzle() {
        int randomPosition;
        byte[] board = Arrays.copyOf(goal, goal.length);

        for (int i = 0; i < board.length; i++) {
            randomPosition = rand.nextInt(board.length);
            swap(board, i, randomPosition);
        }
        return board;
    }


    //Allows user to enter a puzzle, if not solvable, will tell user to enter a new one
    private static void userEntersPuzzle() {
        System.out.print("\nEnter your puzzle (ex: 1 2 3 6 8 4 7 5 0) : ");

        byte[] board = new byte[goal.length];
        for (int i = 0; i < board.length; i++) {
            board[i] = kb.nextByte();
        }

        while (isSolvable(board) == false){
            System.out.print("\nImpossible puzzle, enter again (ex: 1 2 3 6 8 4 7 5 0) : ");
            for (int i = 0; i < board.length; i++) {
                board[i] = kb.nextByte();
            }
        }

        puzzleResult h1 = search(board, misplacedHeuristic);
        puzzleResult h2 = search(board, distanceHeuristic);

        printSteps(h1.resultPath);
        System.out.printf("Depth of soultion: %16d", h1.resultDepth);
        System.out.printf("\nRun time of h1 and h2 (ms) :  %5s | %-5s \n",(h1.timeFinished - h1.timeStarted), (h2.timeFinished - h2.timeStarted));
    }


    private static void printPuzzle(byte[] board) {
        for (int i = 0; i < board.length; i++) {
            System.out.format("%d ", board[i]);
            switch (i) {
                case 2:
                case 5:
                case 8:
                    System.out.format("%n");
                    break;
            }
        }
    }


    //Prints out each step it took to get to the goal step
    private static void printSteps(List<Node> solution) {
        solution.stream().forEach((i) -> {
            printPuzzle(i.board);
            System.out.println();
        });
    }


    //Even amount of inversions means its solvable
    private static boolean isSolvable(byte[] board) { return (getNumInversions(board) & 1) == 0; }


    //Checks if given board matches the goal state
    private static boolean isGoal(byte[] board) { return Arrays.equals(goal, board); }


    //Generates an estimate on how much it will take to get from given state to goal
    private static interface Heuristic<T> { int evaluate(T state); }


    //Swaps board positions
    private static void swap(byte[] array, int x, int y) {
        if (x == y) { return; }

        array[x] ^= array[y];
        array[y] ^= array[x];
        array[x] ^= array[y];
    }


    //Checks how many inversion on a board for the isSolvable() check
    private static int getNumInversions(byte[] board) {
        int inversions = 0;

        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) { continue; }

            for (int j = i; j < board.length; j++) {
                if (board[j] == 0) { continue; }

                if (board[j] < board[i]) { inversions++; }
            }
        }
        return inversions;
    }


    //Finds the 0 (blank) tile
    private static int findBlankSpace(byte[] board) {
        for (int tswift = 0; tswift < board.length; tswift++) {
            if (board[tswift] == 0) { return tswift; }
        }
        return -1;
    }


    //Tries to find path to goal state using either h1 or h2, estimates remaining distance to goal
    private static puzzleResult search(byte[] board, Heuristic<byte[]> heur) {
        int tempCost;
        int nodesVisited = 0;
        int nodesExpanded = 0;
        boolean isThereASuccessor;

        Node nod = new Node(board);
        nod.actualCost = 0;
        nod.heuristicCost = nod.actualCost + heur.evaluate(nod.board);

        Set<Node> exploredNodes = new HashSet<>();
        PriorityQueue<Node> frontier = new PriorityQueue<>((n1, n2) -> { return n1.heuristicCost - n2.heuristicCost; });
        frontier.offer(nod);

        long timeStarted = System.currentTimeMillis();
        //  long timeStarted = System.nanoTime();
        while (!frontier.isEmpty()) {
            nodesVisited++;
            nod = frontier.poll();

            if (nod.hashCode() == goalHash && isGoal(nod.board)) { return new puzzleResult(findPath(new LinkedList<>(), nod), nodesVisited, nodesExpanded, timeStarted); }

            exploredNodes.add(nod);
            for (Node successorNode : nod.generateSuccessors()) {
                if (exploredNodes.contains(successorNode)) { continue; }

                nodesExpanded++;
                tempCost = nod.actualCost + 1;
                isThereASuccessor = frontier.contains(successorNode);

                if (!isThereASuccessor || tempCost < successorNode.actualCost) {
                    successorNode.parent = nod;
                    successorNode.actualCost = tempCost;
                    successorNode.heuristicCost = successorNode.actualCost + heur.evaluate(successorNode.board);

                    frontier.offer(successorNode);
                }
            }
        }
        return new puzzleResult(Collections.EMPTY_LIST, nodesVisited, nodesExpanded, timeStarted);
    }


    //Recursively finds path from the original puzzle to the goal
    private static List<Node> findPath(List<Node> lis, Node nod) {
        if (nod == null) { return lis; }
        else {
            lis.add(0, nod);
            return findPath(lis, nod.parent);
        }
    }


    //Each node stores all information for a state.
    private static class Node {
        int boardHash;
        int blankTilePostion;
        int actualCost;
        int heuristicCost;

        byte[] board;
        Node parent;

        Node(byte[] board) { this(board, findBlankSpace(board)); }

        Node(byte[] board, int blankTilePostion) {
            this.boardHash = Arrays.hashCode(board);
            this.blankTilePostion = blankTilePostion;
            this.actualCost = Integer.MAX_VALUE;
            this.heuristicCost = Integer.MAX_VALUE;

            this.board = board;
            this.parent = null;
        }

        public int hashCode() {
            return boardHash;
        }

        public boolean equals(Object obj) {
            if (obj == this) { return true; }

            if (!(obj instanceof Node)) { return false; }

            Node nod = (Node)obj;
            return Arrays.equals(board, nod.board);
        }

        Collection<Node> generateSuccessors() {
            int newBlankPostion;
            int actions = possibleMoves[blankTilePostion];
            byte[] successorNode;
            Collection<Node> successorNodes = new LinkedList<>();


            if ((actions & up) != 0) {
                successorNode = Arrays.copyOf(board, board.length);
                newBlankPostion = blankTilePostion - 3;
                swap(successorNode, blankTilePostion, newBlankPostion);

                successorNodes.add(new Node(successorNode, newBlankPostion));
            }

            if ((actions & down) != 0) {
                successorNode = Arrays.copyOf(board, board.length);
                newBlankPostion = blankTilePostion + 3;
                swap(successorNode, blankTilePostion, newBlankPostion);

                successorNodes.add(new Node(successorNode, newBlankPostion));
            }

            if ((actions & left) != 0) {
                successorNode = Arrays.copyOf(board, board.length);
                newBlankPostion = blankTilePostion - 1;
                swap(successorNode, blankTilePostion, newBlankPostion);

                successorNodes.add(new Node(successorNode, newBlankPostion));
            }


            if ((actions & right) != 0) {
                successorNode = Arrays.copyOf(board, board.length);
                newBlankPostion = blankTilePostion + 1;
                swap(successorNode, blankTilePostion, newBlankPostion);

                successorNodes.add(new Node(successorNode, newBlankPostion));
            }

            return successorNodes;
        }
    }


    //Stores all information about the final solution from the initial board
    private static class puzzleResult {
        int throughNodes;
        int expandedNodes;
        int resultDepth;
        long timeStarted;
        long timeFinished;

        List<Node> resultPath;

        puzzleResult(List<Node> path, int nodesVisited, int nodesExpanded, long startTime) {
            this.throughNodes = nodesVisited;
            this.expandedNodes = nodesExpanded;
            this.resultDepth = path.size() - 1;
            this.timeStarted = startTime;
            this.timeFinished = System.currentTimeMillis();

            this.resultPath = path;
        }
    }
}
