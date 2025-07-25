import world.Type;
import world.World;

import java.util.Map;
import java.util.Scanner;

import entities.Entity;

public class Main {

    private World world;
    private Display display;
    private int tickspeed = 200; // Default tickspeed in ms

    Main() {
        world = new World();
        world.addEntity(Entity.createDefaultEntity(0, 0));
        display = new Display(world, Display.DisplayMode.TERMINAL);
        display.render();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();
            if (input.equals("/exit"))
                break;
            if (input.equals("/run")) {
                runSteps(1);
            } else if (input.startsWith("/run ")) {
                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    try {
                        int steps = Integer.parseInt(parts[1]);
                        runSteps(steps);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number of steps.");
                    }
                } else {
                    System.out.println("Usage: /run <steps>");
                }
            } else if (input.startsWith("/move ")) {
                String[] parts = input.split(" ");
                if (parts.length == 3) {
                    try {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        world.getEntities().get(0).moveTo(x, y);
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
                        world.addEntity(Entity.createDefaultEntity(x, y));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates.");
                    }
                } else {
                    System.out.println("Usage: /spawn x y");
                }
                display.render();
            } else if (input.startsWith("/tickspeed ")) {
                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    try {
                        int newTickspeed = Integer.parseInt(parts[1]);
                        if (newTickspeed < 0) {
                            System.out.println("Tickspeed must be non-negative.");
                        } else {
                            tickspeed = newTickspeed;
                            System.out.println("Tickspeed set to " + tickspeed + " ms.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid tickspeed.");
                    }
                } else {
                    System.out.println("Usage: /tickspeed <ms>");
                }
            } else {
                System.out.println("Unknown command.");
            }
        }
        scanner.close();
    }

    private void runSteps(int steps) {
        for (int i = 0; i < steps; i++) {
            for (Entity entity : world.getEntities()) { // Use a copy to avoid concurrent modification
                entity.update(world, tickspeed, World.SIZE);
            }

            // Print entity counts in all biomes
            Map<Type, Integer> biomeEntityCounts = world.countEntitiesInAllBiomes();
            System.out.println("Entity counts by biome after step " + (i + 1) + ":");
            for (Map.Entry<Type, Integer> entry : biomeEntityCounts.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            display.render();
            try {
                Thread.sleep(tickspeed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}