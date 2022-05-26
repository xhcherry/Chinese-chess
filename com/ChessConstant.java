package com;

public class ChessConstant {
    public static final int KING = 7;    //��
    public static final int CHARIOT = 6; //��
    public static final int KNIGHT = 5; //��
    public static final int GUN = 4; //��
    public static final int ELEPHANT = 3; //��
    public static final int GUARD = 2; //ʿ
    public static final int SOLDIER = 1; //��
    //��
    public static final int REDKING = KING;    //��
    public static final int REDCHARIOT = CHARIOT; //��
    public static final int REDKNIGHT = KNIGHT; //��
    public static final int REDGUN = GUN; //��
    public static final int REDELEPHANT = ELEPHANT; //��
    public static final int REDGUARD = GUARD; //ʿ
    public static final int REDSOLDIER = SOLDIER; //��

    //��
    public static final int BLACKKING = KING + 7;    //��
    public static final int BLACKCHARIOT = CHARIOT + 7; //��
    public static final int BLACKKNIGHT = KNIGHT + 7; //��
    public static final int BLACKGUN = GUN + 7; //��
    public static final int BLACKELEPHANT = ELEPHANT + 7; //��
    public static final int BLACKGUARD = GUARD + 7; //ʿ
    public static final int BLACKSOLDIER = SOLDIER + 7; //��
    //ÿ�����Ӷ�Ӧ�Ľ�ɫ(��allChess�±��Ӧ)
    public static final int[] chessRoles = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            BLACKKING, BLACKCHARIOT, BLACKCHARIOT, BLACKKNIGHT, BLACKKNIGHT, BLACKGUN, BLACKGUN, BLACKELEPHANT, BLACKELEPHANT, BLACKGUARD, BLACKGUARD, BLACKSOLDIER, BLACKSOLDIER, BLACKSOLDIER, BLACKSOLDIER, BLACKSOLDIER,
            REDKING, REDCHARIOT, REDCHARIOT, REDKNIGHT, REDKNIGHT, REDGUN, REDGUN, REDELEPHANT, REDELEPHANT, REDGUARD, REDGUARD, REDSOLDIER, REDSOLDIER, REDSOLDIER, REDSOLDIER, REDSOLDIER
    };
    public static final int[] chessRoles_eight = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            KING, CHARIOT, CHARIOT, KNIGHT, KNIGHT, GUN, GUN, ELEPHANT, ELEPHANT, GUARD, GUARD, SOLDIER, SOLDIER, SOLDIER, SOLDIER, SOLDIER,
            KING, CHARIOT, CHARIOT, KNIGHT, KNIGHT, GUN, GUN, ELEPHANT, ELEPHANT, GUARD, GUARD, SOLDIER, SOLDIER, SOLDIER, SOLDIER, SOLDIER
    };
    public static final int maxScore = 9999;
    //�췽��־
    public static final int REDPLAYSIGN = 1;
    //�ڷ���־
    public static final int BLACKPLAYSIGN = 0;
    //���̴�С
    public static final int BOARDSIZE90 = 90;
    //�����ж�����һ������
    public static int[] chessPlay = new int[]{16, 32};
    public static final int LONGCHECKSCORE = 8888; //�����ظ��ŷ�
    public static final int drawScore = 0; //�����ŷ�
    public static final int[] boardRow = {
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 2, 2, 2, 2, 2, 2, 2,
            3, 3, 3, 3, 3, 3, 3, 3, 3,
            4, 4, 4, 4, 4, 4, 4, 4, 4,
            5, 5, 5, 5, 5, 5, 5, 5, 5,
            6, 6, 6, 6, 6, 6, 6, 6, 6,
            7, 7, 7, 7, 7, 7, 7, 7, 7,
            8, 8, 8, 8, 8, 8, 8, 8, 8,
            9, 9, 9, 9, 9, 9, 9, 9, 9,
    };
    public static final int[] boardCol = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            0, 1, 2, 3, 4, 5, 6, 7, 8,
    };
    //û������
    public final static int NOTHING = -1;
    public final static long[][] ChessZobristList64 = new long[BOARDSIZE90][15];
    public final static int[][] ChessZobristList32 = new int[BOARDSIZE90][15];
    public static BitBoard[] KnightBitBoards = new BitBoard[BOARDSIZE90];    //�����Ǳ������ܹ�������λ��
    public static BitBoard[] KnightLegBitBoards = new BitBoard[BOARDSIZE90]; //����ȵ�λ��
    public static BitBoard[] KingCheckedSoldierBitBoards = new BitBoard[BOARDSIZE90]; //��������λ��
    public static BitBoard[][] KnightBitBoardOfAttackLimit = new BitBoard[BOARDSIZE90][200];//[����λ��][�������ȵ�BitBoard.checkSum()]
    public static BitBoard[] ElephanLegBitBoards = new BitBoard[BOARDSIZE90]; //�����۵�λ��
    public static BitBoard[][] ElephanBitBoardOfAttackLimit = new BitBoard[BOARDSIZE90][200];//[����λ��][�������ȵ�BitBoard.checkSum()]
    public static BitBoard[][] ChariotBitBoardOfAttackRow = new BitBoard[BOARDSIZE90][512]; //����(һ��������9��λ��)���ܹ�����λ����
    public static BitBoard[][] ChariotBitBoardOfAttackCol = new BitBoard[BOARDSIZE90][1024];  //����(һ��������10��λ��)���ܹ�����λ����
    public static BitBoard[][] MoveChariotOrGunBitBoardRow = new BitBoard[BOARDSIZE90][512];  //��and����(ָ��һ�У���9��������Ϊ512)�����ƶ���λ����
    public static BitBoard[][] MoveChariotOrGunBitBoardCol = new BitBoard[BOARDSIZE90][1024];  //��and���������ƶ���λ����
    public static BitBoard[][] GunBitBoardOfAttackRow = new BitBoard[BOARDSIZE90][512]; //�������ܹ�����λ����
    public static BitBoard[][] GunBitBoardOfAttackCol = new BitBoard[BOARDSIZE90][1024];  //�������ܹ�����λ����
    public static BitBoard[] KingBitBoard = new BitBoard[BOARDSIZE90]; //����λ����
    public static BitBoard[] GuardBitBoard = new BitBoard[BOARDSIZE90]; //ʿ��λ����
    public static BitBoard[][] SoldiersBitBoard = new BitBoard[2][BOARDSIZE90]; //����λ����[���][λ��]
    public static BitBoard[][] GunBitBoardOfFakeAttackRow = new BitBoard[BOARDSIZE90][512]; //�����ܹ�������λ��(ָѹ����)
    public static BitBoard[][] GunBitBoardOfFakeAttackCol = new BitBoard[BOARDSIZE90][1024];  //�������ܹ�������λ��(ָѹ����)
    public static BitBoard[][] GunBitBoardOfMoreRestAttackRow = new BitBoard[BOARDSIZE90][512]; //�ڸ��������ܹ�������λ��
    public static BitBoard[][] GunBitBoardOfMoreRestAttackCol = new BitBoard[BOARDSIZE90][1024];  //�ڸ����������ܹ�������λ��
    public static BitBoard[] MaskChesses = new BitBoard[BOARDSIZE90]; //��λ����ʾһ��������ĳ��λ��
    //������
    public static int[][] ChariotAndGunMobilityRow = new int[BOARDSIZE90][512];
    public static int[][] ChariotAndGunMobilityCol = new int[BOARDSIZE90][1024];
    public static int[][] KnightMobility = new int[BOARDSIZE90][200];
    public static final int[] boardMap = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0,
            0, 0, 0, 9, 10, 11, 12, 13, 14, 15, 16, 17, 0, 0, 0, 0,
            0, 0, 0, 18, 19, 20, 21, 22, 23, 24, 25, 26, 0, 0, 0, 0,
            0, 0, 0, 27, 28, 29, 30, 31, 32, 33, 34, 35, 0, 0, 0, 0,
            0, 0, 0, 36, 37, 38, 39, 40, 41, 42, 43, 44, 0, 0, 0, 0,
            0, 0, 0, 45, 46, 47, 48, 49, 50, 51, 52, 53, 0, 0, 0, 0,
            0, 0, 0, 54, 55, 56, 57, 58, 59, 60, 61, 62, 0, 0, 0, 0,
            0, 0, 0, 63, 64, 65, 66, 67, 68, 69, 70, 71, 0, 0, 0, 0,
            0, 0, 0, 72, 73, 74, 75, 76, 77, 78, 79, 80, 0, 0, 0, 0,
            0, 0, 0, 81, 82, 83, 84, 85, 86, 87, 88, 89, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
}









