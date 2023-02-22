package com.nowcoder.community;

import com.nowcoder.community.controller.HomeController;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CommunityApplicationTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private HomeController homeController;
    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
//        System.out.println(discussPosts);
        for(DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }
    }

    @Test
    void testSelectPostById(){
        int count = discussPostMapper.selectDiscussPostRows(103);
        System.out.println(count);
    }

    @Test
    void testController(){
//        String indexPage = homeController.getIndexPage(null);
    }

    @Test
    void testGetUser(){
        User user = userService.findUserById(101);
        System.out.println(user);
    }

}
