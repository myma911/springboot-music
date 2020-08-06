package cn.aaron911.music.service;

import cn.aaron911.music.domain.Rank;

public interface RankService {

    int rankOfSongListId(Long songListId);

    boolean addRank(Rank rank);
}
