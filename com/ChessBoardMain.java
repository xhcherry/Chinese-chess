package com;

import static com.ChessConstant.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.chessmove.ChessMovePlay;
import com.chessmove.MoveNode;
import com.chessparam.ChessParam;
import com.evaluate.EvaluateComputeMiddleGame;
import com.zobrist.TranspositionTable;

public class ChessBoardMain extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final String[] chessName = new String[]{
            "   ", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            "�ڽ�", "�ڳ�", "�ڳ�", "����", "����", "����", "����", "����", "����", "��ʿ", "��ʿ", "����", "����", "����", "����", "����",
            "�콫", "�쳵", "�쳵", "����", "����", "����", "����", "����", "����", "��ʿ", "��ʿ", "����", "����", "����", "����", "����",
    };
    public static final String[] chessIcon = new String[]{
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            "BK", "BR", "BR", "BN", "BN", "BC", "BC", "BB", "BB", "BA", "BA", "BP", "BP", "BP", "BP", "BP",
            "RK", "RR", "RR", "RN", "RN", "RC", "RC", "RB", "RB", "RA", "RA", "RP", "RP", "RP", "RP", "RP",
    };
    int lastTimeCheckedSite = -1; //�ϴ�ѡ�����ӵ�λ��
    private final ButtonActionListener my = new ButtonActionListener();
    JLabel[] buttons = new JLabel[BOARDSIZE90];
    int play = 1;
    volatile boolean[] android = new boolean[]{false, false};
    int begin = -1;
    int end = 0;
    private static ComputerLevel computerLevel = ComputerLevel.greenHand; //Ĭ��
    boolean isBackstageThink = false;
    TranspositionTable transTable;
    ChessMovePlay cmp = null;
    AICoreHandler _AIThink = new AICoreHandler();
    AICoreHandler backstageAIThink = new AICoreHandler();
    NodeLink moveHistory;
    int turn_num = 0;//�غ���
    ChessParam chessParamCont;

    public void initHandler() {
        String startFen;
        startFen = readSaved();
        String[] fenArray = Tools.fenToFENArray(startFen);
        int[] boardTemp = Tools.parseFEN(fenArray[1]);
        //�������̳�ʼ����
        chessParamCont = ChessInitialize.getGlobalChessParam(boardTemp);
        //������н���ͼƬ
        //��ʼ��������
        for (int i = 0; i < boardTemp.length; i++) {
            if (boardTemp[i] > 0) {
                this.setBoardIconUnchecked(i, boardTemp[i]);
            }
        }
        //��ʼ����(Ҫ�����Ӱںú���ܼ������ֵ)
        transTable = new TranspositionTable();
        if (moveHistory == null) {
            moveHistory = new NodeLink(1 - play, transTable.boardZobrist32, transTable.boardZobrist64);
        }
        play = 1 - moveHistory.play;
        android[1 - play] = true;
        cmp = new ChessMovePlay(chessParamCont, transTable, new EvaluateComputeMiddleGame(chessParamCont));
    }

    JPanel jpanelContent;

    private void setCenter() {
        if (jpanelContent != null) {
            this.remove(jpanelContent);
        }
        jpanelContent = new javax.swing.JPanel() {
            protected void paintComponent(Graphics g) {
                try {
                    BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/MAIN.GIF")));
                    g.drawImage(img, 0, 0, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        this.setLayout(new BorderLayout());

        JPanel panel = new javax.swing.JPanel();

        jpanelContent.setLayout(new BorderLayout());
        //��
        JPanel jpNorth = new JPanel();
        jpNorth.setPreferredSize(new Dimension(25, 25));
        jpNorth.setBackground(Color.white);
        jpNorth.setOpaque(false);
        jpanelContent.add(jpNorth, BorderLayout.NORTH);
        //��
        JPanel jpSouth = new JPanel();
        jpSouth.setPreferredSize(new Dimension(5, 5));
        jpSouth.setBackground(Color.black);
        jpSouth.setOpaque(false);
        jpanelContent.add(jpSouth, BorderLayout.SOUTH);
        //��
        JPanel jpWest = new JPanel();
        jpWest.setPreferredSize(new Dimension(20, 20));
        jpWest.setBackground(Color.blue);
        jpWest.setOpaque(false);
        jpanelContent.add(jpWest, BorderLayout.WEST);
        //��
        JPanel jpEast = new JPanel();
        jpEast.setPreferredSize(new Dimension(20, 20));
        jpEast.setBackground(Color.CYAN);
        jpEast.setOpaque(false);
        jpanelContent.add(jpEast, BorderLayout.EAST);
        //��
        panel.setLayout(new GridLayout(10, 9));
        panel.setPreferredSize(new Dimension(100, 100));
        panel.setOpaque(false);
        jpanelContent.add(panel, BorderLayout.CENTER);

        for (int i = 0; i < BOARDSIZE90; i++) {
            JLabel p = new JLabel();
            p.addMouseListener(my);
            p.setBackground(Color.red);
            p.setSize(55, 55);
            buttons[i] = p;
            panel.add(p);
        }
        this.add(jpanelContent, BorderLayout.CENTER);
    }

    public ChessBoardMain() {
        super("�й�����");
        setCenter();
        JPanel constrol = new JPanel();
        constrol.setLayout(new GridLayout(1, 3));
        Button button = new Button("����");
        button.addActionListener(my);
        Button computerMove = new Button("��������");
        computerMove.addActionListener(my);
        constrol.add(button);
        constrol.add(computerMove);
        this.add(constrol, BorderLayout.SOUTH);
        this.addWindowListener(my);
        //��ʼ������
        initHandler();
        this.setJMenuBar(setJMenuBar());
        this.setSize(568, 680);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    private final MenuItemActionListener menuItemAction = new MenuItemActionListener();
    JRadioButtonMenuItem hashSize2M = new JRadioButtonMenuItem("HASH��С", true);
    JRadioButtonMenuItem hashSize32M = new JRadioButtonMenuItem("HASH����", false);
    JRadioButtonMenuItem hashSize64M = new JRadioButtonMenuItem("HASH���", false);

    private JMenuBar setJMenuBar() {
        JMenuBar jmb = new JMenuBar();
        JMenu menu_file = new JMenu("�ļ�");
        JMenuItem create = new JMenuItem("�½�");
        JMenuItem save = new JMenuItem("����");
        JRadioButtonMenuItem mi_6 = new JRadioButtonMenuItem("����", true);
        JRadioButtonMenuItem mi_7 = new JRadioButtonMenuItem("����", false);
        JRadioButtonMenuItem mi_8 = new JRadioButtonMenuItem("ҵ��", false);
        JRadioButtonMenuItem mi_9 = new JRadioButtonMenuItem("ר��", false);
        JRadioButtonMenuItem mi_10 = new JRadioButtonMenuItem("��ʦ", false);
        JRadioButtonMenuItem mi_11 = new JRadioButtonMenuItem("�޵�", false);
        ButtonGroup group = new ButtonGroup();
        group.add(mi_6);
        group.add(mi_7);
        group.add(mi_8);
        group.add(mi_9);
        group.add(mi_10);
        group.add(mi_11);
        create.addActionListener(menuItemAction);
        save.addActionListener(menuItemAction);
        mi_6.addActionListener(menuItemAction);
        mi_7.addActionListener(menuItemAction);
        mi_8.addActionListener(menuItemAction);
        mi_9.addActionListener(menuItemAction);
        mi_10.addActionListener(menuItemAction);
        mi_11.addActionListener(menuItemAction);
        create.setMnemonic(10);
        mi_6.setMnemonic(2);
        mi_7.setMnemonic(3);
        mi_8.setMnemonic(4);
        mi_9.setMnemonic(5);
        mi_10.setMnemonic(6);
        menu_file.setMnemonic('0');
        menu_file.add(create);
        menu_file.add(mi_6);
        menu_file.add(mi_7);
        menu_file.add(mi_8);
        menu_file.add(mi_9);
        menu_file.add(mi_10);
        menu_file.add(mi_11);
        menu_file.add(save);
        jmb.add(menu_file);
        JMenu menu_set = new JMenu("����");
        JCheckBoxMenuItem redCmp = new JCheckBoxMenuItem("���Ժ췽", play != REDPLAYSIGN);
        JCheckBoxMenuItem blackCmp = new JCheckBoxMenuItem("���Ժڷ�", play != BLACKPLAYSIGN);
        ButtonGroup hashSizeGroup = new ButtonGroup();
        hashSizeGroup.add(hashSize2M);
        hashSizeGroup.add(hashSize32M);
        hashSizeGroup.add(hashSize64M);
        JCheckBoxMenuItem backstageThink = new JCheckBoxMenuItem("��̨˼��", isBackstageThink);
        redCmp.addActionListener(menuItemAction);
        blackCmp.addActionListener(menuItemAction);
        hashSize2M.addActionListener(menuItemAction);
        hashSize32M.addActionListener(menuItemAction);
        hashSize64M.addActionListener(menuItemAction);
        backstageThink.addActionListener(menuItemAction);
        menu_set.add(blackCmp);
        menu_set.add(redCmp);
        menu_set.add(hashSize2M);
        menu_set.add(hashSize32M);
        menu_set.add(hashSize64M);
        menu_set.add(backstageThink);
        jmb.add(menu_set);
        return jmb;
    }

    public void setBoardIconUnchecked(int site, int chess) {
        if (chess == NOTHING) {
            buttons[site].setIcon(null);
        } else {
            buttons[site].setIcon(getImageIcon(chessIcon[chess]));
        }
    }

    public void setBoardIconChecked(int site, int chess) {
        buttons[site].setIcon(getImageIcon(chessIcon[chess] + "S"));
    }

    public void setCheckedLOSS(int play) {
        buttons[chessParamCont.allChess[chessPlay[play]]].setIcon(getImageIcon(chessIcon[chessPlay[play]] + "M"));
    }

    public void move(MoveNode moveNode) {
        if (lastTimeCheckedSite != -1) {
            setBoardIconUnchecked(lastTimeCheckedSite, chessParamCont.board[lastTimeCheckedSite]);
        }
        setBoardIconUnchecked(moveNode.srcSite, NOTHING);
        setBoardIconChecked(moveNode.destSite, moveNode.srcChess);
        lastTimeCheckedSite = moveNode.destSite;
    }

    class ButtonActionListener implements ActionListener, WindowListener, MouseListener {
        public void actionPerformed(ActionEvent e) {
            Button sour = (Button) e.getSource();
            if (sour.getLabel().equals("����")) {
                if (moveHistory.getMoveNode() != null) {
                    MoveNode moveNode = moveHistory.getMoveNode();
                    unMoveNode(moveNode);
                    moveHistory = moveHistory.getLastLink();
                    turn_num--;
                    play = 1 - play; //����˫��
                }
            } else if (sour.getLabel().equals("��������")) {
                if (_AIThink != null) {
                    _AIThink.setStop();
                }
            }
        }

        private boolean checkZFPath(int srcSite, int destSite, int play) {
            if (chessParamCont.board[srcSite] == NOTHING) {
                return false;
            }
            MoveNode moveNode = new MoveNode(srcSite, destSite, chessParamCont.board[srcSite], chessParamCont.board[destSite], 0);
            return cmp.legalMove(play, moveNode);
        }

        private void unMoveNode(MoveNode moveNode) {
            MoveNode unmoveNode = new MoveNode();
            unmoveNode.srcChess = moveNode.destChess;
            unmoveNode.srcSite = moveNode.destSite;
            unmoveNode.destChess = moveNode.srcChess;
            unmoveNode.destSite = moveNode.srcSite;
            unMove(unmoveNode);
            cmp.unMoveOperate(moveNode);
        }

        private void unMove(MoveNode moveNode) {
            if (lastTimeCheckedSite != -1) {
                setBoardIconUnchecked(lastTimeCheckedSite, chessParamCont.board[lastTimeCheckedSite]);
            }
            if (moveNode.srcChess == NOTHING) {
                buttons[moveNode.srcSite].setIcon(null);
            } else {
                setBoardIconUnchecked(moveNode.srcSite, moveNode.srcChess);
            }
            if (moveNode.destChess == NOTHING) {
                buttons[moveNode.destChess].setIcon(null);
            } else {
                setBoardIconChecked(moveNode.destSite, moveNode.destChess);
            }
            lastTimeCheckedSite = moveNode.destSite;
        }

        public void windowActivated(WindowEvent arg0) {
            // TODO Auto-generated method stub
        }

        public void windowClosed(WindowEvent arg0) {
            // TODO Auto-generated method stub
        }

        public void windowClosing(WindowEvent arg0) {
            // TODO Auto-generated method stub
            System.exit(1);
        }

        public void windowDeactivated(WindowEvent arg0) {
            // TODO Auto-generated method stub
        }

        public void windowDeiconified(WindowEvent arg0) {
            // TODO Auto-generated method stub
        }

        public void windowIconified(WindowEvent arg0) {
            // TODO Auto-generated method stub
        }

        public void windowOpened(WindowEvent arg0) {
            // TODO Auto-generated method stub
        }

        public void mouseClicked(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        public void mousePressed(MouseEvent e) {
            if (android[play]) {
                return;
            }
            for (int i = 0; i < buttons.length; i++) {
                JLabel p = buttons[i];
                if (p == e.getSource()) {
                    if (chessParamCont.board[i] != NOTHING && (chessParamCont.board[i] & chessPlay[play]) == chessPlay[play]) {//�Է�����
                        if (i != begin) {
                            begin = i;

                            setBoardIconChecked(i, chessParamCont.board[i]);
                            if (lastTimeCheckedSite != -1) {
                                setBoardIconUnchecked(lastTimeCheckedSite, chessParamCont.board[lastTimeCheckedSite]);
                            }
                            lastTimeCheckedSite = begin;
                        }
                        return;
                    } else if (begin == -1) {
                        return;
                    }
                    end = i;
                    if (this.checkZFPath(begin, end, play)) {
                        MoveNode moveNode = new MoveNode(begin, end, chessParamCont.board[begin], chessParamCont.board[end], 0);
                        showMoveNode(moveNode);
                        NodeLink nextLink = new NodeLink(play, transTable.boardZobrist32, transTable.boardZobrist64);
                        nextLink.setMoveNode(moveNode);
                        moveHistory.setNextLink(nextLink);
                        moveHistory = moveHistory.getNextLink();
                        begin = -1;
                        opponentMove();
                    }
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub
        }
    }

    public void gameOverMsg(String msg) {
        if (JOptionPane.showConfirmDialog(this, msg + "�Ƿ������", "��Ϣ",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dispose();
            new ChessBoardMain();
        } else {
            dispose();
        }
    }

    private ImageIcon getImageIcon(String chessName) {
        String path = "/images/" + chessName + ".GIF";
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
    }

    private boolean checkGameOver() {
        boolean isGameOver = false;
        String msg = null;
        if (moveHistory == null || moveHistory.getMoveNode() == null) {
            msg = (play == BLACKPLAYSIGN ? "�ڷ�" : "�췽") + "�����̵Ľ�����";
            isGameOver = true;
            //�Լ�˧����
        } else if (chessParamCont.allChess[chessPlay[BLACKPLAYSIGN]] == NOTHING || moveHistory.getMoveNode().destChess == chessPlay[BLACKPLAYSIGN]) {
            isGameOver = true;
            msg = "�ڷ�����Ű��";
        } else if (chessParamCont.allChess[chessPlay[REDPLAYSIGN]] == NOTHING || moveHistory.getMoveNode().destChess == chessPlay[REDPLAYSIGN]) {
            msg = "�췽����Ű��";
            isGameOver = true;
        } else if (moveHistory.getMoveNode().score == -LONGCHECKSCORE) {
            msg = (play == BLACKPLAYSIGN ? "�ڷ�" : "�췽") + "�����и���";
            isGameOver = true;
        } else if (moveHistory.getMoveNode().score <= -(maxScore - 2)) {
            setCheckedLOSS(play);
            msg = (play == BLACKPLAYSIGN ? "�ڷ�" : "�췽") + "�����̵Ľ�����";
            isGameOver = true;
        } else if (moveHistory.getMoveNode().score >= (maxScore - 2)) {
            setCheckedLOSS(1 - play);
            msg = (play == BLACKPLAYSIGN ? "�ڷ�" : "�췽") + "Ӯ�������յ�ʤ����";
            isGameOver = true;
        } else if (chessParamCont.getAttackChessesNum(REDPLAYSIGN) == 0 && chessParamCont.getAttackChessesNum(BLACKPLAYSIGN) == 0) {
            msg = "˫�����޹������Ӵ��˺��壡";
            isGameOver = true;
        } else if (turn_num >= 300) {
            msg = "��ս300�غ�δ��ʤ������";
            isGameOver = true;
        }
        if (isGameOver) {
            gameOverMsg(msg);
        }
        return isGameOver;
    }

    class MenuItemActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if ("�½�".equals(actionCommand)) {
                dispose();
                new ChessBoardMain();
            } else if ("����".equalsIgnoreCase(actionCommand)) {
                Tools.saveFEN(chessParamCont.board, moveHistory);
            } else if ("����".equals(actionCommand)) {
                computerLevel = ComputerLevel.greenHand;
            } else if ("����".equals(actionCommand)) {
                computerLevel = ComputerLevel.introduction;
            } else if ("ҵ��".equals(actionCommand)) {
                computerLevel = ComputerLevel.amateur;
            } else if ("ר��".equals(actionCommand)) {
                computerLevel = ComputerLevel.career;
            } else if ("��ʦ".equals(actionCommand)) {
                computerLevel = ComputerLevel.master;
            } else if ("�޵�".equals(actionCommand)) {
                computerLevel = ComputerLevel.invincible;
            } else if ("���Ժ췽".equals(actionCommand)) {
                android[REDPLAYSIGN] = !android[REDPLAYSIGN];
                if (android[REDPLAYSIGN] && (REDPLAYSIGN == play || turn_num <= 0)) {
                    if (turn_num <= 0) {
                        play = REDPLAYSIGN;
                        moveHistory.play = 1 - REDPLAYSIGN;
                    }
                    computeThinkStart();
                }
            } else if ("���Ժڷ�".equals(actionCommand)) {
                android[BLACKPLAYSIGN] = !android[BLACKPLAYSIGN];
                if (android[BLACKPLAYSIGN] && (BLACKPLAYSIGN == play || turn_num <= 0)) {
                    if (turn_num <= 0) {
                        play = BLACKPLAYSIGN;
                        moveHistory.play = 1 - BLACKPLAYSIGN;
                    }
                    computeThinkStart();
                }
            } else if ("HASH��С".equals(actionCommand)) {
                if (turn_num == 0) {
                    TranspositionTable.setHashSize(0x7FFFF);
                }
            } else if ("HASH����".equals(actionCommand)) {
                if (turn_num == 0) {
                    TranspositionTable.setHashSize(0xFFFFF);
                }
            } else if ("HASH���".equals(actionCommand)) {
                if (turn_num == 0) {
                    TranspositionTable.setHashSize(0x1FFFFF);
                }
            } else if ("��̨˼��".equals(actionCommand)) {
                isBackstageThink = !isBackstageThink;
            }
        }
    }

    private void opponentMove() {
        setHashTablesEnabled();
        //�鿴�Ƿ���ʤ��
        if (!checkGameOver()) {
            turn_num++;
            play = 1 - play; //����˫��
            //�����Ƿ�Ϊ����
            if (android[play]) {
                computeThinkStart();
            }
        }
    }

    private void computeThinkStart() {
        //���ú�̨˼��
        if (isBackstageThink && (guessLink != null && moveHistory != null)) {
            //�鿴�Ƿ����
            if (guessLink.getMoveNode().equals(moveHistory.getMoveNode())) {
                new Thread(() -> {
                    System.out.println("---->�²����У���");
                    try {
                        //����ʱ�����
                        backstageAIThink.launchTimer();
                        backstageThinkThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        computeThink();
                    }
                    computeAIMoving(guessLink.getNextLink());
                }).start();
            } else {
                new Thread(() -> {
                    System.out.println("--->δ����");
                    //���û�н�������
                    backstageAIThink.setStop();
                    try {
                        backstageThinkThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("--->����˼��");
                    computeThink();
                }).start();
            }
        } else {
            computeThink();
        }
    }

    private void computeThink() {
        new Thread(() -> {
            _AIThink.setLocalVariable(computerLevel, chessParamCont, moveHistory);
            _AIThink.launchTimer();
            _AIThink.run();
            computeAIMoving(moveHistory.getNextLink());
        }).start();
    }

    private void computeAIMoving(NodeLink nodeLink) {
        moveHistory = nodeLink;
        // if(!checkGameOver()){
        if (nodeLink != null && nodeLink.getMoveNode() != null) {
            MoveNode moveNode = nodeLink.getMoveNode();
            showMoveNode(moveNode);
        }
        opponentMove();
        backstageThink();
        // }
    }

    private Thread backstageThinkThread = null;
    private NodeLink guessLink;

    //��̨˼��
    private void backstageThink() {
        if (!isBackstageThink) {
            return;
        }
        if (moveHistory.getNextLink() != null && moveHistory.getNextLink().getMoveNode() != null) {
            backstageThinkThread = new Thread(() -> {
                //�²���ŷ�
                guessLink = moveHistory.getNextLink();
                backstageAIThink.setLocalVariable(computerLevel, chessParamCont, guessLink);
                System.out.println("---->��ʼ�²�(" + guessLink.getMoveNode() + ")");
                backstageAIThink.guessRun(guessLink.getMoveNode());
            });
            backstageThinkThread.start();
        }
    }

    private void showMoveNode(MoveNode moveNode) {
        if (moveNode != null) {
            move(moveNode);
            cmp.moveOperate(moveNode);
            transTable.synchroZobristBoardToStatic();
        }
    }

    private void setHashTablesEnabled() {
        hashSize2M.setEnabled(false);
        hashSize32M.setEnabled(false);
        hashSize64M.setEnabled(false);
    }

    //��ȡ�ϴα����¼
    public String readSaved() {
        String fen = null;
        FileInputStream fileInput = null;
        try {
            File chessFile = new File("chess.txt");
            fileInput = new java.io.FileInputStream(chessFile);
            BufferedReader bufferedReader = new BufferedReader(
                    new java.io.InputStreamReader(fileInput));
            while (bufferedReader.ready()) {
                fen = bufferedReader.readLine();
            }
            if (fen != null) {
                if (JOptionPane.showConfirmDialog(this, "��⵽�д浵�Ƿ�����ϴ���Ϸ?", "��Ϣ",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try (ObjectInputStream objInput = new ObjectInputStream(new FileInputStream("moves.dat"))) {
                        moveHistory = (NodeLink) objInput.readObject();
                        turn_num = 20;
                    } catch (Exception e) {
                        System.err.println("========��ȡ��ʷ��¼���� moves.dat");
                    }
                } else {
                    chessFile.deleteOnExit();
                    fen = "c6c5  rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR b - - 0 1";
                }
            }
        } catch (Exception e) {
            fen = "c6c5  rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR b - - 0 1";
        } finally {
            if (fileInput != null) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fen;
    }

    public static void main(String[] args) {
        new ChessBoardMain();
    }
}