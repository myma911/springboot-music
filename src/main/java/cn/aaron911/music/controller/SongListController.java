package cn.aaron911.music.controller;

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
import cn.aaron911.music.domain.SongList;
import cn.aaron911.music.service.SongListService;


@RestController
@Controller
public class SongListController {
	
	
	@Autowired
    private MinIoUtils minIoUtils;

    @Autowired
    private SongListService songListService;

//    添加歌单
    @ResponseBody
    @RequestMapping(value = "/songList/add", method = RequestMethod.POST)
    public Object addSongList(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String title = req.getParameter("title").trim();
        String pic = req.getParameter("pic").trim();
        String introduction = req.getParameter("introduction").trim();
        String style = req.getParameter("style").trim();

        SongList songList = new SongList();
        songList.setTitle(title);
        songList.setPic(pic);
        songList.setIntroduction(introduction);
        songList.setStyle(style);

        boolean res = songListService.addSongList(songList);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "添加成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "添加失败");
            return jsonObject;
        }
    }

//    返回所有歌单
    @RequestMapping(value = "/songList", method = RequestMethod.GET)
    public Object allSongList(){
        return songListService.allSongList();
    }

//    返回指定标题对应的歌单
    @RequestMapping(value = "/songList/title/detail", method = RequestMethod.GET)
    public Object songListOfTitle(HttpServletRequest req){
        String title = req.getParameter("title").trim();
        return songListService.songListOfTitle(title);
    }

//    返回标题包含文字的歌单
    @RequestMapping(value = "/songList/likeTitle/detail", method = RequestMethod.GET)
    public Object songListOfLikeTitle(HttpServletRequest req){
        String title = req.getParameter("title").trim();
        return songListService.likeTitle('%'+ title + '%');
    }

//    返回指定类型的歌单
    @RequestMapping(value = "/songList/style/detail", method = RequestMethod.GET)
    public Object songListOfStyle(HttpServletRequest req){
        String style = req.getParameter("style").trim();
        return songListService.likeStyle('%'+ style + '%');
    }

//    删除歌单
    @RequestMapping(value = "/songList/delete", method = RequestMethod.GET)
    public Object deleteSongList(HttpServletRequest req){
        String id = req.getParameter("id");
        return songListService.deleteSongList(Integer.parseInt(id));
    }

//    更新歌单信息
    @ResponseBody
    @RequestMapping(value = "/songList/update", method = RequestMethod.POST)
    public Object updateSongListMsg(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String id = req.getParameter("id").trim();
        String title = req.getParameter("title").trim();
        String pic = req.getParameter("pic").trim();
        String introduction = req.getParameter("introduction").trim();
        String style = req.getParameter("style").trim();

        SongList songList = new SongList();
        songList.setId(Integer.parseInt(id));
        songList.setTitle(title);
        songList.setPic(pic);
        songList.setIntroduction(introduction);
        songList.setStyle(style);

        boolean res = songListService.updateSongListMsg(songList);
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

//    更新歌单图片
    @ResponseBody
    @RequestMapping(value = "/songList/img/update", method = RequestMethod.POST)
    public Object updateSongListPic(@RequestParam("file") MultipartFile avatorFile, @RequestParam("id")int id){
        JSONObject jsonObject = new JSONObject();

        if (avatorFile.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "文件上传失败！");
            return jsonObject;
        }
        String fileName = System.currentTimeMillis() + "_" + avatorFile.getOriginalFilename();
//        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "img" + System.getProperty("file.separator") + "songListPic" ;
//        File file1 = new File(filePath);
//        if (!file1.exists()){
//            file1.mkdir();
//        }
//
//        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String objectName = "img/songListPic/"+fileName;
        try {
            //avatorFile.transferTo(dest);
        	minIoUtils.putObject(Const.MINIO_BUCKET_MUSIC, objectName, avatorFile.getInputStream());
            SongList songList = new SongList();
            songList.setId(id);
            songList.setPic(Const.MINIO_MUSIC + objectName);
            boolean res = songListService.updateSongListImg(songList);
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















