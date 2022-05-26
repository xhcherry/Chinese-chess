package com.chessmove;

import static com.ChessConstant.*;

import com.BitBoard;
import com.ChessConstant;
import com.chessparam.ChessParam;
import com.evaluate.EvaluateCompute;
import com.history.CHistoryHeuritic;
import com.zobrist.TranspositionTable;

public class ChessMovePlay extends ChessMoveAbs {
    public ChessMovePlay(ChessParam chessParam, TranspositionTable tranTable, EvaluateCompute evaluateCompute) {
        super(chessParam, tranTable, evaluateCompute);
    }

    //记录下所有可走的方式
    public void savePlayChess(int srcSite, int destSite, int play) {
        int destChess = board[destSite];
        //攻击的棋子为己方棋子
        for (int i = 0; i < repeatMoveList.size; i++) {
            MoveNode repeatMoveNode = repeatMoveList.get(i);
            if (repeatMoveNode != null && repeatMoveNode.srcSite == srcSite && repeatMoveNode.destSite == destSite) {
                repeatMoveList.set(i, null);
                return;
            }
        }
        int srcChess = board[srcSite];
        boolean isOppProtect = BitBoard.assignAndToNew(oppAttackSite, MaskChesses[destSite]).isEmpty();
        if (destChess != NOTHING) {
            int srcScore;
            //要吃的柜子被对手保护
            if (isOppProtect) {
                srcScore = EvaluateCompute.chessBaseScore[srcChess] + evaluateCompute.chessAttachScore(chessRoles[srcChess], srcSite);
            } else {
                srcScore = -500;
            }
            int destScore = EvaluateCompute.chessBaseScore[destChess] + evaluateCompute.chessAttachScore(chessRoles[destChess], destSite);
            if (destScore >= srcScore) {  //吃子
                //按被吃棋子价值排序
                MoveNode moveNode = new MoveNode(srcSite, destSite, srcChess, destChess, destScore - srcScore);
                moveNode.isOppProtect = isOppProtect;
                goodMoveList.add(moveNode);
                return;
            }
        }
        MoveNode moveNode = new MoveNode(srcSite, destSite, srcChess, destChess, CHistoryHeuritic.cHistory[ChessConstant.chessRoles_eight[srcChess]][destSite] + (isOppProtect ? 0 : 256));
        moveNode.isOppProtect = isOppProtect;
        generalMoveList.add(moveNode); //不吃子
    }
}








