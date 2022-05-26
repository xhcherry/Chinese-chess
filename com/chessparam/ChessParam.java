package com.chessparam;

import static com.ChessConstant.BLACKPLAYSIGN;
import static com.ChessConstant.MaskChesses;
import static com.ChessConstant.NOTHING;
import static com.ChessConstant.REDPLAYSIGN;
import static com.ChessConstant.chessRoles;

import com.BitBoard;
import com.ChessConstant;

// Ϊ��ֹ���߳��£�һЩ����Ҫ�Ĳ�������ͬ������
public class ChessParam {
    public int[] board;     // ����->����

    public int[] allChess; //����->����

    public int[] baseScore = new int[2];

    public int[] boardBitRow; //λ����  ��

    public int[] boardBitCol; //λ����  ��

    private int[] boardRemainChess; //ʣ����������

    //��������λ����
    public BitBoard maskBoardChesses;
    //���Ե�λ����
    public BitBoard[] maskBoardPersonalChesses;
    //���԰���ɫ�����λ����[��ɫ]
    public BitBoard[] maskBoardPersonalRoleChesses;

    //[���][0������������  1������������]
    private final int[][] attackAndDefenseChesses = new int[2][2];

    //ÿ�����Ӷ�ӦattackAndDefenseChesses ���±��
    public static final int[] indexOfAttackAndDefense = new int[]{0,
            0, 1, 1, 0, 0, 0, 1,
            0, 1, 1, 0, 0, 0, 1
    };

    public ChessParam(int[] board, int[] allChess, int[] baseScore, int[] boardBitRow, int[] boardBitCol, int[] boardRemainChess, BitBoard maskBoardChesses, BitBoard[] maskBoardPersonalChesses, BitBoard[] maskBoardPersonalRoleChesses) {
        this.board = board;
        this.allChess = allChess;
        this.baseScore = baseScore;
        this.boardBitRow = boardBitRow;
        this.boardBitCol = boardBitCol;
        this.boardRemainChess = boardRemainChess;
        this.maskBoardChesses = maskBoardChesses;
        this.maskBoardPersonalChesses = maskBoardPersonalChesses;
        this.maskBoardPersonalRoleChesses = maskBoardPersonalRoleChesses;
    }

    public ChessParam(ChessParam param) {
        this.copyToSelf(param);
    }

    public void copyToSelf(ChessParam param) {
        //����copy
        int[] allChessTemp = param.allChess;
        this.allChess = new int[allChessTemp.length];
        System.arraycopy(allChessTemp, 0, this.allChess, 0, allChessTemp.length);
        //����copy
        int[] boardTemp = param.board;
        this.board = new int[boardTemp.length];
        System.arraycopy(boardTemp, 0, this.board, 0, boardTemp.length);
        //λ������
        int[] boardBitRowTemp = param.boardBitRow;
        this.boardBitRow = new int[boardBitRowTemp.length];
        System.arraycopy(boardBitRowTemp, 0, this.boardBitRow, 0, boardBitRowTemp.length);
        //λ������
        int[] boardBitColTemp = param.boardBitCol;
        this.boardBitCol = new int[boardBitColTemp.length];
        System.arraycopy(boardBitColTemp, 0, this.boardBitCol, 0, boardBitColTemp.length);
        //��������
        int[] boardRemainChessTemp = param.boardRemainChess;
        this.boardRemainChess = new int[boardRemainChessTemp.length];
        System.arraycopy(boardRemainChessTemp, 0, this.boardRemainChess, 0, boardRemainChessTemp.length);
        // ���������Ӻͷ�������������
        int[][] attackAndDefenseChessesTemp = param.attackAndDefenseChesses;
        for (int i = 0; i < attackAndDefenseChessesTemp.length; i++) {
            System.arraycopy(attackAndDefenseChessesTemp[i], 0, this.attackAndDefenseChesses[i], 0, attackAndDefenseChessesTemp[i].length);
        }
        //����������λ����
        this.maskBoardChesses = new BitBoard(param.maskBoardChesses);

        this.maskBoardPersonalChesses = new BitBoard[param.maskBoardPersonalChesses.length];
        //����������λ����
        this.maskBoardPersonalChesses[ChessConstant.REDPLAYSIGN] = new BitBoard(param.maskBoardPersonalChesses[ChessConstant.REDPLAYSIGN]);
        this.maskBoardPersonalChesses[ChessConstant.BLACKPLAYSIGN] = new BitBoard(param.maskBoardPersonalChesses[ChessConstant.BLACKPLAYSIGN]);
        //������������ɫ����
        maskBoardPersonalRoleChesses = new BitBoard[param.maskBoardPersonalRoleChesses.length];
        for (int i = 0; i < param.maskBoardPersonalRoleChesses.length; i++) {
            this.maskBoardPersonalRoleChesses[i] = new BitBoard(param.maskBoardPersonalRoleChesses[i]);
        }


        //����
        this.baseScore[ChessConstant.REDPLAYSIGN] = param.baseScore[ChessConstant.REDPLAYSIGN];
        this.baseScore[ChessConstant.BLACKPLAYSIGN] = param.baseScore[ChessConstant.BLACKPLAYSIGN];

    }

    private int getPlayByChessRole(int chessRole) {
        return chessRole > ChessConstant.REDKING ? ChessConstant.BLACKPLAYSIGN : ChessConstant.REDPLAYSIGN;
    }

    public int getChessesNum(int play, int chessRole) {
        return boardRemainChess[getRoleIndexByPlayRole(play, chessRole)];
    }

    /*
     chessRole ���ӽ�ɫ
     ������������
     */
    public void reduceChessesNum(int chessRole) {
        boardRemainChess[chessRole]--;
        attackAndDefenseChesses[getPlayByChessRole(chessRole)][indexOfAttackAndDefense[chessRole]]--;
    }

    /*
     chessRole ���ӽ�ɫ
     ������������
     */
    public void increaseChessesNum(int chessRole) {
        boardRemainChess[chessRole]++;
        int play = getPlayByChessRole(chessRole);
        attackAndDefenseChesses[play][indexOfAttackAndDefense[chessRole]]++;
    }

    //������������
    public int getAllChessesNum() {
        int num = 0;
        for (int i : boardRemainChess) {
            num += i;
        }
        return num;
    }

    //���й�����������
    public int getAttackChessesNum(int play) {
        return attackAndDefenseChesses[play][0];
    }

    //���з�����������
    public int getDefenseChessesNum(int play) {
        return attackAndDefenseChesses[play][1];
    }

    public BitBoard getBitBoardByPlayRole(int play, int role) {
        return maskBoardPersonalRoleChesses[this.getRoleIndexByPlayRole(play, role)];
    }

    public int getRoleIndexByPlayRole(int play, int role) {
        return play == ChessConstant.REDPLAYSIGN ? role : (role + 7);
    }

    public static void main(String[] args) {
    }

    public void initChessBaseScoreAndNum() {
        for (int i = 16; i < allChess.length; i++) {
            if (allChess[i] != NOTHING) {
                int site = allChess[i];
                int chessRole = chessRoles[board[allChess[i]]];
                int play = i < 32 ? BLACKPLAYSIGN : REDPLAYSIGN;
                increaseChessesNum(chessRole);
                maskBoardChesses.assignXor(MaskChesses[site]);
                maskBoardPersonalChesses[play].assignXor(MaskChesses[site]);
                maskBoardPersonalRoleChesses[chessRole].assignXor(MaskChesses[site]);
            }
        }
    }
}











