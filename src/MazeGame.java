import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Stack;

public class MazeGame extends JPanel {
    private static final int CELL_SIZE = 30;
    private static final int ROWS = 15;
    private static final int COLS = 15;
    private static final int WALL = 1;
    private static final int PATH = 0;
    private static final int PLAYER = 2;
    private static final int EXIT = 3;
    private static final int VISITED = 4;
    private int[][] maze = new int[ROWS][COLS];
    private int playerRow = 1, playerCol = 1;

    public MazeGame() {
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        setFocusable(true);
        generateMaze();
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode());
                repaint();
            }
        });
    }

    private void generateMaze() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                maze[r][c] = WALL;
            }
        }
        recursiveBacktracking(1, 1);
        maze[1][1] = PLAYER;
        maze[ROWS - 2][COLS - 2] = EXIT;
    }

    private void recursiveBacktracking(int r, int c) {
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        shuffleArray(directions);
        maze[r][c] = PATH;
        for (int[] d : directions) {
            int nr = r + d[0], nc = c + d[1];
            if (nr > 0 && nr < ROWS - 1 && nc > 0 && nc < COLS - 1 && maze[nr][nc] == WALL) {
                maze[r + d[0] / 2][c + d[1] / 2] = PATH;
                recursiveBacktracking(nr, nc);
            }
        }
    }

    private void shuffleArray(int[][] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private void movePlayer(int keyCode) {
        int newRow = playerRow, newCol = playerCol;
        switch (keyCode) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP: newRow--; break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN: newRow++; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT: newCol--; break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: newCol++; break;
        }
        if (maze[newRow][newCol] != WALL) {
            maze[playerRow][playerCol] = VISITED;
            playerRow = newRow;
            playerCol = newCol;
            if (maze[playerRow][playerCol] == EXIT) {
                JOptionPane.showMessageDialog(this, "Вы выиграли!");
                generateMaze();
            }
            maze[playerRow][playerCol] = PLAYER;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (maze[r][c] == WALL) g.setColor(Color.BLACK);
                else if (maze[r][c] == PATH) g.setColor(Color.WHITE);
                else if (maze[r][c] == VISITED) g.setColor(Color.PINK);
                else if (maze[r][c] == PLAYER) g.setColor(Color.BLUE);
                else if (maze[r][c] == EXIT) g.setColor(Color.RED);
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Maze Game");
        MazeGame game = new MazeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
