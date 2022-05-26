package com.searchengine;

import static com.ChessConstant.BLACKPLAYSIGN;
import static com.ChessConstant.CHARIOT;
import static com.ChessConstant.GUN;
import static com.ChessConstant.KNIGHT;
import static com.ChessConstant.NOTHING;
import static com.ChessConstant.REDPLAYSIGN;
import static com.ChessConstant.chessRoles;
import static com.ChessConstant.drawScore;
import static com.ChessConstant.LONGCHECKSCORE;
import static com.ChessConstant.maxScore;

import static com.ChessConstant.chessPlay;

import com.BitBoard;
import com.ChessConstant;
import com.NodeLink;
import com.chessmove.ChessMovePlay;
import com.chessmove.ChessQuiescMove;
import com.chessmove.MoveNode;
import com.chessmove.MoveNodesSort;
import com.chessparam.ChessParam;
import com.evaluate.EvaluateCompute;
import com.history.CHistoryHeuritic;
import com.zobrist.TranspositionTable;

public abstract class SearchEngine implements Runnable {
    public volatile boolean isStop = false;
    public int stopDepth = 0;
    public int count = 0;
    //�û���
    public TranspositionTable transTable;
    public CHistoryHeuritic cHistorySort = new CHistoryHeuritic();
    public ChessParam chessParam;
    public NodeLink moveHistory;
    MoveNode[][] killerMove = new MoveNode[64][2];
    public ChessMovePlay chessMove;
    public ChessQuiescMove chessQuiescMove;
    EvaluateCompute evaluate;
    //      ���������Ȩֵ
    int R = 0;
    public int StretchNeedNum = 0;//��������Ҫ������

    public SearchEngine(ChessParam chessParam, EvaluateCompute evaluate, TranspositionTable transTable, NodeLink moveHistory) {
        this.transTable = transTable;
        this.chessParam = chessParam;
        chessMove = new ChessMovePlay(chessParam, transTable, evaluate);
        chessQuiescMove = new ChessQuiescMove(chessParam, transTable, evaluate);
        this.evaluate = evaluate;
        this.moveHistory = moveHistory;
        initChessSiteScore();
    }

    private void initChessSiteScore() {
        int[] s = getChessBaseScore();
        chessParam.baseScore[0] = s[0];
        chessParam.baseScore[1] = s[1];
    }

    public int[] getChessBaseScore() {
        int[] allChess = chessParam.allChess;
        int[] board = chessParam.board;
        int[]
                s = new int[2];
        for (int i = 16; i < allChess.length; i++) {
            if (allChess[i] != NOTHING) {
                int chessRole = chessRoles[board[allChess[i]]];
                int play = i < 32 ? BLACKPLAYSIGN : REDPLAYSIGN;
                s[play] += evaluate.chessAttachScore(chessRole, allChess[i]);
                s[play] += EvaluateCompute.chessBaseScore[i];
            }
        }
        return s;
    }

    public void setStretchNeedNumByDepth(int depth) {
        if (depth >= 7) {
            StretchNeedNum = 23;
        } else if (depth >= 6) {
            StretchNeedNum = 19;
        } else {
            StretchNeedNum = 15;
        }
    }

    public abstract int searchMove(int alpha, int beta, int depth);

    public int RAdapt(int depth) {
        //���ݲ�ͬ���������Rֵ������,��Ϊ����Ӧ�Կ��Ųü���(Adaptive Null-Move Pruning)��
        //a. ���С�ڻ����6ʱ����R = 2�Ŀ��Ųü���������
        //b. ��ȴ���8ʱ����R = 3��
        if (depth <= 6) {
            return 2;
        } else if (depth <= 8) {
            return 3;
        } else {
            return 4;
        }
    }

    public int fineEvaluate(int play) {
        count++;
        return evaluate.evaluate(play);
    }

    public int roughEvaluate(int play) {
        return chessParam.baseScore[play] - chessParam.baseScore[1 - play];
    }

    //�������巽
    public int swapPlay(int currplay) {
        return 1 - currplay;
    }

    //��̬����
    public int quiescSearch(int alpha, int beta, NodeLink lastLink, boolean isChecked) {
        int play = swapPlay(lastLink.play);
        //�Լ�������
        if (chessParam.allChess[chessPlay[play]] == NOTHING) {
            return -(maxScore - lastLink.depth);
        }
        boolean isMove = false;
        //����ǰ�ϲ� �Ƿ񽫾�
        lastLink.chk = isChecked;
        if (isLongChk(lastLink)) { //�жϳ���
            return LONGCHECKSCORE;
        }
        //����
        if (isDraw()) {
            return drawScore;
        }
        int thisValue, bestValue = -ChessConstant.maxScore - 2;
        //�ﵽ����
        if (lastLink.depth >= 64) {
            return fineEvaluate(play);
        }
        if (!isChecked) {
            isMove = true;
            thisValue = fineEvaluate(play);
            if (thisValue > bestValue) {
                if (thisValue >= beta) {
                    return thisValue;
                }
                bestValue = thisValue;
                if (thisValue > alpha) {
                    alpha = thisValue;
                }
            }
        }
        MoveNodesSort moveNodeSort = new MoveNodesSort(play, chessQuiescMove, isChecked);
        MoveNode moveNode;
        NodeLink nodeLinkTemp, godNodeLink = null;

        while ((moveNode = moveNodeSort.quiescNext()) != null && moveNodeSort.isOver()) {
            chessQuiescMove.moveOperate(moveNode);
            //�����Լ�������
            if (chessQuiescMove.checked(play)) {
                chessQuiescMove.unMoveOperate(moveNode);
                continue;
            }
            nodeLinkTemp = new NodeLink(play, moveNode, transTable.boardZobrist32, transTable.boardZobrist64, true);
            nodeLinkTemp.setLastLink(lastLink);
            thisValue = -quiescSearch(-beta, -alpha, nodeLinkTemp, chessQuiescMove.checked(1 - play));
            chessQuiescMove.unMoveOperate(moveNode);
            isMove = true;
            if (thisValue > bestValue) {
                bestValue = thisValue;
                godNodeLink = nodeLinkTemp;
                if (thisValue > alpha) {
                    alpha = thisValue;
                }
                if (thisValue >= beta) {
                    break;
                }
            }
        }
        if (isMove) {
            lastLink.setNextLink(godNodeLink);
            return bestValue;
        } else {
            return -(maxScore - lastLink.depth);
        }
    }

    public boolean isLongChk(NodeLink lastLink) {
        if (!lastLink.chk) {
            return false;
        }
        NodeLink tempLink = lastLink.getLastLink();
        while (tempLink.getMoveNode() != null) {
            if (tempLink.boardZobrist32 == lastLink.boardZobrist32 && tempLink.boardZobrist64 == lastLink.boardZobrist64) {
                return true;
//				}
            }
            if (tempLink.getMoveNode().isEatChess()) {
                return false;
            }
            tempLink = tempLink.getLastLink();
        }
        return false;
    }

    //���ܣ������ж�
    protected boolean isDraw(
    ) {
        //���κν�������
        return chessParam.getAttackChessesNum(REDPLAYSIGN) == 0 && chessParam.getAttackChessesNum(BLACKPLAYSIGN) == 0;
    }

    protected boolean isDanger(int play) {
        int opptPlay = this.swapPlay(play);
        BitBoard bitBoard = new BitBoard(chessParam.getBitBoardByPlayRole(opptPlay, CHARIOT));
        bitBoard = BitBoard.assignXorToNew(chessParam.getBitBoardByPlayRole(opptPlay, KNIGHT), bitBoard);
        bitBoard = BitBoard.assignXorToNew(chessParam.getBitBoardByPlayRole(opptPlay, GUN), bitBoard);
        bitBoard.assignAnd(DangerMarginBit[play]);
        //�ڽ����г���3�����ӵ���Ϊ����ȫ
        return bitBoard.Count() < 3;
    }

    private static final int[] blackDangerMarginArray = new int[]{
            1, 1, 1, 1, 1, 1, 1, 1, 1
            , 1, 1, 1, 1, 1, 1, 1, 1, 1
            , 1, 1, 1, 1, 1, 1, 1, 1, 1
            , 0, 0, 0, 1, 1, 1, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static final int[] redDangerMarginArray = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 1, 1, 1, 0, 0, 0
            , 1, 1, 1, 1, 1, 1, 1, 1, 1
            , 1, 1, 1, 1, 1, 1, 1, 1, 1
            , 1, 1, 1, 1, 1, 1, 1, 1, 1
    };
    private static final BitBoard[] DangerMarginBit = new BitBoard[]{new BitBoard(blackDangerMarginArray), new BitBoard(redDangerMarginArray)};
}



















