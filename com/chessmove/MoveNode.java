package com.chessmove;

import static com.ChessBoardMain.chessName;
import static com.ChessConstant.boardCol;
import static com.ChessConstant.boardRow;

import com.ChessConstant;

public class MoveNode implements java.io.Serializable {
    public int destChess;
    public int srcChess;
    public int srcSite;
    public int destSite;
    public int score;
    public boolean isOppProtect = false;

    public MoveNode() {
    }

    public MoveNode(int srcSite, int destSite, int srcChess, int destChess, int score) {
        this.srcSite = srcSite;
        this.destSite = destSite;
        this.destChess = destChess;
        this.srcChess = srcChess;
        this.score = score;
    }

    //�Ƿ��г���
    public boolean isEatChess() {
        return destChess != ChessConstant.NOTHING;
    }

    public String toString() {
        return "\tԭλ��:" + boardRow[srcSite] + "��" + boardCol[srcSite] + "��  ԭ���ӣ�" + chessName[srcChess] + "\tĿ��λ�ã�" + boardRow[destSite] + "��  " + boardCol[destSite] + "��   Ŀ�����ӣ�" + (destChess != ChessConstant.NOTHING ? chessName[destChess] : "�� \t");
    }

    public boolean equals(MoveNode moveNode) {
        return moveNode != null && (moveNode == this || (this.srcSite == moveNode.srcSite && this.destSite == moveNode.destSite));
    }
}
