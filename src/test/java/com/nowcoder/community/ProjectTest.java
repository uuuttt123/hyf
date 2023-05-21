package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class ProjectTest {
    private DiscussPost data;
    @Autowired
    private DiscussPostService postService;

    @BeforeAll
    public static void beforeAll() {
        System.out.println("BeforeAll");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("AfterAll");
    }

    @BeforeEach
    public void before() {
        data = new DiscussPost();
        data.setUserId(999);
        data.setTitle("绝了");
        data.setContent("6");
        data.setCreateTime(new Date());
        postService.addDiscussPost(data);
    }

    @AfterEach
    public void after() {
        postService.updateStatus(data.getId(), 2);
    }

    @Test
    public void testAdd() {
        DiscussPost post = postService.findDiscussPostById(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getTitle(), post.getTitle());
    }
}
