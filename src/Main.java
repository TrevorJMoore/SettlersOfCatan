package src;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.*;

public class Main extends JPanel 
{
    private final int WIDTH = 900;
    private final int HEIGHT = 600;
    private JButton jButton1 = new JButton("    ");
    private Font m_font = new Font("Arial", Font.BOLD, 18);
    FontMetrics metrics;
    Polygon cursor = new Polygon();
    Board m_board = new Board(WIDTH, HEIGHT);

    public Main() 
    {
        m_board.addPlayer(new Player("Bob"), new SimpleStrategy());
        m_board.addPlayer(new Player("Joe"));
        m_board.addPlayer(new Player("Sue"));
        m_board.addPlayer(new Player("Amber"));
        add(jButton1);
        jButton1.setMargin(new Insets(2, 2, 2, 2));
        jButton1.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent evt){jButton1_ActionPerformed(evt);}            });
        jButton1.setBackground(m_board.currentPlayer().getColor());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addMouseListener(new HitTestAdapter());
        addMouseMotionListener(new MouseMoveAdapter());
    }

    public void jButton1_ActionPerformed(ActionEvent evt)
    {
        m_board.nextPlayer();
        jButton1.setBackground(m_board.currentPlayer().getColor());
    }

    @Override
    public void paintComponent(Graphics g) 
    {
        Graphics2D g2d = (Graphics2D) g;
        Color tmpC = g.getColor();

        g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g2d.setFont(m_font);

        int xpoints[] = {0, WIDTH , WIDTH, 0};
        int ypoints[] = {0, 0, HEIGHT, HEIGHT };

        g.setColor(new Color(0xFFFFFF));
        g.drawPolygon(xpoints, ypoints, xpoints.length);

        // let board draw itself
        m_board.paintComponent(g);

        g.setColor(new Color(0xFFFFFF));
        g.fillPolygon(cursor);

        g.setColor(tmpC);

        repaint();
    }

    class HitTestAdapter extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent e) 
        {
            int x = e.getX();
            int y = e.getY();
            Rectangle r = new Rectangle(x - 10, y - 10, 20, 20);
            m_board.build(r);
            getParent().repaint();
        }
    }

    class MouseMoveAdapter implements MouseMotionListener
    {
        public void mouseMoved(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();
            Rectangle r = new Rectangle(x - 10, y - 10, 20, 20);

            int count = m_board.tileCount(r);
            if (count == 2)  // road
                cursor = new SOC.piece(SOC.buildType.ROAD, m_board.currentPlayer().getColor(), e.getPoint()).shape();
            if (count == 3)  // junction
                cursor = new SOC.piece(SOC.buildType.SETTLEMENT, m_board.currentPlayer().getColor(), e.getPoint()).shape();

            getParent().repaint();
        }

        public void mouseDragged(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();
            //System.out.println("Move:" + x + ", " + y);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        Main p = new Main();

        f.setContentPane(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

}