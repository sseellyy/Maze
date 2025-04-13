import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MazeGame extends JPanel {

    private static final int CELL_SIZE = 30;
    private static final int ROWS = 15;
    private static final int COLS = 15;

    // Типы клеток
    private static final int WALL = 1;
    private static final int PATH = 0;
    private static final int PLAYER = 2;
    private static final int EXIT = 3;
    private static final int VISITED = 4;
    private static final int SOLUTION = 5;

    private final int[][] maze = new int[ROWS][COLS];

    // Начало и конец
    private final int startRow = 1;
    private final int startCol = 1;

    public MazeGame() {
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        setFocusable(true);

        generateMaze();
        startSolving(); // Автостарт решения
    }

    private void generateMaze() {
        // Заполнение стенами
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                maze[r][c] = WALL;
            }
        }

        // Генерация пути
        recursiveBacktracking(startRow, startCol);

        // Установка старта и финиша
        maze[startRow][startCol] = PATH;
        int exitRow = ROWS - 2;
        int exitCol = COLS - 2;
        maze[exitRow][exitCol] = EXIT;

        repaint();
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

    private void startSolving() {
        new Thread(() -> {
            // Очистка следов от предыдущего запуска
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (maze[r][c] == VISITED || maze[r][c] == PLAYER || maze[r][c] == SOLUTION) {
                        maze[r][c] = PATH;
                    }
                }
            }

            boolean solved = solveMaze(startRow, startCol);

            if (solved) {
                // Показываем диалог после успешного прохождения
                int option = JOptionPane.showOptionDialog(this, "Путь найден! Что вы хотите сделать?",
                        "Победа!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, new Object[]{"Новый лабиринт", "Выход"}, "Новый лабиринт");

                if (option == JOptionPane.YES_OPTION) {
                    generateMaze();
                    startSolving();
                } else {
                    System.exit(0);
                }
            }
        }).start();
    }

    private boolean solveMaze(int r, int c) {
        if (r < 0 || c < 0 || r >= ROWS || c >= COLS || maze[r][c] == WALL || maze[r][c] == VISITED)
            return false;

        if (maze[r][c] == EXIT) {
            maze[r][c] = SOLUTION;
            repaintDelay();
            return true;
        }

        maze[r][c] = PLAYER;
        repaintDelay();

        maze[r][c] = VISITED;

        if (solveMaze(r - 1, c) || solveMaze(r + 1, c) || solveMaze(r, c - 1) || solveMaze(r, c + 1)) {
            maze[r][c] = SOLUTION;
            repaintDelay();
            return true;
        }

        return false;
    }

    private void repaintDelay() {
        try {
            Thread.sleep(30); // Задержка для визуализации
        } catch (InterruptedException ignored) {}
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                switch (maze[r][c]) {
                    case WALL -> g.setColor(Color.BLUE);
                    case PATH -> g.setColor(Color.WHITE);
                    case VISITED -> g.setColor(Color.PINK);
                    case PLAYER -> g.setColor(Color.RED);
                    case EXIT -> g.setColor(Color.YELLOW);
                    case SOLUTION -> g.setColor(Color.GREEN);
                }
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Maze Solver");
        frame.add(new MazeGame());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
