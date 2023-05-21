package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class CaffeineTest {
    @Autowired
    private DiscussPostService postService;

    @Test
    public void insertTest() {
        for (int i = 0; i < 100000; i++) {
            DiscussPost discussPost = new DiscussPost();
            discussPost.setUserId(111);
            discussPost.setTitle("互联网求职计划");
            discussPost.setContent("2023年的就业形势太不容乐观，牛客携手百家企业开启互联网求职特别企划，帮助广大应届生找到工作！");
            discussPost.setCreateTime(new Date());
            discussPost.setScore(Math.random() * 2000);
            postService.addDiscussPost(discussPost);
        }
    }

    @Test
    public void testCache() {
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
    }
}
