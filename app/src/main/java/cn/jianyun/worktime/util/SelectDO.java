package cn.jianyun.worktime.util;

import lombok.Data;

@Data
public class SelectDO {

    public String label;
    public String value;
    public String backgroundColor;
    public String color;
    public SelectDO(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public SelectDO(String label, String value, String backgroundColor) {
        this.label = label;
        this.value = value;
        this.backgroundColor = backgroundColor;
        this.color = "#ffffffff";
    }

    public SelectDO(String label, String value, String backgroundColor, String color) {
        this.label = label;
        this.value = value;
        this.backgroundColor = backgroundColor;
        this.color = color;
    }

    public SelectDO() {
    }
}
