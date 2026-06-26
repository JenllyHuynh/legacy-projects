import model.Player;
import repository.GameRepository;
import service.SlotMachineService;
import view.SlotMachineView;

/**
 * S04 - Simple Slot Machine
 * Main class - Entry point of the Slot Machine Game application.
 * Initializes all components and starts the game.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Main method that launches the Slot Machine Game application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create repository instance to handle game data storage/loading
        GameRepository repo = new GameRepository();

        // Load saved money from persistent storage
        // Returns null if no saved game exists
        Double savedMoney = repo.loadGame();

        // Initialize player with saved money or default starting amount
        Player player = savedMoney != null ?
                new Player(savedMoney) :      // Use saved money if exists
                new Player(10.0);// Otherwise start with $10

        // Display player's current balance
        System.out.printf("You have %s.\n", player);

        // Notify user if game was successfully loaded
        if (savedMoney != null) {
            System.out.println("Game loaded successfully!");
        }

        // Create service layer instance with game logic
        SlotMachineService service = new SlotMachineService(player);

        // Create view layer instance for user interface
        SlotMachineView view = new SlotMachineView(service);

        // Start the game by launching the view
        view.start();
    }
}