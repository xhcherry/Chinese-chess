package com;

import static com.ChessConstant.*;

import java.util.Timer;
import java.util.TimerTask;

import com.chessmove.MoveNode;
import com.chessparam.ChessParam;
import com.evaluate.EvaluateCompute;
import com.evaluate.EvaluateComputeEndGame;
import com.evaluate.EvaluateComputeMiddleGame;
import com.history.CHistoryHeuritic;
import com.searchengine.PrincipalVariation;
import com.searchengine.SearchEngine;
import com.zobrist.TranspositionTable;

//�����࣬ѡ����� ��AI����,��������
public class AICoreHandler {
    private SearchEngine seEngine = null;
    private ChessParam chessParam;
    private int depth;
    NodeLink moveHistory;
    private Timer timerMonitoring;
    private long time;

    public void run() {
        run(false);
    }

    public void run(boolean isGuess) {
        TranspositionTable.setDefaultHashSize();
        long beginTime = System.currentTimeMillis();
        moveBegin();
        int mtdfV = seEngine.searchMove(-maxScore, maxScore, depth);
        //����ǲ²��ŷ� �û������ʷ�� �����´��������������
        if (!isGuess) {
            moveEnd();
        }
//		}
        long endTime = System.currentTimeMillis();
        System.out.println(" ��ʱ��" + (endTime - beginTime) + "����\t ����:" + mtdfV + "\tҶ�ӽڵ㣺" + seEngine.count);
        if (timerMonitoring != null) timerMonitoring.cancel();
    }

    public void setLocalVariable(ComputerLevel cLevel, ChessParam chessParam, NodeLink moveHistory) {
        this.depth = cLevel.depth;
        this.time = cLevel.time;
        this.moveHistory = moveHistory;
        this.chessParam = new ChessParam(chessParam);
        seEngine = searchEngineFactory();
    }

    public void guessRun(MoveNode guessMoveNode) {
        seEngine.chessMove.moveOperate(guessMoveNode);
        this.run(true);
        seEngine.chessMove.unMoveOperate(guessMoveNode);
    }

    public SearchEngine searchEngineFactory() {
        SearchEngine se;
        EvaluateCompute evaluateCompute = null;
        int phase = getPhase();
        if (phase == MIDDLE_GAME) {
            evaluateCompute = new EvaluateComputeMiddleGame(chessParam);
        } else if (phase == END_GAME) {
            evaluateCompute = new EvaluateComputeEndGame(chessParam);
            depth++; //�оֶ�����һ��
        }
        se = new PrincipalVariation(chessParam, evaluateCompute, new TranspositionTable(), moveHistory);
        return se;
    }

    private static final int MIDDLE_GAME = 1; //�о�
    private static final int END_GAME = 2;    //�о�

    //����(�о֣��о�)
    private int getPhase() {
        int redChessNum = 0, blackChessNum = 0;
        redChessNum += chessParam.getChessesNum(REDPLAYSIGN, ChessConstant.CHARIOT);
        redChessNum += chessParam.getChessesNum(REDPLAYSIGN, ChessConstant.KNIGHT);
        redChessNum += chessParam.getChessesNum(REDPLAYSIGN, ChessConstant.GUN);
        redChessNum += chessParam.getChessesNum(REDPLAYSIGN, ChessConstant.SOLDIER) > 3 ? 1 : 0;

        blackChessNum += chessParam.getChessesNum(BLACKPLAYSIGN, ChessConstant.CHARIOT);
        blackChessNum += chessParam.getChessesNum(BLACKPLAYSIGN, ChessConstant.KNIGHT);
        blackChessNum += chessParam.getChessesNum(BLACKPLAYSIGN, ChessConstant.GUN);
        blackChessNum += chessParam.getChessesNum(BLACKPLAYSIGN, ChessConstant.SOLDIER) > 3 ? 1 : 0;

        if ((redChessNum + blackChessNum) < 7) {
            return END_GAME;
        } else {
            return MIDDLE_GAME;
        }

    }

    public void moveBegin() {
        //�����Ź��������ļ������ļ�ֵ����
        EvaluateCompute.chessBaseScore[27] = EvaluateCompute.chessBaseScore[28] = EvaluateCompute.chessBaseScore[29] = EvaluateCompute.chessBaseScore[30] = EvaluateCompute.chessBaseScore[31] = (EvaluateCompute.SOLDIERSCORE + (11 - chessParam.getAttackChessesNum(BLACKPLAYSIGN)) * 8);
        EvaluateCompute.chessBaseScore[43] = EvaluateCompute.chessBaseScore[44] = EvaluateCompute.chessBaseScore[45] = EvaluateCompute.chessBaseScore[46] = EvaluateCompute.chessBaseScore[47] = (EvaluateCompute.SOLDIERSCORE + (11 - chessParam.getAttackChessesNum(REDPLAYSIGN)) * 8);

        //����������ʱ��ļ�ֵ����
        EvaluateCompute.chessBaseScore[36] = EvaluateCompute.chessBaseScore[35] = EvaluateCompute.chessBaseScore[20] = EvaluateCompute.chessBaseScore[19] = (EvaluateCompute.KNIGHTSCORE + (32 - chessParam.getAllChessesNum()) * 6);
        //�ڵļ�ֵ�½�
        EvaluateCompute.chessBaseScore[21] = EvaluateCompute.chessBaseScore[22] = EvaluateCompute.chessBaseScore[37] = EvaluateCompute.chessBaseScore[38] = (EvaluateCompute.GUNSCORE - (32 - chessParam.getAllChessesNum()) * 6);

    }

    public void moveEnd() {
        for (int i = 0; i < CHistoryHeuritic.cHistory.length; i++) {
            for (int j = 0; j < CHistoryHeuritic.cHistory[i].length; j++) {
                CHistoryHeuritic.cHistory[i][j] /= 512;
            }
        }
        //�����û������
        TranspositionTable.cleanTranZobrist();
    }

    public void setStop() {
        if (seEngine != null) {
            seEngine.isStop = true;
        }
        //ֹͣʱ����
        if (timerMonitoring != null) {
            timerMonitoring.cancel();
        }
    }

    public void launchTimer() {
        TimerTask myTask = new TimerTask() {
            public void run() {
                setStop();
            }
        };
        timerMonitoring = new Timer();
        timerMonitoring.schedule(myTask, time);
    }

    public static void main(String[] args) {
        TimerTask myTask = new TimerTask() {
            public void run() {

            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 0);
        timer.cancel();
    }
}












