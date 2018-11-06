// <script type="text/javascript">

    //判断浏览器是否支持HTML5 Canvas
    //	window.onload = function () {
    //		try {
    //			//动态创建一个canvas元 ，并获取他2Dcontext。如果出现异常则表示不支持 document.createElement("canvas").getContext("2d");
    //			document.getElementById("support").innerHTML = "浏览器支持HTML5 CANVAS";
    //		}
    //		catch (e) {
    //			document.getElementById("support").innerHTML = "浏览器不支持HTML5 CANVAS";
    //		}
    //	};

    /**
     * 其中show() 和 getPath() 方法 是实现读取图片后并且立即显示出来
     */
function show(){
    //以下即为完整客户端路径
    var file_img=document.getElementById("img"),
        iptfileupload = document.getElementById('iptfileupload');
    getPath(file_img,iptfileupload,file_img) ;
}

function getPath(obj,fileQuery,transImg) {
    var imgSrc = '', imgArr = [], strSrc = '' ;
    if(window.navigator.userAgent.indexOf("MSIE")>=1){ // IE浏览器判断
        if(obj.select){
            obj.select();
            var path=document.selection.createRange().text;
            alert(path) ;
            obj.removeAttribute("src");
            imgSrc = fileQuery.value ;
            imgArr = imgSrc.split('.') ;
            strSrc = imgArr[imgArr.length - 1].toLowerCase() ;
            if(strSrc.localeCompare('jpg') === 0 || strSrc.localeCompare('jpeg') === 0 || strSrc.localeCompare('gif') === 0 || strSrc.localeCompare('png') === 0){
                obj.setAttribute("src",transImg);
                obj.style.filter=
                    "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+path+"', sizingMethod='scale');"; // IE通过滤镜的方式实现图片显示
            }else{
                //try{
                throw new Error('File type Error! please image file upload..');
                //}catch(e){
                // alert('name: ' + e.name + 'message: ' + e.message) ;
                //}
            }
        }else{
            // alert(fileQuery.value) ;
            imgSrc = fileQuery.value ;
            imgArr = imgSrc.split('.') ;
            strSrc = imgArr[imgArr.length - 1].toLowerCase() ;
            if(strSrc.localeCompare('jpg') === 0 || strSrc.localeCompare('jpeg') === 0 || strSrc.localeCompare('gif') === 0 || strSrc.localeCompare('png') === 0){
                obj.src = fileQuery.value ;
            }else{
                //try{
                throw new Error('File type Error! please image file upload..') ;
                //}catch(e){
                // alert('name: ' + e.name + 'message: ' + e.message) ;
                //}
            }
        }
    } else{
        var file =fileQuery.files[0];
        var reader = new FileReader();
        reader.onload = function(e){

            imgSrc = fileQuery.value ;
            imgArr = imgSrc.split('.') ;
            strSrc = imgArr[imgArr.length - 1].toLowerCase() ;
            if(strSrc.localeCompare('jpg') === 0 || strSrc.localeCompare('jpeg') === 0 || strSrc.localeCompare('gif') === 0 || strSrc.localeCompare('png') === 0){
                obj.setAttribute("src", e.target.result) ;
            }else{
                //try{
                throw new Error('File type Error! please image file upload..') ;
                //}catch(e){
                // alert('name: ' + e.name + 'message: ' + e.message) ;
                //}
            }
            // alert(e.target.result);
        }
        reader.readAsDataURL(file);
    }
}

//***********************************绑定单击事件**********************************
$(function(){
    $("#iptfileupload").change(function () {
        var formData = new FormData(document.getElementById("add_form"));
        loadJs();
        $.ajax({
            url: "${ctx}/request/ajaxRequestInfo.do",
            method: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function (responseData) {
                console.log(responseData);
                var mes = eval(responseData);
                console.log(mes);
                if (mes.success) {
                    //alert(mes.strjson);
                    var jsonObj =  $.parseJSON(mes.strjson);
                    console.log(jsonObj);
                    //alert(jsonObj);
//                        var age = jsonObj[0].age;
//
//                        var beauty = jsonObj[0].beauty;
//                        var gendergender = jsonObj[0].gender;
//                        var glasses = jsonObj[0].glasses;
//                        var expression = jsonObj[0].expression

                    var age = mes.age;
                    var beauty = mes.beauty;
                    var gendergender = mes.gender;
                    var glasses = mes.glasses;
                    var expression = mes.expression;


                    $("#age").html(age);
                    $("#beauty").html(beauty);
                    $("#faceverify").html(mes.face_liveness);

                    if(gendergender == 'male'){
                        $("#gendergender").html("男");
                    }else{
                        $("#gendergender").html("女");
                    }

                    if(glasses == 'none'){
                        $("#glasses").html("未戴眼镜");
                    }else if(glasses == 'common'){
                        $("#glasses").html("戴了普通眼镜");
                    }else{
                        $("#glasses").html("戴了墨镜");
                    }

                    if(expression == 'none'){
                        $("#expression").html("不笑");
                    }else if(expression == 'smile'){
                        $("#expression").html("微笑");
                    }else{
                        $("#expression").html("大笑");
                    }
                    closeBlockUI();
                }else{
                    closeBlockUI();
                    showPagePrompts("error","检测失败！");
                }
            },error:function (e) {
                closeBlockUI();
                showPagePrompts("error","操作失败："+e);
            }
        });
    });

    //保存
    $("#doSave").click(function(){
        var url="${ctx}/request/addRequestInfoAction.do";
        $("#add_form").ajaxSubmit({
            type:'post',
            url:url,
            success:function (responseData) {
                var mes = eval(responseData);
                if (mes.success) {
                    //alert(mes.strjson);
                    var jsonObj =  $.parseJSON(mes.strjson);
                    console.log(jsonObj);
                    //alert(jsonObj);
                    var age = jsonObj[0].age;

                    var beauty = jsonObj[0].beauty;
                    var gendergender = jsonObj[0].gender;
                    var glasses = jsonObj[0].glasses;
                    var expression = jsonObj[0].expression

                    $("#age").html(age);
                    $("#beauty").html(beauty);
                    $("#faceverify").html(mes.face_liveness);

                    if(gendergender == 'male'){
                        $("#gendergender").html("男");
                    }else{
                        $("#gendergender").html("女");
                    }

                    if(glasses == 'none'){
                        $("#glasses").html("未戴眼镜");
                    }else if(glasses == 'common'){
                        $("#glasses").html("戴了普通眼镜");
                    }else{
                        $("#glasses").html("戴了墨镜");
                    }

                    if(expression == 'none'){
                        $("#expression").html("不笑");
                    }else if(expression == 'smile'){
                        $("#expression").html("微笑");
                    }else{
                        $("#expression").html("大笑");
                    }
                }
            },error:function (e) {
                showPagePrompts("error","操作失败："+e);
            }
        });
    });
});
// </script>