package com.wjw.security.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wjw.security.bean.FaceV3DetectBean;
import com.wjw.security.utils.FaceIdentifyUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * ===================================
 * Created With IntelliJ IDEA
 *
 * @author Waein :)
 * @version method: FaceDetectController, v 0.1
 * @CreateDate 2018/11/6
 * @CreateTime 10:19
 * @GitHub https://github.com/Waein
 * ===================================
 */
@Controller("/")
public class FaceDetectController {

    Logger log = LoggerFactory.getLogger(FaceDetectController.class);

    @ResponseBody
    @RequestMapping("/ajaxRequestInfo.do")
    public Map<String, Object> ajaxRequestInfo(HttpServletRequest request, //RequestQueryForm form,
                                               @RequestParam(value = "file", required = false) MultipartFile file) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            if (null != file) {
                //将数据转为流
                InputStream content = file.getInputStream();
                ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                byte[] buff = new byte[100];
                int rc = 0;
                while ((rc = content.read(buff, 0, 100)) > 0) {
                    swapStream.write(buff, 0, rc);
                }
                //获得二进制数组
                byte[] in2b = swapStream.toByteArray();
                //调用人脸检测的方法
                String str = FaceIdentifyUtil.detectFace(in2b, "" + 1);
                JSONObject job = new JSONObject(FaceIdentifyUtil.faceverify(in2b));
                System.out.println(job.toString());
                JSONObject testData = job.getJSONObject("result");

                JSON json = JSON.parseObject(str);
                FaceV3DetectBean bean = JSON.toJavaObject(json, FaceV3DetectBean.class);
                JSONArray arr = new JSONArray();
                // 美丑打分
                map.put("beauty", bean.getResult().getFace_list().get(0).getBeauty());
                // 年龄
                map.put("age", bean.getResult().getFace_list().get(0).getAge());
                // 性别
                map.put("gender", bean.getResult().getFace_list().get(0).getGender().getType());
                // 获取是否带眼睛 0-无眼镜，1-普通眼镜，2-墨镜
                map.put("glasses", bean.getResult().getFace_list().get(0).getGlasses().getType());
                // 获取是否微笑，0，不笑；1，微笑；2，大笑
                map.put("expression", bean.getResult().getFace_list().get(0).getExpression().getType());

                for (int i = 0; i < bean.getResult().getFace_list().size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    //获取年龄
                    int ageOne = bean.getResult().getFace_list().get(i).getAge();
                    //处理年龄
                    String age = String.valueOf(new BigDecimal(ageOne).setScale(0, BigDecimal.ROUND_HALF_UP));
                    jsonObject.put("age", age);

                    //获取美丑打分
                    Double beautyOne = (Double) bean.getResult().getFace_list().get(i).getBeauty();
                    //处理美丑打分
                    String beauty = String.valueOf(new BigDecimal(beautyOne).setScale(0, BigDecimal.ROUND_HALF_UP));
                    jsonObject.put("beauty", beauty);

                    //获取性别  male(男)、female(女)
                    String gender = (String) bean.getResult().getFace_list().get(i).getGender().getType();
                    jsonObject.put("gender", gender);

                    //获取是否带眼睛 0-无眼镜，1-普通眼镜，2-墨镜
                    String glasses = bean.getResult().getFace_list().get(i).getGlasses().getType();
                    jsonObject.put("glasses", String.valueOf(glasses));

                    //获取是否微笑，0，不笑；1，微笑；2，大笑
                    String expression = bean.getResult().getFace_list().get(i).getExpression().getType();
                    jsonObject.put("expression", String.valueOf(expression));
                    arr.add(jsonObject);
                }
                map.put("strjson", arr.toString());
                map.put("face_liveness", testData.get("face_liveness"));
                map.put("success", true);
                log.info("call ajaxRequestInfo result:{}, arr:{}, arr.toString:{}, bean.getResult().getFace_list().size():{}", map.toString(), arr, arr.toString(), bean.getResult().getFace_list().size());
            }
        } catch (Exception e) {
            log.error("call ajaxRequestInfo 检测异常 ******", e);
            e.printStackTrace();
            map.put("success", false);
            map.put("data", e.getMessage());
        }
        return map;
    }

}
