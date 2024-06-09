import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;  //num of objects
    static final int DELAY = 150;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    //Snake body  & apple
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY,this);  //how fast the game runs
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            //Grid
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT); //vertical line
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);  //horizontal line
            }

            //Apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            //Snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    //Draw Snake Head
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    //Draw Snake Body
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            //Game Score Text
            g.setColor(Color.white);
            g.setFont(new Font("Arial",Font.ITALIC,25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+ applesEaten,(SCREEN_WIDTH - metrics.stringWidth("Score: "+ applesEaten))/15,SCREEN_HEIGHT/15);
        }
        else {
            gameOver(g);
            //Game Score Text
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD,25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+ applesEaten,(SCREEN_WIDTH - metrics.stringWidth("Score: "+ applesEaten))/20,SCREEN_HEIGHT/15);
        }
    }

    /*
    * Generates a new Apple at a random place */
    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    public void move() {
        //move the snake body forward
        for(int i=bodyParts; i>0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        //move the snake head (Up, Down, Left, Right)
        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    /*
    * Grow snake body for each apple eaten */
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    /*
    *Check for the GameOver conditions*/
    public void checkCollisions() {
        //If head collides with body
        for (int i= bodyParts; i>0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        //If head touches left border
        if (x[0] < 0) {running = false;}

        //If head touches right border
        if (x[0] >= SCREEN_WIDTH) {running = false;}

        //If head touches Top border
        if (y[0] < 0) {running = false;}

        //If head touches Bottom border
        if (y[0] >= SCREEN_HEIGHT) {running = false;}

        //stop the timer/game
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Game over Text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD,40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over!",(SCREEN_WIDTH - metrics.stringWidth("Game Over!"+ applesEaten))/2,SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    /*
    * Controlling the Snake to move with directions*/
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') { //avoid 180 turning
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') { //avoid 180 turning
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') { //avoid 180 turning
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') { //avoid 180 turning
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}