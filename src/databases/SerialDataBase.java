package databases;

import entertainment.Genre;
import entertainment.Season;
import entities.Serial;
import entities.SerialSeason;
import fileio.SerialInputData;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SerialDataBase {
    private ArrayList<Serial> serials;

    public SerialDataBase(List<SerialInputData> serialsInput) {
        serials = new ArrayList<>();

        for (SerialInputData serialInputData : serialsInput) {
            // first we form a season for the serial
            ArrayList<Season> serialsInputSeasons = serialInputData.getSeasons();

            int seasonCounter = 1;

            ArrayList<SerialSeason> serialSeasons = new ArrayList<>();

            for (Season season : serialsInputSeasons) {
                serialSeasons.add(new SerialSeason(season.getDuration(),
                        seasonCounter++));
            }

            ArrayList<Genre> genres = new ArrayList<>();

            for (int i = 0; i < serialInputData.getGenres().size(); i++)
                genres.add(Utils.stringToGenre(serialInputData.getGenres().get(i)));

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

    public void setSerials(ArrayList<Serial> serials) {
        this.serials = serials;
    }

    @Override
    public String toString() {
        return "SerialDataBase{" +
                "serials=" + serials +
                '}';
    }
}
