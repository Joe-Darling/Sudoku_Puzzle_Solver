import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Joe on 7/19/2017.
 */
public class SudokuBoard implements Configuration{

    private char[][] board;
    private int bound;
    private String lastMove;
    private int numOfBlanks;

    /**
     * Instantiates a new instance of the board.
     * @param filename The name of the file.
     * @throws FileNotFoundException Thrown if file does not exist.
     */
    public SudokuBoard(String filename) throws FileNotFoundException {

        try (Scanner in = new Scanner(new File(filename))) {
            String scale = in.nextLine();
            bound = Integer.valueOf(scale) * Integer.valueOf(scale);
            board = new char[bound][bound];
            numOfBlanks = 0;

            String[] line;

            for(int i = 0; i < bound; i++) {
                line = in.nextLine().split(" ");
                for (int j = 0; j < bound; j++) {
                    board[i][j] = line[j].charAt(0);
                    if(line[j].charAt(0) == '.')
                        numOfBlanks++;
                }
            }
        }
    }

    /**
     * A secondary constructor used to make an empty replica board to hold successors for the back tracker.
     * @param original The board that the copy is copying from.
     */
    private SudokuBoard(SudokuBoard original){
        this.bound = original.bound;
        this.board = new char[bound][bound];
        this.numOfBlanks = original.numOfBlanks;
    }

    /**
     * When a board is deemed valid but not a possible solution, we call this method to generate successor boards.
     * @return A list of successor configs.
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        List<SudokuBoard> newBoards = new ArrayList<>();
        for(int i = 0; i < bound; i++){
            newBoards.add(new SudokuBoard(this));
        }
        boolean placedPiece = false; // We only want to place one piece per new iteration, so make a flag here.
        for(int i = 0; i < bound; i++){
            for(int j = 0; j < bound; j++){
                if(board[i][j] == '.' && !placedPiece){
                    // Once we have found a location to put a piece we loop through our boards and place the number
                    // at the spot we found, however if we are dealing with a board bound > 3, we start using
                    // letters after the number 9. Finally we set the place piece flag to true.
                    for(int k = 1; k < newBoards.size() + 1; k++){
                        if(k < 10)
                            newBoards.get(k - 1).board[i][j] = Character.forDigit(k, 10);
                        else
                            newBoards.get(k - 1).board[i][j] = (char)(k + 86); // Ascii value of lowercase letters.
                        newBoards.get(k - 1).lastMove = String.valueOf(i + " " + j);
                        newBoards.get(k - 1).numOfBlanks--;
                    }
                    placedPiece = true;
                }
                else{
                    for (SudokuBoard newBoard : newBoards) {
                        newBoard.board[i][j] = board[i][j];
                    }
                }
            }
        }
        List<Configuration> successors = new ArrayList<>();
        successors.addAll(newBoards);
        return successors;
    }

    private boolean checkForFullBoard(){
        return numOfBlanks == 0;
    }

    /**
     * In this method we check if a board config is valid. We do this using traditional sudoku rules.
     * A row/col cannot contain 2 of the same number, and each box of size 'bound' cannot contain two
     * of the same number
     * @return A bool for whether or not the board was valid.
     */
    @Override
    public boolean isValid() {
        List<Character> seen = new ArrayList<>();
        // First we get the coordinates of the char we just placed.
        int xVal = Integer.valueOf(lastMove.split(" ")[0]);
        int yVal = Integer.valueOf(lastMove.split(" ")[1]);

        // Then we check to see if the row or column has any duplicates.

        for(int i = 0; i < bound; i++){
            char val = board[i][yVal];
            if(val != '.'){
                if(!seen.contains(val))
                    seen.add(val);
                else
                    return false;
            }
        }

        seen = new ArrayList<>();
        for(int i = 0; i < bound; i++){
            char val = board[xVal][i];
            if(val != '.'){
                if(!seen.contains(val))
                    seen.add(val);
                else
                    return false;
            }
        }

        return legalQuadrant(xVal, yVal);
    }

    /**
     * Because the boards aren't always the same size this method checks that depending on the bound size of the
     * puzzle, no numbers within the inner square repeat.
     * @param x The x coordinate of the last char placed
     * @param y The y coordinate of the last char placed
     * @return A bool for whether or not the last char placed is a duplicate in a given box.
     */
    private boolean legalQuadrant(int x, int y){
        List<Character> seen = new ArrayList<>();
        // First we find how many values are in our smaller square
        // 4 in a 4 x 4, 9 in a 9 x 9, 16 in a 16 x 16, etc.
        int valuesPerQuad = (int)Math.sqrt(bound);

        // Next we find out which mini square our newly placed char is in.
        int xQuad = x / valuesPerQuad;
        int yQuad = y / valuesPerQuad;

        // Finally we get the bounds of this mini square.
        int xStart = xQuad * valuesPerQuad;
        int yStart = yQuad * valuesPerQuad;
        int xBound = xStart + valuesPerQuad;
        int yBound = yStart + valuesPerQuad;

        // We then loop through the mini square and check for duplicates.
        for(int i = xStart; i < xBound; i++){
            for(int j = yStart; j < yBound; j++){
                char val = board[i][j];
                if(val != '.'){
                    if(!seen.contains(val))
                        seen.add(val);
                    else
                        return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isGoal() {
        return checkForFullBoard() && isValid();
    }

    @Override
    public String toString(){
        String output = "";
        for (int i = 0; i < bound; i++){
            for(int j = 0; j < bound; j++){
                output += board[i][j] + " ";
            }
            output += "\n";
        }
        return output;
    }
}
