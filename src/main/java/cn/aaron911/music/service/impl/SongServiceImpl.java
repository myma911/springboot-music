package cn.aaron911.music.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.aaron911.music.dao.SongMapper;
import cn.aaron911.music.domain.Song;
import cn.aaron911.music.service.SongService;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongMapper songMapper;

    @Override
    public List<Song> allSong()
    {
        return songMapper.allSong();
    }

    @Override
    public boolean addSong(Song song)
    {

        return songMapper.insertSelective(song) > 0?true:false;
    }

    @Override
    public boolean updateSongMsg(Song song) {
        return songMapper.updateSongMsg(song) >0 ?true:false;
    }

    @Override
    public boolean updateSongUrl(Song song) {

        return songMapper.updateSongUrl(song) >0 ?true:false;
    }

    @Override
    public boolean updateSongPic(Song song) {

        return songMapper.updateSongPic(song) >0 ?true:false;
    }

    @Override
    public boolean deleteSong(Integer id) {
        return songMapper.deleteSong(id) >0 ?true:false;
    }

    @Override
    public List<Song> songOfSingerId(Integer singerId)

    {
        return songMapper.songOfSingerId(singerId);
    }

    @Override
    public List<Song> songOfId(Integer id)

    {
        return songMapper.songOfId(id);
    }

    @Override
    public List<Song> songOfSingerName(String name)

    {
        return songMapper.songOfSingerName(name);
    }

    @Override
    public List<Song> songOfName(String name)

    {
        return songMapper.songOfName(name);
    }
}
