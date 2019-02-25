package com.pl.prod.entity;

/**
 * 版本记录
 * Created by brander on 2019/1/16
 */
public class VersionRecord {
    /**
     * id
     */
    private Integer id;
    /**
     * 版本
     */
    private String version;
    /**
     * 备注
     */
    private String remark;
    /**
     * 下载路径
     */
    private String url;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 终端类型：android 1,ios 2
     */
    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VersionRecord{" +
                "id=" + id +
                ", version='" + version + '\'' +
                ", remark='" + remark + '\'' +
                ", url='" + url + '\'' +
                ", createTime=" + createTime +
                ", type=" + type +
                '}';
    }
}
