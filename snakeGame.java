import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
//For image
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class snakeGame extends JPanel implements ActionListener, KeyListener {
    private BufferedImage backgroundIMG;
    private BufferedImage headIMG;
    private BufferedImage bodyIMG;
    private BufferedImage foodIMG;

    private class tile {

        int x;
        int y;

        tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int bordWidth;
    int bordHeight;
    int tileSize = 40;

    // food that's on random places at the game.
    tile food;
    Random random;

    // snake with array
    tile snakeHead;
    ArrayList<tile> snakeBody;

    // game logic
    Timer gameLoop;
    int VX;
    int VY;
    boolean gameOver = false;
    private JButton restartButton;

    snakeGame(int bordWidth, int bordHeight) {
        setFocusable(true);
        // button style
        setLayout(null); 

        restartButton = new JButton("RESTART");
        restartButton.setFocusable(false);
        restartButton.setBounds(bordWidth / 2 - 80, bordHeight / 2 + 100, 160, 50);

        // color and buttonstyle 
        restartButton.setBackground(Color.black); 
        restartButton.setForeground(Color.white); 
        restartButton.setFont(new Font("Monospaced", Font.BOLD, 24));
        restartButton.setBorder(BorderFactory.createLineBorder(Color.white, 3)); 
        restartButton.setFocusPainted(false);  
        restartButton.setContentAreaFilled(false); 
        restartButton.setOpaque(true); 

        restartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                restartButton.setBackground(Color.darkGray);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                restartButton.setBackground(Color.black);
            }
        });

        // reset the game 
        restartButton.addActionListener(e -> reset());

        restartButton.setVisible(false);
        add(restartButton);

        this.bordWidth = bordWidth; // saving size
        this.bordHeight = bordHeight; // saving size
        setPreferredSize(new Dimension(this.bordWidth, this.bordHeight));
        setBackground(Color.black);

        addKeyListener(this);

        try {
            backgroundIMG = ImageIO.read(new File("backgroundIMG.png"));

        } catch (Exception error) {
            error.printStackTrace();
            backgroundIMG = null;
        }
        try {
            headIMG = ImageIO.read(new File("head.png"));

        } catch (Exception error) {
            error.printStackTrace();
            headIMG = null;
        }
        try {
            bodyIMG = ImageIO.read(new File("body.png"));
        } catch (Exception error) {
            error.printStackTrace();
            bodyIMG = null;
        }
        try {
            foodIMG = ImageIO.read(new File("food.png"));
        } catch (Exception error) {
            error.printStackTrace();
            foodIMG = null;
        }

        snakeHead = new tile(5, 5);
        snakeBody = new ArrayList<tile>();

        food = new tile(10, 10);
        random = new Random();
        feed();

        VX = 0;
        VY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        if (backgroundIMG != null) {
            g.drawImage(backgroundIMG, 0, 0, bordWidth, bordHeight, null);
        }

        // food
        if (foodIMG != null) {
            g.drawImage(foodIMG, food.x * tileSize, food.y * tileSize, tileSize, tileSize, null);
        } else {
            g.setColor(Color.pink);
            g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);
        }

        // snake head roated
        if (headIMG != null) {
            Graphics2D g2 = (Graphics2D) g.create(); // Graphics2D to rotate

            int px = snakeHead.x * tileSize;
            int py = snakeHead.y * tileSize;

            // angle with VX/VY and the mouth is on the top
            double angle = 0.0;
            if (VX == 0 && VY == -1) { //up 
                angle = 0.0;
            } else if (VX == 1 && VY == 0) { // right 
                angle = Math.PI / 2.0;
            } else if (VX == 0 && VY == 1) { // down 
                angle = Math.PI;
            } else if (VX == -1 && VY == 0) { // left 
                angle = -Math.PI / 2.0;
            }

            // Rotera around the middle
            g2.rotate(angle, px + tileSize / 2.0, py + tileSize / 2.0);
            g2.drawImage(headIMG, px, py, tileSize, tileSize, null);
            g2.dispose();
        } else {
            g.setColor(Color.orange);
            g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);
        }

        // snake body rotated
        for (int i = 0; i < snakeBody.size(); i++) {
            tile seg = snakeBody.get(i);

            tile ahead;
            if (i == 0) {
                ahead = snakeHead; // the first one follow the head
            } else {
                ahead = snakeBody.get(i - 1); // They follow the snakehead, the first one.
            }

            int px = seg.x * tileSize;
            int py = seg.y * tileSize;

            if (bodyIMG != null) {

                int dx = ahead.x - seg.x;
                int dy = ahead.y - seg.y;

                double angle = 0.0;
                if (dx == 0 && dy == -1) { // upp
                    angle = 0.0;
                }
                if (dx == 1 && dy == 0) { // right
                    angle = Math.PI / 2.0;
                }
                if (dx == 0 && dy == 1) { // down
                    angle = Math.PI;
                }
                if (dx == -1 && dy == 0) { // left
                    angle = -Math.PI / 2.0;
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.rotate(angle, px + tileSize / 2.0, py + tileSize / 2.0);
                g2.drawImage(bodyIMG, px, py, tileSize, tileSize, null);
                g2.dispose();
            } else {
                // if the picture is missing
                g.fill3DRect(px, py, tileSize, tileSize, true);
            }
        }

        // Score
        if (gameOver) {
            String gameOverText = "GAME OVER!";
            String scoreText = "FINAL SCORE: " + snakeBody.size();

            // make the font bigger on Game Over
            Font bigFont = new Font("Monospaced", Font.BOLD, 50);
            g.setFont(bigFont);

            // center and get the size
            FontMetrics fm = g.getFontMetrics(bigFont);
            int textWidth = fm.stringWidth(gameOverText);
            int x = (bordWidth - textWidth) / 2;
            int y = bordHeight / 2;

            // Shadow
            g.setColor(Color.black);
            g.drawString(gameOverText, x + 3, y + 3);

            // the text
            g.setColor(Color.white);
            g.drawString(gameOverText, x, y);

            // score under
            Font scoreFont = new Font("Monospaced", Font.BOLD, 30);
            g.setFont(scoreFont);
            fm = g.getFontMetrics(scoreFont);
            int scoreWidth = fm.stringWidth(scoreText);
            int scoreX = (bordWidth - scoreWidth) / 2;

            g.setColor(Color.black);
            g.drawString(scoreText, scoreX + 2, y + 52 + 2);

            g.setColor(Color.white);
            g.drawString(scoreText, scoreX, y + 52);

            // the text during the game

        } else {
            String text = "SCORE: " + snakeBody.size();
            g.setFont(new Font("Monospaced", Font.BOLD, 30));
            g.setColor(Color.black);
            g.drawString(text, tileSize + 2, tileSize + 32);
            g.setColor(Color.white);
            g.drawString(text, tileSize, tileSize + 30);
        }

    }

    // feed
    public void feed() {
        food.x = random.nextInt(bordWidth / tileSize);
        food.y = random.nextInt(bordHeight / tileSize);
    }

    // move
    public void move() {
        // eat food
        if (bump(snakeHead, food)) {
            snakeBody.add(new tile(food.x, food.y));
            feed();
        }
        // snake body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        // snake head game
        snakeHead.x += VX;
        snakeHead.y += VY;

        // game over then bump
        for (int i = 0; i < snakeBody.size(); i++) {
            tile snakePart = snakeBody.get(i);
            // snakeHead to snakeBody
            if (bump(snakeHead, snakePart)) {
                gameOver = true;
            }

        }
        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > bordWidth
                || snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > bordHeight) {
            gameOver = true;
        }

    }

    public boolean bump(tile t1, tile t2) {
        return t1.x == t2.x && t1.y == t2.y;

    }

    // Gameover stop and show button restart
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            restartButton.setVisible(true); // visa knappen
        }
    }

    // Handles arrow key input. Prevents 180° turns.+ möjligheten att starta om med
    // funktionen reset
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && VY != 1) {
            VX = 0;
            VY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && VY != -1) {
            VX = 0;
            VY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && VX != 1) {
            VX = -1;
            VY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && VX != -1) {
            VX = 1;
            VY = 0;
        }
    }

    private void reset() {
        snakeHead = new tile(5, 5);
        snakeBody.clear();
        VX = 0;
        VY = 0;
        gameOver = false;
        feed();

        restartButton.setVisible(false); // hide button again
        requestFocusInWindow();
        gameLoop.start();
    }

    // These methods are not used in this game, but must be included because of the KeyListener interface.
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
