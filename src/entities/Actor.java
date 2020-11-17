package entities;

import actor.ActorsAwards;
import java.util.ArrayList;
import java.util.Map;

public class Actor {
    private String name;

    private String careerDescription;

    private ArrayList<String> filmography;

    private Map<ActorsAwards, Integer> awards;

    private Double rating;

    public Actor(String name,
                 String careerDescription,
                 ArrayList<String> filmography,
                 Map<ActorsAwards, Integer> awards) {
        this.name = name;

        this.careerDescription = careerDescription;

        this.filmography = filmography;

        this.awards = awards;
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public int getAwardsNo() {
        int number = 0;

        for (Map.Entry<ActorsAwards, Integer> iterator : this.getAwards().entrySet()) {
            number += iterator.getValue();
        }

        return number;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "name='" + name + '\'' +
                ", careerDescription='" + careerDescription + '\'' +
                ", filmography=" + filmography +
                ", awards=" + awards +
                ", rating=" + rating +
                '}';
    }
}
