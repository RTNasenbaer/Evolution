import world.World;

public class Main {

    private World world;

    public static void main(String[] args) {
        new Main();
    }

    Main() {
        world = new World();
        new Display(world).render();
    }
}