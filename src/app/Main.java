package app;

import controller.GameController;
import domain.Blind;
import domain.StandardBlind;
import model.GameState;

import java.util.List;

public class Main {
    public static void main(String[] args) {
    	// just for fun :)
        List<Blind> blinds = List.of(
            new StandardBlind("b1 : florance", 300),
            new StandardBlind("b2 : margarita", 800),
            new StandardBlind("b3 : truc", 2000)
        );
        GameState state = new GameState(blinds, 4);
        new GameController(state).run();
    }
}
