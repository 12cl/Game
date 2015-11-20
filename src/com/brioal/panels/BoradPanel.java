package com.brioal.panels;

import com.brioal.frames.BoradFrame;
import com.brioal.frames.WelcomeFrame;
import com.brioal.interfaces.DiyViews;
import com.brioal.override_view.BlankPanel;
import com.brioal.override_view.ImageButton;
import com.brioal.socket.Client;
import com.brioal.socket.Server;
import com.brioal.utool.Calculate;
import com.brioal.utool.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by null on 15-11-6
 * ����Service����Client��Get������������ӣ����ڴ���Service����Client�����ʱ��Ҫ����BoradPanel����
 * �������ӵ��õ���Service����Client��Put����
 */
public class BoradPanel extends JPanel implements DiyViews, ActionListener {
    private java.util.List<Point> points;  //���ڴ�������д��ڵĵ�
    private Server server = null; // Service����
    private Client client = null; // Client����
    Calculate calculate;
    private int state_start; //����������ʽ
    private int state_color; // �������õ�������ɫ
    private BlankPanel beginpanel; // ��ʼ����ʾ���
    private JLabel label_messagge; // ��ʼ����ʾ��Ϣ
    private JButton btnBegin;
    private int flag = 1;

    //��������� ip�����ͻ���ʹ�ã�  �˿ںţ���ʹ�ã� ������ʽ�������ͻ��˻�ȡ�������ˣ�  ������ɫ�����ӻ���ӣ�
    public BoradPanel(String host, int port, int state_start, int state_color) {
        this.state_start = state_start;
        this.state_color = state_color;
        points = new ArrayList<>();
        initViews();
        setViews();
        addViews();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (state_start == BoradFrame.STATE_SERVICE) {  //������Է�������ʽ������
            System.out.println("�Է�������ʽ������");
            calculate = new Calculate(state_color);
            server = new Server(port, BoradPanel.this, state_color);    //����SocketService
            new Thread() {
                @Override
                public void run() {
                    server.Get(); // �ڿͻ�������֮ǰ����������Ҫ������������������
                }
            }.start();
        } else {
            System.out.println("�Կͻ�����ʽ������");
            calculate = new Calculate(state_color);
            client = new Client(host, WelcomeFrame.port, this, state_color);
            new Thread() {
                @Override
                public void run() {
                    client.Get();
                }
            }.start();
        }

    }

    //ֻ��Ӷ��������ó���һ�������ڵ�һ����ӵ� ֻ�е����ӵ�ʱ��ŵ���
    public void JustAdd() {
        calculate.addPoint(new Point(10, 10, state_color));
        System.out.println("��һ��");
        server.Put("10,10");
        Point point; // //��������һ�������ݷ������Լ�ѡ����ɫ������
        if (state_color == BoradFrame.STATE_BLACK) {
            point = new Point(10, 10, Point.STATE_BLACK);
        } else {
            point = new Point(10, 10, Point.STATE_WHITE);
        }
        points.add(point);
    }

    //����������ӵ����� // ��Ҫ�ж���ѡ��ɫ
    public void addPoint(Point point) {
        System.out.println("�������"+point.getX()+"----"+point.getY());
        if (flag == 1) {
            this.remove(beginpanel);
            updateUI();
            flag++;
        }
        if (calculate.JudegeWin(point)) {
            calculate.addPoint(point);
            points.add(point);  // ������ӵ�list��
            updateUI();

            Point result = calculate.getNext();

            result.setState(state_color);
            calculate.addPoint(result);
            points.add(result);  // ������ӵ�list��
            updateUI();
            System.out.println("����" + result.getX() + "---" + result.getY());
            if (server != null) {
                server.Put(result.getX() + "," + result.getY());
            } else {
                client.Put(result.getX() + "," + result.getY());
            }


        } else {
            System.out.println("��Ϸ����");
            System.exit(0);
        }





//        if (result != null) {

//            if (state_color == BoradFrame.STATE_BLACK) {
//                result.setState(Point.STATE_BLACK);
//            } else {
//                result.setState(Point.STATE_WHITE);
//            }

//        } else {
//            System.out.println("��Ϸ����");
//            System.exit(0);
        }



    @Override
    public void initViews() {
        Font f = new Font("��Բ", Font.PLAIN, 15);
        Font f_label = new Font("��Բ", Font.PLAIN, 17);
        UIManager.put("Label.font", f);
        UIManager.put("Label.foreground", Color.black);
        UIManager.put("Button.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("List.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("Table.font", f);
        //JPanel
        beginpanel = new BlankPanel(200);

        //JLabel
        label_messagge = new JLabel();

        //JButton
        btnBegin = new ImageButton("ȷ��");

    }

    // �������
    @Override
    public void setViews() {
        beginpanel.setBounds(250, 250, 240, 240);
        beginpanel.setLayout(null);

        label_messagge.setBounds(10, 100, 220, 40);
        //Jbutton
        btnBegin.setBounds(80, 180, 80, 40);
        btnBegin.addActionListener(this);
    }

    //������
    @Override
    public void addViews() {
        if (state_color == BoradFrame.STATE_BLACK) {
            label_messagge.setText("�ֵ�������!\n���ȷ����ʼ��Ϸ");
        } else {
            label_messagge.setText("��ȶԷ�����");
        }

        beginpanel.add(label_messagge);
        if (state_start == BoradFrame.STATE_SERVICE) {
            beginpanel.add(btnBegin);
        }
        this.add(beginpanel);
        setLayout(null); // ���ò���Ϊ��
    }

    //�����Ʒ���
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon board = new ImageIcon("drawable/board.png");
        g.drawImage(board.getImage(), 0, 0, 740, 740, null);
//        ���ڲ���λ��
//        ImageIcon icon1 = new ImageIcon("drawable/white.png");
//        g.drawImage(icon1.getImage(), 15,  680, 36, 36, null);
//        g.drawImage(icon1.getImage(),  (14+19*35),680-19*35,35,   35, null);
//        g.drawImage(icon1.getImage(),  14+10*34,695-10*35,35,   35, null);
//        g.drawImage(icon1.getImage(),  682,15,35,   35, null);
//        g.drawImage(icon1.getImage(),  352,15,35,   35, null);

        //ѭ��list�е����е㣬���ݵ����ɫ������ָ����ͬ��ͼƬ��Դ
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Point point : points) {
            ImageIcon icon = null;
            if (point.getState() == Point.STATE_WHITE) {
                icon = new ImageIcon("drawable/white.png"); // ָ��������Դ
            } else {
                icon = new ImageIcon("drawable/black.png"); // ָ��������Դ
            }
            //ָ��λ�û���ָ��ͼƬ
            //�˴������ѿ��Ǳ߽磬ʹ��point��19*19�����е����꼴��
//            g.drawImage(icon.getImage(), point.getX() * 34 + 14, 695-point.getY() * 35 , 36, 36, null);
            g.drawImage(icon.getImage(), point.getX() * 36 - 9, 710 - point.getY() * 36 - 5, 36, 36, null);
        }
    }

    //����ˢ�� ���Զ���Ĳ�ˢ��ȫ����ˢ�·�������Ȼˢ��ȫ������Ӱ���������ܣ�
    //���ڸĽ�
    public void refresh(Graphics graphics) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBegin) {
            this.remove(beginpanel);
            updateUI();
            JustAdd();
        }
    }
}
