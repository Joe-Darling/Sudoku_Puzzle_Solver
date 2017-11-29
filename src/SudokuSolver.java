import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Created by Joe on 7/19/2017.
 * You can run this using the many puzzles in the data folder of the project to test the solver at various difficulties
 * and sizes. It can safely handle anywhere from a 2 x 2 puzzle up to a 25 x 25 puzzle (although you would probably die
 * before it finished.
 */
public class SudokuSolver {

    /**
     * This method simply calls the solver and attempts to solve the puzzle. Outputting the time it took to solve it
     * and the solution, if it found one.
     * @param map
     * @param bt
     * @throws FileNotFoundException
     */
    private void solve(File map, BackTracker bt) throws FileNotFoundException {
        SudokuBoard init = new SudokuBoard(map.getPath());
        System.out.println("Puzzle: " + map.getParentFile().getName().charAt(0) + "-" + map.getName());

        double start = System.currentTimeMillis();

        Optional<Configuration> sol = bt.solve(init);

        // compute the elapsed time
        System.out.println("Elapsed time: " +
                (System.currentTimeMillis() - start) / 1000.0 + " seconds.");

        // indicate whether there was a solution, or not
        if (sol.isPresent()) {
            System.out.println("Solution:\n" + sol.get());
        } else {
            System.out.println("No solution!");
        }
    }

    /**
     * @param args Execution requires:
     *             args[0] = pathname to file or folder containing puzzles. Can only confirm .txt files work.
     *             args[1] = string value of 'true' or 'false' for Debug mode on/off
     *             args[2] = num of puzzles to do, only used when directing to a folder instead of files.
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        SudokuSolver sudokuSolver = new SudokuSolver();
        if(args.length != 3)
            System.err.println("Usage: Incorrect number of arguments, please refer to SudokuSolver.java script.");
        else{
            BackTracker bt = new BackTracker(args[1].equals("true"));
            int puzzlesToDo = Integer.valueOf(args[2]);
            if(args[0].equals("all")){
                File folder = new File("data");
                File[] files = folder.listFiles();
                File[] subdir;
                if(files != null) {
                    for (File file : files) {
                        if(file.isFile())
                            sudokuSolver.solve(file, bt);
                        else if(file.isDirectory()) {
                            subdir = file.listFiles();
                            if(subdir != null) {
                                for (File map : subdir){
                                    if(puzzlesToDo-- > 0)
                                        sudokuSolver.solve(map, bt);
                                }
                            }
                        }
                    }
                }
            }
            else
                sudokuSolver.solve(new File(args[0]), bt);
        }
    }
}
