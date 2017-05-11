package com.king.geomodel.view.pojo;

/**
 * @author king
 * @Description:眼镜实体
 * @date 2016/9/5.
 */
public class Glasses {

    private Integer id;
    private String glassesName;
    private String url;

    public Glasses(Integer id, String glassesName, String url) {
        this.id = id;
        this.glassesName = glassesName;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGlassesName() {
        return glassesName;
    }

    public void setGlassesName(String glassesName) {
        this.glassesName = glassesName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Glasses{" +
                "id=" + id +
                ", glassesName='" + glassesName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
