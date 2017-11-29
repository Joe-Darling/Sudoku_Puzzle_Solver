import java.util.Optional;

/**
 * Created by Joe on 7/19/2017.
 * Class uses a backtracking algorithm to step through a puzzle and find
 * the first possible solution.
 */
public class BackTracker {

    private boolean debug; // If enabled the console will output relevant data at each step.

    public BackTracker(boolean debug){
        this.debug = debug;
        if(debug)
            System.out.println("Back tracker debugging enabled.");
    }

    private void debugPrint(String msg, Configuration config){
        if(debug)
            System.out.println(msg + ": " + "\n" + config);
    }

    /**
     * This method, given a configuration will attempt to find a solution
     * by going depth first down all subsequent configs until it either
     * finds a valid config or or has no more valid configs.
     *
     * @param config The current config that the backtracker is checking
     * @return An optional type either containing the first found solution to the puzzle, or null.
     */
    public Optional<Configuration> solve(Configuration config){
        debugPrint("Current config", config);
        if(config.isGoal()){
            debugPrint("Goal config", config);
            return Optional.of(config);
        }
        else{
            for(Configuration child : config.getSuccessors()){
                debugPrint("Successor", child);
                if(child.isValid()){
                    if(debug)
                        System.out.println("Valid!");
                    Optional<Configuration> sol = solve(child);
                    if(sol.isPresent()){
                        return sol;
                    }
                }
                else{
                    if(debug)
                        System.out.println("Invalid!");
                }
            }
        }
        return Optional.empty();
    }

}
