function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data) {
            data = $.parseJSON(data);
            // data.code == 0表示请求成功
            if (data.code == 0) {
                // 获得按钮，通过按钮得到下级标签，改变值
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞')
            }else {
                alert(data.msg);
            }
        }
    );
}