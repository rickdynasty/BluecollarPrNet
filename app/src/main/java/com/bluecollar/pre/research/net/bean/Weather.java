package com.bluecollar.pre.research.net.bean;

import java.util.List;

public class Weather {
    private List<WeatherResults> results;//命名一定要与数据中的相同，一定要对应

    public List<WeatherResults> getResults() {
        return results;
    }

    public void setResults(List<WeatherResults> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "results=" + results +
                '}';
    }
}