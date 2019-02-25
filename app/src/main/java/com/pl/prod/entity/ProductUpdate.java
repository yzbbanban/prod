package com.pl.prod.entity;

/**
 * 产品更新
 */
public class ProductUpdate {

    /**
     * id
     */
    private Integer id;
    /**
     * 唯一标示
     */
    private String ssid;
    /**
     * 详情
     */
    private String detail;
    /**
     * 备注
     */
    private String remark;
    /**
     * 产品类型
     */
    private Integer typeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "ProductUpdate{" +
                "id=" + id +
                ", ssid='" + ssid + '\'' +
                ", detail='" + detail + '\'' +
                ", remark='" + remark + '\'' +
                ", typeId=" + typeId +
                '}';
    }
}
