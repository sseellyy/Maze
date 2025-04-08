import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class MazeGame extends JPanel {
    // Размер клетки
    private static final int CELL_SIZE = 30;

    // Размеры лабиринта
    private static final int ROWS = 15;
    private static final int COLS = 15;

    // Константы для представления различных объектов в лабиринте
    private static final int WALL = 1;
    private static final int PATH = 0;
    private static final int PLAYER = 2;
    private static final int EXIT = 3;
    private static final int VISITED = 4;

    // Матрица для хранения лабиринта
    private int[][] maze = new int[ROWS][COLS];

    // Начальная позиция игрока
    private int playerRow = 1, playerCol = 1;

    // Конструктор для установки размеров панели и инициализации игры
    public MazeGame() {
        // Устанавливаем размер панели, которая будет отображать лабиринт
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));

        // Делаем панель фокусируемой для получения ввода с клавиатуры
        setFocusable(true);

        // Генерация лабиринта при запуске игры
        generateMaze();

        // Добавляем обработчик клавиш для движения игрока
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode()); // Обработка нажатия клавиш
                repaint(); // Перерисовываем экран
            }
        });
    }

    // Метод для генерации лабиринта с использованием рекурсивного алгоритма
    private void generateMaze() {
        // Заполняем весь лабиринт стенами
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                maze[r][c] = WALL;
            }
        }

        // Генерируем лабиринт с помощью рекурсивного поиска пути
        recursiveBacktracking(1, 1);

        // Устанавливаем начальную позицию игрока
        maze[1][1] = PLAYER;

        // Устанавливаем финиш на противоположной стороне лабиринта
        maze[ROWS - 2][COLS - 2] = EXIT;

        // Устанавливаем начальные координаты игрока
        playerRow = 1;
        playerCol = 1;
    }

    // Рекурсивный алгоритм для генерации пути в лабиринте
    private void recursiveBacktracking(int r, int c) {
        // Направления для движения (вверх, вниз, влево, вправо)
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};

        // Перемешиваем направления, чтобы путь был случайным
        shuffleArray(directions);

        // Отметим текущую клетку как путь
        maze[r][c] = PATH;

        // Пробуем пройти в каждом из направлений
        for (int[] d : directions) {
            int nr = r + d[0], nc = c + d[1];

            // Если клетка внутри лабиринта и еще не посещена, продолжаем путь
            if (nr > 0 && nr < ROWS - 1 && nc > 0 && nc < COLS - 1 && maze[nr][nc] == WALL) {
                // Прокладываем путь между соседними клетками
                maze[r + d[0] / 2][c + d[1] / 2] = PATH;

                // Рекурсивно генерируем путь из новой клетки
                recursiveBacktracking(nr, nc);
            }
        }
    }

    // Метод для перемешивания направлений случайным образом (для создания случайных лабиринтов)
    private void shuffleArray(int[][] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    // Метод для обработки движения игрока по лабиринту
    private void movePlayer(int keyCode) {
        int newRow = playerRow, newCol = playerCol;

        // В зависимости от нажатой клавиши изменяем позицию игрока
        switch (keyCode) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP: newRow--; break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN: newRow++; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT: newCol--; break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: newCol++; break;
        }

        // Проверяем, можно ли двигаться в выбранную клетку (она не должна быть стеной)
        if (maze[newRow][newCol] != WALL) {
            // Отмечаем старую позицию игрока как посещенную
            maze[playerRow][playerCol] = VISITED;

            // Обновляем позицию игрока
            playerRow = newRow;
            playerCol = newCol;

            // Если игрок достиг финиша, показываем окно с результатом
            if (maze[playerRow][playerCol] == EXIT) {
                int option = JOptionPane.showOptionDialog(this, "Вы выиграли! Хотите начать заново?", "Победа!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (option == JOptionPane.YES_OPTION) {
                    generateMaze(); // Генерируем новый лабиринт
                    repaint(); // Перерисовываем экран
                } else {
                    System.exit(0); // Закрываем игру
                }
            }
            // Отмечаем новую позицию игрока
            maze[playerRow][playerCol] = PLAYER;
        }
    }

    // Метод для отрисовки всех элементов лабиринта
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Проходим по всем клеткам лабиринта
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                // Устанавливаем цвет в зависимости от типа клетки
                if (maze[r][c] == WALL) g.setColor(Color.BLUE);  // Стены — синие
                else if (maze[r][c] == PATH) g.setColor(Color.WHITE); // Путь — белый
                else if (maze[r][c] == VISITED) g.setColor(Color.PINK); // Посещенные клетки — розовые
                else if (maze[r][c] == PLAYER) g.setColor(Color.RED); // Игрок — красный
                else if (maze[r][c] == EXIT) g.setColor(Color.YELLOW); // Финиш — желтый

                // Рисуем клетку
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    // Метод для запуска игры
    public static void main(String[] args) {
        // Создаем окно с игрой
        JFrame frame = new JFrame("Maze Game");

        // Создаем объект игры
        MazeGame game = new MazeGame();

        // Добавляем игру в окно
        frame.add(game);

        // Настроим размер окна
        frame.pack();

        // Устанавливаем поведение при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Отображаем окно
        frame.setVisible(true);
    }
}
