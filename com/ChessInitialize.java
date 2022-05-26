package com;

import static com.ChessConstant.*;
import static com.Tools.*;

import com.chessparam.ChessParam;
import com.zobrist.TranspositionTable;

import java.util.Arrays;

//�������̳�ʼ������
//���ӵ��ŷ�Ԥ����
public class ChessInitialize {
    //���ŷ�Ԥ��������
    int[][] knightMove = new int[BOARDSIZE90][8];
    //���ŷ�����λ��Ԥ��������
    int[][] horseLeg = new int[BOARDSIZE90][8];
    //���ŷ�Ԥ��������
    int[][] elephantMove = new int[BOARDSIZE90][4];
    //���ŷ�����λ��Ԥ��������
    int[][] elephantLeg = new int[BOARDSIZE90][4];
    //���ŷ�Ԥ��������
    int[][][] soldierMove = new int[2][BOARDSIZE90][3];
    //�������ŷ�Ԥ��������
    int[][][] chariotMoveRowEat = new int[9][512][2];//��(����)
    int[][][] chariotMoveColEat = new int[10][1024][2];   //��(����)
    //���ڲ������ŷ�Ԥ��������
    int[][][] move_chariotGunRowNop = new int[9][512][9];//��(����)���������ж�����
    int[][][] move_chariotGunColNop = new int[10][1024][10];   //��(����)
    //�ڳ����ŷ�Ԥ��������
    int[][][] gunMoveRowEat = new int[9][512][2];//��(����)
    int[][][] gunMoveColEat = new int[10][1024][2];   //��(����)
    //��α����λ��
    int[][][] gunFackAttackRow = new int[9][512][9];//��(����)
    int[][][] gunFackAttackCol = new int[10][1024][10];   //��(����)
    //�ڸ��������ܹ�������λ��
    int[][][] gunMoreRestAttackRow = new int[9][512][9];//��(����)
    int[][][] gunMoreRestAttackCol = new int[10][1024][10];   //��(����)

    public static ChessParam getGlobalChessParam(int[] boardTemp) {
        int[] board = new int[BOARDSIZE90];
        int[] chesses = new int[48];
        Arrays.fill(board, -1);
        Arrays.fill(chesses, -1);
        BitBoard[] chessBitBoardRole = new BitBoard[15];
        for (int i = 0; i < chessBitBoardRole.length; i++) {
            chessBitBoardRole[i] = new BitBoard();
        }
        ChessParam chessParamCont = new ChessParam(board, chesses, new int[2],
                new int[10], new int[9], new int[15], new BitBoard(),
                new BitBoard[]{new BitBoard(), new BitBoard()},
                chessBitBoardRole);
        for (int i = 0; i < boardTemp.length; i++) {
            if (boardTemp[i] > 0) {
                int chess = boardTemp[i];
                chessParamCont.board[i] = chess;
                chessParamCont.allChess[chess] = i;
                int destRow = boardRow[i];
                int destCol = boardCol[i];
                chessParamCont.boardBitRow[destRow] |= (1 << (8 - destCol));
                chessParamCont.boardBitCol[destCol] |= (1 << (9 - destRow));
            }
        }
        chessParamCont.initChessBaseScoreAndNum();
        TranspositionTable.genStaticZobrist32And64OfBoard(chessParamCont.board);

        return chessParamCont;
    }

    static {
        // λ���̵ĳ�ʼ
        for (int i = 0; i < MaskChesses.length; i++) {
            MaskChesses[i] = new BitBoard(i);
        }
        new ChessInitialize();
    }

    private ChessInitialize() {
        //�����Ǳ��ȵ�λ����
        initBitBoard(KnightBitBoards);
        //����ȵ�λ��
        initBitBoard(KnightLegBitBoards);
        //���󹥻�����
        initBitBoard(KnightBitBoardOfAttackLimit);
        initBitBoard(ElephanBitBoardOfAttackLimit);
        cleanEmpty(knightMove);
        cleanEmpty(horseLeg);
        cleanEmpty(elephantMove);
        cleanEmpty(elephantLeg);
        cleanEmpty(soldierMove);
        cleanEmpty(gunFackAttackRow);
        cleanEmpty(gunFackAttackCol);
        cleanEmpty(gunMoreRestAttackRow);
        cleanEmpty(gunMoreRestAttackCol);
        //��ʼ����ŷ�
        initKnightMove();
        //��ʼ����ŷ�
        initElephantMove();
        //��ʼ����ŷ�
        initSoldier();
        //��ʼ�����ӳ��ڶ�Ӧ�������ŷ�
        this.initChariotGunVariedMove(cleanEmpty(move_chariotGunRowNop), 0, 0, false);
        //��ʼ�����ӳ��ڶ�Ӧ�������ŷ�
        this.initChariotGunVariedMove(cleanEmpty(move_chariotGunColNop), 1, 0, false);
        //��ʼ�ڳ��Ӷ�Ӧ�������ŷ�
        this.initChariotGunVariedMove(cleanEmpty(gunMoveRowEat), 0, 1, true);
        //��ʼ�ڳ��Ӷ�Ӧ�������ŷ�
        this.initChariotGunVariedMove(cleanEmpty(gunMoveColEat), 1, 1, true);
        //������ (��)
        this.initChariotGunVariedMove(cleanEmpty(chariotMoveRowEat), 0, 0, true);
        //������ (��)
        this.initChariotGunVariedMove(cleanEmpty(chariotMoveColEat), 1, 0, true);
        /*��α����λ��*/
        initGunFackEatMove(gunFackAttackRow, 0);
        initGunFackEatMove(gunFackAttackCol, 1);
        //�ڸ������ܹ�������λ��
        this.initChariotGunVariedMove(gunMoreRestAttackRow, 0, 2, true);
        this.initChariotGunVariedMove(gunMoreRestAttackCol, 1, 2, true);
        preAllBitBoard();
    }

    private void preAllBitBoard() {
        // �� ���Ǳ������ܹ�������λ��
        preBitBoardAttack(knightMove, horseLeg, KnightBitBoardOfAttackLimit, 0);
        // �� ���Ǳ������ܹ�������λ��
        preBitBoardAttack(elephantMove, elephantLeg,
                ElephanBitBoardOfAttackLimit, 1);
        // ���ڲ����ӵ���� ��
        preGunAndChariotBitBoardAttack(move_chariotGunRowNop,
                MoveChariotOrGunBitBoardRow, 0);
        // ���ڲ����ӵ���� ��
        preGunAndChariotBitBoardAttack(move_chariotGunColNop,
                MoveChariotOrGunBitBoardCol, 1);
        // ��������� ��
        preGunAndChariotBitBoardAttack(chariotMoveRowEat,
                ChariotBitBoardOfAttackRow, 0);
        // ��������� ��
        preGunAndChariotBitBoardAttack(chariotMoveColEat,
                ChariotBitBoardOfAttackCol, 1);
        // �ڳ������ ��
        preGunAndChariotBitBoardAttack(gunMoveRowEat, GunBitBoardOfAttackRow, 0);
        // �ڳ������ ��
        preGunAndChariotBitBoardAttack(gunMoveColEat, GunBitBoardOfAttackCol, 1);
        // ���ɽ���λ����
        preBitBoardKingMove(KingBitBoard);
        // ����ʿ��λ����
        preBitBoardGuardMove(GuardBitBoard);
        // �佫��������
        preKingCheckedSoldierBitBoards(KingCheckedSoldierBitBoards);
        //��α����λ��
        preGunAndChariotBitBoardAttack(gunFackAttackRow, GunBitBoardOfFakeAttackRow, 0);
        preGunAndChariotBitBoardAttack(gunFackAttackCol, GunBitBoardOfFakeAttackCol, 1);
        //���ڵĻ�����
        preGunAndChariotMobility(MoveChariotOrGunBitBoardRow, ChariotAndGunMobilityRow);
        preGunAndChariotMobility(MoveChariotOrGunBitBoardCol, ChariotAndGunMobilityCol);
        // �ڸ����ӳ������ ��
        preGunAndChariotBitBoardAttack(gunMoreRestAttackRow, GunBitBoardOfMoreRestAttackRow, 0);
        // �ڸ����ӳ������ ��
        preGunAndChariotBitBoardAttack(gunMoreRestAttackCol, GunBitBoardOfMoreRestAttackCol, 1);
    }

    private void preKingCheckedSoldierBitBoards(BitBoard[] bitBoard) {
        for (int i = 0; i < bitBoard.length; i++) {
            bitBoard[i] = new BitBoard(KnightLegBitBoards[i]);
            if (bitBoard[i].Count() == 4) {
                if (i < 45) {
                    bitBoard[i].assignXor(new BitBoard(i - 9));
                } else {
                    bitBoard[i].assignXor(new BitBoard(i + 9));
                }
            }
        }
    }

    //��ʼ����ŷ�Ԥ��������
    private void initKnightMove() {
        int[] cnKnightMoveTab = new int[]{-0x21, -0x1f, -0x12, -0x0e, +0x0e, +0x12, +0x1f, +0x21};
        int[] cnHorseLegTab = new int[]{-0x10, -0x10, -0x01, +0x01, -0x01, +0x01, +0x10, +0x10};
        for (int site = 0; site < 255; site++) {
            if (isBoardTo255(site)) {
                int z = 0;
                for (int j = 0; j < cnKnightMoveTab.length; j++) {
                    int _tKnight = site + cnKnightMoveTab[j];
                    int _tHorseLeg = site + cnHorseLegTab[j];
                    if (isBoardTo255(_tKnight) && isBoardTo255(_tHorseLeg)) {
                        int siteTo90 = boardMap[site];
                        int _tKnightTo90 = boardMap[_tKnight];
                        int _tHorseLegTo90 = boardMap[_tHorseLeg];
                        knightMove[siteTo90][z] = _tKnightTo90;
                        horseLeg[siteTo90][z] = _tHorseLegTo90;
                        z++;
                        if (KnightBitBoards[siteTo90] == null) {
                            KnightBitBoards[siteTo90] = new BitBoard();
                        }
                        if (KnightLegBitBoards[siteTo90] == null) {
                            KnightLegBitBoards[siteTo90] = new BitBoard();
                        }
                        //���λ����
                        KnightBitBoards[siteTo90].assignOr(new BitBoard(_tKnightTo90));
                        KnightLegBitBoards[siteTo90].assignOr(new BitBoard(_tHorseLegTo90));
                    }
                }
            }
        }
    }

    //��ʼ����ŷ�Ԥ��������
    public void initElephantMove() {
        int[] cnElephantMoveTab = new int[]{-0x22, -0x1e, +0x1e, +0x22};
        int[] cnElephantLegTab = new int[]{-0x11, -0xf, +0xf, +0x11};
        for (int site = 0; site < 255; site++) {
            if (isBoardTo255(site)) {
                int z = 0;
                int[] _tElephantMoveTab = cnElephantMoveTab;
                int[] _tElephantLegTab = cnElephantLegTab;
                //���󵽴�����߽��������ŷ�������غ�
                if (site / 16 == 7 || site / 16 == 6) {
                    _tElephantMoveTab = new int[]{-0x22, -0x1e};
                    _tElephantLegTab = new int[]{-0x11, -0xf};
                    //���󵽴�����߽��������ŷ�������غ�
                } else if (site / 16 == 8 || site / 16 == 9) {
                    _tElephantMoveTab = new int[]{+0x1e, +0x22};
                    _tElephantLegTab = new int[]{+0xf, +0x11};
                }
                for (int j = 0; j < _tElephantMoveTab.length; j++) {

                    int _tElephant = site + _tElephantMoveTab[j];
                    int _tElephantLeg = site + _tElephantLegTab[j];

                    if (isBoardTo255(_tElephant) && isBoardTo255(_tElephantLeg)) {

                        int siteTo90 = boardMap[site];
                        int _tElephant_90 = boardMap[_tElephant];
                        int _tElephantLeg_90 = boardMap[_tElephantLeg];

                        elephantMove[siteTo90][z] = _tElephant_90;
                        elephantLeg[siteTo90][z] = _tElephantLeg_90;

                        if (ElephanLegBitBoards[siteTo90] == null) {
                            ElephanLegBitBoards[siteTo90] = new BitBoard();
                        }
                        //�����۵�λ����
                        ElephanLegBitBoards[siteTo90].assignXor(MaskChesses[_tElephantLeg_90]);
                        z++;
                    }
                }
            }
        }
    }

    //��ʼ�����ŷ�Ԥ��������
    private void initSoldier() {
        int[] _tSoldierMoveTab = null;
        for (int i = 0; i < soldierMove.length; i++)
            for (int site = 0; site < 255; site++) {
                if (isBoardTo255(site)) {
                    int z = 0;
                    if (i == BLACKPLAYSIGN) {
                        //�ڷ����Ծ�����
                        if (site / 16 > 7) {
                            _tSoldierMoveTab = new int[]{+0x10, -0x01, +0x01};
                        } else {
                            _tSoldierMoveTab = new int[]{+0x10};
                        }
                    } else if (i == REDPLAYSIGN) {
                        //�췽���Ծ�����
                        if (site / 16 < 8) {
                            _tSoldierMoveTab = new int[]{-0x10, -0x01, +0x01};
                        } else {
                            _tSoldierMoveTab = new int[]{-0x10};
                        }
                    }
                    assert _tSoldierMoveTab != null;
                    for (int k : _tSoldierMoveTab) {
                        int _tSoldier = site + k;
                        if (isBoardTo255(_tSoldier)) {
                            int siteTo90 = boardMap[site];
                            int _tSoldier90 = boardMap[_tSoldier];
                            soldierMove[i][siteTo90][z] = _tSoldier90;
                            if (SoldiersBitBoard[i][siteTo90] == null) {
                                SoldiersBitBoard[i][siteTo90] = new BitBoard();
                            }
                            //�����ܹ�������λ����
                            SoldiersBitBoard[i][siteTo90].assignOr(MaskChesses[_tSoldier90]);
                            z++;
                        }
                    }
                }
            }
    }


    /*
     * ���ܣ����ڳ����벻����
     * moveEat ����
     * direction ����
     * handicapNum �м���������
     * isEat �Ƿ���� true false
     */
    private void initChariotGunVariedMove(int[][][] moveEat, int direction, int handicapNum, boolean isEat) {
        int num = moveEat.length - 1;
        int sort = moveEat[0].length;
        for (int i = 0; i <= num; i++) {
            int site = 1 << i;//����λ��
            for (int j = 0; j <= sort; j++) {
                if (((j & site) > 0)) {
                    if (isEat && j == site) { //�����Լ��������ȥ
                        continue;
                    }
                    int isHandicap = 0;
                    int eatIndex = 0;
                    //����(��)ȡֵ
                    for (int n = i + 1; n <= num; n++) {
                        int _tSite = 1 << n;
                        if (isEat) {
                            if ((j & _tSite) > 0) {
                                if (isHandicap < handicapNum) {
                                    isHandicap++;
                                } else {
                                    if (direction == 0) {
                                        moveEat[num - i][j][eatIndex++] = (num - n);
                                    } else {
                                        moveEat[num - i][j][eatIndex++] = (num - n) * 9;
                                    }
                                    break;
                                }
                            }
                        } else {
                            if ((j & _tSite) == 0) {
                                if (direction == 0) {
                                    moveEat[num - i][j][eatIndex++] = (num - n);
                                } else {
                                    moveEat[num - i][j][eatIndex++] = (num - n) * 9;
                                }
                            } else if ((j & _tSite) > 0) {
                                break;
                            }
                        }
                    }
                    isHandicap = 0;
                    //����(��)ȡֵ
                    for (int n = i - 1; n >= 0; n--) {
                        int _tSite = 1 << n;
                        if (isEat) {
                            if ((j & _tSite) > 0) {
                                if (isHandicap < handicapNum) {
                                    isHandicap++;
                                } else {
                                    if (direction == 0) {
                                        moveEat[num - i][j][eatIndex++] = (num - n);
                                    } else {
                                        moveEat[num - i][j][eatIndex++] = (num - n) * 9;
                                    }
                                    break;
                                }

                            }
                        } else {
                            if ((j & _tSite) == 0) {
                                if (direction == 0) {
                                    moveEat[num - i][j][eatIndex++] = (num - n);
                                } else {
                                    moveEat[num - i][j][eatIndex++] = (num - n) * 9;
                                }
                            } else if ((j & _tSite) > 0) {
                                break;
                            }
                        }
                    }
                }
            }

        }
    }

    /*
     * ���ܣ� ������ and �� ���������ܹ�������λ��
     * attackBoard  Ԥ���ɹ���λ��
     * leg  ����ʱ�ı���λ��
     * attackBoardBit ���շ��ص�����
     * type 0 Ϊ��  1Ϊ��
     */
    private void preBitBoardAttack(int[][] attackBoard, int[][] leg, BitBoard[][] attackBoardBit, int type) {
        for (int i = 0; i < ChessConstant.BOARDSIZE90; i++) {
            //���б���ȥ�ظ��������
            int[] legSite = removeRepeatArray(leg[i]);
            //�õ��˱��ȵ����������� KnightBitBoardOfAttackLimit
            int[][] legSiteComb = getAllLegCombByLeg(legSite);
            for (int[] ints : legSiteComb) {
                if (ints[0] == ChessConstant.NOTHING) {
                    break;
                }
                BitBoard siteAttBit = new BitBoard();
                BitBoard siteLegBit = new BitBoard();
                int[] legTemp = leg[i];
                for (int n = 0; n < legTemp.length && legTemp[n] != ChessConstant.NOTHING; n++) {
                    boolean isExists = false;
                    for (int j = 0; j < ints.length && ints[j] != ChessConstant.NOTHING; j++) {
                        //���������legTemp���ڵ�
                        if (legTemp[n] == ints[j]) {
                            isExists = true;
                            break;
                        }
                    }
                    //���ñ���
                    if (!isExists) {
                        if (attackBoard[i][n] != ChessConstant.NOTHING) {
                            siteLegBit.assignOr(MaskChesses[legTemp[n]]);
                        }
                    }
                    //���ò��������ߵ���λ��
                    if (isExists) {//������в����� д��λ����
                        if (attackBoard[i][n] != ChessConstant.NOTHING) {
                            siteAttBit.assignXor(MaskChesses[attackBoard[i][n]]);
                        }
                    }
                }
                if (type == 0) { //��
                    attackBoardBit[i][siteLegBit.checkSumOfKnight()] = siteAttBit;
                    //��Ļ�����
                    KnightMobility[i][siteLegBit.checkSumOfKnight()] = siteAttBit.Count();
                } else {  //��
                    attackBoardBit[i][siteLegBit.checkSumOfElephant()] = siteAttBit;
                }
            }
        }
    }

    //������ȥ���ظ�����
    private int[] removeRepeatArray(int[] array) {
        int[] duplicate = new int[array.length];
        Arrays.fill(duplicate, -1);
        for (int j : array) {
            if (j == ChessConstant.NOTHING) {
                break;
            }
            for (int z = 0; z < duplicate.length; z++) {
                if (duplicate[z] == j) {
                    break;
                } else if (duplicate[z] == ChessConstant.NOTHING) {
                    duplicate[z] = j;
                    break;
                }
            }
        }
        return duplicate;
    }

    //���б��ȵ����
    private int[][] getAllLegCombByLeg(int[] legs) {
        int[][] r = new int[20][4];
        for (int[] ints : r) {
            Arrays.fill(ints, NOTHING);
        }
        for (int i = 0, b = 0; i < legs.length && legs[i] != ChessConstant.NOTHING; i++) {
            String[] result = computCombination(0, legs, i + 1);
            for (int j = 0; j < result.length && result[j] != null; j++) {
                String[] siteStr = result[j].split(",");
                for (int m = 0; m < siteStr.length; m++) {
                    r[b][m] = Integer.parseInt(siteStr[m]);
                }
                if (siteStr.length > 0) {
                    b++;
                }
            }
        }
        return r;
    }

    /*
     * index : ������ʼλ��
     * a  : ����
     * num : �����������ȡ��λ
     * һֱ��ȡ��ֻΪ0Ϊֹ
     */
    private String[] computCombination(int index, int[] a, int num) {
        String[] value = new String[10];
        int b = 0;
        for (int i = index; i < a.length && a[i] != ChessConstant.NOTHING; i++) {
            if (num == 1) {
                value[b++] = a[i] + "";
            } else {
                String[] r = computCombination(i + 1, a, num - 1);
                for (int j = 0; j < r.length && r[j] != null; j++) {
                    value[b++] = a[i] + "," + r[j];
                }
            }
        }
        return value;
    }

    /*
     * ���ܣ� ���ڻ�������Ԥ����
     */
    private void preGunAndChariotMobility(BitBoard[][] moveSite, int[][] mobility) {
        for (int i = 0; i < moveSite.length; i++) {
            for (int j = 0; j < moveSite[i].length; j++) {
                if (moveSite[i][j] != null) {
                    mobility[i][j] = moveSite[i][j].Count();
                }

            }
        }
    }

    /*
     * moveSite ��Ҫ��֮ǰԤ�����ŷ��б������п����ŷ���������λ����
     * bitBoard ���ɺ�ֵ��λ����
     * type 0��  1 ��
     */
    private void preGunAndChariotBitBoardAttack(int[][][] moveSite, BitBoard[][] bitBoard, int type) {

        for (int i = 0; i < BOARDSIZE90; i++) {
            int row = boardRow[i];
            int col = boardCol[i];
            //moveSite[rowOrCol][��or�еĶ�����״̬][���ƶ�����λ��]
            int rowOrCol;
            int[][] moveSiteTemp;
            if (type == 0) { //��
                rowOrCol = row;
                //��Ϊһ������Ҫ֪�������ڵ�λ�����Է��뵱ǰ�������ڵ�һ���е���һ��
                moveSiteTemp = moveSite[col];
            } else {  //��
                rowOrCol = col;
                moveSiteTemp = moveSite[row];
            }
            for (int j = 0; j < moveSiteTemp.length; j++) {
                bitBoard[i][j] = new BitBoard();
                for (int k = 0; k < moveSiteTemp[j].length && moveSiteTemp[j][k] != NOTHING; k++) {
                    int site;
                    if (type == 0) { //��
                        site = moveSiteTemp[j][k] + rowOrCol * 9;
                    } else {  //��
                        site = moveSiteTemp[j][k] + rowOrCol;
                    }
                    bitBoard[i][j].assignXor(MaskChesses[site]);
                }
            }
        }

    }

    //���ܣ� ����λ��������
    public void preBitBoardKingMove(BitBoard[] bitBoard) {
        for (int i = 0; i < BOARDSIZE90; i++) {
            int[] _tMove = null;
            switch (i) {
                case 3 -> _tMove = new int[]{4, 12};
                case 4 -> _tMove = new int[]{3, 13, 5};
                case 5 -> _tMove = new int[]{4, 14};
                case 12 -> _tMove = new int[]{3, 13, 21};
                case 13 -> _tMove = new int[]{12, 4, 14, 22};
                case 14 -> _tMove = new int[]{5, 13, 23};
                case 21 -> _tMove = new int[]{12, 22};
                case 22 -> _tMove = new int[]{21, 13, 23};
                case 23 -> _tMove = new int[]{22, 14};
                case 84 -> _tMove = new int[]{75, 85};
                case 85 -> _tMove = new int[]{84, 76, 86};
                case 86 -> _tMove = new int[]{85, 77};
                case 75 -> _tMove = new int[]{66, 84, 76};
                case 76 -> _tMove = new int[]{85, 75, 77, 67};
                case 77 -> _tMove = new int[]{76, 68, 86};
                case 66 -> _tMove = new int[]{75, 67};
                case 67 -> _tMove = new int[]{66, 76, 68};
                case 68 -> _tMove = new int[]{67, 77};
            }
            if (_tMove != null) {
                bitBoard[i] = new BitBoard();
                for (int k : _tMove) {
                    bitBoard[i].assignXor(MaskChesses[k]);
                }
            }
        }
    }

    //���ܣ�ʿλ����
    public void preBitBoardGuardMove(BitBoard[] bitBoard) {
        for (int i = 0; i < bitBoard.length; i++) {
            int[] _tMove = null;
            switch (i) {
                case 3, 5, 21, 23 -> _tMove = new int[]{13};
                case 13 -> _tMove = new int[]{3, 5, 21, 23};
                case 84, 86, 66, 68 -> _tMove = new int[]{76};
                case 76 -> _tMove = new int[]{66, 68, 84, 86};
            }
            if (_tMove != null) {
                bitBoard[i] = new BitBoard();
                for (int k : _tMove) {
                    bitBoard[i].assignXor(MaskChesses[k]);
                }
            }
        }
    }

    //��ʼ���ܹ�������λ��
    private void initGunFackEatMove(int[][][] moveEat, int direction) {
        int num = moveEat.length - 1;
        int sort = moveEat[0].length;
        for (int i = 0; i <= num; i++) {
            int site = 1 << i;//����λ��
            for (int j = 0; j <= sort; j++) {
                if (((j & site) > 0) && j != site) {
                    boolean isEat = false;
                    boolean isHandicap = false;
                    int eatIndex = 0;
                    //����(��)ȡֵ
                    for (int n = i + 1; n <= num; n++) {
                        int _tSite = 1 << n;
                        if (isEat) {
                            break;
                        }
                        if ((j & _tSite) > 0) {
                            if (!isHandicap) {
                                isHandicap = true;
                            } else {
                                isEat = true;

                            }
                        } else if (isHandicap) {
                            if (direction == 0) {
                                moveEat[num - i][j][eatIndex++] = (num - n);
                            } else {
                                moveEat[num - i][j][eatIndex++] = (num - n) * 9;
                            }
                        }
                    }
                    isHandicap = false;
                    isEat = false;
                    //����(��)ȡֵ
                    for (int n = i - 1; n >= 0; n--) {
                        int _tSite = 1 << n;
                        if (isEat) {
                            break;
                        }
                        if ((j & _tSite) > 0) {
                            if (!isHandicap) {
                                isHandicap = true;
                            } else {
                                isEat = true;
                            }

                        } else if (isHandicap) {
                            if (direction == 0) {
                                moveEat[num - i][j][eatIndex++] = (num - n);
                            } else {
                                moveEat[num - i][j][eatIndex++] = (num - n) * 9;
                            }
                        }
                    }
                }
            }
        }
    }

    private int[][][] cleanEmpty(int[][][] array) {
        for (int[][] ints : array) {
            for (int[] anInt : ints) {
                Arrays.fill(anInt, -1);
            }
        }
        return array;
    }

    public void cleanEmpty(int[][] array) {
        for (int[] ints : array) {
            Arrays.fill(ints, -1);
        }
    }

    public void initBitBoard(BitBoard[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = new BitBoard();
            }
        }
    }

    public void initBitBoard(BitBoard[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = new BitBoard();
        }
    }

    public static void main(String[] args) {
    }
} 









