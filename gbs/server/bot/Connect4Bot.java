package gbs.server.bot;
import gbs.Matrix;
import gbs.Packet;
import gbs.server.BotLobby;
/*
 * First player joins
 * Second player joins
 * First player sends "yourColor" to second player
 * First player sends "play"
 * 
 * Winner will send "end" to other player
 * Loser will send "rematch"
 * Winner will send "rematch"
 * Loser will send "yourColor" to other player
 * Loser will make first move
 */

public class Connect4Bot extends Bot {
    private final int BLUE_TOKEN = 1;
    private final int GOLD_TOKEN = 5;

    private Matrix<Integer> gameBoard;
    private int myColor;
    private int currentPlayer;
    private int winningPlayer;
    private boolean iWantRematch;

    public Connect4Bot(BotLobby lobby) {
        super(lobby);
        gameBoard = new Matrix<>(6, 7, 0);
        this.newGame();
    }

    private void newGame() {
        gameBoard = new Matrix<>(6, 7, 0);
        myColor = 0;
        currentPlayer = 0;
        if (Math.random() < 0.5) {
            currentPlayer = GOLD_TOKEN;
        } else {
            currentPlayer = BLUE_TOKEN;
        }
        winningPlayer = -1;
        myColor = currentPlayer;
    }

    public void makePlay() {

        // Need to implement a tie check
        int count = 0;
        for (int i = 0; i < 7; i++) {
            if (gameBoard.get(0,i) != 0) {
                count++;
            }
        }
        if (count == 7) {
            send("end","" + 3); // tie
        }

        boolean notValidMove = true;
        while (notValidMove) {
            int placeTokenColumn = (int) (Math.random() * 7);
            for (int r = 5; r >= 0; r--) {
                if (gameBoard.get(r, placeTokenColumn) == 0) {
                    gameBoard.set(r, placeTokenColumn, currentPlayer);
                    send("play", r + " " + placeTokenColumn + " " + myColor);
                    notValidMove = false;

                    break;
                }
            }
        }

        // Check if new move resulted in win
        checkDiagonals();
        checkHorizontal();
        checkVertical();

        // If it did end, send "end"
        if (winningPlayer != -1) {
            send("end", "" + winningPlayer);
            iWantRematch = true;
        }

        // Update currentPlayer
        if (currentPlayer == BLUE_TOKEN) {
            currentPlayer = GOLD_TOKEN;
        } else {
            currentPlayer = BLUE_TOKEN;
        }
    }

    @Override
    public void onRecieve(Packet packet) {
        // System.out.println("<- " + packet);


        if (packet.getType().equals("yourColor")) {
            // newGame();
            gameBoard = new Matrix<>(6, 7, 0);
            winningPlayer = -1;
            myColor = Integer.parseInt(packet.getMessage());
            if (myColor == GOLD_TOKEN) {
                currentPlayer = BLUE_TOKEN;
            } else {
                currentPlayer = GOLD_TOKEN;
            }
        }

        if (packet.getType().equals("play")) {
            String[] words = packet.getMessage().split(" ");
            int row = Integer.parseInt(words[0]);
            int col = Integer.parseInt(words[1]);
            int tokenNum = Integer.parseInt(words[2]);

            gameBoard.set(row, col, tokenNum);
            if (currentPlayer == GOLD_TOKEN) {
                currentPlayer = BLUE_TOKEN;
            } else {
                currentPlayer = GOLD_TOKEN;
            }

            // They played, now it's my turn
            thinkTime(1.0);

            checkDiagonals();
            checkHorizontal();
            checkVertical();

            // If still no winner, then play
            if (winningPlayer == -1) {
                makePlay();
            }
        }

        if (packet.getType().equals("end")) {
            // Think time on whether they want to replay
            thinkTime(1.0);
            send("rematch", "");
        }

        if (packet.getType().equals("rematch")) {
            if (iWantRematch) {
                thinkTime(0.5);
                send("rematch", "");
            } else {
                thinkTime(1.0);
                // newGame();
                gameBoard = new Matrix<>(6, 7, 0);
                winningPlayer = -1;
                myColor = currentPlayer;
                if (myColor == GOLD_TOKEN) {
                    send("yourColor", "" + BLUE_TOKEN);
                } else {
                    send("yourColor", "" + GOLD_TOKEN);
                }
                thinkTime(0.5);
                makePlay();
            }
        }
    }

    public void checkHorizontal() {
        // to be completed by you...
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7 - 3; c++) {
                if (gameBoard.get(r, c) == gameBoard.get(r, c + 1) && gameBoard.get(r, c + 1) == gameBoard.get(r, c + 2)
                        && gameBoard.get(r, c + 2) == gameBoard.get(r, c + 3) && gameBoard.get(r, c) != 0) {
                    winningPlayer = gameBoard.get(r, c);
                }
            }
        }

    }

    public void checkVertical() {
        // to be completed by you...
        for (int r = 0; r < 6 - 3; r++) {
            for (int c = 0; c < 7; c++) {
                if (gameBoard.get(r, c) == gameBoard.get(r + 1, c) && gameBoard.get(r + 1, c) == gameBoard.get(r + 2, c)
                        && gameBoard.get(r + 2, c) == gameBoard.get(r + 3, c) && gameBoard.get(r, c) != 0) {
                    winningPlayer = gameBoard.get(r, c);
                }
            }
        }

    }

    public void checkDiagonals() {
        // to be completed by you...
        for (int r = 0; r < 6 - 3; r++) {
            for (int c = 0; c < 7 - 3; c++) {
                if (gameBoard.get(r, c) == gameBoard.get(r + 1, c + 1)
                        && gameBoard.get(r + 1, c + 1) == gameBoard.get(r + 2, c + 2)
                        && gameBoard.get(r + 2, c + 2) == gameBoard.get(r + 3, c + 3) && gameBoard.get(r, c) != 0) {
                    winningPlayer = gameBoard.get(r, c);
                }
            }
        }
        for (int r = 0 + 3; r < 6; r++) {
            for (int c = 0; c < 7 - 3; c++) {
                if (gameBoard.get(r, c) == gameBoard.get(r - 1, c + 1)
                        && gameBoard.get(r - 1, c + 1) == gameBoard.get(r - 2, c + 2)
                        && gameBoard.get(r - 2, c + 2) == gameBoard.get(r - 3, c + 3) && gameBoard.get(r, c) != 0) {
                    winningPlayer = gameBoard.get(r, c);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Connect4" + super.getName();
    }
}
