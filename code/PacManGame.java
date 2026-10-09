import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class PacManGame extends JFrame {

   
    // COLORS (same palette as the HTML / Tailwind version)
   
    static final Color BG = new Color(0x050508);
    static final Color SLATE950 = new Color(0x020617);
    static final Color SLATE900 = new Color(0x0F172A);
    static final Color SLATE800 = new Color(0x1E293B);
    static final Color SLATE600 = new Color(0x475569);
    static final Color SLATE500 = new Color(0x64748B);
    static final Color SLATE400 = new Color(0x94A3B8);
    static final Color SLATE300 = new Color(0xCBD5E1);
    static final Color SLATE200 = new Color(0xE2E8F0);
    static final Color GRAY300 = new Color(0xD1D5DB);
    static final Color GRAY400 = new Color(0x9CA3AF);
    static final Color PURPLE950 = new Color(0x3B0764);
    static final Color PURPLE900 = new Color(0x581C87);
    static final Color PURPLE600 = new Color(0x9333EA);
    static final Color PURPLE500 = new Color(0xA855F7);
    static final Color PURPLE400 = new Color(0xC084FC);
    static final Color PURPLE200 = new Color(0xE9D5FF);
    static final Color YELLOW500 = new Color(0xEAB308);
    static final Color YELLOW400 = new Color(0xFACC15);
    static final Color YELLOW300 = new Color(0xFDE047);
    static final Color CYAN500 = new Color(0x06B6D4);
    static final Color CYAN400 = new Color(0x22D3EE);
    static final Color CYAN300 = new Color(0x67E8F9);
    static final Color CYAN200 = new Color(0xA5F3FC);
    static final Color CYAN950 = new Color(0x083344);
    static final Color PINK = new Color(0xFF0080);
    static final Color PINK500 = new Color(0xEC4899);
    static final Color PINK400 = new Color(0xF472B6);
    static final Color PINK200 = new Color(0xFBCFE8);
    static final Color PINK950 = new Color(0x500724);
    static final Color GREEN400 = new Color(0x4ADE80);
    static final Color GREEN950 = new Color(0x052E16);
    static final Color RED500 = new Color(0xEF4444);
    static final Color RED200 = new Color(0xFECACA);
    static final Color RED950 = new Color(0x450A0A);
    static final Color BLUE600 = new Color(0x2563EB);
    static final Color MENU_BOX_BG = new Color(0x0C1426); // slate-900 @ 80%
    static final Color MENU_BTN_BG = new Color(0x280C4B); // purple-950 @ 60%

   
    // MAP 1: 1 wall, 0 food, 2 power pellet, 3 empty, 4 ghost house
   
    static final int[][] GOOGLE_PACMAN_MAP = {

            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1 },
            { 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 2, 1 },
            { 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0,
                    1, 1, 1, 1, 0, 0, 1 },
            { 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
                    0, 0, 0, 1, 0, 0, 1 },
            { 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0,
                    1, 0, 0, 1, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0,
                    1, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0,
                    1, 0, 1, 1, 1, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0,
                    1, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0,
                    1, 0, 0, 1, 0, 0, 1 },
            { 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
                    0, 0, 0, 1, 0, 0, 1 },
            { 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0,
                    1, 1, 1, 1, 0, 0, 1 },
            { 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 2, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1 }

    };

    // MAP 2 : 1 wall, 0 food, 2 power pellet, 3 empty, 4 ghost house

    static final int[][] MAP_1 = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1 },
            { 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 2, 1 },
            { 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1,
                    0, 0, 1 },
            { 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,
                    0, 0, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1,
                    1, 0, 1 },
            { 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
                    1, 0, 1 },
            { 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0,
                    1, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 4, 4, 4, 4, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1,
                    0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1,
                    1, 0, 1 },
            { 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1,
                    0, 0, 1 },
            { 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1,
                    0, 0, 1 },
            { 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 2, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1 }
    };

     // MAP 3: (text map: # wall, . food, o power, space empty)

    static int[][] parseMap(String[] rows) {
        int[][] m = new int[rows.length][rows[0].length()];
        for (int r = 0; r < rows.length; r++)
            for (int c = 0; c < rows[r].length(); c++) {
                char ch = rows[r].charAt(c);
                m[r][c] = ch == '#' ? 1 : ch == '.' ? 0 : ch == 'o' ? 2 : 3;
            }
        return m;
    }

    static final int[][] MAP_3 = parseMap(new String[] {
            "##########################################",
            "#o......................................o#",
            "#.######.###########..###########.######.#",
            "#........................................#",
            "#.#.##########.############.##########.#.#",
            "#........................................#",
            "#.######.###########..###########.######.#",
            "#........................................#",
            "#.#.##########.############.##########.#.#",
            "#........................................#",
            "#.######.###########..###########.######.#",
            "#o......................................o#",
            "##########################################" });

    // ---------------- map list + per-map settings (same order everywhere)
    // ----------------
    // Map 1 = MAP_1, Map 2 = your original GOOGLE_PACMAN_MAP, Map 3 = MAP_3
    static final int[][][] MAPS = { MAP_1, GOOGLE_PACMAN_MAP, MAP_3 };
    static final String[] MAP_NAMES = { "NEON ARCADE", "CSE - 4", "ZIGZAG HALLS" };
    static final int[] MAP_TILE = { 20, 18, 18 };
    static final int[] MAP_SPRITE = { 16, 15, 15 };
    static final int[][] PAC_SPAWN = { { 18, 13 }, { 18, 9 }, { 18, 9 } }; // {col,row}
    static final int[][][] GHOST_SPAWN = {
            { { 15, 7 }, { 16, 7 }, { 17, 7 }, { 18, 7 } },
            { { 16, 5 }, { 17, 5 }, { 18, 5 }, { 19, 5 } },
            { { 16, 5 }, { 17, 5 }, { 18, 5 }, { 19, 5 } } };
    static int selectedMap = 0;
    static final Preferences PREFS = Preferences.userNodeForPackage(PacManGame.class);
    static String playerName = PREFS.get("pacman_player_name", "PLAYER");
    static String selectedDifficulty = normalizeDifficulty(PREFS.get("pacman_difficulty", "NORMAL"));
    static boolean resultRecorded = false;

    static String normalizeDifficulty(String difficulty) {
        return "EASY".equals(difficulty) || "HARD".equals(difficulty) ? difficulty : "NORMAL";
    }

    // these change with the selected map
    static int TILE_SIZE = 20;
    static int SPRITE_SIZE = 16;
    static int COLS = 38, ROWS = 15;
    static final int CANVAS_W = 760, CANVAS_H = 300; // only the panel's preferred size
    static final int CANVAS_PAD = 12;

    static int mapW() {
        return COLS * TILE_SIZE;
    }

    static int mapH() {
        return ROWS * TILE_SIZE;
    }

    static void applyMap(int idx) {
        selectedMap = idx;
        TILE_SIZE = MAP_TILE[idx];
        SPRITE_SIZE = MAP_SPRITE[idx];
        ROWS = MAPS[idx].length;
        COLS = MAPS[idx][0].length;
    }

   
    // GAME STATE
   
    static int score = 0;
    static int highScore;
    static int lives = 3;
    static boolean gameOver = false;
    static boolean gamePaused = false;
    static boolean gameStarted = false;
    static int frightenTimer = 0;
    static int[][] currentMap = copyMap();

    static Path leaderboardFile() {
        return new File(System.getProperty("user.home"), ".pacman-classic/leaderboard.tsv").toPath();
    }

    static class RunRecord {
        final String name;
        final int score;
        final int map;
        final String difficulty;
        final int mazesCleared;
        final int hearts;
        final LocalDate date;

        RunRecord(String name, int score, int map, String difficulty, int mazesCleared, int hearts, LocalDate date) {
            this.name = name;
            this.score = score;
            this.map = map;
            this.difficulty = difficulty;
            this.mazesCleared = mazesCleared;
            this.hearts = hearts;
            this.date = date;
        }
    }

    static class LeaderboardStore {
        static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MM/dd/yy");

        static List<RunRecord> load() throws IOException {
            Path file = leaderboardFile();
            List<RunRecord> records = new ArrayList<>();
            if (!Files.exists(file))
                return records;

            int lineNumber = 0;
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                lineNumber++;
                String[] fields = line.split("\\t", -1);
                try {
                    if (fields.length != 7)
                        throw new IllegalArgumentException("expected 7 fields");
                    RunRecord record = new RunRecord(decode(fields[0]), Integer.parseInt(fields[1]),
                            Integer.parseInt(fields[2]), decode(fields[3]), Integer.parseInt(fields[4]),
                            Integer.parseInt(fields[5]), LocalDate.parse(fields[6]));
                    if (record.map < 0 || record.map >= MAP_NAMES.length || record.score < 0
                            || record.mazesCleared < 0 || record.hearts < 0)
                        throw new IllegalArgumentException("invalid score values");
                    records.add(record);
                } catch (RuntimeException ex) {
                    System.err.println("Ignoring invalid leaderboard entry on line " + lineNumber + ": "
                            + ex.getMessage());
                }
            }
            sortAndLimit(records);
            return records;
        }

        static void save(List<RunRecord> records) throws IOException {
            Path file = leaderboardFile();
            Files.createDirectories(file.getParent());
            Path temporary = Files.createTempFile(file.getParent(), "leaderboard-", ".tmp");
            try {
                List<String> lines = new ArrayList<>();
                for (RunRecord record : records) {
                    lines.add(encode(record.name) + "\t" + record.score + "\t" + record.map + "\t"
                            + encode(record.difficulty) + "\t" + record.mazesCleared + "\t" + record.hearts + "\t"
                            + record.date);
                }
                Files.write(temporary, lines, StandardCharsets.UTF_8);
                try {
                    Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException ex) {
                    Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporary);
            }
        }

        static void sortAndLimit(List<RunRecord> records) {
            Collections.sort(records, Comparator.comparingInt((RunRecord record) -> record.score).reversed()
                    .thenComparing(record -> record.date, Comparator.reverseOrder()));
            if (records.size() > 10)
                records.subList(10, records.size()).clear();
        }

        static String encode(String value) {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        }

        static String decode(String value) {
            return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
        }
    }

    static LeaderboardPanel leaderboardPanel;

    // overlay (READY / PAUSED / GAME OVER / VICTORY)
    static boolean overlayVisible = false;
    static String overlayTitle = "READY!";
    static String overlaySubtitle = "PRESS ANY KEY TO START";

    // sprites
    static BufferedImage imgFood, imgPower, imgScared, imgScaredFlash, imgHeart;
    static BufferedImage imgPacUp, imgPacDown, imgPacLeft, imgPacRight;
    static BufferedImage imgRed, imgPink, imgBlue, imgOrange;
    static BufferedImage[] devPhotos = new BufferedImage[3];

    static int[][] copyMap() {
        int[][] src = MAPS[selectedMap];
        int[][] m = new int[src.length][];
        for (int i = 0; i < m.length; i++)
            m[i] = src[i].clone();
        return m;
    }

   
    // PAC-MAN
   
    static class Pac {
        double x, y;
        double radius = TILE_SIZE / 2.0 - 2;
        double speed = 2;
        int dirX = 0, dirY = 0, nextDirX = 0, nextDirY = 0;
        String facing = "right";

        void reset() {
            x = PAC_SPAWN[selectedMap][0] * TILE_SIZE + TILE_SIZE / 2.0;
            y = PAC_SPAWN[selectedMap][1] * TILE_SIZE + TILE_SIZE / 2.0;
            radius = TILE_SIZE / 2.0 - 2;
            dirX = dirY = nextDirX = nextDirY = 0;
            facing = "right";
        }
    }

    static final Pac pacman = new Pac();

   
    // GHOSTS
    // FIX: ghosts move tile-to-tile and always snap exactly onto the centre
    // of the next tile before choosing a new direction, so they can never
    // freeze (the old code waited for an exact pixel match that almost never
    // happened at speed 1.6).
   
    static class Ghost {
        final BufferedImage sprite;
        final Color fallback;
        final int initialX, initialY;
        final double speed;
        int col, row, targetCol, targetRow, dirX, dirY;
        double x, y;
        boolean frightened;

        Ghost(BufferedImage sprite, Color fallback, int gx, int gy) {
            this.sprite = sprite;
            this.fallback = fallback;
            this.initialX = gx;
            this.initialY = gy;
            this.speed = ghostSpeed();
            reset();
        }

        void reset() {
            col = initialX;
            row = initialY;
            x = col * TILE_SIZE + TILE_SIZE / 2.0;
            y = row * TILE_SIZE + TILE_SIZE / 2.0;
            targetCol = col;
            targetRow = row;
            dirX = 0;
            dirY = -1;
            frightened = frightenTimer > 0;
        }

        boolean isWalkable(int tileCol, int tileRow) {
            return tileRow >= 0 && tileRow < ROWS && tileCol >= 0 && tileCol < COLS
                    && currentMap[tileRow][tileCol] != 1;
        }

        void chooseDirection() {
            int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
            int[][] distance = new int[ROWS][COLS];
            for (int[] rowDistances : distance)
                Arrays.fill(rowDistances, -1);

            int pacCol = (int) (pacman.x / TILE_SIZE);
            int pacRow = (int) (pacman.y / TILE_SIZE);
            if (!isWalkable(pacCol, pacRow))
                return;
            ArrayDeque<int[]> queue = new ArrayDeque<>();
            distance[pacRow][pacCol] = 0;
            queue.add(new int[] { pacCol, pacRow });
            while (!queue.isEmpty()) {
                int[] cell = queue.removeFirst();
                for (int[] dir : dirs) {
                    int nextCol = cell[0] + dir[0];
                    int nextRow = cell[1] + dir[1];
                    if (isWalkable(nextCol, nextRow) && distance[nextRow][nextCol] == -1) {
                        distance[nextRow][nextCol] = distance[cell[1]][cell[0]] + 1;
                        queue.addLast(new int[] { nextCol, nextRow });
                    }
                }
            }

            int[] chosen = null;
            int bestDistance = frightened ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            for (int pass = 0; pass < 2 && chosen == null; pass++) {
                for (int[] dir : dirs) {
                    int nextCol = col + dir[0];
                    int nextRow = row + dir[1];
                    if (!isWalkable(nextCol, nextRow) || distance[nextRow][nextCol] < 0)
                        continue;

                    boolean overlapsGhost = false;
                    for (Ghost other : ghosts) {
                        if (other == this)
                            continue;
                        boolean occupiesNext = (other.col == nextCol && other.row == nextRow)
                                || (other.targetCol == nextCol && other.targetRow == nextRow);
                        if (occupiesNext) {
                            overlapsGhost = true;
                            break;
                        }
                    }
                    if (pass == 0 && overlapsGhost)
                        continue;

                    int candidateDistance = distance[nextRow][nextCol];
                    if (chosen == null || (frightened ? candidateDistance > bestDistance
                            : candidateDistance < bestDistance)) {
                        chosen = dir;
                        bestDistance = candidateDistance;
                    }
                }
            }
            if (chosen == null) {
                dirX = dirY = 0;
                targetCol = col;
                targetRow = row;
                return;
            }
            dirX = chosen[0];
            dirY = chosen[1];
            targetCol = col + chosen[0];
            targetRow = row + chosen[1];
        }

        void update() {
            if (!isWalkable(col, row) || !isWalkable(targetCol, targetRow)
                    || !Double.isFinite(x) || !Double.isFinite(y)
                    || x < 0 || y < 0 || x >= mapW() || y >= mapH()) {
                reset();
                return;
            }
            double tx = targetCol * TILE_SIZE + TILE_SIZE / 2.0;
            double ty = targetRow * TILE_SIZE + TILE_SIZE / 2.0;
            double dist = Math.hypot(tx - x, ty - y);
            if (dist <= speed) {
                x = tx;
                y = ty;
                col = targetCol;
                row = targetRow;
                chooseDirection();
            } else {
                double nextX = x + dirX * speed;
                double nextY = y + dirY * speed;
                for (Ghost other : ghosts) {
                    if (other != this && Math.hypot(nextX - other.x, nextY - other.y) < SPRITE_SIZE - 2) {
                        targetCol = col;
                        targetRow = row;
                        dirX = Double.compare(col * TILE_SIZE + TILE_SIZE / 2.0, x);
                        dirY = Double.compare(row * TILE_SIZE + TILE_SIZE / 2.0, y);
                        return;
                    }
                }
                x = nextX;
                y = nextY;
            }
        }

        void draw(Graphics2D g) {
            BufferedImage img;
            if (frightened) {
                boolean flashing = frightenTimer < 117 && (frightenTimer / 12) % 2 == 0;
                img = (flashing && imgScaredFlash != null) ? imgScaredFlash : imgScared;
            } else {
                img = sprite;
            }
            if (img != null) {
                drawSprite(g, img, x, y, SPRITE_SIZE);
            } else {
                // fallback if the PNG files are missing
                g.setColor(frightened ? Color.BLUE : fallback);
                g.fill(new Ellipse2D.Double(x - 8, y - 8, 16, 16));
            }
        }
    }

    static List<Ghost> ghosts = new ArrayList<>();

    static void drawSprite(Graphics2D g, BufferedImage img, double cx, double cy, int size) {
        g.drawImage(img, (int) Math.round(cx - size / 2.0), (int) Math.round(cy - size / 2.0), size, size, null);
    }

   
    // GAME LOGIC
   
    static boolean isWallCollision(double x, double y) {
        double radius = pacman.radius;
        int firstCol = (int) Math.floor((x - radius) / TILE_SIZE);
        int lastCol = (int) Math.floor((x + radius) / TILE_SIZE);
        int firstRow = (int) Math.floor((y - radius) / TILE_SIZE);
        int lastRow = (int) Math.floor((y + radius) / TILE_SIZE);
        if (firstCol < 0 || firstRow < 0 || lastCol >= COLS || lastRow >= ROWS)
            return true;
        for (int row = firstRow; row <= lastRow; row++)
            for (int col = firstCol; col <= lastCol; col++)
                if (currentMap[row][col] == 1)
                    return true;
        return false;
    }

    static double nearestTileCenter(double position) {
        return Math.round((position - TILE_SIZE / 2.0) / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2.0;
    }

    static void setPacmanDirection(int dx, int dy) {
        pacman.dirX = dx;
        pacman.dirY = dy;
        if (dx == 1)
            pacman.facing = "right";
        else if (dx == -1)
            pacman.facing = "left";
        else if (dy == 1)
            pacman.facing = "down";
        else if (dy == -1)
            pacman.facing = "up";
    }

    static boolean canMovePacmanFrom(double x, double y, int dx, int dy) {
        return !isWallCollision(x, y) && !isWallCollision(x + dx * pacman.speed, y + dy * pacman.speed);
    }

    static boolean canTurnPacmanAt(double x, double y, int dx, int dy) {
        int col = (int) (x / TILE_SIZE);
        int row = (int) (y / TILE_SIZE);
        int nextCol = col + dx;
        int nextRow = row + dy;
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS
                || nextRow < 0 || nextRow >= ROWS || nextCol < 0 || nextCol >= COLS
                || currentMap[row][col] == 1 || currentMap[nextRow][nextCol] == 1)
            return false;
        return canMovePacmanFrom(x, y, dx, dy);
    }

    static boolean tryQueuedPacmanTurn() {
        int requestedX = pacman.nextDirX;
        int requestedY = pacman.nextDirY;
        boolean movingHorizontally = pacman.dirX != 0;
        double alongPosition = movingHorizontally ? pacman.x : pacman.y;
        double center = nearestTileCenter(alongPosition);
        double tolerance = TILE_SIZE * 0.30;
        double bestDistance = Double.POSITIVE_INFINITY;
        double turnPosition = center;
        boolean foundTurn = false;

        for (int offset = -1; offset <= 1; offset++) {
            double candidate = center + offset * TILE_SIZE;
            double forwardDistance = (candidate - alongPosition) * (movingHorizontally ? pacman.dirX : pacman.dirY);
            if (forwardDistance < -tolerance || forwardDistance > pacman.speed)
                continue;

            double turnX = movingHorizontally ? candidate : nearestTileCenter(pacman.x);
            double turnY = movingHorizontally ? nearestTileCenter(pacman.y) : candidate;
            if (!canTurnPacmanAt(turnX, turnY, requestedX, requestedY))
                continue;

            double distance = Math.abs(forwardDistance);
            if (distance < bestDistance) {
                bestDistance = distance;
                turnPosition = candidate;
                foundTurn = true;
            }
        }

        if (!foundTurn)
            return false;

        double forwardDistance = (turnPosition - alongPosition) * (movingHorizontally ? pacman.dirX : pacman.dirY);
        if (movingHorizontally) {
            pacman.x = turnPosition;
            pacman.y = nearestTileCenter(pacman.y);
        } else {
            pacman.x = nearestTileCenter(pacman.x);
            pacman.y = turnPosition;
        }
        setPacmanDirection(requestedX, requestedY);
        pacman.nextDirX = pacman.nextDirY = 0;

        double remaining = forwardDistance > 0 ? pacman.speed - forwardDistance : pacman.speed;
        double nextX = pacman.x + pacman.dirX * remaining;
        double nextY = pacman.y + pacman.dirY * remaining;
        if (!isWallCollision(nextX, nextY)) {
            pacman.x = nextX;
            pacman.y = nextY;
        }
        return true;
    }

    static boolean alignPacmanForUpcomingTurn() {
        boolean movingHorizontally = pacman.dirX != 0;
        double alongPosition = movingHorizontally ? pacman.x : pacman.y;
        double center = nearestTileCenter(alongPosition);
        double lookAhead = TILE_SIZE * 0.30 + pacman.speed;
        double bestDistance = Double.POSITIVE_INFINITY;
        boolean foundTurn = false;

        for (int offset = -1; offset <= 1; offset++) {
            double candidate = center + offset * TILE_SIZE;
            double forwardDistance = (candidate - alongPosition) * (movingHorizontally ? pacman.dirX : pacman.dirY);
            if (forwardDistance <= pacman.speed || forwardDistance > lookAhead)
                continue;

            double turnX = movingHorizontally ? candidate : nearestTileCenter(pacman.x);
            double turnY = movingHorizontally ? nearestTileCenter(pacman.y) : candidate;
            if (!canTurnPacmanAt(turnX, turnY, pacman.nextDirX, pacman.nextDirY))
                continue;

            if (forwardDistance < bestDistance) {
                bestDistance = forwardDistance;
                foundTurn = true;
            }
        }

        if (!foundTurn)
            return false;

        double alongX = pacman.x + pacman.dirX * pacman.speed;
        double alongY = pacman.y + pacman.dirY * pacman.speed;
        if (isWallCollision(alongX, alongY)) {
            pacman.dirX = pacman.dirY = 0;
            return true;
        }
        pacman.x = alongX;
        pacman.y = alongY;

        double centerX = movingHorizontally ? pacman.x : nearestTileCenter(pacman.x);
        double centerY = movingHorizontally ? nearestTileCenter(pacman.y) : pacman.y;
        double adjustX = movingHorizontally ? 0 : Math.max(-pacman.speed,
                Math.min(pacman.speed, centerX - pacman.x));
        double adjustY = movingHorizontally ? Math.max(-pacman.speed,
                Math.min(pacman.speed, centerY - pacman.y)) : 0;
        double alignedX = pacman.x + adjustX;
        double alignedY = pacman.y + adjustY;
        if (!isWallCollision(alignedX, alignedY)) {
            pacman.x = alignedX;
            pacman.y = alignedY;
        }
        return true;
    }

    static void updatePacmanMovement() {
        int requestedX = pacman.nextDirX;
        int requestedY = pacman.nextDirY;
        if (requestedX != 0 || requestedY != 0) {
            boolean sameDirection = requestedX == pacman.dirX && requestedY == pacman.dirY;
            boolean moving = pacman.dirX != 0 || pacman.dirY != 0;
            boolean reverseDirection = moving && requestedX == -pacman.dirX && requestedY == -pacman.dirY;
            if (sameDirection) {
                pacman.nextDirX = pacman.nextDirY = 0;
            } else if (reverseDirection) {
                if (canMovePacmanFrom(pacman.x, pacman.y, requestedX, requestedY)) {
                    setPacmanDirection(requestedX, requestedY);
                    pacman.nextDirX = pacman.nextDirY = 0;
                }
            } else if (!moving) {
                if (canMovePacmanFrom(pacman.x, pacman.y, requestedX, requestedY)) {
                    setPacmanDirection(requestedX, requestedY);
                    pacman.nextDirX = pacman.nextDirY = 0;
                } else {
                    double alignedX = nearestTileCenter(pacman.x);
                    double alignedY = nearestTileCenter(pacman.y);
                    double tolerance = TILE_SIZE * 0.30;
                    if (Math.abs(alignedX - pacman.x) <= tolerance
                            && Math.abs(alignedY - pacman.y) <= tolerance
                            && canTurnPacmanAt(alignedX, alignedY, requestedX, requestedY)) {
                        pacman.x = alignedX;
                        pacman.y = alignedY;
                        setPacmanDirection(requestedX, requestedY);
                        pacman.nextDirX = pacman.nextDirY = 0;
                    }
                }
            } else {
                if (tryQueuedPacmanTurn())
                    return;
                if (alignPacmanForUpcomingTurn())
                    return;
            }
        }

        double nextX = pacman.x + pacman.dirX * pacman.speed;
        double nextY = pacman.y + pacman.dirY * pacman.speed;
        if (!isWallCollision(nextX, nextY)) {
            pacman.x = nextX;
            pacman.y = nextY;
        } else {
            pacman.dirX = pacman.dirY = 0;
        }
    }

    static void updateGame() {
        if (gameOver || gamePaused || !gameStarted)
            return;

        if (frightenTimer > 0) {
            frightenTimer--;
            if (frightenTimer == 0)
                for (Ghost g : ghosts)
                    g.frightened = false;
        }

        updatePacmanMovement();

        // eat food & power pellets
        int gx = (int) Math.floor(pacman.x / TILE_SIZE);
        int gy = (int) Math.floor(pacman.y / TILE_SIZE);
        if (gy >= 0 && gy < ROWS && gx >= 0 && gx < COLS) {
            if (currentMap[gy][gx] == 0) {
                currentMap[gy][gx] = 3;
                score += 10;
                Sound.waka();
            } else if (currentMap[gy][gx] == 2) {
                currentMap[gy][gx] = 3;
                score += 50;
                frightenTimer = 350;
                for (Ghost g : ghosts)
                    g.frightened = true;
                Sound.powerPellet();
            }
        }

        // high score
        if (score > highScore) {
            highScore = score;
            PREFS.putInt("pacman_high_score", highScore);
        }

        // ghosts & collisions
        for (Ghost ghost : ghosts) {
            if (gameOver)
                break;
            ghost.update();
            double dist = Math.hypot(pacman.x - ghost.x, pacman.y - ghost.y);
            if (dist < TILE_SIZE - 4) {
                if (ghost.frightened) {
                    Sound.eatGhost();
                    score += 200;
                    ghost.reset();
                } else {
                    Sound.death();
                    lives--;
                    if (lives <= 0) {
                        finishGame(false);
                    } else {
                        pacman.reset();
                        for (Ghost g : ghosts)
                            g.reset();
                    }
                }
            }
        }

        // win condition
        if (!gameOver) {
            boolean left = false;
            for (int r = 0; r < ROWS && !left; r++)
                for (int c = 0; c < COLS; c++)
                    if (currentMap[r][c] == 0 || currentMap[r][c] == 2) {
                        left = true;
                        break;
                    }
            if (!left) {
                finishGame(true);
            }
        }
    }

    static double ghostSpeed() {
        switch (selectedDifficulty) {
            case "EASY":
                return 1.3;
            case "HARD":
                return 1.9;
            default:
                return 1.6;
        }
    }

    static double pacmanSpeed() {
        switch (selectedDifficulty) {
            case "EASY":
                return 1.8;
            case "HARD":
                return 2.2;
            default:
                return 2.0;
        }
    }

    static void finishGame(boolean victory) {
        gameOver = true;
        if (!resultRecorded) {
            resultRecorded = true;
            if (score > highScore) {
                highScore = score;
                PREFS.putInt("pacman_high_score", highScore);
            }
            RunRecord record = new RunRecord(playerName, score, selectedMap, selectedDifficulty,
                    victory ? 1 : 0, lives, LocalDate.now());
            try {
                List<RunRecord> records = LeaderboardStore.load();
                records.add(record);
                LeaderboardStore.sortAndLimit(records);
                LeaderboardStore.save(records);
                if (leaderboardPanel != null)
                    leaderboardPanel.refresh();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "The game result could not be saved:\n" + ex.getMessage(),
                        "Leaderboard save error", JOptionPane.ERROR_MESSAGE);
            }
        }
        showOverlay(victory ? "VICTORY!" : "GAME OVER",
                victory ? "YOU CLEARED THE MAZE!" : "FINAL SCORE: " + score);
    }

    static void startNewGame() {
        applyMap(selectedMap);
        currentMap = copyMap();
        score = 0;
        lives = 3;
        gameOver = false;
        gamePaused = false;
        gameStarted = true;
        resultRecorded = false;
        pacman.speed = pacmanSpeed();
        frightenTimer = 0;
        pacman.reset();
        ghosts = new ArrayList<>();
        int[][] gs = GHOST_SPAWN[selectedMap];
        ghosts.add(new Ghost(imgRed, new Color(0xFF0000), gs[0][0], gs[0][1]));
        ghosts.add(new Ghost(imgPink, new Color(0xFFB8FF), gs[1][0], gs[1][1]));
        ghosts.add(new Ghost(imgBlue, new Color(0x00FFFF), gs[2][0], gs[2][1]));
        ghosts.add(new Ghost(imgOrange, new Color(0xFFB852), gs[3][0], gs[3][1]));
        hideOverlay();
    }

    static CanvasPanel canvasPanel;
    String leaderboardReturnCard = "menu";

    static void showOverlay(String title, String subtitle) {
        overlayTitle = title;
        overlaySubtitle = subtitle;
        overlayVisible = true;
        if (canvasPanel != null)
            canvasPanel.syncOverlay();
    }

    static void hideOverlay() {
        overlayVisible = false;
        if (canvasPanel != null)
            canvasPanel.syncOverlay();
    }

    static void togglePause(String resumeText) {
        gamePaused = !gamePaused;
        if (gamePaused)
            showOverlay("PAUSED", resumeText);
        else
            hideOverlay();
    }

   
    // ASSETS
   
    static BufferedImage loadImage(String name) {
        try {
            File f = new File("images", name);
            if (f.exists())
                return ImageIO.read(f);
            InputStream in = PacManGame.class.getResourceAsStream("/images/" + name);
            if (in != null)
                return ImageIO.read(in);
        } catch (Exception ignored) {
        }
        return null;
    }

    static void loadAssets() {
        imgRed = loadImage("redGhost.png");
        imgPink = loadImage("pinkGhost.png");
        imgBlue = loadImage("blueGhost.png");
        imgOrange = loadImage("orangeGhost.png");
        imgScared = loadImage("scaredGhost.png");
        imgPacUp = loadImage("pacmanUp.png");
        imgPacDown = loadImage("pacmanDown.png");
        imgPacLeft = loadImage("pacmanLeft.png");
        imgPacRight = loadImage("pacmanRight.png");
        imgFood = loadImage("cherry.png");
        imgPower = loadImage("powerFood.png");
        imgHeart = loadImage("heart.png");
        for (int i = 0; i < 3; i++)
            devPhotos[i] = loadImage("dev" + (i + 1) + ".png");

        // frightened "flash" frame: blue -> white, face -> red

        if (imgScared != null) {
            imgScaredFlash = new BufferedImage(imgScared.getWidth(), imgScared.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            for (int yy = 0; yy < imgScared.getHeight(); yy++) {
                for (int xx = 0; xx < imgScared.getWidth(); xx++) {
                    int argb = imgScared.getRGB(xx, yy);
                    int a = (argb >>> 24) & 0xFF;
                    if (a == 0)
                        continue;
                    int r = (argb >> 16) & 0xFF, b = argb & 0xFF;
                    imgScaredFlash.setRGB(xx, yy, (b > 200 && r < 80) ? 0xFFFFFFFF : 0xFFFF0000);
                }
            }
        }
        highScore = PREFS.getInt("pacman_high_score", 0);
    }

   
    // FONTS (optional TTF files, otherwise a monospaced fallback)
   
    static Font pixelBase, vtBase;
    static boolean fontsLoaded = false;

    static Font readFont(String name) {
        try {
            File f = new File("fonts", name);
            if (f.exists())
                return Font.createFont(Font.TRUETYPE_FONT, f);
            InputStream in = PacManGame.class.getResourceAsStream("/fonts/" + name);
            if (in != null)
                return Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (Exception ignored) {
        }
        return null;
    }

    static void loadFonts() {
        if (fontsLoaded)
            return;
        fontsLoaded = true;
        pixelBase = readFont("PressStart2P-Regular.ttf");
        vtBase = readFont("VT323-Regular.ttf");
    }

    /** Press Start 2P (font-pixel) */
    static Font pixel(float size) {
        loadFonts();
        return pixelBase != null ? pixelBase.deriveFont(size) : new Font(Font.MONOSPACED, Font.BOLD, Math.round(size));
    }

    /** VT323 (font-vt) */
    static Font vt(float size) {
        loadFonts();
        return vtBase != null ? vtBase.deriveFont(size)
                : new Font(Font.MONOSPACED, Font.PLAIN, Math.round(size * 0.8f));
    }

   
    // SOUND (synthesised retro effects, like the Web Audio version)
   
    static class Sound {
        static boolean muted = false;

        enum Wave {
            TRIANGLE, SQUARE, SINE, SAW
        }

        static void waka() {
            tone(Wave.TRIANGLE, 400, 200, true, 0.08, 0.15);
        }

        static void eatGhost() {
            tone(Wave.SQUARE, 300, 800, true, 0.20, 0.20);
        }

        static void powerPellet() {
            tone(Wave.SINE, 150, 300, false, 0.15, 0.20);
        }

        static void death() {
            tone(Wave.SAW, 500, 50, false, 0.60, 0.25);
        }

        static void tone(Wave wave, double f0, double f1, boolean exp, double dur, double gain0) {
            if (muted)
                return;
            Thread t = new Thread(() -> {
                try {
                    float sr = 22050f;
                    int n = (int) (sr * dur);
                    byte[] buf = new byte[n * 2];
                    double phase = 0;
                    for (int i = 0; i < n; i++) {
                        double p = i / (double) n;
                        double f = exp ? f0 * Math.pow(f1 / f0, p) : f0 + (f1 - f0) * p;
                        phase = (phase + f / sr) % 1.0;
                        double s;
                        switch (wave) {
                            case TRIANGLE:
                                s = 4 * Math.abs(phase - 0.5) - 1;
                                break;
                            case SQUARE:
                                s = phase < 0.5 ? 1 : -1;
                                break;
                            case SAW:
                                s = 2 * phase - 1;
                                break;
                            default:
                                s = Math.sin(2 * Math.PI * phase);
                        }
                        double gain = gain0 + (0.01 - gain0) * p;
                        short v = (short) (s * gain * 32767 * 0.6);
                        buf[2 * i] = (byte) (v & 0xFF);
                        buf[2 * i + 1] = (byte) (v >> 8);
                    }
                    AudioFormat af = new AudioFormat(sr, 16, 1, true, false);
                    final Clip clip = AudioSystem.getClip();
                    clip.open(af, buf, 0, buf.length);
                    clip.addLineListener(ev -> {
                        if (ev.getType() == LineEvent.Type.STOP)
                            clip.close();
                    });
                    clip.start();
                } catch (Throwable ignored) {
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

   
    // DRAWING HELPERS
   
    static Graphics2D aa(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g;
    }

    static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }

    /** Neon glow around a shape (like the CSS box-shadow). */
    static void glow(Graphics2D g, Shape s, Color c, int spread, int strength) {
        Stroke old = g.getStroke();
        for (int i = spread; i >= 1; i--) {
            g.setColor(alpha(c, strength * (spread - i + 1) / (spread * 2)));
            g.setStroke(new BasicStroke(i * 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(s);
        }
        g.setStroke(old);
    }

    static void drawCentered(Graphics2D g, String text, int cx, int baseline) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, cx - fm.stringWidth(text) / 2, baseline);
    }

    static List<String> wrap(String text, FontMetrics fm, int maxW) {
        List<String> out = new ArrayList<>();
        String line = "";
        for (String w : text.split(" ")) {
            String t = line.isEmpty() ? w : line + " " + w;
            if (fm.stringWidth(t) > maxW && !line.isEmpty()) {
                out.add(line);
                line = w;
            } else {
                line = t;
            }
        }
        if (!line.isEmpty())
            out.add(line);
        return out;
    }

    static JLabel label(String text, Font f, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    /** Heart shape (used for lives when heart.png is not supplied). */
    static void drawHeart(Graphics2D g, double x, double y, double size) {
        double s = size / 24.0;
        Area a = new Area(new Ellipse2D.Double(1.5, 2.5, 11, 11));
        a.add(new Area(new Ellipse2D.Double(11.5, 2.5, 11, 11)));
        Path2D.Double tri = new Path2D.Double();
        tri.moveTo(2.2, 10);
        tri.lineTo(21.8, 10);
        tri.lineTo(12, 21.5);
        tri.closePath();
        a.add(new Area(tri));
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.scale(s, s);
        Shape sh = at.createTransformedShape(a);
        g.setColor(RED500);
        g.fill(sh);
        g.setColor(new Color(0x7F1D1D));
        g.setStroke(new BasicStroke(1f));
        g.draw(sh);
    }

    /** Small vector icons that stand in for the FontAwesome icons. */
    static final int ICON_NONE = 0, ICON_PLAY = 1, ICON_GAMEPAD = 2, ICON_USERS = 3, ICON_POWER = 4;

    static void drawIcon(Graphics2D g, int type, double cx, double cy, double size, Color c) {
        g.setColor(c);
        Stroke old = g.getStroke();
        double h = size / 2;
        switch (type) {
            case ICON_PLAY: {
                Path2D.Double p = new Path2D.Double();
                p.moveTo(cx - h * 0.6, cy - h);
                p.lineTo(cx + h * 0.9, cy);
                p.lineTo(cx - h * 0.6, cy + h);
                p.closePath();
                g.fill(p);
                break;
            }
            case ICON_GAMEPAD: {
                g.fill(new RoundRectangle2D.Double(cx - h * 1.2, cy - h * 0.6, size * 1.2, h * 1.4, h * 0.9, h * 0.9));
                g.setColor(MENU_BTN_BG);
                g.fill(new Rectangle2D.Double(cx - h * 0.8, cy - h * 0.1, h * 0.7, h * 0.18));
                g.fill(new Rectangle2D.Double(cx - h * 0.55, cy - h * 0.35, h * 0.18, h * 0.7));
                g.fill(new Ellipse2D.Double(cx + h * 0.3, cy - h * 0.2, h * 0.3, h * 0.3));
                g.fill(new Ellipse2D.Double(cx + h * 0.65, cy - h * 0.05, h * 0.3, h * 0.3));
                break;
            }
            case ICON_USERS: {
                g.fill(new Ellipse2D.Double(cx - h * 0.95, cy - h * 0.9, h * 0.9, h * 0.9));
                g.fill(new Ellipse2D.Double(cx + h * 0.05, cy - h * 0.9, h * 0.9, h * 0.9));
                g.fill(new Arc2D.Double(cx - h * 1.2, cy + h * 0.05, h * 1.3, h * 1.5, 0, 180, Arc2D.PIE));
                g.fill(new Arc2D.Double(cx - h * 0.1, cy + h * 0.05, h * 1.3, h * 1.5, 0, 180, Arc2D.PIE));
                break;
            }
            case ICON_POWER: {
                g.setStroke(
                        new BasicStroke((float) Math.max(2, size / 8), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Arc2D.Double(cx - h * 0.85, cy - h * 0.75, h * 1.7, h * 1.7, 120, 300, Arc2D.OPEN));
                g.draw(new Line2D.Double(cx, cy - h, cx, cy - h * 0.05));
                break;
            }
            default:
        }
        g.setStroke(old);
    }

    /** CRT scanlines (the HTML overlay). Lets mouse events pass through. */
    static JComponent scanlines() {
        JComponent c = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 64));
                for (int y = 2; y < getHeight(); y += 4)
                    g.fillRect(0, y, getWidth(), 2);
            }

            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        c.setOpaque(false);
        return c;
    }

   
    /** Arcade style button (rounded, neon pink on hover). */
    static class ArcadeButton extends JButton {
        final int icon;
        final Color bg, border, fg;
        final Font font;
        final boolean leftAligned;
        final int arc;
        boolean hover = false;

        ArcadeButton(String text, int icon, Color bg, Color border, Color fg, float fontSize, boolean leftAligned,
                int arc) {
            super(text);
            this.icon = icon;
            this.bg = bg;
            this.border = border;
            this.fg = fg;
            this.leftAligned = leftAligned;
            this.arc = arc;
            this.font = pixel(fontSize);
            setFocusable(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = aa(g0);
            int m = 6;
            RoundRectangle2D r = new RoundRectangle2D.Double(m, m, getWidth() - 2 * m - 1, getHeight() - 2 * m - 1, arc,
                    arc);
            if (hover)
                glow(g, r, PINK, 6, 90);
            g.setColor(hover ? PINK : bg);
            g.fill(r);
            g.setColor(hover ? PINK : border);
            g.setStroke(new BasicStroke(2f));
            g.draw(r);
            Color text = hover ? Color.WHITE : fg;
            g.setFont(font);
            g.setColor(text);
            FontMetrics fm = g.getFontMetrics();
            int baseline = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            if (leftAligned) {
                g.drawString(getText(), m + 16, baseline);
                drawIcon(g, icon, getWidth() - m - 24, getHeight() / 2.0, 12, text);
            } else {
                drawCentered(g, getText(), getWidth() / 2, baseline);
            }
            g.dispose();
        }
    }

    /** Sound on/off button (speaker icon). */
    static class MuteButton extends JButton {
        boolean hover = false;

        MuteButton() {
            setFocusable(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setToolTipText("Toggle Sound");
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(34, 28));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
            addActionListener(e -> {
                Sound.muted = !Sound.muted;
                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = aa(g0);
            Color c = Sound.muted ? RED500 : (hover ? YELLOW400 : GRAY400);
            g.setColor(c);
            double cx = getWidth() / 2.0 - 2, cy = getHeight() / 2.0;
            g.fill(new Rectangle2D.Double(cx - 9, cy - 3.5, 5, 7));
            Path2D.Double cone = new Path2D.Double();
            cone.moveTo(cx - 4, cy - 3.5);
            cone.lineTo(cx + 2, cy - 8);
            cone.lineTo(cx + 2, cy + 8);
            cone.lineTo(cx - 4, cy + 3.5);
            cone.closePath();
            g.fill(cone);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (Sound.muted) {
                g.draw(new Line2D.Double(cx + 5, cy - 4, cx + 11, cy + 4));
                g.draw(new Line2D.Double(cx + 11, cy - 4, cx + 5, cy + 4));
            } else {
                g.draw(new Arc2D.Double(cx - 1, cy - 5, 9, 10, -50, 100, Arc2D.OPEN));
                g.draw(new Arc2D.Double(cx - 3, cy - 9, 15, 18, -50, 100, Arc2D.OPEN));
            }
            g.dispose();
        }
    }

    /** Outer purple "arcade machine" frame. */
    static class FramePanel extends JPanel {
        FramePanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = aa(g0);
            RoundRectangle2D r = new RoundRectangle2D.Double(10, 10, getWidth() - 21, getHeight() - 21, 24, 24);
            glow(g, r, new Color(147, 51, 234), 9, 60);
            g.setColor(SLATE950);
            g.fill(r);
            g.setColor(PURPLE600);
            g.setStroke(new BasicStroke(4f));
            g.draw(r);
            g.dispose();
        }
    }

    static class LeaderboardPanel extends JPanel {
        final DefaultTableModel model;
        final JTable table;
        final List<RunRecord> records = new ArrayList<>();

        LeaderboardPanel(Runnable returnAction) {
            setOpaque(false);
            setLayout(new BorderLayout(0, 10));
            setBorder(new EmptyBorder(22, 24, 18, 24));

            JPanel title = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
            title.setOpaque(false);
            Icon trophy = new Icon() {
                @Override
                public int getIconWidth() {
                    return 22;
                }

                @Override
                public int getIconHeight() {
                    return 22;
                }

                @Override
                public void paintIcon(Component c, Graphics graphics, int x, int y) {
                    Graphics2D g = aa(graphics);
                    g.setColor(YELLOW400);
                    g.fill(new RoundRectangle2D.Double(x + 4, y + 2, 14, 9, 3, 3));
                    g.setStroke(new BasicStroke(2f));
                    g.draw(new Arc2D.Double(x, y + 2, 9, 10, 270, 180, Arc2D.OPEN));
                    g.draw(new Arc2D.Double(x + 13, y + 2, 9, 10, 90, 180, Arc2D.OPEN));
                    g.fillRect(x + 10, y + 11, 3, 5);
                    g.fillRect(x + 6, y + 17, 11, 3);
                    g.dispose();
                }
            };
            JLabel heading = label("TOP 10 LEADERBOARD", pixel(14f), YELLOW400);
            title.add(new JLabel(trophy));
            title.add(heading);
            add(title, BorderLayout.NORTH);

            String[] columns = { "#", "PLAYER", "SCORE", "MAP", "DIFF", "MAZES", "HEARTS", "DATE" };
            model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model) {
                @Override
                public void doLayout() {
                    setRowHeight(Math.max(18, Math.min(36, getHeight() / 10)));
                    super.doLayout();
                }
            };
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.setShowGrid(true);
            table.setGridColor(new Color(0x7C, 0x3A, 0xB8, 105));
            table.setIntercellSpacing(new Dimension(1, 1));
            table.setBackground(SLATE950);
            table.setForeground(SLATE200);
            table.setFont(pixel(9f));
            table.setSelectionBackground(SLATE950);
            table.setSelectionForeground(SLATE200);
            table.setRowSelectionAllowed(false);

            int[] preferredWidths = { 40, 175, 92, 84, 70, 68, 78, 102 };
            for (int column = 0; column < columns.length; column++) {
                table.getColumnModel().getColumn(column).setPreferredWidth(preferredWidths[column]);
                table.getColumnModel().getColumn(column).setMinWidth(column == 1 ? 86 : 42);
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable source, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {
                        super.getTableCellRendererComponent(source, value, false, false, row, column);
                        boolean empty = row >= records.size();
                        boolean first = row == 0 && !records.isEmpty();
                        setFont(pixel(9f));
                        setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
                        setBorder(column == 1 ? new EmptyBorder(0, 8, 0, 4) : new EmptyBorder(0, 2, 0, 2));
                        setOpaque(true);
                        if (first) {
                            setBackground(new Color(0x48, 0x1C, 0x72, 120));
                            setForeground(column == 0 ? YELLOW400 : SLATE200);
                        } else {
                            setBackground(row % 2 == 0 ? new Color(0x08, 0x0E, 0x1D) : SLATE950);
                            setForeground(empty ? SLATE600 : (column == 0 ? YELLOW500 : SLATE300));
                        }
                        return this;
                    }
                };
                table.getColumnModel().getColumn(column).setCellRenderer(renderer);
            }
            JTableHeader header = table.getTableHeader();
            header.setReorderingAllowed(false);
            header.setResizingAllowed(false);
            header.setPreferredSize(new Dimension(10, 32));
            header.setBackground(new Color(0x0C, 0x14, 0x26));
            header.setForeground(CYAN300);
            header.setFont(pixel(9f));
            header.setBorder(new LineBorder(new Color(0x7C, 0x3A, 0xB8, 140), 1));
            DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
            headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            headerRenderer.setForeground(CYAN300);
            headerRenderer.setBackground(new Color(0x0C, 0x14, 0x26));
            headerRenderer.setFont(pixel(9f));
            headerRenderer.setBorder(new EmptyBorder(2, 2, 2, 2));
            header.setDefaultRenderer(headerRenderer);

            JPanel grid = new JPanel(new BorderLayout());
            grid.setOpaque(false);
            grid.setBorder(new LineBorder(new Color(0x7C, 0x3A, 0xB8, 170), 1));
            grid.add(header, BorderLayout.NORTH);
            grid.add(table, BorderLayout.CENTER);
            add(grid, BorderLayout.CENTER);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            footer.setOpaque(false);
            ArcadeButton back = new ArcadeButton("RETURN", ICON_NONE, SLATE800, SLATE600, GRAY300, 12f, false, 6);
            back.setPreferredSize(new Dimension(150, 42));
            back.addActionListener(e -> returnAction.run());
            footer.add(back);
            add(footer, BorderLayout.SOUTH);
        }

        void refresh() {
            try {
                records.clear();
                records.addAll(LeaderboardStore.load());
                model.setRowCount(0);
                for (int rank = 0; rank < 10; rank++) {
                    if (rank >= records.size()) {
                        model.addRow(new Object[] { rank + 1, "---", "---", "---", "---", "---", "---", "--/--/--" });
                    } else {
                        RunRecord record = records.get(rank);
                        model.addRow(new Object[] { rank + 1, (rank == 0 ? "> " : "") + record.name,
                                String.format("%,d", record.score), "MAP-" + (record.map + 1), record.difficulty,
                                record.mazesCleared, record.hearts,
                                LeaderboardStore.DISPLAY_DATE.format(record.date) });
                    }
                }
                table.repaint();
            } catch (IOException ex) {
                records.clear();
                model.setRowCount(0);
                for (int rank = 0; rank < 10; rank++)
                    model.addRow(new Object[] { rank + 1, "---", "---", "---", "---", "---", "---", "--/--/--" });
                JOptionPane.showMessageDialog(this, "The leaderboard could not be read:\n" + ex.getMessage(),
                        "Leaderboard read error", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = aa(g0);
            RoundRectangle2D outline = new RoundRectangle2D.Double(8, 8, getWidth() - 17, getHeight() - 17, 16, 16);
            glow(g, outline, PURPLE500, 7, 64);
            g.setColor(new Color(0x0B, 0x12, 0x20));
            g.fill(outline);
            g.setColor(alpha(PURPLE400, 210));
            g.setStroke(new BasicStroke(2f));
            g.draw(outline);
            g.dispose();
        }
    }

    /** Menu button box with the blue neon glow. */
    static class MenuBox extends JPanel {
        MenuBox() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = aa(g0);
            RoundRectangle2D r = new RoundRectangle2D.Double(8, 8, getWidth() - 17, getHeight() - 17, 12, 12);
            glow(g, r, new Color(0, 150, 255), 7, 70);
            g.setColor(MENU_BOX_BG);
            g.fill(r);
            g.setColor(new Color(0x0096FF));
            g.setStroke(new BasicStroke(2f));
            g.draw(r);
            g.dispose();
        }
    }

    /** Big "PAC-MAN" title with pink glow. */
    /** Big "PAC-MAN" title made of pixel blocks with pink glow. */
    static class TitlePanel extends JPanel {
        // 5x7 block letters ('X' = filled block)
        static final Map<Character, String[]> GLYPHS = new HashMap<>();
        static {
            GLYPHS.put('P', new String[] { "XXXX.", "X...X", "X...X", "XXXX.", "X....", "X....", "X...." });
            GLYPHS.put('A', new String[] { ".XXX.", "X...X", "X...X", "XXXXX", "X...X", "X...X", "X...X" });
            GLYPHS.put('C', new String[] { ".XXX.", "X...X", "X....", "X....", "X....", "X...X", ".XXX." });
            GLYPHS.put('-', new String[] { ".....", ".....", ".....", "XXXXX", ".....", ".....", "....." });
            GLYPHS.put('M', new String[] { "X...X", "XX.XX", "X.X.X", "X...X", "X...X", "X...X", "X...X" });
            GLYPHS.put('N', new String[] { "X...X", "XX..X", "X.X.X", "X..XX", "X...X", "X...X", "X...X" });
        }

        TitlePanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(700, 130));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = aa(g0);
            String title = "PAC-MAN";

            int cw = 13, ch = 9, gap = 1; // block width, block height, blocks between letters
            int cols = title.length() * 5 + (title.length() - 1) * gap;
            int x0 = (getWidth() - cols * cw) / 2;
            int y0 = 22;

            Path2D.Double blocks = new Path2D.Double();
            int col = 0;
            for (char c : title.toCharArray()) {
                String[] rows = GLYPHS.get(c);
                for (int r = 0; r < 7; r++)
                    for (int k = 0; k < 5; k++)
                        if (rows[r].charAt(k) == 'X')
                            blocks.append(new Rectangle2D.Double(x0 + (col + k) * cw, y0 + r * ch, cw, ch), false);
                col += 5 + gap;
            }

            glow(g, blocks, new Color(255, 0, 85), 10, 120); // pink glow
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setColor(YELLOW400);
            g.fill(blocks);
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g.draw(blocks); // thickens every letter
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Map<TextAttribute, Object> attrs = new HashMap<>();
            attrs.put(TextAttribute.TRACKING, 0.15f);
            g.setFont(vt(24f).deriveFont(attrs));
            g.setColor(CYAN400);
            drawCentered(g, "BUP CSE - 4 EDITION", getWidth() / 2, 118);
            g.dispose();
        }
    }

    /** Hearts that show the remaining lives. */
    static class LivesPanel extends JPanel {
        LivesPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(3 * 24, 22));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = aa(g0);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            for (int i = 0; i < lives; i++) {
                int x = i * 24;
                if (imgHeart != null)
                    g.drawImage(imgHeart, x, 1, 20, 20, null);
                else
                    drawHeart(g, x, 1, 20);
            }
            g.dispose();
        }
    }

    /** The game canvas (blue neon frame + maze + overlay). */
    static class CanvasPanel extends JPanel {
        final ArcadeButton overlayBtn;

        CanvasPanel() {
            setOpaque(false);
            setLayout(null);
            setPreferredSize(new Dimension(CANVAS_W + 2 * CANVAS_PAD + 16, CANVAS_H + 2 * CANVAS_PAD + 16));
            overlayBtn = new ArcadeButton("CONTINUE", ICON_NONE, PURPLE900, PURPLE400, Color.WHITE, 12f, false, 8) {
                @Override
                public void setBounds(int x, int y, int w, int h) {
                    super.setBounds(x, y, 170, 54); // always keep the button small
                }
            };
            overlayBtn.setSize(170, 54);
            overlayBtn.setVisible(false);
            overlayBtn.addActionListener(e -> {
                if (gameOver)
                    startNewGame();
                else {
                    gamePaused = false;
                    hideOverlay();
                }
            });
            add(overlayBtn);
        }

        void syncOverlay() {
            overlayBtn.setVisible(overlayVisible);
            doLayout();
            repaint();
        }

        @Override
        public void doLayout() {
            // fixed size, centered under the "GAME OVER" text
            overlayBtn.setBounds((getWidth() - 170) / 2, getHeight() / 2 + 28, 170, 54);
        }

        /** How much the maze is scaled up to fit the panel. */
        double scale() {
            double sw = (getWidth() - 2.0 * (8 + CANVAS_PAD)) / mapW();
            double sh = (getHeight() - 2.0 * (8 + CANVAS_PAD)) / mapH();
            return Math.max(0.3, Math.min(sw, sh));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            double s = scale();
            int pw = (int) Math.round(mapW() * s + 2 * (8 + CANVAS_PAD));
            int ph = (int) Math.round(mapH() * s + 2 * (8 + CANVAS_PAD));
            int ox = (getWidth() - pw) / 2, oy = (getHeight() - ph) / 2;

            Graphics2D g = aa(g0);
            g.translate(ox, oy); // center the neon box in the panel

            RoundRectangle2D r = new RoundRectangle2D.Double(10, 10, pw - 21, ph - 21, 16, 16);
            glow(g, r, new Color(0, 102, 255), 7, 80);
            g.setColor(Color.BLACK);
            g.fill(r);
            g.setColor(BLUE600);
            g.setStroke(new BasicStroke(4f));
            g.draw(r);

            Graphics2D gg = (Graphics2D) g.create();
            gg.setClip(new RoundRectangle2D.Double(14, 14, pw - 29, ph - 29, 12, 12));
            gg.translate(8 + CANVAS_PAD, 8 + CANVAS_PAD);
            gg.scale(s, s);
            gg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            drawGame(gg);
            gg.dispose();

            if (overlayVisible) {
                g.setColor(new Color(0, 0, 0, 205));
                g.fill(new RoundRectangle2D.Double(14, 14, pw - 29, ph - 29, 12, 12));
                int cx = pw / 2, cy = ph / 2;
                g.setFont(pixel(36f));
                for (int rr = 8; rr >= 2; rr -= 3) {
                    g.setColor(new Color(255, 0, 85, 28));
                    for (int a = 0; a < 360; a += 45)
                        drawCentered(g, overlayTitle, cx + (int) (rr * Math.cos(Math.toRadians(a))),
                                cy - 22 + (int) (rr * Math.sin(Math.toRadians(a))));
                }
                g.setColor(YELLOW400);
                drawCentered(g, overlayTitle, cx, cy - 22);
                g.setFont(pixel(14f));
                g.setColor(CYAN300);
                drawCentered(g, overlaySubtitle, cx, cy + 12);
            }
            g.dispose();
        }
    }

   
    // MAZE DRAWING
   
    static void drawGame(Graphics2D g) {
        // map
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int tile = currentMap[r][c];
                int x = c * TILE_SIZE, y = r * TILE_SIZE;
                if (tile == 1) {
                    g.setColor(new Color(0x1919A6));
                    g.setStroke(new BasicStroke(3f));
                    g.draw(new Rectangle2D.Double(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4));
                    g.setColor(new Color(0x0000FF));
                    g.setStroke(new BasicStroke(1f));
                    g.draw(new Rectangle2D.Double(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8));
                } else if (tile == 0) {
                    // food icon (cherry) instead of the small dot
                    if (imgFood != null)
                        drawSprite(g, imgFood, x + TILE_SIZE / 2.0, y + TILE_SIZE / 2.0, SPRITE_SIZE);
                    else {
                        g.setColor(new Color(0xFFB8AE));
                        g.fill(new Ellipse2D.Double(x + 7.5, y + 7.5, 5, 5));
                    }
                } else if (tile == 2) {
                    // flashing power pellet
                    if ((System.currentTimeMillis() / 200) % 2 == 0) {
                        if (imgPower != null)
                            drawSprite(g, imgPower, x + TILE_SIZE / 2.0, y + TILE_SIZE / 2.0, SPRITE_SIZE);
                        else {
                            g.setColor(new Color(0xFFB8AE));
                            g.fill(new Ellipse2D.Double(x + 4, y + 4, 12, 12));
                        }
                    }
                }
            }
        }

        // pac-man
        BufferedImage pac;
        switch (pacman.facing) {
            case "up":
                pac = imgPacUp;
                break;
            case "down":
                pac = imgPacDown;
                break;
            case "left":
                pac = imgPacLeft;
                break;
            default:
                pac = imgPacRight;
        }
        if (pac != null)
            drawSprite(g, pac, pacman.x, pacman.y, SPRITE_SIZE);
        else {
            g.setColor(Color.YELLOW);
            g.fill(new Ellipse2D.Double(pacman.x - 8, pacman.y - 8, 16, 16));
        }

        for (Ghost gh : ghosts)
            gh.draw(g);
    }

   
    // WINDOW
   
    final CardLayout cards = new CardLayout();
    final JPanel center = new JPanel(cards);
    final JLabel highScoreLabel = label("000000", pixel(14f), Color.WHITE);
    final JLabel scoreLabel = label("0", pixel(14f), Color.WHITE);
    final LivesPanel livesPanel = new LivesPanel();
    final JLabel mapCaption = label("MAP 1: NEON ARCADE", vt(20f), SLATE400);
    javax.swing.Timer loop;
    boolean leaderboardPausedGame;

    public PacManGame() {
        super("Pac-Man Classic");
        loadAssets();
        BufferedImage appIcon = loadImage("icon1.png"); // <- add this line
        if (appIcon != null)
            setIconImage(appIcon); // <- add this line
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExit();
            }
        });
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        FramePanel root = new FramePanel();
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(32, 32, 32, 32));
        root.setPreferredSize(new Dimension(920, 700));

        root.add(buildHeader(), BorderLayout.NORTH);

        center.setOpaque(false);
        center.add(buildMenu(), "menu");
        center.add(buildGameScreen(), "game");
        center.add(buildMapSelect(), "maps"); // <-- NEW (must stay after "game")
        leaderboardPanel = new LeaderboardPanel(this::returnFromLeaderboard);
        center.add(leaderboardPanel, "leaderboard");
        root.add(center, BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);
        setResizable(true);
        pack();
        setMinimumSize(new Dimension(700, 540));
        setLocationRelativeTo(null);

        setGlassPane(scanlines());
        getGlassPane().setVisible(true);

        bindKeys();
        updateHud();

        loop = new javax.swing.Timer(14, e -> {
            updateGame();
            updateHud();
            if (center.getComponent(1).isShowing())
                canvasPanel.repaint();
        });
        loop.start();
    }

    void updateHud() {
        scoreLabel.setText(String.valueOf(score));
        highScoreLabel.setText(String.format("%06d", highScore));
        livesPanel.repaint();
    }

    // ---- header / footer --------------------------------------------
    JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 16, 0),
                new CompoundBorder(new MatteBorder(0, 0, 2, 0, new Color(0x58, 0x1C, 0x87, 128)),
                        new EmptyBorder(0, 0, 8, 0))));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(label("HIGH SCORE:", pixel(13f), YELLOW400));
        left.add(highScoreLabel);
        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);
        right.add(new MuteButton());
        right.add(label("PAC-MAN V3.0", vt(22f), PURPLE400));
        header.add(right, BorderLayout.EAST);
        return header;
    }

    JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new CompoundBorder(new EmptyBorder(16, 0, 0, 0),
                new CompoundBorder(new MatteBorder(1, 0, 0, 0, new Color(0x58, 0x1C, 0x87, 102)),
                        new EmptyBorder(10, 0, 0, 0))));
        JLabel l = label("\u00A9 2026 CLASSIC ARCADE \u2022 CSE - 4 PAC-MAN STYLE MAZE", pixel(10f), SLATE500);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        footer.add(l, BorderLayout.CENTER);
        return footer;
    }

    // ---- main menu ----------------------------------------------------
    JComponent buildMenu() {
        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        menu.add(Box.createVerticalGlue());
        TitlePanel title = new TitlePanel();
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        menu.add(title);
        menu.add(Box.createVerticalStrut(14));

        MenuBox box = new MenuBox();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(24, 28, 24, 28));
        box.setAlignmentX(CENTER_ALIGNMENT);
        box.setMaximumSize(new Dimension(470, 350));
        box.setPreferredSize(new Dimension(470, 350));

        ArcadeButton start = menuButton("> START GAME", ICON_PLAY);
        ArcadeButton how = menuButton("> HOW TO PLAY", ICON_GAMEPAD);
        ArcadeButton about = menuButton("> ABOUT US", ICON_USERS);
        ArcadeButton leaderboard = menuButton("> TOP 10", ICON_NONE);
        ArcadeButton exit = menuButton("> EXIT", ICON_POWER);
        start.addActionListener(e -> cards.show(center, "maps"));
        how.addActionListener(e -> showHowToPlay());
        about.addActionListener(e -> showAboutUs());
        leaderboard.addActionListener(e -> showLeaderboard("menu"));
        exit.addActionListener(e -> showExit());
        box.add(start);
        box.add(Box.createVerticalStrut(8));
        box.add(how);
        box.add(Box.createVerticalStrut(8));
        box.add(about);
        box.add(Box.createVerticalStrut(8));
        box.add(leaderboard);
        box.add(Box.createVerticalStrut(8));
        box.add(exit);
        menu.add(box);
        menu.add(Box.createVerticalStrut(14));

        JLabel h1 = label("USE ARROWS / WASD TO MOVE PAC-MAN", vt(22f), GRAY400);
        JLabel h2 = label("PRESS [P] TO PAUSE GAME", vt(22f), YELLOW500);
        h1.setAlignmentX(CENTER_ALIGNMENT);
        h2.setAlignmentX(CENTER_ALIGNMENT);
        menu.add(h1);
        menu.add(h2);
        menu.add(Box.createVerticalGlue());
        return menu;
    }

    ArcadeButton menuButton(String text, int icon) {
        ArcadeButton b = new ArcadeButton(text, icon, MENU_BTN_BG, PURPLE400, PURPLE200, 14f, true, 8);
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setPreferredSize(new Dimension(414, 54));
        b.setMaximumSize(new Dimension(414, 54));
        return b;
    }

    class RunSetupDialog extends JDialog {
        final JTextField nameInput = new JTextField(playerName, 16);
        final JToggleButton[] difficultyOptions = new JToggleButton[3];
        boolean confirmed;

        RunSetupDialog() {
            super(PacManGame.this, "RUN SETUP", true);
            setUndecorated(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JPanel root = new JPanel(new BorderLayout(0, 10)) {
                @Override
                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = aa(graphics);
                    RoundRectangle2D panel = new RoundRectangle2D.Double(8, 8, getWidth() - 17,
                            getHeight() - 17, 18, 18);
                    glow(g, panel, PURPLE500, 8, 74);
                    g.setColor(new Color(0x0B, 0x12, 0x20));
                    g.fill(panel);
                    g.setColor(PURPLE400);
                    g.setStroke(new BasicStroke(2f));
                    g.draw(panel);
                    g.dispose();
                }
            };
            root.setOpaque(false);
            root.setBorder(new EmptyBorder(20, 28, 22, 28));
            setContentPane(root);

            JPanel heading = new JPanel(new BorderLayout());
            heading.setOpaque(false);
            JLabel title = label("PLAYER SETUP", pixel(14f), YELLOW400);
            heading.add(title, BorderLayout.WEST);
            JButton close = new JButton("X");
            close.setFont(vt(22f));
            close.setForeground(SLATE400);
            close.setContentAreaFilled(false);
            close.setBorderPainted(false);
            close.setOpaque(false);
            close.setFocusable(false);
            close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            close.addActionListener(e -> dispose());
            heading.add(close, BorderLayout.EAST);
            root.add(heading, BorderLayout.NORTH);

            JPanel fields = new JPanel();
            fields.setOpaque(false);
            fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

            JLabel map = label("MAP " + (selectedMap + 1) + "  /  " + MAP_NAMES[selectedMap],
                    pixel(9f), CYAN300);
            map.setAlignmentX(LEFT_ALIGNMENT);
            fields.add(map);
            fields.add(Box.createVerticalStrut(18));

            JLabel nameLabel = label("PLAYER NAME", pixel(10f), YELLOW400);
            nameLabel.setAlignmentX(LEFT_ALIGNMENT);
            fields.add(nameLabel);
            fields.add(Box.createVerticalStrut(8));
            nameInput.setFont(vt(25f));
            nameInput.setForeground(SLATE200);
            nameInput.setBackground(SLATE950);
            nameInput.setCaretColor(CYAN300);
            nameInput.setSelectionColor(PURPLE900);
            nameInput.setSelectedTextColor(Color.WHITE);
            nameInput.setBorder(new CompoundBorder(new LineBorder(PURPLE500, 2),
                    new EmptyBorder(5, 10, 5, 10)));
            nameInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            nameInput.setAlignmentX(LEFT_ALIGNMENT);
            fields.add(nameInput);
            fields.add(Box.createVerticalStrut(6));

            JLabel hint = label("UP TO 14 CHARACTERS", vt(16f), SLATE500);
            hint.setAlignmentX(LEFT_ALIGNMENT);
            fields.add(hint);
            fields.add(Box.createVerticalStrut(20));

            JLabel difficultyLabel = label("DIFFICULTY", pixel(10f), YELLOW400);
            difficultyLabel.setAlignmentX(LEFT_ALIGNMENT);
            fields.add(difficultyLabel);
            fields.add(Box.createVerticalStrut(8));

            JPanel difficultyRow = new JPanel(new GridLayout(1, 3, 8, 0));
            difficultyRow.setOpaque(false);
            ButtonGroup group = new ButtonGroup();
            String[] choices = { "EASY", "NORMAL", "HARD" };
            for (int i = 0; i < choices.length; i++) {
                final String choice = choices[i];
                JToggleButton option = new JToggleButton(choice) {
                    @Override
                    protected void paintComponent(Graphics graphics) {
                        Graphics2D g = aa(graphics);
                        int inset = 3;
                        RoundRectangle2D button = new RoundRectangle2D.Double(inset, inset,
                                getWidth() - inset * 2 - 1, getHeight() - inset * 2 - 1, 9, 9);
                        if (isSelected())
                            glow(g, button, PURPLE500, 4, 55);
                        g.setColor(isSelected() ? PURPLE950 : SLATE950);
                        g.fill(button);
                        g.setColor(isSelected() ? PURPLE400 : SLATE600);
                        g.setStroke(new BasicStroke(1.5f));
                        g.draw(button);
                        g.setFont(pixel(8f));
                        g.setColor(isSelected() ? YELLOW400 : SLATE400);
                        drawCentered(g, getText(), getWidth() / 2, getHeight() / 2 + 4);
                        g.dispose();
                    }
                };
                option.setFocusable(false);
                option.setContentAreaFilled(false);
                option.setBorderPainted(false);
                option.setOpaque(false);
                option.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                option.setPreferredSize(new Dimension(100, 44));
                group.add(option);
                difficultyRow.add(option);
                difficultyOptions[i] = option;
                if (choice.equals(normalizeDifficulty(selectedDifficulty)))
                    option.setSelected(true);
            }
            difficultyRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            difficultyRow.setAlignmentX(LEFT_ALIGNMENT);
            fields.add(difficultyRow);
            fields.add(Box.createVerticalStrut(8));
            JLabel difficultyHint = label("Adjusts PAC-MAN AND GHOST SPEED", vt(16f), SLATE500);
            difficultyHint.setAlignmentX(LEFT_ALIGNMENT);
            fields.add(difficultyHint);
            fields.add(Box.createVerticalStrut(4));
            JLabel validation = label("", vt(17f), RED200);
            validation.setAlignmentX(LEFT_ALIGNMENT);
            fields.add(validation);
            root.add(fields, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            actions.setOpaque(false);
            ArcadeButton cancel = new ArcadeButton("CANCEL", ICON_NONE, SLATE800, SLATE600, GRAY300,
                    9f, false, 7);
            cancel.setPreferredSize(new Dimension(110, 42));
            cancel.addActionListener(e -> dispose());
            ArcadeButton start = new ArcadeButton("START GAME", ICON_PLAY, MENU_BTN_BG, PURPLE400,
                    Color.WHITE, 9f, false, 7);
            start.setPreferredSize(new Dimension(160, 42));
            start.addActionListener(e -> {
                String name = nameInput.getText().trim();
                if (name.isEmpty()) {
                    validation.setText("PLEASE ENTER A PLAYER NAME");
                    nameInput.requestFocusInWindow();
                    return;
                }
                confirmed = true;
                dispose();
            });
            actions.add(cancel);
            actions.add(start);
            root.add(actions, BorderLayout.SOUTH);

            getRootPane().setDefaultButton(start);
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
            root.getActionMap().put("cancel", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            setGlassPane(scanlines());
            getGlassPane().setVisible(true);
            setMinimumSize(new Dimension(440, 360));
            setPreferredSize(new Dimension(480, 390));
            pack();
            setLocationRelativeTo(PacManGame.this);
        }

        String selectedDifficulty() {
            for (int i = 0; i < difficultyOptions.length; i++) {
                if (difficultyOptions[i].isSelected())
                    return new String[] { "EASY", "NORMAL", "HARD" }[i];
            }
            return "NORMAL";
        }
    }

    boolean configureRun() {
        RunSetupDialog dialog = new RunSetupDialog();
        dialog.setVisible(true);
        if (!dialog.confirmed)
            return false;

        String name = dialog.nameInput.getText().trim();
        int codePoints = name.codePointCount(0, name.length());
        if (codePoints > 14)
            name = name.substring(0, name.offsetByCodePoints(0, 14));
        playerName = name;
        selectedDifficulty = dialog.selectedDifficulty();
        PREFS.put("pacman_player_name", playerName);
        PREFS.put("pacman_difficulty", selectedDifficulty);
        return true;
    }

    void showLeaderboard(String returnCard) {
        leaderboardReturnCard = returnCard;
        if ("game".equals(returnCard) && !gameOver && !gamePaused) {
            leaderboardPausedGame = true;
            togglePause("PRESS P TO RESUME");
        }
        leaderboardPanel.refresh();
        cards.show(center, "leaderboard");
    }

    void returnFromLeaderboard() {
        cards.show(center, leaderboardReturnCard);
        if (leaderboardPausedGame) {
            leaderboardPausedGame = false;
            gamePaused = false;
            hideOverlay();
        }
    }

    // ---- game screen ----------------------------------------------------
    JComponent buildGameScreen() {
        JPanel screen = new JPanel(new BorderLayout());
        screen.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 8, 6, 8));
        JPanel scoreBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        scoreBox.setOpaque(false);
        scoreBox.add(label("SCORE:", pixel(13f), RED500));
        scoreBox.add(scoreLabel);
        scoreLabel.setFont(pixel(13f));
        top.add(scoreBox, BorderLayout.WEST);
        JPanel livesBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        livesBox.setOpaque(false);
        livesBox.add(label("LIVES:", pixel(13f), YELLOW400));
        livesBox.add(livesPanel);
        top.add(livesBox, BorderLayout.EAST);
        screen.add(top, BorderLayout.NORTH);

        // Map - 1

        // canvasPanel = new CanvasPanel();
        // JPanel holder = new JPanel(new GridBagLayout());
        // holder.setOpaque(false);
        // holder.add(canvasPanel);
        // screen.add(holder, BorderLayout.CENTER);

        // Map - 2
        canvasPanel = new CanvasPanel();
        screen.add(canvasPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(6, 8, 0, 8));
        ArcadeButton back = smallButton("< MENU");
        back.addActionListener(e -> {
            cards.show(center, "menu");
            gameStarted = false;
        });
        ArcadeButton leaderboard = smallButton("TOP 10");
        leaderboard.addActionListener(e -> showLeaderboard("game"));
        ArcadeButton pause = smallButton("PAUSE");
        pause.addActionListener(e -> togglePause("PRESS PAUSE TO RESUME"));
        bottom.add(back, BorderLayout.WEST);
        mapCaption.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(mapCaption, BorderLayout.CENTER);
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(leaderboard);
        rightButtons.add(pause);
        bottom.add(rightButtons, BorderLayout.EAST);
        screen.add(bottom, BorderLayout.SOUTH);
        return screen;
    }

    JComponent buildMapSelect() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(Box.createVerticalGlue());

        JLabel title = label("CHOOSE YOUR MAP", pixel(22f), YELLOW400);
        title.setAlignmentX(CENTER_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(14));

        for (int i = 0; i < MAPS.length; i++) {
            final int idx = i;
            MapCard card = new MapCard(i);
            card.setAlignmentX(CENTER_ALIGNMENT);
            card.setMaximumSize(new Dimension(640, 110));
            card.addActionListener(e -> {
                if (!configureRun())
                    return;
                selectedMap = idx;
                mapCaption.setText("MAP " + (idx + 1) + ": " + MAP_NAMES[idx]);
                cards.show(center, "game");
                startNewGame();
            });
            p.add(card);
            p.add(Box.createVerticalStrut(6));
        }

        p.add(Box.createVerticalStrut(8));
        ArcadeButton back = smallButton("< BACK");
        back.setAlignmentX(CENTER_ALIGNMENT);
        back.setMaximumSize(new Dimension(130, 44));
        back.addActionListener(e -> cards.show(center, "menu"));
        p.add(back);
        p.add(Box.createVerticalGlue());
        return p;
    }

    ArcadeButton smallButton(String text) {
        ArcadeButton b = new ArcadeButton(text, ICON_NONE, SLATE800, SLATE600, GRAY300, 12f, false, 6);
        b.setPreferredSize(new Dimension(130, 44));
        return b;
    }

    // ---- keyboard -------------------------------------------------------
    void bindKeys() {
        bindKey("pressed UP", () -> setNext(0, -1));
        bindKey("pressed W", () -> setNext(0, -1));
        bindKey("pressed DOWN", () -> setNext(0, 1));
        bindKey("pressed S", () -> setNext(0, 1));
        bindKey("pressed LEFT", () -> setNext(-1, 0));
        bindKey("pressed A", () -> setNext(-1, 0));
        bindKey("pressed RIGHT", () -> setNext(1, 0));
        bindKey("pressed D", () -> setNext(1, 0));
        bindKey("pressed P", () -> togglePause("PRESS P TO RESUME"));
    }

    void bindKey(String key, Runnable action) {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        getRootPane().getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    static void setNext(int dx, int dy) {
        pacman.nextDirX = dx;
        pacman.nextDirY = dy;
    }

   
    // MODALS (HOW TO PLAY / ABOUT US / EXIT)
   
    static class ModalPanel extends JPanel {
        final Color border, glowColor;

        ModalPanel(Color border, Color glowColor) {
            this.border = border;
            this.glowColor = glowColor;
            setOpaque(true);
            setBackground(BG);
            setBorder(new EmptyBorder(40, 40, 40, 40));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = aa(g0);
            RoundRectangle2D r = new RoundRectangle2D.Double(14, 14, getWidth() - 29, getHeight() - 29, 14, 14);
            glow(g, r, glowColor, 8, 80);
            g.setColor(SLATE900);
            g.fill(r);
            g.setColor(border);
            g.setStroke(new BasicStroke(2f));
            g.draw(r);
            g.dispose();
        }
    }

    /** One clickable map choice: mini-preview + name. */
    static class MapCard extends JButton {
        final int index;
        boolean hover = false;

        MapCard(int index) {
            this.index = index;
            setFocusable(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(640, 110));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = aa(g0);
            int m = 6;
            RoundRectangle2D r = new RoundRectangle2D.Double(m, m, getWidth() - 2 * m - 1, getHeight() - 2 * m - 1, 12,
                    12);
            if (hover)
                glow(g, r, PINK, 6, 90);
            g.setColor(hover ? PURPLE900 : MENU_BTN_BG);
            g.fill(r);
            g.setColor(hover ? PINK : PURPLE400);
            g.setStroke(new BasicStroke(2f));
            g.draw(r);

            // mini preview of this map
            int[][] map = MAPS[index];
            int cols = map[0].length, rows = map.length;
            double ts = Math.min(300.0 / cols, 88.0 / rows);
            int pw = (int) Math.round(ts * cols), ph = (int) Math.round(ts * rows);
            int px = m + 14 + (300 - pw) / 2, py = (getHeight() - ph) / 2;
            g.setColor(Color.BLACK);
            g.fillRect(px - 3, py - 3, pw + 6, ph + 6);
            for (int rr = 0; rr < rows; rr++)
                for (int cc = 0; cc < cols; cc++) {
                    double x = px + cc * ts, y = py + rr * ts;
                    int t = map[rr][cc];
                    if (t == 1) {
                        g.setColor(BLUE600);
                        g.fill(new Rectangle2D.Double(x, y, ts, ts));
                    } else if (t == 0) {
                        g.setColor(new Color(0xFFB8AE));
                        g.fill(new Ellipse2D.Double(x + ts * 0.35, y + ts * 0.35, ts * 0.3, ts * 0.3));
                    } else if (t == 2) {
                        g.setColor(YELLOW400);
                        g.fill(new Ellipse2D.Double(x + ts * 0.1, y + ts * 0.1, ts * 0.8, ts * 0.8));
                    }
                }

            int tx = m + 14 + 300 + 28;
            g.setFont(pixel(16f));
            g.setColor(hover ? Color.WHITE : YELLOW400);
            g.drawString("MAP - " + (index + 1), tx, getHeight() / 2 - 4);
            g.setFont(vt(22f));
            g.setColor(hover ? Color.WHITE : CYAN300);
            g.drawString(MAP_NAMES[index], tx, getHeight() / 2 + 22);
            g.dispose();
        }
    }

    JDialog makeDialog(JPanel content, Color border, Color glowColor, boolean modal) {
        JDialog d = new JDialog(this, modal);
        d.setUndecorated(true);
        ModalPanel wrap = new ModalPanel(border, glowColor);
        wrap.setLayout(new BorderLayout());
        content.setOpaque(false);
        wrap.add(content, BorderLayout.CENTER);
        d.setContentPane(wrap);
        d.setGlassPane(scanlines());
        d.getGlassPane().setVisible(true);
        for (String k : new String[] { "ENTER", "ESCAPE" }) {
            d.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(k), "close");
        }
        d.getRootPane().getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                d.dispose();
            }
        });
        d.pack();
        d.setLocationRelativeTo(this);
        return d;
    }

    /** Thin divider line under the modal titles. */
    static class Rule extends JPanel {
        final Color color;

        Rule(Color color) {
            this.color = color;
            setOpaque(false);
            setPreferredSize(new Dimension(10, 2));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), 1);
        }
    }

    static String html(int width, String body) {
        return "<html><div style='width:" + width + "px'>" + body + "</div></html>";
    }

    JDialog buildHowToPlay(boolean modal) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel title = label("HOW TO PLAY", pixel(24f), PINK500);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setAlignmentX(LEFT_ALIGNMENT);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(title);
        p.add(Box.createVerticalStrut(10));
        Rule sep = new Rule(new Color(0x83, 0x18, 0x43, 128));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        p.add(sep);
        p.add(Box.createVerticalStrut(14));

        addSection(p, "OBJECTIVE:",
                "Guide Pac-Man through the maze to eat all the food while avoiding the four colorful ghosts.");
        addSection(p, "KEYBOARD CONTROLS:",
                "<font color='#22D3EE'>UP / W:</font> Move Up<br>"
                        + "<font color='#22D3EE'>DOWN / S:</font> Move Down<br>"
                        + "<font color='#22D3EE'>LEFT / A:</font> Move Left<br>"
                        + "<font color='#22D3EE'>RIGHT / D:</font> Move Right<br>"
                        + "<font color='#22D3EE'>P KEY:</font> Pause Game");
        addSection(p, "POWER PELLETS & GHOSTS:",
                "Eat the larger flashing Power Pellets to turn ghosts vulnerable (blue) for a short period. Eat blue ghosts for bonus points!");

        ArcadeButton close = new ArcadeButton("PRESS ENTER OR CLICK TO RETURN", ICON_NONE, PINK950, PINK500, PINK200,
                12f, false, 8);
        close.setAlignmentX(LEFT_ALIGNMENT);
        close.setPreferredSize(new Dimension(480, 56));
        close.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        p.add(Box.createVerticalStrut(6));
        p.add(close);

        JDialog d = makeDialog(p, PINK500, PINK, modal);
        close.addActionListener(e -> d.dispose());
        return d;
    }

    void addSection(JPanel p, String heading, String body) {
        JLabel h = label(heading, pixel(12f), YELLOW400);
        h.setAlignmentX(LEFT_ALIGNMENT);
        JLabel b = label(html(480, body), vt(22f), SLATE200);
        b.setAlignmentX(LEFT_ALIGNMENT);
        p.add(h);
        p.add(Box.createVerticalStrut(4));
        p.add(b);
        p.add(Box.createVerticalStrut(12));
    }

    /** One developer card (photo slot + name + role). */
    class DevCard extends JPanel {
        private static final int SIZE = 140;
        private final String name, role;
        private final Color accent, bg, border;
        private final BufferedImage photo; // already cropped and scaled

        DevCard(String letter, String name, String role, Color accent, Color bg, Color border, BufferedImage src) {
            this.name = name;
            this.role = role;
            this.accent = accent;
            this.bg = bg;
            this.border = border;
            this.photo = (src == null) ? null : coverScale(src, SIZE);
            setOpaque(false);
            setPreferredSize(new Dimension(200, 270));
        }

        // crop to a square (keeps the upper part for portraits), then shrink in smooth
        // steps
        private static BufferedImage coverScale(BufferedImage src, int size) {
            int sw = src.getWidth(), sh = src.getHeight();
            int side = Math.min(sw, sh);
            int sx = (sw - side) / 2;
            int sy = (sh > sw) ? (int) ((sh - side) * 0.10) : 0; // 0.0 = top, 0.5 = middle
            BufferedImage cur = src.getSubimage(sx, sy, side, side);
            int w = side;
            while (w / 2 >= size) {
                w /= 2;
                cur = resize(cur, w);
            }
            return resize(cur, size);
        }

        private static BufferedImage resize(BufferedImage in, int s) {
            BufferedImage out = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = out.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(in, 0, 0, s, s, null);
            g.dispose();
            return out;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            int w = getWidth(), h = getHeight();

            g2.setColor(bg);
            g2.fillRect(0, 0, w, h);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRect(1, 1, w - 3, h - 3);

            int x = (w - SIZE) / 2, y = 18;
            if (photo != null) {
                Shape old = g2.getClip();
                g2.setClip(new java.awt.geom.Ellipse2D.Float(x, y, SIZE, SIZE));
                g2.drawImage(photo, x, y, null); // no scaling here, so it stays sharp
                g2.setClip(old);
            } else {
                g2.setColor(border);
                g2.fillOval(x, y, SIZE, SIZE);
            }
            g2.setColor(accent);
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(x, y, SIZE, SIZE);
            // Dev Name
            g2.setFont(pixel(12f));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(accent);
            g2.drawString(name, (w - fm.stringWidth(name)) / 2, y + SIZE + 26);
            // Dev Role
            g2.setFont(vt(20f));
            fm = g2.getFontMetrics();
            g2.setColor(Color.WHITE);
            g2.drawString(role, (w - fm.stringWidth(role)) / 2, y + SIZE + 62);

            g2.dispose();
        }
    }

    JDialog buildAboutUs(boolean modal) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel title = label("DEVELOPERS", pixel(24f), CYAN400);
        title.setAlignmentX(CENTER_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(10));
        Rule sep = new Rule(new Color(0x16, 0x4E, 0x63, 128));
        sep.setAlignmentX(CENTER_ALIGNMENT);
        p.add(sep);
        p.add(Box.createVerticalStrut(14));

        JLabel about = label("ABOUT US", pixel(12f), YELLOW400);
        about.setAlignmentX(CENTER_ALIGNMENT);
        p.add(about);
        p.add(Box.createVerticalStrut(14));

        // PHOTOS: put dev1.png, dev2.png, dev3.png in the images/ folder.
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsRow.setOpaque(false);
        cardsRow.add(new DevCard("", "JUNAED AHMED", "LEAD DEVELOPER", CYAN400, CYAN950, new Color(0x155E75),
                devPhotos[2]));
        cardsRow.add(
                new DevCard("", "IMTIYAZ ALI", "GAME ARCHITECT", PINK400, PINK950, new Color(0x9D174D), devPhotos[1]));
        cardsRow.add(new DevCard("", "MD UDOY HOSSAIN JOY", "UI/UX DESIGNER", GREEN400, GREEN950, new Color(0x166534),
                devPhotos[0]));
        cardsRow.setAlignmentX(CENTER_ALIGNMENT);
        p.add(cardsRow);
        p.add(Box.createVerticalStrut(14));

        JLabel proj = label("2-2 GAME PROJECT \u2026 PAC-MAN CLASSIC GAME", vt(22f), SLATE400);
        proj.setAlignmentX(CENTER_ALIGNMENT);
        p.add(proj);
        p.add(Box.createVerticalStrut(14));

        ArcadeButton close = new ArcadeButton("PRESS ENTER OR CLICK TO RETURN", ICON_NONE, CYAN950, CYAN500, CYAN200,
                12f, false, 8);
        close.setPreferredSize(new Dimension(620, 56));
        close.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        close.setAlignmentX(CENTER_ALIGNMENT);
        p.add(close);

        JDialog d = makeCleanDialog(p, CYAN500, modal);
        close.addActionListener(e -> d.dispose());
        return d;
    }

    JDialog makeCleanDialog(JPanel content, Color borderColor, boolean modal) {
        JDialog d = new JDialog((Frame) null, modal);
        d.setUndecorated(true);
        d.setBackground(new Color(0, 0, 0, 0));

        content.setOpaque(false);
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0B, 0x12, 0x20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(14, 18, 14, 18));
        root.add(content, BorderLayout.CENTER);
        d.setContentPane(root);

        // ENTER or ESC closes the dialog
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "close");
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        root.getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                d.dispose();
            }
        });

        d.pack();
        d.setLocationRelativeTo(null);
        return d;
    }

    /** Red power icon that pulses (like animate-pulse). */
    static class PulseIcon extends JPanel {
        final long start = System.currentTimeMillis();
        final javax.swing.Timer t;

        PulseIcon() {
            setOpaque(false);
            setPreferredSize(new Dimension(90, 90));
            t = new javax.swing.Timer(50, e -> repaint());
            t.start();
        }

        @Override
        public void removeNotify() {
            t.stop();
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = aa(g0);
            double ph = (System.currentTimeMillis() - start) / 1000.0 * Math.PI;
            int a = (int) (170 + 85 * Math.sin(ph));
            drawIcon(g, ICON_POWER, getWidth() / 2.0, getHeight() / 2.0 + 2, 56, alpha(RED500, a));
            g.dispose();
        }
    }

    JDialog buildExit(boolean modal) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(18, 28, 18, 28));

        JLabel t = label("EXIT GAME", pixel(24f), RED500);
        t.setAlignmentX(CENTER_ALIGNMENT);
        JLabel s = label("THANKS FOR PLAYING PAC-MAN!", vt(28f), SLATE300);
        s.setAlignmentX(CENTER_ALIGNMENT);
        PulseIcon icon = new PulseIcon();
        icon.setAlignmentX(CENTER_ALIGNMENT);
        ArcadeButton exitButton = new ArcadeButton("YES, EXIT GAME", ICON_NONE, RED950, RED500, RED200, 12f, false, 8);
        exitButton.setPreferredSize(new Dimension(440, 56));
        exitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        exitButton.setAlignmentX(CENTER_ALIGNMENT);
        ArcadeButton cancelButton = new ArcadeButton("CANCEL", ICON_NONE, SLATE900, SLATE600, SLATE300, 12f, false,
                8);
        cancelButton.setPreferredSize(new Dimension(440, 56));
        cancelButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        cancelButton.setAlignmentX(CENTER_ALIGNMENT);

        p.add(t);
        p.add(Box.createVerticalStrut(22));
        p.add(s);
        p.add(Box.createVerticalStrut(18));
        p.add(icon);
        p.add(Box.createVerticalStrut(18));
        p.add(exitButton);
        p.add(Box.createVerticalStrut(12));
        p.add(cancelButton);

        JDialog d = makeDialog(p, RED500, new Color(239, 68, 68), modal);
        exitButton.addActionListener(e -> System.exit(0));
        cancelButton.addActionListener(e -> d.dispose());
        return d;
    }

    void showHowToPlay() {
        buildHowToPlay(true).setVisible(true);
    }

    void showAboutUs() {
        buildAboutUs(true).setVisible(true);
    }

    void showExit() {
        buildExit(true).setVisible(true);
    }

   
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> new PacManGame().setVisible(true));
    }

}
