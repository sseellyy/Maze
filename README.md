---

## 🧩 Общее описание

Программа **генерирует случайный лабиринт и решает его**, находя путь от входа (в левом верхнем углу) до выхода (в правом нижнем углу). Всё отображается графически с помощью библиотеки **Swing**.

---

## 🔧 Структура программы

Код состоит из одного класса: `MazeGame`, который:

- расширяет `JPanel` — графическая панель для рисования;
- использует массив `maze` для хранения состояния лабиринта;
- создаёт графическое окно (`JFrame`) и запускает алгоритмы генерации и решения лабиринта.

---

## 🧱 Основные переменные

- `maze[][]` — двумерный массив (`int[ROWS][COLS]`), хранящий тип каждой клетки:
  - `0` — путь (можно идти);
  - `1` — стена;
  - `2` — текущая позиция алгоритма;
  - `3` — выход;
  - `4` — посещённая клетка;
  - `5` — клетка, входящая в правильный путь.

---

## 🛠️ Основные методы

### `generateMaze()`
- Заполняет весь лабиринт стенами.
- Запускает **рекурсивный метод `recursiveBacktracking()`** для "высверливания" проходов.
- Устанавливает старт и финиш.

### `recursiveBacktracking(int r, int c)` — **Рекурсивный метод генерации лабиринта**
- Рекурсивно создаёт проходы в случайных направлениях.
- Продвигается на 2 клетки вперёд, чтобы не делать лабиринт слишком узким.
- Между клетками "прокладывает путь".
- Использует **случайный порядок направлений** для уникальности каждой генерации.

### `startSolving()`
- Запускает **рекурсивный поиск пути (`solveMaze`)** в отдельном потоке.
- После нахождения пути — предлагает выбрать: сгенерировать новый лабиринт или выйти.

### `solveMaze(int r, int c)` — **Рекурсивный метод поиска пути (DFS)**
- Реализует алгоритм поиска в глубину (DFS).
- Отмечает текущую клетку, проверяет, не достиг ли выхода.
- Пробует пойти вверх, вниз, влево, вправо.
- Если путь найден — помечает его зелёным (`SOLUTION`).

### `repaintDelay()`
- Делает задержку в 30 мс, чтобы отрисовать движение алгоритма шаг за шагом.

### `paintComponent(Graphics g)`
- Отвечает за отрисовку лабиринта.
- Каждая клетка закрашивается в зависимости от её значения:
  - стена — синий,
  - путь — белый,
  - посещено — розовый,
  - путь решения — зелёный,
  - текущая клетка — красный,
  - выход — жёлтый.

### `main(String[] args)`
- Запускает приложение: создаёт окно и отображает панель с лабиринтом.

---

## 🔄 Как работают рекурсивные методы?

### Генерация (`recursiveBacktracking`)
- Программа идёт от стартовой точки и **рекурсивно "прокапывает" проходы**, двигаясь в случайных направлениях.
- После каждого удачного хода запускается та же функция из новой клетки.
- Так создаётся сложная, но связная сеть путей.

### Решение (`solveMaze`)
- Программа **рекурсивно исследует** соседние клетки:
  - Если клетка проходимая и не была посещена — идём туда.
  - Если нашли выход — возвращаем `true` и **отмечаем путь зелёным**.
  - Если путь не найден — возвращаемся назад (бэктрекинг).

---

## 📌 Вывод

Программа использует **два рекурсивных метода**:
- один — для **генерации лабиринта**;
- другой — для **поиска пути**.

Каждый запуск создаёт новый уникальный лабиринт, показывает путь в реальном времени и даёт выбор продолжения или выхода. Алгоритмы просты, но эффективны — и прекрасно демонстрируют основы рекурсии, графики и логики в Java.
