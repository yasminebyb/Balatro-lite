package app;

import controller.GameController;
import view.ConsoleView;
import view.View;
import view.Zen6View;
import domain.Blind;
import domain.StandardBlind;
import model.GameState;

import java.util.List;

public class Main {
    public static void main(String[] args) {
    	// just for fun :)
        List<Blind> blinds = List.of(
            new StandardBlind("b1 : florence", 300),
            new StandardBlind("b2 : marguarita", 800),
            new StandardBlind("b3 : truc", 2000)
        );
        GameState state = new GameState(blinds, 4);
        
        //Choix pour lancer en mode console ou en mode interface 
        
        //View view = new ConsoleView();
        View view = new Zen6View();
        
        new GameController(state, view).run();
    }
}
