package fr.ubx.poo.ugarden;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.launcher.MapEntity;
import fr.ubx.poo.ugarden.launcher.MapLevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Functions {



    public static double distanceHelper(Position p1, Position p2) {
        return Math.sqrt(Math.pow(p2.x() - p1.x(), 2) + Math.pow(p2.y() - p1.y(), 2));
    }
    public static List<Position> randomPositions(int count, int width, int height, Position playerPosition, int level) {
        List<Position> positions = new ArrayList<>();
        Random random = new Random();
        int minDistance = 3;

        for (int i = 0; i < count; i++) {
            Position position;
            do {

                int x = random.nextInt(width);
                int y = random.nextInt(height);


                position = new Position(level, x, y);
            } while (distanceHelper(playerPosition, position) < minDistance);


            positions.add(position);
        }

        return positions;
    }
    public static String stringGenerator(int length) {
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int ascii = random.nextInt(122 - 48 + 1) + 48; // ASCII range from '0' (48) to 'z' (122)
            while ((ascii > 57 && ascii < 65) || (ascii > 90 && ascii < 97)) {
                ascii = random.nextInt(122 - 48 + 1) + 48; // Regenerate if it's a non-alphanumeric character
            }
            randomString.append((char) ascii);
        }

        return randomString.toString();
    }



    public static List<MapLevel> readLevelsFromfile(File file) throws IOException {
        List<MapLevel> allLvls = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            int lvls = 0;
            int level=1;
            boolean iscompressed = false;
            String row;
            while ((row = reader.readLine()) != null) {
                if (row.startsWith("compression")) {
                    iscompressed = Boolean.parseBoolean(row.split("=")[1].trim());
                } else if (row.startsWith("levels")) {
                    lvls = Integer.parseInt(row.split("=")[1].trim());
                } else if (row.startsWith("level")) {
                    String layout = row.split("=")[1].trim();
                    MapEntity[][] map = parseLayout(layout, iscompressed);
                    MapLevel mapLevel=new MapLevel(map[0].length,map.length,level);
                    level++;
                    mapLevel.setGrid(map);
                    allLvls.add(mapLevel);
                    if (allLvls.size() == lvls) {

                        break;
                    }
                }
            }
        }
        return allLvls;
    }

    private static MapEntity[][] parseLayout(String layout, boolean isCompressed) {
        List<String> rows = new ArrayList<>();

        if (isCompressed) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < layout.length(); x++) {
                char c = layout.charAt(x);
                if (Character.isDigit(c)) {

                    char dernierChar = row.charAt(row.length() - 1);
                    int Counter = Character.getNumericValue(c);
                    row.append(String.valueOf(dernierChar).repeat(Math.max(0, Counter - 1)));
                } else if (c != 'x') {
                    row.append(c);
                } else {
                    rows.add(row.toString());
                    row.setLength(0);
                }
            }

            if (!row.isEmpty()) {
                rows.add(row.toString());
            }
        } else {
            rows = Arrays.asList(layout.split("x"));
        }

        int numRows = rows.size();
        int columns = numRows > 0 ? rows.get(0).length() : 0;
        MapEntity[][] map = new MapEntity[numRows][columns];
        for (int i = 0; i < numRows; i++) {
            String row = rows.get(i);
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                map[i][j] = MapEntity.fromCode(c);
            }
        }
        return map;
    }
}