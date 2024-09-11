import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int EKRAN_SZEROKOSC = 500;
    static final int EKRAN_WYSOKOSC = 500;
    static final int WIELKOSC_BLOKU = 25;
    static final int EKRAN_WIELKOSC = (EKRAN_SZEROKOSC * EKRAN_WYSOKOSC) / WIELKOSC_BLOKU;
    static final int FPS = 80;

    final int x[] = new int[EKRAN_WIELKOSC];
    final int y[] = new int[EKRAN_WIELKOSC];
    int czesciWenza = 4;
    int zjedz;
    int blokx;
    int bloky;
    char direstion = 'R';
    boolean running = false;
    public int opcje = 0;
    Timer timer;
    Random random;


    JButton startNormalButton;
    JButton startOptionalButton;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(EKRAN_SZEROKOSC, EKRAN_WYSOKOSC));
        this.setBackground(Color.white);
        this.setFocusable(true);
        this.setLayout(null);
        this.addKeyListener(new MyKeyAdapter());




        startMenu();
    }

    private void startMenu() {
        startNormalButton = new JButton("Normal - klasyczna gra w węża");
        startNormalButton.setBounds(EKRAN_SZEROKOSC / 2 - 150, EKRAN_WYSOKOSC / 2 - 50, 300, 50);
        startNormalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNormalButton.setVisible(false);
                startOptionalButton.setVisible(false);
                if (timer == null) {
                    opcje = 0;
                    startGame();
                } else {
                    opcje = 0;
                    restartGame();
                }
            }
        });
        this.add(startNormalButton);

        startOptionalButton = new JButton("Optional - możliwość przechodzenia przez ściany");
        startOptionalButton.setBounds(EKRAN_SZEROKOSC / 2 - 175, EKRAN_WYSOKOSC / 2, 350, 50);
        startOptionalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNormalButton.setVisible(false);
                startOptionalButton.setVisible(false);
                if (timer == null) {
                    opcje = 1;
                    startGame();
                } else {
                    opcje = 1;
                    restartGame();
                }
            }
        });
        this.add(startOptionalButton);
    }

    public void restartGame() {
        czesciWenza = 4;
        zjedz = 0;
        direstion = 'R';
        running = true;
        x[0] = 0;
        y[0] = 0;
        newBlock();
        timer.restart();

    }

    public void startGame() {
        czesciWenza = 4;
        zjedz = 0;
        direstion = 'R';
        running = true;
        newBlock();
        timer = new Timer(FPS, this);
        timer.start();
    }

    private void newBlock() {
        boolean onSnake;
        do {
            onSnake = false;
            blokx = random.nextInt(EKRAN_WYSOKOSC / WIELKOSC_BLOKU) * WIELKOSC_BLOKU;
            bloky = random.nextInt(EKRAN_SZEROKOSC / WIELKOSC_BLOKU) * WIELKOSC_BLOKU;

            // Check if the block is on the snake
            for (int i = 0; i < czesciWenza; i++) {
                if (x[i] == blokx && y[i] == bloky) {
                    onSnake = true;
                    break;
                }
            }
        } while (onSnake);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (int i = 0; i < EKRAN_WIELKOSC; i++) {
            g.drawLine(i * WIELKOSC_BLOKU, 0, i * WIELKOSC_BLOKU, EKRAN_WYSOKOSC);
            g.drawLine(0, i * WIELKOSC_BLOKU, EKRAN_SZEROKOSC, i * WIELKOSC_BLOKU);
        }

        g.setColor(Color.red);
        g.fillRect(blokx, bloky, WIELKOSC_BLOKU, WIELKOSC_BLOKU);

        for (int i = 0; i < czesciWenza; i++) {
            if (i == 0) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], WIELKOSC_BLOKU, WIELKOSC_BLOKU);
            } else {
                g.setColor(Color.blue);
                g.fillRect(x[i], y[i], WIELKOSC_BLOKU, WIELKOSC_BLOKU);
            }
        }

        punkty(g);
        if (!running) {
           gameOver(g);
        }




    }

    public void punkty(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Punkty: " + zjedz, (EKRAN_SZEROKOSC - metrics.stringWidth("Game Over")) / 2, WIELKOSC_BLOKU);
    }

    public void move() {
        for (int i = czesciWenza; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direstion) {
            case 'U':
                y[0] = y[0] - WIELKOSC_BLOKU;
                break;
            case 'D':
                y[0] = y[0] + WIELKOSC_BLOKU;
                break;
            case 'L':
                x[0] = x[0] - WIELKOSC_BLOKU;
                break;
            case 'R':
                x[0] = x[0] + WIELKOSC_BLOKU;
                break;
        }
    }

    public void checkBlock() {
        if (x[0] == blokx && y[0] == bloky) {
            czesciWenza++;
            zjedz++;
            newBlock();
        }
    }

    public void checkCollisions(int opcje) {
        // jeśli głowa walnie w ciało
        for (int i = czesciWenza; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }

        if(opcje == 0){
        // jeśli głowa walnie w ściane
            if (x[0] < 0 || x[0] > EKRAN_SZEROKOSC || y[0] < 0 || y[0] > EKRAN_WYSOKOSC) {
                running = false;
            }

             if (!running) {
                timer.stop();

            }
        }else{
            if (x[0] < 0)
                x[0] = EKRAN_SZEROKOSC;
            else if (x[0] > EKRAN_SZEROKOSC)
                x[0] = 0;
            else if (y[0] < 0)
                y[0] = EKRAN_WYSOKOSC;
            else if (y[0] > EKRAN_WYSOKOSC)
                y[0] = 0;



            if (!running) {
                timer.stop();

            }
    }










    }

    public void gameOver(Graphics g) {


        startNormalButton.setVisible(true);
        startOptionalButton.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions(opcje);
            checkBlock();
        }
        repaint();
    }



    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (direstion != 'D') {
                        direstion = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direstion != 'U') {
                        direstion = 'D';
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (direstion != 'R') {
                        direstion = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direstion != 'L') {
                        direstion = 'R';
                    }
                    break;
            }
        }
    }
}
