package ge.shitbot.persist.services;

/**
 * Created by giga on 12/8/17.
 */
public interface RankingService {
    int getRankingFor(String subject, String skill);

    void addRanking(String subject, String observer, String skill, int ranking);
}
