import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

enum MOVE
{
    BOARD_INIT,
    YOUR_SINK,
    OPPONENT_SINK,
    MAX_CAPACITY,
    MAKE_MOVE,
    OPPONENT_MOVE,
    VALIDATE_BOARD
}

/*
    Empty = 0
    Our cell = 1
    their cell = -1
    our sink cell = 2
    their sink cell = -2
 */

class Cell {
    public int owner=0, currentMass=0, weight=0, criticalMass, heuristic=0;
}

public class HunkBot {

    Cell[][] board;
    int sinkMaxCapacity;
    int ourCounter = 0;
    int oppCounter = 0;
    FastReader reader = new FastReader();
    Point ourSink;

    static class Point {
        int x;
        int y;
        Point(int x,int y){
            this.x = x;
            this.y = y;
        }
    }
    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader()
        {
            br = new BufferedReader(
                    new InputStreamReader(System.in));
        }

        String next()
        {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() { return Integer.parseInt(next()); }

        String nextLine()
        {
            String str = "";
            try {
                str = br.readLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }

    public static void main(String[] args){
         HunkBot mrBot = new HunkBot();
         mrBot.runProgram();
    }

    private void runProgram() {
        while(true) {
            String input = reader.next();
            MOVE move = getMoveFromInput(input);
            if(move!=null){
                processMove(move);
            }
        }
    }

    MOVE getMoveFromInput(String input) {
        try {
            return MOVE.valueOf(input);
        }
        catch (Exception e) {
            return null;
        }
    }

    void processMove(MOVE move){
        Point newPoint;
        switch (move)
        {
            case BOARD_INIT:
                int row = reader.nextInt();
                int col = reader.nextInt();
                initializeBoard(row,col);
                printOutput(0);
                break;
            case YOUR_SINK:
            case OPPONENT_SINK:
                int xSink = reader.nextInt();
                int ySink = reader.nextInt();
                setupSink(xSink,ySink,move == MOVE.YOUR_SINK);
                printOutput(0);
                break;
            case MAX_CAPACITY:
                int maxCapacity = reader.nextInt();
                setupMaxCapacity(maxCapacity);
                printOutput(0);
                break;
            case MAKE_MOVE:
                newPoint = makeMove();
                printOutput(newPoint.x+" "+ newPoint.y);
                break;
            case OPPONENT_MOVE:
                int x = reader.nextInt();
                int y= reader.nextInt();
                newPoint = opponentMove(x, y);
                printOutput(newPoint.x+" "+ newPoint.y);
                break;
            case VALIDATE_BOARD:
//                int ownBoard = reader.nextInt();
//                boolean boardValid = true;
//                for(int i=0;i<board.length;i++) {
//                    for(int j=0;j<board[0].length;j++) {
//                        int cellMass = reader.nextInt();
//                        cellMass *= ownBoard;
//                        Cell currentCell = board[i][j];
//                        if ((currentCell.owner == 1 && cellMass <= 0) ||
//                                (currentCell.owner == -1 && cellMass >= 0) ||
//                                (currentCell.currentMass != Math.abs(cellMass))
//                        ) {
//                            boardValid = false;
//                        }
//                        else if ((currentCell.owner == 2 && cellMass != ourCounter) ||
//                                (currentCell.owner == -2 && cellMass != oppCounter * -1)) {
//                            boardValid = false;
//                        }
//                    }
//                }
//                if (boardValid) {
//                    printOutput("0");
//                }
//                else {
//                    printBoard();
//                }
                printOutput("0");
                break;
        }
    }

    void setupMaxCapacity(int cap) {
        sinkMaxCapacity = cap;
    }

    void printOutput(Object message) {
        System.out.println(message);
    }

    void setupSink(int xSink, int ySink, boolean ourSink){
        board[xSink][ySink].owner = (ourSink ? 2 : -2);
        if(ourSink) {
            this.ourSink = new Point(xSink, ySink);
        }
    }

    void initializeBoard(int rows, int columns) {
        board = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = new Cell();
                boolean endRow = i == 0 || i == rows - 1;
                boolean endCol = j == 0 || j == columns - 1;
                if (endRow && endCol) {
                    board[i][j].criticalMass = 2;
                } else if (endRow || endCol) {
                    board[i][j].criticalMass = 3;
                } else {
                    board[i][j].criticalMass = 4;
                }
            }
        }
    }

    void printBoard() {
        for (int i=0;i<board.length;i++) {
            for(int j=0;j<board[0].length;j++) {
                if (board[i][j].owner < 2 && board[i][j].owner > -2) {
                    System.out.print(board[i][j].currentMass * board[i][j].owner);
                } else {
                    if(board[i][j].owner == 2) {
                        System.out.print(ourCounter);
                    } else if (board[i][j].owner == -2) {
                        System.out.print(oppCounter * -1);
                    }
                }
                System.out.print(' ');
            }
        }
        System.out.println();
    }

    Point makeMove() {
        Point selectedPoint = new Point(0,0);
        doBoardMove(true, selectedPoint);
        refreshWeights();
//        printBoard();
        return selectedPoint;
    }

    Point opponentMove(int x1, int y1) {
        Cell opponentMoveCell = board[x1][y1];
        opponentMoveCell.owner = -1;
        Point opponentPoint = new Point(x1, y1);
        doBoardMove(false, opponentPoint);
        refreshWeights();
//        printBoard();

        Point ourPoint = cellSelection();
        doBoardMove(true, ourPoint);
        refreshWeights();
//        printBoard();
        return ourPoint;
    }

    private void doBoardMove(boolean ownMove, Point p){
        // In case point is out of bounds
        if ((p.x < 0 || p.x>board.length) || (p.y < 0 || p.y>board[0].length)) {
            return;
        }
        Cell currentCell = board[p.x][p.y];
        if (ownMove && currentCell.owner == 2 && ourCounter < sinkMaxCapacity) {
            ourCounter++;
            return;
        }
        if (!ownMove && currentCell.owner == -2 && oppCounter < sinkMaxCapacity) {
            oppCounter++;
            return;
        }

        if (currentCell.currentMass < currentCell.criticalMass - 1) {
            if (currentCell.owner == 0) {
                currentCell.owner = ownMove ? 1 : -1;
            }
            if (ownMove && currentCell.owner == -1) {
                currentCell.owner = 1;
            }
            else if (!ownMove && currentCell.owner == 1) {
                currentCell.owner = -1;
            }
            if (currentCell.owner == 1 || currentCell.owner == -1) {
                currentCell.currentMass++;
            }
            return;
        }
        currentCell.owner = 0;
        currentCell.currentMass = 0;
        if (p.x > 0) {
            Point nPoint = new Point(p.x - 1, p.y);
            doBoardMove(ownMove, nPoint);

            Cell boardCell = board[nPoint.x][nPoint.y];
            if ((boardCell.owner == 2 && ourCounter == sinkMaxCapacity) ||
                    (boardCell.owner == -2 && oppCounter == sinkMaxCapacity)) {
                doBoardMove(ownMove, p);
            }
        }
        if (p.x < board.length - 1) {
            Point sPoint = new Point(p.x + 1, p.y);
            doBoardMove(ownMove, sPoint);

            Cell boardCell = board[sPoint.x][sPoint.y];
            if ((boardCell.owner == 2 && ourCounter == sinkMaxCapacity) ||
                    (boardCell.owner == -2 && oppCounter == sinkMaxCapacity)) {
                doBoardMove(ownMove, p);
            }
        }
        if (p.y < board.length - 1) {
            Point wPoint = new Point(p.x, p.y + 1);
            doBoardMove(ownMove, wPoint);

            Cell boardCell = board[wPoint.x][wPoint.y];
            if ((boardCell.owner == 2 && ourCounter == sinkMaxCapacity) ||
                    (boardCell.owner == -2 && oppCounter == sinkMaxCapacity)) {
                doBoardMove(ownMove, p);
            }
        }
        if (p.y > 0) {
            Point ePoint = new Point(p.x, p.y - 1);
            doBoardMove(ownMove, ePoint);

            Cell boardCell = board[ePoint.x][ePoint.y];
            if ((boardCell.owner == 2 && ourCounter == sinkMaxCapacity) ||
                    (boardCell.owner == -2 && oppCounter == sinkMaxCapacity)) {
                doBoardMove(ownMove, p);
            }
        }
    }

    Vector<Cell> getAdjacentCells(Point p) {
        Vector <Cell> adjacentCells = new Vector<>();
        for(int i = -1 ; i < 2 ; i ++) {
            for(int j = -1 ; j < 2 ; j++) {
                if ((p.x + i > 0 && p.x + i < board.length - 1)
                        && (p.y + j > 0 && p.y + j < board[0].length - 1)
                        && Math.abs(i) != Math.abs(j)) {
                    adjacentCells.add(board[p.x + i][p.y + j]);
                }
            }
        }
        return adjacentCells;
    }

    void calculateCellHeuristic() {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++) {
                Point currentPoint = new Point(i, j);
                Cell currentCell = board[i][j];
                Vector <Cell> adjacentCells = getAdjacentCells(currentPoint);
                int opponentAdjacentCount = 0;
                for(int cellItr=0 ; cellItr < adjacentCells.size(); cellItr++) {
                    Cell adjacentCell = adjacentCells.get(cellItr);
                    if(adjacentCell.currentMass == adjacentCell.criticalMass - 1 && adjacentCell.owner < 0) {
                        currentCell.weight -= 5 - adjacentCell.criticalMass;
                        opponentAdjacentCount++;
                    } else if (adjacentCell.owner > 0) {
                        currentCell.weight += 1;
                    }
                }
                if(opponentAdjacentCount == 0) {
                    if(currentCell.currentMass == currentCell.criticalMass - 1) {
                        currentCell.weight += 2;
                    } else {
                        currentCell.weight += currentCell.criticalMass - 1;
                    }
                }
            }
        }
    }


    void refreshWeights() {
        calculateCellHeuristic();


        // Reset weights for opponent tiles and sink tiles
        // Don't change
        for(int i = 0; i < board.length ; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].owner < 0 || board[i][j].owner == 2) {
                    board[i][j].weight = -100;
                }
            }
        }
    }

    private Point cellSelection() {
        Vector<Cell> bestCells = new Vector<Cell>();
        Vector<Point> cellPoints = new Vector<Point>();
        // Add the first cell selection cells
        for (int i = 0; i < board.length; i++) {
            for (int j = 0 ; j < board[0].length; j++) {
                // Add cell to bestCells vector if it is empty
                if(bestCells.isEmpty() && (board[i][j].owner == 0 || board[i][j].owner == 1)) {
                    bestCells.add(board[i][j]);
                    cellPoints.add(new Point(i, j));
                }
                if(!bestCells.isEmpty()) {
                    // Add to collection of cells if cell has maximum detected weight yet
                    if(bestCells.firstElement().weight == board[i][j].weight) {
                        bestCells.add(board[i][j]);
                        cellPoints.add(new Point(i, j));
                    }
                    // Clear up the vector and add new element
                    else if (bestCells.firstElement().weight < board[i][j].weight) {
                        bestCells.clear();
                        bestCells.add(board[i][j]);
                        cellPoints.clear();
                        cellPoints.add(new Point(i, j));
                    }
                }
            }
        }
        int randomIndex = (int)(Math.random() * (cellPoints.size() - 1));
        return cellPoints.get(randomIndex);
        // Select the best cell which is closest to the group of cells belonging to us, without triggering chain reactions
    }


}
