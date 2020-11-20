package databases;

import entertainment.Genre;
import entertainment.Season;
import entertainment.Serial;
import entertainment.SerialSeason;
import fileio.SerialInputData;
import utils.Utils;
import java.util.ArrayList;
import java.util.List;

public final class SerialDataBase {
    private final ArrayList<Serial> serials;

    public SerialDataBase(final List<SerialInputData> serialsInput) {
        // create the serials list
        serials = new ArrayList<>();

        for (SerialInputData serialInputData : serialsInput) {
            // first we form a season for the serial
            ArrayList<Season> serialsInputSeasons = serialInputData.getSeasons();

            int seasonCounter = 1;

            ArrayList<SerialSeason> serialSeasons = new ArrayList<SerialSeason>();

            for (Season season : serialsInputSeasons) {
                serialSeasons.add(new SerialSeason(season.getDuration(),
                                                        seasonCounter++));
            }

            // create the serial genre list
            ArrayList<Genre> genres = new ArrayList<Genre>();

            for (int i = 0; i < serialInputData.getGenres().size(); i++) {
                genres.add(Utils.stringToGenre(serialInputData.getGenres().get(i)));
            }

            // add the new serial to the list
            serials.add(new Serial(serialInputData.getTitle(),
                    serialInputData.getCast(),
                    genres,
                    serialInputData.getNumberSeason(),
                    serialSeasons,
                    serialInputData.getYear()));
        }
    }

    public ArrayList<Serial> getSerials() {
        return serials;
    }

    @Override
    public String toString() {
        return "SerialDataBase{"
                + "serials="
                + serials
                + '}';
    }
}
