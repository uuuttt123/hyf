package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    //注册功能需要发送邮件
    @Autowired
    private MailClient mailClient;
    //需要模板引擎
    @Autowired
    private TemplateEngine templateEngine;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //邮箱里的激活地址
    @Value("${community.path.domain}")
    private String domain;
    //项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
//        return userMapper.selectById(id);
        User user = getCache(id);
        if(user == null) {
            user = initCache(id);
        }
        return user;
    }

    public Map<String, Object> register(User user) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (user == null) {
            throw new IllegalAccessException("参数不能为空！");
        }
        //还是使用apache的工具类判断是否为空
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        //验证账号,传入一个username，去查找
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在");
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已注册");
        }
        //注册用户
        //生成五位的盐
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        //先对用户输入的密码进行加密 再加盐
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //0普通用户
        user.setType(0);
        //未激活
        user.setStatus(0);
        //发送激活码 随机字符串
        user.setActivationCode(CommunityUtil.generateUUID());
        //随机头像 0-1000
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //用户创建时间
        user.setCreateTime(new Date());
        //添加到数据库
        userMapper.insertUser(user);
        //激活邮件
        Context context = new Context();
        //给前端页面传递email的值 	<b th:text="${email}">xxx@xxx.com</b>, 您好!
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        // //给前端页面传递url的值 <a th:href="${url}">此链接</a>,
        context.setVariable("url", url);
        //前端页面地址
        String content = templateEngine.process("/mail/activation", context);
        //发送邮箱
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;

    }
    
    public int activation(int userid, String code){
        User user = userMapper.selectById(userid);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userid, 1);
            clearCache(userid);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        if(user.getStatus() == 0){
            map.put("statusMsg", "该账号未激活");
            return map;
        }
        password = CommunityUtil.md5(password + user.getSalt());

        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket =(LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String url) {
        int rows = userMapper.updateHeader(userId, url);
        clearCache(userId);
        return rows;
    }

    public Map<String, Object> updatePassword(int userId, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        User user = userMapper.selectById(userId);
        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(userId, password);
        clearCache(userId);
        return map;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    //优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    //取不到初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    //数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
