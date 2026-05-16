package app;

import controller.GameController;
import domain.Blind;
import domain.StandardBlind;
import model.GameState;
import view.ConsoleView;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Blind> blinds = List.of(
            new StandardBlind("Petit aveugle", 300),
            new StandardBlind("Grand aveugle", 800),
            new StandardBlind("Boss", 2000)
        );
        var state = new GameState(blinds, 4);
        var view = new ConsoleView();
        new GameController(state, view).run();
    }
}