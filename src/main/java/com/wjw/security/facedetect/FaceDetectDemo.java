package com.wjw.security.facedetect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.wjw.security.utils.FaceIdentifyUtil;

import java.io.File;
import java.util.Map;

/**
 * ===================================
 * Created With IntelliJ IDEA
 *
 * @author Waein :)
 * @version method: FaceDetectDemo, v 0.1
 * @CreateDate 2018/11/7
 * @CreateTime 16:09
 * @GitHub https://github.com/Waein
 * ===================================
 */
public class FaceDetectDemo {

    public static void main(String[] args) {
        String filePath = "/Users/SeungRi/opt/snapshot/savedImage.jpg";
        String machFilePath = "/Users/SeungRi/Work/download/Wallpaper/machPic.jpeg";
        String userId = "snapshot1";
        String groupId = "safety_exam";
        String userInfo = "新增snapshot用户";
        String name = "鲍万里";
        String idCardNumber = "330726199405221530";

//        FaceIdentifyUtil.addUser(new File(filePath),userInfo, userId, groupId); //注册用户

//        String faceListStr = FaceIdentifyUtil.getUserFaceList(userId, groupId); //获取同组用户列表
//        JSONObject jsonObject = JSONObject.parseObject(faceListStr);

//        String faceListStr = FaceIdentifyUtil.searchUserInfo(userId, groupId); //获取用户信息
//        JSONObject jsonObject = JSONObject.parseObject(faceListStr);

        String result = FaceIdentifyUtil.matchFace(new File(filePath), new File(machFilePath)); //人脸比对
        JSONObject jsonObject = JSONObject.parseObject(result);
        Map<String, String> params = JSONObject.parseObject(jsonObject.toJSONString(), new TypeReference<Map<String, String>>(){});
        System.out.println(params.get("result"));

//        String result = FaceIdentifyUtil.personVerify(new File(filePath), idCardNumber, name); //公安校验:需要权限
//        Map<String, String> params = JSONObject.parseObject(JSONObject.parseObject(result).toJSONString(), new TypeReference<Map<String, String>>(){});
//        System.out.println((Integer.parseInt(params.get("score")) > 80) ? "是本人" : "不是本人");
    }
}
