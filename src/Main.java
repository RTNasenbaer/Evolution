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
                display.render();
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
                } else {
                    System.out.println("Usage: /move x y");
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