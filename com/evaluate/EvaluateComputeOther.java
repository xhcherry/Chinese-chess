package com.evaluate;

import static com.ChessConstant.*;

import com.BitBoard;
import com.Tools;
import com.chessparam.ChessParam;

public class EvaluateComputeOther extends EvaluateCompute {

    public EvaluateComputeOther(ChessParam chessParam) {
        this.chessParam = chessParam;
        init();
    }

    int[] score = new int[2];

    public int evaluate(int play) {
        score[REDPLAYSIGN] = chessParam.baseScore[REDPLAYSIGN];
        score[BLACKPLAYSIGN] = chessParam.baseScore[BLACKPLAYSIGN];
        return score[play] - score[1 - play];
    }

    //马
    public final int[] blackKnightAttach = {
            -60, -35, -20, -40, -40, -40, -20, -35, -60,
            -30, +20, +20, +30, -75, +30, +20, +20, -30,
            -30, +20, +35, +25, +20, +25, +35, +20, -30,
            -30, +30, +40, +45, +60, +45, +40, +30, -30,
            -35, +60, +60, +60, +70, +60, +60, +60, -35,

            -30, +50, +65, +70, +80, +70, +65, +50, -30,
            -40, +60, +70, +75, +80, +75, +70, +60, -40,
            -40, +60, +75, +90, +95, +90, +75, +60, -40,
            -40, +60, +90, +80, +40, +80, +90, +60, -40,
            -50, +10, +40, +40, +10, +40, +40, +10, -50
    };
    //炮
    public final int[] blackGunAttach = {
            -50, -20, -20, 0, 0, 0, -20, -20, -50,
            -10, +30, +40, +60, +40, +60, +40, +30, -10,
            -10, +40, +40, +60, +75, +60, +40, +40, -10,
            -10, +40, +30, +45, +65, +45, +30, +40, -10,
            +20, +40, +40, +45, +60, +45, +40, +40, +20,

            -10, +30, +30, +40, +60, +40, +30, +30, -10,
            -10, +30, +30, +40, +50, +40, +30, +30, -10,
            -10, +60, +60, +30, 0, +30, +60, +60, -10,
            -10, +60, +60, +30, 0, +30, +60, +60, -10,
            +30, +70, +60, +30, -10, +30, +60, +70, +30
    };
    //车
    public final int[] blackChariotAttach = {
            -60, -10, -10, -10, -10, -10, -10, -10, -60,
            -10, +10, +10, +30, -40, +30, +10, +10, -10,
            -20, +15, +15, +30, +10, +30, +15, +15, -20,
            -20, +30, +30, +30, +50, +30, +30, +30, -20,
            -20, +70, +70, +85, +80, +85, +70, +70, -20,

            -20, +70, +70, +85, +80, +85, +70, +70, -20,
            -20, +40, +40, +50, +70, +50, +40, +40, -20,
            +20, +60, +60, +60, +70, +60, +60, +60, +20,
            +20, +60, +60, +60, +100, +60, +60, +60, +20,
            +20, +60, +60, +60, +60, +60, +60, +60, +10
    };
    //卒
    public final int[] blackSoldierAttach = {

            0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, +10, 0, 0, 0, 0
            , +20, 0, +45, 0, +35, 0, +45, 0, +20
            , +80, +100, +100, +100, +100, +100, +120, +100, +80
            , +100, +120, +150, +170, +170, +170, +150, +120, +100
            , +100, +150, +200, +240, +250, +240, +200, +150, +100
            , +100, +150, +200, +250, +300, +250, +200, +150, +100
            , +100, +100, +100, +100, +100, +100, +100, +100, +100
    };
    //象
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
    //士
    public final int[] GuardAttach = {

            0, 0, 0, 10, 0, 10, 0, 0, 0
            , 0, 0, 0, 0, 5, 0, 0, 0, 0
            , 0, 0, 0, 10, 0, 10, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 10, 0, 10, 0, 0, 0
            , 0, 0, 0, 0, 5, 0, 0, 0, 0
            , 0, 0, 0, 10, 0, 10, 0, 0, 0
    };
    //王
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
     *附加分
     */
    public int chessAttachScore(int chessRole, int chessSite) {
        return chessSiteScoreByRole[chessRole][chessSite];
    }


    private final BitBoard[] AttackPalaceBitboard = new BitBoard[2];

    //	private static final int attackControlPointScore=20;//,defenseControlPointScore=2;
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
        int[] attackBlackPalacePoint = Tools.exchange(attackRedPalacePoint);
        //9宫格
        AttackPalaceBitboard[REDPLAYSIGN] = new BitBoard(attackRedPalacePoint);
        AttackPalaceBitboard[BLACKPLAYSIGN] = new BitBoard(attackBlackPalacePoint);

    }
} 
 


