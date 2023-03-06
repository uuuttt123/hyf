package com.nowcoder.community;

import com.nowcoder.community.controller.HomeController;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class CommunityApplicationTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private HomeController homeController;
    @Autowired
    private UserService userService;

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

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

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以⭐赌⭐博⭐，可以⭐嫖⭐娼⭐，可以吸毒，可以开票，哈哈哈！";
        System.out.println(sensitiveFilter.filter(text));
    }
}
