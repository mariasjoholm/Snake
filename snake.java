//import java.awt.*;
import javax.swing.*;

public class snake {
    public static void main(String[] args) {
        //Define width in the gamebord and then make the height the same 
        int bordWidth = 600; 
        int bordHeight = bordWidth; 
        
        //The window 
        JFrame  f = new JFrame("Snake"); 
        f.setVisible(true);
        f.setSize(bordWidth,bordHeight); 
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        snakeGame snakeGame = new snakeGame(bordWidth, bordHeight); 
        f.add(snakeGame);
        f.pack();
        snakeGame.requestFocus(); 
    }
}
