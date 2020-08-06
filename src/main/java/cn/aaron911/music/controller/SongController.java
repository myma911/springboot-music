package cn.aaron911.music.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.aaron911.minio.util.MinIoUtils;
import cn.aaron911.music.constant.Const;
import cn.aaron911.music.domain.Song;
import cn.aaron911.music.service.SongService;

@RestController
@Controller
public class SongController {
	
	@Autowired
    private MinIoUtils minIoUtils;

    @Autowired
    private SongService songService;

//    添加歌曲
    @ResponseBody
    @RequestMapping(value = "/song/add", method = RequestMethod.POST)
    public Object addSong(HttpServletRequest req, @RequestParam("file") MultipartFile mpfile){
        JSONObject jsonObject = new JSONObject();
        String singer_id = req.getParameter("singerId").trim();
        String name = req.getParameter("name").trim();
        String introduction = req.getParameter("introduction").trim();
        String pic = "/img/songPic/tubiao.jpg";
        String lyric = req.getParameter("lyric").trim();

        if (mpfile.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "音乐上传失败！");
            return jsonObject;
        }
        String fileName = System.currentTimeMillis() + "_" + mpfile.getOriginalFilename();
//        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "song";
//        File file1 = new File(filePath);
//        if (!file1.exists()){
//            file1.mkdir();
//        }
//
//        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String objectName = "song/"+fileName;
        try {
            //mpfile.transferTo(dest);
        	minIoUtils.putObject(Const.MINIO_BUCKET_MUSIC, objectName, mpfile.getInputStream());
            Song song = new Song();
            song.setSingerId(Integer.parseInt(singer_id));
            song.setName(name);
            song.setIntroduction(introduction);
            song.setCreateTime(new Date());
            song.setUpdateTime(new Date());
            song.setPic(pic);
            song.setLyric(lyric);
            song.setUrl(Const.MINIO_MUSIC + objectName);
            boolean res = songService.addSong(song);
            if (res) {
                jsonObject.put("code", 1);
                jsonObject.put("avator", Const.MINIO_MUSIC + objectName);
                jsonObject.put("msg", "上传成功");
                return jsonObject;
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "上传失败");
                return jsonObject;
            }
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "上传失败" + e.getMessage());
            return jsonObject;
        }
    }

//    返回所有歌曲
    @RequestMapping(value = "/song", method = RequestMethod.GET)
    public Object allSong(){
        return songService.allSong();
    }

//    返回指定歌曲ID的歌曲
    @RequestMapping(value = "/song/detail", method = RequestMethod.GET)
    public Object songOfId(HttpServletRequest req){
        String id = req.getParameter("id");
        return songService.songOfId(Integer.parseInt(id));
    }

//    返回指定歌手ID的歌曲
    @RequestMapping(value = "/song/singer/detail", method = RequestMethod.GET)
    public Object songOfSingerId(HttpServletRequest req){
        String singerId = req.getParameter("singerId");
        return songService.songOfSingerId(Integer.parseInt(singerId));
    }

//    返回指定歌手名的歌曲
    @RequestMapping(value = "/song/singerName/detail", method = RequestMethod.GET)
    public Object songOfSingerName(HttpServletRequest req){
        String name = req.getParameter("name");
        return songService.songOfSingerName('%'+ name + '%');
    }

//    返回指定歌曲名的歌曲
    @RequestMapping(value = "/song/name/detail", method = RequestMethod.GET)
    public Object songOfName(HttpServletRequest req){
        String name = req.getParameter("name").trim();
        return songService.songOfName(name);
    }

//    删除歌曲
    @RequestMapping(value = "/song/delete", method = RequestMethod.GET)
    public Object deleteSong(HttpServletRequest req){
        String id = req.getParameter("id");
        return songService.deleteSong(Integer.parseInt(id));
    }

//    更新歌曲信息
    @ResponseBody
    @RequestMapping(value = "/song/update", method = RequestMethod.POST)
    public Object updateSongMsg(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String id = req.getParameter("id").trim();
        String singer_id = req.getParameter("singerId").trim();
        String name = req.getParameter("name").trim();
        String introduction = req.getParameter("introduction").trim();
        String lyric = req.getParameter("lyric").trim();

        Song song = new Song();
        song.setId(Integer.parseInt(id));
        song.setSingerId(Integer.parseInt(singer_id));
        song.setName(name);
        song.setIntroduction(introduction);
        song.setUpdateTime(new Date());
        song.setLyric(lyric);

        boolean res = songService.updateSongMsg(song);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "修改成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "修改失败");
            return jsonObject;
        }
    }

//    更新歌曲图片
    @ResponseBody
    @RequestMapping(value = "/song/img/update", method = RequestMethod.POST)
    public Object updateSongPic(@RequestParam("file") MultipartFile urlFile, @RequestParam("id")int id){
        JSONObject jsonObject = new JSONObject();

        if (urlFile.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "音乐上传失败！");
            return jsonObject;
        }
        String fileName = System.currentTimeMillis() + "_" + urlFile.getOriginalFilename();
//        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "img" + System.getProperty("file.separator") + "songPic";
//        File file1 = new File(filePath);
//        if (!file1.exists()){
//            file1.mkdir();
//        }
//
//        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        
        String objectName = "img/songPic/"+fileName;
        try {
            //urlFile.transferTo(dest);
        	minIoUtils.putObject(Const.MINIO_BUCKET_MUSIC, objectName, urlFile.getInputStream());
            Song song = new Song();
            song.setId(id);
            song.setPic(Const.MINIO_MUSIC + objectName);
            boolean res = songService.updateSongPic(song);
            if (res){
                jsonObject.put("code", 1);
                jsonObject.put("avator", Const.MINIO_MUSIC + objectName);
                jsonObject.put("msg", "上传成功");
                return jsonObject;
            }else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "上传失败");
                return jsonObject;
            }
        }catch (Exception e){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "上传失败" + e.getMessage());
            return jsonObject;
        }
    }

//    更新歌曲URL
    @ResponseBody
    @RequestMapping(value = "/song/url/update", method = RequestMethod.POST)
    public Object updateSongUrl(@RequestParam("file") MultipartFile urlFile, @RequestParam("id")int id){
        JSONObject jsonObject = new JSONObject();

        if (urlFile.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "音乐上传失败！");
            return jsonObject;
        }
        String fileName = System.currentTimeMillis() + "_" + urlFile.getOriginalFilename();
//        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "song";
//        File file1 = new File(filePath);
//        if (!file1.exists()){
//            file1.mkdir();
//        }
//
//        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String objectName = "song/"+fileName;
        try {
            //urlFile.transferTo(dest);
        	minIoUtils.putObject(Const.MINIO_BUCKET_MUSIC, objectName, urlFile.getInputStream());
            Song song = new Song();
            song.setId(id);
            song.setUrl(Const.MINIO_MUSIC + objectName);
            boolean res = songService.updateSongUrl(song);
            if (res){
                jsonObject.put("code", 1);
                jsonObject.put("avator", Const.MINIO_MUSIC + objectName);
                jsonObject.put("msg", "上传成功");
                return jsonObject;
            }else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "上传失败");
                return jsonObject;
            }
        }catch (Exception e){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "上传失败" + e.getMessage());
            return jsonObject;
        }
    }
}
