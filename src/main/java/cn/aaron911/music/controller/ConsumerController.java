package cn.aaron911.music.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import cn.aaron911.music.domain.Consumer;
import cn.aaron911.music.service.ConsumerService;
import cn.aaron911.music.util.CompressPictureTools;
import cn.hutool.core.util.StrUtil;

@RestController
@Controller
public class ConsumerController {
	
	@Autowired
    private MinIoUtils minIoUtils;

    @Autowired
    private ConsumerService consumerService;


//    添加用户
    @ResponseBody
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public Object addUser(
		HttpServletRequest req,
	    String username,		
	    String password,		
	    String sex,		
	    String phone_num,		
	    String email,		
	    String birth,		
	    String introduction,		
	    String location,		
	    String avator 		
    ){
        JSONObject jsonObject = new JSONObject();
        username = req.getParameter("username").trim();
        password = req.getParameter("password").trim();
        sex = req.getParameter("sex").trim();
        phone_num = req.getParameter("phone_num").trim();
        email = req.getParameter("email").trim();
        birth = req.getParameter("birth").trim();
        introduction = req.getParameter("introduction").trim();
        location = req.getParameter("location").trim();
        avator = req.getParameter("avator").trim();

        if (StrUtil.isBlank(username)){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名或密码错误");
            return jsonObject;
        }
        Consumer consumer = new Consumer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        } catch (Exception e){
            e.printStackTrace();
        }
        consumer.setUsername(username);
        consumer.setPassword(password);
        consumer.setSex(new Byte(sex));
        if (phone_num == "") {
            consumer.setPhoneNum(null);
        } else{
            consumer.setPhoneNum(phone_num);
        }

        if (email == "") {
            consumer.setEmail(null);
        } else{
            consumer.setEmail(email);
        }
        consumer.setBirth(myBirth);
        consumer.setIntroduction(introduction);
        consumer.setLocation(location);
        consumer.setAvator(Const.MINIO_MUSIC + avator);
        consumer.setCreateTime(new Date());
        consumer.setUpdateTime(new Date());

        boolean res = consumerService.addUser(consumer);
        if (res) {
            jsonObject.put("code", 1);
            jsonObject.put("msg", "注册成功");
            return jsonObject;
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "注册失败");
            return jsonObject;
        }
    }

//    判断是否登录成功
    @ResponseBody
    @RequestMapping(value = "/user/login/status", method = RequestMethod.POST)
    public Object loginStatus(HttpServletRequest req, HttpSession session){

        JSONObject jsonObject = new JSONObject();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
//        System.out.println(username+"  "+password);
        boolean res = consumerService.veritypasswd(username, password);

        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "登录成功");
            jsonObject.put("userMsg", consumerService.loginStatus(username));
            session.setAttribute("username", username);
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名或密码错误");
            return jsonObject;
        }

    }

//    返回所有用户
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Object allUser(){
        return consumerService.allUser();
    }

//    返回指定ID的用户
    @RequestMapping(value = "/user/detail", method = RequestMethod.GET)
    public Object userOfId(HttpServletRequest req){
        String id = req.getParameter("id");
        return consumerService.userOfId(Integer.parseInt(id));
    }

//    删除用户
    @RequestMapping(value = "/user/delete", method = RequestMethod.GET)
    public Object deleteUser(HttpServletRequest req){
        String id = req.getParameter("id");
        return consumerService.deleteUser(Integer.parseInt(id));
    }

//    更新用户信息
    @ResponseBody
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public Object updateUserMsg(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String id = req.getParameter("id").trim();
        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String sex = req.getParameter("sex").trim();
        String phone_num = req.getParameter("phone_num").trim();
        String email = req.getParameter("email").trim();
        String birth = req.getParameter("birth").trim();
        String introduction = req.getParameter("introduction").trim();
        String location = req.getParameter("location").trim();

        if (username.equals("") || username == null){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名或密码错误");
            return jsonObject;
        }
        Consumer consumer = new Consumer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        }catch (Exception e){
            e.printStackTrace();
        }
        consumer.setId(Integer.parseInt(id));
        consumer.setUsername(username);
        consumer.setPassword(password);
        consumer.setSex(new Byte(sex));
        consumer.setPhoneNum(phone_num);
        consumer.setEmail(email);
        consumer.setBirth(myBirth);
        consumer.setIntroduction(introduction);
        consumer.setLocation(location);
//        consumer.setAvator(avator);
        consumer.setUpdateTime(new Date());

        boolean res = consumerService.updateUserMsg(consumer);
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

//    更新用户头像
    @ResponseBody
    @RequestMapping(value = "/user/avatar/update", method = RequestMethod.POST)
    public Object updateUserPic(@RequestParam("file") MultipartFile avatorFile, @RequestParam("id")int id){
        JSONObject jsonObject = new JSONObject();

        if (avatorFile.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "文件上传失败！");
            return jsonObject;
        }
        String fileName = System.currentTimeMillis() + "_" + avatorFile.getOriginalFilename();
//        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "avatorImages" ;
//        File file1 = new File(filePath);
//        if (!file1.exists()){
//            file1.mkdir();
//        }
//
//        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String storeAvatorPath = "avatorImages/"+fileName;
        
        try {
        	InputStream inputStream = avatorFile.getInputStream();
        	byte[] compressOfQuality = CompressPictureTools.compressOfQuality(inputStream, 0.6F);
        	ByteArrayInputStream swapStream = new ByteArrayInputStream(compressOfQuality);
			minIoUtils.putObject(Const.MINIO_BUCKET_MUSIC, storeAvatorPath, swapStream);
			Consumer consumer = new Consumer();
            consumer.setId(id);
            consumer.setAvator(Const.MINIO_MUSIC + storeAvatorPath);
            boolean res = consumerService.updateUserAvator(consumer);
            if (res){
                jsonObject.put("code", 1);
                jsonObject.put("avator", storeAvatorPath);
                jsonObject.put("msg", "上传成功");
                return jsonObject;
            }else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "上传失败");
                return jsonObject;
            }
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("code", 0);
            jsonObject.put("msg", "上传失败"+e.getMessage());
            return jsonObject;
		}
    }
}
