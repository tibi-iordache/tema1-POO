package databases;

import actor.ActorsAwards;
import entities.Actor;
import fileio.ActorInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorDataBase {
    private ArrayList<Actor> actors;

    public ActorDataBase(List<ActorInputData> actorsInput) {
        actors = new ArrayList<>();

        for (ActorInputData actorInputData : actorsInput) {
            Map<ActorsAwards, Integer> awards = new HashMap<>(actorInputData.getAwards());

            actors.add(new Actor(actorInputData.getName(),
                    actorInputData.getCareerDescription(),
                    actorInputData.getFilmography(),
                    awards));
        }
    }

    public ArrayList<Actor> getActors() {
        return actors;
    }

    public void setActors(ArrayList<Actor> actors) {
        this.actors = actors;
    }

    @Override
    public String toString() {
        return "ActorDataBase{" +
                "actors=" + actors +
                '}';
    }
}
