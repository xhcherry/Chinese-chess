package com.evaluate;

import static com.ChessConstant.*;

import com.BitBoard;
import com.Tools;
import com.chessparam.ChessParam;

public class EvaluateComputeMiddle extends EvaluateCompute {

    //ÿ�����ӻ����Գͷ�
    public static final int[] chessMobilityRewards = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            //����   ��     ��
            50, 10, 10, 20, 20, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            50, 10, 10, 20, 20, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    //ÿ��������ͻ�����Ҫ��(���ڴ�ֵҪ��)
    public static final int[] chessMinMobility = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            //����   ��     ��
            1, 6, 6, 4, 4, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            1, 6, 6, 4, 4, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    public EvaluateComputeMiddle(ChessParam chessParam) {
        this.chessParam = chessParam;
        init();
    }

    int[] score = new int[2];
    BitBoard[] bitBoard = new BitBoard[2];
    BitBoard[] bitBoardAttack = new BitBoard[2];

    public int evaluate(int play) {
        score[REDPLAYSIGN] = chessParam.baseScore[REDPLAYSIGN];
        score[BLACKPLAYSIGN] = chessParam.baseScore[BLACKPLAYSIGN];
        bitBoard[REDPLAYSIGN] = new BitBoard(chessParam.maskBoardPersonalChesses[REDPLAYSIGN]);
        bitBoard[BLACKPLAYSIGN] = new BitBoard(chessParam.maskBoardPersonalChesses[BLACKPLAYSIGN]);
        chessParam.getRoleIndexByPlayRole(REDPLAYSIGN, CHARIOT);
        chessParam.getRoleIndexByPlayRole(REDPLAYSIGN, CHARIOT);
        bitBoardAttack[0] = new BitBoard();
        bitBoardAttack[1] = new BitBoard();
        //�õ���������ͼ
        for (int chess = 16; chess < 48; chess++) {
            if (chessParam.allChess[chess] != NOTHING) {
                if (chess < 32) {
                    BitBoard bAttack = chessAllMove(chessRoles[chess], chessParam.allChess[chess], BLACKPLAYSIGN);
                    bitBoardAttack[BLACKPLAYSIGN].assignOr(bAttack);
                    //�л����Բ���
                    if (chessMinMobility[chess] > 0) {
                        int mobility = this.chessMobility(chessRoles[chess], chessParam.allChess[chess], bitBoard[BLACKPLAYSIGN]);
                        //<��ͻ�����Ҫ����
                        if (mobility < chessMinMobility[chess]) {
                            score[BLACKPLAYSIGN] -= (chessMinMobility[chess] - mobility) * chessMobilityRewards[chess];
                        }
                    }
                } else {
                    BitBoard bAttack = chessAllMove(chessRoles[chess], chessParam.allChess[chess], REDPLAYSIGN);
                    bitBoardAttack[REDPLAYSIGN].assignOr(bAttack);
                    //�л����Բ���
                    if (chessMinMobility[chess] > 0) {
                        int mobility = this.chessMobility(chessRoles[chess], chessParam.allChess[chess], bitBoard[REDPLAYSIGN]);
                        //<��ͻ�����Ҫ����
                        if (mobility < chessMinMobility[chess]) {
                            score[REDPLAYSIGN] -= (chessMinMobility[chess] - mobility) * chessMobilityRewards[chess];
                        }
                    }
                }
            }
        }
        return score[play] - score[1 - play];
    }

    //��
    public final int[] blackKnightAttach = {
            -60, -35, -20, -20, -20, -20, -20, -35, -60,
            -35, 0, +20, +20, -70, +20, +20, 0, -35,
            -35, 0, +20, +20, +20, +20, +20, 0, -35,
            -35, 0, +20, +20, +56, +20, +20, 0, -35,
            -35, +40, +40, +50, +60, +50, +40, +40, -35,

            -30, +45, +60, +70, +70, +70, +60, +45, -30,
            -30, +50, +60, +75, +75, +75, +60, +50, -30,
            -30, +50, +80, +90, +90, +90, +80, +50, -30,
            -30, +50, +90, +80, +40, +80, +90, +50, -30,
            -60, +10, +20, +20, -20, +20, +20, +10, -60
    };
    //��
    public final int[] blackGunAttach = {
            -50, -20, -20, -20, -20, -20, -20, -20, -50,
            -20, +30, +40, +50, +30, +50, +40, +30, -20,
            -20, +30, +40, +50, +60, +50, +40, +30, -20,
            -20, +30, +40, +40, +60, +40, +40, +30, -20,
            -20, +30, +45, +45, +60, +45, +45, +30, -20,

            -20, +20, +20, +20, +51, +20, +20, +20, -20,
            -20, +20, +20, +10, +50, +10, +20, +20, -20,
            -20, +20, +20, 0, 0, 0, +20, +20, -20,
            -20, +20, +20, 0, 0, 0, +20, +20, -20,
            -30, +50, +30, +10, -10, +10, +30, +50, -30
    };
    //��
    public final int[] blackChariotAttach = {
            -60, -20, -20, -20, -20, -20, -20, -20, -60,
            -20, +10, +10, +30, -40, +30, +10, +10, -20,
            -20, +15, +15, +30, +10, +30, +15, +15, -20,
            -20, +30, +30, +30, +40, +30, +30, +30, -20,
            -20, +50, +50, +80, +60, +80, +50, +50, -20,

            -20, +50, +50, +80, +60, +80, +50, +50, -20,
            -20, +40, +40, +50, +50, +50, +40, +40, -20,
            -20, +40, +40, +50, +50, +50, +40, +40, -20,
            -20, +40, +40, +60, +60, +60, +40, +40, -20,
            -30, +20, +20, +20, +20, +20, +20, +20, -30
    };
    //��
    public final int[] blackSoldierAttach = {

            0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, +15, 0, 0, 0, 0
            , +20, 0, +45, 0, +35, 0, +45, 0, +20
            , +80, +100, +120, +120, +120, +120, +120, +100, +80
            , +100, +120, +150, +180, +180, +180, +150, +120, +100
            , +100, +150, +200, +250, +250, +250, +200, +150, +100
            , +100, +150, +200, +250, +300, +250, +200, +150, +100
            , +100, +100, +100, +100, +100, +100, +100, +100, +100
    };
    //��
    public final int[] ElephantAttch = {

            0, 0, 15, 0, 0, 0, 15, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , -10, 0, 0, 0, 40, 0, 0, 0, -10
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, -10, 0, 0, 0, -10, 0, 0

            , 0, 0, -10, 0, 0, 0, -10, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , -10, 0, 0, 0, 40, 0, 0, 0, -10
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 15, 0, 0, 0, 15, 0, 0

    };
    //ʿ
    public final int[] GuardAttach = {

            0, 0, 0, 5, 0, 5, 0, 0, 0
            , 0, 0, 0, 0, 5, 0, 0, 0, 0
            , 0, 0, 0, 10, 0, 10, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 10, 0, 10, 0, 0, 0
            , 0, 0, 0, 0, 5, 0, 0, 0, 0
            , 0, 0, 0, 5, 0, 5, 0, 0, 0
    };
    //��
    public final int[] kingAttach = {
            0, 0, 0, 10, 20, 10, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 10, 20, 10, 0, 0, 0
    };
    public final int[] redSoldierAttach = Tools.exchange(blackSoldierAttach);
    public final int[] redKnightAttach = Tools.exchange(blackKnightAttach);
    public final int[] redChariotAttach = Tools.exchange(blackChariotAttach);
    public final int[] redGunAttach = Tools.exchange(blackGunAttach);
    public final int[][] chessSiteScoreByRole = new int[][]{{},
            redSoldierAttach, GuardAttach, ElephantAttch, redGunAttach, redKnightAttach, redChariotAttach, kingAttach,
            blackSoldierAttach, GuardAttach, ElephantAttch, blackGunAttach, blackKnightAttach, blackChariotAttach, kingAttach
    };

    /*
     *���ӷ�
     */
    public int chessAttachScore(int chessRole, int chessSite) {
        return chessSiteScoreByRole[chessRole][chessSite];
    }


    private final BitBoard[] AttackPalaceControlPoint = new BitBoard[2];
    private final BitBoard[] AttackCenterControlPoint = new BitBoard[2];
    private final BitBoard[][] AttackDirectionControlPoint = new BitBoard[2][2];

    private void init() {
        int[] attackRedPalacePoint = {
                0, 0, 0, 1, 1, 1, 0, 0, 0
                , 0, 0, 0, 1, 1, 1, 0, 0, 0
                , 0, 0, 0, 1, 1, 1, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0

                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
        };
        //���ӵĿ��Ƶ�÷�(9��)
        int[] blackDefenseInt = {
                0, 1, 1, 1, 1, 1, 1, 1, 0
                , 0, 1, 1, 1, 1, 1, 1, 1, 0
                , 0, 1, 1, 1, 1, 1, 1, 1, 0
                , 0, 1, 1, 1, 1, 1, 1, 1, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0

                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
        };
        //���ӵĿ��Ƶ�÷�(9��)
        int[] redLeftAttackPoint = {
                0, 1, 1, 1, 1, 0, 0, 0, 0
                , 0, 1, 1, 1, 1, 0, 0, 0, 0
                , 0, 1, 1, 1, 1, 0, 0, 0, 0
                , 0, 1, 1, 1, 1, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0

                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
        };
        //���ӵĿ��Ƶ�÷�(9��)
        int[] redRightAttackPoint = {
                0, 0, 0, 0, 1, 1, 1, 1, 0
                , 0, 0, 0, 0, 1, 1, 1, 1, 0
                , 0, 0, 0, 0, 1, 1, 1, 1, 0
                , 0, 0, 0, 0, 1, 1, 1, 1, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0

                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
        };
        int[] attackBlackPalacePoint = Tools.exchange(attackRedPalacePoint);
        int[] redDefenseInt = Tools.exchange(blackDefenseInt);
        int[] blackLeftAttackPoint = Tools.exchange(redLeftAttackPoint);
        int[] blackRightAttackPoint = Tools.exchange(redRightAttackPoint);

        //9����
        AttackPalaceControlPoint[REDPLAYSIGN] = new BitBoard(attackRedPalacePoint);
        AttackPalaceControlPoint[BLACKPLAYSIGN] = new BitBoard(attackBlackPalacePoint);


        //�м�
        AttackCenterControlPoint[REDPLAYSIGN] = new BitBoard(blackDefenseInt);
        AttackCenterControlPoint[BLACKPLAYSIGN] = new BitBoard(redDefenseInt);
        //����λ�ù���
        AttackDirectionControlPoint[REDPLAYSIGN][0] = new BitBoard(redLeftAttackPoint);
        AttackDirectionControlPoint[REDPLAYSIGN][1] = new BitBoard(redRightAttackPoint);
        AttackDirectionControlPoint[BLACKPLAYSIGN][0] = new BitBoard(blackLeftAttackPoint);
        AttackDirectionControlPoint[BLACKPLAYSIGN][1] = new BitBoard(blackRightAttackPoint);
    }
} 
 


