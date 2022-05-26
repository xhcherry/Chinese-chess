package com.chessmove;

import static com.ChessConstant.*;

import com.ChessConstant;
import com.chessparam.ChessParam;
import com.evaluate.EvaluateCompute;
import com.history.CHistoryHeuritic;
import com.zobrist.TranspositionTable;

public class ChessQuiescMove extends ChessMoveAbs {
    public ChessQuiescMove(ChessParam chessParam, TranspositionTable tranTable, EvaluateCompute evaluateCompute) {
        super(chessParam, tranTable, evaluateCompute);
    }

    //��¼�����п��ߵķ�ʽ
    public void savePlayChess(int srcSite, int destSite, int play) {
        int destChess = board[destSite];
        int srcChess = board[srcSite];
        MoveNode moveNode;
        if (destChess != NOTHING) {
            int destScore;
            int srcScore;
            destScore = EvaluateCompute.chessBaseScore[destChess] + evaluateCompute.chessAttachScore(chessRoles[destChess], destSite);
            if (destScore >= 150) {  //����
                //Ҫ�ԵĹ��ӱ����ֱ���
                srcScore = EvaluateCompute.chessBaseScore[srcChess] + evaluateCompute.chessAttachScore(chessRoles[srcChess], srcSite);
                //���������Ӽ�ֵ����
                moveNode = new MoveNode(srcSite, destSite, srcChess, destChess, destScore - srcScore);
                goodMoveList.add(moveNode);
                return;
            }
        }
        //����������
        moveNode = new MoveNode(srcSite, destSite, srcChess, destChess, CHistoryHeuritic.cHistory[ChessConstant.chessRoles_eight[srcChess]][destSite]);
        generalMoveList.add(moveNode); //������
    }
}
















