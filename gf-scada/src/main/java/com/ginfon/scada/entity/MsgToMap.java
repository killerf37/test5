package com.ginfon.scada.entity;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/11/08/16:09
 * @Description:
 */
public class MsgToMap {

    private Integer ID;

    private Integer LineNo;

    private String LineName;

    private Integer MsgNo;

    private String MapNo;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getLineNo() {
        return LineNo;
    }

    public void setLineNo(Integer lineNo) {
        LineNo = lineNo;
    }

    public String getLineName() {
        return LineName;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public Integer getMsgNo() {
        return MsgNo;
    }

    public void setMsgNo(Integer msgNo) {
        MsgNo = msgNo;
    }

    public String getMapNo() {
        return MapNo;
    }

    public void setMapNo(String mapNo) {
        MapNo = mapNo;
    }

    @Override
    public String toString() {
        return "MsgToMap{" +
                "ID=" + ID +
                ", LineNo=" + LineNo +
                ", LineName='" + LineName + '\'' +
                ", MsgNo=" + MsgNo +
                ", MapNo='" + MapNo + '\'' +
                '}';
    }
}
