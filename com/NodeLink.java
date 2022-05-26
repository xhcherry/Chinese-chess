package com;

import java.io.Serializable;

import com.chessmove.MoveNode;

//�����ŷ�����
public class NodeLink implements Serializable {
    private NodeLink lastLink;
    private NodeLink nextLink;
    public int depth = 0;
    private MoveNode moveNode;
    public boolean isNullMove;
    public long boardZobrist64;
    public int boardZobrist32;
    public boolean chk = false; //�Ƿ񽫾�
    //���巽
    public int play;
    public boolean isQuiesc;

    public NodeLink(int play, int boardZobrist32, long boardZobrist64) {

        //Ĭ�ϴ���״̬
        this(play, false, boardZobrist32, boardZobrist64);
    }

    public NodeLink(int play, boolean isNullMove, int boardZobrist32, long boardZobrist64) {
        this.play = play;
        this.isNullMove = isNullMove;
        this.boardZobrist32 = boardZobrist32;
        this.boardZobrist64 = boardZobrist64;
    }

    public NodeLink(int play, MoveNode moveNode, int boardZobrist32, long boardZobrist64) {
        this.play = play;
        this.moveNode = moveNode;
        this.boardZobrist32 = boardZobrist32;
        this.boardZobrist64 = boardZobrist64;
        isQuiesc = false;
    }

    public NodeLink(int play, MoveNode moveNode, int boardZobrist32, long boardZobrist64, boolean isQuiesc) {
        this.play = play;
        this.moveNode = moveNode;
        this.boardZobrist32 = boardZobrist32;
        this.boardZobrist64 = boardZobrist64;
        this.isQuiesc = isQuiesc;
    }

    public void setNextLink(NodeLink nextLink) {
        this.nextLink = nextLink;
        if (nextLink != null) {
            nextLink.lastLink = this;
        }
    }

    public NodeLink getNextLink() {
        return nextLink;
    }

    public MoveNode getMoveNode() {
        return moveNode;
    }

    public void setMoveNode(MoveNode moveNode) {
        this.moveNode = moveNode;
    }

    public NodeLink getLastLink() {
        return lastLink;
    }

    public void setLastLink(NodeLink previousLink) {
        this.lastLink = previousLink;
        previousLink.nextLink = this;
        this.depth = previousLink.depth + 1;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        NodeLink nextLink = this;
        sb.append("==========================================================\n");
        while (nextLink != null) {
            MoveNode movenode = nextLink.getMoveNode();
            sb.append(" ��->").append(nextLink.depth).append("�� ").append(movenode).append(" ").append(nextLink.isQuiesc ? "��̬����" : "��������").append("\t").append(nextLink.chk ? "����" : "�޽���").append("\n");
            nextLink = nextLink.getNextLink();
        }
        sb.append("==========================================================\n");
        return sb.toString();
    }
}
	
	
	
	
	
	
	
	
	
	
	