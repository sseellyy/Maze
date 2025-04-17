---

## 🧩 Общее описание

Программа **генерирует случайный лабиринт и решает его**, находя путь от входа (в левом верхнем углу) до выхода (в правом нижнем углу). Всё отображается графически с помощью библиотеки **Swing**.
Это простая **игра-лабиринт**, где:
- Компьютер **строит лабиринт** случайным образом.
- Потом сам **ищет выход**.
- Всё это **рисуется на экране** (окно с квадратиками — стены, путь, человечек, выход).

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

---

## 🔷 Пошаговая работа кода

---

## 🧱 Основные штуки в коде

### 📏 Размеры
- Каждая клетка — 30 пикселей.
- Лабиринт — 15 на 15 клеток.

```java
private static final int CELL_SIZE = 30;
private static final int ROWS = 15;
private static final int COLS = 15;
```

---

## 📦 Клетки в лабиринте
Это как "легенда" — какие цвета что обозначают:
```java
WALL = 1;      // Синяя стена
PATH = 0;      // Белая клетка — по ней можно идти
PLAYER = 2;    // Красная — текущее место "поиска"
EXIT = 3;      // Жёлтая — выход из лабиринта
VISITED = 4;   // Розовая — где уже были
SOLUTION = 5;  // Зелёная — правильный путь до выхода
```

---

## 🧠 Что происходит шаг за шагом?

### 1. 🧱 Построй лабиринт
```java
generateMaze(); // Заполняет всё стенами и делает случайные пути
```

- Все клетки сначала — **стены**.
- Из стартовой точки (1,1) начинается **вырезание пути**.
- Случайно выбирается направление, и **прокапывается путь** рекурсией.
- Установка **выхода** — это почти внизу справа.

---

### 2. 🔄 Алгоритм генерации
```java
recursiveBacktracking()
```
- Это как "копатель", который случайно выбирает куда пойти и прокапывает путь.
- Он **перемешивает направления** и идёт туда, где ещё не был.
- Он "прыгает через клетку", чтобы не делать пути слишком узкими.

---

### 3. 🚶 Начинаем искать путь
```java
startSolving();
```
- Запускается **новый поток (параллельно)**, чтобы интерфейс не завис.
- Алгоритм начинает идти от старта и ищет жёлтую клетку (выход).
- Он пробует **вверх, вниз, влево, вправо**.

---

### 4. 🔁 Поиск пути
```java
solveMaze(r, c)
```
- Рекурсивно пробует пути.
- Если попадает в тупик — **откатывается** назад.
- Если находит выход — **отмечает зелёным путь**.

---

### 5. 🎨 Рисование на экране
```java
paintComponent(Graphics g)
```
- Каждая клетка рисуется в нужном цвете.
- Например:
  - `WALL -> BLUE`
  - `PATH -> WHITE`
  - `EXIT -> YELLOW`
  - `SOLUTION -> GREEN`
- Это вызывается каждый раз, когда нужно **перерисовать экран**.

---

### 6. 🪟 Окно приложения
```java
main()
```
- Создаёт **окно (JFrame)**.
- Вставляет в него панель с лабиринтом.
- Показывает окно на экране.

---

### 🎉 Когда выход найден
- Показывается **окошко с кнопками**:
  - "Новый лабиринт" — всё перезапускается.
  - "Выход" — программа закрывается.

---

## 💡 Пример визуализации

```
█████████████████
█   █     █     █
█ █ █ ███ █ ███ █
█ █     █     █ █
█ ███ █ █████ █ █
█     █       █ █
███████████ ███ █
█     █     █   █
█ ███ █ ███ █ ███
█ █   █ █ █ █   █
█ ███ ███ ███ █ █
█   █     █   █ █
███████████ █████
█             E █
█████████████████

```

(Где `E` — выход, `█` — стены, пробелы — путь)

---

## 🔚 Вывод
**Простой алгоритм + визуализация = магия!**
- Лабиринт — случайный.
- Компьютер сам находит выход.
- Ты видишь всё прямо на экране.

---
