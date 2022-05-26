package com.evaluate;

import static com.ChessConstant.*;

import com.BitBoard;
import com.ChessConstant;
import com.Tools;
import com.chessparam.ChessParam;

public class EvaluateComputeEndGame extends EvaluateCompute {
    public EvaluateComputeEndGame(ChessParam chessParam) {
        this.chessParam = chessParam;
    }

    int[] score = new int[2];
    //��֮���໥����
    int[] soldiersProtected = new int[]{0, 55, 150, 300, 400, 500};
    //�ڶ�Ӧ����ʿ����������
    int[] gunOpptNotGuard = new int[]{0, 40, 110};
    //���Ӧ����ʿ����������
    int[] knightOpptNotGuard = new int[]{110, 40, 0};

    public int evaluate(int play) {
        score[REDPLAYSIGN] = chessParam.baseScore[REDPLAYSIGN];
        score[BLACKPLAYSIGN] = chessParam.baseScore[BLACKPLAYSIGN];
        for (int curplay = 0; curplay < 2; curplay++) {
            int soldierNum = chessParam.getChessesNum(curplay, ChessConstant.SOLDIER);
            int gunNum = chessParam.getChessesNum(curplay, ChessConstant.GUN);
            int knightNum = chessParam.getChessesNum(curplay, ChessConstant.KNIGHT);
            //��֮���໥�����ӷ�
            if (soldierNum >= 2) {
                //�����ܹ�������λ��
                BitBoard soldierAttack = this.getSoldiersAttackBitBoard(curplay);
                //�����ڵ�λ��
                BitBoard soldierSite = chessParam.getBitBoardByPlayRole(curplay, ChessConstant.SOLDIER);
                soldierAttack.assignAnd(soldierSite);
                score[curplay] += soldiersProtected[soldierAttack.Count()];
            }
            //����ʿ������
            int opponentGuardNum = chessParam.getChessesNum(1 - curplay, ChessConstant.GUARD);
            //��
            if (gunNum > 0) {
                score[curplay] += gunOpptNotGuard[opponentGuardNum] * (gunNum == 2 ? 1.7 : 1);
            }
            //��
            if (knightNum > 0) {
                score[curplay] += knightOpptNotGuard[opponentGuardNum] * (knightNum == 2 ? 1.7 : 1);
            }
        }
        return score[play] - (score[1 - play]);
    }

    private static final int[][] soldiersRole = new int[][]{{27, 28, 29, 30, 31}, {43, 44, 45, 46, 47}};

    //�����ܹ�������λ��
    private BitBoard getSoldiersAttackBitBoard(int play) {
        BitBoard soldierAttack = new BitBoard();
        for (int i = 0; i < soldiersRole[play].length; i++) {
            int chess = soldiersRole[play][i];
            if (chessParam.allChess[chess] != NOTHING) {
                BitBoard temp = chessAllMove(chessRoles[chess], chessParam.allChess[chess], play);
                soldierAttack.assignOr(temp);
            }
        }
        return soldierAttack;
    }

    //��
    public final int[] blackKnightAttach = {
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, +30, +30, -20, +30, +30, 0, 0,
            0, 0, +30, +40, +45, +40, +30, 0, 0,
            0, +35, +45, +55, +66, +55, +45, +35, 0,
            0, +55, +60, +70, +70, +70, +60, +55, 0,

            0, +55, +60, +70, +70, +70, +60, +55, 0,
            0, +45, +55, +55, +66, +55, +55, +45, 0,
            0, +35, +45, +50, +63, +50, +45, +35, 0,
            0, +35, +35, +50, +60, +50, +35, +35, 0,
            0, +10, +20, +20, -20, +20, +20, +10, 0
    };
    //��
    public final int[] blackGunAttach = {
            0, 0, +30, +80, +20, +80, +30, 0, 0,
            0, 0, +30, +70, +55, +70, +30, 0, 0,
            0, 0, +30, +65, +65, +65, +30, 0, 0,
            0, 0, +20, +20, +35, +20, +20, 0, 0,
            0, 0, +20, +25, +25, +25, +20, 0, 0,

            0, +10, +20, +20, +21, +20, +20, +10, 0,
            0, +10, +20, +10, +20, +10, +20, +10, 0,
            0, +10, +10, 0, 0, 0, +10, +10, 0,
            0, +10, +10, 0, 0, 0, +10, +10, 0,
            0, +10, +10, +10, -10, +10, +10, +10, 0
    };
    //��
    public final int[] blackChariotAttach = {
            +20, +20, +20, +65, 0, +65, +20, +20, +20,
            +20, +30, +30, +50, +35, +50, +30, +30, +20,
            +20, +35, +35, +50, +50, +50, +35, +35, +20,
            +20, +30, +30, +50, +55, +50, +30, +30, +20,
            +20, +65, +65, +85, +75, +85, +65, +65, +20,

            +20, +65, +65, +85, +75, +85, +65, +65, +20,
            +20, +40, +40, +50, +50, +50, +40, +40, +20,
            +20, +40, +40, +50, +50, +50, +40, +40, +20,
            +20, +40, +40, +60, +90, +60, +40, +40, +20,
            +20, +20, +20, +20, +20, +20, +20, +20, +20
    };
    //��
    public final int[] blackSoldierAttach = {

            0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , +55, 0, +60, 0, +60, 0, +60, 0, +55
            , +90, +110, +110, +120, +120, +120, +110, +110, +90
            , +110, +150, +150, +180, +200, +180, +150, +150, +110
            , +110, +180, +220, +230, +250, +230, +220, +160, +110
            , +110, +180, +220, +260, +300, +260, +220, +160, +110
            , +100, +100, +100, +100, +100, +100, +100, +100, +100
    };
    //��
    public final int[] ElephantAttch = {
            0, 0, 10, 0, 0, 0, 10, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 30, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 30, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 10, 0, 0, 0, 10, 0, 0
    };
    //ʿ
    public final int[] GuardAttach = {
            0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 25, 0, 0, 0, 0
            , 0, 0, 0, 20, 0, 20, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 20, 0, 20, 0, 0, 0
            , 0, 0, 0, 0, 25, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    //��
    public final int[] kingAttach = {
            0, 0, 0, 10, 10, 10, 0, 0, 0
            , 0, 0, 0, -15, -20, -15, 0, 0, 0
            , 0, 0, 0, -40, -50, -40, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, -40, -50, -40, 0, 0, 0
            , 0, 0, 0, -15, -20, -15, 0, 0, 0
            , 0, 0, 0, 10, 10, 10, 0, 0, 0
    };
    public final int[] redSoldierAttach = Tools.exchange(blackSoldierAttach);
    public final int[] redKnightAttach = Tools.exchange(blackKnightAttach);
    public final int[] redChariotAttach = Tools.exchange(blackChariotAttach);
    public final int[] redGunAttach = Tools.exchange(blackGunAttach);
    public final int[][] chessSiteScoreByRole = new int[][]{{},
            redSoldierAttach, GuardAttach, ElephantAttch, redGunAttach, redKnightAttach, redChariotAttach, kingAttach,
            blackSoldierAttach, GuardAttach, ElephantAttch, blackGunAttach, blackKnightAttach, blackChariotAttach, kingAttach
    };

    //���ӷ�
    public int chessAttachScore(int chessRole, int chessSite) {
        return chessSiteScoreByRole[chessRole][chessSite];
    }
}
 


