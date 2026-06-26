package model;

/**
 * S04 - Simple Slot Machine
 * Represents a player in the slot machine game.
 * Manages player's money in cents internally for precision.
 * Money is stored in cents to avoid floating-point precision issues.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Player {
    // Player's money stored in cents (e.g., $1.00 = 100 cents)
    private double money;

    /**
     * Constructs a new Player with the specified initial money.
     *
     * @param moneyInDollars initial money amount in dollars
     */
    public Player(double moneyInDollars){
        this.money = moneyInDollars * 100;  // Convert dollars to cents
    }

    /**
     * Gets the player's money in dollars.
     *
     * @return money amount in dollars
     */
    public double getMoneyInDollars(){
        return money / 100.0;  // Convert cents to dollars
    }

    /**
     * Gets the player's money in cents.
     *
     * @return money amount in cents
     */
    public int getMoneyInCents() {
        return (int) money;  // Cast to int to get whole cents
    }

    /**
     * Deducts the standard bet amount (25 cents) from player's money.
     */
    public void deductBet() {
        money -= 25;  // Subtract 25 cents
    }

    /**
     * Adds winnings to player's money.
     *
     * @param cents amount to add in cents
     */
    public void addWinnings(int cents) {
        money += cents;  // Add specified cents amount
    }

    /**
     * Checks if the player has enough money to play.
     *
     * @return true if player has at least 25 cents, false otherwise
     */
    public boolean canPlay() {
        return money >= 25;  // Minimum bet is 25 cents
    }

    /**
     * Returns a string representation of player's money in dollars.
     *
     * @return formatted string showing money with dollar sign and 2 decimal places
     */
    @Override
    public String toString() {
        return String.format("$%.2f", getMoneyInDollars());  // Format as $X.XX
    }
}