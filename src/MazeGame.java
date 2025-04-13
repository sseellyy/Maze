import javax.swing.*; // Импортируем Swing для графического интерфейса
import java.awt.*;    // Импортируем компоненты рисования
import java.util.Random; // Для генерации случайных направлений в лабиринте

// Основной класс игры, унаследован от JPanel для рисования на экране
public class MazeGame extends JPanel {

    // Размер каждой клетки (в пикселях)
    private static final int CELL_SIZE = 30;

    // Количество строк и столбцов в лабиринте
    private static final int ROWS = 15;
    private static final int COLS = 15;

    // Типы клеток в лабиринте
    private static final int WALL = 1;      // Стена
    private static final int PATH = 0;      // Путь (проходимая клетка)
    private static final int PLAYER = 2;    // Текущая позиция поиска
    private static final int EXIT = 3;      // Выход
    private static final int VISITED = 4;   // Посещённая клетка
    private static final int SOLUTION = 5;  // Клетка, входящая в найденный путь

    // Массив, представляющий лабиринт
    private final int[][] maze = new int[ROWS][COLS];

    // Начальная и конечная позиции
    private final int startRow = 1;
    private final int startCol = 1;

    // Конструктор — вызывается при создании панели
    public MazeGame() {
        // Устанавливаем размер панели
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        setFocusable(true); // Делает панель активной для взаимодействия

        generateMaze();   // Генерируем лабиринт
        startSolving();   // Сразу начинаем его решать
    }

    // Метод генерации лабиринта
    private void generateMaze() {
        // Заполняем весь лабиринт стенами
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                maze[r][c] = WALL;
            }
        }

        // Генерируем путь с помощью рекурсии
        recursiveBacktracking(startRow, startCol);

        // Устанавливаем начальную и конечную клетки
        maze[startRow][startCol] = PATH;
        int exitRow = ROWS - 2;
        int exitCol = COLS - 2;
        maze[exitRow][exitCol] = EXIT;

        repaint(); // Перерисовываем лабиринт
    }

    // Рекурсивная генерация пути с помощью алгоритма "recursive backtracking"
    private void recursiveBacktracking(int r, int c) {
        // Возможные направления (вправо, вниз, влево, вверх)
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        shuffleArray(directions); // Перемешиваем порядок направлений

        maze[r][c] = PATH; // Текущую клетку делаем проходимой

        // Пытаемся двигаться во всех направлениях
        for (int[] d : directions) {
            int nr = r + d[0], nc = c + d[1];

            // Проверка, что новая клетка внутри лабиринта и ещё не посещена
            if (nr > 0 && nr < ROWS - 1 && nc > 0 && nc < COLS - 1 && maze[nr][nc] == WALL) {
                // Прокладываем проход между клетками
                maze[r + d[0] / 2][c + d[1] / 2] = PATH;

                // Рекурсивно продолжаем из новой клетки
                recursiveBacktracking(nr, nc);
            }
        }
    }

    // Метод перемешивания направлений (для случайности)
    private void shuffleArray(int[][] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    // Метод для запуска решения лабиринта (в отдельном потоке)
    private void startSolving() {
        new Thread(() -> {
            // Очистка предыдущего состояния лабиринта
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (maze[r][c] == VISITED || maze[r][c] == PLAYER || maze[r][c] == SOLUTION) {
                        maze[r][c] = PATH;
                    }
                }
            }

            // Запуск поиска пути от старта
            boolean solved = solveMaze(startRow, startCol);

            // Если найден путь, показать диалог с выбором
            if (solved) {
                int option = JOptionPane.showOptionDialog(this, "The path is found! What do you want to do?",
                        "Victory!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, new Object[]{"New Maze", "Exit"}, "New Maze");

                if (option == JOptionPane.YES_OPTION) {
                    generateMaze();   // Снова генерируем лабиринт
                    startSolving();   // Запускаем поиск снова
                } else {
                    System.exit(0);   // Завершаем программу
                }
            }
        }).start();
    }

    // Метод для рекурсивного поиска выхода
    private boolean solveMaze(int r, int c) {
        // Проверка выхода за границы и на стены
        if (r < 0 || c < 0 || r >= ROWS || c >= COLS || maze[r][c] == WALL || maze[r][c] == VISITED)
            return false;

        // Если достигли выхода — путь найден
        if (maze[r][c] == EXIT) {
            maze[r][c] = SOLUTION; // Отмечаем финиш как часть решения
            repaintDelay();
            return true;
        }

        maze[r][c] = PLAYER; // Временно обозначаем клетку как текущую позицию
        repaintDelay();      // Визуализируем

        maze[r][c] = VISITED; // Отмечаем клетку как посещённую

        // Рекурсивно пробуем все направления
        if (solveMaze(r - 1, c) || solveMaze(r + 1, c) || solveMaze(r, c - 1) || solveMaze(r, c + 1)) {
            maze[r][c] = SOLUTION; // Если путь найден — помечаем как часть пути
            repaintDelay();
            return true;
        }

        return false; // Если путь не найден
    }

    // Метод, обеспечивающий задержку и обновление экрана
    private void repaintDelay() {
        try {
            Thread.sleep(30); // Задержка для анимации
        } catch (InterruptedException ignored) {}
        repaint();
    }

    // Метод для отрисовки лабиринта
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Проход по всем клеткам
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                switch (maze[r][c]) {
                    case WALL -> g.setColor(Color.BLUE);      // Стены — синие
                    case PATH -> g.setColor(Color.WHITE);     // Проходимые клетки — белые
                    case VISITED -> g.setColor(Color.PINK);   // Посещённые — розовые
                    case PLAYER -> g.setColor(Color.RED);     // Текущая клетка поиска — красная
                    case EXIT -> g.setColor(Color.YELLOW);    // Выход — жёлтый
                    case SOLUTION -> g.setColor(Color.GREEN); // Верный путь — зелёный
                }

                // Рисуем квадрат-клетку
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    // Точка входа в программу
    public static void main(String[] args) {
        JFrame frame = new JFrame("Maze Solver"); // Создаём окно
        frame.add(new MazeGame());                // Добавляем панель с лабиринтом
        frame.pack();                             // Упаковываем
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Закрытие при выходе
        frame.setVisible(true);                   // Отображаем окно
    }
}
