package com.bluecollar.pre.research.net.bean;

import java.util.List;

public class WeatherResults {
    private WeatherResLocation location;//对应location：{}，命名一定要与数据中的相同，一定要对应
    private List<WeatherResDaily> daily;//对应daily：数组
    private String last_update;//对应"last_update"：一个字符串

    public WeatherResLocation getLocation() {
        return location;
    }

    public List<WeatherResDaily> getDaily() {
        return daily;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLocation(WeatherResLocation location) {
        this.location = location;
    }

    public void setDaily(List<WeatherResDaily> daily) {
        this.daily = daily;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    @Override
    public String toString() {
        return "WeatherResults{" +
                "location=" + location +
                ", daily=" + daily +
                ", last_update='" + last_update + '\'' +
                '}';
    }
}
