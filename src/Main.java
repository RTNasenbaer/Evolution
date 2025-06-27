import world.World;

import java.util.Scanner;

import entities.Entity;

public class Main {

    private World world;
    private Entity entity;
    private Display display;

    Main() {
        world = new World();
        entity = new Entity(0, 0, 100, 1);
        world.addEntity(entity);
        display = new Display(world);
        display.render();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();
            if (input.equals("/exit")) break;
            if (input.equals("/run")) {
                // For now, just re-render (simulate a step if needed)
                try {
                    entity.moveRandomly();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid coordinates.");
                }
                display.render();
            } else if (input.startsWith("/run ")) {
                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    try {
                        int steps = Integer.parseInt(parts[1]);
                        for (int i = 0; i < steps; i++) {
                            entity.moveRandomly();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number of steps.");
                    }
                } else {
                    System.out.println("Usage: /run <steps>");
                }
                display.render();
            } else if (input.equals("/status")) {
                System.out.println("Entity at (" + entity.getX() + ", " + entity.getY() + "), Energy: " + entity.getEnergy() + ", Speed: " + entity.getSpeed());
            } else if (input.startsWith("/move ")) {
                String[] parts = input.split(" ");
                if (parts.length == 3) {
                    try {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        entity.moveTo(x, y);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates.");
                    }
                } else if (parts.length == 5) {
                    try {
                        int ex = Integer.parseInt(parts[1]);
                        int ey = Integer.parseInt(parts[2]);
                        int x = Integer.parseInt(parts[3]);
                        int y = Integer.parseInt(parts[4]);
                        world.getEntity(ex, ey).moveTo(x, y);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates.");
                    }
                } else {
                    System.out.println("Usage: /move x y");
                }
                display.render();
            } else if (input.startsWith("/spawn ")) {
                String[] parts = input.split(" ");
                if (parts.length == 3) {
                    try {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        world.addEntity(new Entity(x, y, 100, 1));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates.");
                    }
                } else {
                    System.out.println("Usage: /spawn x y");
                }
                display.render();
            } else {
                System.out.println("Unknown command.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        new Main();
    }
}