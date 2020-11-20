package databases;

import actor.ActorsAwards;
import actor.Actor;
import fileio.ActorInputData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ActorDataBase {
    private final ArrayList<Actor> actors;

    public ActorDataBase(final List<ActorInputData> actorsInput) {
        //create the actors list
        actors = new ArrayList<Actor>();

        // iterate through each input
        for (ActorInputData actorInputData : actorsInput) {
            // create the iterator award
            Map<ActorsAwards, Integer> awards = new HashMap<>(actorInputData.getAwards());

            // add a new actor to the list
            actors.add(new Actor(actorInputData.getName(),
                        actorInputData.getCareerDescription(),
                        actorInputData.getFilmography(),
                        awards));
        }
    }

    public ArrayList<Actor> getActors() {
        return actors;
    }

    @Override
    public String toString() {
        return "ActorDataBase{"
                + "actors="
                + actors
                + '}';
    }
}
