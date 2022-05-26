package com.searchengine;

import static com.ChessConstant.*;

import com.NodeLink;
import com.chessmove.MoveNode;
import com.chessmove.MoveNodesSort;
import com.chessparam.ChessParam;
import com.evaluate.EvaluateCompute;
import com.movelist.MoveNodeList;
import com.zobrist.TranspositionTable;

public class PrincipalVariation extends SearchEngine {
    //FutilityMoveCounts
    public static int[][] FutilityScore = new int[32][80];
    public static int[] FutilityMoveCounts = new int[32];

    static {
        for (int d = 0; d < 32; d++) {
            for (int k = 0; k < 80; k++) {
                int v = (int) (d * (1.29) * 155) - (k * d * 10);
                FutilityScore[d][k] = v;
            }
        }
        for (int d = 0; d < 32; d++) {
            FutilityMoveCounts[d] = (int) (7.001 + 0.4 * Math.pow(d, 2.0));
        }
    }

    public PrincipalVariation(ChessParam chessParam, EvaluateCompute evaluate,
                              TranspositionTable transTable, NodeLink moveHistory) {
        super(chessParam, evaluate, transTable, moveHistory);
        // TODO Auto-generated constructor stub
    }

    public int[] values = new int[8];
    public int checkNum = 0;
    public int[] test1Num = new int[10];
    public int[] test1Numpv = new int[10];

    public int searchMove(int alpha, int beta, int depth) {
        MoveNodesSort.trancount1 = 0;
        MoveNodesSort.trancount2 = 0;
        MoveNodesSort.killcount1 = 0;
        MoveNodesSort.killcount2 = 0;
        MoveNodesSort.eatmovecount = 0;
        MoveNodesSort.othercount = 0;
        checkNum = 0;
        test1Num = new int[10];
        test1Numpv = new int[10];
        moveHistory.depth = 0;
        this.setStretchNeedNumByDepth(depth);
        int s = 0;
        MoveNodesSort moveNodeSort = new MoveNodesSort(swapPlay(moveHistory.play), new MoveNodeList(2), killerMove[depth], chessMove, false);
        MoveNodeList moveNodeList = new MoveNodeList(100);
        MoveNode moveNode;
        int initScore = 100;
        int currPlay = swapPlay(moveHistory.play);
        while ((moveNode = moveNodeSort.next()) != null && moveNodeSort.isOver()) {
            chessMove.moveOperate(moveNode);
            if (!chessMove.checked(currPlay)) {
                moveNode.score = initScore--;
                moveNodeList.add(moveNode);
            }
            chessMove.unMoveOperate(moveNode);
        }
        //��������
        for (int d = 4; d <= depth && !isStop; d++) {

            s = this.rootNegaScout(alpha, beta, d, moveNodeList, moveHistory);
            NodeLink nextLink = moveHistory.getNextLink();
            int k = d + 1;
            while (nextLink != null && k >= 0) {
                killerMove[k][1] = killerMove[k][0];
                killerMove[k][0] = nextLink.getMoveNode();
                k--;
                nextLink = nextLink.getNextLink();
            }

        }

        System.out.println(moveHistory.getNextLink());


        return s;
    }

    public int rootNegaScout(int alpha, int beta, int depth, MoveNodeList moveNodeList, NodeLink lastLink) {

        boolean isMove = false;
        int play = swapPlay(lastLink.play);
        //�Ƿ񱻽���
        //����ǰ�ϲ� �Ƿ񽫾�
        lastLink.chk = chessMove.checked(play);
        int thisValue, bestValue = -maxScore - 2;
        NodeLink nodeLinkTemp, bestNodeLink = null;
        int i = 0, thisAlpha = alpha;
        //�����������ŷ�
        MoveNode moveNode;
        while (i < moveNodeList.size) {
            moveNode = this.getSortAfterBestMove(moveNodeList, i++);
            chessMove.moveOperate(moveNode);
            nodeLinkTemp = new NodeLink(play, moveNode, transTable.boardZobrist32, transTable.boardZobrist64);
            nodeLinkTemp.setLastLink(lastLink);
            if (isMove) {
                // ��һ����ȫ�����Ժ��ü�С����(��һ��Ϊ׼ȷֵ�Ժ�Ϊ��С����ֵ)
                thisValue = -negaScout(-thisAlpha - 1, -thisAlpha, depth - 1, nodeLinkTemp, false);
                if (thisValue > thisAlpha) {
                    // ���²���
                    thisValue = -negaScout(-beta, -thisAlpha, depth - 1, nodeLinkTemp, true);
                }
            } else {
                thisValue = -negaScout(-beta, -thisAlpha, depth - 1, nodeLinkTemp, true);
                isMove = true;
            }
            chessMove.unMoveOperate(moveNode);
            moveNode.score = thisValue;
            if (thisValue > bestValue) {
                bestValue = thisValue;
                bestNodeLink = nodeLinkTemp;

                if (thisValue > thisAlpha) {
                    thisAlpha = thisValue;
                }
            }
            if (isStop) {
                break;
            }
        }

        if (isMove) {
            if (bestNodeLink != null) lastLink.setNextLink(bestNodeLink);
            return bestValue;
        } else {
            return -(maxScore - lastLink.depth);
        }
        // �������ֵ�߽�
    }

    public int negaScout(int alpha, int beta, int depth, NodeLink lastLink, boolean isPVNode) {

        int play = swapPlay(lastLink.play);
        // �Լ�˧����
        if (chessParam.allChess[chessPlay[play]] == NOTHING) {
            return -(maxScore - lastLink.depth);
        }
        int bestValue = lastLink.depth - maxScore;
        if (bestValue > beta) return bestValue;

        MoveNodeList tranGodMoveNode = new MoveNodeList(2);
        int[] value = new int[1];
        int score = transTable.getTranZobrist(alpha, beta, depth, play, tranGodMoveNode, value);
        if (score != TranspositionTable.FAIL) { // �û���̽��
            return score;
        }
        //�Ƿ񱻽���
        boolean isChecked = chessMove.checked(play);
        //���ý���״̬
        lastLink.chk = isChecked;
        //�жϳ���
        if (isLongChk(lastLink)) {
            return LONGCHECKSCORE;
        }
        if (!lastLink.isNullMove && lastLink.getMoveNode().isEatChess()) {
            //�����ж�
            if (isDraw()) {
                return drawScore;
            }
        }
        //����������������
        if (isChecked) {
            depth++;
            checkNum++;
        }

        int entryType = TranspositionTable.hashAlpha;

        if (depth <= stopDepth) {
            //��̬����
            return this.quiescSearch(alpha, beta, lastLink, isChecked);
        }
        //������ǰ(note: �������������Ŀղü�) ���Ҳ���Ϊ����״̬ ����������Ҫ>=3
        if (!lastLink.isNullMove && !isChecked && !isPVNode) {
            if (depth >= 2) {
                R = RAdapt(depth);
                int attackChessNum = chessParam.getAttackChessesNum(play);
                if (attackChessNum > 0) {
                    int val;
                    NodeLink nodeLinkNULL = new NodeLink(play, true, transTable.boardZobrist32, transTable.boardZobrist64);
                    nodeLinkNULL.setLastLink(lastLink);
                    val = -negaScout(-beta, -beta + 1, depth - R - 1, nodeLinkNULL, false);
                    if (val >= beta) {
                        if (attackChessNum > 2 && depth < 6) {
                            return val;
                        }
                        //������֤�Ŀ���ǰ����
                        val = -negaScout(-beta, -beta + 1, depth - R + 1, nodeLinkNULL, false);
                        if (val >= beta) {
                            return val;
                        }
                    }
                }
            }
        }
        //�ڲ���������
        if (depth >= 6 && isPVNode && tranGodMoveNode.get(0) == null) {
            negaScout(alpha, beta, depth - 2, lastLink, true);
            if (lastLink.getNextLink() != null) {
                tranGodMoveNode.set(0, lastLink.getNextLink().getMoveNode());
                transTable.setRootTranZobrist(play, lastLink.getNextLink().getMoveNode());
            }
        }
        boolean isMove = false;
        int thisValue, thisAlpha = alpha;
        NodeLink nodeLinkTemp, bestNodeLink = null;
        int movesSearched = 0, newDepth, curType = -1;
        //�����������ŷ�
        MoveNodesSort moveNodeSort = new MoveNodesSort(play, tranGodMoveNode, killerMove[depth], chessMove, isChecked);
        MoveNode moveNode;
        int movesSearchedCount = isPVNode ? 10 : 5;
        while ((moveNode = moveNodeSort.next()) != null && moveNodeSort.isOver()) {

            chessMove.moveOperate(moveNode);
            //�����Լ�������
            if (chessMove.checked(play)) {
                //��ʷ����������ŷ�Ҫ���֣�
                chessMove.unMoveOperate(moveNode);
                continue;
            }
            newDepth = depth;
            if (!isChecked && !isPVNode && newDepth < 6 && isDanger(play) && movesSearched >= movesSearchedCount && FutilityScore[newDepth][movesSearched] + this.roughEvaluate(play) < thisAlpha) {
                chessMove.unMoveOperate(moveNode);
                movesSearched++;
                continue;
            }

            nodeLinkTemp = new NodeLink(play, moveNode, transTable.boardZobrist32, transTable.boardZobrist64);
            nodeLinkTemp.setLastLink(lastLink);

            if (isMove) {
                int kk = 2;
                if (!isChecked && newDepth >= 3 && movesSearched >= movesSearchedCount) {

                    if (isDanger(1 - play)) {
                        if (movesSearched >= (movesSearchedCount + (5 + newDepth)) * 2) kk = 4;
                        else if (movesSearched >= (movesSearchedCount + 5 + newDepth)) kk = 3;
                    }
                    thisValue = -negaScout(-thisAlpha - 1, -thisAlpha, newDepth - kk, nodeLinkTemp, false);
                } else {
                    thisValue = thisAlpha + 1;
                }
                if (thisValue > thisAlpha) {
                    // �ü�С����
                    thisValue = -negaScout(-thisAlpha - 1, -thisAlpha, newDepth - 1, nodeLinkTemp, false);
                    if (thisValue > thisAlpha) {// ���²���
                        thisValue = -negaScout(-beta, -thisAlpha, newDepth - 1, nodeLinkTemp, true);
                    }
                }
            } else {
                //��һ����ȫ��������
                thisValue = -negaScout(-beta, -thisAlpha, newDepth - 1, nodeLinkTemp, true);
                isMove = true;
            }

            chessMove.unMoveOperate(moveNode);
            movesSearched++;
            if (thisValue > bestValue) {
                bestValue = thisValue;
                bestNodeLink = nodeLinkTemp;
                // �����±߽�
                if (thisValue >= beta) {
                    //��Ϊ���ŷ�
                    if (!lastLink.isNullMove && moveNodeSort.currType != MoveNodesSort.kill1 && moveNodeSort.currType != MoveNodesSort.kill2) {
                        killerMove[depth][1] = killerMove[depth][0];
                        killerMove[depth][0] = moveNode;
                    }
                    curType = moveNodeSort.currType;
                    entryType = TranspositionTable.hashBeta;
                    break;
                }
                if (thisValue > thisAlpha) {
                    curType = moveNodeSort.currType;
                    thisAlpha = thisValue;
                    entryType = TranspositionTable.hashPV;
                }

            }
        }

        if (isMove) {
            lastLink.setNextLink(bestNodeLink);
            MoveNode bestMoveNode = null;
            if (bestNodeLink != null) {
                if (entryType != TranspositionTable.hashAlpha) {
                    bestMoveNode = bestNodeLink.getMoveNode();
                    cHistorySort.setCHistoryGOOD(bestMoveNode, depth);

                }
            }
            if (curType > -1) {
                values[curType]++;
            }
            transTable.setTranZobrist(entryType, bestValue, depth, play, bestMoveNode);
            return bestValue;
        } else {
            return -(maxScore - lastLink.depth);
        }
        // �������ֵ�߽�

    }

    public MoveNode getSortAfterBestMove(MoveNodeList AllmoveNode, int index) {
        int replaceIndex = index;
        for (int i = index + 1; i < AllmoveNode.size; i++) {
            if (AllmoveNode.get(i).score > AllmoveNode.get(replaceIndex).score) {
                replaceIndex = i;
            }
        }
        if (replaceIndex != index) {
            MoveNode t = AllmoveNode.get(index);
            AllmoveNode.set(index, AllmoveNode.get(replaceIndex));
            AllmoveNode.set(replaceIndex, t);
        }
        return AllmoveNode.get(index);
    }

    public void run() {
    }
}
